package co.edu.adopcionmascota.data.repository.memory

import co.edu.adopcionmascota.domain.model.User
import co.edu.adopcionmascota.domain.model.UserRole
import co.edu.adopcionmascota.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserRepositoryImpl @Inject constructor(): UserRepository{


    private val users = mutableListOf(
        User(
            id = "1",
            name = "Santiago López",
            city = "Armenia",
            address = "Barrio Centro, Calle 12",
            email = "santiago@email.com",
            password = "111111",
            profilePictureUrl = "https://picsum.photos/200?random=10"
        ),
        User(
            id = "2",
            name = "Valentina Gómez",
            city = "Pereira",
            address = "Barrio Cuba, Carrera 25",
            email = "valentina@email.com",
            password = "222222",
            profilePictureUrl = "https://picsum.photos/200?random=11"
        ),
        User(
            id = "3",
            name = "Andrés Martínez",
            city = "Manizales",
            address = "Barrio Palermo, Calle 8",
            email = "andres@email.com",
            password = "333333",
            profilePictureUrl = "https://picsum.photos/200?random=12",
            role = UserRole.ADMIN
        )
    )


    override suspend fun save(user: User) {
        users.add(user);
    }

    override suspend fun findById(id: String): User? {
       return users.firstOrNull{it.id==id}
    }

    override suspend fun login(email: String, password: String): User? {
        return users.firstOrNull{it.email==email && it.password== password}
    }

    override suspend fun findByEmail(email: String): User? {
        return users.firstOrNull{it.email==email}
    }
}