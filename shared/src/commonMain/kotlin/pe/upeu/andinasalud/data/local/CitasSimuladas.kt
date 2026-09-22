package pe.upeu.andinasalud.data.local

import pe.upeu.andinasalud.domain.model.*
import pe.upeu.andinasalud.domain.time.Reloj

data class DatosSemilla(
    val paciente: Paciente,
    val catalogo: CatalogoCitas,
    val citas: List<Cita>
)

object CitasSimuladas {
    fun crear(reloj: Reloj): DatosSemilla {
        val paciente = Paciente("P-0417", "Lucía Quispe Mamani", "70154823", "lucia.quispe@correo.pe", "987 654 321")
        val sedes = listOf(
            Sede("nana", "Ñaña"), Sede("chosica", "Chosica"),
            Sede("chaclacayo", "Chaclacayo"), Sede("santa-anita", "Santa Anita")
        )
        val especialidades = listOf("Medicina General", "Odontología", "Pediatría", "Nutrición", "Psicología")
        val medicos = listOf(
            Medico("M01", "Dr. Iván Rojas", "Medicina General", listOf("nana", "chosica")),
            Medico("M02", "Dra. Elena Paredes", "Medicina General", listOf("chaclacayo", "santa-anita")),
            Medico("M03", "Dra. Rosa Flores", "Odontología", listOf("chosica", "santa-anita")),
            Medico("M04", "Dr. Diego Salazar", "Odontología", listOf("nana", "chaclacayo")),
            Medico("M05", "Dra. Carla Núñez", "Pediatría", listOf("chaclacayo", "nana")),
            Medico("M06", "Dr. Marco León", "Pediatría", listOf("chosica", "santa-anita")),
            Medico("M07", "Lic. Ana Bermúdez", "Nutrición", listOf("santa-anita", "nana")),
            Medico("M08", "Lic. José Huamán", "Nutrición", listOf("chosica", "chaclacayo")),
            Medico("M09", "Ps. Luis Tapia", "Psicología", listOf("nana", "santa-anita")),
            Medico("M10", "Ps. Andrea Ríos", "Psicología", listOf("chosica", "chaclacayo"))
        )
        val hoy = reloj.ahora().fecha
        fun sede(id: String) = sedes.first { it.id == id }
        fun medico(id: String) = medicos.first { it.id == id }
        val citas = listOf(
            Cita(1, paciente.id, "Medicina General", medico("M01"), sede("nana"), hoy.masDias(2), Hora(9,0), "Dolor de cabeza recurrente", EstadoCita.Programada(true)),
            Cita(2, paciente.id, "Odontología", medico("M03"), sede("chosica"), hoy.masDias(5), Hora(16,30), "Control preventivo y limpieza dental", EstadoCita.Programada(false)),
            Cita(3, paciente.id, "Nutrición", medico("M07"), sede("santa-anita"), hoy.masDias(9), Hora(11,15), "Evaluación nutricional y plan de alimentación", EstadoCita.Programada(true)),
            Cita(4, paciente.id, "Pediatría", medico("M05"), sede("chaclacayo"), hoy.masDias(-12), Hora(8,45), "Control general", EstadoCita.Atendida("Control en tres meses")),
            Cita(5, paciente.id, "Psicología", medico("M09"), sede("nana"), hoy.masDias(-6), Hora(15,0), "Seguimiento psicológico", EstadoCita.Atendida("Continuar sesiones quincenales")),
            Cita(6, paciente.id, "Medicina General", medico("M01"), sede("chosica"), hoy.masDias(-3), Hora(10,30), "Consulta general", EstadoCita.Cancelada("Viaje del paciente", true))
        )
        return DatosSemilla(paciente, CatalogoCitas(sedes, especialidades, medicos), citas)
    }
}
