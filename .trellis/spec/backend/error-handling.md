# Error Handling

> The app talks to the SOB backend over a single **Retrofit** stack. Every response is the
> `ApiResponse<T>` envelope, and `Repository` maps it to a Kotlin `Result<T>` — that is where
> errors are handled.

---

## The `execption` package (sic)

Custom exceptions live in `app/.../execption/`. **The package is misspelled `execption`
on purpose (historical). Do not "fix" it — many files import it.**

| Exception | Base | Notes |
|---|---|---|
| `ServiceException` | `RuntimeException` | generic business failure (`!isSuccess`) |
| `NotLoginException` | `RuntimeException` | default msg `"账号未登录"` |
| `NotBuyException` | `RuntimeException` | default msg `"未购买"` |
| `LoginFailedException` | `RuntimeException` | default msg `"登录失败"` |

---

## Retrofit / SOB content (`ApiResponse<T>`)

Envelope: `model/ApiResponse.kt` — `{ code, success, message, data }` implementing `IApiResponse<T>`.

Flow: `http/api/**` (Retrofit) → `http/network/*Network` → `repository/Repository.kt` →
`ViewModel` (`LiveData<Result<T>>`) → screen observes and toasts.

`Repository` converts the envelope to a Kotlin `Result<T>` inside `liveData {}` builders
(`launchAndGetData` / `launchAndGetMsg` / `launchAndGet`), always on `Dispatchers.IO`:

```kotlin
if (result.isSuccess()) Result.success(onSuccess(result))
else when (result.getCode()) {
    NOT_LOGIN_CODE -> { checkToken(); Result.failure(NotLoginException(result.getMessage())) } // 11126
    else           -> Result.failure(ServiceException(result.getMessage()))
}
// outer try/catch → Timber.e(t) + Result.failure(t)
```

Known business codes handled in `Repository`: `11126` not-logged-in, `11128`/`11129` VIP allowance
(`toAllowanceResult`), plus per-call checks such as `checkCourseHasBuy` throwing `NotBuyException`.

**Rules:**
- Never let an `ApiResponse` escape the Repository — map it to `Result<T>` there.
- Wrap the whole call in `try/catch`, log with `Timber.e(t)`, return `Result.failure(t)`.
- ViewModels/screens branch on `result.isSuccess` / `result.getOrNull()` — they do not parse codes.

---

## JSON parse failures

Configured once in `AppApplication` via `GsonFactory.setParseExceptionCallback { ... }`:

- **Debug** → `throw IllegalArgumentException(message)` (fail loud).
- **Release** → `CrashReport.postCatchedException(...)` (report to Bugly, keep running).

---

## Surfacing errors to the user

- Toaster (`com.hjq.toast.Toaster`) is the toast layer; helpers `simpleToast(...)` /
  `ToastAction`. A global `AppToastLogInterceptor` logs every toast.
- Uncaught crashes: local `other/CrashHandler.kt` + Tencent **Bugly** (`CrashReport`).
- Use `CrashReport.postCatchedException(t)` for caught-but-notable failures you don't want to swallow.

---

## Wrong vs Correct

| Wrong | Correct |
|---|---|
| Rename `execption` → `exception` | Leave it — it is an imported package name |
| Return `ApiResponse<T>` from a ViewModel | Repository maps it to `Result<T>` first |
| Handle token expiry manually in a screen | Repository handles code `11126` (`checkToken()` + `NotLoginException`) |
| Swallow a caught exception silently | `Timber.e(t)` and return `Result.failure(t)` (Repo pattern) |
| Introduce a second error envelope | Reuse `ApiResponse`, mapped to `Result<T>` in the Repository |
