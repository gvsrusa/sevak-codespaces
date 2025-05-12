// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false // If you have pure Kotlin modules
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.kapt) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Define versions in libs.versions.toml
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}