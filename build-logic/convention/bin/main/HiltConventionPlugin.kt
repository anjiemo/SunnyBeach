/**
 * Hilt 依赖注入约定插件
 * 为使用 Hilt 的模块提供统一的 Hilt 配置
 */

import cn.cqautotest.sunnybeach.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // 应用 KSP 插件
            apply(plugin = "com.google.devtools.ksp")

            // 添加 Hilt 相关依赖
            dependencies {
                add("ksp", libs.findLibrary("hilt-compiler").get())
            }

            // 针对 Android 模块的特殊配置
            // 兼容性提醒：在 AGP 9.0 + newDsl=false 时，Hilt 需要 BaseExtension
            pluginManager.withPlugin("com.android.application") {
                apply(plugin = "dagger.hilt.android.plugin")
                dependencies {
                    add("implementation", libs.findLibrary("hilt-android").get())
                }
            }
            pluginManager.withPlugin("com.android.library") {
                apply(plugin = "dagger.hilt.android.plugin")
                dependencies {
                    add("implementation", libs.findLibrary("hilt-android").get())
                }
            }
        }
    }
}
