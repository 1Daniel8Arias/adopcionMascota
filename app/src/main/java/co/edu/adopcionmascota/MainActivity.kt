package co.edu.adopcionmascota

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import co.edu.adopcionmascota.core.theme.AdopcionMascotaTheme
import co.edu.adopcionmascota.features.password.PasswordScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdopcionMascotaTheme {
                PasswordScreen()
            }
        }
    }
}

