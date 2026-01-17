/**
 * Kotlin Android 通用配置
 * 提供 Android 模块的基础配置函数
 */

package cn.cqautotest.sunnybeach

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * 配置 Kotlin Android 基础选项
 * 适配 AGP 9.0：彻底分离 Application 和 Library 的配置逻辑
 */
internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension) {
    commonExtension.compileSdk = 36

    when (commonExtension) {
        is ApplicationExtension -> {
            commonExtension.run {
                defaultConfig {
                    minSdk = 26
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_21
                    targetCompatibility = JavaVersion.VERSION_21
                }
                buildFeatures {
                    viewBinding = true
                    resValues = true
                }
                sourceSets.getByName("main") {
                    jniLibs {
                        directories += "libs"
                    }
                }
                buildTypes {
                    getByName("debug") {}
                    create("preview") {}
                    getByName("release") {}
                }
                lint {
                    disable += setOf("HardcodedText", "ContentDescription")
                }
            }
        }

        is LibraryExtension -> {
            commonExtension.run {
                defaultConfig {
                    minSdk = 26
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_21
                    targetCompatibility = JavaVersion.VERSION_21
                }
                buildFeatures {
                    viewBinding = true
                    resValues = true
                }
                sourceSets.getByName("main") {
                    jniLibs {
                        directories += "libs"
                    }
                }
                buildTypes {
                    getByName("debug") {}
                    create("preview") {}
                    getByName("release") {}
                }
                lint {
                    disable += setOf("HardcodedText", "ContentDescription")
                }
            }
        }
    }

    configureKotlin()
}

/**
 * 配置 Kotlin 编译选项
 */
internal fun Project.configureKotlin() {
    // 配置 Kotlin 编译选项
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            freeCompilerArgs.add("-Xcontext-parameters")
        }
    }
}

/**
 * 配置 Java 工具链
 */
internal fun Project.configureJavaToolchain() {
    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(org.gradle.jvm.toolchain.JavaLanguageVersion.of(21))
        }
    }
}
