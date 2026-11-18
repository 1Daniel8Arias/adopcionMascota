package co.edu.adopcionmascota.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import co.edu.adopcionmascota.data.local.dao.AdoptionRequestDao
import co.edu.adopcionmascota.data.local.dao.CommentDao
import co.edu.adopcionmascota.data.local.dao.FavoriteDao
import co.edu.adopcionmascota.data.local.dao.NotificationDao
import co.edu.adopcionmascota.data.local.dao.PublicationDao
import co.edu.adopcionmascota.data.local.dao.UserDao
import co.edu.adopcionmascota.data.local.entity.AdoptionRequestEntity
import co.edu.adopcionmascota.data.local.entity.CommentEntity
import co.edu.adopcionmascota.data.local.entity.FavoriteEntity
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import co.edu.adopcionmascota.data.local.entity.NotificationEntity
import co.edu.adopcionmascota.data.local.entity.PublicationEntity
import co.edu.adopcionmascota.data.local.entity.UserEntity

// Base de datos local: única fuente de verdad de la aplicación
@Database(
    entities = [
        UserEntity::class,
        PublicationEntity::class,
        CommentEntity::class,
        NotificationEntity::class,
        AdoptionRequestEntity::class,
        FavoriteEntity::class
    ],
    version = 3, // 3: Room pasa a ser caché de Firestore (campos de la solicitud formal)
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun publicationDao(): PublicationDao
    abstract fun commentDao(): CommentDao
    abstract fun notificationDao(): NotificationDao
    abstract fun adoptionRequestDao(): AdoptionRequestDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        const val NAME = "adopcion_mascota.db"

        // Versión 2: se agrega la tabla de favoritos ("Me encanta") sin borrar los datos
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `favorites` (`userId` TEXT NOT NULL, `publicationId` TEXT NOT NULL, " +
                        "`createdAt` INTEGER NOT NULL, PRIMARY KEY(`userId`, `publicationId`))"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_favorites_userId` ON `favorites` (`userId`)")
            }
        }
    }
}
