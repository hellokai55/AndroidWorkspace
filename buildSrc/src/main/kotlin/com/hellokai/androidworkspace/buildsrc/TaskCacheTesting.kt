package com.hellokai.androidworkspace.buildsrc

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register

class TaskCacheTesting : Plugin<Project> {
    override fun apply(target: Project) {
        println("TaskCacheTesting--start")
        val sourceFile = target.file("cache_testing_app_key_creation.txt")
        val outFile = target.layout.buildDirectory
            .dir("outputs")
            .map { it.file("cache_testing_app_key_transformation.txt") }

        val taskA = target.tasks.register<TaskACreate>("CacheTestingTaskA") {
            outcome.set(sourceFile)
            this.dependsOn(target.tasks.findByPath("preBuild"))
        }

        val taskB = target.tasks.register<TaskBCreate>("CacheTestingTaskB") {
            income.set(taskA.flatMap { it.outcome })
            outcome.set(sourceFile)
        }

        val taskC = target.tasks.register<TaskCTransformantion>("CacheTestingTaskC") {
            income.set(taskB.flatMap { it.outcome })
            outcome.set(outFile)
        }
    }

    /**
     * 只有输出没有输入，所以不支持增量编译
     */
    abstract class TaskACreate : DefaultTask() {
        @get:OutputFile
        abstract val outcome: RegularFileProperty

        @TaskAction
        fun create() {
            println("TaskACreate is running")
            outcome.get().asFile.writeText("abcdefg")
        }
    }

    /**
     * 修改了输入，所以不支持增量编译
     */
    abstract class TaskBCreate : DefaultTask() {
        @get:InputFile
        abstract val income: RegularFileProperty

        @get:OutputFile
        abstract val outcome: RegularFileProperty

        @TaskAction
        fun append() {
            println("TaskBCreate is running")
            income.get().asFile.appendText("hijklmn")
        }
    }

    /**
     * 合理写法，支持增量编译
     */
    abstract class TaskCTransformantion : DefaultTask() {
        @get:InputFile
        abstract val income: RegularFileProperty

        @get:OutputFile
        abstract val outcome: RegularFileProperty

        @TaskAction
        fun transform() {
            println("TaskCTransformantion is running")
            val origin = income.get().asFile.readText()
            outcome.get().asFile.writeText(origin.reversed())
        }
    }
}
