package co.edu.adopcionmascota.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import co.edu.adopcionmascota.data.local.dao.AdoptionRequestDao
import co.edu.adopcionmascota.data.local.dao.CommentDao
import co.edu.adopcionmascota.data.local.dao.FavoriteDao
import co.edu.adopcionmascota.data.local.dao.NotificationDao
import co.edu.adopcionmascota.data.local.dao.PublicationDao
import co.edu.adopcionmascota.data.local.dao.UserDao
import co.edu.adopcionmascota.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.NAME)
            // Room ahora es la caché local de Firestore: si cambia el esquema se recrea,
            // porque los datos se vuelven a descargar de la nube
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .fallbackToDestructiveMigration(true)
            .build()

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

    @Provides
    fun providePublicationDao(database: AppDatabase): PublicationDao = database.publicationDao()

    @Provides
    fun provideCommentDao(database: AppDatabase): CommentDao = database.commentDao()

    @Provides
    fun provideNotificationDao(database: AppDatabase): NotificationDao = database.notificationDao()

    @Provides
    fun provideFavoriteDao(database: AppDatabase): FavoriteDao = database.favoriteDao()

    @Provides
    fun provideAdoptionRequestDao(database: AppDatabase): AdoptionRequestDao = database.adoptionRequestDao()
}
