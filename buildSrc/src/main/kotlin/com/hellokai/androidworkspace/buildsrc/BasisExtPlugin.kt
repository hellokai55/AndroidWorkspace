package com.hellokai.androidworkspace.buildsrc

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 打印引用者的implementation集合依赖
 */
class BasisExtPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        println("BasisExtPlugin apply")
        target.afterEvaluate {
            target.configurations.first { cf ->
                cf.name == "implementation"
            }.let {cf ->
                println("${cf.name} = ${cf.dependencies.size}")
                cf.dependencies.map { println(it.toString()) }
            }
        }
    }

}