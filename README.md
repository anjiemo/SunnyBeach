# SunnyBeach Android

![Kotlin](https://img.shields.io/badge/Language-Kotlin-brightgreen.svg)
![Android](https://img.shields.io/badge/Platform-Android-blue.svg)
![License](https://img.shields.io/badge/License-Apache%202.0-orange.svg)
![Stars](https://img.shields.io/github/stars/anjiemo/SunnyBeach.svg?style=social)

SunnyBeach 是基于 [阳光沙滩社区开放 API](https://www.sunofbeach.net/) 开发的非官方/社区开源 Android 客户端。项目贯彻 **Kotlin First** 原则，遵循现代 Android 应用架构（Modern Android Architecture）最佳实践。

## Architecture & Tech Stack

本项目采用模块化工程设计与经典的 **MVVM** 架构体系。

### Project Structure
- `:app` - 应用主入口与宿主业务层。
- `:library` - 沉淀的通用 UI 组件与基础核心功能封装。
- `:build-logic` - (基于 Gradle KTS) 统一的构建逻辑与插件管理。

### Tech Stack
- **语言**：[Kotlin](https://kotlinlang.org/) (100% Kotlin First)
- **架构组件**：[Google Jetpack](https://developer.android.google.cn/jetpack) (ViewModel, LiveData 等)
- **UI / 基础组件**：[BRVAH](https://github.com/CymChad/BaseRecyclerViewAdapterHelper) (列表适配器)、[XXPermissions](https://github.com/getActivity/XXPermissions) (权限管理)、AndroidUtilCode
- **网络与通信**：[Retrofit](https://square.github.io/retrofit/)、OkHttp、EasyHttp
- **持久化**：[Room](https://developer.android.google.cn/training/data-storage/room)
- **图像加载**：[Glide](https://github.com/bumptech/glide)
- **集成服务**：UmengSDK (友盟统计)

## Building & Development

### 1. Requirements
- 推荐使用最新稳定版 **Android Studio**。
- Git clone 本仓库后，请将 IDE 视图切换为 **Project** 模式。

### 2. Configuration (必备签名与密钥)
为保障编译通过及核心服务运行，您需要配置签名文件与第三方开放凭证。

创建/修改项目根目录的配置文件：
- **`app/gradle.properties`**：配置 App 签名信息。（项目中已内置测试用的 `AppSignature.jks`，可直接填入如下默认配置，且不会被提交到 Git）：
  ```properties
  StoreFile=AppSignature.jks
  StorePassword=AndroidProject
  KeyAlias=AndroidProject
  KeyPassword=AndroidProject
  ```
- **`configs.gradle`**：配置第三方 SDK 的专属参数（如 `UMENG_APP_KEY`, `WX_APP_ID`, `WX_APP_SECRET`, `BUGLY_ID` 等）。

配置完成后，点击 **Sync Now**，即可编译运行 `:app`。

## Screenshots & Download

> 预发布/测试版本的体验下载方式：

|                                                      蒲公英下载 (扫码或点击)                                                       | 蓝奏云下载 |
|:------------------------------------------------------------------------------------------------------------------------:| :---: |
| <img src="https://www.pgyer.com/app/qrcode/sob-app" alt="蒲公英下载" width="120" /> <br> [点击下载](https://www.pgyer.com/sob-app) | 密码：`5qlt` <br><br> [点击下载](https://wwa.lanzoui.com/b02zz8dva) |

完整的应用界面运行截图，请查阅 [SCREENSHOTS.md](./docs/SCREENSHOTS.md)。

## Community & Support

关于阳光沙滩的创立故事、使命愿景以及作者赞赏打赏信息，请阅读 👉 [ABOUT.md](./docs/ABOUT.md)。

## License

```text
Copyright 2021 He XiaoFeng (anjiemo)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
