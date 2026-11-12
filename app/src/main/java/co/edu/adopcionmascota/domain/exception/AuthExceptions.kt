package co.edu.adopcionmascota.domain.exception

// Excepciones del dominio: los ViewModels no necesitan saber que por debajo está Firebase

// El correo ya tiene una cuenta (hereda de IllegalArgumentException para que el registro
// siga mostrando el mensaje de "usuario ya existe")
class EmailAlreadyInUseException : IllegalArgumentException("El correo ya está registrado")

// No existe una cuenta con ese correo (recuperación de contraseña)
class UserNotFoundException : Exception("No existe una cuenta con ese correo")
