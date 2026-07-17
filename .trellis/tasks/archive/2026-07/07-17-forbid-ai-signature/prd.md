# 禁止 AI 署名与协作者邮箱的提交检查

## Goal

在本仓库建立一道自动化关卡，**禁止任何 AI 协作者署名与 AI 邮箱进入 git 提交**——包括但不限于 Cursor、Claude、Gemini、Copilot、Codex 等。目的是让提交历史只体现真实人类作者，避免 AI 工具默认写入的 `Co-authored-by:` trailer 与"Generated with …"页脚污染仓库历史。

## What I already know

**现状勘察（已核实）：**

- **历史是干净的**：736 个提交中，AI 署名/AI 邮箱命中数为 **0**。→ 无需设"基线豁免"，检查可直接对全历史生效。
- **提交身份单一**：历史中 author/committer 只有 `anjiemo <2695734816@qq.com>`。→ 白名单式校验也可行。
- **本地 hook 完全从零**：无 `.githooks/` 目录；`core.hooksPath` 未设置；`.git/hooks/` 下无任何非 `.sample` 的 hook。
- **CI 已存在**：`.github/workflows/check.yml`（CI 编译检查）与 `build.yml`。
- **⚠ check.yml 有 `paths-ignore`**：包含 `**.md` 与 `.trellis/**`。意味着**纯文档提交不会触发 check.yml**——若把 AI 署名检查挂在该工作流下，只改 `.md` 的提交将完全绕过检查。这是选型时必须处理的坑。
- **环境**：Windows + Git Bash 为主；Android/Kotlin/Gradle 多模块，含 `build-logic` included build（有 convention 插件，具备"用 Gradle task 自动装 hook"的条件）。

**背景：** 仓库既有约定是提交不带 `Co-Authored-By: Claude` 尾注；本任务是把该约定从"靠自觉"升级为"自动强制"，并扩展到所有 AI 工具。

## Assumptions (temporary)

- 目标是**拦截新提交**，而非改写已有历史（历史本就干净，无需改写）。
- `--no-verify` 可绕过本地 hook 是已知事实 → 需要 CI 侧兜底才算真正"禁止"。
- 误伤风险存在（如正常提交里出现 "gemini"、"copilot" 等普通词），规则需以 trailer/邮箱域为主、关键词为辅。

> 研究已收敛的问题（见文末 Open Questions 为剩余待决项）：
> - 检查对象：**消息 + author/committer 身份都要查**（实测构造过「消息干净但 author 是 AI」的提交，只查消息会完全放过）
> - CI 范围：**按增量范围**（PR `base..head`；push `before..sha`，需处理新分支全 0 与 force-push 对象不存在两种边界）。全历史扫描须用单趟 `git log`+`grep`（0.23s），逐提交循环会超时（>2min）
> - `check.yml` 盲区：**新建独立 `commit-guard.yml`，不设任何 `paths-ignore`**
> - 现成工具：**全部不引入**（commitlint/gitlint/pre-commit 需 Node/Python 工具链，本仓库纯 Gradle）

## Requirements (evolving)

- 检测并阻止提交信息中的 AI `Co-authored-by:` trailer
- 检测并阻止提交信息中的 AI 署名页脚（如 "Generated with …"）
- 检测并阻止 AI 邮箱域名（如 `@anthropic.com`、`@cursor.com` 等）
- 检测规则需**单一事实来源**，供本地 hook 与 CI 复用，避免两处规则漂移
- 规则清单需易于扩展（新 AI 工具层出不穷）

## Acceptance Criteria (evolving)

- [ ] 含 AI `Co-authored-by:` trailer 的提交被拒绝
- [ ] 含 AI 署名页脚的提交被拒绝
- [ ] 含 AI 邮箱域名的 author/committer 被拒绝（若决定纳入范围）
- [ ] 正常人类提交不被误伤（回归：现有 736 个提交全部通过）
- [ ] `--no-verify` 绕过本地 hook 后，CI 仍能拦截
- [ ] 纯 `.md` 提交同样受检查覆盖（不被 `paths-ignore` 漏掉）

## Definition of Done

- 规则脚本可独立运行且有明确退出码与可读报错
- CI 绿灯；对现有历史执行不产生误报
- 文档说明如何安装本地 hook、如何扩展规则清单
- 明确记录绕过路径与其覆盖情况

## Out of Scope

