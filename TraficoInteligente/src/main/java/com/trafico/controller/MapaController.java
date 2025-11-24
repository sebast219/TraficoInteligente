package com.trafico.controller;

import com.trafico.model.*;
import com.trafico.util.Simulador;
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

/**
 * Gestiona la lógica de la simulación y la animación en el mapa 2D.
 * Interactúa con los modelos (nodos, ambulancia, semáforos).
 * Controla botones como "Iniciar", "Pausar", "Reiniciar".
 * Dibuja el mapa OSM de fondo con el grafo superpuesto.
 */
public class MapaController {
    private BorderPane root;
    private Canvas canvas;
    private GraphicsContext gc;
    private Simulador simulador;
    private AnimationTimer animacion;
    
    private Label lblEstado;
    private Label lblDistancia;
    private Label lblTiempo;
    
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
        panelSuperior.setPadding(new Insets(10));
        panelSuperior.setAlignment(Pos.CENTER);
        panelSuperior.setStyle("-fx-background-color: #2c3e50;");
        
        Button btnIniciar = new Button("🚑 Iniciar Emergencia");
        Button btnPausar = new Button("⏸ Pausar");
        Button btnReiniciar = new Button("🔄 Reiniciar");
        
        btnIniciar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        btnPausar.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        btnReiniciar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        
        btnIniciar.setOnAction(e -> iniciarEmergencia());
        btnPausar.setOnAction(e -> pausarSimulacion());
        btnReiniciar.setOnAction(e -> reiniciarSimulacion());
        
        panelSuperior.getChildren().addAll(btnIniciar, btnPausar, btnReiniciar);
        
        // Panel lateral con información
        VBox panelLateral = new VBox(10);
        panelLateral.setPadding(new Insets(10));
        panelLateral.setStyle("-fx-background-color: #34495e;");
        panelLateral.setPrefWidth(200);
        
        Label titulo = new Label("📊 ESTADO");
        titulo.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        
        lblEstado = new Label("Estado: Esperando");
        lblDistancia = new Label("Distancia: 0 km");
        lblTiempo = new Label("Tiempo: 0 min");
        
        lblEstado.setStyle("-fx-text-fill: white;");
        lblDistancia.setStyle("-fx-text-fill: white;");
        lblTiempo.setStyle("-fx-text-fill: white;");
        
        Label leyenda = new Label("\n🚦 LEYENDA:\n\n🟢 Semáforo Verde\n🟡 Semáforo Amarillo\n🔴 Semáforo Rojo\n\n🚑 Ambulancia\n📍 Accidente\n🏥 Hospital");
        leyenda.setStyle("-fx-text-fill: white; -fx-font-size: 11px;");
        
        // Atribución OSM
        Label atribucion = new Label("\n© OpenStreetMap\ncontributors");
        atribucion.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 9px;");
        
        panelLateral.getChildren().addAll(titulo, lblEstado, lblDistancia, lblTiempo, leyenda, atribucion);
        
