package pe.upeu.andinasalud.presentation.detalle

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.model.ModalidadAtencion
import pe.upeu.andinasalud.presentation.components.*

@Composable
fun DetalleCitaScreen(
    id: Long,
    viewModel: DetalleCitaViewModel
) {

    LaunchedEffect(id) {
        viewModel.cargar(id)
    }

    val state by viewModel.uiState.collectAsState()

    var confirmar by remember {
        mutableStateOf(false)
    }

    when (val s = state) {

        DetalleUiState.Cargando -> {
            EstadoCarga("Cargando detalle…")
        }

        is DetalleUiState.Error -> {
            EstadoError(
                s.mensaje,
                viewModel::recargar
            )
        }

        is DetalleUiState.Contenido -> {

            val cita = s.cita

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                Text(
                    text = cita.especialidad,
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Médico: ${cita.medico.nombre}"
                )

                Text(
                    text = "Sede: ${cita.sede.nombre}"
                )

                Text(
                    text = "Fecha: ${cita.fecha.formatoCorto()}"
                )

                Text(
                    text = "Hora: ${cita.hora.formato()}"
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                            when (cita.modalidad) {

                                ModalidadAtencion.Presencial ->
                                    Icons.Default.LocationOn

                                ModalidadAtencion.Teleconsulta ->
                                    Icons.Default.Videocam
                            },
                        contentDescription = cita.modalidad.etiqueta()
                    )

                    Text(
                        text = "Modalidad: ${cita.modalidad.etiqueta()}"
                    )
                }

                Text(
                    text = "Estado: ${cita.estado.etiqueta()}"
                )

                Text(
                    text = "Motivo: ${cita.motivo}"
                )

                when (val estado = cita.estado) {

                    is EstadoCita.Atendida -> {

                        Text(
                            text = "Indicaciones: ${estado.indicaciones}"
                        )
                    }

                    is EstadoCita.Cancelada -> {

                        Text(
                            text = "Motivo de cancelación: ${estado.motivo}"
                        )
                    }

                    is EstadoCita.Programada -> {

                        Text(
                            text =
                                if (estado.recordatorioActivo) {
                                    "Recordatorio activo"
                                } else {
                                    "Recordatorio desactivado"
                                }
                        )
                    }
                }

                s.mensaje?.let {

                    AssistChip(
                        onClick = {},
                        label = {
                            Text(it)
                        }
                    )
                }

                if (cita.estado is EstadoCita.Programada) {

                    Button(
                        onClick = {
                            confirmar = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                                MaterialTheme.colorScheme.error
                        )
                    ) {

                        Text(
                            text = "Cancelar cita"
                        )
                    }
                }
            }
        }
    }

    if (confirmar) {

        AlertDialog(
            onDismissRequest = {
                confirmar = false
            },

            title = {
                Text("Cancelar cita")
            },

            text = {
                Text(
                    "¿Confirmas que deseas cancelar esta cita?"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        confirmar = false

                        viewModel.cancelar()
                    }
                ) {

                    Text("Sí, cancelar")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        confirmar = false
                    }
                ) {

                    Text("Volver")
                }
            }
        )
    }
}