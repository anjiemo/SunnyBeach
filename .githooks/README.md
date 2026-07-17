# 提交署名校验

禁止 AI 协作者署名与 AI 邮箱进入 git 提交历史（Cursor / Claude / Gemini / Copilot / Codex 等，
**包括但不限于**）。目的是让提交历史只体现真实人类作者。

## 安装

**通常无需手动操作** —— `settings.gradle.kts` 会在每次 Gradle sync / build 的配置期幂等地设置好：

```bash
git config core.hooksPath .githooks
```

想立刻手动启用，执行上面这条即可。注意 `core.hooksPath` 是**本地 config，无法随仓库提交**，
所以每个新 clone 都要设置一次（Gradle 会自动代劳；CI 环境自动跳过）。

⚠️ 启用后 `.githooks/` **完全取代** `.git/hooks/`（不是叠加）。本仓库 `.git/hooks/` 下只有
`.sample` 文件，无影响。

## 组成

| 文件 | 作用 |
|---|---|
| `lib/ai-signature-rules.sh` | **规则的唯一事实来源**。hook 与 CI 都 source 它，不各写一份 |
| `commit-msg` | 提交那一刻即时拦截（消息 + `git var GIT_AUTHOR_IDENT` 身份） |
| `pre-push` | 遍历推送范围整批拦截，挡住 rebase 带入的旧提交 |
| `../.github/workflows/commit-guard.yml` | CI 兜底；**刻意不设 `paths-ignore`** |

## 四层防线

| 层 | 规则 | 覆盖 |
|---|---|---|
| 1 | 精确名单：A 类域名 / B 类完整邮箱 / C 类 18 个 bot 数值 ID / 页脚前缀 | 已确认的 AI 工具 |
| 2 | 禁止一切 `Co-authored-by:` trailer | **未知的新 AI 工具** |
| 3 | `<数值ID>+<login>[bot]@users.noreply.github.com` | 任何 GitHub App bot |
| 4 | 邮箱 local-part 里 `bot` 作为分隔符 token | `bot@` / `ai-bot@` / `my_bot@` … |

第 2/3/4 层是**结构规则，零维护** —— 新 AI 工具无需追加名单。

## 扩展规则

编辑 `lib/ai-signature-rules.sh` 顶部的 `AI_SIG_DOMAINS` / `AI_SIG_EXACT_MAILS` /
`AI_SIG_BOT_IDS`。追加前请确认标识有**一手证据**（官方源码 / GitHub Users API），不要凭印象添加。

改完必须复跑回归，全历史应为 **0 命中**：

```bash
. .githooks/lib/ai-signature-rules.sh && ai_sig_check_range HEAD && echo OK
```

### 两条最容易踩的坑

1. **只匹配署名结构，绝不对正文做子串匹配。** 裸词 `cursor` / `codex` / `amp` / `continue`
   禁止进入正则 —— `android.database.Cursor` 真实存在于本仓库代码；`continue` 是编程关键字；
   历史提交 `fa776494`「为 Claude Code, Cursor, GitHub Copilot, Gemini 和 Windsurf 提供集成…」
   是**合法的人类提交**。讨论 AI 的提交必须放行，只禁止「以 AI 身份署名」。
2. **绝不单独匹配 `@users.noreply.github.com`** —— 那是所有开启邮箱隐私保护的**真人**用户的
   通用邮箱域，单独匹配会误伤全部正常提交。必须锚定「数值 ID」或「`[bot]` 后缀」。

## 绕过路径与覆盖情况

| 绕过路径 | 本地 hook | CI | 说明 |
|---|---|---|---|
| 正常 `git commit` | ✅ 即时拦 | ✅ | |
| `git commit --no-verify` | ❌ 漏 | ✅ 拦 | 本地 hook 的**结构性上限**，故 CI 必需 |
| `git push --no-verify` | ❌ 漏 | ✅ 拦 | |
| GitHub 网页端编辑 | ❌ 漏 | ✅ 拦 | 不经本地 |
| 未设 `core.hooksPath` 的新 clone | ❌ 漏 | ✅ 拦 | |
| 纯 `.md` / `.trellis/**` 提交 | ✅ 拦 | ✅ 拦 | commit-guard 无 `paths-ignore`，故不漏 |
| 改写历史后 force push | ❌ 漏 | ✅ 拦 | CI 回退为全历史扫描 |
| `git commit -F`＋伪造 scissors 行藏署名 | ❌ 漏（仅 `commit-msg`） | ✅ 拦 | 见下方缺口说明；`pre-push` 也拦得住 |
| **直接 push 到 `dev`** | ✅ 拦 | ⚠️ **事后红** | 无分支保护 → 提交已落地才报错，需 `rebase -i` 清理后重推 |

**已知残留缺口**（记录，不假装不存在）：

- **无法事前拒收**直接 push 到 `dev` 的违规提交。缓解：`main` 开分支保护 + 把 `guard` 设为
  required status check（GitHub 侧设置，不随仓库版本化，需手动开启）。
- **squash merge** 的最终消息在 CI 通过**之后**才由 GitHub 生成 → CI 校验不到。当前单人直推
  无风险；若将来启用 PR + squash 流程需重新评估。
- 未知 AI 若**既不写 trailer、又非 GitHub bot、邮箱也不含 `bot` token**（如 `<agent@newai.com>`），
  四层均漏。堵它需 author 白名单，本次未采用（会拒绝外部贡献者 PR）。
- **`commit-msg` 会漏掉「藏在伪造 scissors 行之后的署名」**（已实测复现）。成因：`commit-msg`
  必须剥掉 scissors 之后的内容，否则 `git commit -v` 的 diff 会让规则文件**自我误报**；
  而 `git commit -F` / `-m` 默认 `cleanup=whitespace`，git 自己**不剥**，署名遂原样落进历史。
  不修的理由：`commit-msg` 本就能被 `--no-verify` 一键跳过，能伪造 scissors 的人用 `--no-verify`
  更省事，堵它不增加真实安全性；且 `pre-push` 与 CI 读的是**已落盘的原始消息**（不经剥离），
  两者均已实测拦得住 → 洞只在最内层，外两层封死。详见 `lib/ai-signature-rules.sh` 中
  `_ai_sig_clean_message` 的注释。

**将来需要开豁免口子的情况**：启用 Dependabot / Renovate（其提交会被第 3 层拒绝）；
引入真人协作者的 `Co-authored-by`（第 2 层）；workflow 以 `github-actions[bot]` 身份提交（第 3 层）。
