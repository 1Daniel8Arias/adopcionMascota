package co.edu.adopcionmascota.data.remote

// Nombres de las colecciones en Firestore
object Collections {
    const val USERS = "users"
    const val PUBLICATIONS = "publications"
    const val COMMENTS = "comments"
    const val NOTIFICATIONS = "notifications"
    const val ADOPTION_REQUESTS = "adoption_requests"
    const val FAVORITES = "favorites"   // Subcolección: users/{uid}/favorites/{publicationId}
    const val META = "meta"
}
