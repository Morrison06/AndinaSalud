package pe.upeu.andinasalud.domain.model

data class Cita(
    val id: Long,
    val pacienteId: String,
    val especialidad: String,
    val medico: Medico,
    val sede: Sede,
    val fecha: Fecha,
    val hora: Hora,
    val motivo: String,
    val estado: EstadoCita
) {
    val fechaHora: FechaHora get() = FechaHora(fecha, hora)
}
