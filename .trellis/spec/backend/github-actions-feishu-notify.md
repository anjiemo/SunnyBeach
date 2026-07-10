# GitHub Actions Feishu Notify

> Executable contract for Feishu custom-bot build/CI cards.

---

## Scope / Trigger

Applies when changing:

- `.github/scripts/feishu_notify.sh`
- `.github/workflows/build.yml`
- `.github/workflows/check.yml`
- Feishu-related GitHub Secrets

## Signatures

```bash
# Required env for feishu_notify.sh
WEBHOOK_URL WEBHOOK_SECRET STATUS SCENE
BUILD_ID APP_NAME BUILD_TYPE_LABEL TRIGGER_LABEL BRANCH
VERSION_NAME VERSION_CODE GIT_SHA GIT_MESSAGE
RUN_URL JOB_URL CREATED_AT UPDATED_AT

# STATUS: start|success|failure
# SCENE: build|check
bash .github/scripts/feishu_notify.sh
```

HMAC sign (Feishu official):

```bash
string_to_sign=$(printf '%s\n%s' "${timestamp}" "${WEBHOOK_SECRET}")
sign=$(printf '' | openssl dgst -sha256 -hmac "${string_to_sign}" -binary | openssl base64 -A)
```

## Contracts

| Secret | Consumer |
|---|---|
| `FEISHU_BUILD_WEBHOOK_URL` / `FEISHU_BUILD_WEBHOOK_SECRET` | `build.yml` |
| `FEISHU_CI_WEBHOOK_URL` / `FEISHU_CI_WEBHOOK_SECRET` | `check.yml` (non-PR only) |

| Field | Rule |
|---|---|
| Card header title | `{BUILD_ID} - {STATUS_TEXT}` e.g. `123 - 检查开始🚀` / `45 - 打包失败❌` |
| Body version/git | Plain text only — no markdown backticks around version or git sha |
| Trigger label | `手动触发` / `自动触发` only — never `Tag 触发` |
| Branch | Branch name only; tag runs resolve via `git branch -r --contains` |
| app_name | Read from `app/src/main/res/values/strings.xml` |
| Artifact | Only `workflow_dispatch` + `release` |
| Notify failure | Must not fail the job (`continue-on-error` / script `exit 0`) |
| CI buttons | SCENE=check: start/success/failure all show 工作流 + Gradle 日志; resolve JOB_URL before check-start |

## Validation & Error Matrix

| Case | Behavior |
|---|---|
| Missing WEBHOOK_URL or SECRET | Warn, skip notify, exit 0 |
| HTTP / Feishu business error | Warn, exit 0 |
| Empty JOB_URL on success/failure | Omit Gradle button or fall back to RUN_URL |

## Good / Base / Bad

- **Good**: Tag `v*` → release build, card shows `自动触发`, creates GitHub Release
- **Base**: Manual debug/preview → notify, no Artifact
- **Bad**: Showing `Tag 触发` on cards; failing the job because Feishu is down

## Tests Required

- Manual: dispatch `build.yml` with each `build_type`; confirm start + terminal cards
- Manual: push non-PR to `dev`/`main`; confirm CI cards; open PR and confirm no Feishu
- Assert: success/failure cards include 工作流 + Gradle 日志 when JOB_URL available

## Wrong vs Correct

| Wrong | Correct |
|---|---|
| HMAC key = secret, message = timestamp | HMAC key = `timestamp\nsecret`, message empty |
| PR CI also posts Feishu | `if: github.event_name != 'pull_request'` |
| Python notify helper | Bash + openssl + curl only |
