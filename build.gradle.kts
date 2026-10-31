// Top-level build file. Plugin versions come from gradle/libs.versions.toml.
// The plugins are declared here with "apply false" so every module uses the same
// version. AGP 9 has built-in Kotlin, so there is no kotlin-android plugin.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
