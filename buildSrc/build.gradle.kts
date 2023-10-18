plugins {
    `kotlin-dsl`//引入了kotlin系列插件，java-gradle-plugin,precompiled-script-plugin等相关插件
    `java-gradle-plugin`
}

// To make it available as direct dependency
group = "com.hellokai.androidworkspace"
version = "SNAPSHOT"

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    implementation(kotlin("stdlib-jdk8"))
    implementation(libs.android.gradle.plugin)
}

gradlePlugin {

    plugins.register("lib-convention-binary-plugin") {
        id = "lib-convention-binary-plugin"
        implementationClass = "com.hellokai.androidworkspace.buildsrc.LibConventionBinaryPlugin"
    }

    plugins.register("lib-basis-plugin") {
        id = "lib-basis-plugin"
        implementationClass = "com.hellokai.androidworkspace.buildsrc.BasisExtPlugin"
    }

    plugins.register("task-essentials-plugin") {
        id = "task-essentials-plugin"
        implementationClass = "com.hellokai.androidworkspace.buildsrc.TaskEssentialsPlugin"
    }
}