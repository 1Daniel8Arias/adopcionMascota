package co.edu.adopcionmascota.data.remote

import com.google.firebase.firestore.DocumentSnapshot

// Lectura segura de campos de un DocumentSnapshot (valor por defecto si falta el campo).
// Los DTOs leen campo por campo en lugar de usar toObject(): las propiedades "isAlgo" de
// Kotlin se guardarían en Firestore con otro nombre ("algo") y los val no tienen setter.
internal fun DocumentSnapshot.str(field: String) = getString(field).orEmpty()
internal fun DocumentSnapshot.long(field: String) = getLong(field) ?: 0L
internal fun DocumentSnapshot.int(field: String) = getLong(field)?.toInt() ?: 0
internal fun DocumentSnapshot.bool(field: String) = getBoolean(field) ?: false
internal fun DocumentSnapshot.double(field: String) = getDouble(field) ?: 0.0