        // Agregar componentes al root
        root.setTop(panelSuperior);
        root.setCenter(canvas);
        root.setRight(panelLateral);
    }
    
    private void inicializarSimulador() {
        simulador = new Simulador();
        simulador.crearMapaCiudad();
        
        // Ajustar tamaño del canvas según el mapa
        MapaOSM mapa = simulador.getMapa();
        if (mapa != null && mapa.getImagen() != null) {
            canvas.setWidth(mapa.getAnchoPixeles());
            canvas.setHeight(mapa.getAltoPixeles());
        }
    }
    
    private void iniciarEmergencia() {
        Grafo grafo = simulador.getGrafo();
        Ambulancia ambulancia = simulador.getAmbulancia();
        
        // Base → Accidente
        Nodo base = grafo.getNodo("n0");
        Nodo accidente = grafo.getNodo("n6");
        
        if (base != null && accidente != null) {
            List<Nodo> rutaAlAccidente = grafo.calcularRutaMasCorta(base, accidente);
            if (!rutaAlAccidente.isEmpty()) {
                ambulancia.iniciarEmergencia(rutaAlAccidente, accidente);
                lblEstado.setText("Estado: En ruta al accidente");
            }
        }
    }
    
    private void pausarSimulacion() {
        if (animacion != null) {
            animacion.stop();
            lblEstado.setText("Estado: Pausado");
        }
    }
    
    private void reiniciarSimulacion() {
        simulador.reiniciar();
        lblEstado.setText("Estado: Esperando");
        lblDistancia.setText("Distancia: 0 km");
        lblTiempo.setText("Tiempo: 0 min");
        // Reiniciar animación si estaba pausada
        if (animacion != null) {
            animacion.start();
        }
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
    }
    
    private void actualizarSimulacion() {
        simulador.actualizar();
        
        // Actualizar información
        Ambulancia ambulancia = simulador.getAmbulancia();
        if (ambulancia != null && ambulancia.isEnEmergencia()) {
            lblEstado.setText("Estado: Emergencia activa");
            lblDistancia.setText(String.format("Distancia: %.2f km", simulador.getDistanciaRecorrida()));
            lblTiempo.setText(String.format("Tiempo: %.1f min", simulador.getTiempoTranscurrido()));
        }
    }
    
    private void dibujarMapa() {
        // Limpiar canvas con color de fondo
        gc.setFill(Color.rgb(44, 62, 80));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        MapaOSM mapa = simulador.getMapa();
        Grafo grafo = simulador.getGrafo();
        
        // 1. Dibujar imagen de fondo OSM (si está disponible)
        if (mapa != null && mapa.getImagen() != null) {
            gc.drawImage(mapa.getImagen(), 0, 0);
        }
        
        // 2. Dibujar aristas (calles) con transparencia
        gc.setGlobalAlpha(0.7);
        gc.setStroke(Color.rgb(52, 152, 219));
        gc.setLineWidth(3);
        for (Arista arista : grafo.getAristas()) {
            Nodo origen = arista.getOrigen();
            Nodo destino = arista.getDestino();
            gc.strokeLine(origen.getX(), origen.getY(), destino.getX(), destino.getY());
        }
        
        // 3. Dibujar ruta de la ambulancia si existe
        Ambulancia ambulancia = simulador.getAmbulancia();
        if (ambulancia != null && ambulancia.getRutaActual() != null) {
            gc.setGlobalAlpha(0.9);
            gc.setStroke(Color.rgb(46, 204, 113));
            gc.setLineWidth(5);
            List<Nodo> ruta = ambulancia.getRutaActual();
            for (int i = 0; i < ruta.size() - 1; i++) {
                Nodo actual = ruta.get(i);
                Nodo siguiente = ruta.get(i + 1);
                gc.strokeLine(actual.getX(), actual.getY(), siguiente.getX(), siguiente.getY());
            }
        }
        
        // 4. Dibujar nodos (intersecciones) con semáforos
        gc.setGlobalAlpha(1.0);
        for (Nodo nodo : grafo.getNodos().values()) {
            // Círculo del nodo
            gc.setFill(Color.rgb(52, 73, 94));
            gc.fillOval(nodo.getX() - 15, nodo.getY() - 15, 30, 30);
            
            // Semáforo
            Color colorSemaforo;
            switch (nodo.getSemaforo().getEstadoActual()) {
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
            gc.setFill(colorSemaforo);
            gc.fillOval(nodo.getX() - 8, nodo.getY() - 8, 16, 16);
            
            // Borde del semáforo
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeOval(nodo.getX() - 8, nodo.getY() - 8, 16, 16);
            
            // Nombre del nodo
            gc.setFill(Color.WHITE);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.fillText(nodo.getNombre(), nodo.getX() - 20, nodo.getY() + 35);
        }
        
        // 5. Dibujar ambulancia
        if (ambulancia != null) {
            gc.setFill(Color.rgb(231, 76, 60));
            gc.fillRect(ambulancia.getX() - 12, ambulancia.getY() - 12, 24, 24);
            
            gc.setFill(Color.WHITE);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.fillText("🚑", ambulancia.getX() - 8, ambulancia.getY() + 5);
        }
        
        // 6. Dibujar marcadores especiales
        Nodo accidente = grafo.getNodo("n6");
        if (accidente != null) {
            gc.setFill(Color.ORANGE);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.fillText("📍 ACCIDENTE", accidente.getX() + 20, accidente.getY());
        }
        
        Nodo hospital = grafo.getNodo("n7");
        if (hospital != null) {
            gc.setFill(Color.LIGHTBLUE);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.fillText("🏥 HOSPITAL", hospital.getX() + 20, hospital.getY());
        }
        
        gc.setGlobalAlpha(1.0); // Restaurar opacidad
    }
}
