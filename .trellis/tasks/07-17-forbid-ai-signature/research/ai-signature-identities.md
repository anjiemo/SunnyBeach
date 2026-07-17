# Research: 各主流 AI 编码工具的 git 提交署名标识

- **Query**: 各主流 AI 编码工具在 git 提交中留下的确切署名标识（trailer / 正文页脚 / bot 身份）
- **Scope**: external（GitHub REST API + 开源源码 + 第三方检测项目）
- **Date**: 2026-07-17

## 验证方法与可信度定义

本文所有"已确认"结论均来自下列**可复现的一手/准一手证据**，不依赖印象：

| 方法 | 说明 | 可靠性 |
|---|---|---|
| GitHub Users API (`gh api users/<login>`) | 返回 bot 账号的**权威数值 ID**，`users.noreply.github.com` 邮箱由 `<id>+<login>@` 构成 | 权威 |
| GitHub **Code** Search API | **实测通过对照组验证**：无意义字符串返回 `0`，真实字符串返回数千 → 精确匹配可信 | 高 |
| 工具**自身开源源码** | 字符串硬编码处（如 aider `repo.py:252`、gemini-cli `gitService.ts:25`） | 权威 |
| 第三方检测项目 | CHAOSS（Linux 基金会）`disclosure`、cdxgen `aiProvenanceCollector.js` | 佐证 |

**置信度口径**：
- **已确认** = 有工具官方源码，或 GitHub API 权威 ID，或 ≥2 处独立来源互相印证
- **未确认** = 账号/字符串存在但缺乏其进入提交的证据，或证据量过弱（个位数命中）

> ⚠️ **一条重要的方法论排除**：GitHub **Commits** Search API（`search/commits`）**不做精确短语匹配**。实测查询 `"noreply@anthropic.com"` 返回 1749 万条，首条命中的 author 邮箱却是完全无关的 `aayushpuri@cloudtechservice.com`。**其 `total_count` 不可用作证据**，本文一律未采用。若后续有人想用 commit 搜索验证，请注意此坑。

**交叉验证结果**：CHAOSS 清单中的 **15 个 bot 数值 ID 全部经 GitHub Users API 独立复核一致**，无一例外 → 该清单可信度高。

---

## 汇总表

类型缩写：**T**=Co-authored-by trailer，**F**=正文页脚/前缀，**B**=bot 身份（author/committer）

