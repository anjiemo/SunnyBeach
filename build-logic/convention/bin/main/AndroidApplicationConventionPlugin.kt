/**
 * Android Application 约定插件
 * 为 app 模块提供统一的 Application 配置
 */

import cn.cqautotest.sunnybeach.configureKotlinAndroid
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // 应用 Android Application 插件
            apply(plugin = "com.android.application")

            // 配置 Android Application 扩展
            extensions.configure<ApplicationExtension> {
                // 应用通用 Kotlin Android 配置
                configureKotlinAndroid(this)
                // 设置 targetSdk
                defaultConfig.targetSdk = 33
                // 只有 app 模块才生成 BuildConfig
                buildFeatures {
                    buildConfig = true
                }
            }
        }
    }
}
