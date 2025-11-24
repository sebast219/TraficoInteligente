package com.trafico.controller;

import com.trafico.model.*;
import com.trafico.util.Simulador;
import com.trafico.util.SistemaTrafico;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.util.List;

public class MapaController {
    private BorderPane root;
    private Canvas canvas;
    private GraphicsContext gc;
    private Simulador simulador;
    private AnimationTimer animacion;
    private boolean animacionActiva = false;

    // Labels de información
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

    public MapaController(BorderPane root) {
        this.root = root;
        this.canvas = new Canvas(1200, 800);
        this.gc = canvas.getGraphicsContext2D();
    }

    public void inicializar() {
        configurarUI();
        inicializarSimulador();
        iniciarAnimacion();
    }

    private void configurarUI() {
        // Panel superior con controles
        HBox panelSuperior = new HBox(15);
        panelSuperior.setPadding(new Insets(15));
        panelSuperior.setAlignment(Pos.CENTER);
        panelSuperior.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);");

        Button btnIniciar = new Button("🚨 Nueva Emergencia");
        Button btnPausar = new Button("⏸ Pausar");
        Button btnReanudar = new Button("▶ Reanudar");
        Button btnReiniciar = new Button("🔄 Reiniciar");
        Button btnSimularTrafico = new Button("🚗 Simular Tráfico");

        String estiloBoton = "-fx-font-size: 14px; -fx-padding: 10px 20px; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);";

