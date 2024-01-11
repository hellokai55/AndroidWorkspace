package com.hellokai.androidworkspace.slack

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

class SlackNotificationPlugin : Plugin<Project> {

    private val androidAppPluginApplied = AtomicBoolean(false)

    override fun apply(project: Project) {
        val slackExtension = project.extensions.create<SlackNotificationExtension>(
            "slackNotification",
        )
        project.afterEvaluate {
            check(androidAppPluginApplied.get()) {
                "Slack notification plugin should only be applied to an Android Application project " +
                    "but ${project.displayName} doesn't have the 'com.android.application' plugin applied."
            }
        }
        project.plugins.withType<AppPlugin>() {
            androidAppPluginApplied.set(true)
            val androidExtension = project.extensions.findByType(AppExtension::class.java)!!
            androidExtension.applicationVariants.configureEach {
                if (slackExtension.channelId.isBlank() ||
                    slackExtension.token.isBlank()
                ) {
                    throw IllegalArgumentException(
                        "Please specify target Slack Channel and Token " +
                            "in \"slackNotification{}\" block.",
                    )
                }
                if (slackExtension.enabled) {
                    val appVariant = this
                    val taskProvider = project.tasks.register<SlackNotificationTask>(
                        "assembleAndNotifySlack${appVariant.name.capitalized()}",
                    ) {
                        token = slackExtension.token
                        channelId = slackExtension.channelId
                        message = slackExtension.message
                        notifyPayloadLog = File(
                            appVariant.outputs.first().outputFile.parentFile,
                            "slack-notification.log",
                        )
                    }
                    taskProvider.dependsOn(this.assembleProvider)
                }
            }
        }
    }
}
