package com.trafico.model;

/**
 * Representa una intersección en la ciudad.
 * Contiene un semáforo y lista de aristas conectadas.
 */
public class Nodo {
class Nodo {
    // TODO: Definir atributos básicos (id, nombre, posición x, posición y)
    // TODO: Agregar semáforo asociado a esta intersección
    // TODO: Agregar lista de aristas (conexiones con otros nodos)
    // TODO: Implementar constructor
    // TODO: Implementar métodos getter y setter
    // TODO: Implementar método toString para depuración

    private String id;
    private String nombre;
    private double x;
    private double y;
    private Semaforo semaforo;
    private List<Arista> aristasAdyacentes;

    public Nodo(String id, String nombre, double x, double y) {
        this.id = id;
        this.nombre = nombre;
        this.x = x;
        this.y = y;
        this.semaforo = new Semaforo();
        this.aristasAdyacentes = new ArrayList<>();
    }

    public void agregarArista(Arista arista) {
        aristasAdyacentes.add(arista);
    }

    // Getters y Setters
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public double getX() { return x; }
    public double getY() { return y; }
    public Semaforo getSemaforo() { return semaforo; }
    public List<Arista> getAristasAdyacentes() { return aristasAdyacentes; }
}

