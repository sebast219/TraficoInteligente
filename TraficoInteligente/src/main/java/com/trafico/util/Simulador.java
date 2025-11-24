// ============================================
// Simulador.java - MEJORADO
// Integra el sistema de tráfico inteligente
// ============================================
package com.trafico.util;

import com.trafico.model.*;
import java.util.List;

public class Simulador {
    private Grafo grafo;
    private Ambulancia ambulancia;
    private MapaOSM mapa;
    private SistemaTrafico sistemaTrafico; // NUEVO
    private double tiempoTranscurrido;
    private double distanciaRecorrida;
    private static final double VELOCIDAD_SIMULACION = 1.0;

    public Simulador() {
        this.grafo = new Grafo();
        this.tiempoTranscurrido = 0;
        this.distanciaRecorrida = 0;
    }

    public void crearMapaCiudad() {
        // Crear mapa OSM
        mapa = new MapaOSM(
                "images/mapa_asuncion.png",
                -25.2750, -25.2850,
                -57.6400, -57.6300
        );

        // Crear nodos y aristas
        crearNodosMicrocentro();
        crearAristasMicrocentro();

        // Calcular posiciones en píxeles
        for (Nodo nodo : grafo.getNodos().values()) {
            nodo.calcularPosicionPixel(mapa);
        }

        // Crear ambulancia
        Nodo base = grafo.getNodo("n0");
        if (base != null) {
            ambulancia = new Ambulancia("AMB-001", base.getX(), base.getY());
        }

        // NUEVO: Inicializar sistema de tráfico inteligente
        sistemaTrafico = new SistemaTrafico(grafo);
        if (ambulancia != null) {
            sistemaTrafico.registrarAmbulancia(ambulancia);
        }
    }

    // ... (métodos crearNodosMicrocentro y crearAristasMicrocentro igual que antes) ...

    /**
     * Actualización mejorada con sistema de tráfico inteligente.
     */
    public void actualizar() {
        double deltaTime = 0.016; // ~60 FPS

        // 1. Actualizar sistema de tráfico inteligente
        if (sistemaTrafico != null) {
            sistemaTrafico.actualizar(deltaTime);
        }

        // 2. Actualizar semáforos normales (solo los que no tienen prioridad)
        for (Nodo nodo : grafo.getNodos().values()) {
            if (!nodo.getSemaforo().isModoPrioridad()) {
                nodo.getSemaforo().actualizar();
            }
        }

        // 3. Actualizar movimiento de ambulancia
        if (ambulancia != null && ambulancia.isEnEmergencia()) {
            Nodo siguienteNodo = ambulancia.getSiguienteNodo();

            if (siguienteNodo != null) {
                double dx = siguienteNodo.getX() - ambulancia.getX();
                double dy = siguienteNodo.getY() - ambulancia.getY();
                double distancia = Math.sqrt(dx * dx + dy * dy);

                if (distancia < 5) {
                    // Llegó al nodo
                    ambulancia.setX(siguienteNodo.getX());
                    ambulancia.setY(siguienteNodo.getY());
                    ambulancia.avanzarEnRuta();

                } else {
                    // Mover hacia el nodo
                    double velocidad = ambulancia.getVelocidad() * VELOCIDAD_SIMULACION;
                    ambulancia.mover((dx / distancia) * velocidad, (dy / distancia) * velocidad);
                    distanciaRecorrida += velocidad / 100.0;
                }
            }

            tiempoTranscurrido += deltaTime;
        }
    }

    public void reiniciar() {
        tiempoTranscurrido = 0;
        distanciaRecorrida = 0;

        Nodo base = grafo.getNodo("n0");
        if (base != null && ambulancia != null) {
            ambulancia.setX(base.getX());
            ambulancia.setY(base.getY());
            ambulancia.reiniciar();
        }

        // Reiniciar todos los semáforos
        for (Nodo nodo : grafo.getNodos().values()) {
            nodo.getSemaforo().desactivarPrioridad();
        }
    }

    // Getters
    public Grafo getGrafo() { return grafo; }
    public Ambulancia getAmbulancia() { return ambulancia; }
    public MapaOSM getMapa() { return mapa; }
    public SistemaTrafico getSistemaTrafico() { return sistemaTrafico; }
    public double getTiempoTranscurrido() { return tiempoTranscurrido / 60.0; }
    public double getDistanciaRecorrida() { return distanciaRecorrida; }

