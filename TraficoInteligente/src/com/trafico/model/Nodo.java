package com.trafico.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa una intersección en la ciudad.
 * Contiene un semáforo y lista de aristas conectadas.
 * Soporta coordenadas geográficas reales (lat/lon) y coordenadas de píxeles.
 */
public class Nodo {
    private String id;
    private String nombre;
    
    // Coordenadas geográficas reales (OSM)
    private double latitud;
    private double longitud;
    
    // Coordenadas en píxeles (calculadas desde lat/lon)
    private double x;
    private double y;
    
    private Semaforo semaforo;
    private List<Arista> aristasAdyacentes;
    
    /**
     * Constructor con coordenadas geográficas.
     */
    public Nodo(String id, String nombre, double latitud, double longitud) {
        this.id = id;
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.semaforo = new Semaforo();
        this.aristasAdyacentes = new ArrayList<>();
        // Las coordenadas x, y se calcularán después con calcularPosicionPixel()
    }
    
    /**
     * Constructor con coordenadas de píxeles (para compatibilidad con código existente).
     */
    public Nodo(String id, String nombre, double x, double y, boolean usarPixeles) {
        this.id = id;
        this.nombre = nombre;
        if (usarPixeles) {
            this.x = x;
            this.y = y;
            // Coordenadas geográficas no definidas
            this.latitud = 0;
            this.longitud = 0;
        } else {
            this.latitud = x;
            this.longitud = y;
        }
        this.semaforo = new Semaforo();
        this.aristasAdyacentes = new ArrayList<>();
    }
    
    /**
     * Calcula la posición en píxeles a partir de las coordenadas geográficas usando el mapa OSM.
     */
    public void calcularPosicionPixel(MapaOSM mapa) {
        if (mapa != null && latitud != 0 && longitud != 0) {
            javafx.geometry.Point2D pos = mapa.convertirLatLonAPixel(latitud, longitud);
            this.x = pos.getX();
            this.y = pos.getY();
        }
    }
    
    public void agregarArista(Arista arista) {
        aristasAdyacentes.add(arista);
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    
    public double getX() { return x; }
    public double getY() { return y; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    
    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
    
    public Semaforo getSemaforo() { return semaforo; }
    public List<Arista> getAristasAdyacentes() { return aristasAdyacentes; }
}
