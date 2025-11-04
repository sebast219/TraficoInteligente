package com.trafico.model;

/**
 * Controla el estado del semáforo (rojo, amarillo, verde).
 * Cambia a verde anticipadamente si se acerca una ambulancia.
 */
// public class Semaforo {
class Semaforo {
    // TODO: Definir enum para estados (ROJO, AMARILLO, VERDE)
    // TODO: Definir estado actual del semáforo
    // TODO: Definir tiempo de cambio de estado
    // TODO: Definir si está en modo prioridad (ambulancia cerca)
    // TODO: Implementar constructor
    // TODO: Implementar método cambiarEstado()
    // TODO: Implementar método activarPrioridad()
    // TODO: Implementar método desactivarPrioridad()
    // TODO: Implementar métodos getter y setter

    public enum Estado {
        VERDE, AMARILLO, ROJO
    }

    private Estado estadoActual;
    private boolean modoPrioridad; // Activado cuando ambulancia se acerca
    private long tiempoUltimoCambio;
    private static final long DURACION_VERDE = 30000; // 30 segundos
    private static final long DURACION_AMARILLO = 3000; // 3 segundos

    public Semaforo() {
        this.estadoActual = Estado.ROJO;
        this.modoPrioridad = false;
        this.tiempoUltimoCambio = System.currentTimeMillis();
    }

    public void activarPrioridad() {
        this.modoPrioridad = true;
        this.estadoActual = Estado.VERDE;
    }

    public void desactivarPrioridad() {
        this.modoPrioridad = false;
    }

    public void actualizar() {
        if (modoPrioridad) return; // No cambiar si hay prioridad

        long tiempoActual = System.currentTimeMillis();
        long tiempoTranscurrido = tiempoActual - tiempoUltimoCambio;

        switch (estadoActual) {
            case VERDE:
                if (tiempoTranscurrido > DURACION_VERDE) {
                    estadoActual = Estado.AMARILLO;
                    tiempoUltimoCambio = tiempoActual;
                }
                break;
            case AMARILLO:
                if (tiempoTranscurrido > DURACION_AMARILLO) {
                    estadoActual = Estado.ROJO;
                    tiempoUltimoCambio = tiempoActual;
                }
                break;
            case ROJO:
                if (tiempoTranscurrido > DURACION_VERDE) {
                    estadoActual = Estado.VERDE;
                    tiempoUltimoCambio = tiempoActual;
                }
                break;
        }
    }

    // Getters
    public Estado getEstadoActual() { return estadoActual; }
    public boolean isModoPrioridad() { return modoPrioridad; }
}

