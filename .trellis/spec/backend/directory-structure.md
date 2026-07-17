# Directory Structure

> How the SunnyBeach Android/Kotlin code is organized. "Backend" here means the app's
> data/network/persistence layers (there is no server-side code in this repo).

---

## Overview

SunnyBeach is a **multi-module Gradle** project. Build logic is centralized in an
included build (`build-logic`) exposing convention plugins; feature/app code lives in `:app`;
reusable infra is split into `:library:*` modules.

- Module graph: `settings.gradle.kts`
- Kotlin package root: `app/src/main/java/cn/cqautotest/sunnybeach/`
- Namespace / applicationId: `cn.cqautotest.sunnybeach`

---

## Module Layout

| Module | Path | Responsibility |
|---|---|---|
| `:app` | `app/` | Application, all UI, http/db/viewmodel/repository layers |
| `:library:base` | `library/base/` | Base classes `com.hjq.base.*` — `BaseActivity`, `BaseFragment`, `BaseDialog`, `BaseAdapter` |
| `:library:widget` | `library/widget/` | Custom views `com.hjq.widget.*` |
| `:library:umeng` | `library/umeng/` | Umeng SDK wrapper `com.hjq.umeng.*` (stats/login/share) |
| `:library:network` | `library/network/` | Multi-base-url support `cn.funkt.*` — `@BaseUrl`, `BaseUrlInterceptor` |
| `build-logic` (included build) | `build-logic/convention/` | Convention plugins + `ProjectConfig.kt` |

Convention plugin IDs (registered in `build-logic/convention/build.gradle.kts`, consumed by
`app/build.gradle.kts`): `sunnybeach.android.application`, `sunnybeach.android.library`,
`sunnybeach.android.compose`, `sunnybeach.hilt`, `sunnybeach.project.config`.

---

## App Package Map (`cn.cqautotest.sunnybeach.*`)

| Package | What lives here |
|---|---|
| `app/` | App-level bases: `AppApplication` (`@HiltAndroidApp`), `AppActivity : BaseActivity`, `AppFragment<A : AppActivity>`, `AppAdapter`, `Paging*`/`TitleBar*` fragment bases |
| `http/` | All networking (see split below) |
| `http/api/{sob,weather,photo,app,other}/` | Retrofit interfaces returning `ApiResponse<T>`; `other/` holds account/auth-related APIs |
| `http/network/` | `XxxNetwork` `object` wrappers that call the Retrofit APIs |
| `http/model/` | Shared HTTP response/list models & request config |
| `http/interceptor/` | `accountInterceptor`, `loggingInterceptor`, `CodeInvokeInterceptor` |
| `http/annotation/baseurl/` | `@SobBaseUrl`, `@CaiYunBaseUrl`, `@GiteeBaseUrl`, `@AliyunVodBaseUrl` |
| `http/glide/` | Glide + OkHttp integration |
| `repository/` | `Repository` (`object`, the bulk of the read/write surface) + `VideoRepository(Impl)`, `UserBlockRepository`, `CheckUserParseRepository` |
| `viewmodel/` | `XxxViewModel`; subpackages `app/`, `discover/`, `fishpond/`, `weather/` |
| `db/` | Room: `AppRoomDatabase`, `Converters`, `SobCacheManager`; `db/dao/` DAOs + entities |
| `manager/` | Long-lived singletons: `UserManager`, `ActivityManager`, `LocalCookieManager` (also holds the `CookieStore` entity), `AppManager` |
| `di/` | Hilt modules: `AppModule`, `NetworkModule`, `RepositoryModule`, `SingletonModule` |
| `execption/` | Custom exceptions — **note the intentional misspelling `execption`** (see error-handling.md) |
| `model/` | Data classes; subpackages `weather/`, `msg/`, `course/`, `wallpaper/`, `aliyun/`, `scan/` |
| `ktx/` | Kotlin extension functions, one file per receiver type (`Activity.kt`, `Context.kt`, `ApiResponse.kt`, …) |
| `aop/` | AndroidAOP: `@Log` annotation + `LogInterceptCut` |
| `event/` | `FlowBus` / `FlowBusKey` in-app event bus |
| `paging/` | Paging 3 sources |
| `ui/` | `activity/`, `fragment/`, `dialog/`, `popup/`, `adapter/`, `delegate/` |
| `other/` | `AppConfig`, `CrashHandler`, `DebugLoggerTree`, toast/title-bar styles |
| `work/` | WorkManager workers (`CacheCleanupWorker`) |
| `util/`, `widget/`, `contract/`, `deeplink/`, `wxapi/` | utilities, app widgets, `ActivityResult` contracts, deep-link routing, WeChat callback |

---

## Naming Conventions

| Kind | Pattern | Example |
|---|---|---|
| Retrofit API | `XxxApi.kt` — `interface`, `suspend fun`, `ApiResponse<T>`, `companion object : XxxApi by ServiceCreator.create()` | `http/api/sob/ArticleApi.kt` |
| Network wrapper | `XxxNetwork.kt` (`object`) | `http/network/ArticleNetwork.kt` |
| ViewModel | `XxxViewModel.kt` | `viewmodel/weather/WeatherViewModel.kt` |
| Screen | `XxxActivity.kt` / `XxxFragment.kt` / `XxxDialog.kt` / `XxxPopup.kt` | `ui/activity/LoginActivity.kt` |
| DAO / entity | `XxxDao.kt` interface; entity = `data class` with `@Entity` | `db/dao/UserBlockDao.kt`, `db/dao/UserBlock.kt` |
| Extensions | file named after the receiver type | `ktx/Context.kt` |
| Base classes | `App*` prefix in `:app`, `Base*` prefix in `:library:base` | `app/AppActivity.kt`, `com.hjq.base.BaseActivity` |

---

## Where new code goes

- New SOB endpoint → add to a Retrofit `interface` in `http/api/` (`sob/` for content, `other/` for
  account/auth), expose via an `XxxNetwork` object in `http/network/`, wrap in `Repository`, consume
  from a `ViewModel`.
- New screen → `ui/activity` (or `fragment`/`dialog`/`popup`), extend `AppActivity`/`AppFragment`.
- New persistent table → `db/dao/` entity + DAO, register in `AppRoomDatabase` (see database-guidelines.md).
