// App module build file. Versions come from gradle/libs.versions.toml.
// Android Gradle Plugin 9 has built-in Kotlin, so there is no kotlin-android plugin.
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "io.github.iamarasheslami.openartexplorer"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "io.github.iamarasheslami.openartexplorer"
        minSdk {
            version = release(23)
        }
        targetSdk {
            version = release(36)
        }
        versionCode = 1
        versionName = "0.1.0"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)

    testImplementation(libs.junit)
}
