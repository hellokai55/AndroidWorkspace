package com.hellokai.androidworkspace.notification

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.IOException

abstract class NotificationTask : DefaultTask() {

    @get:Input
    var token: String = ""

    @get:Input
    var channelId: String = ""

    @get:Input
    var message: String = ""

    @get:OutputFile
    var notifyPayloadLog: File? = null

    @TaskAction
    fun notifyBuildCompletion() {
        project.logger.lifecycle("Build completed, posting result...")
        val customMsg = StringBuilder().append("Message: ${message}\n")
            .append("Result: OK\n")
            .toString()
        val (code, responseBody) = postOnSlack(token, channelId, customMsg)
        logToFile(
            channelId,
            customMsg,
            responseBody,
            code,
        )
        project.logger.lifecycle("Done")
    }

    private fun postOnSlack(
        token: String,
        channelId: String,
        text: String,
    ): Pair<Int, String> {
        val jsonWithUtf8 = "application/json; charset=utf-8".toMediaType()
        val requestBody = (
            "{\"channel\": \"$channelId\", " +
                "\"text\": \"$text\"}"
            )
        val request: Request = Request.Builder()
            .url("https://slack.com/api/chat.postMessage")
            .header("Authorization", "Bearer $token")
            .post(requestBody.toRequestBody(jsonWithUtf8))
            .build()
        var code: Int
        var responseBody: String
        try {
            OkHttpClient().newCall(request)
                .execute()
                .use { response ->
                    code = response.code
                    responseBody = response.body!!.string()
                }
        } catch (e: IOException) {
            code = -1
            responseBody = e.message ?: ""
        }
        return Pair(code, responseBody)
    }

    private fun logToFile(
        slackChannelName: String,
        requestBody: String,
        responseBody: String,
        responseCode: Int,
    ) {
        if (notifyPayloadLog != null) {
            notifyPayloadLog!!.appendText(
                """
                    |requestOf = $slackChannelName
                    |requestBody = $requestBody
                    |responseCode = $responseCode
                    |responseBody = $responseBody
                """.trimIndent(),
            )
        }
    }
}
