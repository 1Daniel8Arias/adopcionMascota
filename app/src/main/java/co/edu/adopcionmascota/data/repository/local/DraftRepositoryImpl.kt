package co.edu.adopcionmascota.data.repository.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import co.edu.adopcionmascota.domain.repository.DraftRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.draftDataStore: DataStore<Preferences> by preferencesDataStore(name = "comment_drafts")

// Los borradores son preferencias pequeñas, por eso se guardan en Data Store y no en Room
@Singleton
class DraftRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : DraftRepository {

    private fun key(publicationId: String) = stringPreferencesKey("draft_$publicationId")

    override fun observeDraft(publicationId: String): Flow<String> =
        context.draftDataStore.data.map { it[key(publicationId)].orEmpty() }

    override suspend fun saveDraft(publicationId: String, text: String) {
        context.draftDataStore.edit { prefs ->
            if (text.isBlank()) prefs.remove(key(publicationId)) else prefs[key(publicationId)] = text
        }
    }

    override suspend fun clearDraft(publicationId: String) {
        context.draftDataStore.edit { it.remove(key(publicationId)) }
    }
}
