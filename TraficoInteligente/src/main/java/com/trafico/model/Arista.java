package com.trafico.model;

/**
 * Representa una vía entre dos nodos.
 * Almacena la distancia o peso de la conexión.
 */
public class Arista {
    private Nodo origen;
    private Nodo destino;
    private double distancia;
    private double tiempoPromedio;
    private double factorTrafico; // 1.0 = normal, >1.0 = congestionado
    
    public Arista(Nodo origen, Nodo destino, double distancia) {
        this.origen = origen;
        this.destino = destino;
        this.distancia = distancia;
        this.tiempoPromedio = distancia / 50.0; // Velocidad base 50 km/h
        this.factorTrafico = 1.0;
    }
    
    public double getPeso() {
        return tiempoPromedio * factorTrafico;
    }
    
    // Getters y Setters
    public Nodo getOrigen() { return origen; }
    public Nodo getDestino() { return destino; }
    public double getDistancia() { return distancia; }
    public double getTiempoPromedio() { return tiempoPromedio; }
    public double getFactorTrafico() { return factorTrafico; }
    public void setFactorTrafico(double factor) { this.factorTrafico = factor; }
}
