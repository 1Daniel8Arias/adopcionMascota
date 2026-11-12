package co.edu.adopcionmascota.data.repository.remote

import co.edu.adopcionmascota.BuildConfig
import co.edu.adopcionmascota.data.remote.openrouter.ChatMessageDto
import co.edu.adopcionmascota.data.remote.openrouter.ChatRequestDto
import co.edu.adopcionmascota.data.remote.openrouter.OpenRouterApi
import co.edu.adopcionmascota.domain.exception.AiRateLimitException
import co.edu.adopcionmascota.domain.exception.AiUnavailableException
import co.edu.adopcionmascota.domain.model.AdoptionApplication
import co.edu.adopcionmascota.domain.model.AvailableTime
import co.edu.adopcionmascota.domain.model.HousingType
import co.edu.adopcionmascota.domain.model.OtherPets
import co.edu.adopcionmascota.domain.model.PetExperience
import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.domain.model.User
import co.edu.adopcionmascota.domain.repository.AdoptionLetterRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

// Carta formal de adopción redactada por un modelo GRATUITO de OpenRouter (BuildConfig.OPENROUTER_MODEL)
@Singleton
class OpenRouterLetterRepositoryImpl @Inject constructor(
    private val api: OpenRouterApi
) : AdoptionLetterRepository {

    override suspend fun generateLetter(
        publication: Publication,
        applicant: User,
        application: AdoptionApplication,
        language: String
    ): String {
        if (BuildConfig.OPENROUTER_API_KEY.isBlank()) throw AiUnavailableException("Falta OPENROUTER_API_KEY")
        // Seguridad: solo se permiten modelos gratuitos
        require(BuildConfig.OPENROUTER_MODEL.endsWith(":free")) { "OPENROUTER_MODEL debe ser un modelo :free" }

        val english = language.startsWith("en")
        val request = ChatRequestDto(
            model = BuildConfig.OPENROUTER_MODEL,
            messages = listOf(
                ChatMessageDto("system", systemPrompt(english)),
                ChatMessageDto("user", userPrompt(publication, applicant, application, english))
            )
        )
        val response = try {
            api.chatCompletion(request)
        } catch (e: HttpException) {
            if (e.code() == 429) throw AiRateLimitException()
            throw AiUnavailableException("HTTP ${e.code()}", e)
        } catch (e: IOException) {
            throw AiUnavailableException("Sin conexión", e)
        }
        // OpenRouter a veces responde 200 con un error del proveedor dentro del cuerpo
        response.error?.let { if (it.code == 429) throw AiRateLimitException() else throw AiUnavailableException(it.message) }
        val text = response.choices?.firstOrNull()?.message?.content?.trim()
        if (text.isNullOrBlank()) throw AiUnavailableException("Respuesta vacía")
        return text
    }

    private fun systemPrompt(english: Boolean) = if (english) {
        "You write formal, warm and honest pet adoption request letters for a Colombian animal " +
            "adoption app. Write only the letter (no title, no notes, no markdown), 150-220 words, in English. " +
            "Do not invent facts that are not in the data."
    } else {
        "Redactas cartas formales, cálidas y honestas de solicitud de adopción de mascotas para una app " +
            "colombiana de adopción. Escribe solo la carta (sin título, sin notas, sin markdown), de 150 a 220 " +
            "palabras, en español. No inventes datos que no estén en la información."
    }

    private fun userPrompt(p: Publication, u: User, a: AdoptionApplication, english: Boolean): String {
        val housing = when (a.housing) {
            HousingType.HOUSE_WITH_YARD -> if (english) "house with a yard" else "casa con patio"
            HousingType.HOUSE -> if (english) "house without a yard" else "casa sin patio"
            HousingType.APARTMENT -> if (english) "apartment" else "apartamento"
            HousingType.RURAL -> if (english) "farm / rural home" else "finca o casa campestre"
        }
        val experience = when (a.experience) {
            PetExperience.FIRST_TIME -> if (english) "first time with a pet" else "primera vez con una mascota"
            PetExperience.SOME -> if (english) "has had pets before" else "ya ha tenido mascotas"
            PetExperience.EXPERIENCED -> if (english) "a lot of experience with animals" else "mucha experiencia con animales"
        }
        val time = when (a.availableTime) {
            AvailableTime.LOW -> if (english) "less than 2 hours a day" else "menos de 2 horas al día"
            AvailableTime.MEDIUM -> if (english) "2 to 5 hours a day" else "de 2 a 5 horas al día"
            AvailableTime.HIGH -> if (english) "more than 5 hours a day" else "más de 5 horas al día"
        }
        val pets = when (a.otherPets) {
            OtherPets.NONE -> if (english) "no other pets" else "ninguna otra mascota"
            OtherPets.DOGS -> if (english) "dogs" else "perros"
            OtherPets.CATS -> if (english) "cats" else "gatos"
            OtherPets.SEVERAL -> if (english) "several pets" else "varias mascotas"
        }
        return buildString {
            appendLine(if (english) "Pet:" else "Mascota:")
            appendLine("- ${p.name}, ${p.species} ${p.breed}, ${p.size}, ${p.age}, ${p.location}")
            appendLine("- ${p.description.take(400)}")
            appendLine(if (english) "Applicant:" else "Solicitante:")
            appendLine("- ${u.name}, ${u.city}")
            appendLine((if (english) "- Home: " else "- Vivienda: ") + housing)
            appendLine((if (english) "- Experience: " else "- Experiencia: ") + experience)
            appendLine((if (english) "- Available time: " else "- Tiempo disponible: ") + time)
            appendLine((if (english) "- Other pets: " else "- Otras mascotas: ") + pets)
            if (a.extraInfo.isNotBlank()) appendLine((if (english) "- Notes: " else "- Comentarios: ") + a.extraInfo.take(500))
            append(if (english) "Write the letter addressed to the person who published the pet."
                   else "Escribe la carta dirigida a la persona que publicó a la mascota.")
        }
    }
}
