package com.hellokai.androidworkspace.notification

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import java.io.File
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean

abstract class NotificationPlugin : Plugin<Project> {

    private val androidAppPluginApplied = AtomicBoolean(false)

    override fun apply(project: Project) {
        println("[NotificationPlugin] apply")
        val notificationExtension = project.extensions.create<NotificationExtension>(
            "test_notification",
        )
        project.afterEvaluate {
            check(androidAppPluginApplied.get()) {
                "Notification plugin should only be applied to an Android Application project " +
                    "but ${project.displayName} doesn't have the 'com.android.application' plugin applied."
            }
        }
        project.plugins.withType<AppPlugin> {
            androidAppPluginApplied.set(true)
            val androidExtension = project.extensions.findByType(AppExtension::class.java)
            androidExtension?.applicationVariants?.configureEach {
                println("[variant]${this.name}")
                if (notificationExtension.channelId.isBlank() ||
                    notificationExtension.token.isBlank()
                ) {
                    throw IllegalArgumentException(
                        "Please specify target Slack Channel and Token " +
                            "in \"slackNotification{}\" block.",
                    )
                }
                if (notificationExtension.enabled) {
                    val appVariant = this
                    val taskProvider = project.tasks.register<NotificationTask>(
                        "assembleAndNotify${
                            appVariant.name.replaceFirstChar {
                                if (it.isLowerCase()) {
                                    it.titlecase(
                                        Locale.getDefault(),
                                    )
                                } else {
                                    it.toString()
                                }
                            }
                        }",
                    ) {
                        token = notificationExtension.token
                        channelId = notificationExtension.channelId
                        message = notificationExtension.message
                        notifyPayloadLog = File(
                            appVariant.outputs.first().outputFile.parentFile,
                            "notification-payload.log",
                        )
                    }
                    taskProvider.get().dependsOn(appVariant.assembleProvider)
                }
            }
        }
    }
}
