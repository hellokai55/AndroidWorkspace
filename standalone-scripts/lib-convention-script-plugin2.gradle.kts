import com.android.build.gradle.LibraryExtension

/**
 * 独立脚本了，需要任何的依赖都需要在buildscript中声明
 */
buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.2")
        classpath("com.squareup.okhttp3:okhttp:4.9.0")
    }
}

apply(plugin = "com.android.library")

// `android {}` does not work here since Type-safe model accessor
// is not working for standalone script plugin.
configure<LibraryExtension> {
    lint {
        abortOnError = false
    }
}

println("The lib-convention-script-plugin2 from ./standalone-scripts is applied.")
