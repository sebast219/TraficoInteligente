package com.trafico.model;

import java.util.List;

/**
 * Ambulancia con sistema de emergencia de dos etapas.
 * Maneja el recorrido: Posición inicial → Accidente → Hospital
 */
public class Ambulancia extends Vehiculo {
    private List<Nodo> rutaActual;
    private int indiceRutaActual;
    private boolean enEmergencia;
    private Nodo ubicacionAccidente;
    private Nodo hospital;
    private EstadoEmergencia estadoActual;
    private long tiempoInicioEmergencia;
    private long tiempoLlegadaAccidente;
    private int rutasRecalculadas;

    public enum EstadoEmergencia {
        ESPERANDO,           // Esperando llamada
        EN_RUTA_ACCIDENTE,  // Yendo hacia el accidente
        EN_ACCIDENTE,       // Llegó al accidente (recogiendo paciente)
        EN_RUTA_HOSPITAL,   // Llevando paciente al hospital
        EN_HOSPITAL,        // Llegó al hospital
        FINALIZADO          // Emergencia completada
    }

    public Ambulancia(String id, double x, double y) {
        super(id, x, y, 2.5); // Velocidad en píxeles por frame
        this.enEmergencia = false;
        this.indiceRutaActual = 0;
        this.rutasRecalculadas = 0;
        this.estadoActual = EstadoEmergencia.ESPERANDO;
    }

    @Override
    public void mover(double deltaX, double deltaY) {
        this.x += deltaX;
        this.y += deltaY;
    }

    /**
     * Inicia emergencia completa de dos etapas.
     * @param posicionInicial Nodo donde está la ambulancia
     * @param accidente Nodo donde está el accidente
     * @param hospital Nodo del hospital (destino final)
     * @param rutaInicialHaciaAccidente Ruta calculada hacia el accidente
     */
    public void iniciarEmergencia(Nodo posicionInicial, Nodo accidente, Nodo hospital, List<Nodo> rutaInicialHaciaAccidente) {
        if (rutaInicialHaciaAccidente == null || rutaInicialHaciaAccidente.isEmpty()) {
            System.err.println("⚠️ No se puede iniciar emergencia: ruta vacía");
            return;
        }

        this.ubicacionAccidente = accidente;
        this.hospital = hospital;
        this.rutaActual = rutaInicialHaciaAccidente;
        this.enEmergencia = true;
        this.indiceRutaActual = 0;
        this.estadoActual = EstadoEmergencia.EN_RUTA_ACCIDENTE;
        this.tiempoInicioEmergencia = System.currentTimeMillis();
        this.rutasRecalculadas = 0;

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🚨 EMERGENCIA INICIADA: " + id);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📍 Posición inicial: " + posicionInicial.getNombre());
        System.out.println("🆘 Ubicación accidente: " + accidente.getNombre());
        System.out.println("🏥 Hospital destino: " + hospital.getNombre());
        System.out.println("📊 Nodos en ruta al accidente: " + rutaInicialHaciaAccidente.size());
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    public Nodo getSiguienteNodo() {
        if (rutaActual != null && indiceRutaActual < rutaActual.size()) {
            return rutaActual.get(indiceRutaActual);
        }
        return null;
    }

    public void avanzarEnRuta() {
        if (rutaActual == null || rutaActual.isEmpty()) return;

        if (indiceRutaActual < rutaActual.size() - 1) {
            indiceRutaActual++;
            Nodo nodoActual = rutaActual.get(indiceRutaActual - 1);
            System.out.println("✓ " + id + " pasó por: " + nodoActual.getNombre());
        } else {
            // Llegó al final de la ruta actual
            procesarLlegadaADestino();
        }
    }

    /**
     * Procesa la llegada a un destino (accidente u hospital).
     */
    private void procesarLlegadaADestino() {
        switch (estadoActual) {
            case EN_RUTA_ACCIDENTE:
                llegarAlAccidente();
                break;

            case EN_RUTA_HOSPITAL:
                llegarAlHospital();
                break;

            default:
                break;
        }
    }

    /**
     * Ambulancia llega al accidente, simula tiempo de atención y prepara ruta al hospital.
     */
    private void llegarAlAccidente() {
        this.estadoActual = EstadoEmergencia.EN_ACCIDENTE;
        this.tiempoLlegadaAccidente = System.currentTimeMillis();
        long tiempoHastaAccidente = tiempoLlegadaAccidente - tiempoInicioEmergencia;

        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🆘 LLEGADA AL ACCIDENTE");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("⏱️  Tiempo hasta accidente: " + (tiempoHastaAccidente / 1000.0) + " segundos");
        System.out.println("🚑 Atendiendo paciente...");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
    }