- 改写既有历史（历史已干净，无需 filter-branch / rebase）
- 检测提交**代码内容**是否由 AI 生成（不可行且非目标）
- 服务端 `pre-receive` hook（GitHub 非 Enterprise 不支持）

## Research References

- [`research/ai-signature-identities.md`](research/ai-signature-identities.md) — 20+ 工具的确切署名标识清单；**核心结论：按邮箱/域名匹配，绝不匹配裸显示名**
- [`research/enforcement-mechanisms.md`](research/enforcement-mechanisms.md) — 强制手段对比；**核心结论：本地 hook 与 CI 覆盖的绕过路径互补，缺一不可**

### 设计铁律（研究实测得出）

1. **只锚定署名结构，绝不对正文做子串匹配。** 朴素关键词正则在本仓库 736 条历史上产生 **2 个误报**（如 `fa776494`「为 Claude Code, Cursor, GitHub Copilot, Gemini 和 Windsurf 提供集成…」——正当的人类提交，只是正文提到了工具名）。且 `android.database.Cursor` 真实存在于代码中，「关闭 Cursor」类提交必然出现。**讨论 AI 的提交必须允许，只禁止以 AI 身份署名。**
2. **优先匹配邮箱而非显示名。** 实证 `codex@openai.com` 显示名会漂移（`GPT 5.5`/`GPT 5.4`/`Codex GPT-5`）；而 `Claude`/`Devin`/`Jules`/`Cody` 都是真人名（GitHub `claude` id 81847 为 `type: User` 真人账号）。显示名仅在与 AI 邮箱**同行共现**时才作数。
3. **裸词禁入正则**：`continue`（编程关键字）、`cursor`、`codex`、`amp`。
4. **`@users.noreply.github.com` 绝不可单独匹配** —— 那是所有开启邮箱隐私的真人用户的通用域，必须锚定「数值 ID + login」。
5. **`gemini-cli@google.com` 是噪音规则** —— 官方源码中它是 `SHADOW_REPO_AUTHOR_EMAIL`，仅用于 gemini-cli 内部 checkpoint 影子仓库，永不进入用户真实提交。

### 匹配清单分层

- **A 类（可按域名安全匹配）**：`anthropic.com`、`cursor.com`、`aider.chat`、`ampcode.com`
- **B 类（必须带 local-part）**：`copilot@github.com`、`codex@openai.com`
- **C 类（必须带数值 ID）**：18 个已经 GitHub Users API 复核的 bot ID（详见研究文件）
- **页脚/前缀类**：`Generated with [Claude Code]`（两种 URL 都要覆盖）、`Replit-Commit-Author:`、`aider:` 前缀

## Feasible Approaches

**方案 A：仅 CI**（1 个文件）
- 如何工作：只加 `commit-guard.yml`
- 优点：改动最小；零本地配置；对新 clone/网页端天然生效；`--no-verify` 绕不过
- 缺点：反馈滞后——提交已进 `dev` 才红，修复需 `rebase -i` 改写已推历史

**方案 B：本地 hook + CI 兜底**（推荐）
- 如何工作：`.githooks/commit-msg`(+`pre-push`) + `.gitattributes` 锁 LF + `--chmod=+x` 提交 + `settings.gradle.kts` 配置期幂等设 `core.hooksPath` + 独立 `commit-guard.yml`
- 优点：秒级本地反馈，问题永不进历史；CI 补齐所有绕过路径；**零新依赖**（git+grep+sh）；Gradle sync 自动装，无需记忆命令
- 缺点：文件多几个；`core.hooksPath` 会覆盖 `.git/hooks/`（本仓库仅有 sample，无损）

**方案 C：B + GitHub Ruleset**
- 如何工作：在 B 之上加 metadata ruleset，服务端 push 时事前拒收
- 优点：唯一能事前挡住「直接 push 到 dev」
- 缺点：**可用性未确证**（个人账号公开仓库很可能不支持）；管理员默认可 bypass；规则存在 GitHub 设置里不随仓库版本化

## Decision (ADR-lite)

**Context**: 需要在 GitHub 托管的个人公开仓库上强制「提交不得含 AI 署名」。本地 hook 可被 `--no-verify` 绕过、对网页端编辑与未配置的新 clone 无效；纯 CI 则反馈滞后（提交已落地才红）。二者覆盖的绕过路径互补。

