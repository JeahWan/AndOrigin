// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        //agp、kotlin
        classpath("com.android.tools.build:gradle:7.0.4")
        classpath(kotlin("gradle-plugin", version = "1.5.31"))
        //booster
        val booster = "com.didiglobal.booster"
        val version = "4.4.0"
        listOf(
            "booster-gradle-plugin",
            //性能优化
            "booster-transform-thread",
            "booster-transform-webview",
//            "booster-transform-shared-preferences",//导致concurrentModificationException
            //修复bug
            "booster-transform-toast",
            "booster-transform-res-check",
            "booster-transform-media-player",
            "booster-transform-activity-thread",
            "booster-transform-finalizer-watchdog-daemon",
            //查看权限、动态库清单 gradlew listPermissions/listSharedLibraries
            "booster-task-list-permission",
            "booster-task-list-shared-library",
            //资源压缩 暂时不可用
//            "booster-transform-r-inline",//会导致context空 类型转换错误等问题
//            "booster-transform-br-inline",
//            "booster-task-resource-deredundancy",
//            "booster-task-compression-processed-res",
        ).forEach { classpath(booster, it, version) }
    }
}

allprojects {
    repositories {
        //todo 等依赖库都迁移了 移除
        jcenter()
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
    //skip Test tasks
    gradle.taskGraph.whenReady {
        tasks.forEach(action = {
            if (it.name.contains("Test")) {
                it.enabled = false
            }
        })
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}