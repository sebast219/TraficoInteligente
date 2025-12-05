package com.trafico.controller;

import com.trafico.model.*;
import com.trafico.util.Simulador;
import com.trafico.util.SistemaTrafico;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;

/**
 * MapaController (modo COVER) — el mapa siempre ocupa todo el canvas (sin bandas).
 * Correcciones y mejoras aplicadas:
 * - Se eliminaron los errores de CSS en -fx-effect usando efectos programáticos (DropShadow).
 * - Se centralizó la creación de botones en un helper createStyledButton para evitar duplicar código.
 * - Pequeñas defensas nulas para evitar NPE en tiempo de inicialización.
 */
public class MapaController {
    private final BorderPane root;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private Simulador simulador;
    private AnimationTimer animacion;
    private boolean animacionActiva = false;

    // Escalado y offsets para transformar coordenadas del mapa a canvas (modo COVER)
    private double scale = 1.0;
    private double offsetX = 0.0;
    private double offsetY = 0.0;

    // UI
    private VBox panelLateral;
    private Label lblEstado;
    private Label lblEstadoDetallado;
    private Label lblDistancia;
    private Label lblTiempo;
    private Label lblProgreso;
    private Label lblRutasRecalculadas;
    private Label lblCongestion;
    private Label lblSemaforosPrioridad;
    private Label lblPosicionInicial;
    private Label lblUbicacionAccidente;
    private Label lblUbicacionHospital;

    // Umbral para colapsar panel lateral (px)
    private static final double COLLAPSE_PANEL_WIDTH = 1000.0;

    public MapaController(BorderPane root) {
        this.root = root;
        this.canvas = new Canvas(1200, 800);
        this.gc = canvas.getGraphicsContext2D();

        // Que el canvas ocupe el centro disponible (considerando panel lateral)
        // Usamos binding defensivo: evaluamos si root.getRight() está presente en tiempo de cálculo.
        canvas.widthProperty().bind(Bindings.createDoubleBinding(
                () -> {
                    double rightWidth = (root.getRight() == null) ? 0.0 : root.getRight().prefWidth(-1);
                    return Math.max(200, root.getWidth() - rightWidth);
                }, root.widthProperty(), root.rightProperty()));

        canvas.heightProperty().bind(Bindings.createDoubleBinding(
                () -> {
                    double topH = (root.getTop() == null) ? 0 : root.getTop().prefHeight(-1);
                    double bottomH = (root.getBottom() == null) ? 0 : root.getBottom().prefHeight(-1);
                    return Math.max(200, root.getHeight() - topH - bottomH - 8);
                }, root.heightProperty(), root.topProperty(), root.bottomProperty()));

        // Listeners de resize para recalcular escala COVER
        canvas.widthProperty().addListener((obs, o, n) -> onResize());
        canvas.heightProperty().addListener((obs, o, n) -> onResize());
        root.widthProperty().addListener((obs, o, n) -> onResize());
        root.heightProperty().addListener((obs, o, n) -> onResize());
    }

    public void inicializar() {
        configurarUI();
        inicializarSimulador();
        iniciarAnimacion();
        onResize();
    }

    private void configurarUI() {
        // Top controls
        HBox panelSuperior = new HBox(15);
        panelSuperior.setPadding(new Insets(12));
        panelSuperior.setAlignment(Pos.CENTER_LEFT);
        panelSuperior.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);");

        // Creamos botones usando helper para aplicar efectos programáticos y estilos CSS (sin -fx-effect)
        Button btnIniciar = createStyledButton("🚨 Nueva Emergencia", "#e74c3c", true);
        Button btnPausar = createStyledButton("⏸ Pausar", "#f39c12", false);
        Button btnReanudar = createStyledButton("▶ Reanudar", "#27ae60", false);
        Button btnReiniciar = createStyledButton("🔄 Reiniciar", "#3498db", false);
        Button btnSimularTrafico = createStyledButton("🚗 Simular Tráfico", "#9b59b6", false);

