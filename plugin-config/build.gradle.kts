plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

// To make it available as direct dependency
group = "com.hellokai.androidworkspace.buildinaction"
version = "SNAPSHOT"

dependencies {
    implementation(gradleApi())
    implementation(libs.kotlin.std)
    implementation(libs.android.gradle.plugin)
    implementation("com.hellokai.androidworkspace:slack:+")
    implementation("com.hellokai.androidworkspace:slack-lazy:+")
//    implementation("me.2bab.buildinaction:slack-nested-blocks:+")
//    implementation("me.2bab.buildinaction:slack-task-orchestra:+")
//    implementation("me.2bab.buildinaction:slack-cache-rules-compliance:+")
//    implementation("me.2bab.buildinaction:slack-test:+")
}

repositories {
    google()
    mavenCentral()
}
