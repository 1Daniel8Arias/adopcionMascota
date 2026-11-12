package co.edu.adopcionmascota.features.publication

import co.edu.adopcionmascota.domain.model.PetType

// El formulario trabaja con el texto de la categoría ("Adopción", "Veterinaria"...)
// y el dominio con el enum PetType
fun categoryToType(category: String): PetType = when (category) {
    "Perdido" -> PetType.PERDIDO
    "Encontrado" -> PetType.ENCONTRADO
    "Veterinaria" -> PetType.VETERINARIA
    else -> PetType.ADOPCION
}

fun typeToCategory(type: PetType): String = when (type) {
    PetType.ADOPCION -> "Adopción"
    PetType.PERDIDO -> "Perdido"
    PetType.ENCONTRADO -> "Encontrado"
    PetType.VETERINARIA -> "Veterinaria"
}
