package com.hellokai.androidworkspace.buildsrc

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.BuiltArtifactsLoader
import com.android.build.api.variant.DslExtension
import com.android.build.api.variant.VariantExtension
import com.android.build.api.variant.VariantOutputConfiguration
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.register
import java.io.File

class VariantV2AdvancedPlugin : Plugin<Project> {

    class RST : VariantExtension

    override fun apply(project: Project) {
        val androidExtension =
            project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
        val abc = DslExtension.Builder(RST::class.simpleName!!).build()
        androidExtension.registerExtension(abc) { variantExtConfig ->
            variantExtConfig.variant
            RST()
        }
        androidExtension.onVariants { variant ->
            val mainOutput = variant.outputs.single {
                it.outputType == VariantOutputConfiguration.OutputType.SINGLE
            }
            val variantCapitalizedName = variant.name.capitalized()

            // 修改APK位置
            val renameApkTask =
                project.tasks.register<RenameApkFile>("renameV2${variantCapitalizedName}Apk") {
                    val apkFolderProvider = variant.artifacts.get(SingleArtifact.APK)
                    this.outApk.set(
                        File(
                            apkFolderProvider.get().asFile,
                            "custom-name-${variant.name}.apk",
                        ),
                    )
                    this.apkFolder.set(apkFolderProvider)
                    this.builtArtifactsLoader.set(variant.artifacts.getBuiltArtifactsLoader())
                }

            // 修改AndroidManifest.xml
            val postUpdateTask = project.tasks.register<ManifestAfterMergeTask>(
                "postUpdate${variantCapitalizedName}Manifest",
            )
            variant.artifacts
                .use(postUpdateTask)
                .wiredWithFiles(
                    ManifestAfterMergeTask::mergedManifest,
                    ManifestAfterMergeTask::updatedManifest,
                )
                .toTransform(SingleArtifact.MERGED_MANIFEST)

            val getManifestTask = project.tasks.register<GetManifestTask>(
                "getManifest$variantCapitalizedName",
            ) {
                updatedManifest.set(variant.artifacts.get(SingleArtifact.MERGED_MANIFEST))
            }

            renameApkTask.configure {
                dependsOn(getManifestTask)
            }

            // 添加一个文件到asset中
            val addAssetTask = project.tasks.register<AddAssetTask>(
                "AddAssetTask$variantCapitalizedName",
            ) {
                additionalAsset.set(project.file("app_key.txt"))
            }
            //
//            variant.artifacts.use(addAssetTask)
//                .wiredWith(AddAssetTask::outputDirectory)
//                .toAppendTo(MultipleArtifact.ASSETS)//找不到MultipleArtifact.ASSETS

            val notificationTaskProvider = project.tasks.register<NotificationTask>(
                "notify${variantCapitalizedName}Build",
            ) {
                title.set("${project.name} apk is built successfully.")
                releaseNode.set(
                    renameApkTask.map {
                        val size = it.outApk.get().asFile.length() / 1024 / 1024
                        "Apk - $size MB"
                    },
                )
            }
        }
    }

    abstract class RenameApkFile : DefaultTask() {
        @get:InputFiles
        abstract val apkFolder: DirectoryProperty

        @get:Internal
        abstract val builtArtifactsLoader: Property<BuiltArtifactsLoader>

        @get:OutputFile
        abstract val outApk: RegularFileProperty

        @TaskAction
        fun taskAction() {
            val builtArtifacts = builtArtifactsLoader.get().load(apkFolder.get())
                ?: throw RuntimeException("Cannot load APKs")
            File(builtArtifacts.elements.single().outputFile)
                .copyTo(outApk.get().asFile)
        }
    }

    abstract class ManifestAfterMergeTask : DefaultTask() {
        @get:InputFile
        abstract val mergedManifest: RegularFileProperty

        @get:OutputFile
        abstract val updatedManifest: RegularFileProperty

        @TaskAction
        fun afterMerge() {
            val modifiedManifest = mergedManifest.get().asFile.readText()
                .replace("allowBackup=\"true\"", "allowBackup=\"false\"")
            updatedManifest.get().asFile.writeText(modifiedManifest)
        }
    }

    abstract class GetManifestTask : DefaultTask() {
        @get:InputFile
        abstract val updatedManifest: RegularFileProperty

        @TaskAction
        fun afterMerge() {
            println(updatedManifest.get().asFile.readText())
        }
    }

    abstract class AddAssetTask : DefaultTask() {
        @get:InputFile
        abstract val additionalAsset: RegularFileProperty

        @get:OutputFiles
        abstract val outputDirectory: DirectoryProperty

        @TaskAction
        fun addAsset() {
            val target = additionalAsset.get().asFile
            val assetDir = outputDirectory.get().asFile
            assetDir.mkdirs()
            target.copyTo(File(assetDir, target.name))
        }
    }

    abstract class NotificationTask : DefaultTask() {
        @get:Input
        abstract val title: Property<String>

        @get:Input
        abstract val releaseNode: Property<String>

        @TaskAction
        fun taskAction() {
            val message = "${title.get()}\n${releaseNode.get()}"
            val channel = "1234567"
            println("NotificationTask: sending \"$message\" to channel $channel")
        }
    }
}
