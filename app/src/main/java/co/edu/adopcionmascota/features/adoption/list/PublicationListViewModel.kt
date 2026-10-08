package co.edu.adopcionmascota.features.adoption.list

import androidx.lifecycle.ViewModel
import co.edu.adopcionmascota.domain.PetType
import co.edu.adopcionmascota.domain.PublicationStatus
import co.edu.adopcionmascota.domain.Publications
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PublicationListViewModel: ViewModel() {

    private val _publications = MutableStateFlow(emptyList<Publications>())

    val publications: StateFlow<List<Publications>> = _publications.asStateFlow()

    init {
        fetchPublications()
    }

    fun findById(id: String): Publications?{
        return _publications.value.find { it.id==id }
    }



    private fun fetchPublications() {
        val publications =listOf(
            Publications(
                id = "1",
            name = "Max",
            description = "Perro amigable y juguetón en busca de un hogar.",
            image = "https://example.com/images/max.jpg",
            status = PublicationStatus.VERIFIED,
            type = PetType.ADOPCION,
            location = "Filandia",
            time = "Hace 2 horas",
            distancia = "1.2 km",
            likes = "45",
            age = "3 años",
            gender = "Macho"
        ),

         Publications(
            id = "2",
            name = "Luna",
            description = "Gatita tranquila y cariñosa, vacunada y esterilizada.",
            image = "https://example.com/images/luna.jpg",
            status = PublicationStatus.RESOLVED,
            type = PetType.PERDIDO,
            location = "Circasia",
            time = "Hace 1 día",
            distancia = "3.8 km",
            likes = "78",
            age = "2 años",
            gender = "Hembra"
        ),

        Publications(
            id = "3",
            name = "Rocky",
            description = "Perro rescatado que necesita una familia responsable.",
            image = "https://example.com/images/rocky.jpg",
            status = PublicationStatus.PENDING,
            type = PetType.ENCONTRADO,
            location = "Armenia",
            time = "Hace 5 horas",
            distancia = "850 m",
            likes = "32",
            age = "5 años",
            gender = "Macho"
        )

            )
        _publications.value=publications
    }


}