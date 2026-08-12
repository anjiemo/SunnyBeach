# AGP Roadmap 与依赖版本审计

## 结论

用户提供的 Android 官方 Roadmap 页面将 AGP 10.0 标记为 2026 年晚些时候的计划版本，并明确建议先升级到最新 AGP 9.x、修复弃用警告，再迁移到 AGP 10.0。因此本次采用当前稳定的 AGP 9.3.1，不采用 AGP 10 预览或路线图版本。

AGP 9.3.0 官方发布说明给出的兼容要求是 Gradle 最低 9.5.0、默认 9.5.0、JDK 最低 17，最高支持 API 37。项目当前使用 JDK 21、compileSdk 37，满足这些要求；Gradle wrapper 从 9.6.0 升级到当前稳定 9.7.0。

## 版本核验

通过 Android CLI 版本查询尝试、Google Maven/Maven Central 元数据和 Gradle 依赖报告交叉核验。Android CLI 的 `studio version-lookup` 因当前 Android Studio 实例未被 CLI 识别而不可用；这不影响 Maven 元数据和 Gradle 解析结果。当前 Android Studio 安装版本为 `AI-262.9437.185.2621.16058404`。

已确认可升级并保持稳定坐标的版本：

* AGP `9.2.1` -> `9.3.1`
* Kotlin Gradle Plugin / metadata `2.4.0` -> `2.4.10`
* KSP `2.3.9` -> `2.3.11`
* Hilt `2.59.2` -> `2.60.1`
* Gradle wrapper `9.6.0` -> `9.7.0`
* Compose BOM `2026.06.00` -> `2026.06.01`
* Media3 `1.10.1` -> `1.11.0`
* Glide `5.0.7` -> `5.0.9`
* WeChat SDK `6.8.34` -> `6.8.40`
* MMKV `2.4.0` -> `2.4.1`
* Umeng Common `9.9.2` -> `9.9.8`
* BaseRecyclerViewAdapterHelper `4.4.0` -> `4.4.1`
* Hidden API Bypass `4.3` -> `6.1`
* Android Studio LeakCanary artifact dynamic version `+` -> pinned stable `1.0.0`

抽查的 AndroidX、Material、Coroutines、OkHttp、Retrofit、Gson、Coil、Lottie、测试库和 LeakCanary 主库已经是当前稳定元数据版本，暂不修改。JitPack 坐标中没有明确安全的新稳定版本，或扫描结果存在版本排序/仓库元数据歧义的依赖不升级，避免误降级或引入未验证版本。

## 源码兼容性回归

尝试升级但已回退的版本：

* GsonFactory `10.5` -> `10.8`：`GsonFactory 10.8` 移除了项目在 `AppApplication.kt` 中使用的 `ParseExceptionCallback` 与 `setParseExceptionCallback`，会导致 Kotlin 编译失败；在不修改业务源码的约束下保留 `10.5`。
* ImmersionBar `3.2.2` -> `3.3.3`：`ImmersionBar 3.3.3` 改变了 `setTitleBar` 的参数类型，项目 `HomeFragment.kt` 现有调用传入的可空 `HomeActivity` 无法编译；在不修改业务源码的约束下保留 `3.2.2`。

这两项不能仅凭 Maven 元数据升级；以后需要单独安排源码迁移和行为回归，不能与纯依赖版本批量升级混在一起。

## 最终验证结果

`gradlew help`、`gradlew build --dry-run`、`:app:dependencies --configuration releaseRuntimeClasspath` 和 `:app:assembleDebug` 均成功。真实 Debug 构建完成 APK 打包，说明保留的升级与现有源码兼容。

`:app:lintDebug` 未通过，但报告的 4 个错误均位于现有业务源码：`BrowserActivity.onBackPressed` 的预测返回迁移，以及 `HomeActivity`/`MessageFragment` 的 Material badge 实验性 API opt-in。按照本次只升级依赖、不修改业务源码的范围，这些问题不在本次修复；同时保留 403 条已有/非阻断警告作为后续技术债记录。

## 验证基线

在修改前使用 Gradle 9.6.0/JDK 21 执行 `gradlew help`、`app:dependencies --configuration releaseRuntimeClasspath` 和各模块 `buildEnvironment`，均成功。升级后必须重新执行 `help`、`build --dry-run`，并至少重新解析 release runtime classpath。

## 参考

* https://developer.android.com/build/releases/gradle-plugin-roadmap?hl=en#agp-10
* https://developer.android.com/build/releases/agp-9-3-0-release-notes
* https://services.gradle.org/versions/current
