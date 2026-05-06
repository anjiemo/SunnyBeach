/**
 * Android Application 约定插件
 * 为 app 模块提供统一的 Application 配置
 */

import cn.cqautotest.sunnybeach.configureCommonDependencies
import cn.cqautotest.sunnybeach.configureKotlinAndroid
import cn.cqautotest.sunnybeach.libs
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // 应用 Android Application 插件
            pluginManager.apply("com.android.application")

            // 配置 Android Application 扩展
            extensions.configure<ApplicationExtension> {
                // 应用通用 Kotlin Android 配置
                configureKotlinAndroid(this)
                // 设置 targetSdk
                defaultConfig.targetSdk = 36
                // 只有 app 模块才生成 BuildConfig
                buildFeatures {
                    buildConfig = true
                }
            }

            // 应用基础依赖
            dependencies {
                configureCommonDependencies(libs)
            }
        }
    }
}
