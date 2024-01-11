plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

gradlePlugin {
    plugins.register("slack") {
        id = "com.hellokai.androidworkspace.slack"
        implementationClass = "com.hellokai.androidworkspace.slack.SlackNotificationPlugin"
    }
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

dependencies {
    // Basis
    implementation(gradleApi())
    implementation(libs.kotlin.std)
    implementation(libs.android.gradle.plugin)
    // Module specific
    implementation(libs.okHttp)
}
