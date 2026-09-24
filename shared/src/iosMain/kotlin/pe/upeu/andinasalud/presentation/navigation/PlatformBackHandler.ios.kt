package pe.upeu.andinasalud.presentation.navigation

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // En iOS el retorno principal se ofrece con la flecha de la TopAppBar.
}
