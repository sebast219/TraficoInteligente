package com.trafico.model;

import java.util.List;

public class Ambulancia extends Vehiculo {
    private List<Nodo> rutaActual;
    private int indiceRutaActual;
    private boolean enEmergencia;
    private Nodo origen;
    private Nodo destino;
    private long tiempoInicioEmergencia;
    private int rutasRecalculadas;

    public Ambulancia(String id, double x, double y) {
        super(id, x, y, 2.5); // Velocidad en píxeles por frame (ajustable)
        this.enEmergencia = false;
        this.indiceRutaActual = 0;
        this.rutasRecalculadas = 0;
    }

    @Override
    public void mover(double deltaX, double deltaY) {
        this.x += deltaX;
        this.y += deltaY;
    }

    public void iniciarEmergencia(List<Nodo> ruta, Nodo destino) {
        if (ruta == null || ruta.isEmpty()) {
            System.err.println("⚠️ No se puede iniciar emergencia: ruta vacía");
            return;
        }

        this.origen = ruta.get(0);
        this.rutaActual = ruta;
        this.destino = destino;
        this.enEmergencia = true;
        this.indiceRutaActual = 0;
        this.tiempoInicioEmergencia = System.currentTimeMillis();
        this.rutasRecalculadas = 0;

        System.out.println("🚨 Emergencia iniciada: " + id);
        System.out.println("   Origen: " + origen.getNombre());
        System.out.println("   Destino: " + destino.getNombre());
        System.out.println("   Nodos en ruta: " + ruta.size());
    }

    public Nodo getSiguienteNodo() {
        if (rutaActual != null && indiceRutaActual < rutaActual.size()) {
            return rutaActual.get(indiceRutaActual);
        }
        return null;
    }

    public void avanzarEnRuta() {
        if (indiceRutaActual < rutaActual.size() - 1) {
            indiceRutaActual++;
            System.out.println("✓ " + id + " llegó a: " +
                    rutaActual.get(indiceRutaActual - 1).getNombre());
        } else {
            finalizarEmergencia();
        }
    }

    private void finalizarEmergencia() {
        long tiempoTotal = System.currentTimeMillis() - tiempoInicioEmergencia;
        enEmergencia = false;

        System.out.println("✅ Emergencia finalizada: " + id);
        System.out.println("   Tiempo total: " + (tiempoTotal / 1000.0) + " segundos");
        System.out.println("   Rutas recalculadas: " + rutasRecalculadas);
    }

    /**
     * Actualiza la ruta actual con una nueva ruta optimizada.
     */
    public void actualizarRuta(List<Nodo> nuevaRuta) {
        if (nuevaRuta == null || nuevaRuta.isEmpty()) return;

        this.rutaActual = nuevaRuta;
        this.indiceRutaActual = 0;
        this.rutasRecalculadas++;
    }

    public void reiniciar() {
        this.rutaActual = null;
        this.destino = null;
        this.origen = null;
        this.enEmergencia = false;
        this.indiceRutaActual = 0;
        this.rutasRecalculadas = 0;
    }

    /**
     * Calcula el progreso de la emergencia (0.0 a 1.0).
     */
    public double getProgreso() {
        if (rutaActual == null || rutaActual.isEmpty()) return 0.0;
        return (double) indiceRutaActual / rutaActual.size();
    }

    // Getters adicionales
    public List<Nodo> getRutaActual() { return rutaActual; }
    public boolean isEnEmergencia() { return enEmergencia; }
    public Nodo getDestino() { return destino; }
    public Nodo getOrigen() { return origen; }
    public int getIndiceRutaActual() { return indiceRutaActual; }
    public int getRutasRecalculadas() { return rutasRecalculadas; }
    public long getTiempoEmergencia() {
        return enEmergencia ? System.currentTimeMillis() - tiempoInicioEmergencia : 0;
    }
}