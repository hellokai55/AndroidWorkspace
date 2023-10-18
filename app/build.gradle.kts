import com.hellokai.androidworkspace.buildsrc.getAppFeatureSwitchesFromDotKt
import com.hellokai.androidworkspace.buildsrc.getAppFeatureSwitchesFromDotKt2

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("com.android.application")
    kotlin("android")
    id("com.hellokai.androidworkspace.notification")
    id("lib-basis-plugin")
    id("task-essentials-plugin")
}

apply(from = "../standalone-scripts/app-build-features-export2.gradle.kts")

android {
    namespace = "com.hellokai.androidworkspace"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hellokai.androidworkspace"
        minSdk = 24
        targetSdk = 34
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.nav.fragment.ktx)
    implementation(libs.nav.ui.ktx)
    implementation(libs.nav.ui.ktx)
}

test_notification {
    token = "slackToken"
    channelId = "slackChannelId"
    message = "Slack Notification project built successfully!"
}

afterEvaluate {
    // Test Extension Function from BuildSrc
    println("app->build.gradle.kts,afterEvaluate,Test Extension Function from .kt file:")
    println(getAppFeatureSwitchesFromDotKt(android))
    println("app->build.gradle.kts,afterEvaluate,Test Extension Function from .kt file2:")
    println(getAppFeatureSwitchesFromDotKt2())

    // 使用独立脚本的方法
    val funcFromScript = extra["getAppFeatureSwitchesFromScriptPlugin"] as () -> Map<String, Boolean?>
    println(funcFromScript())
}
