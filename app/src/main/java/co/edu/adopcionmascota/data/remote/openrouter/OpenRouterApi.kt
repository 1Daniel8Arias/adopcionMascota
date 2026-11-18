package co.edu.adopcionmascota.data.remote.openrouter

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST

// API de OpenRouter (compatible con el formato de chat de OpenAI), consumida con Retrofit (guía 18)
interface OpenRouterApi {
    // La clave viaja en el encabezado Authorization, que agrega el interceptor de NetworkModule
    @POST("chat/completions")
    suspend fun chatCompletion(@Body request: ChatRequestDto): ChatResponseDto
}

// ---------- DTOs: el JSON tal como viaja por la red ----------

data class ChatRequestDto(
    val model: String,
    val messages: List<ChatMessageDto>,
    @SerializedName("max_tokens") val maxTokens: Int = 1000,
    val temperature: Double = 0.7,
    // Muchos modelos gratuitos "razonan" antes de responder y gastan ahí todos los tokens,
    // dejando la carta vacía. Para una carta no hace falta razonar, así que se desactiva
    val reasoning: ReasoningDto = ReasoningDto(enabled = false)
)

data class ReasoningDto(val enabled: Boolean)

data class ChatMessageDto(
    val role: String,       // "system" o "user"
    val content: String
)

data class ChatResponseDto(
    val choices: List<ChoiceDto>? = null,
    val error: ErrorDto? = null
)

data class ChoiceDto(val message: ChatMessageDto? = null)

data class ErrorDto(val code: Int? = null, val message: String? = null)
