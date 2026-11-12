package co.edu.adopcionmascota.domain.exception

// El modelo gratuito alcanzó su límite de uso (HTTP 429): hay que esperar un momento
class AiRateLimitException : Exception("Límite de uso de la IA alcanzado")

// La IA no respondió (sin clave, sin conexión o error del proveedor)
class AiUnavailableException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)
