package co.edu.adopcionmascota.features.request

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.material3.LinearProgressIndicator
import co.edu.adopcionmascota.domain.model.AvailableTime
import co.edu.adopcionmascota.domain.model.Compatibility
import co.edu.adopcionmascota.domain.model.CompatibilityLevel
import co.edu.adopcionmascota.domain.model.CompatibilityNote
import co.edu.adopcionmascota.domain.model.HousingType
import co.edu.adopcionmascota.domain.model.OtherPets
import co.edu.adopcionmascota.domain.model.PetExperience
import co.edu.adopcionmascota.features.request.application.experienceLabel
import co.edu.adopcionmascota.features.request.application.housingLabel
import co.edu.adopcionmascota.features.request.application.otherPetsLabel
import co.edu.adopcionmascota.features.request.application.timeLabel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.core.component.AppTag
import co.edu.adopcionmascota.core.theme.AppPrimary
import co.edu.adopcionmascota.core.util.relativeTime
import co.edu.adopcionmascota.domain.model.AdoptionRequest
import co.edu.adopcionmascota.domain.model.AdoptionRequestStatus
import co.edu.adopcionmascota.domain.model.AdoptionRequestType

private val RequestGreen = Color(0xFF067647)
private val RequestRed = Color(0xFFBA1A1A)

// El autor ve las solicitudes de adopción / hogar de paso que recibió y las responde
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdoptionRequestsScreen(
    onNavigateBack: () -> Unit,
    onViewPublication: (String) -> Unit,
    viewModel: AdoptionRequestsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.infoMessage) {
        state.infoMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.resetInfoMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.requests_received), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = !state.showAnswered,
                        onClick = { viewModel.onTabChange(false) },
                        label = { Text(stringResource(R.string.requests_pending_tab, state.pending.size)) }
                    )
                    FilterChip(
                        selected = state.showAnswered,
                        onClick = { viewModel.onTabChange(true) },
                        label = { Text(stringResource(R.string.requests_answered_tab)) }
                    )
                }
            }

            if (state.isLoading) {
                item { CircularProgressIndicator() }
            } else if (state.visibleRequests.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.requests_empty),
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            }

            items(state.visibleRequests, key = { it.id }) { request ->
                RequestCard(
                    request = request,
                    onViewPublication = { onViewPublication(request.publicationId) },
                    onAccept = { viewModel.onRespond(request, accept = true) },
                    onReject = { viewModel.onRespond(request, accept = false) }
                )
            }
        }
    }
}

@Composable
private fun RequestCard(
    request: AdoptionRequest,
    onViewPublication: () -> Unit,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF2FF))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppTag(
                    text = stringResource(
                        if (request.type == AdoptionRequestType.ADOPTION) R.string.request_type_adoption
                        else R.string.request_type_foster
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(text = relativeTime(request.createdAt), fontSize = 11.sp, color = Color.Gray)
            }
            Text(text = request.requesterName, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(
                text = stringResource(R.string.request_for_pet, request.publicationName),
                fontSize = 13.sp,
                color = AppPrimary,
                modifier = Modifier.clickable(onClick = onViewPublication)
            )
            if (request.message.isNotBlank()) {
                Text(text = request.message, fontSize = 13.sp)
            }

            // Solicitud formal: resumen de compatibilidad calculado con las respuestas
            request.compatibilityScore?.let { score -> CompatibilitySummary(request, score) }

            when (request.status) {
                AdoptionRequestStatus.PENDING -> Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = onReject, modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.reject))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = RequestGreen)
                    ) {
                        Text(stringResource(R.string.accept))
                    }
                }

                AdoptionRequestStatus.ACCEPTED -> AppTag(
                    text = stringResource(R.string.request_status_accepted),
                    backgroundColor = RequestGreen.copy(alpha = 0.12f),
                    textColor = RequestGreen
                )

                AdoptionRequestStatus.REJECTED -> AppTag(
                    text = stringResource(R.string.request_status_rejected),
                    backgroundColor = RequestRed.copy(alpha = 0.12f),
                    textColor = RequestRed
                )
            }
        }
    }
}

@Composable
private fun CompatibilitySummary(request: AdoptionRequest, score: Int) {
    val level = Compatibility.levelFor(score)
    val color = when (level) {
        CompatibilityLevel.HIGH -> RequestGreen
        CompatibilityLevel.MEDIUM -> Color(0xFFB54708)
        CompatibilityLevel.LOW -> RequestRed
    }
    val levelText = stringResource(
        when (level) {
            CompatibilityLevel.HIGH -> R.string.compatibility_high
            CompatibilityLevel.MEDIUM -> R.string.compatibility_medium
            CompatibilityLevel.LOW -> R.string.compatibility_low
        }
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(R.string.compatibility_title, levelText, score),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = color
        )
        LinearProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier.fillMaxWidth(),
            color = color
        )
        val housing = HousingType.entries.find { it.name == request.housing }
        val experience = PetExperience.entries.find { it.name == request.experience }
        val time = AvailableTime.entries.find { it.name == request.availableTime }
        val pets = OtherPets.entries.find { it.name == request.otherPets }
        if (housing != null && experience != null && time != null && pets != null) {
            Text(
                text = stringResource(
                    R.string.answers_summary,
                    housingLabel(housing), experienceLabel(experience), timeLabel(time), otherPetsLabel(pets)
                ),
                fontSize = 12.sp,
                color = Color.DarkGray
            )
        }
        request.compatibilityNotes
            .mapNotNull { code -> CompatibilityNote.entries.find { it.name == code } }
            .forEach { note -> Text(text = "• " + stringResource(noteText(note)), fontSize = 12.sp) }
        if (request.aiGenerated) {
            Text(text = stringResource(R.string.compatibility_ai_letter), fontSize = 11.sp, color = Color.Gray)
        }
    }
}

private fun noteText(note: CompatibilityNote) = when (note) {
    CompatibilityNote.SPACIOUS_HOME -> R.string.note_spacious_home
    CompatibilityNote.SMALL_SPACE_LARGE_PET -> R.string.note_small_space_large_pet
    CompatibilityNote.FIRST_TIME_OWNER -> R.string.note_first_time_owner
    CompatibilityNote.EXPERIENCED_OWNER -> R.string.note_experienced_owner
    CompatibilityNote.LOW_TIME -> R.string.note_low_time
    CompatibilityNote.GOOD_TIME -> R.string.note_good_time
    CompatibilityNote.OTHER_PETS_REVIEW -> R.string.note_other_pets_review
    CompatibilityNote.NO_OTHER_PETS -> R.string.note_no_other_pets
}
