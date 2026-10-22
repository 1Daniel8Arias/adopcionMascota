package co.edu.adopcionmascota.features.publication

import co.edu.adopcionmascota.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Estados de una publicación propia ("Mis Publicaciones" del Figma)
enum class MyPublicationStatus(val label: String) {
    ACTIVE("Verificada en Feed"),
    IN_REVIEW("En Revisión Ética"),
    RESOLVED("¡Finalizada con Éxito!"),
    OBSERVATION("Acción Requerida"),
    PAUSED("Pausada")
}

data class MyPublication(
    val id: String,
    val feedId: String?,            // Id de la publicación en el feed (null si aún no está publicada)
    val name: String,
    val category: String,           // Adopción, Perdido, Encontrado...
    val species: String = "Perro",
    val breed: String = "",
    val size: String = "Mediano",
    val description: String,
    val location: String,
    val image: Int,
    val status: MyPublicationStatus,
    val isVaccinated: Boolean = true,
    val isDewormed: Boolean = true,
    val isSterilized: Boolean = false,
    val hasMicrochip: Boolean = false,
    val followers: Int = 0,
    val comments: Int = 0,
    val shares: Int = 0,
    val moderatorNote: String? = null
)

// Por ahora las publicaciones propias viven en memoria; más adelante vendrán del repositorio / Firebase
object MyPublicationsStore {

    private val _publications = MutableStateFlow(initialPublications())
    val publications: StateFlow<List<MyPublication>> = _publications.asStateFlow()

    // Ids del feed que el usuario eliminó; el feed los oculta
    private val _deletedFeedIds = MutableStateFlow<Set<String>>(emptySet())
    val deletedFeedIds: StateFlow<Set<String>> = _deletedFeedIds.asStateFlow()

    // Datos de la última eliminación, para la pantalla de éxito
    var lastDeleted: Pair<MyPublication, String>? = null
        private set

    fun findById(id: String): MyPublication? = _publications.value.find { it.id == id }

    fun add(publication: MyPublication) {
        _publications.update { listOf(publication) + it }
    }

    fun update(publication: MyPublication) {
        _publications.update { list -> list.map { if (it.id == publication.id) publication else it } }
    }

    fun changeStatus(id: String, status: MyPublicationStatus) {
        findById(id)?.let { update(it.copy(status = status)) }
    }

    fun delete(id: String, reason: String) {
        val publication = findById(id) ?: return
        lastDeleted = publication to reason
        publication.feedId?.let { feedId -> _deletedFeedIds.update { it + feedId } }
        _publications.update { list -> list.filterNot { it.id == id } }
    }

    // Datos quemados con las publicaciones del Figma (usando las mascotas que existen en la app)
    private fun initialPublications(): List<MyPublication> = listOf(
        MyPublication(
            id = "m1",
            feedId = "1",
            name = "Max",
            category = "Adopción",
            breed = "Cruce de Golden Retriever",
            description = "Rescatado cerca a la Calle 127. Ya cuenta con vacunas al día, desparasitación completa y es sumamente juguetón.",
            location = "1.2 km · Filandia",
            image = R.drawable.imagen_max,
            status = MyPublicationStatus.ACTIVE,
            followers = 84,
            comments = 12,
            shares = 26
        ),
        MyPublication(
            id = "m2",
            feedId = null,
            name = "Rocky",
            category = "Encontrado",
            breed = "Mestizo",
            description = "Los moderadores están verificando los soportes veterinarios para garantizar que el caso cumpla las normas éticas antes de publicarse en el feed general.",
            location = "Encontrado • Hace 2 horas",
            image = R.drawable.imagen_rocky,
            status = MyPublicationStatus.IN_REVIEW
        ),
        MyPublication(
            id = "m3",
            feedId = null,
            name = "Toby",
            category = "Adopción",
            breed = "Schnauzer Mestizo",
            description = "¡Adoptado por una familia en Suba! Caso cerrado satisfactoriamente el 18 de Mayo.",
            location = "Suba",
            image = R.drawable.icono,
            status = MyPublicationStatus.RESOLVED
        ),
        MyPublication(
            id = "m4",
            feedId = "2",
            name = "Luna",
            category = "Perdido",
            species = "Gato",
            breed = "Siamesa mestiza",
            size = "Pequeño",
            description = "Gatita tranquila y cariñosa, vacunada y esterilizada.",
            location = "Circasia",
            image = R.drawable.imagen_luna,
            status = MyPublicationStatus.OBSERVATION,
            isSterilized = true,
            moderatorNote = "\"La foto adjunta del carnet de vacunación está borrosa y no permite leer la fecha de la antirrábica. Por favor sube una imagen nítida para reactivar el post.\""
        )
    )
}
