package com.trafico.model;

import java.util.List;

/**
 * Hereda de Vehiculo.
 * Añade comportamiento especial (prioridad semafórica).
 */
public class Ambulancia extends Vehiculo {
    private List<Nodo> rutaActual;
    private int indiceRutaActual;
    private boolean enEmergencia;
    private Nodo destino;
    
    public Ambulancia(String id, double x, double y) {
        super(id, x, y, 80.0); // Velocidad 80 km/h
        this.enEmergencia = false;
        this.indiceRutaActual = 0;
    }
    
    @Override
    public void mover(double deltaX, double deltaY) {
        this.x += deltaX;
        this.y += deltaY;
    }
    
    public void iniciarEmergencia(List<Nodo> ruta, Nodo destino) {
        this.rutaActual = ruta;
        this.destino = destino;
        this.enEmergencia = true;
        this.indiceRutaActual = 0;
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
        } else {
            enEmergencia = false;
        }
    }
    
    public void reiniciar() {
        this.rutaActual = null;
        this.destino = null;
        this.enEmergencia = false;
        this.indiceRutaActual = 0;
    }
    
    // Getters
    public List<Nodo> getRutaActual() { return rutaActual; }
    public boolean isEnEmergencia() { return enEmergencia; }
    public Nodo getDestino() { return destino; }
}
