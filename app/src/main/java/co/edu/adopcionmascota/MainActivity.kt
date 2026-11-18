package co.edu.adopcionmascota

import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import co.edu.adopcionmascota.core.theme.AdopcionMascotaTheme
import co.edu.adopcionmascota.features.profile.ProfileScreen
import co.edu.adopcionmascota.navigation.AppNavigation

import co.edu.adopcionmascota.features.publication.PublicationScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Código (oobCode) del enlace de recuperación de contraseña, si la app se abrió desde el correo
    private var resetPasswordCode by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        resetPasswordCode = readResetCode(intent)
        setContent {
            AdopcionMascotaTheme {
                AppNavigation(
                    resetPasswordCode = resetPasswordCode,
                    onResetPasswordHandled = { resetPasswordCode = null }
                )
            }
        }
    }

    // La app ya estaba abierta (launchMode singleTop) y llega otro enlace
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        readResetCode(intent)?.let { resetPasswordCode = it }
    }

    // Enlace de Firebase: https://<proyecto>.firebaseapp.com/__/auth/action?mode=resetPassword&oobCode=...
    private fun readResetCode(intent: Intent?): String? {
        val data = intent?.data ?: return null
        if (data.getQueryParameter("mode") != "resetPassword") return null
        return data.getQueryParameter("oobCode")
    }
}