        btnIniciar.setStyle(estiloBoton + "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        btnPausar.setStyle(estiloBoton + "-fx-background-color: #f39c12; -fx-text-fill: white;");
        btnReanudar.setStyle(estiloBoton + "-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnReiniciar.setStyle(estiloBoton + "-fx-background-color: #3498db; -fx-text-fill: white;");
        btnSimularTrafico.setStyle(estiloBoton + "-fx-background-color: #9b59b6; -fx-text-fill: white;");

        btnIniciar.setOnAction(e -> iniciarEmergencia());
        btnPausar.setOnAction(e -> pausarSimulacion());
        btnReanudar.setOnAction(e -> reanudarSimulacion());
        btnReiniciar.setOnAction(e -> reiniciarSimulacion());
        btnSimularTrafico.setOnAction(e -> simularCongestion());

        panelSuperior.getChildren().addAll(btnIniciar, btnPausar, btnReanudar, btnReiniciar, btnSimularTrafico);

        // Panel lateral con información
        VBox panelLateral = new VBox(12);
        panelLateral.setPadding(new Insets(15));
        panelLateral.setStyle("-fx-background-color: linear-gradient(to bottom, #34495e, #2c3e50); " +
                "-fx-background-radius: 10px;");
        panelLateral.setPrefWidth(280);

        Label titulo = new Label("📊 PANEL DE CONTROL");
        titulo.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        // Sección: Ubicaciones
        Label subtituloUbicaciones = new Label("📍 UBICACIONES");
        subtituloUbicaciones.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 14px; -fx-font-weight: bold;");

        lblPosicionInicial = crearLabel("🚑 Ambulancia: ---");
        lblUbicacionAccidente = crearLabel("🆘 Accidente: ---");
        lblUbicacionHospital = crearLabel("🏥 Hospital: ---");

        Label separador1 = crearSeparador();

        // Sección: Estado de Emergencia
        Label subtituloEmergencia = new Label("🚨 EMERGENCIA");
        subtituloEmergencia.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 14px; -fx-font-weight: bold;");

        lblEstado = crearLabel("Estado: Esperando");
        lblEstadoDetallado = crearLabel("⏳ Sin emergencia activa");
        lblDistancia = crearLabel("Distancia: 0.00 km");
        lblTiempo = crearLabel("Tiempo: 0.0 seg");
        lblProgreso = crearLabel("Progreso: 0%");
        lblRutasRecalculadas = crearLabel("Rutas recalculadas: 0");

        Label separador2 = crearSeparador();

        // Sección: Sistema Inteligente
        Label subtituloSistema = new Label("🤖 SISTEMA INTELIGENTE");
        subtituloSistema.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 14px; -fx-font-weight: bold;");

        lblSemaforosPrioridad = crearLabel("Semáforos en prioridad: 0");
        lblCongestion = crearLabel("Congestión promedio: 0%");

        Label separador3 = crearSeparador();

        // Leyenda
        Label tituloLeyenda = new Label("🗺️ LEYENDA");
        tituloLeyenda.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 14px; -fx-font-weight: bold;");

        VBox leyenda = new VBox(5);
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

        MapaOSM mapa = simulador.getMapa();
        if (mapa != null) {
            canvas.setWidth(mapa.getAnchoPixeles());
            canvas.setHeight(mapa.getAltoPixeles());
        }

        // Mostrar ubicaciones iniciales
        actualizarLabelsUbicaciones();
    }

    private void actualizarLabelsUbicaciones() {
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
        // Generar nueva emergencia con ubicaciones aleatorias
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
        simulador.reiniciar();
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
        Grafo grafo = simulador.getGrafo();
        java.util.Random random = new java.util.Random();

        int callesCongestionadas = 0;
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
        simulador.actualizar();

        Ambulancia ambulancia = simulador.getAmbulancia();
        if (ambulancia != null && (ambulancia.isEnEmergencia() ||
                ambulancia.getEstadoActual() != Ambulancia.EstadoEmergencia.ESPERANDO)) {

            // Actualizar estado detallado
            lblEstadoDetallado.setText(ambulancia.getDescripcionEstado());

            // Actualizar métricas
            lblDistancia.setText(String.format("Distancia: %.2f km", simulador.getDistanciaRecorrida()));
            lblTiempo.setText(String.format("Tiempo: %.1f seg", ambulancia.getTiempoEmergencia() / 1000.0));
            lblProgreso.setText(String.format("Progreso: %.0f%%", ambulancia.getProgreso() * 100));
            lblRutasRecalculadas.setText("Rutas recalculadas: " + ambulancia.getRutasRecalculadas());

            // Actualizar estadísticas del sistema
            SistemaTrafico sistema = simulador.getSistemaTrafico();
            if (sistema != null) {
                SistemaTrafico.EstadisticasSistema stats = sistema.getEstadisticas();
                lblSemaforosPrioridad.setText("Semáforos en prioridad: " + stats.semaforosConPrioridad);
                lblCongestion.setText(String.format("Congestión promedio: %.0f%%",
                        (stats.congestionPromedio - 1.0) * 100));
            }
        }
    }

    private void dibujarMapa() {
        // Limpiar canvas
        gc.setFill(Color.rgb(44, 62, 80));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        MapaOSM mapa = simulador.getMapa();
        Grafo grafo = simulador.getGrafo();

        // 1. Dibujar imagen de fondo OSM
        if (mapa != null && mapa.getImagen() != null) {
            gc.setGlobalAlpha(0.8);
            gc.drawImage(mapa.getImagen(), 0, 0);
            gc.setGlobalAlpha(1.0);
        }

        // 2. Dibujar aristas con indicador de congestión
        for (Arista arista : grafo.getAristas()) {
            double congestion = arista.getFactorTrafico();
            Color colorCalle = Color.rgb(52, 152, 219); // Azul por defecto

            if (congestion > 2.0) {
                colorCalle = Color.rgb(231, 76, 60);
            } else if (congestion > 1.5) {
                colorCalle = Color.rgb(230, 126, 34);
            } else if (congestion > 1.2) {
                colorCalle = Color.rgb(241, 196, 15);
            }

            gc.setGlobalAlpha(0.6);
            gc.setStroke(colorCalle);
            gc.setLineWidth(4);

            Nodo origen = arista.getOrigen();
            Nodo destino = arista.getDestino();
            gc.strokeLine(origen.getX(), origen.getY(), destino.getX(), destino.getY());
        }
        gc.setGlobalAlpha(1.0);

        // 3. Dibujar ruta de la ambulancia
        Ambulancia ambulancia = simulador.getAmbulancia();
        if (ambulancia != null && ambulancia.getRutaActual() != null) {
            List<Nodo> ruta = ambulancia.getRutaActual();

            // Color según etapa
            Color colorRuta;
            if (ambulancia.getEstadoActual() == Ambulancia.EstadoEmergencia.EN_RUTA_HOSPITAL) {
                colorRuta = Color.rgb(52, 152, 219); // Azul = llevando paciente
            } else {
                colorRuta = Color.rgb(46, 204, 113); // Verde = yendo al accidente
            }

            gc.setGlobalAlpha(0.9);
            gc.setStroke(colorRuta);
            gc.setLineWidth(6);
            for (int i = 0; i < ruta.size() - 1; i++) {
                Nodo actual = ruta.get(i);
                Nodo siguiente = ruta.get(i + 1);
                gc.strokeLine(actual.getX(), actual.getY(), siguiente.getX(), siguiente.getY());
            }
        }

        // 4. Dibujar nodos con semáforos
        for (Nodo nodo : grafo.getNodos().values()) {
            gc.setGlobalAlpha(1.0);
            gc.setFill(Color.rgb(52, 73, 94));
            gc.fillOval(nodo.getX() - 18, nodo.getY() - 18, 36, 36);

            Semaforo semaforo = nodo.getSemaforo();
            boolean tienePrioridad = semaforo.isModoPrioridad();

            Color colorSemaforo = semaforo.getEstadoActual() == Semaforo.Estado.VERDE ?
                    Color.rgb(46, 204, 113) : Color.rgb(231, 76, 60);

            if (tienePrioridad) {
                gc.setGlobalAlpha(0.5);
                gc.setFill(Color.WHITE);
                gc.fillOval(nodo.getX() - 14, nodo.getY() - 14, 28, 28);
            }

            gc.setGlobalAlpha(1.0);
            gc.setFill(colorSemaforo);
            gc.fillOval(nodo.getX() - 10, nodo.getY() - 10, 20, 20);

            gc.setStroke(tienePrioridad ? Color.WHITE : Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeOval(nodo.getX() - 10, nodo.getY() - 10, 20, 20);

            // Nombre del nodo
            gc.setFill(Color.WHITE);
            gc.fillText(nodo.getNombre(), nodo.getX() - 20, nodo.getY() + 35);
        }

        // 5. Dibujar ambulancia
        if (ambulancia != null) {
            gc.setGlobalAlpha(1.0);
            gc.setFill(Color.rgb(231, 76, 60));
            gc.fillRect(ambulancia.getX() - 15, ambulancia.getY() - 15, 30, 30);

            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2);
            gc.strokeRect(ambulancia.getX() - 15, ambulancia.getY() - 15, 30, 30);

            gc.setFill(Color.WHITE);
            gc.fillText("🚑", ambulancia.getX() - 10, ambulancia.getY() + 5);

            // Indicador si lleva paciente
            if (ambulancia.tieneParticipante()) {
                gc.setFill(Color.LIGHTBLUE);
                gc.fillText("👤", ambulancia.getX() + 15, ambulancia.getY() - 10);
            }
        }

        // 6. Dibujar marcadores especiales
        Nodo accidente = simulador.getNodoAccidenteActual();
        if (accidente != null && ambulancia != null &&
                ambulancia.getEstadoActual() != Ambulancia.EstadoEmergencia.FINALIZADO) {
            gc.setFill(Color.ORANGE);
            gc.fillText("🆘 ACCIDENTE", accidente.getX() + 25, accidente.getY());
        }

        Nodo hospital = simulador.getNodoHospital();
        if (hospital != null) {
            gc.setFill(Color.LIGHTBLUE);
            gc.fillText("🏥 HOSPITAL", hospital.getX() + 25, hospital.getY());
        }

        gc.setGlobalAlpha(1.0);
    }
}