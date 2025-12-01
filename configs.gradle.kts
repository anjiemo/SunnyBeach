/**
 * 项目配置文件
 */

// 测试服
val SERVER_TYPE_TEST = "test"
// 预发布服
val SERVER_TYPE_PREVIEW = "pre"
// 正式服
val SERVER_TYPE_PRODUCT = "product"

val taskName = gradle.startParameter.taskNames.firstOrNull() ?: ""
// 打印当前执行的任务名称
println("GradleLog TaskNameOutput $taskName")

var serverType = SERVER_TYPE_PRODUCT

when {
    taskName.endsWith("Debug") -> serverType = SERVER_TYPE_TEST
    taskName.endsWith("Preview") -> serverType = SERVER_TYPE_PREVIEW
}

// 从 Gradle 命令中读取参数配置，例如：./gradlew assembleRelease -P ServerType="test"
if (project.hasProperty("ServerType")) {
    serverType = project.properties["ServerType"] as String
}

// 打印当前服务器配置
println("GradleLog ServerTypeOutput $serverType")

// 友盟 AppKey
extra["UMENG_APP_KEY"] = "60c8883fe044530ff0a58a52"
// 微信 AppId
extra["WX_APP_ID"] = "wxd35706cc9f46114c"
// 微信 Secret
extra["WX_APP_SECRET"] = "0c8c7cf831dd135a32b3e395ea459b5a"

when (serverType) {
    SERVER_TYPE_TEST, SERVER_TYPE_PREVIEW -> {
        extra["LOG_ENABLE"] = true
        extra["BUGLY_ID"] = "6ce2807170"
        extra["HOST_URL"] = if (serverType == SERVER_TYPE_PREVIEW) {
            "https://www.pre.baidu.com/"
        } else {
            "https://www.test.baidu.com/"
        }
    }

    SERVER_TYPE_PRODUCT -> {
        extra["LOG_ENABLE"] = false
        extra["BUGLY_ID"] = "6ce2807170"
        extra["HOST_URL"] = "https://www.baidu.com/"
    }
}