**Decision**: 采用**方案 B —— `.githooks/` 本地 hook + 独立 `commit-guard.yml` CI 兜底**，零新依赖（git + grep + sh）。不引入 commitlint/gitlint/pre-commit（需 Node/Python 工具链，与纯 Gradle 仓库不匹配）。暂不采用方案 C 的 GitHub Ruleset（个人账号公开仓库可用性未确证，且规则不随仓库版本化）。

**Consequences**:
- ✅ 秒级本地反馈，违规提交永不进入历史
- ✅ CI 补齐 `--no-verify` / 网页端 / 新 clone / 纯文档提交等全部绕过路径
- ✅ 零新依赖，本地与 CI 复用同一份 shell 规则（单一事实来源，不会规则漂移）
- ⚠️ `core.hooksPath` 会**完全取代** `.git/hooks/`（本仓库仅有 `.sample`，无损）
- ⚠️ 配置期在 `settings.gradle.kts` 执行 git 命令不够 Gradle 正统，但换来「sync 即自动装」，单人仓库下简单性优先
- ⚠️ 仍**无法事前拒收**直接 push 到 `dev` 的违规提交（那是方案 C 的能力）；缓解手段是分支保护 + required check
- ⚠️ squash merge 的最终消息在 CI 通过**之后**才生成，CI 校验不到；当前单人直推无风险，若将来启用 PR+squash 需重新评估

## Technical Approach

方案 B 的六个动作：

1. `.githooks/commit-msg` —— 校验最终提交信息（`$1` 是消息**文件路径**）+ `git var GIT_AUTHOR_IDENT` 身份兜底
2. `.githooks/pre-push` —— 遍历推送范围整批拦截（挡住 rebase 带入的旧署名）
3. `.gitattributes` 加 `.githooks/** text eol=lf` —— 本仓库 `core.autocrlf=true` 且无 `.gitattributes`，否则 hook 在 Linux 上 `bad interpreter: /bin/sh^M`
4. `git update-index --add --chmod=+x` 提交 hook —— Windows 下 `git add` 记录 `100644`，Linux clone 后 hook 静默不执行
5. `settings.gradle.kts` 配置期幂等设置 `core.hooksPath`（`runCatching` 包住、跳过 CI）
6. `.github/workflows/commit-guard.yml` —— 独立文件、**不设 `paths-ignore`**、`fetch-depth: 0`、按增量范围校验（处理新分支全 0 与 force-push 对象不存在两种边界）

## Decision 2: 分支保护范围 (ADR-lite)

**Context**: `dev` 与 `main` 当前均无分支保护、无 ruleset，可被直接 push。CI guard 若无 required check 加持，只能在违规提交落地后亮红灯。但开启保护即意味着该分支必须走 PR，对单人仓库是实打实的流程负担。

**Decision**: **只保护 `main`** —— `main` 开分支保护并把 `commit-guard` 设为 required status check；`dev` 保持自由直推。

**Consequences**:
- ✅ 发布分支守死：含 AI 署名的提交合不进 `main`
- ✅ `dev` 上的开发节奏不受影响，仍可直推
- ✅ `dev` 的主防线是本地 hook（提交那一刻即时拦），CI 负责兜 `--no-verify` / 网页端 / 新 clone
- ⚠️ `dev` 上若有人 `--no-verify` 硬提交，CI 只能事后红，需 `rebase -i` 清理后重推
- ⚠️ 分支保护是 GitHub 侧设置，不随仓库版本化（与 hook/workflow 的「配置即代码」不同）

## Decision 3: 规则严格度——四层防线 (ADR-lite)

**Context**: 纯枚举名单只能拦已确认的工具，与用户要求的「包括但**不限于**」相悖——新 AI 工具出现即漏网。而本仓库历史提供了两个极强的结构性事实，使更严格的规则成为可能且几乎零误伤。

**依据（全部实测）**：
- 736 条提交中 **trailer 总数 = 0**（从未出现过任何 `Co-authored-by`）
- 历史中**从未出现过任何 `[bot]` 身份**；author/committer 仅 `anjiemo <2695734816@qq.com>`
- **无 dependabot / renovate 配置**；workflow 中**无任何 git commit/push** 行为（不产生 `github-actions[bot]` 提交）

**Decision**: 采用**四层防线**：

