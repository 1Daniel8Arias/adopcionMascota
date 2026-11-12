package co.edu.adopcionmascota.features.request.application

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Edit
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
import androidx.compose.material3.OutlinedTextField
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
import co.edu.adopcionmascota.core.theme.AppPrimary
import co.edu.adopcionmascota.core.util.RequestResult
import co.edu.adopcionmascota.domain.model.AvailableTime
import co.edu.adopcionmascota.domain.model.HousingType
import co.edu.adopcionmascota.domain.model.OtherPets
import co.edu.adopcionmascota.domain.model.PetExperience

// Solicitud formal de adopción: el usuario responde el cuestionario, la IA redacta la carta
// y el usuario la revisa (o la escribe a mano) antes de enviarla al autor de la publicación
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdoptionApplicationScreen(
    onNavigateBack: () -> Unit,
    onSent: (String) -> Unit,
    viewModel: AdoptionApplicationViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.sendResult) {
        when (val result = state.sendResult) {
            is RequestResult.Success -> { viewModel.resetSendResult(); onSent(result.message) }
            is RequestResult.Failure -> { snackbarHostState.showSnackbar(result.errorMessage); viewModel.resetSendResult() }
            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.application_title, state.petName), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { if (state.showLetterEditor) viewModel.onBackToQuestions() else onNavigateBack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!state.showLetterEditor) {
                QuestionsStep(state, viewModel)
            } else {
                LetterStep(state, viewModel)
            }
        }
    }
}

@Composable
private fun QuestionsStep(state: AdoptionApplicationUiState, viewModel: AdoptionApplicationViewModel) {
    Text(stringResource(R.string.application_intro), fontSize = 14.sp, color = Color.Gray)

    Question(
        title = stringResource(R.string.application_q_housing),
        options = HousingType.entries,
        selected = state.housing,
        label = { housingLabel(it) },
        onSelect = viewModel::onHousingChange
    )
    Question(
        title = stringResource(R.string.application_q_experience),
        options = PetExperience.entries,
        selected = state.experience,
        label = { experienceLabel(it) },
        onSelect = viewModel::onExperienceChange
    )
    Question(
        title = stringResource(R.string.application_q_time),
        options = AvailableTime.entries,
        selected = state.availableTime,
        label = { timeLabel(it) },
        onSelect = viewModel::onTimeChange
    )
    Question(
        title = stringResource(R.string.application_q_pets),
        options = OtherPets.entries,
        selected = state.otherPets,
        label = { otherPetsLabel(it) },
        onSelect = viewModel::onOtherPetsChange
    )

    OutlinedTextField(
        value = state.extraInfo,
        onValueChange = viewModel::onExtraInfoChange,
        label = { Text(stringResource(R.string.application_q_extra)) },
        modifier = Modifier.fillMaxWidth().heightIn(min = 96.dp),
        shape = RoundedCornerShape(12.dp)
    )

    Button(
        onClick = viewModel::onGenerateLetter,
        enabled = state.answersComplete && !state.isGenerating,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = AppPrimary)
    ) {
        if (state.isGenerating) {
            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.application_generating))
        } else {
            Icon(Icons.Outlined.AutoAwesome, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.application_generate))
        }
    }
    OutlinedButton(
        onClick = viewModel::onWriteManually,
        enabled = state.answersComplete && !state.isGenerating,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Outlined.Edit, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(stringResource(R.string.application_write_manually))
    }
    if (!state.answersComplete) {
        Text(stringResource(R.string.application_answer_all), fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
private fun LetterStep(state: AdoptionApplicationUiState, viewModel: AdoptionApplicationViewModel) {
    state.aiMessage?.let { message ->
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF4E5))
        ) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(message, fontSize = 13.sp)
                OutlinedButton(onClick = viewModel::onGenerateLetter, enabled = !state.isGenerating) {
                    Text(stringResource(if (state.isGenerating) R.string.application_generating else R.string.application_retry))
                }
            }
        }
    }
    Text(
        text = stringResource(if (state.aiGenerated) R.string.application_review_ai else R.string.application_write_hint),
        fontSize = 14.sp,
        color = Color.Gray
    )
    OutlinedTextField(
        value = state.letter,
        onValueChange = viewModel::onLetterChange,
        label = { Text(stringResource(R.string.application_letter_label)) },
        modifier = Modifier.fillMaxWidth().heightIn(min = 260.dp),
        shape = RoundedCornerShape(12.dp)
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(R.string.application_letter_min, AdoptionApplicationUiState.MIN_LETTER),
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )
        Text("${state.letter.trim().length}", fontSize = 12.sp, color = Color.Gray)
    }
    Button(
        onClick = viewModel::onSend,
        enabled = state.canSend,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = AppPrimary)
    ) {
        if (state.sendResult == RequestResult.Loading) {
            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
        } else {
            Text(stringResource(R.string.application_send))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun <T> Question(
    title: String,
    options: List<T>,
    selected: T?,
    label: @Composable (T) -> String,
    onSelect: (T) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                FilterChip(
                    selected = option == selected,
                    onClick = { onSelect(option) },
                    label = { Text(label(option)) }
                )
            }
        }
    }
}

@Composable
fun housingLabel(value: HousingType) = stringResource(
    when (value) {
        HousingType.HOUSE_WITH_YARD -> R.string.opt_housing_house_yard
        HousingType.HOUSE -> R.string.opt_housing_house
        HousingType.APARTMENT -> R.string.opt_housing_apartment
        HousingType.RURAL -> R.string.opt_housing_rural
    }
)

@Composable
fun experienceLabel(value: PetExperience) = stringResource(
    when (value) {
        PetExperience.FIRST_TIME -> R.string.opt_experience_first_time
        PetExperience.SOME -> R.string.opt_experience_some
        PetExperience.EXPERIENCED -> R.string.opt_experience_expert
    }
)

@Composable
fun timeLabel(value: AvailableTime) = stringResource(
    when (value) {
        AvailableTime.LOW -> R.string.opt_time_low
        AvailableTime.MEDIUM -> R.string.opt_time_medium
        AvailableTime.HIGH -> R.string.opt_time_high
    }
)

@Composable
fun otherPetsLabel(value: OtherPets) = stringResource(
    when (value) {
        OtherPets.NONE -> R.string.opt_pets_none
        OtherPets.DOGS -> R.string.opt_pets_dogs
        OtherPets.CATS -> R.string.opt_pets_cats
        OtherPets.SEVERAL -> R.string.opt_pets_several
    }
)
