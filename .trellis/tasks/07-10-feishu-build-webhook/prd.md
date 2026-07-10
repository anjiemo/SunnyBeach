# PRD: 飞书 WebHook 打包/CI 状态卡片推送

## 背景

项目通过 GitHub Actions 打包与 CI 检查，需要将开始/成功/失败状态推送到飞书群，便于团队及时感知构建结果。

## 目标

- 打包工作流与 CI 检查工作流分别向不同飞书机器人推送交互式卡片
- 开始、成功、失败各发一条卡片（同一 `github.run_number` 作为打包 id）
- 使用自定义机器人 WebHook + 签名校验（Bash 实现，不用 Python）

## 范围

### In Scope

1. 新增 `.github/scripts/feishu_notify.sh`（openssl 签名 + curl 推送）
2. `release.yml` → `build.yml`：支持 `debug` / `preview` / `release`
3. `check.yml` 接入 CI 机器人；PR 不推送
4. 卡片字段见下方规格

### Out of Scope

- 飞书应用机器人 / 同一张卡片原地更新
- 本地 Gradle 打包推送
- 自定义关键词校验

## 决策摘要

| 项 | 结论 |
|---|---|
| 推送方式 | WebHook + 多条卡片 |
| 实现 | Bash（openssl + curl） |
| Tag `v*` | 固定正式版 + GitHub Release；触发方式显示「自动触发」 |
| Artifact | 仅手动 + 正式版上传 |
| CI PR | 不推飞书 |
| app_name | 从 `strings.xml` 读取 |
| 分支 | 只显示分支名 |
| 触发方式 | 手动触发 / 自动触发（禁止「Tag 触发」文案） |
| 打包状态文案 | 打包开始🚀 / 打包成功✅ / 打包失败❌ |
| CI 状态文案 | 检查开始🚀 / 检查成功✅ / 检查失败❌ |
| 按钮 | 开始：工作流；成功/失败：工作流 + Gradle 日志 |

## Secrets

- `FEISHU_BUILD_WEBHOOK_URL` / `FEISHU_BUILD_WEBHOOK_SECRET`
- `FEISHU_CI_WEBHOOK_URL` / `FEISHU_CI_WEBHOOK_SECRET`

## 卡片字段

- 打包 id、状态、`{app_name}/{类型}`、触发方式、分支、版本、git 短哈希、git 提交首行、工作流链接、Gradle 日志链接（成功/失败）、创建时间、更新时间

## 验收标准

- [ ] 手动三种 build_type 收到开始 + 终态卡片；仅 release 上传 Artifact
- [ ] Tag `v*` 正式版 + Release；触发方式为「自动触发」
- [ ] CI 使用检查开始/成功/失败；成功与失败均可点工作流与 Gradle 日志
- [ ] PR 不推飞书；Secrets 缺失时跳过通知、构建继续
