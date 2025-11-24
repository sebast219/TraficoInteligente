package com.trafico.model;

import java.util.*;

/**
 * Contiene todos los nodos y aristas.
 * Implementa el algoritmo de Dijkstra para calcular rutas más cortas.
 */
public class Grafo {
    private Map<String, Nodo> nodos;
    private List<Arista> aristas;
    
    public Grafo() {
        this.nodos = new HashMap<>();
        this.aristas = new ArrayList<>();
    }
    
    public void agregarNodo(Nodo nodo) {
        nodos.put(nodo.getId(), nodo);
    }
    
    public void agregarArista(Arista arista) {
        aristas.add(arista);
        arista.getOrigen().agregarArista(arista);
    }
    
    public Nodo getNodo(String id) {
        return nodos.get(id);
    }
    
    // Implementación de Dijkstra
    public List<Nodo> calcularRutaMasCorta(Nodo origen, Nodo destino) {
        Map<Nodo, Double> distancias = new HashMap<>();
        Map<Nodo, Nodo> predecesores = new HashMap<>();
        PriorityQueue<NodoDistancia> colaPrioridad = new PriorityQueue<>();
        Set<Nodo> visitados = new HashSet<>();
        
        // Inicializar distancias
        for (Nodo nodo : nodos.values()) {
            distancias.put(nodo, Double.POSITIVE_INFINITY);
        }
        distancias.put(origen, 0.0);
        colaPrioridad.add(new NodoDistancia(origen, 0.0));
        
        // Algoritmo de Dijkstra
        while (!colaPrioridad.isEmpty()) {
            NodoDistancia actual = colaPrioridad.poll();
            Nodo nodoActual = actual.nodo;
            
            if (visitados.contains(nodoActual)) continue;
            visitados.add(nodoActual);
            
            if (nodoActual.equals(destino)) break;
            
            // Explorar vecinos
            for (Arista arista : nodoActual.getAristasAdyacentes()) {
                Nodo vecino = arista.getDestino();
                double nuevaDistancia = distancias.get(nodoActual) + arista.getPeso();
                
                if (nuevaDistancia < distancias.get(vecino)) {
                    distancias.put(vecino, nuevaDistancia);
                    predecesores.put(vecino, nodoActual);
                    colaPrioridad.add(new NodoDistancia(vecino, nuevaDistancia));
                }
            }
        }
        
        // Reconstruir ruta
        return reconstruirRuta(predecesores, origen, destino);
    }
    
    private List<Nodo> reconstruirRuta(Map<Nodo, Nodo> predecesores, Nodo origen, Nodo destino) {
        LinkedList<Nodo> ruta = new LinkedList<>();
        Nodo actual = destino;
        
        while (actual != null) {
            ruta.addFirst(actual);
            actual = predecesores.get(actual);
        }
        
        if (ruta.isEmpty() || !ruta.getFirst().equals(origen)) {
            return new ArrayList<>();
        }
        return ruta;
    }
    
    // Clase auxiliar para cola de prioridad
    private static class NodoDistancia implements Comparable<NodoDistancia> {
        Nodo nodo;
        double distancia;
        
        NodoDistancia(Nodo nodo, double distancia) {
            this.nodo = nodo;
            this.distancia = distancia;
        }
        
        @Override
        public int compareTo(NodoDistancia otro) {
            return Double.compare(this.distancia, otro.distancia);
        }
    }
    
    // Getters
    public Map<String, Nodo> getNodos() { return nodos; }
    public List<Arista> getAristas() { return aristas; }
}