    // Métodos auxiliares (copiar del código anterior)
    private void crearNodosMicrocentro() {
        grafo.agregarNodo(new Nodo("n0", "Base", -25.2821, -57.6351));
        grafo.agregarNodo(new Nodo("n1", "Palma y Palacio", -25.2818, -57.6365));
        grafo.agregarNodo(new Nodo("n2", "Palma y Alberdi", -25.2825, -57.6338));
        grafo.agregarNodo(new Nodo("n3", "Chile y Independencia", -25.2830, -57.6355));
        grafo.agregarNodo(new Nodo("n4", "Palma y Estrella", -25.2815, -57.6375));
        grafo.agregarNodo(new Nodo("n5", "Alberdi y Estrella", -25.2828, -57.6378));
        grafo.agregarNodo(new Nodo("n6", "Accidente", -25.2835, -57.6340));
        grafo.agregarNodo(new Nodo("n7", "Hospital", -25.2805, -57.6320));
        grafo.agregarNodo(new Nodo("n8", "Palma y 14 de Mayo", -25.2810, -57.6385));
        grafo.agregarNodo(new Nodo("n9", "Alberdi y 14 de Mayo", -25.2823, -57.6388));
        grafo.agregarNodo(new Nodo("n10", "Chile y Mariscal López", -25.2832, -57.6360));
    }

    private void crearAristasMicrocentro() {
        Nodo n0 = grafo.getNodo("n0");
        Nodo n1 = grafo.getNodo("n1");
        Nodo n2 = grafo.getNodo("n2");
        Nodo n3 = grafo.getNodo("n3");
        Nodo n4 = grafo.getNodo("n4");
        Nodo n5 = grafo.getNodo("n5");
        Nodo n6 = grafo.getNodo("n6");
        Nodo n7 = grafo.getNodo("n7");
        Nodo n8 = grafo.getNodo("n8");
        Nodo n9 = grafo.getNodo("n9");
        Nodo n10 = grafo.getNodo("n10");

        crearAristaBidireccional(n0, n1, calcularDistanciaReal(n0, n1));
        crearAristaBidireccional(n1, n4, calcularDistanciaReal(n1, n4));
        crearAristaBidireccional(n4, n8, calcularDistanciaReal(n4, n8));
        crearAristaBidireccional(n0, n2, calcularDistanciaReal(n0, n2));
        crearAristaBidireccional(n2, n5, calcularDistanciaReal(n2, n5));
        crearAristaBidireccional(n5, n9, calcularDistanciaReal(n5, n9));
        crearAristaBidireccional(n0, n3, calcularDistanciaReal(n0, n3));
        crearAristaBidireccional(n3, n6, calcularDistanciaReal(n3, n6));
        crearAristaBidireccional(n3, n10, calcularDistanciaReal(n3, n10));
        crearAristaBidireccional(n1, n2, calcularDistanciaReal(n1, n2));
        crearAristaBidireccional(n2, n3, calcularDistanciaReal(n2, n3));
        crearAristaBidireccional(n4, n5, calcularDistanciaReal(n4, n5));
        crearAristaBidireccional(n5, n6, calcularDistanciaReal(n5, n6));
        crearAristaBidireccional(n8, n9, calcularDistanciaReal(n8, n9));
        crearAristaBidireccional(n9, n10, calcularDistanciaReal(n9, n10));
        crearAristaBidireccional(n2, n7, calcularDistanciaReal(n2, n7));
        crearAristaBidireccional(n7, n1, calcularDistanciaReal(n7, n1));
    }

    private double calcularDistanciaReal(Nodo a, Nodo b) {
        return MapaOSM.calcularDistanciaReal(
                a.getLatitud(), a.getLongitud(),
                b.getLatitud(), b.getLongitud()
        );
    }

    private void crearAristaBidireccional(Nodo a, Nodo b, double distancia) {
        if (a != null && b != null) {
            double distanciaMetros = distancia * 1000;
            grafo.agregarArista(new Arista(a, b, distanciaMetros));
            grafo.agregarArista(new Arista(b, a, distanciaMetros));
        }
    }
}