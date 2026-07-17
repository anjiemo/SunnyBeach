# Quality Guidelines

> What "good" looks like in this repo — recorded as it **actually is**, including the debt.

---

## Toolchain (enforced by `build-logic` convention plugins)

| Setting | Value | Source |
|---|---|---|
| Language | Kotlin-first (a handful of legacy `.java`, e.g. `manager/ThreadPoolManager.java`) | — |
| JDK / `jvmTarget` | 21 | `build-logic/.../KotlinAndroid.kt`, `settings.gradle.kts` (requires JDK 21+) |
| `compileSdk` / `minSdk` | 37 / 26 | `KotlinAndroid.kt` |
| ViewBinding | on (`buildFeatures.viewBinding = true`) | `KotlinAndroid.kt` |
| Kotlin flags | `-Xcontext-parameters` enabled | `KotlinAndroid.kt` |
| DI | Hilt (`@HiltAndroidApp`, `@Module @InstallIn(SingletonComponent::class)`) | `di/*`, `sunnybeach.hilt` plugin |

Module config is centralized — do not hand-configure `compileSdk`/`jvmTarget` in a module; apply the
convention plugin (`sunnybeach.android.application` / `sunnybeach.android.library`).

---

## Linting

- **Android Lint only** (bundled with AGP). There is **no detekt, ktlint, spotless, or checkstyle**.
- Globally disabled checks (`KotlinAndroid.kt`): `HardcodedText`, `ContentDescription`.
- So style is convention-by-example, not machine-enforced. Match the surrounding file.

---

## Architecture conventions

Layering (SOB/Retrofit path): **UI → ViewModel → Repository → Network → Api**.

- Screens extend the app bases (`AppActivity : BaseActivity`, `AppFragment<A : AppActivity>`); shared
  behavior belongs in `:library:base` (`com.hjq.base.*`) or the `App*` bases, not copy-pasted.
- ViewModels are obtained with `by viewModels()`; they expose `LiveData<Result<T>>` or `StateFlow`
  and never touch Retrofit/Room directly — they go through `Repository`.
- `Repository` (an `object`) centralizes network→`Result<T>` mapping; keep that logic out of screens.
- Views use ViewBinding through the VBPD delegate (`by viewBinding()` / `library vbpd`).
- **Keep Activities thin** ("extract to base + ViewModel"). This is respected today: the largest UI
  file is `ui/activity/VideoSelectActivity.kt` at ~592 lines; nothing approaches the ~1000-line ceiling.

---

## Testing (current reality: effectively none)

| Fact | Detail |
|---|---|
| `app/src/androidTest/` | **Does not exist** — zero instrumented tests |
| `app/src/test/kotlin/Test.kt` | JUnit-annotated but really **ad-hoc developer scripts** (apk MD5, bulk article/image migration hitting the **real production API**) — no assertions |
| `app/src/test/kotlin/SelectTest.kt` | A `fun main()` coroutine `select` demo — not a test at all |
| Test deps | only `junit`, `androidx.junit`, `espresso-core` declared, largely unused |

Treat automated tests as **absent**. Do not assume a test safety net exists; verify changes by
building/running. If you add real tests, put unit tests under `app/src/test/` and instrumented tests
under a new `app/src/androidTest/`.

---

## Known debt (record, don't be surprised by)

- **Misspelled package `execption`** — intentional; never rename (see error-handling.md).
- **Hardcoded secrets** — Umeng/WeChat keys in `ProjectConfig.kt`, weather token in `AppApplication`.
  Don't add more; don't log the existing ones.
- **Secrets/PII in logs** in debug builds (see logging-guidelines.md).

---

## Review checklist (backend/data changes)

- [ ] New endpoint follows the Retrofit → `Repository` → `Result<T>` pattern.
- [ ] Network/DB calls run off the main thread (`Dispatchers.IO`; `CookieDao` is blocking).
- [ ] New Room entity → registered in `AppRoomDatabase`, `version` bumped, `Migration` written & added.
- [ ] JSON uses `GsonFactory.getSingletonGson()`.
- [ ] Errors mapped to `Result<T>` in the Repository; `catch` blocks call `Timber.e(t)`.
- [ ] No tokens/cookies/passwords/PII added to logs; no new hardcoded secrets.
- [ ] Screen stays thin — logic pushed to ViewModel/Repository/base class.
