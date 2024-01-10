import com.android.build.gradle.internal.core.MergedFlavor
import com.hellokai.androidworkspace.buildsrc.getAppFeatureSwitchesFromDotKt
import com.hellokai.androidworkspace.buildsrc.getAppFeatureSwitchesFromDotKt2
import org.gradle.configurationcache.extensions.capitalized

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("com.android.application")
    kotlin("android")
    id("com.hellokai.androidworkspace.notification")
    id("lib-basis-plugin")
    id("task-essentials-plugin")
    id("task-cache-testing")
    id("variant-v1-advanced")
    id("variant-v2-advanced")
    id("test-polyfill-plugin")
}

apply(from = "../standalone-scripts/app-build-features-export2.gradle.kts")

android {
    namespace = "com.hellokai.androidworkspace"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hellokai.androidworkspace"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
        }
        getByName("release") {
            isMinifyEnabled = true
        }
    }
//    flavorDimensions += "server"
//    productFlavors {
//        create("staging") {
//            dimension = "server"
//            applicationIdSuffix = ".staging"
//            versionNameSuffix = "-staging"
//        }
//        create("production") {
//            dimension = "server"
//            applicationIdSuffix = ".production"
//            versionNameSuffix = "-production"
//            versionCode = 2
//        }
//    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.nav.fragment.ktx)
    implementation(libs.nav.ui.ktx)
    implementation(libs.nav.ui.ktx)
    implementation("com.hellokai.variant:variantlib1:1.0.0") {
        capabilities {
            requireCapability("com.hellokai.variant:variantlib1-okhttp")
        }
    }
    implementation(project(mapOf("path" to ":variantlib1-api")))
}

test_notification {
    token = "slackToken"
    channelId = "slackChannelId"
    message = "Slack Notification project built successfully!"
}

afterEvaluate {
    // Test Extension Function from BuildSrc
    println("app->build.gradle.kts,afterEvaluate,Test Extension Function from .kt file:")
    println(getAppFeatureSwitchesFromDotKt(android))
    println("app->build.gradle.kts,afterEvaluate,Test Extension Function from .kt file2:")
    println(getAppFeatureSwitchesFromDotKt2())

    // 使用独立脚本的方法
    val funcFromScript =
        extra["getAppFeatureSwitchesFromScriptPlugin"] as () -> Map<String, Boolean?>
    println(funcFromScript())
}

// 错误方式：无法判断入参是什么类型
println("Task runOnReleaseVariantOnly: config")
if (gradle.startParameter.taskNames.toString().contains("release", ignoreCase = true)) {
    tasks.create("runOnReleaseVariantOnly") {
        doFirst {
            println("Task runOnReleaseVariantOnly: running...")
        }
    }
}

// 应该使用下列方式
android.applicationVariants.configureEach {
    println("Task applicationVariants: config correct variant:${this.name}")
}

// 变体感知型任务
androidComponents.onVariants { v ->
    println("Task runOnDebugVariantOnly2: config correct variant:${v.name.capitalized()}")
    if (gradle.startParameter.taskNames.toString().contains("debug", ignoreCase = true)) {
        tasks.create("runOnDebugVariantOnly2${v.name.capitalized()}") {
            doFirst {
                println("Task runOnDebugVariantOnly2: running...")
            }
        }
    }
}

// 获取已经配置内容
android.applicationVariants.configureEach {
    val variantCapitalizedName = this.name.capitalized()
    logger.lifecycle("variant name:${this.name}")
    logger.lifecycle("variant.applicationId: ${this.applicationId}")
    logger.lifecycle("variant.versionCode: ${this.versionCode}")
    logger.lifecycle("variant.versionName: ${this.versionName}")
    logger.lifecycle("variant.minSdkVersion:${this.mergedFlavor.minSdkVersion}")
    logger.lifecycle("variant.mergedFlavor.manifestPlaceholders:${this.mergedFlavor.manifestPlaceholders}")
    (this.mergedFlavor as MergedFlavor).applicationId = this.applicationId + ".custom"

    val beforeAssemble = project.tasks.register("beforeAssemble$variantCapitalizedName") {
        doFirst {
            logger.lifecycle("beforeAssemble$variantCapitalizedName is running...")
        }
    }
    this.assembleProvider.configure {
        dependsOn(beforeAssemble)
    }
}

// 修改apk包地址
android.applicationVariants.configureEach {
    val appVariant = this
    this.outputs.forEach { output ->
        val file = output.outputFile
        if (file.extension == "apk") {
            val out = File(file.parentFile, "custom-${this.versionName}")
            println("RenameApkFile,outpath:${out.absolutePath}")
            val renameApkTask = project.tasks.register<RenameApkFile>(
                "rename${this.name.capitalized()}BuildGradleApk",
            ) {
                inputApk = file
                outputApk = out
                dependsOn(appVariant.assembleProvider)
            }
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
        println("RenameApkFile,taskaction")
    }
}
