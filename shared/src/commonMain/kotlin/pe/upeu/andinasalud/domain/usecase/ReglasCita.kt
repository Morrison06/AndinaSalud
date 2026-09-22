package pe.upeu.andinasalud.domain.usecase

import pe.upeu.andinasalud.domain.model.*

object ReglasCita {

    const val MAX_CITAS_PROGRAMADAS = 3
    const val MOTIVO_MIN = 10
    const val MOTIVO_MAX = 200
    const val HORAS_MIN_CANCELACION = 24

    fun esFutura(
        cita: FechaHora,
        ahora: FechaHora
    ): Boolean = cita > ahora

    fun cantidadProgramadas(
        citas: List<Cita>
    ): Int =
        citas.count {
            it.estado is EstadoCita.Programada
        }

    fun limiteProgramadasAlcanzado(
        citas: List<Cita>
    ): Boolean =
        cantidadProgramadas(citas) >= MAX_CITAS_PROGRAMADAS

    fun hayDuplicada(
        citas: List<Cita>,
        fecha: Fecha,
        hora: Hora
    ): Boolean =
        citas.any {
            it.estado is EstadoCita.Programada &&
                    it.fecha == fecha &&
                    it.hora == hora
        }

    fun motivoValido(
        motivo: String
    ): Boolean =
        motivo.trim().length in MOTIVO_MIN..MOTIVO_MAX

    fun puedeCancelar(
        cita: Cita,
        ahora: FechaHora
    ): Boolean =
        cita.estado is EstadoCita.Programada &&
                cita.fechaHora.minutosDesde(ahora) >
                HORAS_MIN_CANCELACION * 60L
}