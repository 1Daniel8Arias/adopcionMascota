package co.edu.adopcionmascota.data.local.database

import co.edu.adopcionmascota.domain.model.Comment
import co.edu.adopcionmascota.domain.model.ContributionType
import co.edu.adopcionmascota.domain.model.Location
import co.edu.adopcionmascota.domain.model.Notification
import co.edu.adopcionmascota.domain.model.NotificationType
import co.edu.adopcionmascota.domain.model.PetType
import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.domain.model.PublicationStatus
import co.edu.adopcionmascota.domain.model.User
import co.edu.adopcionmascota.domain.model.UserRole

// Datos de ejemplo: se insertan una sola vez, cuando se crea la base de datos
object SeedData {

    private const val HOUR = 60 * 60 * 1000L
    private const val DAY = 24 * HOUR

    fun users(now: Long): List<User> = listOf(
        User(
            id = "1",
            name = "Santiago López",
            city = "Armenia",
            address = "Barrio Centro, Calle 12",
            email = "santiago@patitas.test",
            password = "Santiago123",
            phoneNumber = "+57 310 123 4567",
            profilePictureUrl = "https://picsum.photos/200?random=10",
            bio = "Rescatista voluntario en Armenia y hogar de paso para cachorros.",
            isTemporaryHome = true,
            points = 140,
            createdAt = now - 120 * DAY
        ),
        User(
            id = "2",
            name = "Valentina Gómez",
            city = "Pereira",
            address = "Barrio Cuba, Carrera 25",
            email = "valentina@patitas.test",
            password = "Valentina123",
            phoneNumber = "+57 320 456 7890",
            profilePictureUrl = "https://picsum.photos/200?random=11",
            bio = "Veterinaria y voluntaria de brigadas de esterilización.",
            points = 60,
            createdAt = now - 60 * DAY
        ),
        User(
            id = "3",
            name = "Andrés Martínez",
            city = "Manizales",
            address = "Barrio Palermo, Calle 8",
            email = "andres@patitas.test",
            password = "Andres123",
            profilePictureUrl = "https://picsum.photos/200?random=12",
            role = UserRole.MODERATOR,
            createdAt = now - 200 * DAY
        )
    )

    fun publications(now: Long): List<Publication> = listOf(
        Publication(
            id = "1",
            name = "Max",
            description = "Perro amigable y juguetón en busca de un hogar. Se lleva bien con niños y otros perros.",
            image = "imagen_max",
            status = PublicationStatus.VERIFIED,
            type = PetType.ADOPCION,
            location = "Filandia",
            createdAt = now - 2 * HOUR,
            reviewedAt = now - HOUR,
            distancia = "1.2 km",
            likes = 45,
            age = "3 meses",
            gender = "Macho",
            species = "Perro",
            breed = "Golden Mestizo",
            size = "Mediano",
            isVaccinated = true,
            isDewormed = true,
            isSterilized = true,
            hasMicrochip = true,
            coordinates = Location(latitude = 4.6733, longitude = -75.6583),
            ownerId = "1"
        ),
        Publication(
            id = "2",
            name = "Luna",
            description = "Gatita tranquila y cariñosa que salió de casa en Circasia. Tiene collar rojo; si la ves, no la persigas y avisa por la app.",
            image = "imagen_luna",
            status = PublicationStatus.VERIFIED,
            type = PetType.PERDIDO,
            location = "Circasia",
            createdAt = now - DAY,
            reviewedAt = now - DAY + HOUR,
            distancia = "3.8 km",
            likes = 78,
            age = "2 años",
            gender = "Hembra",
            species = "Gato",
            breed = "Siamesa mestiza",
            size = "Pequeño",
            isVaccinated = true,
            isSterilized = true,
            coordinates = Location(latitude = 4.6189, longitude = -75.6356),
            ownerId = "2"
        ),
        Publication(
            id = "3",
            name = "Rocky",
            description = "Perro rescatado cerca del parque principal de Armenia. Está a salvo en un hogar de paso mientras aparece su familia.",
            image = "imagen_rocky",
            status = PublicationStatus.PENDING,
            type = PetType.ENCONTRADO,
            location = "Armenia",
            createdAt = now - 5 * HOUR,
            distancia = "850 m",
            likes = 32,
            age = "5 años",
            gender = "Macho",
            species = "Perro",
            breed = "Mestizo",
            size = "Grande",
            isDewormed = true,
            coordinates = Location(latitude = 4.5389, longitude = -75.6725),
            ownerId = "1"
        ),
        Publication(
            id = "4",
            name = "Toby",
            description = "Toby fue atropellado y necesita una cirugía de cadera. Buscamos veterinarios voluntarios o aportes de medicamentos.",
            image = "icono",
            status = PublicationStatus.VERIFIED,
            type = PetType.VETERINARIA,
            location = "Calarcá",
            createdAt = now - 3 * DAY,
            reviewedAt = now - 3 * DAY + 2 * HOUR,
            distancia = "6 km",
            likes = 21,
            age = "4 años",
            gender = "Macho",
            species = "Perro",
            breed = "Schnauzer Mestizo",
            size = "Pequeño",
            isVaccinated = true,
            coordinates = Location(latitude = 4.5297, longitude = -75.6411),
            ownerId = "2"
        ),
        Publication(
            id = "5",
            name = "Cachorros Bulldog Francés",
            description = "Cachorros con pedigrí en adopción con aporte económico de \$500.000.",
            image = "imagen_max",
            status = PublicationStatus.PENDING,
            type = PetType.ADOPCION,
            location = "Pereira",
            createdAt = now - 2 * DAY,
            distancia = "15 km",
            age = "2 meses",
            gender = "Macho",
            species = "Perro",
            breed = "Bulldog Francés",
            size = "Pequeño",
            coordinates = Location(latitude = 4.8133, longitude = -75.6961),
            ownerId = "2"
        )
    )

    fun comments(now: Long): List<Comment> = listOf(
        Comment(
            id = "c1",
            publicationId = "1",
            authorId = "2",
            author = "Valentina Gómez",
            authorTag = "Vet Verificado",
            text = "Puedo aportar un bulto de alimento y la primera consulta gratuita para la familia que adopte a Max.",
            type = ContributionType.VET_FOOD,
            createdAt = now - HOUR,
            thanks = 12
        )
    )

    fun notifications(now: Long): List<Notification> = listOf(
        Notification(
            id = "n1",
            userId = "1",
            type = NotificationType.PUBLICATION_APPROVED,
            category = "APROBACIÓN ÉTICA",
            title = "¡Publicación Aprobada!",
            message = "Tu caso Max fue verificado éticamente y ya está en el feed público.",
            createdAt = now - HOUR,
            publicationId = "1",
            referenceId = "ID: 1",
            actionLabel = "Ver Caso"
        ),
        Notification(
            id = "n2",
            userId = "1",
            type = NotificationType.NEW_COMMENT,
            category = "COMENTARIO EN CASO",
            title = "Valentina Gómez",
            message = "Comentó en Max: \"Puedo aportar un bulto de alimento y la primera consulta gratuita...\"",
            createdAt = now - HOUR,
            publicationId = "1",
            referenceId = "ID: 1",
            actionLabel = "Responder",
            isRead = true
        )
    )
}
