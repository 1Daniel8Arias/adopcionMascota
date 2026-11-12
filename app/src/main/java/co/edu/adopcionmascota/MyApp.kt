package co.edu.adopcionmascota

import android.app.Application
import co.edu.adopcionmascota.data.remote.sync.FirebaseSeeder
import co.edu.adopcionmascota.data.remote.sync.FirestoreCacheSync
import co.edu.adopcionmascota.di.ApplicationScope
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MyApp : Application() {

    @Inject lateinit var cacheSync: FirestoreCacheSync
    @Inject lateinit var seeder: FirebaseSeeder
    @Inject @ApplicationScope lateinit var appScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        // Room se mantiene como caché de Firestore mientras haya un usuario autenticado
        cacheSync.start()
        // Cuentas y datos de ejemplo en Firebase (solo para desarrollo)
        if (BuildConfig.DEBUG) appScope.launch { seeder.seedIfNeeded() }
    }
}
