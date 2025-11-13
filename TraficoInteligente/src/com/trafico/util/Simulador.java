package com.trafico.util;

import com.trafico.model.*;
import java.util.List;

/**
 * Crea el grafo de la ciudad (nodos, aristas, posiciones).
 * Coordina los eventos del sistema (movimiento de la ambulancia, actualización de semáforos).
 * Usa coordenadas reales del microcentro de Asunción.
 */
public class Simulador {
    private Grafo grafo;
    private Ambulancia ambulancia;
    private MapaOSM mapa;
    private double tiempoTranscurrido;
    private double distanciaRecorrida;
    private static final double VELOCIDAD_SIMULACION = 2.0;
    
    public Simulador() {
        this.grafo = new Grafo();
        this.tiempoTranscurrido = 0;
        this.distanciaRecorrida = 0;
    }
    
    /**
     * Crea el mapa OSM y el grafo del microcentro de Asunción.
     * Coordenadas del área: Plaza de los Héroes y alrededores.
     */
    public void crearMapaCiudad() {
        // Crear mapa OSM con límites del microcentro de Asunción
        // Área aproximada: desde Plaza de los Héroes hasta Hospital de Clínicas
        mapa = new MapaOSM(
            "images/mapa_asuncion.png",
            -25.2750,  // Latitud norte (esquina superior)
            -25.2850,  // Latitud sur (esquina inferior)
            -57.6400,  // Longitud oeste (esquina izquierda)
            -57.6300   // Longitud este (esquina derecha)
        );
        
        // Crear nodos con coordenadas reales del microcentro de Asunción
        crearNodosMicrocentro();
        
        // Crear aristas (calles) entre los nodos
        crearAristasMicrocentro();
        
        // Calcular posiciones en píxeles para todos los nodos
        for (Nodo nodo : grafo.getNodos().values()) {
            nodo.calcularPosicionPixel(mapa);
        }
        
        // Crear ambulancia en la base (n0)
        Nodo base = grafo.getNodo("n0");
        if (base != null) {
            ambulancia = new Ambulancia("AMB-001", base.getX(), base.getY());
        }
    }
    
    /**
     * Crea los nodos (intersecciones) del microcentro de Asunción con coordenadas reales.
     */
    private void crearNodosMicrocentro() {
        // Base de operaciones (Plaza de los Héroes)
        Nodo n0 = new Nodo("n0", "Base", -25.2821, -57.6351);
        
        // Intersecciones principales
        Nodo n1 = new Nodo("n1", "Palma y Palacio", -25.2818, -57.6365);
        Nodo n2 = new Nodo("n2", "Palma y Alberdi", -25.2825, -57.6338);
        Nodo n3 = new Nodo("n3", "Chile y Independencia", -25.2830, -57.6355);
        Nodo n4 = new Nodo("n4", "Palma y Estrella", -25.2815, -57.6375);
        Nodo n5 = new Nodo("n5", "Alberdi y Estrella", -25.2828, -57.6378);
        
        // Zona del accidente
        Nodo n6 = new Nodo("n6", "Accidente", -25.2835, -57.6340);
        
        // Hospital de Clínicas
        Nodo n7 = new Nodo("n7", "Hospital", -25.2805, -57.6320);
        
        // Otras intersecciones
        Nodo n8 = new Nodo("n8", "Palma y 14 de Mayo", -25.2810, -57.6385);
        Nodo n9 = new Nodo("n9", "Alberdi y 14 de Mayo", -25.2823, -57.6388);
        Nodo n10 = new Nodo("n10", "Chile y Mariscal López", -25.2832, -57.6360);
        
        // Agregar nodos al grafo
        grafo.agregarNodo(n0);
        grafo.agregarNodo(n1);
        grafo.agregarNodo(n2);
        grafo.agregarNodo(n3);
        grafo.agregarNodo(n4);
        grafo.agregarNodo(n5);
        grafo.agregarNodo(n6);
        grafo.agregarNodo(n7);
        grafo.agregarNodo(n8);
        grafo.agregarNodo(n9);
        grafo.agregarNodo(n10);
    }
    
