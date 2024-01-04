package com.hellokai.androidworkspace.buildsrc

import org.gradle.api.Plugin
import org.gradle.api.Project

class TaskCacheTesting: Plugin<Project> {
    override fun apply(target: Project) {
        val sourceFile = target.file("cache_testing_app_key_creation.txt")
        val outFile = target.layout.buildDirectory
    }
}