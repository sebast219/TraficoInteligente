// ============================================
// SistemaTrafico.java - NUEVA CLASE COORDINADORA
// Gestiona la inteligencia del sistema de tráfico
// ============================================
package com.trafico.util;

import com.trafico.model.*;
import java.util.*;

/**
 * Sistema inteligente de gestión de tráfico.
 * Coordina semáforos, detecta congestión, y optimiza rutas en tiempo real.
 */
public class SistemaTrafico {
    private Grafo grafo;
    private Map<Nodo, EstadoInterseccion> estadosIntersecciones;
    private List<Ambulancia> ambulanciasActivas;
    private static final double RADIO_DETECCION = 200; // metros
    private static final double TIEMPO_ANTICIPACION = 15.0; // segundos

    public SistemaTrafico(Grafo grafo) {
        this.grafo = grafo;
        this.estadosIntersecciones = new HashMap<>();
        this.ambulanciasActivas = new ArrayList<>();

        // Inicializar estados de intersecciones
        for (Nodo nodo : grafo.getNodos().values()) {
            estadosIntersecciones.put(nodo, new EstadoInterseccion(nodo));
        }
    }

    /**
     * Actualiza el sistema completo: detecta ambulancias, gestiona semáforos,
     * detecta congestión y recalcula rutas si es necesario.
     */
    public void actualizar(double deltaTime) {
        // 1. Actualizar estados de intersecciones
        for (EstadoInterseccion estado : estadosIntersecciones.values()) {
            estado.actualizar(deltaTime);
        }

        // 2. Detectar ambulancias cercanas a cada intersección
        for (Ambulancia ambulancia : ambulanciasActivas) {
            if (ambulancia.isEnEmergencia()) {
                gestionarPrioridadAmbulancia(ambulancia);
            }
        }

        // 3. Detectar congestión y actualizar pesos de aristas
        detectarYActualizarCongestion();

        // 4. Recalcular rutas si hay cambios significativos
        recalcularRutasSiNecesario();
    }

    /**
     * Sistema inteligente de prioridad para ambulancias.
     * Calcula cuándo llegará la ambulancia a cada intersección y activa
     * semáforos con anticipación para crear "onda verde".
     */
    private void gestionarPrioridadAmbulancia(Ambulancia ambulancia) {
        List<Nodo> ruta = ambulancia.getRutaActual();
        if (ruta == null || ruta.isEmpty()) return;

        int indiceActual = ambulancia.getIndiceRutaActual();

        // Predecir llegada a los próximos 3 nodos
        for (int i = 0; i < 3 && (indiceActual + i) < ruta.size(); i++) {
            Nodo nodoProximo = ruta.get(indiceActual + i);

            // Calcular distancia total hasta ese nodo
            double distanciaHastaNodo = calcularDistanciaEnRuta(ambulancia, indiceActual, indiceActual + i);

            // Calcular tiempo estimado de llegada (TEA)
            double tiempoLlegada = distanciaHastaNodo / ambulancia.getVelocidad();

            EstadoInterseccion estado = estadosIntersecciones.get(nodoProximo);
            if (estado != null) {
                // Activar semáforo con anticipación
                if (tiempoLlegada <= TIEMPO_ANTICIPACION) {
                    estado.activarPrioridadAmbulancia(tiempoLlegada);
                }
            }
        }
    }

    /**
     * Calcula la distancia real en píxeles entre la ambulancia y un nodo futuro en su ruta.
     */
    private double calcularDistanciaEnRuta(Ambulancia ambulancia, int indiceInicio, int indiceFin) {
        double distanciaTotal = 0;
        List<Nodo> ruta = ambulancia.getRutaActual();

        // Distancia desde posición actual hasta primer nodo
        if (indiceInicio < ruta.size()) {
            Nodo primerNodo = ruta.get(indiceInicio);
            double dx = primerNodo.getX() - ambulancia.getX();
            double dy = primerNodo.getY() - ambulancia.getY();
            distanciaTotal += Math.sqrt(dx * dx + dy * dy);
        }

        // Distancia entre nodos intermedios
        for (int i = indiceInicio; i < indiceFin && i < ruta.size() - 1; i++) {
            Nodo actual = ruta.get(i);
            Nodo siguiente = ruta.get(i + 1);
            double dx = siguiente.getX() - actual.getX();
            double dy = siguiente.getY() - actual.getY();
            distanciaTotal += Math.sqrt(dx * dx + dy * dy);
        }

        return distanciaTotal;
    }

    /**
     * Detecta congestión en tiempo real analizando el tráfico en cada arista.
     * Actualiza los pesos de las aristas según el nivel de congestión.
     */
    private void detectarYActualizarCongestion() {
        // Simular congestión aleatoria en algunas calles (puede mejorarse con datos reales)
        Random random = new Random();

        for (Arista arista : grafo.getAristas()) {
            // 10% de probabilidad de congestión en cada actualización
            if (random.nextDouble() < 0.001) { // Muy baja para que sea ocasional
                // Congestión aleatoria entre 1.0 (normal) y 2.5 (muy congestionado)
                double nuevoFactor = 1.0 + random.nextDouble() * 1.5;
                arista.setFactorTrafico(nuevoFactor);
            } else {
                // Recuperación gradual del tráfico
                double factorActual = arista.getFactorTrafico();
                if (factorActual > 1.0) {
                    arista.setFactorTrafico(Math.max(1.0, factorActual - 0.01));
                }
            }
        }
    }

