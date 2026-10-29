package co.edu.adopcionmascota.data.repository.memory

import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.domain.model.PetType
import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.domain.model.PublicationStatus
import co.edu.adopcionmascota.domain.repository.PublicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PublicationRepositoryImpl @Inject constructor(): PublicationRepository {

    private val _publications= MutableStateFlow<List<Publication>>(emptyList())

    override val publication: StateFlow<List<Publication>> = _publications.asStateFlow()

    override suspend fun save(publication: Publication) {
        if (!publication.name.contains("calculadore")){
            _publications.value += publication
        }else{
            throw Exception("El mobre de la mascota no corresponde \"calculadora\"")
        }
    }

    override suspend fun findById(id: String): Publication? {
       return _publications.value.firstOrNull{it.id==id}
    }

    init {
        _publications.value=fetchPublications();
    }


    private fun fetchPublications(): List<Publication>{
                return listOf(
                    Publication(
                        id = "1",
                        name = "Max",
                        description = "Perro amigable y juguetón en busca de un hogar.",
                        image = R.drawable.imagen_max,
                        status = PublicationStatus.VERIFIED,
                        type = PetType.ADOPCION,
                        location = "Filandia",
                        time = "Hace 2 horas",
                        distancia = "1.2 km",
                        likes = "45",
                        age = "3 meses",
                        gender = "Macho"
                    ),

                    Publication(
                        id = "2",
                        name = "Luna",
                        description = "Gatita tranquila y cariñosa, vacunada y esterilizada.",
                        image = R.drawable.imagen_luna,
                        status = PublicationStatus.RESOLVED,
                        type = PetType.PERDIDO,
                        location = "Circasia",
                        time = "Hace 1 día",
                        distancia = "3.8 km",
                        likes = "78",
                        age = "2 años",
                        gender = "Hembra"
                    ),

                    Publication(
                        id = "3",
                        name = "Rocky",
                        description = "Perro rescatado que necesita una familia responsable.",
                        image = R.drawable.imagen_rocky,
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


    }


}