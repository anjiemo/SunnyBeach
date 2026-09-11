# 依赖核查记录

核查日期：2026-09-11。完整 Maven 核查结果见 `versions-audit.json`。

## 已实施升级

| 优先级 | 依赖 | 原版本 | 新版本 |
| --- | --- | --- | --- |
| P0 | Gradle | 9.7.0 | 9.7.1 |
| P0 | AGP | 9.3.1 | 9.4.0 |
| P0 | Kotlin | 2.4.10 | 2.4.20 |
| P0 | KSP | 2.3.11 | 2.3.12 |
| P1 | AppCompat | 1.7.1 | 1.8.0 |
| P1 | Fragment | 1.8.9 | 1.9.0 |
| P1 | Room | 2.8.4 | 2.8.5 |
| P1 | Paging | 3.5.0 | 3.5.1 |
| P1 | Navigation | 2.9.8 | 2.10.1 |
| P1 | Compose BOM | 2026.06.01 | 2026.09.00 |
| P1 | OkHttp | 5.4.0 | 5.5.0 |
| P1 | Coil | 3.5.0 | 3.6.2 |
| P1 | MMKV | 2.4.1 | 2.4.2 |
| P2 | Umeng Common | 9.9.8 | 9.9.9 |
| P2 | EasyHttp | 13.6 | 13.8 |
| P2 | Luban | 2.0.1 | 2.0.2 |
| P3 | actions/checkout | v5 | v7.0.1 |
| P3 | actions/setup-java | v5 | v6.0.1 |
| P3 | gradle/actions/setup-gradle | v5 | v6.3.0 |
| P3 | actions/upload-artifact | v5 | v7.0.1 |
| P3 | softprops/action-gh-release | v2 | v3.0.3 |

Activity Compose 与 Lifecycle ViewModel Compose 显式关联对应 AndroidX 版本，避免依赖传递解析决定版本；Compose BOM 不管理这两个 group。

## 来源与兼容性

- AGP：https://developer.android.com/build/releases/agp-9-4-0-release-notes 。最低 Gradle 9.6.0、JDK 17，支持 API 37；项目使用 JDK 21/API 37。
- Gradle：https://services.gradle.org/versions/current 返回稳定版 9.7.1。
- 腾讯镜像下载包 SHA-256 与 `https://services.gradle.org/distributions/gradle-9.7.1-bin.zip.sha256` 一致，已在 Wrapper 中固定校验值。
- CI Action：分别读取 GitHub `/repos/{owner}/{repo}/releases/latest` 及对应版本 README。现有工作流使用 `ubuntu-latest`、Temurin 21 和标准上传/发布参数，满足 Node 24 runner 要求。
- Gradle Action v6.3.0 升级缓存协议，首次 CI 运行会重新建立缓存。
- Foojay：https://plugins.gradle.org/m2/org/gradle/toolchains/foojay-resolver-convention/org.gradle.toolchains.foojay-resolver-convention.gradle.plugin/maven-metadata.xml ，稳定版仍为 1.0.0。
- Maven Central 官方端点访问失败时使用项目既有阿里云镜像；Google Maven 和 JitPack 优先直接查询。元数据只证明发布情况，兼容性由构建进一步验证。

## 保留项与特殊情况

- Hilt 2.60.1、AndroidAOP 2.7.6 及其余未修改版本：核查未发现更新稳定版，详见 JSON。
- SmartRefresh：最新仍为 3.0.0-alpha，稳定版 2.1.1 更旧，不降级。
- ExpandableLayout：JitPack 元数据仅列出 1.0/1.2；GitHub `cachapa/ExpandableLayout` tags 确认最新 2.9.2，本地 Gradle 缓存也包含该版本，保留。
- Glide：实际位于 Maven Central，补查镜像元数据及 GitHub `bumptech/glide` 最新 release，仍为 5.0.9。
- GsonFactory：首次超时，重试 JitPack 元数据及 GitHub `getActivity/GsonFactory` 最新 release，仍为 10.8。
- Glance：补查 Maven Central 镜像 `com/guolindev/glance/glance/maven-metadata.xml`，仍为 1.1.0。
- `copy/build.gradle` 是未纳入 settings 的历史模板，未声明版本；不属于实际构建模块。
- `app/libs` 内仅有旧版预编译 `.so`，没有可追溯的 Maven 坐标或源码构建流程，无法自动核查/替换；保留并明确列为人工维护项。

## 验证

- 运行 `gradlew.bat :app:assembleDebug :app:lintDebug --console=plain`，结果见 `build.log`。
- 首次因 build-logic 访问 Maven Central 返回 403 失败；补齐本地镜像后 build-logic 编译通过。第二次因根 buildscript 从官方源下载 BouncyCastle 返回 403 失败；将既有镜像移到官方源前，并统一 `CI_BUILD` 分支，继续验证，见 `build-verified.log`。
- actionlint 对全部工作流检查通过（禁用未安装的 shellcheck/pyflakes 外部检查）。
- 不运行项目中会访问生产 API 的开发者测试脚本。
- CI Action 仅做本地配置与上游参数核查；未触发远程工作流或发布。
- 最终 Gradle 输出 `BUILD SUCCESSFUL in 5m 7s`，226 个任务；`assembleDebug` 和 `lintDebug` 均完成。PowerShell 将 Java 写入 stderr 的警告包装为 NativeCommandError，因此外层工具返回 1；以 Gradle 完成状态、APK 和 Lint XML 交叉确认实际成功。
- Lint XML/TXT：0 errors、399 warnings；actionlint 1.7.12：退出码 0；`git diff --check` 通过，所有修改文件末尾换行状态与 HEAD 一致。
- APK：`app/build/outputs/apk/debug/app-debug.apk`，以及既有重命名任务生成的 `app/build/outputs/apk-renamed/debug/SunnyBeach_v5.4.2_debug.apk`。
- 剩余限制：AndroidAOP 2.7.6 在执行期访问 `Task.project`，产生 1 个配置缓存问题；D8 对 AOP 产物提示缺少 stack map table。未阻止 Debug 打包，未通过抑制警告规避问题。尚未进行真机业务回归、Release 签名构建或远程 CI 执行。