    /**
     * Inicia la segunda etapa: del accidente al hospital.
     */
    public void iniciarRutaAlHospital(List<Nodo> rutaAlHospital) {
        if (rutaAlHospital == null || rutaAlHospital.isEmpty()) {
            System.err.println("⚠️ No hay ruta al hospital disponible");
            return;
        }

        this.rutaActual = rutaAlHospital;
        this.indiceRutaActual = 0;
        this.estadoActual = EstadoEmergencia.EN_RUTA_HOSPITAL;

        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🏥 INICIANDO TRASLADO AL HOSPITAL");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 Nodos en ruta al hospital: " + rutaAlHospital.size());
        System.out.println("🚑 Paciente a bordo");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
    }

    /**
     * Ambulancia llega al hospital, finaliza emergencia.
     */
    private void llegarAlHospital() {
        this.estadoActual = EstadoEmergencia.EN_HOSPITAL;
        this.enEmergencia = false;

        long tiempoTotal = System.currentTimeMillis() - tiempoInicioEmergencia;
        long tiempoTraslado = System.currentTimeMillis() - tiempoLlegadaAccidente;

        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("✅ EMERGENCIA FINALIZADA: " + id);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("⏱️  Tiempo total: " + (tiempoTotal / 1000.0) + " segundos");
        System.out.println("   ├─ Hasta accidente: " + ((tiempoLlegadaAccidente - tiempoInicioEmergencia) / 1000.0) + " seg");
        System.out.println("   └─ Traslado a hospital: " + (tiempoTraslado / 1000.0) + " seg");
        System.out.println("🔄 Rutas recalculadas: " + rutasRecalculadas);
        System.out.println("✅ Paciente entregado en hospital");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        this.estadoActual = EstadoEmergencia.FINALIZADO;
    }

    /**
     * Actualiza la ruta actual con una nueva ruta optimizada.
     */
    public void actualizarRuta(List<Nodo> nuevaRuta) {
        if (nuevaRuta == null || nuevaRuta.isEmpty()) return;

        this.rutaActual = nuevaRuta;
        this.indiceRutaActual = 0;
        this.rutasRecalculadas++;
    }

    public void reiniciar() {
        this.rutaActual = null;
        this.ubicacionAccidente = null;
        this.hospital = null;
        this.enEmergencia = false;
        this.estadoActual = EstadoEmergencia.ESPERANDO;
        this.indiceRutaActual = 0;
        this.rutasRecalculadas = 0;
        this.tiempoInicioEmergencia = 0;
        this.tiempoLlegadaAccidente = 0;
    }

    /**
     * Calcula el progreso de la emergencia (0.0 a 1.0).
     */
    public double getProgreso() {
        if (rutaActual == null || rutaActual.isEmpty()) return 0.0;
        return (double) indiceRutaActual / rutaActual.size();
    }

    /**
     * Retorna descripción detallada del estado actual.
     */
    public String getDescripcionEstado() {
        return switch (estadoActual) {
            case ESPERANDO -> "⏳ Esperando llamada de emergencia";
            case EN_RUTA_ACCIDENTE -> "🚨 En ruta hacia el accidente";
            case EN_ACCIDENTE -> "🆘 Atendiendo en el lugar del accidente";
            case EN_RUTA_HOSPITAL -> "🏥 Trasladando paciente al hospital";
            case EN_HOSPITAL -> "✅ Paciente entregado en hospital";
            case FINALIZADO -> "✓ Emergencia completada";
        };
    }

    /**
     * Retorna si está transportando paciente (para mostrar icono).
     */
    public boolean tieneParticipante() {
        return estadoActual == EstadoEmergencia.EN_RUTA_HOSPITAL ||
                estadoActual == EstadoEmergencia.EN_ACCIDENTE;
    }

    // ============================================
    // GETTERS COMPLETOS
    // ============================================

    public List<Nodo> getRutaActual() {
        return rutaActual;
    }

    public boolean isEnEmergencia() {
        return enEmergencia;
    }
    /**
     * Retorna el destino actual según el estado.
     * - Si va al accidente: retorna ubicacionAccidente
     * - Si va al hospital: retorna hospital
     * - En otros casos: retorna null
     */
    public Nodo getDestino() {
        return switch (estadoActual) {
            case EN_RUTA_ACCIDENTE -> ubicacionAccidente;
            case EN_RUTA_HOSPITAL, EN_ACCIDENTE -> hospital;
            default -> null;
        };
    }

    public EstadoEmergencia getEstadoActual() {
        return estadoActual;
    }

    public int getIndiceRutaActual() {
        return indiceRutaActual;
    }

    public int getRutasRecalculadas() {
        return rutasRecalculadas;
    }

    public long getTiempoEmergencia() {
        return enEmergencia ? System.currentTimeMillis() - tiempoInicioEmergencia : 0;
    }
}