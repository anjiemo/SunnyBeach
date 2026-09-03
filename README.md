[English](README.md) | [简体中文](README.zh-CN.md)

---

# SunnyBeach Android

![Kotlin](https://img.shields.io/badge/Language-Kotlin-brightgreen.svg)
![Android](https://img.shields.io/badge/Platform-Android-blue.svg)
![License](https://img.shields.io/badge/License-Apache%202.0-orange.svg)
![Stars](https://img.shields.io/github/stars/anjiemo/SunnyBeach.svg?style=social)

SunnyBeach is an unofficial/community open-source Android client developed based on the [SunnyBeach Community Open API](https://www.sunofbeach.net/). The project adheres to the **Kotlin First** principle and follows Modern Android Architecture best practices.

## Architecture & Tech Stack

This project adopts a modular engineering design with the classic **MVVM** architecture.

### Project Structure
- `:app` - Main application entry point and host business layer.
- `:library` - Collection of common capability modules (not a single module), including several submodules:
  - `:library:base` - Base class encapsulation (BaseActivity / Fragment / Adapter, etc.).
  - `:library:widget` - Custom UI controls and components.
  - `:library:network` - Network layer.
  - `:library:umeng` - Umeng integration.
- `build-logic` - (included build) Unified build logic and plugin management, with the `convention` subproject storing Gradle convention plugins and `ProjectConfig.kt`.
- `tools` - Project auxiliary tools isolated by purpose (such as emoji resource processing and course video playback link extraction tools).

### Tech Stack
- **Language**: [Kotlin](https://kotlinlang.org/) (100% Kotlin First)
- **Architecture Components**: [Google Jetpack](https://developer.android.com/jetpack) (ViewModel, LiveData, etc.)
- **UI / Base Components**: [BRVAH](https://github.com/CymChad/BaseRecyclerViewAdapterHelper) (list adapter), [XXPermissions](https://github.com/getActivity/XXPermissions) (permission management), AndroidUtilCode
- **Networking**: [Retrofit](https://square.github.io/retrofit/), OkHttp, EasyHttp
- **Persistence**: [Room](https://developer.android.com/training/data-storage/room)
- **Image Loading**: [Glide](https://github.com/bumptech/glide)
- **Integrated Services**: UmengSDK (Umeng Analytics)

## Building & Development

### 1. Requirements
- **JDK 21+** (The project enforces version validation in `settings.gradle.kts`; builds will fail with versions below 21).
- Latest stable version of **Android Studio** is recommended.
- After cloning the repository, switch the IDE view to **Project** mode.

### 2. Configuration (Required Signing and Keys)
To ensure successful compilation and core service operation, you need to configure the signing file and third-party credentials.

Create/modify the configuration files in the project root directory:
- **`app/gradle.properties`**: Configure app signing information. (The project includes a test `AppSignature.jks`, you can directly use the following default configuration, which won't be committed to Git):
  ```properties
  StoreFile=AppSignature.jks
  StorePassword=AndroidProject
  KeyAlias=AndroidProject
  KeyPassword=AndroidProject
  ```
- **[`ProjectConfig.kt`](./build-logic/convention/src/main/kotlin/cn/cqautotest/sunnybeach/ProjectConfig.kt)**: Configure third-party SDK parameters (such as `UMENG_APP_KEY`, `WX_APP_ID`, `WX_APP_SECRET`, `BUGLY_ID`, etc.).

After configuration, click **Sync Now** to compile and run `:app`.

## Screenshots & Download

> Pre-release/test version download options:

|                                                      Pgyer Download (Scan or Click)                                                       | Lanzou Download |
|:------------------------------------------------------------------------------------------------------------------------:| :---: |
| <img src="https://www.pgyer.com/app/qrcode/sob-app" alt="Pgyer Download" width="120" /> <br> [Click to Download](https://www.pgyer.com/sob-app) | Password: `5qlt` <br><br> [Click to Download](https://wwa.lanzoui.com/b02zz8dva) |

For complete application interface screenshots, please refer to [SCREENSHOTS.md](./docs/en/guides/SCREENSHOTS.md).

## Community & Support

For the story behind SunnyBeach's founding, mission and vision, and author appreciation information, please read 👉 [ABOUT.md](./docs/en/community/ABOUT.md).

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
