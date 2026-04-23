/**
 * Android Compose 约定插件
 * 为使用 Compose 的模块提供统一的 Compose 配置
 */

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // 应用 Kotlin Compose 编译器插件
            apply(plugin = "org.jetbrains.kotlin.plugin.compose")

            // 根据模块类型配置 Compose
            pluginManager.withPlugin("com.android.application") {
                extensions.configure<ApplicationExtension> {
                    buildFeatures {
                        // 启用 Compose
                        compose = true
                    }
                }
            }

            pluginManager.withPlugin("com.android.library") {
                extensions.configure<LibraryExtension> {
                    buildFeatures {
                        // 启用 Compose
                        compose = true
                    }
                }
            }
        }
    }
}
