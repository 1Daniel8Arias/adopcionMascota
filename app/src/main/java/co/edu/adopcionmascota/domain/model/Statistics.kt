package co.edu.adopcionmascota.domain.model

// Conteos reales calculados a partir de la base de datos
data class Statistics(
    val totalPublications: Int = 0,
    val byStatus: Map<PublicationStatus, Int> = emptyMap(),
    val byType: Map<PetType, Int> = emptyMap(),
    val completedAdoptions: Int = 0,
    val totalRequests: Int = 0,
    val pendingRequests: Int = 0,
    val acceptedRequests: Int = 0,
    val totalComments: Int = 0,
    val totalUsers: Int = 0,
    val averageReviewMinutes: Int = 0
) {
    fun countFor(status: PublicationStatus): Int = byStatus[status] ?: 0
    fun countFor(type: PetType): Int = byType[type] ?: 0
}
