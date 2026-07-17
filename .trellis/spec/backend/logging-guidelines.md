# Logging Guidelines

> The logger is **Timber** (`com.jakewharton.timber`). Use `Timber`, not `android.util.Log`,
> and never `println` in app code.

---

## Setup

| Piece | Location | Behavior |
|---|---|---|
| Logger | Timber | Planted only when logging is enabled |
| Custom tree | `other/DebugLoggerTree.kt` (`: Timber.DebugTree`) | Auto TAG `(FileName.kt:lineNumber)` + a stack-trace prefix block |
| Enable flag | `BuildConfig.LOG_ENABLE` via `AppConfig.isLogEnable()` | `true` for `test`/`preview` server types, `false` for `product` (`build-logic/.../ProjectConfig.kt`) |
| Planting | `AppApplication.initSdk()` | `if (AppConfig.isLogEnable()) Timber.plant(DebugLoggerTree())` |
| Release stripping | `removeLog.pro` (proguard) | Log calls removed from `release` builds |

Because Timber is only planted when `LOG_ENABLE` is on, `Timber.*` calls are no-ops in `product`
builds even before proguard runs.

---

## Log Levels

| Call | Use for |
|---|---|
| `Timber.d(...)` | Default developer trace (request results, ids, flow markers) — the overwhelmingly common call |
| `Timber.e(t)` / `Timber.e(t, msg)` | Caught exceptions in `catch` blocks (Repository, DAO transactions) |
| `Timber.w(...)` | Recoverable/unexpected-but-handled conditions |
| `Timber.tag("X")` | Set a one-shot custom tag before the next log (used by AOP `@Log`) |

Convention seen throughout: prefix messages with a method tag, e.g.
`Timber.d("refreshWeather：===> lng is $lng lat is $lat")`.

---

## Method tracing via AOP `@Log`

`aop/Log.kt` + `aop/LogInterceptCut.kt` (AndroidAOP). Annotate a function with `@Log("tag")` to log
entry/args, exit/return, and elapsed ms, plus a `Trace.beginSection/endSection` for the profiler.
Gated by `BuildConfig.LOG_ENABLE`. Example — startup timing:

```kotlin
@Log("启动耗时")
override fun onCreate() { super.onCreate(); initSdk(this) }
```

---

## HTTP logging

| Layer | Wiring | Notes |
|---|---|---|
| Retrofit/OkHttp | `http/interceptor/LoggingInterceptor.kt` — `HttpLoggingInterceptor(Level.BODY)` → `Timber.d` | wrapped by `CodeInvokeInterceptor` |

> Reality note: `LoggingInterceptor.kt` also declares `const val debugLoggerEnable = true`, so
> **full request/response bodies are logged at `Level.BODY`** whenever a tree is planted.

---

## What NOT to log

Treat the following as forbidden. **The codebase currently violates some of these — do not add more, and
prefer to redact when you touch the surrounding code:**

| Sensitive value | Current offender (do not extend) |
|---|---|
| `sob_token` | `db/SobCacheManager.kt` (`Timber.d("...sobToken is $sobToken")`) |
| Cookies | `manager/LocalCookieManager.kt` (`Timber.d("...cookie value is $cookie")`) |
| Full response bodies | `LoggingInterceptor` at `Level.BODY` |
| Passwords / PII | login sends `password.lowercaseMd5` — never log the raw or hashed value |
| API keys / secrets | Weather token (`AppApplication.WEATHER_API_TOKEN`) and keys in `ProjectConfig.kt` are hardcoded — never log them, and don't add new hardcoded secrets |

Rule of thumb: log **ids, codes, sizes, status flags** — not credentials, tokens, cookies, headers,
or personal data. Since `Level.BODY` and token logs leak in debug builds, never enable `LOG_ENABLE`
for a `product`/release build.
