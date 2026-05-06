package cn.cqautotest.sunnybeach

import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra

/**
 * 项目全局配置逻辑
 */
fun Project.configureProjectConfig() {
    // 测试服
    val serverTypeTest = "test"
    // 预发布服
    val serverTypePreview = "pre"
    // 正式服
    val serverTypeProduct = "product"

    val taskNames = gradle.startParameter.taskNames
    val taskName = taskNames.firstOrNull() ?: ""
    // 打印当前执行的任务名称
    println("GradleLog TaskNameOutput $taskName")

    var serverType = serverTypeProduct

    when {
        taskName.endsWith("Debug", ignoreCase = true) -> serverType = serverTypeTest
        taskName.endsWith("Preview", ignoreCase = true) -> serverType = serverTypePreview
    }

    // 从 Gradle 命令中读取参数配置，例如：./gradlew assembleRelease -P ServerType="test"
    if (hasProperty("ServerType")) {
        serverType = property("ServerType") as String
    }

    // 打印当前服务器配置
    println("GradleLog ServerTypeOutput $serverType")

    // 存储到 extra 供全局使用
    extra["UMENG_APP_KEY"] = "60c8883fe044530ff0a58a52"
    extra["WX_APP_ID"] = "wxd35706cc9f46114c"
    extra["WX_APP_SECRET"] = "0c8c7cf831dd135a32b3e395ea459b5a"

    when (serverType) {
        serverTypeTest, serverTypePreview -> {
            extra["LOG_ENABLE"] = true
            extra["BUGLY_ID"] = "6ce2807170"
            extra["HOST_URL"] = if (serverType == serverTypePreview) {
                "https://www.pre.baidu.com/"
            } else {
                "https://www.test.baidu.com/"
            }
        }

        serverTypeProduct -> {
            extra["LOG_ENABLE"] = false
            extra["BUGLY_ID"] = "6ce2807170"
            extra["HOST_URL"] = "https://www.baidu.com/"
        }
    }
}
