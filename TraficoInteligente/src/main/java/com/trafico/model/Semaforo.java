package com.trafico.model;

/**
 * Semáforo inteligente con comportamiento normal y prioridad para ambulancias.
 *
 * FUNCIONAMIENTO:
 * - Ciclo normal: Verde → Amarillo → Rojo → Verde (automático)
 * - Modo prioridad: Se pone en verde cuando la ambulancia se acerca
 * - Recuperación: Vuelve al ciclo normal después de que pasa la ambulancia
 */
public class Semaforo {
    public enum Estado {
        VERDE, AMARILLO, ROJO
    }

    private Estado estadoActual;
    private Estado estadoAnteriorAPrioridad; // Para restaurar después
    private boolean modoPrioridad;
    private long tiempoUltimoCambio;
    private long tiempoPrioridadActivada;

    // Duraciones realistas (en milisegundos)
    private static final long DURACION_VERDE_NORMAL = 4000;      // 8 segundos
    private static final long DURACION_AMARILLO = 1000;          // 2 segundos
    private static final long DURACION_ROJO_NORMAL = 4000;      // 10 segundos

    // Prioridad ambulancia
    private static final long DURACION_PRIORIDAD_MAXIMA = 4000; // 15 seg máximo en verde
    private static final long TIEMPO_RECUPERACION = 1000;        // 3 seg para volver a ciclo

    // Desfase inicial para que no todos cambien al mismo tiempo
    private long desfaseInicial;

    /**
     * Constructor con desfase aleatorio para simular sincronización real.
     */
    public Semaforo() {
        // Iniciar en estado aleatorio para que no todos estén sincronizados
        double random = Math.random();
        if (random < 0.4) {
            this.estadoActual = Estado.VERDE;
        } else if (random < 0.6) {
            this.estadoActual = Estado.AMARILLO;
        } else {
            this.estadoActual = Estado.ROJO;
        }

        this.modoPrioridad = false;
        this.tiempoUltimoCambio = System.currentTimeMillis();

        // Desfase inicial aleatorio (0-5 segundos)
        this.desfaseInicial = (long) (Math.random() * 1000);
        this.tiempoUltimoCambio -= desfaseInicial;
    }

    /**
     * Activa el modo prioridad para ambulancia.
     * Cambia inmediatamente a VERDE y memoriza el estado anterior.
     */
    public void activarPrioridad() {
        if (!modoPrioridad) {
            // Guardar estado actual antes de cambiar a prioridad
            this.estadoAnteriorAPrioridad = this.estadoActual;
            this.modoPrioridad = true;
            this.tiempoPrioridadActivada = System.currentTimeMillis();

            // Cambiar inmediatamente a VERDE
            if (this.estadoActual != Estado.VERDE) {
                this.estadoActual = Estado.VERDE;
                this.tiempoUltimoCambio = System.currentTimeMillis();
                System.out.println("🚦 Semáforo: Prioridad activada → VERDE");
            }
        } else {
            // Mantener prioridad activa (renovar tiempo)
            this.tiempoPrioridadActivada = System.currentTimeMillis();
        }
    }

    /**
     * Desactiva el modo prioridad.
     * Inicia transición gradual de vuelta al ciclo normal.
     */
    public void desactivarPrioridad() {
        if (modoPrioridad) {
            this.modoPrioridad = false;

            // Transición suave: de verde a amarillo antes de volver a ciclo normal
            if (this.estadoActual == Estado.VERDE) {
                this.estadoActual = Estado.AMARILLO;
                this.tiempoUltimoCambio = System.currentTimeMillis();
                System.out.println("🚦 Semáforo: Prioridad desactivada → Transición a ciclo normal");
            }
        }
    }