    /**
     * Recalcula rutas de ambulancias si detecta cambios significativos en el tráfico.
     */
    private void recalcularRutasSiNecesario() {
        for (Ambulancia ambulancia : ambulanciasActivas) {
            if (!ambulancia.isEnEmergencia()) continue;

            Nodo destino = ambulancia.getDestino();
            if (destino == null) continue;

            // Encontrar nodo más cercano a la posición actual de la ambulancia
            Nodo nodoActual = encontrarNodoMasCercano(ambulancia.getX(), ambulancia.getY());

            if (nodoActual != null && !nodoActual.equals(destino)) {
                // Recalcular ruta desde posición actual
                List<Nodo> nuevaRuta = grafo.calcularRutaMasCorta(nodoActual, destino);

                // Comparar con ruta actual (simple heurística: comparar longitud)
                if (!nuevaRuta.isEmpty()) {
                    double pesoNuevaRuta = calcularPesoRuta(nuevaRuta);
                    double pesoRutaActual = calcularPesoRuta(ambulancia.getRutaActual());

                    // Si la nueva ruta es significativamente mejor (>15%), cambiar
                    if (pesoNuevaRuta < pesoRutaActual * 0.85) {
                        ambulancia.actualizarRuta(nuevaRuta);
                        System.out.println("🔄 Ruta recalculada para " + ambulancia.getId() +
                                " (mejora: " + String.format("%.1f%%",
                                (1 - pesoNuevaRuta/pesoRutaActual) * 100) + ")");
                    }
                }
            }
        }
    }

    /**
     * Encuentra el nodo más cercano a una posición dada.
     */
    private Nodo encontrarNodoMasCercano(double x, double y) {
        Nodo masCercano = null;
        double menorDistancia = Double.MAX_VALUE;

        for (Nodo nodo : grafo.getNodos().values()) {
            double dx = nodo.getX() - x;
            double dy = nodo.getY() - y;
            double distancia = Math.sqrt(dx * dx + dy * dy);

            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                masCercano = nodo;
            }
        }

        return masCercano;
    }

    /**
     * Calcula el peso total de una ruta sumando los pesos de sus aristas.
     */
    private double calcularPesoRuta(List<Nodo> ruta) {
        if (ruta == null || ruta.size() < 2) return Double.MAX_VALUE;

        double pesoTotal = 0;
        for (int i = 0; i < ruta.size() - 1; i++) {
            Nodo actual = ruta.get(i);
            Nodo siguiente = ruta.get(i + 1);

            // Buscar arista entre estos nodos
            for (Arista arista : actual.getAristasAdyacentes()) {
                if (arista.getDestino().equals(siguiente)) {
                    pesoTotal += arista.getPeso();
                    break;
                }
            }
        }

        return pesoTotal;
    }

    /**
     * Registra una ambulancia en el sistema para que sea gestionada.
     */
    public void registrarAmbulancia(Ambulancia ambulancia) {
        if (!ambulanciasActivas.contains(ambulancia)) {
            ambulanciasActivas.add(ambulancia);
        }
    }

    /**
     * Desregistra una ambulancia del sistema.
     */
    public void desregistrarAmbulancia(Ambulancia ambulancia) {
        ambulanciasActivas.remove(ambulancia);
    }

    /**
     * Obtiene estadísticas del sistema.
     */
    public EstadisticasSistema getEstadisticas() {
        int semaforosEnVerde = 0;
        int semaforosConPrioridad = 0;
        double congestionPromedio = 0;

        for (EstadoInterseccion estado : estadosIntersecciones.values()) {
            if (estado.getNodo().getSemaforo().getEstadoActual() == Semaforo.Estado.VERDE) {
                semaforosEnVerde++;
            }
            if (estado.getNodo().getSemaforo().isModoPrioridad()) {
                semaforosConPrioridad++;
            }
        }

        for (Arista arista : grafo.getAristas()) {
            congestionPromedio += arista.getFactorTrafico();
        }
        congestionPromedio /= grafo.getAristas().size();

        return new EstadisticasSistema(
                ambulanciasActivas.size(),
                semaforosEnVerde,
                semaforosConPrioridad,
                congestionPromedio
        );
    }

    // ============================================
    // Clase interna: Estado de Intersección
    // ============================================
    private static class EstadoInterseccion {
        private Nodo nodo;
        private boolean ambulanciaCercana;
        private double tiempoHastaAmbulancia;
        private long ultimaActualizacion;

        public EstadoInterseccion(Nodo nodo) {
            this.nodo = nodo;
            this.ambulanciaCercana = false;
            this.tiempoHastaAmbulancia = Double.MAX_VALUE;
            this.ultimaActualizacion = System.currentTimeMillis();
        }

        public void actualizar(double deltaTime) {
            // Si hay ambulancia cercana, reducir tiempo hasta llegada
            if (ambulanciaCercana) {
                tiempoHastaAmbulancia -= deltaTime;

                // Si la ambulancia ya debería haber pasado, desactivar prioridad
                if (tiempoHastaAmbulancia <= 0) {
                    nodo.getSemaforo().desactivarPrioridad();
                    ambulanciaCercana = false;
                    tiempoHastaAmbulancia = Double.MAX_VALUE;
                }
            }
        }

        public void activarPrioridadAmbulancia(double tiempoLlegada) {
            this.ambulanciaCercana = true;
            this.tiempoHastaAmbulancia = tiempoLlegada;
            nodo.getSemaforo().activarPrioridad();
        }

        public Nodo getNodo() { return nodo; }
    }

    // ============================================
    // Clase de estadísticas
    // ============================================
    public static class EstadisticasSistema {
        public final int ambulanciasActivas;
        public final int semaforosEnVerde;
        public final int semaforosConPrioridad;
        public final double congestionPromedio;

        public EstadisticasSistema(int ambulancias, int verdes, int prioridad, double congestion) {
            this.ambulanciasActivas = ambulancias;
            this.semaforosEnVerde = verdes;
            this.semaforosConPrioridad = prioridad;
            this.congestionPromedio = congestion;
        }
    }
}