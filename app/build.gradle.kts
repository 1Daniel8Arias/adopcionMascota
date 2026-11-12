import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.google.gms.google.services)
}

// Se lee fuera de android { } para que localProperties se pueda usar en todo el archivo
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

android {
    namespace = "co.edu.adopcionmascota"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "co.edu.adopcionmascota"
        minSdk = 28
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Crea el recurso "mapbox_access_token" con el token de local.properties
        resValue("string", "mapbox_access_token", localProperties.getProperty("MAPBOX_ACCESS_TOKEN") ?: "")

        // OpenRouter (IA para la carta de adopción). La clave la pone cada desarrollador en
        // local.properties y nunca se sube al repositorio. El modelo debe ser gratuito (":free")
        buildConfigField("String", "OPENROUTER_API_KEY", "\"${localProperties.getProperty("OPENROUTER_API_KEY") ?: ""}\"")
        buildConfigField(
            "String", "OPENROUTER_MODEL",
            "\"${localProperties.getProperty("OPENROUTER_MODEL") ?: "nvidia/nemotron-3.5-super-vl:free"}\""
        )

        // Solo se empaquetan las librerías nativas para procesadores de 64 bits:
        // arm64-v8a (casi todos los teléfonos) y x86_64 (emuladores en PC)
        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        resValues = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.benchmark.traceprocessor)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.text)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.firebase.annotations)

    // Firebase (guías 19 y 20): el BoM fija versiones compatibles entre sí
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.storage)
    implementation(libs.kotlinx.coroutines.play.services) // await() sobre las Task de Firebase
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.material.icons.extended)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.data.store)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.mapbox.android)
    implementation(libs.mapbox.compose)

    // Room: base de datos local (guía 18 - Persistencia de datos)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    testImplementation(libs.kotlinx.coroutines.test)

    // Retrofit (guía 18): cliente de la API de OpenRouter
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp.logging)


}