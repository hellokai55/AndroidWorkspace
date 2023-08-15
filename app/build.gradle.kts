plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.hellokai.androidworkspace"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.hellokai.androidworkspace"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(deps.androidx.ktx)
    implementation(deps.appcompat)
    implementation(deps.material)
    implementation(deps.constraintlayout)
    implementation(deps.nav.fragment.ktx)
    implementation(deps.nav.ui.ktx)
    implementation(deps.nav.ui.ktx)
}