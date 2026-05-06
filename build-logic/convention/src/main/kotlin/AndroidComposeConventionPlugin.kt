/**
 * Android Compose 约定插件
 * 为使用 Compose 的模块提供统一的 Compose 配置与依赖
 */

import cn.cqautotest.sunnybeach.androidExtension
import cn.cqautotest.sunnybeach.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // 应用 Kotlin Compose 编译器插件
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            // 通用 Compose 配置
            androidExtension {
                buildFeatures.compose = true
            }

            // 自动化 Compose 基础依赖
            dependencies {
                val bom = libs.findLibrary("compose-bom").get()
                add("implementation", platform(bom))
                add("androidTestImplementation", platform(bom))

                add("implementation", libs.findLibrary("compose-ui").get())
                add("implementation", libs.findLibrary("compose-ui-tooling-preview").get())
                add("debugImplementation", libs.findLibrary("compose-ui-tooling").get())
                add("implementation", libs.findLibrary("compose-material3").get())
                add("implementation", libs.findLibrary("compose-activity").get())
                add("implementation", libs.findLibrary("androidx-lifecycle-viewmodel-compose").get())
            }
        }
    }
}
