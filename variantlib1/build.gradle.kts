@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    kotlin("jvm")
    `maven-publish`
}

group = "com.hellokai.variant"
version = "1.0.0"

val SourceSet.kotlin: SourceDirectorySet
    get() = project.extensions
        .getByType<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension>()
        .sourceSets
        .getByName(name)
        .kotlin

val okhttp = sourceSets.create("okhttp") {
    // 约定大于定义，代表的src是在okhttp目录下的
    java.srcDirs("src/okhttp/java", "src/main/java")
    kotlin.srcDirs("src/okhttp/kotlin", "src/main/kotlin")
}

java {
    registerFeature("okhttp") {
        usingSourceSet(okhttp)
    }
}

configurations.named("okhttpImplementation")
    .get()
    .extendsFrom(configurations.implementation.get())

dependencies {
    implementation(project(":variantlib1-api"))
    implementation(libs.kotlin.std)
    "okhttpImplementation"(libs.okHttp)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

afterEvaluate {
    println(
        "okhttpImplementation-all deps size:" + configurations.named("okhttpImplementation")
            .get().dependencies.size,
    )
    configurations.named("okhttpImplementation").get().allDependencies.forEach {
        println("okhttpImplementation - dep from all: $it")
    }
    configurations.named("okhttpImplementation").get().dependencies.forEach {
        println("okhttpImplementation - dep from it self:$it")
    }
}
