package com.hellokai.androidworkspace.buildsrc

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

/**
 * 需要依赖外部环境
 */
fun getAppFeatureSwitchesFromDotKt(android: ApplicationExtension): Map<String, Boolean?> {
    return mapOf(
        "dataBinding" to android.buildFeatures.dataBinding,
        "mlModelBinding" to android.buildFeatures.mlModelBinding,
        "prefab" to android.buildFeatures.prefab,
        "aidl" to android.buildFeatures.aidl,
        "buildConfig" to android.buildFeatures.buildConfig,
        "compose" to android.buildFeatures.compose,
        "renderScript" to android.buildFeatures.renderScript,
        "shaders" to android.buildFeatures.shaders,
        "resValues" to android.buildFeatures.resValues,
        "viewBinding" to android.buildFeatures.viewBinding
    )
}

/**
 * 不需要依赖外部环境，通过extensions.getByType<ApplicationExtension>()来做的
 */
fun Project.getAppFeatureSwitchesFromDotKt2(): Map<String, Boolean?> {
    val android = extensions.getByType<ApplicationExtension>()
    return mapOf(
        "dataBinding" to android.buildFeatures.dataBinding,
        "mlModelBinding" to android.buildFeatures.mlModelBinding,
        "prefab" to android.buildFeatures.prefab,
        "aidl" to android.buildFeatures.aidl,
        "buildConfig" to android.buildFeatures.buildConfig,
        "compose" to android.buildFeatures.compose,
        "renderScript" to android.buildFeatures.renderScript,
        "shaders" to android.buildFeatures.shaders,
        "resValues" to android.buildFeatures.resValues,
        "viewBinding" to android.buildFeatures.viewBinding,
        "caching" to findProperty("org.gradle.caching").toString().toBoolean(),
        "parallel" to findProperty("org.gradle.parallel").toString().toBoolean()
    )
}