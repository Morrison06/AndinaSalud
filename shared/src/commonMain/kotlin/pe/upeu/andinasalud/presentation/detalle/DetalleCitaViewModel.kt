package pe.upeu.andinasalud.presentation.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.usecase.CancelarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerDetalleCitaUseCase

sealed interface DetalleUiState { data object Cargando: DetalleUiState; data class Contenido(val cita:Cita, val mensaje:String?=null): DetalleUiState; data class Error(val mensaje:String): DetalleUiState }

class DetalleCitaViewModel(private val obtener: ObtenerDetalleCitaUseCase, private val cancelar: CancelarCitaUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow<DetalleUiState>(DetalleUiState.Cargando)
    val uiState: StateFlow<DetalleUiState> = _uiState.asStateFlow()
    private var idActual: Long? = null
    fun cargar(id: Long) { if (idActual == id && _uiState.value is DetalleUiState.Contenido) return; idActual=id; recargar() }
    fun recargar() = viewModelScope.launch {
        val id = idActual ?: return@launch
        _uiState.value = DetalleUiState.Cargando
        obtener(id).fold({ _uiState.value = DetalleUiState.Contenido(it) }, { _uiState.value = DetalleUiState.Error(it.message ?: "No se pudo cargar la cita") })
    }
    fun cancelar() = viewModelScope.launch {
        val id = idActual ?: return@launch
        cancelar(id).fold(
            onSuccess = { obtener(id).onSuccess { _uiState.value = DetalleUiState.Contenido(it, "Cita cancelada correctamente") } },
            onFailure = { val actual = (_uiState.value as? DetalleUiState.Contenido)?.cita; if (actual != null) _uiState.value = DetalleUiState.Contenido(actual, it.message) }
        )
    }
}
