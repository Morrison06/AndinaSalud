package pe.upeu.andinasalud.presentation.citas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.*
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase

class CitasViewModel(private val obtenerCitas: ObtenerCitasUseCase) : ViewModel() {
    private var originales: List<Cita> = emptyList()
    private val _uiState = MutableStateFlow(CitasUiState())
    val uiState: StateFlow<CitasUiState> = _uiState.asStateFlow()
    init { cargar() }

    fun cargar() = viewModelScope.launch {
        _uiState.update { it.copy(fase = FaseCitas.Cargando) }
        obtenerCitas().fold(
            onSuccess = { originales = it; aplicar() },
            onFailure = { _uiState.update { s -> s.copy(fase = FaseCitas.Error(it.message ?: "No se pudieron cargar las citas")) } }
        )
    }
    fun onBusquedaChange(v: String) { _uiState.update { it.copy(busqueda = v) }; aplicar() }
    fun onFiltroChange(v: FiltroCitas) { _uiState.update { it.copy(filtro = v) }; aplicar() }

    private fun aplicar() {
        val s = _uiState.value
        val q = normalizar(s.busqueda)
        val filtradas = originales.filter { cita ->
            val estadoOk = when (s.filtro) {
                FiltroCitas.Todas -> true
                FiltroCitas.Programadas -> cita.estado is EstadoCita.Programada
                FiltroCitas.Atendidas -> cita.estado is EstadoCita.Atendida
                FiltroCitas.Canceladas -> cita.estado is EstadoCita.Cancelada
            }
            val textoOk = q.isBlank() || normalizar(cita.especialidad).contains(q) || normalizar(cita.medico.nombre).contains(q)
            estadoOk && textoOk
        }
        _uiState.value = s.copy(fase = if (filtradas.isEmpty()) FaseCitas.Vacio else FaseCitas.Contenido(filtradas))
    }

    private fun normalizar(t: String) = t.lowercase().replace("á","a").replace("é","e").replace("í","i").replace("ó","o").replace("ú","u").replace("ü","u").replace("ñ","n")
}