| 层 | 规则 | 覆盖 | 依据 |
|---|---|---|---|
| **1. 精确名单** | A 类域名（`anthropic.com`/`cursor.com`/`aider.chat`/`ampcode.com`）、B 类完整邮箱（`copilot@github.com`/`codex@openai.com`）、**C 类 18 个 bot 数值 ID**、页脚前缀（`Generated with [Claude Code]` 两种 URL、`Replit-Commit-Author:`、`Replit-Commit-Session-Id:`、`aider:` 前缀、author 名后缀 `(aider)`） | 已确认 AI，**消息侧与身份侧都查** | 研究实证 |
| **2. 禁止一切 `Co-authored-by:` trailer** | 任何 `^[[:space:]]*co-authored-by:` 一律拒 | **未知的新 AI 工具** | 历史 trailer 总数 = 0 |
| **3. GitHub bot 结构匹配** | 身份侧 `[0-9]+\+[^@]*\[bot\]@users\.noreply\.github\.com` | **任何带 `[bot]` 的 GitHub App** | 历史无 `[bot]`；真人隐私邮箱不带 `[bot]`，零误伤 |
| **4. bot 命名邮箱 token** | local-part 中 `bot` 作为分隔符 token：`(^\|[._+-])bot([._+-]\|$)`，**大小写不敏感**。作用于身份侧 + 任何 trailer 形态的行 | `bot@` / `Bot@` / `BOT@` / `ai-bot@` / `bot.agent@` / `my_bot@` | 用户指定 |

**⚠️ 为什么 C 类 18 个 ID 必须保留为强制规则（不可降级为报错文案）**：

`198982749+Copilot@users.noreply.github.com` 的 login 是 **`Copilot`，不含 `[bot]` 后缀**（研究记录：`copilot-swe-agent[bot]` 被 GitHub 规范化为同一 login `Copilot` / 同一邮箱）。因此：
- 第 3 层（`\[bot\]@`）**抓不到它**
- 第 4 层（local-part 含 token `bot`）—— local-part 为 `198982749+Copilot`，**也抓不到**

→ **只有第 1 层的 C 类名单能拦住 GitHub Copilot coding agent。** 这是一个反直觉的例外，实现时不得省略 C 类枚举。

**第 4 层的边界**（用户已确认取「分隔符 token」档）：
- ❌ 拦：`bot@` `Bot@` `BOT@` `ai-bot@` `bot.agent@` `my_bot@` `bot-01@`
- ✅ 放：`abbot@`（姓氏 Abbot）、`botha@`（姓氏 Botha）、`robot@`、`chatbot@`、`bots@`
- 理由：避免误伤含 `bot` 子串的真实姓氏；`robot`/`chatbot` 经权衡不纳入

**范围限定**：第 2 层**只针对 `Co-authored-by:`**，不动 `Signed-off-by:` 等其他 trailer 类型。

**Consequences**:
- ✅ 真正做到「包括但不限于」——未知新 AI 工具（第 2 层）、任何 GitHub App bot（第 3 层）、任何 bot 命名邮箱（第 4 层）都拦得住
- ✅ 零误伤：真人隐私邮箱形如 `<id>+<用户名>@users.noreply.github.com` 不含 `[bot]`；`abbot@`/`botha@` 这类真实姓氏不被 token 规则命中
- ✅ 第 2/3/4 层是**结构规则，零维护**——新 AI 工具无需追加名单
- ⚠️ **但第 1 层的 C 类 18 个 ID 仍需维护**（GitHub Copilot 的 `198982749+Copilot@` 不含 `[bot]`，只有枚举能拦），新增 AI bot 账号时需复核
- ⚠️ **将来若启用 Dependabot / Renovate，其提交会被第 3 层拒绝** → 届时需在可信 bot 白名单中加豁免
- ⚠️ 将来若要引入真人协作者的 `Co-authored-by`，需为第 2 层开豁免口子
- ⚠️ 若将来有 workflow 需要以 `github-actions[bot]` 身份提交，同样需豁免
- ⚠️ 残留缺口：未知 AI 若既不写 trailer、又非 GitHub bot、邮箱也不含 `bot` token（如 `<agent@newai.com>`），四层均漏 → 堵它需 author 白名单（本次未采用，因会拒外部贡献者 PR）

## Open Questions

*无 —— 需求已收敛，可进入实现。*

## Technical Notes

- 相关文件：`.github/workflows/check.yml`（注意 `paths-ignore`）、`.github/workflows/build.yml`
- `build-logic/` convention 插件可承载"自动设置 `core.hooksPath`"的 Gradle task
- Windows/Git Bash 下 hook 的可执行权限与换行符（CRLF）需注意
- 仓库 `.gitattributes`/换行策略：git 存在 LF→CRLF 归一化（提交时曾出现 warning）
