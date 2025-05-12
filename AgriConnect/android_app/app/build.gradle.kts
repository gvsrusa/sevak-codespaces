plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.agriconnect"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.agriconnect"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose) // Includes ui, graphics, tooling-preview, material3

    // Room
    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)

    // Coroutines
    implementation(libs.bundles.coroutines)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)


    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4) // For Compose testing
    debugImplementation(libs.androidx.compose.ui.tooling) // For Compose tooling (preview)
    debugImplementation(libs.androidx.compose.ui.test.manifest) // For Compose testing

    // Mockito (for mocking in unit tests)
    testImplementation(libs.mockito.core) // Replace with libs.mockk if MockK is preferred and defined
    // testImplementation(libs.mockk) // Example for MockK

    // Hilt Testing
    testImplementation(libs.hilt.android.testing)
    kspTest(libs.hilt.compiler) // Use kspTest for KSP
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler) // Use kspAndroidTest for KSP
}

// Allow references to generated code
// kapt is generally for Java annotation processors.
// Since Hilt and Room are using KSP (as seen with 'ksp(libs.hilt.compiler)' and 'ksp(libs.androidx.room.compiler)'),
// the kapt block might not be strictly necessary if all annotation processing is via KSP.
// However, leaving it as is unless issues arise or a full migration to KSP for all processors is confirmed.
kapt {
    correctErrorTypes = true
}