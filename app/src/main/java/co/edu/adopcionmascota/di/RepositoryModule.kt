package co.edu.adopcionmascota.di

import co.edu.adopcionmascota.data.repository.remote.OpenRouterLetterRepositoryImpl
import co.edu.adopcionmascota.domain.repository.AdoptionLetterRepository

import co.edu.adopcionmascota.data.repository.remote.FirebaseImageRepositoryImpl
import co.edu.adopcionmascota.domain.repository.ImageRepository

import co.edu.adopcionmascota.data.repository.remote.AdoptionRequestRepositoryImpl
import co.edu.adopcionmascota.data.repository.remote.CommentRepositoryImpl
import co.edu.adopcionmascota.data.repository.local.DraftRepositoryImpl
import co.edu.adopcionmascota.data.repository.remote.FavoriteRepositoryImpl
import co.edu.adopcionmascota.domain.repository.FavoriteRepository
import co.edu.adopcionmascota.data.repository.local.FilterPreferencesRepositoryImpl
import co.edu.adopcionmascota.data.repository.remote.NotificationRepositoryImpl
import co.edu.adopcionmascota.data.repository.remote.PublicationRepositoryImpl
import co.edu.adopcionmascota.data.repository.local.SessionRepositoryImpl
import co.edu.adopcionmascota.data.repository.remote.UserRepositoryImpl
import co.edu.adopcionmascota.domain.repository.AdoptionRequestRepository
import co.edu.adopcionmascota.domain.repository.CommentRepository
import co.edu.adopcionmascota.domain.repository.DraftRepository
import co.edu.adopcionmascota.domain.repository.FilterPreferencesRepository
import co.edu.adopcionmascota.domain.repository.NotificationRepository
import co.edu.adopcionmascota.domain.repository.PublicationRepository
import co.edu.adopcionmascota.domain.repository.SessionRepository
import co.edu.adopcionmascota.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds // Indica a Hilt que esta función vincula una implementación a una interfaz
    @Singleton
    abstract fun bindPublicationRepository(
        publicationRepositoryImpl: PublicationRepositoryImpl
    ): PublicationRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepository: UserRepositoryImpl
    ): UserRepository // Vincula UserRepositoryImpl con UserRepository

    @Binds
    @Singleton
    abstract fun bindCommentRepository(impl: CommentRepositoryImpl): CommentRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindAdoptionRequestRepository(impl: AdoptionRequestRepositoryImpl): AdoptionRequestRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(impl: FavoriteRepositoryImpl): FavoriteRepository

    @Binds
    @Singleton
    abstract fun bindDraftRepository(impl: DraftRepositoryImpl): DraftRepository

    @Binds
    @Singleton
    abstract fun bindFilterPreferencesRepository(
        impl: FilterPreferencesRepositoryImpl
    ): FilterPreferencesRepository

    // Subida de imágenes (Firebase Storage); para cambiar a Cloudinary basta otra implementación
    @Binds
    @Singleton
    abstract fun bindImageRepository(impl: FirebaseImageRepositoryImpl): ImageRepository

    // Carta formal de adopción con IA (OpenRouter, modelo gratuito)
    @Binds
    @Singleton
    abstract fun bindAdoptionLetterRepository(impl: OpenRouterLetterRepositoryImpl): AdoptionLetterRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository
}
