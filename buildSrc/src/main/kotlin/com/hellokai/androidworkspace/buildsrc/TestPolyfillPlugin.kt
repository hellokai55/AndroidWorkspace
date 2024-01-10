package com.hellokai.androidworkspace.buildsrc

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 使用polyfill报错了，后面再看吧
 */
class TestPolyfillPlugin : Plugin<Project> {
    override fun apply(project: Project) {
//        project.apply(plugin = "me.2bab.polyfill")
        println("TestPolyfillPlugin is running!!!")

//        val androidExtension =
//            project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
//        androidExtension.onVariants { variant ->
//            // use()
//            val preHookManifestTaskAction1 =
//                PreUpdateManifestsTaskAction(project.buildDir, id = "preHookManifestTaskAction1")
//            variant.artifactsPolyfill.use(
//                action = preHookManifestTaskAction1,
//                toInPlaceUpdate = PolyfilledMultipleArtifact.ALL_MANIFESTS,
//            )
//        }
    }

//    class PreUpdateManifestsTaskAction(
//        buildDir: File,
//        id: String,
//    ) : PolyfillAction<List<RegularFile>> {
//
//        override fun onTaskConfigure(task: Task) {}
//
//        override fun onExecute(artifact: Provider<List<RegularFile>>) {
//            artifact.get().let { files ->
//                files.forEach {
//                    val manifestFile = it.asFile
//                    // Check per manifest input and filter whatever you want, remove broken pieces, etc.
//                    val updatedContent = manifestFile.readText().replace("abc", "def")
//                    manifestFile.writeText(updatedContent)
//                }
//            }
//        }
//    }
}
