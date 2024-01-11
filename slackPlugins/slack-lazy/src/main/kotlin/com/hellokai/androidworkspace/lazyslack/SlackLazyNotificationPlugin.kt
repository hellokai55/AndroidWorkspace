package com.hellokai.androidworkspace.lazyslack

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean

class SlackLazyNotificationPlugin : Plugin<Project> {

    private val androidAppPluginApplied = AtomicBoolean(false)
    override fun apply(project: Project) {
        val slackExtension = project.extensions.create(
            "slackNotification1",
            SlackNotificationExtension::class.java,
        ).apply {
            enabled.convention(true)
            message.convention("")
        }

        project.afterEvaluate {
            check(true) {
                "Slack notification plugin should only be applied to an Android Application project " +
                    "but ${project.displayName} doesn't have the 'com.android.application' plugin applied."
            }
        }

        project.plugins.withType<AppPlugin>() {
            androidAppPluginApplied.set(true)
            val androidExtension =
                project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
            androidExtension.onVariants { appVariant ->
                if (slackExtension.channelId.get().isBlank() ||
                    slackExtension.token.get().isBlank()
                ) {
                    throw IllegalArgumentException(
                        "Please specify target Slack Channel and Token " +
                            "in \"slackNotification{}\" block.",
                    )
                }
                if (slackExtension.enabled.get()) {
                    val artifactsLoader = appVariant.artifacts.getBuiltArtifactsLoader()
                    val apkDir = appVariant.artifacts.get(SingleArtifact.APK)
                    val logFile = project.layout
                        .buildDirectory
                        .dir("outputs/logs/")
                        .map { it.file("slack-notification.log") }
                    project.tasks.register(
                        "assembleAndNotify333${appVariant.name.capitalize(Locale.ENGLISH)}",
                        SlackLazyNotificationTask::class.java,
                    ) {
                        token.set(slackExtension.token)
                        channelId.set(slackExtension.channelId)
                        message.set(slackExtension.message)
                        builtArtifactsLoader.set(artifactsLoader)
                        apkFolder.set(apkDir)
                        notifyPayloadLog.set(logFile)
                    }
                }
            }
        }
    }
}
