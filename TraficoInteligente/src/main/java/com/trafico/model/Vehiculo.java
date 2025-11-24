package com.trafico.model;

/**
 * Clase abstracta base para todos los vehículos.
 * Define atributos comunes (posición, velocidad, destino).
 */
public abstract class Vehiculo {
    protected double x;
    protected double y;
    protected double velocidad;
    protected String id;
    
    public Vehiculo(String id, double x, double y, double velocidad) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.velocidad = velocidad;
    }
    
    public abstract void mover(double deltaX, double deltaY);
    
    // Getters y Setters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getVelocidad() { return velocidad; }
    public String getId() { return id; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
}
