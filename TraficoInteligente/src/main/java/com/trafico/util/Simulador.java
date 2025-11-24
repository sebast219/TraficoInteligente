// ============================================
// Simulador.java - CON POSICIONES ALEATORIAS
// ============================================
package com.trafico.util;

import com.trafico.model.*;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

public class Simulador {
    private Grafo grafo;
    private Ambulancia ambulancia;
    private MapaOSM mapa;
    private SistemaTrafico sistemaTrafico;
    private double tiempoTranscurrido;
    private double distanciaRecorrida;
    private static final double VELOCIDAD_SIMULACION = 1.0;

    // Nodos especiales
    private Nodo nodoHospital; // Hospital tiene ubicación FIJA
    private Nodo nodoAccidenteActual; // Ubicación aleatoria del accidente
    private Nodo nodoPosicionInicialAmbulancia; // Ubicación aleatoria inicial

    private Random random;

    public Simulador() {
        this.grafo = new Grafo();
        this.tiempoTranscurrido = 0;
        this.distanciaRecorrida = 0;
        this.random = new Random();
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

        // Definir hospital FIJO (siempre el mismo)
        nodoHospital = grafo.getNodo("n7");
        System.out.println("🏥 Hospital fijo establecido en: " + nodoHospital.getNombre());

        // Seleccionar posición inicial ALEATORIA para ambulancia
        nodoPosicionInicialAmbulancia = seleccionarNodoAleatorio();
        System.out.println("🚑 Ambulancia posicionada en: " + nodoPosicionInicialAmbulancia.getNombre());

        // Crear ambulancia en posición aleatoria
        ambulancia = new Ambulancia("AMB-001",
                nodoPosicionInicialAmbulancia.getX(),
                nodoPosicionInicialAmbulancia.getY());

        // Inicializar sistema de tráfico inteligente
        sistemaTrafico = new SistemaTrafico(grafo);
        sistemaTrafico.registrarAmbulancia(ambulancia);
    }

    /**
     * Selecciona un nodo aleatorio del grafo (excluyendo el hospital).
     */
    private Nodo seleccionarNodoAleatorio() {
        List<Nodo> nodosDisponibles = new ArrayList<>(grafo.getNodos().values());

        // Excluir el hospital de opciones para posición inicial y accidente
        nodosDisponibles.removeIf(n -> n.getId().equals("n7")); // n7 es el hospital

        if (nodosDisponibles.isEmpty()) {
            return grafo.getNodo("n0"); // Fallback
        }

        int indiceAleatorio = random.nextInt(nodosDisponibles.size());
        return nodosDisponibles.get(indiceAleatorio);
    }

    /**
     * Genera un nuevo accidente en ubicación aleatoria y calcula ruta.
     */
    public void generarNuevaEmergencia() {
        // 1. Seleccionar ubicación ALEATORIA del accidente
        nodoAccidenteActual = seleccionarNodoAleatorio();

        // Asegurar que accidente no sea en la misma posición de la ambulancia
        while (nodoAccidenteActual.getId().equals(nodoPosicionInicialAmbulancia.getId())) {
            nodoAccidenteActual = seleccionarNodoAleatorio();
        }

        System.out.println("\n🆘 Nuevo accidente generado en: " + nodoAccidenteActual.getNombre());

        // 2. Calcular ruta: Posición actual ambulancia → Accidente
        List<Nodo> rutaAlAccidente = grafo.calcularRutaMasCorta(
                nodoPosicionInicialAmbulancia,
                nodoAccidenteActual
        );

        if (!rutaAlAccidente.isEmpty()) {
            // 3. Iniciar emergencia completa (con hospital como destino final)
            ambulancia.iniciarEmergencia(
                    nodoPosicionInicialAmbulancia,
                    nodoAccidenteActual,
                    nodoHospital,
                    rutaAlAccidente
            );
        } else {
            System.err.println("❌ No se pudo encontrar ruta al accidente");
        }
    }

    /**
     * Actualización mejorada que maneja las dos etapas de la emergencia.
     */
    public void actualizar() {
        double deltaTime = 0.016; // ~60 FPS

        // 1. Actualizar sistema de tráfico inteligente
        if (sistemaTrafico != null) {
            sistemaTrafico.actualizar(deltaTime);
        }

        // 2. Actualizar semáforos
        for (Nodo nodo : grafo.getNodos().values()) {
            if (!nodo.getSemaforo().isModoPrioridad()) {
                nodo.getSemaforo().actualizar();
            }
        }

        // 3. Actualizar movimiento de ambulancia según su estado
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

        // 4. Si llegó al accidente y está esperando, iniciar ruta al hospital
        if (ambulancia != null &&
                ambulancia.getEstadoActual() == Ambulancia.EstadoEmergencia.EN_ACCIDENTE) {

            // Calcular ruta desde accidente al hospital
            List<Nodo> rutaAlHospital = grafo.calcularRutaMasCorta(
                    nodoAccidenteActual,
                    nodoHospital
            );

            if (!rutaAlHospital.isEmpty()) {
                ambulancia.iniciarRutaAlHospital(rutaAlHospital);
            }
        }
    }

    public void reiniciar() {
        tiempoTranscurrido = 0;
        distanciaRecorrida = 0;

        // Generar nueva posición inicial ALEATORIA para ambulancia
        nodoPosicionInicialAmbulancia = seleccionarNodoAleatorio();
        System.out.println("🔄 Ambulancia reposicionada en: " + nodoPosicionInicialAmbulancia.getNombre());

        if (ambulancia != null) {
            ambulancia.setX(nodoPosicionInicialAmbulancia.getX());
            ambulancia.setY(nodoPosicionInicialAmbulancia.getY());
            ambulancia.reiniciar();
        }

        // Limpiar accidente anterior
        nodoAccidenteActual = null;

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
    public Nodo getNodoHospital() { return nodoHospital; }
    public Nodo getNodoAccidenteActual() { return nodoAccidenteActual; }
    public Nodo getNodoPosicionInicialAmbulancia() { return nodoPosicionInicialAmbulancia; }

    // Métodos auxiliares de creación (mantener igual que antes)
    private void crearNodosMicrocentro() {
        grafo.agregarNodo(new Nodo("n0", "Plaza Héroes", -25.2821, -57.6351));
        grafo.agregarNodo(new Nodo("n1", "Palma y Palacio", -25.2818, -57.6365));
        grafo.agregarNodo(new Nodo("n2", "Palma y Alberdi", -25.2825, -57.6338));
        grafo.agregarNodo(new Nodo("n3", "Chile y Indep.", -25.2830, -57.6355));
        grafo.agregarNodo(new Nodo("n4", "Palma y Estrella", -25.2815, -57.6375));
        grafo.agregarNodo(new Nodo("n5", "Alberdi y Estrella", -25.2828, -57.6378));
        grafo.agregarNodo(new Nodo("n6", "Chile y Mariscal", -25.2835, -57.6340));
        grafo.agregarNodo(new Nodo("n7", "Hospital", -25.2805, -57.6320)); // FIJO
        grafo.agregarNodo(new Nodo("n8", "Palma y 14 Mayo", -25.2810, -57.6385));
        grafo.agregarNodo(new Nodo("n9", "Alberdi y 14 Mayo", -25.2823, -57.6388));
        grafo.agregarNodo(new Nodo("n10", "Estrella y Brasil", -25.2832, -57.6360));
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
        crearAristaBidireccional(n6, n10, calcularDistanciaReal(n6, n10));
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