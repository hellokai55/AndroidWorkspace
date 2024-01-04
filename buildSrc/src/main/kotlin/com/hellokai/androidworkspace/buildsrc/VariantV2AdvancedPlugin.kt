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
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.register
import java.io.File
import java.lang.RuntimeException

class VariantV2AdvancedPlugin : Plugin<Project> {

    class RST: VariantExtension

    override fun apply(project: Project) {
        val androidExtension =  project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
        val abc = DslExtension.Builder(RST::class.simpleName!!).build()
        androidExtension.registerExtension(abc) {variantExtConfig ->
            variantExtConfig.variant
            RST()
        }
        androidExtension.onVariants {variant ->
            val mainOutput = variant.outputs.single {
                it.outputType == VariantOutputConfiguration.OutputType.SINGLE
            }
            val variantCapitalizedName = variant.name.capitalized()

            val renameApkTask = project.tasks.register<RenameApkFile>("renameV2${variantCapitalizedName}Apk") {
                val apkFolderProvider = variant.artifacts.get(SingleArtifact.APK)
                this.outApk.set(File(apkFolderProvider.get().asFile, "custom-name-${variant.name}.apk"))
                this.apkFolder.set(apkFolderProvider)
                this.builtArtifactsLoader.set(variant.artifacts.getBuiltArtifactsLoader())
            }
        }
    }

    abstract class RenameApkFile: DefaultTask() {
        @get:InputFiles
        abstract val apkFolder: DirectoryProperty

        @get:Internal
        abstract val builtArtifactsLoader: Property<BuiltArtifactsLoader>

        @get:OutputFile
        abstract val outApk : RegularFileProperty

        @TaskAction
        fun taskAction() {
            val builtArtifacts = builtArtifactsLoader.get().load(apkFolder.get())
                ?: throw RuntimeException("Cannot load APKs")
            File(builtArtifacts.elements.single().outputFile)
                .copyTo(outApk.get().asFile)
        }
    }

}
