plugins {
    kotlin("jvm")
    `maven-publish`
}

group = "com.hellokai.variant"
version = "1.0.0"

dependencies {
    implementation(libs.kotlin.std)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
