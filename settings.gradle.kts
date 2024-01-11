pluginManagement {
    val versions = file("gradle/libs.versions.toml").readText()
    val regexPlaceHolder = "%s\\s\\=\\s\\\"([A-Za-z0-9\\.\\-]+)\\\""
    val getVersion =
        { s: String -> regexPlaceHolder.format(s).toRegex().find(versions)!!.groupValues[1] }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.namespace) {
                "com.android" ->
                    useModule("com.android.tools.build:gradle:${getVersion("androidGradlePlugin")}")

                "org.jetbrains.kotlin" ->
                    useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${getVersion("kotlinPlugin")}")
            }
        }
    }
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    includeBuild("./build-logic")
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}

rootProject.name = "AndroidWorkspace"
include(":app")
includeBuild("plugins")
includeBuild("plugin-config")
include(":lib1")
include(":variant-feature")
include(":variantlib1")
include(":variantlib1-api")

