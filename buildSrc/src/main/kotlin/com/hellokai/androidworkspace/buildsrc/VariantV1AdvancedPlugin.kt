package com.hellokai.androidworkspace.buildsrc

import com.android.build.gradle.AppExtension
import com.android.build.gradle.tasks.ProcessApplicationManifest
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.register
import java.io.File

class VariantV1AdvancedPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val android = project.extensions.getByType(AppExtension::class.java)
        android.applicationVariants.configureEach {
            val variant = this
            val variantCapitalizedName = variant.name.capitalized()

            variant.outputs.forEach { output ->
                val file = output.outputFile
                println("output: " + output.outputType)
                println("output: " + file.name)
                if (file.extension == "apk") {
                    val out = File(file.parentFile, "custom-${variant.versionName}.apk")
                    val renameApkTask = project.tasks.register<RenameApkFile>(
                        "rename${variantCapitalizedName}Apk",
                    ) {
                        inputApk = file
                        outputApk = out
                        dependsOn(variant.packageApplicationProvider)
                    }

                    val releaseNodeFile = File(file.parentFile, "release-note.txt")
                    val apkSizeObtainTask = project.tasks.register<ApkSizeObtainTask>(
                        "apkSizeObtain$variantCapitalizedName",
                    ) {
                        apk = file
                        releaseNode = releaseNodeFile
                        dependsOn(renameApkTask)
                    }
                    val notificationTask = project.tasks.register<NotificationTask>(
                        "notifyHelloVariantV1$variantCapitalizedName",
                    ) {
                        title = "${project.name} apk is built successfully."
                        releaseNote = releaseNodeFile
                        dependsOn(apkSizeObtainTask)
                    }
                    assembleProvider.configure {
                        dependsOn(notificationTask)
                    }
                }
            }

            // 在原来的基础上修改，不会影响原来任务的增量编译
            /**
             * > Task :app:processDebugMainManifest UP-TO-DATE
             * > Task :app:processDebugManifest UP-TO-DATE
             * > Task :app:processDebugManifestForPackage UP-TO-DATE
             */
            val processManifestTask = project.tasks
                .withType(ProcessApplicationManifest::class.java)
                .first { it.name.contains(variant.name, true) }

            processManifestTask.inputs.apply {
                property("replace_value", "allowBackup=\"false\"")
            }

            processManifestTask.doLast("postUpdateAction") {
                val task = this as ProcessApplicationManifest
                val targetValue = task.inputs.properties["replace_value"].toString()
                val modifiedManifest = task.mergedManifest.get().asFile.readText()
                    .replace("allowBackup=\"true\"", targetValue)
                task.mergedManifest.get().asFile.writeText(modifiedManifest)
            }
        }
    }

    abstract class RenameApkFile : DefaultTask() {

        @get:InputFile
        lateinit var inputApk: File

        @get:OutputFile
        lateinit var outputApk: File

        @TaskAction
        fun taskAction() {
            inputApk.copyTo(outputApk)
        }
    }

    abstract class ApkSizeObtainTask : DefaultTask() {
        @get:InputFile
        lateinit var apk: File

        @get:OutputFile
        lateinit var releaseNode: File

        @TaskAction
        fun taskAction() {
            val size = apk.length() / 1024.0 / 1024.0
            releaseNode.writeText("Apk - $size MB")
        }
    }

    abstract class NotificationTask : DefaultTask() {
        @get:Input
        lateinit var title: String

        @get:InputFile
        lateinit var releaseNote: File

        @TaskAction
        fun taskAction() {
            val msg = "$title\n${releaseNote.readText()}"
            val channel = "1234556"
            NotificationClient33().send(msg, channel)
        }
    }

    class NotificationClient33 {
        fun send(message: String, channel: String) {
            println("NotificationTask: sending \"$message\" to channel $channel")
        }
    }
}
