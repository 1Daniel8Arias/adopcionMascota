package co.edu.adopcionmascota.core.util

 sealed class RequestResult {

     object Loading: RequestResult()

     data class Success(val message: String): RequestResult()

     data class Failure(val erroMessage: String): RequestResult()

}