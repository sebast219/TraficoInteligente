package com.trafico.model;

/**
 * Representa una vía entre dos nodos.
 * Almacena la distancia o peso de la conexión.
 */
public class Arista {
    private final Nodo origen;
    private final Nodo destino;
    private final double tiempoPromedio;
    private double factorTrafico; // 1.0 = normal, >1.0 = congestionado
    
    public Arista(Nodo origen, Nodo destino, double distancia) {
        this.origen = origen;
        this.destino = destino;
        this.tiempoPromedio = distancia / 50.0; // Velocidad base 50 km/h
        this.factorTrafico = 1.0;
    }
    
    public double getPeso() {
        return tiempoPromedio * factorTrafico;
    }
    
    // Getters y Setters
    public Nodo getOrigen() { return origen; }
    public Nodo getDestino() { return destino; }
    public double getFactorTrafico() { return factorTrafico; }
    public void setFactorTrafico(double factor) { this.factorTrafico = factor; }
}
