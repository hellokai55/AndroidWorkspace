/**
 * 如果你的预编译插件是在 buildSrc 目录下创建的，那么无需为其添加 package 语句，
 * 因为 Gradle 会基于这些脚本文件的物理位置来决定其包路径和命名。
 * 实际上，对于 buildSrc 下的预编译插件，其文件路径决定了它的包名和引用名
 */

plugins {
    id("com.android.library")
}

android {
    lint {
        abortOnError = false
    }
}

println("The lib-convention-script-plugin from ./buildSrc is applied.")