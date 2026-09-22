package pe.upeu.andinasalud.presentation.inicio

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.presentation.components.*

@Composable
fun InicioScreen(viewModel: InicioViewModel, onMisCitas: () -> Unit, onSolicitar: () -> Unit, onDetalle: (Long) -> Unit) {
    val state by viewModel.uiState.collectAsState()
    when (val s = state) {
        InicioUiState.Cargando -> EstadoCarga("Preparando tu agenda…")
        is InicioUiState.Error -> EstadoError(s.mensaje, viewModel::cargar)
        is InicioUiState.Contenido -> Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Text("Hola, ${s.paciente.nombre.substringBefore(' ')}", style = MaterialTheme.typography.headlineMedium)
            Text("Gestiona tus citas médicas desde un solo lugar", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Próxima cita", style = MaterialTheme.typography.titleLarge)
            if (s.proximaCita != null) CitaCard(s.proximaCita, { onDetalle(s.proximaCita.id) })
            else EstadoVacio("Sin citas próximas", "Cuando solicites una cita aparecerá aquí")
            Text("Accesos rápidos", style = MaterialTheme.typography.titleLarge)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ElevatedCard(onClick = onMisCitas, modifier = Modifier.weight(1f)) { Column(Modifier.padding(16.dp)) { Icon(Icons.Default.CalendarMonth, null); Spacer(Modifier.height(8.dp)); Text("Mis citas") } }
                ElevatedCard(onClick = onSolicitar, modifier = Modifier.weight(1f)) { Column(Modifier.padding(16.dp)) { Icon(Icons.Default.EventAvailable, null); Spacer(Modifier.height(8.dp)); Text("Solicitar cita") } }
            }
        }
    }
}