    /**
     * Crea las aristas (calles) entre los nodos del microcentro.
     * Usa distancias reales calculadas con la fórmula de Haversine.
     */
    private void crearAristasMicrocentro() {
        // Obtener nodos
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
        
        // Crear aristas bidireccionales con distancias reales
        // Calle Palma (principal)
        crearAristaBidireccional(n0, n1, calcularDistanciaReal(n0, n1));
        crearAristaBidireccional(n1, n4, calcularDistanciaReal(n1, n4));
        crearAristaBidireccional(n4, n8, calcularDistanciaReal(n4, n8));
        
        // Calle Alberdi
        crearAristaBidireccional(n0, n2, calcularDistanciaReal(n0, n2));
        crearAristaBidireccional(n2, n5, calcularDistanciaReal(n2, n5));
        crearAristaBidireccional(n5, n9, calcularDistanciaReal(n5, n9));
        
        // Calle Chile
        crearAristaBidireccional(n0, n3, calcularDistanciaReal(n0, n3));
        crearAristaBidireccional(n3, n6, calcularDistanciaReal(n3, n6));
        crearAristaBidireccional(n3, n10, calcularDistanciaReal(n3, n10));
        
        // Conexiones transversales
        crearAristaBidireccional(n1, n2, calcularDistanciaReal(n1, n2));
        crearAristaBidireccional(n2, n3, calcularDistanciaReal(n2, n3));
        crearAristaBidireccional(n4, n5, calcularDistanciaReal(n4, n5));
        crearAristaBidireccional(n5, n6, calcularDistanciaReal(n5, n6));
        crearAristaBidireccional(n8, n9, calcularDistanciaReal(n8, n9));
        crearAristaBidireccional(n9, n10, calcularDistanciaReal(n9, n10));
        
        // Ruta al hospital
        crearAristaBidireccional(n2, n7, calcularDistanciaReal(n2, n7));
        crearAristaBidireccional(n7, n1, calcularDistanciaReal(n7, n1));
    }
    
    /**
     * Calcula la distancia real entre dos nodos usando la fórmula de Haversine.
     */
    private double calcularDistanciaReal(Nodo a, Nodo b) {
        return MapaOSM.calcularDistanciaReal(
            a.getLatitud(), a.getLongitud(),
            b.getLatitud(), b.getLongitud()
        );
    }
    
    /**
     * Crea una arista bidireccional entre dos nodos.
     */
    private void crearAristaBidireccional(Nodo a, Nodo b, double distancia) {
        if (a != null && b != null) {
            // Convertir distancia de km a metros para el peso
            double distanciaMetros = distancia * 1000;
            grafo.agregarArista(new Arista(a, b, distanciaMetros));
            grafo.agregarArista(new Arista(b, a, distanciaMetros));
        }
    }
    
    public void actualizar() {
        // Actualizar semáforos
        for (Nodo nodo : grafo.getNodos().values()) {
            nodo.getSemaforo().actualizar();
        }
        
        // Actualizar movimiento de ambulancia
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
                    
                    // Activar semáforo en verde
                    siguienteNodo.getSemaforo().activarPrioridad();
                    
                } else {
                    // Mover hacia el nodo
                    double velocidad = VELOCIDAD_SIMULACION;
                    ambulancia.mover((dx / distancia) * velocidad, (dy / distancia) * velocidad);
                    distanciaRecorrida += velocidad / 100.0;
                }
            }
            
            tiempoTranscurrido += 0.016; // ~60 FPS
        }
    }
    
    public void reiniciar() {
        tiempoTranscurrido = 0;
        distanciaRecorrida = 0;
        Nodo base = grafo.getNodo("n0");
        if (base != null && ambulancia != null) {
            ambulancia.setX(base.getX());
            ambulancia.setY(base.getY());
        }
        // Reiniciar semáforos
        for (Nodo nodo : grafo.getNodos().values()) {
            nodo.getSemaforo().desactivarPrioridad();
        }
    }
    
    // Getters
    public Grafo getGrafo() { return grafo; }
    public Ambulancia getAmbulancia() { return ambulancia; }
    public MapaOSM getMapa() { return mapa; }
    public double getTiempoTranscurrido() { return tiempoTranscurrido / 60.0; }
    public double getDistanciaRecorrida() { return distanciaRecorrida; }
}
