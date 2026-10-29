package co.edu.adopcionmascota.domain.model

import java.time.LocalDateTime

data class Comment (
    val id: String,
    val message: String,
    val userId: String,
    val date: LocalDateTime
)