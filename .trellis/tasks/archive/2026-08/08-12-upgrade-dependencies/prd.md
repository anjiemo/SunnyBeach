# 升级 Android 构建与项目依赖

## Goal

在不触碰任务开始前已有未提交文件的前提下，升级当前 Android 项目的 AGP、Kotlin、Gradle wrapper 以及可安全升级的声明依赖，并通过 Gradle 构建验证。

## Requirements

* 保护任务开始前已存在的未跟踪路径：`mapFile`、`modify_config.json`、`other/prototype/`、`web-articles/`。
* 优先使用当前工程结构、Version Catalog、约定插件和 Android CLI 可获得的版本信息。
* 仅修改依赖声明、构建工具版本及为兼容升级所必需的 Gradle 配置；不修改业务源码或保护路径。
* 版本升级必须基于可解析的仓库元数据或 Gradle 依赖报告，并在升级后验证构建。

## Acceptance Criteria

* [ ] 保护路径在变更前后保持原状，且不出现在本次依赖升级改动中。
* [ ] AGP、Kotlin、Gradle wrapper 和可安全升级的依赖已更新到当前仓库可用的稳定版本；不能安全升级的依赖有明确记录。
* [ ] `gradlew help` 成功。
* [ ] `gradlew build --dry-run` 成功；条件允许时完成实际构建或说明阻塞原因。
* [ ] Gradle 依赖解析没有因版本升级产生新的配置错误。

## Definition of Done

* 变更保持最小范围，未覆盖用户已有未提交内容。
* 完成构建与依赖解析验证。
* 记录版本选择、兼容性限制和剩余风险。

## Out of Scope

* 不升级或重构业务代码、资源、Manifest、发布签名和本地未跟踪资料。
* 不删除、移动、覆盖或添加 Git 忽略规则来处理已有未提交文件。
* 不自动提交或推送代码。

## Technical Notes

* 当前工程使用 `gradle/libs.versions.toml`，AGP `9.2.1`、Kotlin `2.4.0`、KSP `2.3.9`、Hilt `2.59.2`、Gradle `9.6.0`。
* 根工程和 `build-logic/convention` 已通过 JDK 21/Gradle 9.6.0 的 `help` 基线验证。
* `settings.gradle.kts` 要求 JDK 21；约定插件集中管理 Android 编译配置。
* Android Studio CLI 检查未识别正在运行的 Studio 实例，因此版本核验需要结合 Android CLI 可用信息、Maven 元数据和 Gradle 解析结果。
