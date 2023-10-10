package com.hellokai.androidworkspace.buildsrc

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

/**
 * 1.先定义
 * 2.去build.gradle中注册
 * 3.类似原来的META-INF中手动写的方式，它会自动生成的
 */
class LibConventionBinaryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        println("The lib-convention-binary-plugin from ./buildSrc is applied")

        val android = target.extensions.getByType<LibraryExtension>()
        android.lint.abortOnError = false
    }

}