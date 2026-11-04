package co.edu.adopcionmascota.core.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

// Entrega los textos de strings.xml a los ViewModels, que no pueden usar stringResource()
interface ResourceProvider {
    fun getString(id: Int, vararg args: Any): String
}

class ResourceProviderImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ResourceProvider {

    override fun getString(id: Int, vararg args: Any): String {
        return context.getString(id, *args)
    }
}
