package co.edu.adopcionmascota.domain

import android.health.connect.datatypes.ExerciseRoute
import java.time.LocalDate

data class  Publications(
    val id: String,
    val name: String,
    val description: String,
    val image: Int,
    val status: PublicationStatus,
    val type: PetType,
    val location: String,
    val time: String,
    val distancia: String,
    val likes: String = "",
    val age: String = "",
    val gender: String = ""
) 
