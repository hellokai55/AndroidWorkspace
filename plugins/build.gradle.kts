@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

gradlePlugin {
    plugins.register("notification") {
        id = "com.hellokai.androidworkspace.notification"
        implementationClass = "com.hellokai.androidworkspace.notification.NotificationPlugin"
    }
}

dependencies {
    implementation(gradleApi())
    implementation(libs.okHttp)
    implementation(libs.android.gradle.plugin)
}
