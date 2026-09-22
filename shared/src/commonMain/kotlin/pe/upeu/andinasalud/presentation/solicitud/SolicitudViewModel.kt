package pe.upeu.andinasalud.presentation.solicitud

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.CatalogoCitas
import pe.upeu.andinasalud.domain.usecase.*

data class FormularioSolicitud(val especialidad:String="", val sedeId:String="", val fecha:String="", val hora:String="", val motivo:String="")
data class SolicitudUiState(
    val cargando:Boolean=true, val catalogo:CatalogoCitas?=null, val formulario:FormularioSolicitud=FormularioSolicitud(),
    val errores:ErroresSolicitud=ErroresSolicitud(), val enviando:Boolean=false, val mensaje:String?=null, val citaCreadaId:Long?=null
)

class SolicitudViewModel(private val obtenerCatalogo: ObtenerCatalogoUseCase, private val solicitar: SolicitarCitaUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(SolicitudUiState())
    val uiState: StateFlow<SolicitudUiState> = _uiState.asStateFlow()
    init { cargarCatalogo() }
    fun cargarCatalogo() = viewModelScope.launch { obtenerCatalogo().fold({ _uiState.update { s -> s.copy(cargando=false, catalogo=it) } }, { _uiState.update { s -> s.copy(cargando=false, mensaje="No se pudo cargar el formulario") } }) }
    fun especialidad(v:String)=editar { copy(especialidad=v) }
    fun sede(v:String)=editar { copy(sedeId=v) }
    fun fecha(v:String)=editar { copy(fecha=v) }
    fun hora(v:String)=editar { copy(hora=v) }
    fun motivo(v:String)=editar { copy(motivo=v) }
    private fun editar(cambio: FormularioSolicitud.() -> FormularioSolicitud) { _uiState.update { it.copy(formulario = it.formulario.cambio(), errores=ErroresSolicitud(), mensaje=null) } }
    fun registrar() = viewModelScope.launch {
        val f = _uiState.value.formulario
        _uiState.update { it.copy(enviando=true, errores=ErroresSolicitud(), mensaje=null) }
        solicitar(f.especialidad, f.sedeId, f.fecha, f.hora, f.motivo).fold(
            onSuccess = { c -> _uiState.update { it.copy(enviando=false, mensaje="Cita solicitada correctamente", citaCreadaId=c.id, formulario=FormularioSolicitud()) } },
            onFailure = { e ->
                val errores = (e as? SolicitudInvalidaException)?.errores ?: ErroresSolicitud(general=e.message ?: "No se pudo solicitar la cita")
                _uiState.update { it.copy(enviando=false, errores=errores, mensaje=errores.general) }
            }
        )
    }
    fun consumirNavegacion() { _uiState.update { it.copy(citaCreadaId=null) } }
}
