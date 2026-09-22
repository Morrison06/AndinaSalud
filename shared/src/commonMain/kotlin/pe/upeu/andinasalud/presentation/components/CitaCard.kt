package pe.upeu.andinasalud.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.model.*

fun EstadoCita.etiqueta(): String = when (this) {
    is EstadoCita.Programada -> "Programada"
    is EstadoCita.Atendida -> "Atendida"
    is EstadoCita.Cancelada -> "Cancelada"
}

@Composable
fun CitaCard(cita: Cita, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ElevatedCard(modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(cita.especialidad, style = MaterialTheme.typography.titleMedium)
                AssistChip(onClick = {}, label = { Text(cita.estado.etiqueta()) })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, null, modifier = Modifier.size(18.dp))
                Text(cita.medico.nombre, style = MaterialTheme.typography.bodyMedium)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(18.dp))
                Text(cita.sede.nombre, style = MaterialTheme.typography.bodyMedium)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, null, modifier = Modifier.size(18.dp))
                Text("${cita.fecha.formatoCorto()} · ${cita.hora.formato()}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
