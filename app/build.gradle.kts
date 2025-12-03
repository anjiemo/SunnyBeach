import AppConfigUtils.printAppConfig
import groovy.json.JsonSlurper
import groovy.xml.XmlParser
import java.io.FileInputStream
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Properties

// 将 Date.format 放在顶部，它不依赖 Project 或 Task
fun Date.format(pattern: String): String {
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(this)
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    // 华为 AGConnect 插件（使用字符串 ID）
    id("com.huawei.agconnect")
    // AndroidAOP 插件（使用字符串 ID）
    id("android.aop")
    // 此版本与 Kotlin 版本匹配
    alias(libs.plugins.kotlin.compose)
}

// Android 代码规范文档：https://github.com/getActivity/AndroidCodeStandard
android {

    // 设置命名空间：https://developer.android.com/build/configure-app-module#set-namespace
    namespace = "cn.cqautotest.sunnybeach"

    // 从 gradle.properties 读取签名配置
    val keystorePropertiesFile = file("gradle.properties")
    val keystoreProperties = Properties()
    if (keystorePropertiesFile.exists()) {
        keystoreProperties.load(FileInputStream(keystorePropertiesFile))
    }
    val storeFile = keystoreProperties.getProperty("StoreFile")
    val storePassword = keystoreProperties.getProperty("StorePassword")
    val keyAlias = keystoreProperties.getProperty("KeyAlias")
    val keyPassword = keystoreProperties.getProperty("KeyPassword")

    // Apk 签名的那些事：https://www.jianshu.com/p/a1f8e5896aa2
    signingConfigs {
        if (storeFile != null && storePassword != null && keyAlias != null && keyPassword != null) {
            create("config") {
                this.storeFile = file(storeFile)
                this.storePassword = storePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            }
        }
    }

    // 构建配置：https://developer.android.google.cn/studio/build/build-variants
    buildTypes {
        debug {
            // 给包名添加后缀
            applicationIdSuffix = ".debug"
            // 调试模式开关
            isDebuggable = true
            isJniDebuggable = true
            // 移除无用的资源
            isShrinkResources = false
            // 代码混淆开关
            isMinifyEnabled = false
            // 签名信息配置
            signingConfig = signingConfigs.getByName("config")
            // 添加清单占位符
            manifestPlaceholders["app_name"] = "阳光沙滩 Debug 版"
            // 调试模式下只保留一种架构的 so 库，提升打包速度
            ndk {
                // abiFilters += "armeabi-v7a"
                // 为方便在模拟器上调试，添加了 arm64-v8a 架构的 so 库
                abiFilters += listOf("armeabi-v7a", "arm64-v8a")
            }
        }

        getByName("preview") {
            initWith(getByName("debug"))
            applicationIdSuffix = ""
            // 添加清单占位符
            manifestPlaceholders["app_name"] = "阳光沙滩 Preview 版"
        }

        release {
            // 调试模式开关
            isDebuggable = false
            isJniDebuggable = false
            // 移除无用的资源
            isShrinkResources = true
            // 代码混淆开关
            isMinifyEnabled = true
            // 签名信息配置
            signingConfig = signingConfigs.getByName("config")
            // 添加清单占位符
            manifestPlaceholders["app_name"] = "@string/app_name"
            // 仅保留两种架构的 so 库，根据 Bugly 统计得出
            ndk {
                // armeabi：万金油架构平台（占用率：0%）
                // armeabi-v7a：曾经主流的架构平台（占用率：10%）
                // arm64-v8a：目前主流架构平台（占用率：95%）
                abiFilters += listOf("armeabi-v7a", "arm64-v8a")
            }
        }
    }

    // 资源目录存放指引：https://developer.android.google.cn/guide/topics/resources/providing-resources
    defaultConfig {
        // 无痛修改包名：https://www.jianshu.com/p/17327e191d2e
        applicationId = "cn.cqautotest.sunnybeach"

        // 仅保留 xxhdpi 图片资源（目前主流分辨率 1920 * 1080）
        resConfigs("xxhdpi")

        // 混淆配置
        proguardFiles("proguard-sdk.pro", "proguard-app.pro", "removeLog.pro")

        // 日志打印开关
        buildConfigField("boolean", "LOG_ENABLE", "${rootProject.extra["LOG_ENABLE"]}")
        // 测试包下的 BuglyId
        buildConfigField("String", "BUGLY_ID", "\"${rootProject.extra["BUGLY_ID"]}\"")
        // 测试服务器的主机地址
        buildConfigField("String", "HOST_URL", "\"${rootProject.extra["HOST_URL"]}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // 仅保留中文语种的资源
    androidResources {
        localeFilters += listOf("zh", "zh-rCN")
    }

    // 剔除这个包下的所有文件（不会移除签名信息）
    packaging {
        jniLibs {
            excludes += "META-INF/*******"
        }
        resources {
            excludes += "META-INF/*******"
        }
    }

    buildFeatures {
        compose = true // 启用 Compose
    }

    applicationVariants.all {
        // apk 输出文件名配置
        outputs.all {
            val output = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            val variantName = name
            val buildTypeName = buildType.name
            var outputFileName = "${rootProject.name}_v${versionName}_${buildTypeName}"
            if (buildTypeName == "release") {
                outputFileName += "_${Date().format("MMdd")}"
            }
            outputFileName += ".apk"
            output.outputFileName = outputFileName
        }
    }
}

object AppConfigUtils {

    /**
     * 从 strings.xml 中读取特定的字符串值
     */
    private fun getAppNameFromStrings(project: Project): String? {
        // 假设 strings.xml 位于 app 模块的默认位置
        val stringsFile = project.file("src/main/res/values/strings.xml")

        if (!stringsFile.exists()) {
            println("警告: strings.xml 文件不存在于 ${stringsFile.absolutePath}")
            return null
        }

        return try {
            val parser = XmlParser(false, false)
            // 显式将解析结果转换为 groovy.util.Node 类型，增强 IDE 识别
            val xml = parser.parse(stringsFile) as groovy.util.Node

            // 查找 name="app_name" 的 <string> 标签
            val appNameNode = xml.children().find {
                // 1. 强制将 Any? 类型的迭代元素安全转换为 Node
                val node = it as? groovy.util.Node ?: return@find false

                // 2. 现在 node.name() 和 node.attributes() 可以被识别
                node.name() == "string" && node.attributes()["name"] == "app_name"
            }

            // 返回节点内容，并去除可能的空格
            if (appNameNode != null) {
                // 3. 对找到的元素进行最终类型转换并访问 text()
                (appNameNode as groovy.util.Node).text()?.trim()
            } else {
                null
            }
        } catch (e: Exception) {
            println("错误: 解析 strings.xml 失败: ${e.message}")
            // 打印异常堆栈，方便调试文件读取或解析错误
            // e.printStackTrace()
            null
        }
    }

    fun Project.printAppConfig(variantName: String) {
        val appName = getAppNameFromStrings(project) ?: project.name
        val outputDirPath = layout.projectDirectory.dir(variantName).asFile.absolutePath

        val apkConfig = File("$outputDirPath${File.separator}output-metadata.json").readText()
        val apkConfigJson = JsonSlurper().parseText(apkConfig) as Map<*, *>
        val elements = apkConfigJson["elements"] as List<*>
        val outputConfig = elements[0] as Map<*, *>
        val outputFileName = outputConfig["outputFile"] as String
        val configVersionName = outputConfig["versionName"] as String
        val versionCode = outputConfig["versionCode"] as Int
        val apkFile = File(outputDirPath + File.separator + outputFileName)
        val apkMd5 = generateMD5(apkFile)
        val combinedVersionName = "$appName$configVersionName"

        println("apkConfig：===> variantName is $variantName combinedVersionName is $combinedVersionName")
        println("apkConfig：===> variantName is $variantName versionName is $configVersionName")
        println("apkConfig：===> variantName is $variantName versionCode is $versionCode")
        println("apkConfig：===> variantName is $variantName apkSize     is ${apkFile.length()}")
        println("apkConfig：===> variantName is $variantName apkHash     is $apkMd5")

        // 生成 appConfig.json
        val appConfigMap = mapOf(
            "versionName" to combinedVersionName,
            "versionCode" to versionCode,
            "apkSize" to apkFile.length(),
            "apkHash" to apkMd5
        )
        val appConfigJsonString = groovy.json.JsonOutput.toJson(appConfigMap)
        val appConfigPrettyJsonString = groovy.json.JsonOutput.prettyPrint(appConfigJsonString, true)

        val appConfigFile = File(outputDirPath + File.separator + "appConfig.json")
        appConfigFile.writeText(appConfigPrettyJsonString)
        println("apkConfig：===> appConfig.json generated at ${appConfigFile.absolutePath}")
    }

    fun generateMD5(file: File): String {
        return file.inputStream().use { inputStream ->
            val digest = MessageDigest.getInstance("MD5")
            val buffer = ByteArray(8192)
            var read: Int
            while (inputStream.read(buffer).also { read = it } > 0) {
                digest.update(buffer, 0, read)
            }
            digest.digest().joinToString("") { "%02x".format(it) }
        }
    }
}

tasks.register("printReleaseAppConfig") {
    doLast {
        project.printAppConfig("release")
    }
}

tasks.register("printPreviewAppConfig") {
    doLast {
        project.printAppConfig("preview")
    }
}

tasks.register("printDebugAppConfig") {
    doLast {
        project.printAppConfig("debug")
    }
}

// MiPush 接入文档：https://dev.mi.com/console/doc/detail?pId=41#_0_0
// 添加构建依赖项：https://developer.android.google.cn/studio/build/dependencies
// api 与 implementation 的区别：https://www.jianshu.com/p/8962d6ba936e
dependencies {
    val pagingVersion = "3.3.6"
    val navVersion = "2.9.5"
    val emoji2Version = "1.0.0-beta01"
    val roomVersion = "2.8.3"
    val workVersion = "2.11.0"
    val markwonVersion = "4.6.2"
    val prismVersion = "2.0.0"
    val qmuiArchVersion = "2.0.1"
    val ariaVersion = "3.8.16"

    // 基类封装
    implementation(project(":library:base"))
    // 控件封装
    implementation(project(":library:widget"))
    // 友盟封装
    implementation(project(":library:umeng"))
    // 网络封装
    implementation(project(":library:network"))

    // 友盟公共库
    implementation(libs.umeng.common)
    implementation(libs.umeng.asms)
    // 友盟分享库
    implementation(libs.umeng.share.core)
    // 友盟微信分享
    implementation(libs.umeng.share.wx)

    // 微信组件：https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Access_Guide/Android.html
    implementation(libs.wechat.sdk)

    // 单元测试
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Android Core：https://developer.android.com/jetpack/androidx/releases/core?hl=zh-cn
    implementation(libs.androidx.core.ktx)
    // Activity ktx：https://developer.android.com/jetpack/androidx/releases/activity
    implementation(libs.androidx.activity.ktx)
    // Fragment ktx：https://developer.android.com/jetpack/androidx/releases/fragment
    implementation(libs.androidx.fragment.ktx)

    // Room 数据库：https://developer.android.com/jetpack/androidx/releases/room
    // CodeLab：https://developer.android.com/codelabs/android-room-with-a-view-kotlin#0
    implementation(libs.androidx.room.runtime)
    // 使用 Kotlin 注解处理工具
    ksp(libs.androidx.room.compiler)
    // 对 Room 的 Kotlin 扩展和协程支持
    implementation(libs.androidx.room.ktx)

    // Paging 分页库：https://developer.android.google.cn/jetpack/androidx/releases/paging
    implementation(libs.androidx.paging.runtime.ktx)
    // Navigation 导航：https://developer.android.com/jetpack/androidx/releases/navigation?hl=zh-cn
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    // Feature module Support
    implementation(libs.androidx.navigation.dynamic.features)
    // Emoji2：https://developer.android.google.cn/jetpack/androidx/releases/emoji2
    // implementation("androidx.emoji2:emoji2:$emoji2Version")
    // implementation("androidx.emoji2:emoji2-views:$emoji2Version")
    // implementation("androidx.emoji2:emoji2-views-helper:$emoji2Version")
    // WorkManager（Kotlin + coroutines）：https://developer.android.google.cn/jetpack/androidx/releases/work
    implementation(libs.androidx.work.runtime.ktx)

    // 权限请求框架：https://github.com/getActivity/XXPermissions
    implementation(libs.xxpermissions)

    // 标题栏框架：https://github.com/getActivity/TitleBar
    implementation(libs.titlebar)

    // 吐司框架：https://github.com/getActivity/Toaster
    implementation(libs.toaster)

    // 网络请求框架：https://github.com/getActivity/EasyHttp
    implementation(libs.easyhttp)
    // OkHttp 框架：https://github.com/square/okhttp
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp.logging.interceptor)
    // Android网络请求库：https://github.com/square/retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // Json 解析框架：https://github.com/google/gson
    implementation(libs.gson)
    // Gson 解析容错：https://github.com/getActivity/GsonFactory
    implementation(libs.gson.factory)

    // Shape 框架：https://github.com/getActivity/ShapeView
    implementation(libs.shapeview)

    // AndroidAOP：https://github.com/FlyJingFish/AndroidAOP
    implementation(libs.androidaop.core)
    implementation(libs.androidaop.extra)
    ksp(libs.androidaop.apt)

    // 图片加载框架：https://github.com/bumptech/glide
    // 官方使用文档：https://github.com/Muyangmin/glide-docs-cn
    implementation(libs.glide)
    implementation(libs.glide.okhttp3.integration)
    ksp(libs.glide.compiler)

    // 沉浸式框架：https://github.com/gyf-dev/ImmersionBar
    implementation(libs.immersionbar)
    // kotlin扩展（可选）
    implementation(libs.immersionbar.ktx)

    // 手势 ImageView：https://github.com/Baseflow/PhotoView
    implementation(libs.photoview)

    // Bugly 异常捕捉：https://bugly.qq.com/docs/user-guide/instruction-manual-android/?v=20190418140644
    implementation(libs.bugly.crashreport)
    implementation(libs.bugly.nativecrashreport)

    // 动画解析库：https://github.com/airbnb/lottie-android
    // 动画资源：https://lottiefiles.com、https://icons8.com/animated-icons
    implementation(libs.lottie)

    // 上拉刷新下拉加载框架：https://github.com/scwang90/SmartRefreshLayout
    implementation(libs.smartrefresh.kernel)
    implementation(libs.smartrefresh.header.material)
    implementation(libs.smartrefresh.header.classics)
    implementation(libs.smartrefresh.header.two.level)

    // 日志打印框架：https://github.com/JakeWharton/timber
    implementation(libs.timber)

    // 指示器框架：https://github.com/ongakuer/CircleIndicator
    implementation(libs.circleindicator)

    // 腾讯 MMKV：https://github.com/Tencent/MMKV
    implementation(libs.mmkv)

    // 内存泄漏监测框架：https://github.com/square/leakcanary
    debugImplementation(libs.leakcanary)
    "previewImplementation"(libs.leakcanary)

    // 多语种：https://github.com/getActivity/MultiLanguages
    // 悬浮窗：https://github.com/getActivity/XToast
    // 日志输出：https://github.com/getActivity/Logcat
    // 工具类：https://github.com/Blankj/AndroidUtilCode
    implementation(libs.utilcodex)
    // 轮播图：https://github.com/bingoogolapple/BGABanner-Android
    // 二维码：https://github.com/bingoogolapple/BGAQRCode-Android
    // 跑马灯：https://github.com/sunfusheng/MarqueeView
    // 对象注解：https://www.jianshu.com/p/f1f888e4a35f
    // 平板适配：https://github.com/JessYanCoding/AndroidAutoSize
    // 图片压缩：https://github.com/Curzibn/Luban
    implementation(libs.luban)
    // 对象注解：https://www.jianshu.com/p/f1f888e4a35f
    // 对象存储：https://github.com/leavesC/DoKV
    // 第三方支付：https://github.com/Cuieney/RxPay
    // 多渠道打包：https://github.com/Meituan-Dianping/walle
    // 设备唯一标识：http://msa-alliance.cn/col.jsp?id=120
    // 嵌套滚动容器：https://github.com/donkingliang/ConsecutiveScroller
    // 隐私调用监控：https://github.com/huage2580/PermissionMonitor

    // 一个强大并且灵活的RecyclerViewAdapter：https://github.com/CymChad/BaseRecyclerViewAdapterHelper
    implementation(libs.baserecyclerviewadapter)
    // 轮播图库：https://github.com/youth5201314/banner
    implementation(libs.banner)

    // 小米更新SDK：https://dev.mi.com/console/doc/detail?pId=4
    implementation(libs.xiaomi.update)

    // // Markwon是一个适用于 Android 的Markdown库：https://noties.io/Markwon/
    // implementation("io.noties.markwon:core:$markwonVersion")
    // implementation("io.noties.markwon:html:$markwonVersion")
    // // implementation("io.noties.markwon:image:$markwonVersion")
    // implementation("io.noties.markwon:image-glide:$markwonVersion")
    // implementation("io.noties:prism4j:$prismVersion")
    // kapt("io.noties:prism4j-bundler:$prismVersion")
    // implementation("io.noties.markwon:syntax-highlight:$markwonVersion")
    // // 自动生成语法高亮类的注解处理器
    // annotationProcessor("io.noties:prism4j-bundler:$prismVersion")
    // 支持动画展开和折叠其子视图的 Android 布局类：https://github.com/cachapa/ExpandableLayout
    implementation(libs.expandablelayout)

    // 使用 Android ViewBinding 更简单：https://github.com/androidbroadcast/ViewBindingPropertyDelegate
    implementation(libs.vbpd)
    // Longan 是一个简化 Android 开发的 Kotlin 工具类集合，可以使代码更加简洁易读：https://github.com/DylanCaiCoding/Longan
    implementation(libs.longan)
    // Optional
    implementation(libs.longan.design)
    // 一个简单而友好的Android数据库调试库：https://github.com/guolindev/Glance/
    debugImplementation(libs.glance)
    // 一个帮助键盘平滑过渡到功能面板的框架：https://github.com/YummyLau/PanelSwitchHelper
    // implementation("com.github.YummyLau:PanelSwitchHelper:1.4.0")

    // // skin-support
    // implementation("skin.support:skin-support:4.0.5")
    // // skin-support 基础控件支持
    // implementation("skin.support:skin-support-appcompat:4.0.5")
    // // skin-support-design material design 控件支持[可选]
    // implementation("skin.support:skin-support-design:4.0.5")
    // // skin-support-cardview CardView 控件支持[可选]
    // implementation("skin.support:skin-support-cardview:4.0.5")
    // // skin-support-constraint-layout ConstraintLayout 控件支持[可选]
    // implementation("skin.support:skin-support-constraint-layout:4.0.5")
    // // 提高 Android UI 开发效率的 UI 库：https://github.com/Tencent/QMUI_Android
    // implementation("com.qmuiteam:qmui:$qmuiArchVersion")
    // implementation("com.qmuiteam:arch:$qmuiArchVersion")
    // kapt("com.qmuiteam:arch-compiler:$qmuiArchVersion")
    // 华为统一扫码服务：https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/service-introduction-0000001050041994
    implementation(libs.huawei.agconnect.core)
    implementation(libs.huawei.scan)

    // Hilt依赖注入：https://developer.android.google.cn/jetpack/androidx/releases/hilt
    // 组件层次结构备忘录：https://dagger.dev/hilt/components.html#component-lifetimes
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // // 从图片中提取具有代表性的调色板：https://developer.android.com/jetpack/androidx/releases/palette
    implementation(libs.androidx.palette)

    // Android下打造通用便捷的PopupWindow弹窗库：https://github.com/razerdp/BasePopup
    implementation(libs.basepopup)
    // 功能强大，交互优雅，动画丝滑的通用弹窗：https://github.com/junixapp/XPopup
    implementation(libs.xpopup)

    // 一个强大的🚀Android 图表视图/图形视图库，支持折线图、雷达图、气泡图和烛台图以及缩放、平移和动画：https://github.com/PhilJay/MPAndroidChart
    implementation(libs.mpandroidchart)

    // 浮窗从未如此简单（后续考虑替换）：https://github.com/princekin-f/EasyFloat
    implementation(libs.easyfloat)
    // 悬浮窗框架：https://github.com/getActivity/EasyWindow
    implementation(libs.easywindow)

    // 阿里云视频播放器 SDK：https://help.aliyun.com/document_detail/124711.html?spm=a2c4g.11186623.0.0.7bd24addVQH3VE
    implementation(libs.aliyun.player)

    // implementation("me.laoyuyu.aria:core:$ariaVersion")
    // annotationProcessor("me.laoyuyu.aria:compiler:$ariaVersion")
    // // 如果需要使用ftp，请增加该组件
    // implementation("me.laoyuyu.aria:ftp:$ariaVersion")
    // // 如果需要使用ftp，请增加该组件
    // implementation("me.laoyuyu.aria:sftp:$ariaVersion")
    // // 如果需要使用m3u8下载功能，请增加该组件
    // implementation("me.laoyuyu.aria:m3u8:$ariaVersion")

    // 消息总线，基于LiveData，具有生命周期感知能力：https://github.com/JeremyLiao/LiveEventBus
    implementation(libs.liveeventbus)

    // 使用物料清单：https://developer.android.com/develop/ui/compose/bom?hl=zh-cn#kts
    // BOM 与库版本对应表：https://developer.android.com/develop/ui/compose/bom/bom-mapping?hl=zh-cn
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Compose 库，无需指定版本 (BOM已管理)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview) // 预览支持
    debugImplementation(libs.compose.ui.tooling) // 调试工具
    implementation(libs.compose.material3) // Material 3 组件
    implementation(libs.compose.activity) // 与 Activity 集成
    implementation(libs.androidx.lifecycle.viewmodel.compose) // ViewModel 集成

    // Android 和 Compose 多平台图像加载：https://github.com/coil-kt/coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.coil.gif)
}
