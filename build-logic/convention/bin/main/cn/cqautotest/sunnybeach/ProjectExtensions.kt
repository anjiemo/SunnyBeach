/**
 * Project 扩展函数
 * 提供对 Version Catalog 的便捷访问
 */

package cn.cqautotest.sunnybeach

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