        btnIniciar.setOnAction(e -> iniciarEmergencia());
        btnPausar.setOnAction(e -> pausarSimulacion());
        btnReanudar.setOnAction(e -> reanudarSimulacion());
        btnReiniciar.setOnAction(e -> reiniciarSimulacion());
        btnSimularTrafico.setOnAction(e -> simularCongestion());

        panelSuperior.getChildren().addAll(btnIniciar, btnPausar, btnReanudar, btnReiniciar, btnSimularTrafico);

        // Panel lateral (información)
        panelLateral = new VBox(10);
        panelLateral.setPadding(new Insets(12));
        panelLateral.setStyle("-fx-background-color: linear-gradient(to bottom, #34495e, #2c3e50);");
        panelLateral.setPrefWidth(300);

        Label titulo = new Label("📊 PANEL DE CONTROL");
        titulo.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        Label subtituloUbicaciones = new Label("📍 UBICACIONES");
        subtituloUbicaciones.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 13px; -fx-font-weight: bold;");

        lblPosicionInicial = crearLabel("🚑 Ambulancia: ---");
        lblUbicacionAccidente = crearLabel("🆘 Accidente: ---");
        lblUbicacionHospital = crearLabel("🏥 Hospital: ---");

        Label separador1 = crearSeparador();

        Label subtituloEmergencia = new Label("🚨 EMERGENCIA");
        subtituloEmergencia.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 13px; -fx-font-weight: bold;");

        lblEstado = crearLabel("Estado: Esperando");
        lblEstadoDetallado = crearLabel("⏳ Sin emergencia activa");
        lblDistancia = crearLabel("Distancia: 0.00 km");
        lblTiempo = crearLabel("Tiempo: 0.0 seg");
        lblProgreso = crearLabel("Progreso: 0%");
        lblRutasRecalculadas = crearLabel("Rutas recalculadas: 0");

        Label separador2 = crearSeparador();

        Label subtituloSistema = new Label("🤖 SISTEMA INTELIGENTE");
        subtituloSistema.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 13px; -fx-font-weight: bold;");

        lblSemaforosPrioridad = crearLabel("Semáforos en prioridad: 0");
        lblCongestion = crearLabel("Congestión promedio: 0%");

        Label separador3 = crearSeparador();

        Label tituloLeyenda = new Label("🗺️ LEYENDA");
        tituloLeyenda.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 13px; -fx-font-weight: bold;");

        VBox leyenda = new VBox(6);
        leyenda.getChildren().addAll(
                crearItemLeyenda("🟢", "Semáforo Verde"),
                crearItemLeyenda("🔴", "Semáforo Rojo"),
                crearItemLeyenda("💚", "Prioridad Activa"),
                crearItemLeyenda("🚑", "Ambulancia"),
                crearItemLeyenda("🆘", "Accidente (Aleatorio)"),
                crearItemLeyenda("🏥", "Hospital (Fijo)")
        );

        Label atribucion = new Label("\n© OpenStreetMap contributors");
        atribucion.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 9px;");

        panelLateral.getChildren().addAll(
                titulo,
                subtituloUbicaciones,
                lblPosicionInicial,
                lblUbicacionAccidente,
                lblUbicacionHospital,
                separador1,
                subtituloEmergencia,
                lblEstado,
                lblEstadoDetallado,
                lblDistancia,
                lblTiempo,
                lblProgreso,
                lblRutasRecalculadas,
                separador2,
                subtituloSistema,
                lblSemaforosPrioridad,
                lblCongestion,
                separador3,
                tituloLeyenda,
                leyenda,
                atribucion
        );

