package co.edu.adopcionmascota.data.repository.local

import android.content.Context
import co.edu.adopcionmascota.domain.model.UserRole
import co.edu.adopcionmascota.domain.model.UserSession
import co.edu.adopcionmascota.domain.repository.SessionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")
@Singleton
class SessionRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : SessionRepository{

    private object Keys{
        val USER_ID = stringPreferencesKey("user_id")
        val ROLE = stringPreferencesKey("role")
    }




    override val session: Flow<UserSession?> = context.dataStore.data.map { prefs ->
        val userId = prefs[Keys.USER_ID]
        val roleStr =prefs[Keys.ROLE]

        if (userId.isNullOrBlank() || roleStr.isNullOrBlank()) {
            null
        } else {
            // Retorna un objeto UserSession con los datos obtenidos si existen
            UserSession(
                userId = userId,
                role = UserRole.valueOf(roleStr)
            )
        }

    }


    override suspend fun saveSession(
        UserId: String,
        role: UserRole
    ) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_ID] = UserId
            prefs[Keys.ROLE] = role.name
        }
    }

    override suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }


}