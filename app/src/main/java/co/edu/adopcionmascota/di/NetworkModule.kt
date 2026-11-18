package co.edu.adopcionmascota.di

import co.edu.adopcionmascota.BuildConfig
import co.edu.adopcionmascota.data.remote.openrouter.OpenRouterApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

// Retrofit para OpenRouter (guía 18). La clave sale de local.properties vía BuildConfig
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val OPENROUTER_URL = "https://openrouter.ai/api/v1/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .header("Authorization", "Bearer ${BuildConfig.OPENROUTER_API_KEY}")
                    .header("X-Title", "Patitas Conectadas")
                    .build()
            )
        }
        // En debug se registran solo los encabezados de la respuesta (nunca la clave ni el cuerpo)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
        })
        // Los modelos gratuitos pueden tardar en responder
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(OPENROUTER_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideOpenRouterApi(retrofit: Retrofit): OpenRouterApi = retrofit.create(OpenRouterApi::class.java)
}
