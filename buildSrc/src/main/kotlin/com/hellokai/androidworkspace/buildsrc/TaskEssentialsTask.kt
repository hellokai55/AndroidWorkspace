package com.hellokai.androidworkspace.buildsrc

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectories
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class TaskEssentialsTask : DefaultTask() {

    /**
     * Input
     */
    @get:Input
    var inputString: String = ""

    @get:InputFile
    var inputFile: File? = null

    @get:InputFiles
    var inputFiles: List<File>? = null

    @get:InputFiles
    abstract val inputFileCollection: ConfigurableFileCollection

    @get:InputDirectory
    var inputDir: File? = null

    /**
     * Output
     */
    @get:OutputFile
    var outputFile: File? = null

    @get:OutputFiles
    var outputFiles: List<File>? = null

    @get:OutputFiles
    abstract val outputFileCollection: ConfigurableFileCollection

    @get:OutputFiles
    var outputFileMap: Map<String, File>? = null

    @get:OutputDirectory
    var outputDir: File? = null

    @get:OutputDirectories
    abstract val outputDirCollection: ConfigurableFileCollection

    @get:OutputDirectories
    var outputDirMap: Map<String, File>? = null

    @TaskAction
    fun apply() {
        inputFile!!.copyTo(outputFile!!)

        inputFiles!!.forEachIndexed { index, file ->
            file.copyTo(outputFiles!![index])
        }

        val outputCollection = outputFileCollection.files.toList().sorted()
        inputFileCollection.files.toList().sorted()
            .forEachIndexed { index, file ->
                file.copyTo(outputCollection[index])
                file.copyTo(outputFileMap!![index.toString()]!!)
            }

        inputDir!!.copyRecursively(outputDir!!)

        val outputDirCollection = outputDirCollection.files.toList()
        outputDirCollection.forEach {
            inputDir!!.copyRecursively(it)
        }

        for (i in 0 until outputDirMap!!.size) {
            inputDir!!.copyRecursively(outputDirMap!![i.toString()]!!)
        }
    }

}