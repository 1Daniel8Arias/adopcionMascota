package co.edu.adopcionmascota.features.moderator.panel

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.domain.model.RejectionReason


@Composable
fun RejectPublicationRoute(
    viewModel: RejectPublicationViewModel = viewModel()
) {

    val state by viewModel.uiState.collectAsState()

    RejectPublicationBottomSheet(
        selectedReason = state.selectedReason,

        onReasonSelected = { reason ->
            viewModel.onEvent(
                RejectPublicationEvent.SelectReason(reason)
            )
        },

        onConfirm = {
            viewModel.onEvent(
                RejectPublicationEvent.ConfirmRejection
            )
        },
        onDismiss = {}
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RejectPublicationBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: (RejectionReason?) -> Unit,
    selectedReason: RejectionReason?,
    onReasonSelected: (RejectionReason) -> Unit,

    ) {

    var selectedReason by remember {
        mutableStateOf<RejectionReason?>(reasons.first())
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {

            HeaderSection()

            Spacer(modifier = Modifier.height(16.dp))

            PublicationPreviewCard()

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "Motivo de infracción",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Normativa Bogotá 2025",
                    color = Color(0xFF3F51B5),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.heightIn(max = 320.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(reasons) { reason ->

                    ReasonItem(
                        reason = reason,
                        selected = selectedReason?.id == reason.id,
                        onSelected = {
                            selectedReason = reason
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    onConfirm(selectedReason)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F)
                )
            ) {

                Icon(
                    imageVector = Icons.Default.Block,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Confirmar Rechazo y Notificar")
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
@Composable
private fun HeaderSection() {

    Row(
        verticalAlignment = Alignment.Top
    ) {

        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    Color(0xFFFFEBEE),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.Gavel,
                contentDescription = null,
                tint = Color.Red
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "Rechazar Publicación",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )

            Text(
                text = "Indica a Carlos M. el motivo ético del rechazo para orientarlo o respaldar las medidas de la comunidad.",
                color = Color.Gray,
                fontSize = 13.sp
            )
        }

        IconButton(onClick = {}) {

            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null
            )
        }
    }
}
@Composable
private fun PublicationPreviewCard() {

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF3F4F6)
        )
    ) {

        Row(
            modifier = Modifier.padding(10.dp)
        ) {

            Image(
                painter = painterResource(R.drawable.imagen_max),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {

                Text(
                    "Venta encubierta",
                    color = Color.Red,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Chapinero",
                    color = Color.Gray,
                    fontSize = 11.sp
                )

                Text(
                    "Bulldog Francés con pedigrí",
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    "Aporte exigido: \$500.000 COP",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
@Composable
private fun ReasonItem(
    reason: RejectionReason,
    selected: Boolean,
    onSelected: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected() },
        colors = CardDefaults.cardColors(
            containerColor = if (selected)
                Color(0xFFE8F0FE)
            else
                Color(0xFFF5F5F5)
        ),
        border = BorderStroke(
            width = if (selected) 1.dp else 0.dp,
            color = if (selected)
                Color(0xFF4F46E5)
            else
                Color.Transparent
        )
    ) {

        Row(
            modifier = Modifier.padding(14.dp)
        ) {

            RadioButton(
                selected = selected,
                onClick = onSelected
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {

                Text(
                    text = reason.title,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = reason.description,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ModeratorScreenPreview() {

    MaterialTheme {

        RejectPublicationBottomSheet(
            onDismiss = {},
            onConfirm = {},
            selectedReason = reasons.first(),
            onReasonSelected = {}
        )
    }
}