    /**
     * Actualiza el estado del semáforo según su ciclo.
     * - En modo normal: Ciclo automático Verde → Amarillo → Rojo
     * - En modo prioridad: Se mantiene en verde
     */
    public void actualizar() {
        long tiempoActual = System.currentTimeMillis();
        long tiempoTranscurrido = tiempoActual - tiempoUltimoCambio;

        // Si está en modo prioridad
        if (modoPrioridad) {
            // Verificar tiempo máximo de prioridad (seguridad)
            long tiempoEnPrioridad = tiempoActual - tiempoPrioridadActivada;
            if (tiempoEnPrioridad > DURACION_PRIORIDAD_MAXIMA) {
                // Forzar desactivación si pasa mucho tiempo
                desactivarPrioridad();
            }
            return; // Mantener verde mientras haya prioridad
        }

        // Ciclo normal del semáforo
        switch (estadoActual) {
            case VERDE:
                if (tiempoTranscurrido > DURACION_VERDE_NORMAL) {
                    cambiarEstado(Estado.AMARILLO);
                }
                break;

            case AMARILLO:
                if (tiempoTranscurrido > DURACION_AMARILLO) {
                    cambiarEstado(Estado.ROJO);
                }
                break;

            case ROJO:
                if (tiempoTranscurrido > DURACION_ROJO_NORMAL) {
                    cambiarEstado(Estado.VERDE);
                }
                break;
        }
    }

    /**
     * Cambia el estado del semáforo y registra el tiempo.
     */
    private void cambiarEstado(Estado nuevoEstado) {
        this.estadoActual = nuevoEstado;
        this.tiempoUltimoCambio = System.currentTimeMillis();
    }

    /**
     * Reinicia el semáforo a su estado inicial.
     */
    public void reiniciar() {
        this.modoPrioridad = false;
        this.estadoActual = Estado.ROJO;
        this.tiempoUltimoCambio = System.currentTimeMillis();

        // Nuevo desfase aleatorio
        this.desfaseInicial = (long) (Math.random() * 5000);
        this.tiempoUltimoCambio -= desfaseInicial;
    }

    /**
     * Retorna si el semáforo permite el paso (verde).
     */
    public boolean permiteElPaso() {
        return estadoActual == Estado.VERDE;
    }

    /**
     * Retorna el tiempo restante en el estado actual (en segundos).
     */
    public double getTiempoRestante() {
        long tiempoTranscurrido = System.currentTimeMillis() - tiempoUltimoCambio;
        long duracionEstado = 0;

        switch (estadoActual) {
            case VERDE:
                duracionEstado = modoPrioridad ? DURACION_PRIORIDAD_MAXIMA : DURACION_VERDE_NORMAL;
                break;
            case AMARILLO:
                duracionEstado = DURACION_AMARILLO;
                break;
            case ROJO:
                duracionEstado = DURACION_ROJO_NORMAL;
                break;
        }

        long tiempoRestante = duracionEstado - tiempoTranscurrido;
        return Math.max(0, tiempoRestante / 1000.0);
    }

    /**
     * Retorna descripción del estado actual.
     */
    public String getDescripcion() {
        StringBuilder desc = new StringBuilder();
        desc.append(estadoActual.toString());

        if (modoPrioridad) {
            desc.append(" (PRIORIDAD)");
        }

        double tiempoRestante = getTiempoRestante();
        if (tiempoRestante > 0) {
            desc.append(String.format(" - %.1fs", tiempoRestante));
        }

        return desc.toString();
    }

    // ============================================
    // GETTERS
    // ============================================

    public Estado getEstadoActual() {
        return estadoActual;
    }

    public boolean isModoPrioridad() {
        return modoPrioridad;
    }

    public long getTiempoUltimoCambio() {
        return tiempoUltimoCambio;
    }

    public Estado getEstadoAnteriorAPrioridad() {
        return estadoAnteriorAPrioridad;
    }

    /**
     * Retorna el porcentaje de progreso en el estado actual (0.0 - 1.0).
     * Útil para animaciones.
     */
    public double getProgresoEstado() {
        long tiempoTranscurrido = System.currentTimeMillis() - tiempoUltimoCambio;
        long duracionEstado = 0;

        switch (estadoActual) {
            case VERDE:
                duracionEstado = modoPrioridad ? DURACION_PRIORIDAD_MAXIMA : DURACION_VERDE_NORMAL;
                break;
            case AMARILLO:
                duracionEstado = DURACION_AMARILLO;
                break;
            case ROJO:
                duracionEstado = DURACION_ROJO_NORMAL;
                break;
        }

        return Math.min(1.0, (double) tiempoTranscurrido / duracionEstado);
    }

    // ============================================
    // CONSTANTES PÚBLICAS (para referencia)
    // ============================================

    public static long getDuracionVerdeNormal() {
        return DURACION_VERDE_NORMAL;
    }

    public static long getDuracionAmarillo() {
        return DURACION_AMARILLO;
    }

    public static long getDuracionRojoNormal() {
        return DURACION_ROJO_NORMAL;
    }
}