        root.setTop(panelSuperior);
        root.setCenter(canvas);
        root.setRight(panelLateral);
    }

    private Button createStyledButton(String text, String bgColorHex, boolean bold) {
        Button btn = new Button(text);
        String weight = bold ? "-fx-font-weight: bold;" : "";
        String style = String.format("-fx-font-size: 13px; -fx-padding: 8px 14px; -fx-cursor: hand; -fx-background-color: %s; -fx-text-fill: white; %s",
                bgColorHex, weight);
        btn.setStyle(style);

        // Efecto programático (más seguro que usar -fx-effect en CSS):
        DropShadow ds = new DropShadow();
        ds.setBlurType(BlurType.THREE_PASS_BOX);
        ds.setRadius(4);
        ds.setSpread(0.0);
        ds.setOffsetX(0);
        ds.setOffsetY(1);
        ds.setColor(Color.web("rgba(0,0,0,0.25)"));
        btn.setEffect(ds);

        return btn;
    }

    private Label crearLabel(String texto) {
        Label label = new Label(texto);
        label.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 12px;");
        label.setWrapText(true);
        return label;
    }

    private Label crearSeparador() {
        Label sep = new Label("─".repeat(35));
        sep.setStyle("-fx-text-fill: #7f8c8d;");
        return sep;
    }

    private HBox crearItemLeyenda(String simbolo, String descripcion) {
        HBox item = new HBox(8);
        Label lblSimbolo = new Label(simbolo);
        lblSimbolo.setStyle("-fx-font-size: 14px;");
        Label lblDesc = new Label(descripcion);
        lblDesc.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 11px;");
        item.getChildren().addAll(lblSimbolo, lblDesc);
        return item;
    }

    private void inicializarSimulador() {
        simulador = new Simulador();
        simulador.crearMapaCiudad();
        actualizarLabelsUbicaciones();
    }

    private void actualizarLabelsUbicaciones() {
        if (simulador == null) return;

        Nodo posInicial = simulador.getNodoPosicionInicialAmbulancia();
        Nodo accidente = simulador.getNodoAccidenteActual();
        Nodo hospital = simulador.getNodoHospital();

        if (posInicial != null) {
            lblPosicionInicial.setText("🚑 Ambulancia: " + posInicial.getNombre());
        }

        if (accidente != null) {
            lblUbicacionAccidente.setText("🆘 Accidente: " + accidente.getNombre());
        } else {
            lblUbicacionAccidente.setText("🆘 Accidente: (generando...)");
        }

        if (hospital != null) {
            lblUbicacionHospital.setText("🏥 Hospital: " + hospital.getNombre());
        }
    }

    private void iniciarEmergencia() {
        if (simulador == null) return;
        simulador.generarNuevaEmergencia();
        actualizarLabelsUbicaciones();

        Ambulancia ambulancia = simulador.getAmbulancia();
        if (ambulancia != null && ambulancia.isEnEmergencia()) {
            lblEstado.setText("Estado: 🚨 Emergencia activa");
            animacionActiva = true;
        }
    }

    private void pausarSimulacion() {
        if (animacion != null) {
            animacion.stop();
            lblEstado.setText("Estado: ⏸ Pausado");
            animacionActiva = false;
        }
    }

    private void reanudarSimulacion() {
        if (animacion != null) {
            animacion.start();
            lblEstado.setText("Estado: ▶ Reanudado");
            animacionActiva = true;
        }
    }

    private void reiniciarSimulacion() {
        if (simulador != null) simulador.reiniciar();
        lblEstado.setText("Estado: Esperando");
        lblEstadoDetallado.setText("⏳ Sin emergencia activa");
        lblDistancia.setText("Distancia: 0.00 km");
        lblTiempo.setText("Tiempo: 0.0 seg");
        lblProgreso.setText("Progreso: 0%");
        lblRutasRecalculadas.setText("Rutas recalculadas: 0");
        actualizarLabelsUbicaciones();
        if (!animacionActiva && animacion != null) {
            animacion.start();
            animacionActiva = true;
        }
    }

    private void simularCongestion() {
        if (simulador == null) return;
        Grafo grafo = simulador.getGrafo();
        java.util.Random random = new java.util.Random();
        int callesCongestionadas = 0;
        if (grafo == null) return;
        for (Arista arista : grafo.getAristas()) {
            if (random.nextDouble() < 0.3) {
                arista.setFactorTrafico(1.5 + random.nextDouble());
                callesCongestionadas++;
            }
        }
        System.out.println("🚗 Congestión simulada en " + callesCongestionadas + " calles");
    }

    private void iniciarAnimacion() {
        animacion = new AnimationTimer() {
            @Override
            public void handle(long now) {
                actualizarSimulacion();
                dibujarMapa();
            }
        };
        animacion.start();
        animacionActiva = true;
    }

    private void actualizarSimulacion() {
        if (simulador == null) return;
        simulador.actualizar();

        Ambulancia ambulancia = simulador.getAmbulancia();
        if (ambulancia != null && (ambulancia.isEnEmergencia() ||
                ambulancia.getEstadoActual() != Ambulancia.EstadoEmergencia.ESPERANDO)) {

            lblEstadoDetallado.setText(ambulancia.getDescripcionEstado());
            lblDistancia.setText(String.format("Distancia: %.2f km", simulador.getDistanciaRecorrida()));
            lblTiempo.setText(String.format("Tiempo: %.1f seg", ambulancia.getTiempoEmergencia() / 1000.0));
            lblProgreso.setText(String.format("Progreso: %.0f%%", ambulancia.getProgreso() * 100));
            lblRutasRecalculadas.setText("Rutas recalculadas: " + ambulancia.getRutasRecalculadas());

            SistemaTrafico sistema = simulador.getSistemaTrafico();
            if (sistema != null) {
                SistemaTrafico.EstadisticasSistema stats = sistema.getEstadisticas();
                lblSemaforosPrioridad.setText("Semáforos en prioridad: " + stats.semaforosConPrioridad());
                lblCongestion.setText(String.format("Congestión promedio: %.0f%%",
                        (stats.congestionPromedio() - 1.0) * 100));
            }
        }
    }

    // -------------------- MODO COVER: recalculo de escala --------------------
    private void onResize() {
        if (simulador == null) return;
        MapaOSM mapa = simulador.getMapa();
        double canvasW = canvas.getWidth();
        double canvasH = canvas.getHeight();

        if (mapa != null && mapa.getAnchoPixeles() > 0 && mapa.getAltoPixeles() > 0) {
            double mapW = mapa.getAnchoPixeles();
            double mapH = mapa.getAltoPixeles();

            // COVER: usamos max para que la imagen cubra todo el canvas (posible crop)
            double sX = canvasW / mapW;
            double sY = canvasH / mapH;
            scale = Math.max(sX, sY);

            // offset puede ser negativo para centrar el crop
            double drawW = mapW * scale;
            double drawH = mapH * scale;
            offsetX = (canvasW - drawW) / 2.0;
            offsetY = (canvasH - drawH) / 2.0;
        } else {
            scale = 1.0;
            offsetX = 0;
            offsetY = 0;
        }

        // colapsar panel lateral si ventana estrecha
        double windowW = root.getWidth();
        if (windowW < COLLAPSE_PANEL_WIDTH) {
            if (root.getRight() != null) {
                root.setRight(null);
            }
        } else {
            if (root.getRight() == null && panelLateral != null) {
                root.setRight(panelLateral);
            }
        }

        ajustarFuentesPorScale();
        dibujarMapa();
    }

    private void ajustarFuentesPorScale() {
        if (panelLateral == null) return;
        double factor = Math.max(0.6, Math.min(1.8, scale));
        double baseSize = 12.0 * factor;

        Font labelFont = Font.font(baseSize);
        panelLateral.getChildren().stream()
                .filter(node -> node instanceof Label)
                .forEach(node -> ((Label) node).setFont(labelFont));
    }

    private double mapToCanvasX(double mapX) {
        return offsetX + mapX * scale;
    }

    private double mapToCanvasY(double mapY) {
        return offsetY + mapY * scale;
    }

    private double escalaLinea(double base) {
        return Math.max(1.0, base * Math.max(0.5, scale));
    }

    // -------------------- DIBUJADO (MAPA cubre todo el canvas) --------------------
    private void dibujarMapa() {
        if (simulador == null) {
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            return;
        }

        MapaOSM mapa = simulador.getMapa();
        Grafo grafo = simulador.getGrafo();

        // Si hay imagen del mapa, dibujarla de forma COVER para que cubra todo el canvas
        if (mapa != null && mapa.getImagen() != null) {
            Image imagen = mapa.getImagen();
            double imgW = imagen.getWidth();
            double imgH = imagen.getHeight();

            double drawW = mapa.getAnchoPixeles() * scale;
            double drawH = mapa.getAltoPixeles() * scale;

            // drawImage(inX, inY, inW, inH, outX, outY, outW, outH)
            // aquí usamos toda la fuente (0,0,imgW,imgH) y la pintamos escalada con offset (puede ser negativa)
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()); // limpiar
            gc.setGlobalAlpha(1.0);
            gc.drawImage(imagen, 0, 0, imgW, imgH, offsetX, offsetY, drawW, drawH);
        } else {
            // fallback: rellenar todo canvas para que no se vea el fondo
            gc.setFill(Color.rgb(44, 62, 80));
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        }

        // --- resto del dibujado (calles, ruta, nodos, ambulancia) ---
        if (grafo != null) {
            // Dibujar aristas (calles)
            for (Arista arista : grafo.getAristas()) {
                double congestion = arista.getFactorTrafico();
                Color colorCalle = Color.rgb(52, 152, 219);
                if (congestion > 2.0) colorCalle = Color.rgb(231, 76, 60);
                else if (congestion > 1.5) colorCalle = Color.rgb(230, 126, 34);
                else if (congestion > 1.2) colorCalle = Color.rgb(241, 196, 15);

                gc.setGlobalAlpha(0.85);
                gc.setStroke(colorCalle);
                gc.setLineWidth(escalaLinea(3.0));

                Nodo origen = arista.getOrigen();
                Nodo destino = arista.getDestino();
                gc.strokeLine(mapToCanvasX(origen.getX()), mapToCanvasY(origen.getY()),
                        mapToCanvasX(destino.getX()), mapToCanvasY(destino.getY()));
            }
            gc.setGlobalAlpha(1.0);

            // Dibujar ruta de la ambulancia
            Ambulancia ambulancia = simulador.getAmbulancia();
            if (ambulancia != null && ambulancia.getRutaActual() != null) {
                List<Nodo> ruta = ambulancia.getRutaActual();

                Color colorRuta = (ambulancia.getEstadoActual() == Ambulancia.EstadoEmergencia.EN_RUTA_HOSPITAL)
                        ? Color.rgb(52, 152, 219)
                        : Color.rgb(46, 204, 113);

                gc.setGlobalAlpha(0.95);
                gc.setStroke(colorRuta);
                gc.setLineWidth(escalaLinea(6.0));
                for (int i = 0; i < ruta.size() - 1; i++) {
                    Nodo a = ruta.get(i);
                    Nodo b = ruta.get(i + 1);
                    gc.strokeLine(mapToCanvasX(a.getX()), mapToCanvasY(a.getY()),
                            mapToCanvasX(b.getX()), mapToCanvasY(b.getY()));
                }
            }

            // Dibujar nodos y semáforos
            double baseNodoRadius = 18.0;
            for (Nodo nodo : grafo.getNodos().values()) {
                double cx = mapToCanvasX(nodo.getX());
                double cy = mapToCanvasY(nodo.getY());

                gc.setFill(Color.rgb(52, 73, 94));
                double rBase = baseNodoRadius * Math.max(0.6, scale);
                gc.fillOval(cx - rBase, cy - rBase, rBase * 2, rBase * 2);

                Semaforo sem = nodo.getSemaforo();
                boolean prioridad = sem != null && sem.isModoPrioridad();

                Color colorSem = (sem != null && sem.getEstadoActual() == Semaforo.Estado.VERDE)
                        ? Color.rgb(46, 204, 113) : Color.rgb(231, 76, 60);

                if (prioridad) {
                    gc.setGlobalAlpha(0.5);
                    gc.setFill(Color.WHITE);
                    double r1 = (rBase - 4);
                    gc.fillOval(cx - r1, cy - r1, r1 * 2, r1 * 2);
                }

                gc.setGlobalAlpha(1.0);
                double rSem = rBase * 0.55;
                gc.setFill(colorSem);
                gc.fillOval(cx - rSem, cy - rSem, rSem * 2, rSem * 2);

                gc.setStroke(prioridad ? Color.WHITE : Color.BLACK);
                gc.setLineWidth(Math.max(1.0, scale));
                gc.strokeOval(cx - rSem, cy - rSem, rSem * 2, rSem * 2);

                gc.setFont(Font.font(10 * Math.max(0.7, scale)));
                gc.setLineWidth(Math.max(0.8, 0.8 * Math.max(0.5, scale))); // ancho del contorno (ajustable)
                gc.setStroke(Color.WHITE);
                gc.strokeText(nodo.getNombre(), cx - 20 * Math.max(0.7, scale), cy + rBase + 8 * Math.max(0.7, scale));
                gc.setFill(Color.BLACK);
                gc.fillText(nodo.getNombre(), cx - 20 * Math.max(0.7, scale), cy + rBase + 8 * Math.max(0.7, scale));
            }

            // Dibujar ambulancia
            ambulancia = simulador.getAmbulancia();
            if (ambulancia != null) {
                double ax = mapToCanvasX(ambulancia.getX());
                double ay = mapToCanvasY(ambulancia.getY());
                double boxSize = 30 * Math.max(0.6, scale);

                gc.setGlobalAlpha(1.0);
                gc.setFill(Color.rgb(231, 76, 60));
                gc.fillRect(ax - boxSize/2, ay - boxSize/2, boxSize, boxSize);

                gc.setStroke(Color.WHITE);
                gc.setLineWidth(Math.max(1.0, scale));
                gc.strokeRect(ax - boxSize/2, ay - boxSize/2, boxSize, boxSize);

                gc.setFill(Color.WHITE);
                gc.setFont(Font.font(12 * Math.max(0.7, scale)));
                gc.fillText("🚑", ax - 6 * Math.max(0.7, scale), ay + 4 * Math.max(0.7, scale));

                if (ambulancia.tieneParticipante()) {
                    gc.setFill(Color.LIGHTBLUE);
                    gc.fillText("👤", ax + boxSize/2 + 4, ay - boxSize/2 - 4);
                }
            }

            // Marcadores especiales
            Nodo accidente = simulador.getNodoAccidenteActual();
            if (accidente != null) {
                gc.setFill(Color.ORANGE);
                gc.setFont(Font.font(12 * Math.max(0.7, scale)));
                gc.fillText("🆘 ACCIDENTE", mapToCanvasX(accidente.getX()) + 8, mapToCanvasY(accidente.getY()) - 8);
            }

            Nodo hospital = simulador.getNodoHospital();
            if (hospital != null) {
                gc.setFill(Color.LIGHTBLUE);
                gc.setFont(Font.font(12 * Math.max(0.7, scale)));
                gc.fillText("🏥 HOSPITAL", mapToCanvasX(hospital.getX()) + 8, mapToCanvasY(hospital.getY()) - 8);
            }
        }
        gc.setGlobalAlpha(1.0);
    }
}
