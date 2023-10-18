package com.hellokai.androidworkspace.buildsrc

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.PathSensitivity
import org.gradle.kotlin.dsl.register
import java.io.File

abstract class TaskEssentialsPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.tasks.register<TaskEssentialsTask>("testTaskEssentials") {
            inputString = "file"
            inputFile = File(project.projectDir, "inputs/single-file.txt")
            inputFiles = listOf(
                File(project.projectDir, "inputs/single-file.txt"),
                File(project.projectDir, "inputs/single-file1.txt")
            )
            inputFileCollection.from(
                File(project.projectDir, "inputs/single-file.txt"),
                File(project.projectDir, "inputs/single-file1.txt")
            )
            inputDir = File(project.projectDir, "inputs")

            outputFile = File(project.buildDir, "outputs/single/single-file.txt")
            outputFiles = listOf(
                File(project.buildDir, "outputs/files/single-file.txt"),
                File(project.buildDir, "outputs/files/single-file1.txt")
            )
            outputFileCollection.from(
                File(project.buildDir, "outputs/file-collection/single-file.txt"),
                File(project.buildDir, "outputs/file-collection/single-file1.txt")
            )
            outputFileMap = mapOf(
                "0" to File(project.buildDir, "outputs/file-map/single-file.txt"),
                "1" to File(project.buildDir, "outputs/file-map/single-file1.txt"),
            )
            outputDir = File(project.buildDir, "outputs/dir")
            outputDirCollection.from(
                File(project.buildDir, "outputs/dir-collection-1/"),
                File(project.buildDir, "outputs/dir-collection-2/")
            )
            outputDirMap = mapOf(
                "0" to File(project.buildDir, "outputs/dir-map-1/"),
                "1" to File(project.buildDir, "outputs/dir-map-3/"),
            )

            project.tasks.register<NoInputTask>("noInputTask") {
                final = File(project.buildDir, "outputs/no-input-task.txt")

                inputs.file(File(project.projectDir, "inputs/single-file.txt"))
                    .withPropertyName("injectedInput")
                    .withPathSensitivity(PathSensitivity.RELATIVE)
                    .skipWhenEmpty()
                inputs.property("custom-key", "custom-value")
                outputs.file(File(project.buildDir, "outputs/injected-output.txt"))
                    .withPropertyName("injectedOutput")

                doLast("injectedAction"){
                    val i = inputs.files.singleFile
                    val o = outputs.files.files.find { it.name == "injected-output.txt" }!!
                    i.copyTo(o)
                    o.appendText(inputs.properties["custom-key"].toString())
                }
            }
        }
    }

}