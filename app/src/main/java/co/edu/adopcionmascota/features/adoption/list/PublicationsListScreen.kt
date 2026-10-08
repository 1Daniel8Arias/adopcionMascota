package co.edu.adopcionmascota.features.adoption.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.adopcionmascota.domain.Publications
import com.google.firebase.annotations.concurrent.Background


@Composable
fun PublicationsListScreen(
    onNavigationToPublicationDetail: (String)-> Unit,

    padding: PaddingValues= WindowInsets.systemBars.asPaddingValues(),
    publicationsViewModel: PublicationListViewModel= viewModel()
){

    val publications by publicationsViewModel.publications.collectAsState(initial = emptyList())

    Scaffold(
        containerColor = Color(0xFFF7F9FC),

        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = Color(0xFF151C2B),
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .width(112.dp)
                    .height(52.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.AddCircleOutline,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = "Publicar",
                    fontWeight = FontWeight.SemiBold
                )
            }
        },

        bottomBar = {
           // BottomNavigationBar()
        }

    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),

            contentPadding = PaddingValues(
                start = 14.dp,
                end = 14.dp,
                top = 10.dp,
                bottom = 100.dp
            ),

            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

        }
    }

}

