/**
 * Project 扩展函数
 * 提供对 Version Catalog 的便捷访问
 */

package cn.cqautotest.sunnybeach

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/**
 * Version Catalog 扩展属性
 * 用于在约定插件中方便地访问 libs.versions.toml 中定义的依赖
 */
val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

/**
 * Android 扩展配置助手
 * 统一处理 Application 与 Library 模块的配置
 */
fun Project.androidExtension(configure: CommonExtension.() -> Unit) {
    @Suppress("UNCHECKED_CAST")
    extensions.configure(CommonExtension::class.java, configure)
}


