package pe.upeu.andinasalud.presentation.solicitud

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.presentation.components.EstadoCarga

@Composable
fun SolicitudScreen(viewModel: SolicitudViewModel, onCitaCreada:(Long)->Unit) {
    val s by viewModel.uiState.collectAsState()
    LaunchedEffect(s.citaCreadaId) { s.citaCreadaId?.let { onCitaCreada(it); viewModel.consumirNavegacion() } }
    if (s.cargando) { EstadoCarga("Cargando opciones…"); return }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Solicitar cita", style = MaterialTheme.typography.headlineSmall)
        SelectorTexto("Especialidad", s.formulario.especialidad, s.catalogo?.especialidades.orEmpty(), viewModel::especialidad, s.errores.especialidad)
        SelectorPar("Sede", s.formulario.sedeId, s.catalogo?.sedes?.map { it.id to it.nombre }.orEmpty(), viewModel::sede, s.errores.sede)
        Campo("Fecha (AAAA-MM-DD)", s.formulario.fecha, viewModel::fecha, s.errores.fecha)
        Campo("Hora (HH:mm)", s.formulario.hora, viewModel::hora, s.errores.hora)
        OutlinedTextField(value=s.formulario.motivo, onValueChange=viewModel::motivo, modifier=Modifier.fillMaxWidth(), label={Text("Motivo")}, minLines=3, isError=s.errores.motivo!=null, supportingText={ s.errores.motivo?.let { Text(it) } ?: Text("Entre 10 y 200 caracteres") })
        s.mensaje?.let { Text(it, color = if (s.errores.general != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary) }
        Button(onClick=viewModel::registrar, enabled=!s.enviando, modifier=Modifier.fillMaxWidth()) { Text(if(s.enviando) "Enviando…" else "Solicitar cita") }
    }
}

@Composable private fun Campo(label:String, value:String, on:(String)->Unit, error:String?) {
    OutlinedTextField(value=value, onValueChange=on, modifier=Modifier.fillMaxWidth(), label={Text(label)}, singleLine=true, isError=error!=null, supportingText={ error?.let { Text(it) } })
}

@Composable private fun SelectorTexto(label:String, selected:String, opciones:List<String>, on:(String)->Unit, error:String?) {
    var abierto by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxWidth()) {
        OutlinedButton(onClick={abierto=true}, modifier=Modifier.fillMaxWidth()) { Text(if(selected.isBlank()) label else selected) }
        DropdownMenu(expanded=abierto, onDismissRequest={abierto=false}) { opciones.forEach { op -> DropdownMenuItem(text={Text(op)}, onClick={on(op); abierto=false}) } }
    }
    error?.let { Text(it, color=MaterialTheme.colorScheme.error, style=MaterialTheme.typography.bodySmall) }
}

@Composable private fun SelectorPar(label:String, selected:String, opciones:List<Pair<String,String>>, on:(String)->Unit, error:String?) {
    var abierto by remember { mutableStateOf(false) }
    val texto = opciones.firstOrNull { it.first==selected }?.second ?: label
    Box(Modifier.fillMaxWidth()) {
        OutlinedButton(onClick={abierto=true}, modifier=Modifier.fillMaxWidth()) { Text(texto) }
        DropdownMenu(expanded=abierto, onDismissRequest={abierto=false}) { opciones.forEach { op -> DropdownMenuItem(text={Text(op.second)}, onClick={on(op.first); abierto=false}) } }
    }
    error?.let { Text(it, color=MaterialTheme.colorScheme.error, style=MaterialTheme.typography.bodySmall) }
}
