package pe.upeu.andinasalud

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import pe.upeu.andinasalud.presentation.citas.*
import pe.upeu.andinasalud.presentation.detalle.*
import pe.upeu.andinasalud.presentation.inicio.*
import pe.upeu.andinasalud.presentation.navigation.Ruta
import pe.upeu.andinasalud.presentation.navigation.PlatformBackHandler
import pe.upeu.andinasalud.presentation.perfil.*
import pe.upeu.andinasalud.presentation.solicitud.*
import pe.upeu.andinasalud.presentation.theme.AndinaSaludTheme

private data class Destino(val ruta: Ruta, val titulo:String, val icono:ImageVector)
private val destinos = listOf(
    Destino(Ruta.Inicio, "Inicio", Icons.Default.Home),
    Destino(Ruta.Citas, "Citas", Icons.Default.CalendarMonth),
    Destino(Ruta.Perfil, "Perfil", Icons.Default.Person)
)

@Composable
fun App() = KoinContext {
    var darkTheme by rememberSaveable { mutableStateOf(false) }
    var ruta by remember { mutableStateOf<Ruta>(Ruta.Inicio) }
    var principalAnterior by remember { mutableStateOf<Ruta>(Ruta.Inicio) }

    fun navegar(nueva: Ruta) {
        if (nueva is Ruta.Inicio || nueva is Ruta.Citas || nueva is Ruta.Perfil) principalAnterior = nueva
        ruta = nueva
    }
    fun volver() { ruta = principalAnterior }
    PlatformBackHandler(enabled = ruta is Ruta.Detalle || ruta is Ruta.Solicitud) { volver() }

    AndinaSaludTheme(darkTheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(when(val r=ruta){ Ruta.Inicio->"AndinaSalud"; Ruta.Citas->"Mis citas"; Ruta.Perfil->"Perfil"; is Ruta.Detalle->"Detalle de cita"; Ruta.Solicitud->"Solicitar cita" }) },
                    navigationIcon = { if (ruta is Ruta.Detalle || ruta is Ruta.Solicitud) IconButton(onClick={volver()}) { Icon(Icons.Default.ArrowBack, "Volver") } }
                )
            },
            bottomBar = {
                if (ruta is Ruta.Inicio || ruta is Ruta.Citas || ruta is Ruta.Perfil) NavigationBar {
                    destinos.forEach { d -> NavigationBarItem(selected = ruta == d.ruta, onClick={navegar(d.ruta)}, icon={Icon(d.icono,null)}, label={Text(d.titulo)}) }
                }
            }
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding)) {
                when(val r=ruta) {
                    Ruta.Inicio -> InicioScreen(koinViewModel(), { navegar(Ruta.Citas) }, { navegar(Ruta.Solicitud) }, { navegar(Ruta.Detalle(it)) })
                    Ruta.Citas -> CitasScreen(koinViewModel(), { navegar(Ruta.Detalle(it)) }, { navegar(Ruta.Solicitud) })
                    Ruta.Perfil -> PerfilScreen(koinViewModel(), darkTheme) { darkTheme=it }
                    is Ruta.Detalle -> DetalleCitaScreen(r.citaId, koinViewModel())
                    Ruta.Solicitud -> SolicitudScreen(koinViewModel()) { navegar(Ruta.Detalle(it)) }
                }
            }
        }
    }
}
