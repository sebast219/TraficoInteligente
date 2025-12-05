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
        nodoHospital = grafo.getNodo("n15"); // Hospital en esquina inferior derecha
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
        nodosDisponibles.removeIf(n -> n.getId().equals("n15")); // n15 es el hospital

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

    /**
     * Crea nodos en CUADRÍCULA REGULAR (4x4 = 16 intersecciones).
     * Simulando calles organizadas como una ciudad real.
     *
     * Coordenadas geográficas distribuidas uniformemente:
     * - Latitud: desde -25.2770 hasta -25.2830 (incrementos de ~0.002)
     * - Longitud: desde -57.6380 hasta -57.6320 (incrementos de ~0.002)
     */
    private void crearNodosMicrocentro() {
        // Definir límites de la cuadrícula
        double latNorte = -25.2770;
        double latSur = -25.2830;
        double lonOeste = -57.6380;
        double lonEste = -57.6320;

        // Calcular incrementos para cuadrícula 4x4
        int filas = 4;
        int columnas = 4;
        double incrementoLat = (latSur - latNorte) / (filas - 1);
        double incrementoLon = (lonEste - lonOeste) / (columnas - 1);

        // Crear cuadrícula de nodos
        int idNodo = 0;

        // FILA 1 (Norte) - Avenida principal superior
        grafo.agregarNodo(new Nodo("n0", "Av. Norte 1",
                latNorte, lonOeste));
        grafo.agregarNodo(new Nodo("n1", "Av. Norte 2",
                latNorte, lonOeste + incrementoLon));
        grafo.agregarNodo(new Nodo("n2", "Av. Norte 3",
                latNorte, lonOeste + 2 * incrementoLon));
        grafo.agregarNodo(new Nodo("n3", "Av. Norte 4",
                latNorte, lonEste));

        // FILA 2 - Calle central superior
        grafo.agregarNodo(new Nodo("n4", "Calle A1",
                latNorte + incrementoLat, lonOeste));
        grafo.agregarNodo(new Nodo("n5", "Calle A2",
                latNorte + incrementoLat, lonOeste + incrementoLon));
        grafo.agregarNodo(new Nodo("n6", "Calle A3",
                latNorte + incrementoLat, lonOeste + 2 * incrementoLon));
        grafo.agregarNodo(new Nodo("n7", "Calle A4",
                latNorte + incrementoLat, lonEste));

        // FILA 3 - Calle central inferior
        grafo.agregarNodo(new Nodo("n8", "Calle B1",
                latNorte + 2 * incrementoLat, lonOeste));
        grafo.agregarNodo(new Nodo("n9", "Calle B2",
                latNorte + 2 * incrementoLat, lonOeste + incrementoLon));
        grafo.agregarNodo(new Nodo("n10", "Calle B3",
                latNorte + 2 * incrementoLat, lonOeste + 2 * incrementoLon));
        grafo.agregarNodo(new Nodo("n11", "Calle B4",
                latNorte + 2 * incrementoLat, lonEste));

        // FILA 4 (Sur) - Avenida principal inferior
        grafo.agregarNodo(new Nodo("n12", "Av. Sur 1",
                latSur, lonOeste));
        grafo.agregarNodo(new Nodo("n13", "Av. Sur 2",
                latSur, lonOeste + incrementoLon));
        grafo.agregarNodo(new Nodo("n14", "Av. Sur 3",
                latSur, lonOeste + 2 * incrementoLon));
        grafo.agregarNodo(new Nodo("n15", "Hospital Central",
                latSur, lonEste)); // HOSPITAL FIJO en esquina sur-este

        System.out.println("✅ Creada cuadrícula de " + filas + "x" + columnas + " = " + (filas * columnas) + " nodos");
    }

    /**
     * Crea aristas conectando la cuadrícula de forma bidireccional.
     * Conecta cada nodo con sus vecinos (arriba, abajo, izquierda, derecha).
     */
    private void crearAristasMicrocentro() {
        // Obtener todos los nodos organizados por filas
        Nodo[][] cuadricula = new Nodo[4][4];

        // Fila 0 (Norte)
        cuadricula[0][0] = grafo.getNodo("n0");
        cuadricula[0][1] = grafo.getNodo("n1");
        cuadricula[0][2] = grafo.getNodo("n2");
        cuadricula[0][3] = grafo.getNodo("n3");

        // Fila 1
        cuadricula[1][0] = grafo.getNodo("n4");
        cuadricula[1][1] = grafo.getNodo("n5");
        cuadricula[1][2] = grafo.getNodo("n6");
        cuadricula[1][3] = grafo.getNodo("n7");

        // Fila 2
        cuadricula[2][0] = grafo.getNodo("n8");
        cuadricula[2][1] = grafo.getNodo("n9");
        cuadricula[2][2] = grafo.getNodo("n10");
        cuadricula[2][3] = grafo.getNodo("n11");

        // Fila 3 (Sur)
        cuadricula[3][0] = grafo.getNodo("n12");
        cuadricula[3][1] = grafo.getNodo("n13");
        cuadricula[3][2] = grafo.getNodo("n14");
        cuadricula[3][3] = grafo.getNodo("n15");

        int aristasCreadas = 0;

        // Conectar nodos horizontalmente (Este-Oeste)
        for (int fila = 0; fila < 4; fila++) {
            for (int col = 0; col < 3; col++) {
                Nodo nodoActual = cuadricula[fila][col];
                Nodo nodoSiguiente = cuadricula[fila][col + 1];

                if (nodoActual != null && nodoSiguiente != null) {
                    double distancia = calcularDistanciaReal(nodoActual, nodoSiguiente);
                    crearAristaBidireccional(nodoActual, nodoSiguiente, distancia);
                    aristasCreadas++;
                }
            }
        }

        // Conectar nodos verticalmente (Norte-Sur)
        for (int col = 0; col < 4; col++) {
            for (int fila = 0; fila < 3; fila++) {
                Nodo nodoActual = cuadricula[fila][col];
                Nodo nodoSiguiente = cuadricula[fila + 1][col];

                if (nodoActual != null && nodoSiguiente != null) {
                    double distancia = calcularDistanciaReal(nodoActual, nodoSiguiente);
                    crearAristaBidireccional(nodoActual, nodoSiguiente, distancia);
                    aristasCreadas++;
                }
            }
        }

        System.out.println("✅ Creadas " + aristasCreadas + " conexiones bidireccionales (total de " + (aristasCreadas * 2) + " aristas)");
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