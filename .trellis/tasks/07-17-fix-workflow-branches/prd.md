# 修正工作流触发分支：补齐 master

## Goal

两个 GitHub Actions 工作流的触发分支都写的是 `[ "main", "dev" ]`，但**本仓库根本没有 `main` 分支**，默认/发布分支是 **`master`**。导致工作流在发布分支上从不触发。补齐 `master`，同时保留 `main` 以备将来重命名。

## What I already know（已实测核实）

**远端真实分支**（`git ls-remote --heads origin`）：

```
dev、master、dev-java、dev_migration_gradle_to_kts_bak、
dev_test_keybords、dev_upgrade_gradle_version、dev_workflow_test、oldArchive
```

- **没有 `main`**。GitHub 默认分支（`gh repo view`）= **`master`**。
- `.github/workflows/commit-guard.yml`：`push.branches` 与 `pull_request.branches` 均为 `[ "main", "dev" ]`
- `.github/workflows/check.yml`：同样为 `[ "main", "dev" ]`

**影响**：

| 工作流 | 现状 | 后果 |
|---|---|---|
| `commit-guard.yml` | 只在 `main`(不存在)/`dev` 触发 | **发布分支 `master` 完全无署名防线**；且 dev→master 的 PR 也不触发（`pull_request.branches` 同样不含 master） |
| `check.yml` | 同上 | **编译检查在 `master` 上从未跑过**（既存 bug，非本次引入） |

**漏检原因**：实现时照抄了 `check.yml` 的分支列表（合理的一致性推断），而 `check.yml` 自身就是错的；复核在 scratch 仓库里测范围解析逻辑，测不到触发配置。**只有拿配置与真实远端对撞才会暴露。**

## Requirements

1. `.github/workflows/commit-guard.yml` —— `push.branches` 与 `pull_request.branches` 均改为 `[ "main", "master", "dev" ]`
2. `.github/workflows/check.yml` —— 同样改为 `[ "main", "master", "dev" ]`
3. 保留 `main`：列出不存在的分支无害（永不匹配），为将来 `master` → `main` 重命名留后路
4. 不改动两个工作流的其它任何配置（尤其 `commit-guard.yml` **不得**新增 `paths-ignore`）

## Acceptance Criteria

- [ ] 两个工作流的 push 与 pull_request 触发分支均含 `master`
- [ ] `commit-guard.yml` 仍无任何 `paths-ignore`
- [ ] YAML 可被正确解析（用解析器验证，不靠肉眼）
- [ ] 其余配置零改动（diff 只应出现分支列表行）

## Definition of Done

- 两个 workflow 的 diff 仅限分支列表
- YAML 解析通过
- 用户知悉 `check.yml` 的行为变化（见 Consequences）

## Decision (ADR-lite)

**Context**: 需决定是「`main` → `master` 替换」还是「两者并存」，以及是否连带修复 `check.yml` 的同类既存 bug。

**Decision**: **两者并存** `[ "main", "master", "dev" ]`，且**两个工作流都改**（用户明确要求「main 和 master 都需要」）。

**Consequences**:
- ✅ 发布分支 `master` 纳入署名防线；dev→master 的 PR 也会触发校验
- ✅ 将来若把 `master` 重命名为 `main`，工作流无需改动
- ⚠️ **行为变化（用户已知悉）**：`check.yml` 加上 `master` 后，**Gradle 编译检查会第一次真正在 master 推送时运行**（约 6–7 分钟，并推送飞书 CI 卡片）。这是修复而非回归——CI 本就该覆盖发布分支
- ⚠️ 列出不存在的 `main` 是有意为之，不是笔误；勿"清理"

## Out of Scope

- 分支保护 / required status check（属 GitHub 侧设置，用户另行决定）
- `check.yml` 的 `paths-ignore` 配置（不动）
- 重命名 `master` → `main`
- 其它分支（`dev-java`、`oldArchive` 等）不纳入触发

## Technical Notes

- 相关规范：`.trellis/spec/backend/git-hooks-commit-guard.md`（本任务应向其 Wrong vs Correct 补一条：**工作流触发分支必须对着 `git ls-remote --heads origin` 核实，不得照抄同仓库其它 workflow**）
- 相关规范：`.trellis/spec/backend/github-actions-feishu-notify.md`（check.yml 的既有约定）
