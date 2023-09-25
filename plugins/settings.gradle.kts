pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

gradle.addBuildListener(object : BuildListener {
    override fun settingsEvaluated(settings: Settings) {
        println("settingsEvaluated")
    }

    override fun projectsLoaded(gradle: Gradle) {
        println("projectsLoaded")
    }

    override fun projectsEvaluated(gradle: Gradle) {
        println("projectsEvaluated")
    }

    override fun buildFinished(result: BuildResult) {
        println("buildFinished")
    }
})

gradle.taskGraph.whenReady {
    // 4.任务配置完成
    println("taskGraph")
}
