package co.edu.adopcionmascota.domain.exception

// Excepciones del dominio: los ViewModels no necesitan saber que por debajo está Firebase

// El correo ya tiene una cuenta (hereda de IllegalArgumentException para que el registro
// siga mostrando el mensaje de "usuario ya existe")
class EmailAlreadyInUseException : IllegalArgumentException("El correo ya está registrado")

// La cuenta existe pero el correo aún no se ha verificado
class EmailNotVerifiedException : Exception("Debes verificar tu correo antes de iniciar sesión")

// El enlace de recuperación venció o ya se usó
class InvalidResetCodeException : Exception("El enlace de recuperación no es válido o ya venció")

// No existe una cuenta con ese correo (recuperación de contraseña)
class UserNotFoundException : Exception("No existe una cuenta con ese correo")
