package pe.upeu.andinasalud.domain.usecase

import pe.upeu.andinasalud.domain.model.*
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.time.Reloj

data class ErroresSolicitud(
    val especialidad: String? = null,
    val sede: String? = null,
    val fecha: String? = null,
    val hora: String? = null,
    val motivo: String? = null,
    val general: String? = null
) {
    val hayErrores get() = listOf(especialidad, sede, fecha, hora, motivo, general).any { it != null }
}

class SolicitudInvalidaException(val errores: ErroresSolicitud) : Exception("Solicitud de cita inválida")

class SolicitarCitaUseCase(
    private val repository: CitaRepository,
    private val reloj: Reloj
) {
    suspend operator fun invoke(
        especialidad: String, sedeId: String, fechaTexto: String, horaTexto: String, motivo: String
    ): Result<Cita> = runCatching {
        var errores = ErroresSolicitud(
            especialidad = if (especialidad.isBlank()) "Selecciona una especialidad" else null,
            sede = if (sedeId.isBlank()) "Selecciona una sede" else null,
            fecha = if (fechaTexto.isBlank()) "La fecha es obligatoria" else null,
            hora = if (horaTexto.isBlank()) "La hora es obligatoria" else null,
            motivo = when {
                motivo.isBlank() -> "El motivo es obligatorio"
                !ReglasCita.motivoValido(motivo) -> "El motivo debe tener entre 10 y 200 caracteres"
                else -> null
            }
        )
        val fecha = Fecha.parse(fechaTexto)
        val hora = Hora.parse(horaTexto)
        if (fechaTexto.isNotBlank() && fecha == null) errores = errores.copy(fecha = "Usa el formato AAAA-MM-DD")
        if (horaTexto.isNotBlank() && hora == null) errores = errores.copy(hora = "Usa el formato HH:mm")
        if (errores.hayErrores) throw SolicitudInvalidaException(errores)

        fecha!!; hora!!
        val momento = FechaHora(fecha, hora)
        if (!ReglasCita.esFutura(momento, reloj.ahora())) {
            throw SolicitudInvalidaException(ErroresSolicitud(fecha = "La cita debe ser posterior al momento actual"))
        }

        val citas = repository.obtenerCitas()
        if (ReglasCita.limiteProgramadasAlcanzado(citas)) {
            throw SolicitudInvalidaException(ErroresSolicitud(general = "Ya tienes 3 citas programadas. Cancela una antes de solicitar otra."))
        }
        if (ReglasCita.hayDuplicada(citas, fecha, hora)) {
            throw SolicitudInvalidaException(ErroresSolicitud(hora = "Ya tienes una cita programada en esa fecha y hora"))
        }

        val catalogo = repository.obtenerCatalogo()
        val sede = catalogo.sedes.firstOrNull { it.id == sedeId }
            ?: throw SolicitudInvalidaException(ErroresSolicitud(sede = "Selecciona una sede válida"))
        val medico = catalogo.medicos.firstOrNull { it.especialidad == especialidad && sede.id in it.sedes }
            ?: throw SolicitudInvalidaException(ErroresSolicitud(general = "No hay médico disponible para esa especialidad y sede"))
        val paciente = repository.obtenerPaciente()
        val nuevoId = (citas.maxOfOrNull { it.id } ?: 0L) + 1L

        repository.guardarCita(
            Cita(
                id = nuevoId,
                pacienteId = paciente.id,
                especialidad = especialidad.trim(),
                medico = medico,
                sede = sede,
                fecha = fecha,
                hora = hora,
                motivo = motivo.trim(),
                estado = EstadoCita.Programada(recordatorioActivo = true)
            )
        )
    }
}
