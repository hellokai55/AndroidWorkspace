plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

gradlePlugin {
    plugins.register("slack") {
        id = "com.hellokai.androidworkspace.slack-lazy"
        implementationClass = "com.hellokai.androidworkspace.lazyslack.SlackLazyNotificationPlugin"
    }
    plugins.register("tlack") {
        id = "com.hellokai.androidworkspace.tlack-lazy"
        implementationClass = "com.hellokai.androidworkspace.lazyslack.TlackLazyNotificationPlugin"
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
