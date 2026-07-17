# Research: 禁止 AI 署名的工程强制手段对比

- **Query**: 在 GitHub 托管仓库中，强制「提交信息与作者身份不得包含 AI 署名」的工程实现手段对比
- **Scope**: mixed（内部仓库实测为主 + 外部工具知识）
- **Date**: 2026-07-17

## TL;DR

推荐 **方案 B：`.githooks/` 本地 hook + 独立 CI workflow 兜底**，不引入 Node/Python 工具链。

三条本仓库实测结论会直接改变设计，务必先读：

1. **朴素子串匹配在本仓库产生 2 个误报**（真实提交），必须用「署名结构锚定」正则 —— 见 [致命误报](#致命误报朴素正则在本仓库真实误报)。
2. **`check.yml` 的 `push.paths-ignore` 含 `.trellis/**` 与 `**.md`**，把校验塞进 `check.yml` 会漏掉纯文档提交 —— 必须新建独立 workflow。
3. **本仓库 `core.autocrlf=true` 且无 `.gitattributes`**，hook 脚本会被检出成 CRLF；Git-for-Windows 能容忍，Linux 会 `bad interpreter: /bin/sh^M`。

---

## 本仓库现状（已实测核实）

| 事实 | 值 | 核实方式 |
|---|---|---|
| 提交总数 | 736 | `git rev-list --count HEAD` |
| AI 署名基线（锚定正则） | **0 命中**，耗时 0.23s | 单趟 `git log` 扫描 |
| 开发者身份 | 仅 `anjiemo <2695734816@qq.com>`（author 与 committer 一致） | `git log --format='%an <%ae> \| %cn <%ce>' \| sort -u` |
| 历史中的 trailer | **完全没有任何 trailer** | `git log --format='%(trailers:key=Co-authored-by)'` 全空 |
| `core.hooksPath` | 未设置（`git config` 退出码 1） | — |
| `.githooks/` | 不存在 | — |
| `core.fileMode` | **false**（Windows） | `git config core.fileMode` |
| `core.autocrlf` | **true**，且**无 `.gitattributes`** | 全仓库 `find` 无结果 |
| Git 版本 | 2.53.0.windows.3（`%(trailers)` 等新语法可用） | `git --version` |
| 远端 | `github.com/anjiemo/SunnyBeach`，**PUBLIC**，**个人账号**（非 org） | `gh repo view --json` |
| 现有 ruleset | **`[]` 空** | `gh api repos/anjiemo/SunnyBeach/rulesets` |
| `dev` 分支保护 | **无**（404 Branch not protected） | `gh api .../branches/dev/protection` |
| 现有自定义 Gradle task | 无（`tasks.register` 全仓库 0 命中） | grep |

> 结论：**服务端目前零约束**，`dev` 可被直接 push。基线干净意味着可以对**全历史**生效，无需豁免名单。

---

## 致命误报：朴素正则在本仓库真实误报

这是本次调研最重要的发现。对 736 条真实历史做对比扫描：

| 正则策略 | 命中数 | 结论 |
|---|---|---|
| 朴素子串 `cursor\|gemini\|copilot\|claude\|codex`（全文任意位置） | **2** | 全是**误报** |
| 署名结构锚定（见下） | **0** | 正确 |

两条被误伤的**合法**提交：

```
264124a5  chore: 删除 Windsurf AI IDE 配置以精简项目结构
fa776494  feat(workflow): 初始化 Trellis AI 协作工作流及多平台适配环境
          └─ 正文含：「部署多平台适配层，为 Claude Code, Cursor, GitHub Copilot,
                      Gemini 和 Windsurf 提供集成的 Hooks、Skills 与指令集」
```

放大风险的两点：

- 本仓库是 **Android 项目**，`android.database.Cursor` 真实存在于 `ImageSelectActivity.kt`、`VideoSelectActivity.kt`，未来「fix: 查询后关闭 Cursor」这类提交必然出现 → 朴素 `cursor` 规则会直接卡住正常开发。
- 本任务本身就在 `.trellis/` 里讨论 AI 署名，**讨论 AI 的提交必须允许**，只禁止「以 AI 身份署名」。

**设计铁律：只匹配署名的结构（trailer 行首、身份字段、固定生成语），绝不对正文做自由子串匹配。**

已验证的锚定正则（0 误报 / 0 漏报）：

```bash
# 消息侧：必须行首锚定 ^，限定在 trailer 形态
'^[[:space:]]*co-authored-by:[[:space:]]*.*(claude|copilot|anthropic|cursor|chatgpt|openai|gemini|codex|windsurf)'
'^[[:space:]]*(🤖[[:space:]]*)?generated with[[:space:]]+\[?(claude|codex|cursor)'
'noreply@anthropic\.com'

# 身份侧：只扫 %an/%ae/%cn/%ce 四个字段，不碰正文
'(claude|copilot|anthropic|chatgpt|openai)'
```

---

## 1. 版本化 git hooks 的分发

### 机制

`core.hooksPath` 是**本地 config，无法随仓库提交**。每个 clone 必须自己设置一次：

```bash
git config core.hooksPath .githooks
```

- 设置后 **完全取代** `.git/hooks/`（不是叠加）—— 该目录下现有 hook 全部失效。本仓库 `.git/hooks/` 只有 `.sample`，无影响。
- 仓库里的 `.githooks/` 是普通受版本控制的目录，随 clone 分发；只有「启用」这一步是本地的。

### Windows 实测结果（scratch 仓库真跑）

| 项 | 结果 |
|---|---|
| hook 无可执行位能否运行 | **能**。`core.fileMode=false`，Git-for-Windows 通过 `sh` 调用，不看 x 位。已实测 commit-msg 成功拦截 |
| Windows `git add` 记录的 mode | **`100644`**（非可执行）—— 实测 `git ls-files -s` |
| 修正方式 | `git update-index --add --chmod=+x .githooks/commit-msg` → 变为 `100755`（已实测） |
| CRLF shebang | Git-for-Windows 的 sh **容忍** CRLF（实测 hook 正常执行）；**Linux/macOS 会 `bad interpreter: /bin/sh^M`** |

> **两个跨平台坑（本仓库必踩）**
> 1. 在 Windows 上 `git add` hook → mode `100644` → Linux/macOS 同事 clone 后 hook **静默不执行**（不报错，直接跳过）。必须 `--chmod=+x` 提交。
> 2. `core.autocrlf=true` + 无 `.gitattributes` → hook 被检出成 CRLF。当前单人 Windows 无感，但一旦有 Linux 贡献者或想在 CI 复用同一脚本就会炸。
>
> **必须同时提交 `.gitattributes`：**
> ```gitattributes
> .githooks/** text eol=lf
> ```

### Gradle 自动安装（Android 项目常见做法）

本仓库 `settings.gradle.kts` 有 `includeBuild("build-logic")`，`build-logic/convention/` 存在 convention 插件（`ProjectConfigConventionPlugin` 等），但**全仓库无任何 `tasks.register`** —— 这会是第一个自定义 task。

最省事、且能覆盖「sync 时自动装上」的做法是在 `settings.gradle.kts` 配置期直接执行（幂等、成本约等于 0）：

```kotlin
// settings.gradle.kts 末尾
// 自动启用版本化 git hooks（幂等；CI 与非 git 环境自动跳过）
if (System.getenv("CI").isNullOrBlank() && rootDir.resolve(".git").exists()) {
    val hooksDir = rootDir.resolve(".githooks")
    if (hooksDir.isDirectory) {
        runCatching {
            val current = providers.exec {
                commandLine("git", "config", "--get", "core.hooksPath")
                isIgnoreExitValue = true
            }.standardOutput.asText.get().trim()
            if (current != ".githooks") {
                providers.exec {
                    commandLine("git", "config", "core.hooksPath", ".githooks")
                }.result.get()
            }
        }
    }
}
```

要点：
- **必须 `runCatching` 包住**：没装 git / 非 git 环境（如源码 zip）不能让 sync 失败。
- **必须跳过 CI**：CI 不需要 hook，且 `.githooks` 会拖慢/干扰。
- 配置期执行 → **每次 sync 和每次 build 都会校正**，开发者无需记忆任何命令。这是相对「写进 README 让人手动执行」的核心优势。
- 权衡：配置期做 I/O 不够「Gradle 正统」（理想是 task + 显式调用），但本仓库单人 + 只求 sync 即生效，此处简单性胜过纯洁性。

---

## 2. Hook 选型

| Hook | 入参 | 能拿到最终 message？ | 能拿到 author 身份？ | 适用性 |
|---|---|---|---|---|
| `prepare-commit-msg` | `$1`=msg 文件, `$2`=来源, `$3`=SHA | ❌ 用户**尚未编辑** | ❌ | 适合**注入/删除** trailer，不适合校验 |
| `pre-commit` | 无参 | ❌ **消息还不存在** | ⚠️ 只能读 config，非最终值 | 不适合本任务 |
| **`commit-msg`** | **`$1`** = msg 文件路径 | ✅ **最终消息**（编辑后、生成 commit 前） | ⚠️ 间接（`git var GIT_AUTHOR_IDENT`） | ✅ **本任务首选** |
| `pre-push` | stdin: `<local ref> <local sha> <remote ref> <remote sha>` | ✅ 可遍历范围内所有提交 | ✅ 完整 | ✅ **推荐叠加**，可拦住整批历史 |

关键细节：

- `commit-msg` 的 `$1` 是**文件路径**，不是内容 —— 必须 `grep ... "$1"`。
- `commit-msg` 里拿 author 身份要靠 `git var GIT_AUTHOR_IDENT`，它会尊重 `GIT_AUTHOR_NAME/EMAIL` 环境变量与 `--author`。但 **`--author` 传入的值在 `commit-msg` 阶段不体现在 `git var`** ——这是 `commit-msg` 校验身份的真实局限，因此**身份校验的可靠位置是 pre-push 和 CI**。
- `commit-msg` **不会**在 `git merge`（无冲突快进）、`git rebase` 重放旧提交等路径上稳定触发 → 历史里的旧署名可被 rebase 带入而不触发。
- **`--no-verify` / `-n` 完全跳过 `commit-msg` 与 `pre-push`**（已实测：带 `Co-Authored-By: Claude` 的提交 `--no-verify` 后成功落盘）。这是本地 hook 的**结构性上限**，无法在本地修补 → **CI 兜底不是可选项，是必需项**。

---

## 3. CI 侧校验

### 必须新建独立 workflow（不要塞进 `check.yml`）

`check.yml` 实测配置：

```yaml
on:
  push:
    branches: [ "main", "dev" ]
    paths-ignore:
      - '**.md'          # ← 漏洞
      - '.trellis/**'    # ← 漏洞
  pull_request:
    branches: [ "main", "dev" ]   # ← 无 paths-ignore，PR 总会跑
```

- **push 侧有 `paths-ignore`** → 一条只改 `.md` 或 `.trellis/**` 的提交（哪怕带 AI 署名）**直接跳过整个 workflow**。本任务自身产物就在 `.trellis/` 下，命中率极高。
- `check.yml` 是 30 分钟的 Gradle 编译，把秒级的署名校验绑在上面浪费且拖慢反馈。
- 因此：**新建 `.github/workflows/commit-guard.yml`，不设任何 `paths-ignore`**。

`fetch-depth: 0` 说明：`check.yml` 已经在用（`actions/checkout@v5` + `fetch-depth: 0`）。新 workflow **同样必需** —— 默认 `fetch-depth: 1` 只有一个提交，`base..head` 会直接 `bad revision`。

### 范围取法（4 种边界已实测）

| 场景 | `github.event.before` | 正确处理 |
|---|---|---|
| 普通 push | 真实 SHA | `before..sha` |
| **新分支首推** | **全 0** `000...0` | 回退，不能用 `before..` |
| **force push** | 指向**已被重写、不存在**的 SHA | `git cat-file -e` 探测后回退 |
| PR | — | `pull_request.base.sha..pull_request.head.sha` |

> force-push 那一格是最容易漏的：`before` 看起来是合法 40 位 SHA，但 `git rev-list before..head` 会因对象不存在而 `fatal: bad revision`，让 job 红成一片假警报。**必须先 `git cat-file -e "$BEFORE^{commit}"` 探测。**（已实测四种分支全部走对）

### 可直接用的 workflow 骨架

```yaml
# .github/workflows/commit-guard.yml
# 禁止提交信息 / 作者身份出现 AI 署名。刻意不设 paths-ignore：文档提交同样受约束。
name: 提交署名校验

on:
  push:
    branches: [ "main", "dev" ]
  pull_request:
    branches: [ "main", "dev" ]
  workflow_dispatch:

concurrency:
  group: commit-guard-${{ github.ref }}
  cancel-in-progress: true

jobs:
  guard:
    runs-on: ubuntu-latest
    timeout-minutes: 5
    permissions:
      contents: read
    steps:
      - name: 检出代码
        uses: actions/checkout@v5
        with:
          fetch-depth: 0        # 必需：默认 depth=1 会让 base..head 变成 bad revision

      - name: 校验提交署名
        env:
          PR_BASE: ${{ github.event.pull_request.base.sha }}
          PR_HEAD: ${{ github.event.pull_request.head.sha }}
          PUSH_BEFORE: ${{ github.event.before }}
        run: |
          set -euo pipefail

          if [ "${{ github.event_name }}" = "pull_request" ]; then
            RANGE="${PR_BASE}..${PR_HEAD}"
          elif [ "${{ github.event_name }}" = "push" ] \
               && [ -n "${PUSH_BEFORE:-}" ] \
               && ! echo "${PUSH_BEFORE}" | grep -qE '^0{40}$' \
               && git cat-file -e "${PUSH_BEFORE}^{commit}" 2>/dev/null; then
            RANGE="${PUSH_BEFORE}..${{ github.sha }}"
          else
            # 新分支 / force-push / 手动触发：退化为「不在其他远端分支上的提交」
            RANGE="${{ github.sha }} --not --remotes=origin"
            RANGE="${RANGE} ^refs/remotes/origin/main ^refs/remotes/origin/dev"
          fi
          echo "校验范围: ${RANGE}"

          MSG_RE='^[[:space:]]*co-authored-by:[[:space:]]*.*(claude|copilot|anthropic|cursor|chatgpt|openai|gemini|codex|windsurf)|^[[:space:]]*(🤖[[:space:]]*)?generated with[[:space:]]+\[?(claude|codex|cursor)|noreply@anthropic\.com'
          ID_RE='(claude|copilot|anthropic|chatgpt|openai|noreply@anthropic\.com)'

          FAIL=0
          for sha in $(git rev-list ${RANGE}); do
            SUBJ=$(git log -1 --format='%s' "$sha")
            if git log -1 --format='%B' "$sha" | grep -qiE "${MSG_RE}"; then
              echo "::error::提交 ${sha} 的信息含 AI 署名: ${SUBJ}"
              FAIL=1
            fi
            if git log -1 --format='%an <%ae>%n%cn <%ce>' "$sha" | grep -qiE "${ID_RE}"; then
              echo "::error::提交 ${sha} 的 author/committer 身份含 AI 署名: ${SUBJ}"
              FAIL=1
            fi
          done

          if [ "${FAIL}" -ne 0 ]; then
            echo "校验失败：请用 git rebase -i / git commit --amend 清除 AI 署名后重推。" >&2
            exit 1
          fi
          echo "✅ 范围内提交均无 AI 署名"
```

> **消息侧与身份侧必须都查**：实测构造过一条「消息干净但 author 是 `Claude <noreply@anthropic.com>`」的提交，**只查消息会完全放过它**。
>
> 性能：736 条全历史用单趟 `git log` 扫描仅 **0.23s**；但**逐提交 `for` 循环跑 736 条会超时**（实测 >2min，每条 fork 两个 git 进程）。上面骨架按增量范围跑（PR 通常几条到几十条）没问题；**若要扫全历史，务必改成单趟 `git log --format` + `grep`，不要循环。**

---

## 4. 现成工具取舍

前提：本仓库是 **纯 Android/Kotlin/Gradle** 项目，**没有 Node、没有 Python 依赖**，团队 1 人 Windows。引入任何新语言工具链 = 新的版本管理、CI 安装步骤、锁文件。

| 工具 | 依赖 | 对本仓库的判断 |
|---|---|---|
| **commitlint** | Node + `package.json` + `node_modules` | ❌ **不引入**。为一条正则引入整套 Node 工具链；强项是 Conventional Commits 结构校验，本任务不需要。想约束 commit 格式再另议 |
| **gitlint** | Python + pip | ❌ **不引入**。同上，且 Windows 上 Python 环境是额外负担 |
| **pre-commit framework** | Python | ❌ **不引入**。它管理的是 `pre-commit` 钩子生态，而本任务核心是 `commit-msg` + 身份校验，5 行 shell 足够 |
| **gitleaks** | Go 二进制 | ❌ **场景不符**。它扫的是密钥泄漏，不是署名。若将来要做 secret scanning 可另开任务 |
| **`git interpret-trailers`** | **git 内置，零依赖** | ⚠️ **可选**。`--parse` 能结构化解出 trailer，比 grep 严谨（正确处理折行/续行）。但实测本仓库历史 **trailer 完全为空**，且 AI 署名恰恰以标准 trailer 形式出现，`^co-authored-by:` 行首锚定已足够。**保留为增强项，不作首选** |

**结论：零新依赖。** 用 `git` + `grep` + `sh`（Git Bash 与 ubuntu-latest 都自带），本地与 CI 复用同一份脚本。

> 若确实想更严谨地解析 trailer，纯内置增强写法：
> ```bash
> git log -1 --format='%(trailers:key=Co-authored-by,valueonly)' "$sha" | grep -qiE "$ID_RE"
> ```
> （已验证 git 2.53 支持该 pretty 格式；本仓库当前输出为空。）

---

## 5. 服务端强制

| 手段 | 本仓库可用性 |
|---|---|
| **`pre-receive` hook** | ❌ **不可用**。github.com 不开放 pre-receive；仅 **GitHub Enterprise Server** 支持。本仓库在 github.com → 排除 |
| **Branch protection（经典）** | ✅ 可用（公开仓库免费）。但**不能校验 commit 内容/身份**，只能挂 required status checks |
| **Rulesets → Metadata restrictions** | ⚠️ **需实测确认**（见下） |
| **Required status checks** | ✅ **兜底 `--no-verify` 的关键**。把 `commit-guard` 设为 required 后，绕过本地 hook 的提交仍会卡在 PR 合并前 |

### Rulesets metadata restrictions（本次未能确证，标记为待验证）

GitHub Rulesets 提供 metadata 类规则，理论上正好命中本任务：

- `commit_message_pattern`
- `commit_author_email_pattern`
- `committer_email_pattern`

每条支持 `starts_with / ends_with / contains / regex` + `negate`（取反 → 「不得包含」）。

**不确定点**：这些 metadata 规则历史上被限定为「**组织所有**的仓库 + GitHub Team/Enterprise 套餐」。本仓库是 **个人账号下的公开仓库**（`owner.id` 为 `MDQ6VXNlcjQ2Mjc0MTc1`，User 类型），**很可能不具备该能力**。

- 我**没有**创建 ruleset 去实测 —— 那会真实改动你仓库的设置，属于需要你授权的动作。
- 我也**无法联网**核对当前文档措辞，故不臆断。

**建议的最小验证命令**（会真实创建一条 ruleset，请自行决定是否执行；`enforcement: disabled` 使其不生效，验证后可删）：

```bash
gh api -X POST repos/anjiemo/SunnyBeach/rulesets \
  -f name='ai-signature-guard' \
  -f target='branch' \
  -f enforcement='disabled' \
  -F 'conditions[ref_name][include][]=~DEFAULT_BRANCH' \
  -f 'rules[0][type]=commit_message_pattern' \
  -f 'rules[0][parameters][operator]=regex' \
  -f 'rules[0][parameters][pattern]=(?i)co-authored-by:.*(claude|copilot|anthropic)' \
  -F 'rules[0][parameters][negate]=true' \
  -f 'rules[0][parameters][name]=禁止 AI 署名'
# 成功 → 该能力可用；422/403 且提示 plan/org → 不可用，退回 CI 兜底
# 清理：gh api -X DELETE repos/anjiemo/SunnyBeach/rulesets/<id>
```

即便可用，仍需注意其固有局限：**仓库管理员（即你本人）默认可 bypass**；且**不作用于 GitHub 自身生成的 merge commit**。因此 **rulesets 是加分项，不能取代 CI**。

---

## 绕过路径 × 各方案覆盖矩阵

| 绕过路径 | 仅本地 hook | 仅 CI | **B: hook+CI** | C: +Ruleset |
|---|---|---|---|---|
| 正常 `git commit` | ✅ 拦 | ✅ 拦 | ✅ 即时拦 | ✅ |
| **`git commit --no-verify`**（已实测可绕过 hook） | ❌ **漏** | ✅ 拦 | ✅ CI 拦 | ✅ 推送即拒 |
| 直接 push 到 `dev`（当前无保护） | ❌ 漏 | ⚠️ 事后红（已进 dev） | ⚠️ 事后红 | ✅ **事前拒** |
| 纯 `.md` / `.trellis/**` 提交 | ✅ 拦 | ❌ **漏**（若复用 `check.yml` 的 paths-ignore） | ✅ 独立 workflow 无 paths-ignore | ✅ |
| **GitHub 网页端编辑**（不经本地） | ❌ **漏** | ✅ 拦 | ✅ 拦 | ✅ |
| 未设 `core.hooksPath` 的新 clone | ❌ **漏** | ✅ 拦 | ✅ 拦 | ✅ |
| Linux clone + Windows 提交的 644 hook | ❌ **静默漏** | ✅ 拦 | ✅ 拦 | ✅ |
| 管理员 bypass | — | — | ⚠️ 可关 check | ⚠️ 默认可 bypass |
| 改写历史后 force-push | ❌ 漏 | ✅ 拦（需 `cat-file` 兜底） | ✅ 拦 | ✅ |

**关键不对称**：本地 hook 覆盖不了「网页端 / 新 clone / `--no-verify`」，CI 覆盖不了「即时反馈 / 提交前」。二者互补，故 B 是最小完备组合。

---

## 三个可落地方案

### 方案 A：仅 CI

- **如何工作**：只加 `commit-guard.yml`。
- **优点**：改动最小（1 个文件）；零本地配置；对新 clone、网页端编辑天然生效；不可被 `--no-verify` 绕过。
- **缺点**：反馈**滞后**——提交已进 `dev` 才红；修复要 `rebase -i` 改写已推历史（单人仓库尚可，多人即灾难）。
- **适配度**：⭐⭐⭐⭐ 保底可用。若只想投入 10 分钟，先上这个。

### 方案 B：本地 hook + CI 兜底 ✅ **推荐**

- **如何工作**：
  1. `.githooks/commit-msg`（+ 可选 `.githooks/pre-push`）—— 提交瞬间拦截；
  2. `.gitattributes` 加 `.githooks/** text eol=lf`；
  3. hook 以 `--chmod=+x` 提交（mode 100755）；
  4. `settings.gradle.kts` 配置期幂等设置 `core.hooksPath`（sync 即自动装上）；
  5. `commit-guard.yml` 兜底 `--no-verify` 与网页端；
  6. （建议）给 `main`/`dev` 开分支保护 + 把 `guard` 设为 required check。
- **优点**：**秒级本地反馈**，问题在提交那一刻就被挡住，永不进历史；CI 补齐所有绕过路径；**零新依赖**，本地/CI 同一份 shell；Gradle 自动安装 → 无需记忆命令。
- **缺点**：文件多几个；`core.hooksPath` 会**覆盖** `.git/hooks/`（本仓库只有 sample，无损）；配置期执行 git 命令不够 Gradle 正统。
- **适配度**：⭐⭐⭐⭐⭐ **最契合**。Windows+Git Bash 已实测全绿；基线 0 命中可直接对全历史生效；无 Node/Python 污染。

### 方案 C：B + GitHub Ruleset

- **如何工作**：在 B 之上加 metadata ruleset，让服务端在 **push 时直接拒收**。
- **优点**：唯一能挡住「直接 push 到 dev」的**事前**手段；不依赖 clone 是否配置正确。
- **缺点**：**可用性未确证**（个人账号公开仓库很可能不支持，见上）；管理员默认可 bypass；规则写在 GitHub 设置里、**不随仓库版本化**，与 `.trellis` 的「配置即代码」取向相悖。
- **适配度**：⭐⭐ 先按上面命令验证可用性再说；不可用则停留在 B，损失很小（B 已覆盖除「事前拒收」外的全部路径）。

---

## 关键代码骨架

### `.githooks/commit-msg`

```sh
#!/bin/sh
# 禁止 AI 署名。$1 = 提交信息文件路径（注意：是路径，不是内容）
# 注意：只匹配「署名结构」，不对正文做子串匹配——本仓库有讨论 Cursor/Copilot 的合法提交。
set -eu

MSG_FILE="$1"

MSG_RE='^[[:space:]]*[Cc]o-[Aa]uthored-[Bb]y:[[:space:]]*.*([Cc]laude|[Cc]opilot|[Aa]nthropic|[Cc]ursor|[Cc]hat[Gg][Pp][Tt]|[Oo]pen[Aa][Ii]|[Gg]emini|[Cc]odex|[Ww]indsurf)'
GEN_RE='^[[:space:]]*(🤖[[:space:]]*)?[Gg]enerated with[[:space:]]+\[?([Cc]laude|[Cc]odex|[Cc]ursor)'
MAIL_RE='noreply@anthropic\.com'

if grep -qE "$MSG_RE" "$MSG_FILE" \
   || grep -qE "$GEN_RE" "$MSG_FILE" \
   || grep -qE "$MAIL_RE" "$MSG_FILE"; then
  echo "✗ 提交被拒：提交信息含 AI 署名（Co-Authored-By / Generated with）。" >&2
  echo "  请删除该署名行后重试。" >&2
  exit 1
fi

# 身份兜底：git var 会反映 GIT_AUTHOR_NAME/EMAIL 环境变量
IDENT="$(git var GIT_AUTHOR_IDENT 2>/dev/null || true)"
if echo "$IDENT" | grep -qiE '(claude|copilot|anthropic|chatgpt|openai)'; then
  echo "✗ 提交被拒：author 身份含 AI 署名：$IDENT" >&2
  exit 1
fi

exit 0
```

### `.githooks/pre-push`（可选增强：整批拦截）

```sh
#!/bin/sh
# stdin 每行: <local ref> <local sha> <remote ref> <remote sha>
set -eu
NULL_SHA='0000000000000000000000000000000000000000'
ID_RE='(claude|copilot|anthropic|chatgpt|openai)'
MSG_RE='^[[:space:]]*[Cc]o-[Aa]uthored-[Bb]y:.*([Cc]laude|[Cc]opilot|[Aa]nthropic)|noreply@anthropic\.com'
FAIL=0

while read -r _local_ref local_sha _remote_ref remote_sha; do
  [ "$local_sha" = "$NULL_SHA" ] && continue          # 删除分支
  if [ "$remote_sha" = "$NULL_SHA" ]; then
    RANGE="$local_sha --not --remotes"                # 新分支
  else
    RANGE="$remote_sha..$local_sha"
  fi
  for sha in $(git rev-list $RANGE); do
    if git log -1 --format='%B' "$sha" | grep -qE "$MSG_RE"; then
      echo "✗ $sha 信息含 AI 署名: $(git log -1 --format='%s' "$sha")" >&2; FAIL=1
    fi
    if git log -1 --format='%an <%ae>%n%cn <%ce>' "$sha" | grep -qiE "$ID_RE"; then
      echo "✗ $sha 身份含 AI 署名: $(git log -1 --format='%s' "$sha")" >&2; FAIL=1
    fi
  done
done

[ "$FAIL" -eq 0 ] || { echo "推送被拒，请先 rebase 清理署名。" >&2; exit 1; }
exit 0
```

### 落地清单

```bash
# 1. 提交 hook 并保证 Linux 可执行（Windows 上 git add 默认是 644！）
git update-index --add --chmod=+x .githooks/commit-msg .githooks/pre-push

# 2. 锁定换行符（本仓库 core.autocrlf=true 且无 .gitattributes）
printf '.githooks/** text eol=lf\n' >> .gitattributes

# 3. 本地启用（Gradle sync 也会自动做）
git config core.hooksPath .githooks

# 4. 自检：全历史应为 0（实测 0.23s）
git log --format='%H%n%an <%ae>%n%cn <%ce>%n%B%n@@' HEAD |
  grep -icE '^[[:space:]]*co-authored-by:.*(claude|copilot|anthropic)|noreply@anthropic\.com'
```

---

## Related Specs

- `.github/workflows/check.yml` — 现有 CI；**其 `push.paths-ignore` 含 `.trellis/**` 与 `**.md`**，故新校验须独立成文件
- `.github/workflows/build.yml` — 现有构建流水线，本任务不涉及
- `settings.gradle.kts` — `includeBuild("build-logic")`，hook 自动安装的挂载点
- `build-logic/convention/src/main/kotlin/` — convention 插件目录；当前无任何 `tasks.register`

## Caveats / Not Found

- **未确证（最大开放问题）**：GitHub Rulesets 的 metadata restrictions 对**个人账号公开仓库**是否可用。本次无联网核对文档，且**刻意未创建 ruleset**（那是对你仓库设置的真实改动，需你授权）。方案 C 的可行性**取决于该验证结果**；已在上文给出可直接跑的验证与清理命令。
- **`--author` 与 `commit-msg` 的交互未实测**：`git commit --author="Claude <...>"` 在 `commit-msg` 阶段是否被 `git var GIT_AUTHOR_IDENT` 反映，我基于机制推断为「不反映」，但未构造用例验证。这不影响结论（身份校验的可靠层本就是 pre-push + CI），但若要依赖 hook 层的身份检查则需补测。
- **squash merge 的生成消息未验证**：GitHub 在 squash 合并时会聚合各提交的 `Co-authored-by` trailer，且最终 squash 消息在 CI 通过**之后**才生成 → **CI 无法校验最终 squash commit**。本仓库当前无分支保护、单人直推，暂不构成实际风险；若将来启用 PR + squash 流程需重新评估。
- **`git var GIT_AUTHOR_IDENT` 在 hook 中的输出格式**未逐字段实测（含时间戳），正则按子串匹配故不受影响。
- 工具对比（commitlint/gitlint/pre-commit/gitleaks）基于既有知识，**未联网核对各自最新版本能力**；但结论「不引入」源于依赖成本这一与版本无关的判断，不受影响。
- 本文所有标注「已实测」的结论均在 Windows + Git Bash + git 2.53.0.windows.3 下、于临时 scratch 仓库或本仓库只读扫描中真实执行验证。