| 工具 | 类型 | 确切字符串或邮箱 | 来源 | 置信度 |
|---|---|---|---|---|
| **Claude Code** | T | `Co-Authored-By: Claude <noreply@anthropic.com>` | [CHAOSS constants.go](https://github.com/chaoss/disclosure/blob/main/detection/constants.go) `KnownCoAuthorEmails`；[cdxgen](https://github.com/cdxgen/cdxgen/blob/master/lib/helpers/aiProvenanceCollector.js) `/noreply@anthropic\.com/i`；code search 2608 | **已确认** |
| **Claude Code** | F | `🤖 Generated with [Claude Code](https://claude.ai/code)`（旧） | code search 1400 | **已确认** |
| **Claude Code** | F | `🤖 Generated with [Claude Code](https://claude.com/claude-code)`（新） | code search 2020 | **已确认** |
| Claude | B | `209825114+claude[bot]@users.noreply.github.com` | Users API + CHAOSS | **已确认** |
| Claude (Anthropic) | B | `215619710+anthropic-claude[bot]@users.noreply.github.com` | Users API + CHAOSS | **已确认** |
| Claude Code Action | B | `208546643+claude-code-action[bot]@users.noreply.github.com` | Users API + CHAOSS | **已确认** |
| **Cursor** | T | `Co-authored-by: Cursor Agent <cursoragent@cursor.com>` | CHAOSS `KnownCoAuthorEmails`；code search 邮箱 1484 / 全名 165 | **已确认** |
| Cursor | B | `206951365+cursor[bot]@users.noreply.github.com` | Users API + CHAOSS | **已确认** |
| Cursor Agent | B | `cursoragent[bot]` id `201670782` | Users API | **已确认**（ID）|
| **GitHub Copilot**（coding agent） | B | `Copilot <198982749+Copilot@users.noreply.github.com>` | Users API + CHAOSS + [gitbutler `.mailmap`](https://github.com/gitbutlerapp/gitbutler/blob/master/.mailmap)；code search 1102 | **已确认** |
| GitHub Copilot | B | `copilot-swe-agent[bot]` → **规范化为同一 login `Copilot` / 同一邮箱** | Users API + gitbutler `.mailmap` | **已确认** |
| GitHub Copilot（chat） | B | `167198135+copilot[bot]@users.noreply.github.com` | Users API + CHAOSS | **已确认** |
| GitHub Copilot | T | `Co-authored-by: Copilot <copilot@github.com>` | CHAOSS + cdxgen `/copilot@github\.com/i` | **已确认** |
| **Gemini Code Assist** | B | `176961590+gemini-code-assist[bot]@users.noreply.github.com` | Users API + CHAOSS；code search 86 | **已确认** |
| **Jules**（Google Labs） | B | `161369871+google-labs-jules[bot]@users.noreply.github.com` | Users API + code search 136 | **已确认** |
| **Gemini CLI** | B | `Gemini CLI <gemini-cli@google.com>` | [gitService.ts:24-25](https://github.com/google-gemini/gemini-cli/blob/main/packages/core/src/services/gitService.ts) | **已确认存在，但见下方 ⚠️ 重大限定** |
| Gemini CLI | T | 是否写入 `Co-authored-by` | 无证据 | **未确认** |
| **OpenAI Codex**（ChatGPT 连接器） | B | `199175422+chatgpt-codex-connector[bot]@users.noreply.github.com` | Users API + CHAOSS + gitbutler `.mailmap` | **已确认** |
| **OpenAI Codex** | B | `215057067+openai-codex[bot]@users.noreply.github.com` | Users API + CHAOSS | **已确认** |
| **OpenAI Codex / ChatGPT** | B | `codex@openai.com`，**显示名不固定**（实见 `GPT 5.5` / `GPT 5.4` / `Codex GPT-5`） | gitbutler `.mailmap`；code search 1194 | **已确认** |
| **Devin** (Cognition) | B | `Devin AI <158243242+devin-ai-integration[bot]@users.noreply.github.com>` | Users API + CHAOSS；code search 273 | **已确认** |
| **aider** | T | `Co-authored-by: aider (<model>) <aider@aider.chat>` | **官方源码** [repo.py:252](https://github.com/Aider-AI/aider/blob/main/aider/repo.py) | **已确认** |
| aider | T | `noreply@aider.chat`（CHAOSS 记录的另一形式） | CHAOSS `KnownCoAuthorEmails` | **已确认**（见下方分歧说明）|
| aider | B | author/committer 名被改为 `<用户名> (aider)` | **官方源码** repo.py:294 | **已确认** |
| aider | F | 提交信息前缀 `aider:` | CHAOSS `AiderCommitPrefix`；cdxgen `/\baider\b:/i` | **已确认** |
| **Replit Agent** | F | `Replit-Commit-Author: Agent` / `Replit-Commit-Author: Assistant`，常伴 `Replit-Commit-Session-Id: <uuid>` | CHAOSS `ReplitAttributionRegex`；cdxgen；code search 377 | **已确认** |
| **Amp** (Sourcegraph) | T | `Amp <amp@ampcode.com>` | code search 全名 129 / 邮箱 143 | **已确认** |
| **Cline** | B | `205137888+cline[bot]@users.noreply.github.com` | Users API + CHAOSS | **已确认** |
| **Continue.dev** | B | `230936708+continue[bot]@users.noreply.github.com` | Users API + CHAOSS | **已确认** |
| **Windsurf / Codeium** | B | `windsurf-bot[bot]` id `189301087` **存在** | Users API | **已确认**（仅 ID 存在）|
| Windsurf / Codeium | T/B | 其 noreply 邮箱实际出现在提交中 | code search **0 命中** | **未确认** |
| **Zed** | T | `agent@zed.dev` | code search 仅 **2** 命中 | **未确认** |
| **v0** (Vercel) | T | `v0@vercel.com` | code search 仅 **14** 命中 | **未确认** |
| **Roo Code** | — | 任何固定署名 | 未查到 | **未确认** |
| *（附带）* Amazon Q Developer | B | `208079219+amazon-q-developer[bot]@users.noreply.github.com` | Users API + CHAOSS | **已确认** |
| *（附带）* Sourcegraph Cody | B | `201248094+sourcegraph-cody[bot]@users.noreply.github.com` | Users API + CHAOSS | **已确认** |
| *（附带）* JetBrains AI | B | `220155983+jetbrains-ai[bot]@users.noreply.github.com` | Users API + CHAOSS | **已确认** |
| *（附带）* CodeRabbit | B | `136622811+coderabbitai[bot]@users.noreply.github.com` | Users API + CHAOSS | **已确认** |

### ⚠️ Gemini CLI 的重大限定（易误判为可拦截项）

`gemini-cli@google.com` 在官方源码中的定义是 **`SHADOW_REPO_AUTHOR_EMAIL`**：

```ts
// packages/core/src/services/gitService.ts:24-25
export const SHADOW_REPO_AUTHOR_NAME = 'Gemini CLI';
export const SHADOW_REPO_AUTHOR_EMAIL = 'gemini-cli@google.com';
```

它仅作为 `GIT_AUTHOR_NAME/EMAIL`、`GIT_COMMITTER_NAME/EMAIL` 注入 gemini-cli **内部用于 checkpoint 的影子仓库**（源码注释：*"Common configuration for the shadow Git repository used for checkpointing"*，并使用专用 gitconfig）。**它不会进入用户真实仓库的提交历史**。

→ 把它写进 commit-msg hook 规则**不会有任何拦截效果**，属于噪音规则。仅在审计他人仓库时可作参考。

### aider 邮箱的来源分歧

- 官方源码当前为 `aider@aider.chat`（repo.py:252）
- CHAOSS 记录为 `noreply@aider.chat`

两者应为**不同版本的历史差异**。→ **按域名 `aider.chat` 匹配可同时覆盖两者**，优于匹配完整邮箱。

---

## 归纳：邮箱域名清单（用于写正则）

### A 类：AI 专属域名，可直接按域名匹配，误伤风险极低

| 域名 | 归属 |
|---|---|
| `anthropic.com` | Claude / Claude Code |
| `cursor.com` | Cursor（`cursoragent@cursor.com`）|
| `aider.chat` | aider（覆盖 `aider@` 与 `noreply@` 两种形式）|
| `ampcode.com` | Amp (Sourcegraph) |

### B 类：域名过宽，**必须连 local-part 一起匹配**

| 完整邮箱 | 说明 |
|---|---|
| `copilot@github.com` | ❌ 绝不可只匹配 `github.com` |
| `codex@openai.com` | ❌ 绝不可只匹配 `openai.com` |
| `gemini-cli@google.com` | ❌ 绝不可只匹配 `google.com`；且见上方限定（影子仓库专用）|

> `noreply@google.com` 实测 code search 2656 命中，绝大多数与 AI 无关（如 gmail.js 文档）→ **不是 AI 标识**。

### C 类：GitHub bot noreply 邮箱

统一形态：`<数值ID>+<login>[bot]@users.noreply.github.com`

> 🚨 **绝不可只匹配 `@users.noreply.github.com`** —— 这是**所有**开启邮箱隐私保护的 GitHub 真人用户的通用邮箱域，会误伤全部正常人类提交。**必须锚定"数值 ID + login"**。

已确认的数值 ID 全表（均经 Users API 复核）：

```
209825114+claude[bot]                     215619710+anthropic-claude[bot]
208546643+claude-code-action[bot]         198982749+Copilot           (= copilot-swe-agent[bot])
167198135+copilot[bot]                    206951365+cursor[bot]
215057067+openai-codex[bot]               199175422+chatgpt-codex-connector[bot]
176961590+gemini-code-assist[bot]         161369871+google-labs-jules[bot]
158243242+devin-ai-integration[bot]       205137888+cline[bot]
230936708+continue[bot]                   201248094+sourcegraph-cody[bot]
220155983+jetbrains-ai[bot]               208079219+amazon-q-developer[bot]
136622811+coderabbitai[bot]               189301087+windsurf-bot[bot]  (ID 存在，未见实际使用)
```

### 关键词清单（非邮箱类）

| 关键词 | 类型 | 备注 |
|---|---|---|
| `Generated with [Claude Code]` | 页脚 | 后接 `(https://claude.ai/code)` 或 `(https://claude.com/claude-code)`，**两种 URL 都要覆盖** |
| `Replit-Commit-Author:` | trailer | 值为 `Agent` 或 `Assistant` |
| `Replit-Commit-Session-Id:` | trailer | 伴随上一条 |
| `aider:` | 提交信息前缀 | |
| `(aider)` | author/committer 名后缀 | |
| `[bot]` | author/committer 名 | 宽泛，见误伤风险 |

---

## 🚨 误伤风险（务必阅读）

cdxgen 明确把下列模式归类为 **`tool: "unattributed"`**（即低置信度推测，非确切标识）：

```js
pattern: /\b(ai-generated|ai-assisted|llm|gpt|copilot|cursor)\b/i,
tool: "unattributed",
```

### 高危：**真人姓名**（这些都是常见英文/法文名）

| 词 | 风险 |
|---|---|
| **Claude** | 常见法语人名（Claude Monet…）。且 GitHub 上 `claude` (**id 81847**) 是一个**注册于早期的真人账号**，非 bot —— 经 Users API 确认 `type: User`。裸匹配 `Co-authored-by:\s*Claude\b` 会误伤真名叫 Claude 的协作者 |
| **Devin** | 常见英文名 |
| **Jules** | 常见英文/法文名 |
| **Cody** | 常见英文名 |

### 高危：**普通技术词汇**

| 词 | 风险 |
|---|---|
| **cursor** | 数据库游标 / 文本光标 / 分页 cursor —— 在提交信息里极常见，裸匹配必然大量误伤 |
| **codex** | 普通英文词（法典/抄本），亦是项目名 |
| **amp** | amperage / amplifier / `&` 的缩写 |
| **continue** | **编程关键字**，裸匹配灾难性误伤 |
| **cline** | 可能是人名或缩写 |
| **gpt / llm / ai-assisted** | 正常技术讨论中高频出现 |

### 规避建议

1. **锚定行首 trailer 键**，而非全文搜关键词：
   `^[[:space:]]*co-authored-by:` 之后再判断邮箱
2. **优先按邮箱/域名匹配，而非显示名**。理由：gitbutler `.mailmap` 实证 `codex@openai.com` 的**显示名会漂移**（`GPT 5.5` / `GPT 5.4` / `Codex GPT-5`），而邮箱稳定
3. **A 类域名**可安全按域名匹配；**B 类必须带 local-part**；**C 类必须带数值 ID**
4. 裸词 `continue` / `cursor` / `amp` / `codex` **不要**进入正则
5. 若要匹配显示名（如 `Claude`），务必要求其与 AI 邮箱**同行共现**，例如
   `^co-authored-by:.*<[^>]*@anthropic\.com>`，不要单独匹配名字
6. `[bot]` 后缀可作为**辅助**信号，但 CI/发布类正常 bot（dependabot、renovate 等）也带 `[bot]`，需白名单

---

## 与本仓库的关系

- 本仓库现有 git 历史**完全干净**：`git log --all` 全部 author/committer 均为 `anjiemo <2695734816@qq.com>`，无任何 `Co-authored-by` / `Generated with` / `noreply` 痕迹（已实测）
- 同任务目录下已有姊妹研究 `.trellis/tasks/07-17-forbid-ai-signature/research/enforcement-mechanisms.md`，其中已列出一版正则草案并记录了 `--no-verify` 可绕过本地 hook、必须 CI 兜底的结论。本文提供的是**标识清单侧的证据补充**

---

## Caveats / Not Found

- **未确认**：Windsurf/Codeium、Zed、v0 (Vercel)、Roo Code 的确切提交署名。Windsurf 的 bot 账号 ID 存在但 code search **0 命中**其 noreply 邮箱；Zed (2 命中)、v0 (14 命中) 证据量过弱，不足以作为拦截依据
- **未确认**：Gemini CLI、Codex CLI 在**本地提交**时是否默认写入 `Co-authored-by`。已确认的 Gemini 邮箱仅用于影子仓库
- **未确认**：Cursor 是否有正文页脚（仅确认 trailer）
- 各工具署名行为**随版本变化**（aider 邮箱分歧、Claude 页脚 URL 从 `claude.ai/code` 迁移到 `claude.com/claude-code` 即为实例）。本清单为 2026-07-17 快照，需定期复核
- code search 命中数**只反映"该字符串出现在公开代码文件中"**，其中相当比例其实是**别的项目的禁用规则/mailmap/文档**，而非真实署名本身。故命中数仅用作"该标识形式真实存在且被业界识别"的佐证，不代表署名频次
- CHAOSS `disclosure` 与 cdxgen 均为第三方检测项目（非工具厂商官方），本文已将其全部 bot ID 经 GitHub Users API 独立复核一致后才采信
