includeBuild("../slackPlugins/slack") {
    dependencySubstitution {
        substitute(module("com.hellokai.androidworkspace:slack"))
            .using(project(":"))
    }
}
includeBuild("../slackPlugins/slack-lazy") {
    dependencySubstitution {
        substitute(module("com.hellokai.androidworkspace:slack-lazy"))
            .using(project(":"))
    }
}

pluginManagement {
    plugins {
        `kotlin-dsl`
    }
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
