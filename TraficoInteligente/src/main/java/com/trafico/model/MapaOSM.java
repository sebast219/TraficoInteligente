package com.trafico.model;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

import java.util.Objects;

/**
 * Maneja el mapa de OpenStreetMap y la conversión entre coordenadas geográficas y píxeles.
 */
public class MapaOSM {
    private Image imagenMapa;
    private double latitudNorte;
    private double latitudSur;
    private double longitudOeste;
    private double longitudEste;
    private int anchoPixeles;
    private int altoPixeles;
    
    /**
     * Constructor que carga el mapa y define los límites geográficos.
     * 
     * @param rutaImagen Ruta relativa al recurso (ej: "images/mapa_asuncion.png")
     * @param latN Latitud norte (esquina superior)
     * @param latS Latitud sur (esquina inferior)
     * @param lonO Longitud oeste (esquina izquierda)
     * @param lonE Longitud este (esquina derecha)
     */
    public MapaOSM(String rutaImagen, 
                   double latN, double latS, 
                   double lonO, double lonE) {
        try {
            this.imagenMapa = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/" + rutaImagen)));
            this.latitudNorte = latN;
            this.latitudSur = latS;
            this.longitudOeste = lonO;
            this.longitudEste = lonE;
            this.anchoPixeles = (int) imagenMapa.getWidth();
            this.altoPixeles = (int) imagenMapa.getHeight();
        } catch (Exception e) {
            // Si no se puede cargar la imagen, usar valores por defecto
            System.err.println("No se pudo cargar el mapa: " + rutaImagen);
            System.err.println("Usando mapa virtual por defecto.");
            this.anchoPixeles = 1200;
            this.altoPixeles = 800;
        }
    }
    
    /**
     * Convierte coordenadas geográficas (lat/lon) a coordenadas de píxeles en el canvas.
     */
    public Point2D convertirLatLonAPixel(double lat, double lon) {
        double x = ((lon - longitudOeste) / (longitudEste - longitudOeste)) * anchoPixeles;
        double y = ((latitudNorte - lat) / (latitudNorte - latitudSur)) * altoPixeles;
        return new Point2D(x, y);
    }

    /**
     * Calcula la distancia real entre dos puntos geográficos usando la fórmula de Haversine.
     * @return Distancia en kilómetros
     */
    public static double calcularDistanciaReal(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371; // Radio de la Tierra en km
        
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon/2) * Math.sin(dLon/2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c; // Distancia en kilómetros
    }
    
    // Getters
    public Image getImagen() {
        return imagenMapa;
    }
    public int getAnchoPixeles() { return anchoPixeles; }
    public int getAltoPixeles() { return altoPixeles; }
}

