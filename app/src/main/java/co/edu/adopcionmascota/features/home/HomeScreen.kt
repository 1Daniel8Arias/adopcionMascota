package co.edu.adopcionmascota.features.home

import android.widget.Button
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import co.edu.adopcionmascota.R
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.adopcionmascota.core.component.IconoText
import co.edu.adopcionmascota.core.component.IconoTextField
import co.edu.adopcionmascota.features.login.ButtonLogin


@Composable
fun HomeScreen(onNavigateToLogin:()-> Unit){

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
       AsyncImage(
           model = ImageRequest.Builder(LocalContext.current)
               .data(R.drawable.icono)
               .crossfade(true)
               .build(),
           contentDescription = "Logo de aplicacion"
       )
        Text(
            text = "Pantalla Principal",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.align ( Alignment.CenterHorizontally )

        )
        Text(
            text = "Conectando hogares, rescatando vidas",
            fontSize = 15.sp,
            color= Color(0xFF4A5568),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        IconoText(
            texto = "Adopta, reporta y ayuda a mascotas comunitarias.",
            icono= Icons.Outlined.VolunteerActivism
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
          ButtonLog(onNavigateToLogin)

            Button(
                onClick = { },
                modifier=Modifier
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors= ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A5CDE),
                    contentColor = Color.White)){
                Text(
            text = "Crear cuenta",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
            }

        }
    }
}
@Composable
fun ButtonLog(onNavigateToLogin: () -> Unit){
    Button(
        onClick = onNavigateToLogin,
        modifier=Modifier
            .height(50.dp),
        shape = RoundedCornerShape(16.dp),
        colors= ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4A5CDE),
            contentColor = Color.White
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Iniciar sesion",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier= Modifier.width(8.dp))

        }
    }
}

