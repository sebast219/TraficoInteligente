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
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;

import java.util.List;

/**
 * Controlador mejorado con visualización avanzada del sistema inteligente.
 */
public class MapaController {
    private BorderPane root;
    private Canvas canvas;
    private GraphicsContext gc;
    private Simulador simulador;
    private AnimationTimer animacion;
    private boolean animacionActiva = false;

    // Labels de información
    private Label lblEstado;
    private Label lblDistancia;
    private Label lblTiempo;
    private Label lblProgreso;
    private Label lblRutasRecalculadas;
    private Label lblCongestion;
    private Label lblSemaforosPrioridad;

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

        Button btnIniciar = new Button("🚑 Iniciar Emergencia");
        Button btnPausar = new Button("⏸ Pausar");
        Button btnReanudar = new Button("▶ Reanudar");
        Button btnReiniciar = new Button("🔄 Reiniciar");

        // Estilos mejorados para botones
        String estiloBoton = "-fx-font-size: 14px; -fx-padding: 10px 20px; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);";

        btnIniciar.setStyle(estiloBoton + "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        btnPausar.setStyle(estiloBoton + "-fx-background-color: #f39c12; -fx-text-fill: white;");
        btnReanudar.setStyle(estiloBoton + "-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnReiniciar.setStyle(estiloBoton + "-fx-background-color: #3498db; -fx-text-fill: white;");

        btnIniciar.setOnAction(e -> iniciarEmergencia());
        btnPausar.setOnAction(e -> pausarSimulacion());
        btnReanudar.setOnAction(e -> reanudarSimulacion());
        btnReiniciar.setOnAction(e -> reiniciarSimulacion());

        // Botón de simulación de tráfico
        Button btnSimularTrafico = new Button("🚗 Simular Tráfico");
        btnSimularTrafico.setStyle(estiloBoton + "-fx-background-color: #9b59b6; -fx-text-fill: white;");
        btnSimularTrafico.setOnAction(e -> simularCongestion());

        panelSuperior.getChildren().addAll(btnIniciar, btnPausar, btnReanudar, btnReiniciar, btnSimularTrafico);

        // Panel lateral con información MEJORADA
        VBox panelLateral = new VBox(12);
        panelLateral.setPadding(new Insets(15));
        panelLateral.setStyle("-fx-background-color: linear-gradient(to bottom, #34495e, #2c3e50); " +
                "-fx-background-radius: 10px;");
        panelLateral.setPrefWidth(250);

        // Título
        Label titulo = new Label("📊 PANEL DE CONTROL");
        titulo.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        // Sección: Estado de Emergencia
        Label subtituloEmergencia = new Label("🚨 EMERGENCIA");
        subtituloEmergencia.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 14px; -fx-font-weight: bold;");

        lblEstado = crearLabel("Estado: Esperando");
        lblDistancia = crearLabel("Distancia: 0.00 km");
        lblTiempo = crearLabel("Tiempo: 0.0 min");
        lblProgreso = crearLabel("Progreso: 0%");
        lblRutasRecalculadas = crearLabel("Rutas recalculadas: 0");

        // Separador
        Label separador1 = new Label("─".repeat(30));
        separador1.setStyle("-fx-text-fill: #7f8c8d;");

        // Sección: Sistema Inteligente
        Label subtituloSistema = new Label("🤖 SISTEMA INTELIGENTE");
        subtituloSistema.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 14px; -fx-font-weight: bold;");

        lblSemaforosPrioridad = crearLabel("Semáforos en prioridad: 0");
        lblCongestion = crearLabel("Congestión promedio: 0%");

        // Separador
        Label separador2 = new Label("─".repeat(30));
        separador2.setStyle("-fx-text-fill: #7f8c8d;");

        // Leyenda
        Label tituloLeyenda = new Label("🗺️ LEYENDA");
        tituloLeyenda.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 14px; -fx-font-weight: bold;");

        VBox leyenda = new VBox(5);
        leyenda.getChildren().addAll(
                crearItemLeyenda("🟢", "Semáforo Verde"),
                crearItemLeyenda("🟡", "Semáforo Amarillo"),
                crearItemLeyenda("🔴", "Semáforo Rojo"),
                crearItemLeyenda("💚", "Prioridad Activa"),
                crearItemLeyenda("🚑", "Ambulancia"),
                crearItemLeyenda("📍", "Accidente"),
                crearItemLeyenda("🏥", "Hospital")
        );

        // Atribución OSM
        Label atribucion = new Label("\n© OpenStreetMap contributors\nODbL License");
        atribucion.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 9px; -fx-text-alignment: center;");

        panelLateral.getChildren().addAll(
                titulo,
                subtituloEmergencia,
                lblEstado,
                lblDistancia,
                lblTiempo,
                lblProgreso,
                lblRutasRecalculadas,
                separador1,
                subtituloSistema,
                lblSemaforosPrioridad,
                lblCongestion,
                separador2,
                tituloLeyenda,
                leyenda,
                atribucion
        );

        // Agregar componentes al root
        root.setTop(panelSuperior);
        root.setCenter(canvas);
        root.setRight(panelLateral);
    }

    private Label crearLabel(String texto) {
        Label label = new Label(texto);
        label.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 12px;");
        return label;
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

        // Ajustar tamaño del canvas
        MapaOSM mapa = simulador.getMapa();
        if (mapa != null) {
            canvas.setWidth(mapa.getAnchoPixeles());
            canvas.setHeight(mapa.getAltoPixeles());
        }
    }

    private void iniciarEmergencia() {
        Grafo grafo = simulador.getGrafo();
        Ambulancia ambulancia = simulador.getAmbulancia();

        // Base → Accidente → Hospital (dos etapas)
        Nodo base = grafo.getNodo("n0");
        Nodo accidente = grafo.getNodo("n6");

        if (base != null && accidente != null && ambulancia != null) {
            List<Nodo> rutaAlAccidente = grafo.calcularRutaMasCorta(base, accidente);

            if (!rutaAlAccidente.isEmpty()) {
                ambulancia.iniciarEmergencia(rutaAlAccidente, accidente);
                lblEstado.setText("Estado: 🚨 En ruta al accidente");
                animacionActiva = true;
            } else {
                lblEstado.setText("Estado: ❌ No hay ruta disponible");
            }
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
        lblDistancia.setText("Distancia: 0.00 km");
        lblTiempo.setText("Tiempo: 0.0 min");
        lblProgreso.setText("Progreso: 0%");
        lblRutasRecalculadas.setText("Rutas recalculadas: 0");
        lblSemaforosPrioridad.setText("Semáforos en prioridad: 0");
        lblCongestion.setText("Congestión promedio: 0%");

        if (!animacionActiva && animacion != null) {
            animacion.start();
            animacionActiva = true;
        }
    }

    private void simularCongestion() {
        // Simular congestión aleatoria en algunas calles
        Grafo grafo = simulador.getGrafo();
        java.util.Random random = new java.util.Random();

        int callesCongestionadas = 0;
        for (Arista arista : grafo.getAristas()) {
            if (random.nextDouble() < 0.3) { // 30% de probabilidad
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
        if (ambulancia != null && ambulancia.isEnEmergencia()) {
            // Actualizar información básica
            lblEstado.setText("Estado: 🚨 Emergencia activa");
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

            // Color según congestión
            Color colorCalle;
            if (congestion > 2.0) {
                colorCalle = Color.rgb(231, 76, 60); // Rojo oscuro
            } else if (congestion > 1.5) {
                colorCalle = Color.rgb(230, 126, 34); // Naranja
            } else if (congestion > 1.2) {
                colorCalle = Color.rgb(241, 196, 15); // Amarillo
            } else {
                colorCalle = Color.rgb(52, 152, 219); // Azul normal
            }

            gc.setGlobalAlpha(0.6);
            gc.setStroke(colorCalle);
            gc.setLineWidth(4);

            Nodo origen = arista.getOrigen();
            Nodo destino = arista.getDestino();
            gc.strokeLine(origen.getX(), origen.getY(), destino.getX(), destino.getY());
        }
        gc.setGlobalAlpha(1.0);

        // 3. Dibujar ruta de la ambulancia con efecto brillante
        Ambulancia ambulancia = simulador.getAmbulancia();
        if (ambulancia != null && ambulancia.getRutaActual() != null) {
            List<Nodo> ruta = ambulancia.getRutaActual();

            // Sombra de la ruta
            gc.setGlobalAlpha(0.3);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(8);
            for (int i = 0; i < ruta.size() - 1; i++) {
                Nodo actual = ruta.get(i);
                Nodo siguiente = ruta.get(i + 1);
                gc.strokeLine(actual.getX(), actual.getY() + 2,
                        siguiente.getX(), siguiente.getY() + 2);
            }

            // Ruta principal con brillo
            gc.setGlobalAlpha(1.0);
            gc.setStroke(Color.rgb(46, 204, 113));
            gc.setLineWidth(6);
            for (int i = 0; i < ruta.size() - 1; i++) {
                Nodo actual = ruta.get(i);
                Nodo siguiente = ruta.get(i + 1);
                gc.strokeLine(actual.getX(), actual.getY(),
                        siguiente.getX(), siguiente.getY());
            }
        }

        // 4. Dibujar nodos con semáforos mejorados
        for (Nodo nodo : grafo.getNodos().values()) {
            // Base del nodo
            gc.setFill(Color.rgb(52, 73, 94));
            gc.fillOval(nodo.getX() - 18, nodo.getY() - 18, 36, 36);

            // Borde del nodo
            gc.setStroke(Color.rgb(44, 62, 80));
            gc.setLineWidth(3);
            gc.strokeOval(nodo.getX() - 18, nodo.getY() - 18, 36, 36);

            // Semáforo con efecto brillante si tiene prioridad
            Semaforo semaforo = nodo.getSemaforo();
            Color colorSemaforo;
            boolean tienePrioridad = semaforo.isModoPrioridad();

            switch (semaforo.getEstadoActual()) {
                case VERDE:
                    colorSemaforo = Color.rgb(46, 204, 113);
                    break;
                case AMARILLO:
                    colorSemaforo = Color.rgb(241, 196, 15);
                    break;
                case ROJO:
                default:
                    colorSemaforo = Color.rgb(231, 76, 60);
                    break;
            }

            // Efecto de brillo para prioridad
            if (tienePrioridad) {
                gc.setGlobalAlpha(0.5);
                gc.setFill(Color.WHITE);
                gc.fillOval(nodo.getX() - 14, nodo.getY() - 14, 28, 28);
            }

            // Semáforo
            gc.setGlobalAlpha(1.0);
            gc.setFill(colorSemaforo);
            gc.fillOval(nodo.getX() - 10, nodo.getY() - 10, 20, 20);

            // Borde del semáforo
            gc.setStroke(tienePrioridad ? Color.WHITE : Color.BLACK);
            gc.setLineWidth(tienePrioridad ? 3 : 2);
            gc.strokeOval(nodo.getX() - 10, nodo.getY() - 10, 20, 20);

            // Nombre del nodo con sombra
            gc.setGlobalAlpha(0.7);
            gc.setFill(Color.BLACK);
            gc.fillText(nodo.getNombre(), nodo.getX() - 18, nodo.getY() + 37);

            gc.setGlobalAlpha(1.0);
            gc.setFill(Color.WHITE);
            gc.fillText(nodo.getNombre(), nodo.getX() - 20, nodo.getY() + 35);
        }

        // 5. Dibujar ambulancia con animación
        if (ambulancia != null) {
            // Sombra de ambulancia
            gc.setGlobalAlpha(0.3);
            gc.setFill(Color.BLACK);
            gc.fillRect(ambulancia.getX() - 13, ambulancia.getY() - 11, 26, 26);

            // Ambulancia
            gc.setGlobalAlpha(1.0);
            gc.setFill(Color.rgb(231, 76, 60));
            gc.fillRect(ambulancia.getX() - 15, ambulancia.getY() - 15, 30, 30);

            // Borde blanco
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2);
            gc.strokeRect(ambulancia.getX() - 15, ambulancia.getY() - 15, 30, 30);

            // Emoji ambulancia
            gc.setFill(Color.WHITE);
            gc.fillText("🚑", ambulancia.getX() - 10, ambulancia.getY() + 5);
        }

        // 6. Dibujar marcadores
        Nodo accidente = grafo.getNodo("n6");
        if (accidente != null) {
            gc.setFill(Color.ORANGE);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.fillText("📍 ACCIDENTE", accidente.getX() + 25, accidente.getY());
        }

        Nodo hospital = grafo.getNodo("n7");
        if (hospital != null) {
            gc.setFill(Color.LIGHTBLUE);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.fillText("🏥 HOSPITAL", hospital.getX() + 25, hospital.getY());
        }

        gc.setGlobalAlpha(1.0);
    }
}