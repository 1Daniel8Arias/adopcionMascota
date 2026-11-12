package co.edu.adopcionmascota.features.password

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.core.theme.AppPrimary
import co.edu.adopcionmascota.core.util.RequestResult

// Guía 20, actividad 2: se abre desde el enlace del correo de recuperación (oobCode)
@Composable
fun ResetPasswordScreen(
    oobCode: String,
    onFinished: () -> Unit,
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val done = state.result is RequestResult.Success

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Icon(Icons.Outlined.LockReset, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(56.dp))
        Text(stringResource(R.string.reset_password_title), fontSize = 24.sp, fontWeight = FontWeight.Bold)

        if (!done) {
            Text(stringResource(R.string.reset_password_subtitle), color = MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text(stringResource(R.string.reset_password_new)) },
                isError = state.passwordError != null,
                supportingText = { state.passwordError?.let { Text(it) } },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                label = { Text(stringResource(R.string.reset_password_confirm)) },
                isError = state.confirmPasswordError != null,
                supportingText = { state.confirmPasswordError?.let { Text(it) } },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            (state.result as? RequestResult.Failure)?.let {
                Text(it.errorMessage, color = MaterialTheme.colorScheme.error)
            }
            Button(
                onClick = { viewModel.confirm(oobCode) },
                enabled = state.isFormValid && state.result !is RequestResult.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.result is RequestResult.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.reset_password_button))
                }
            }
            TextButton(onClick = onFinished) { Text(stringResource(R.string.go_to_login)) }
        } else {
            Text((state.result as RequestResult.Success).message)
            Button(onClick = onFinished, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.go_to_login))
            }
        }
    }
}
