package pe.upeu.andinasalud.presentation.detalle

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.presentation.components.*

@Composable
fun DetalleCitaScreen(id: Long, viewModel: DetalleCitaViewModel) {
    LaunchedEffect(id) { viewModel.cargar(id) }
    val state by viewModel.uiState.collectAsState()
    var confirmar by remember { mutableStateOf(false) }
    when (val s = state) {
        DetalleUiState.Cargando -> EstadoCarga("Cargando detalle…")
        is DetalleUiState.Error -> EstadoError(s.mensaje, viewModel::recargar)
        is DetalleUiState.Contenido -> Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(s.cita.especialidad, style = MaterialTheme.typography.headlineSmall)
            Text("Médico: ${s.cita.medico.nombre}")
            Text("Sede: ${s.cita.sede.nombre}")
            Text("Fecha: ${s.cita.fecha.formatoCorto()}")
            Text("Hora: ${s.cita.hora.formato()}")
            Text("Estado: ${s.cita.estado.etiqueta()}")
            Text("Motivo: ${s.cita.motivo}")
            when (val e = s.cita.estado) {
                is EstadoCita.Atendida -> Text("Indicaciones: ${e.indicaciones}")
                is EstadoCita.Cancelada -> Text("Motivo de cancelación: ${e.motivo}")
                is EstadoCita.Programada -> Text(if (e.recordatorioActivo) "Recordatorio activo" else "Recordatorio desactivado")
            }
            s.mensaje?.let { AssistChip(onClick = {}, label = { Text(it) }) }
            if (s.cita.estado is EstadoCita.Programada) Button(onClick = { confirmar = true }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Cancelar cita") }
        }
    }
    if (confirmar) AlertDialog(
        onDismissRequest = { confirmar=false }, title = { Text("Cancelar cita") },
        text = { Text("¿Confirmas que deseas cancelar esta cita?") },
        confirmButton = { TextButton(onClick = { confirmar=false; viewModel.cancelar() }) { Text("Sí, cancelar") } },
        dismissButton = { TextButton(onClick = { confirmar=false }) { Text("Volver") } }
    )
}
