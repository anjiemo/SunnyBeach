# Git Hooks / Commit Guard

> Executable contract for the AI-signature commit guard: versioned hooks + CI backstop.
> **Read the SIGPIPE gotcha before writing any git hook in this repo.**

---

## Scope / Trigger

Applies when changing:

- `.githooks/**` — `commit-msg`, `pre-push`, `lib/ai-signature-rules.sh`
- `.github/workflows/commit-guard.yml`
- `.gitattributes` — the `.githooks/** text eol=lf` line
- `settings.gradle.kts` — the `core.hooksPath` bootstrap block
- **Any new git hook added to this repo** (the SIGPIPE + mode + EOL contracts below are hook-generic)

## Signatures

```sh
# Rule library — single source of truth. SOURCED by all 3 call sites, never executed.
. .githooks/lib/ai-signature-rules.sh

# commit-msg: $1 = path to the message FILE (not its content)
.githooks/commit-msg <msg-file>

# pre-push: stdin lines = "<local ref> <local sha> <remote ref> <remote sha>"
.githooks/pre-push <remote-name> <remote-url>
```

Index modes — enforce with `git update-index --add --chmod=+x`:

| Path | Mode | Why |
|---|---|---|
| `.githooks/commit-msg` | `100755` | executed by git directly |
| `.githooks/pre-push` | `100755` | executed by git directly |
| `.githooks/lib/ai-signature-rules.sh` | `100644` | **sourced** — POSIX `.` needs read, not exec. Do not "fix" this to 755 |

## Contracts

Four enforcement layers. Each covers what the others structurally cannot — none is redundant.

| Layer | Rule | Covers |
|---|---|---|
| 1 | Confirmed identities: domains `anthropic.com` `cursor.com` `aider.chat` `ampcode.com`; full addresses `copilot@github.com` `codex@openai.com`; enumerated GitHub bot numeric IDs; footers `Generated with [Claude Code]` (**both** `claude.ai/code` and `claude.com/claude-code`), `Replit-Commit-Author:`, `Replit-Commit-Session-Id:`, `aider:` subject prefix, `(aider)` name suffix | known tools |
| 2 | Reject **any** `^[[:space:]]*co-authored-by:` | unknown / future AI |
| 3 | Identity matching `[0-9]+\+[^@]*\[bot\]@users\.noreply\.github\.com` | any GitHub App bot |
| 4 | Local-part token `(^\|[._+-])bot([._+-]\|$)`, case-insensitive | bot-named addresses |

**Match sites**: trailer-shaped lines + the fixed footers in the message body, and identity fields `%an/%ae/%cn/%ce`. **Never free-text prose.**

**Scope limit**: layer 2 targets `Co-authored-by:` only. `Signed-off-by:` and other trailer keys are untouched.

**Dependencies**: `git`, `grep`, `sh`. No Node, no Python, no external actions.

## Validation & Error Matrix

| Condition | Behavior |
|---|---|
| Message or identity hits any layer | exit 1 + diagnostic on stderr |
| **CI range cannot be resolved** | **exit 1 (fail-closed)** — never skip silently |
| push `before` is all-zero (new branch) | fall back to full-history scan |
| push `before` object missing (force-push) | detect with `git cat-file -e "$BEFORE^{commit}"`, then fall back to full-history scan |
| Gradle bootstrap: no git / not a git dir | `runCatching` → skip; must not fail sync |
| Gradle bootstrap: `CI` env set | skip hook install |
| `core.hooksPath` already `.githooks` | no-op (idempotent) |

## Good / Base / Bad

- **Good**: `fix: 查询后关闭 Cursor` → **passes**. Prose naming a tool is legitimate; only signing *as* an AI is forbidden.
- **Base**: `Co-Authored-By: Claude <noreply@anthropic.com>` → rejected by `commit-msg` at commit time.
- **Bad**: matching bare `cursor` anywhere in the message → false-positives on real commits (measured: 2 in this repo's history).

## Tests Required

- **Regression (hard gate)**: full-history scan yields **0 hits**. Any hit means the pattern is wrong — fix the pattern, never the history.
- **Control group (falsification, mandatory)**: naive `cursor|gemini|copilot|claude|codex` over the same history yields **2 hits** (`264124a5`, `fa776494`). *If the control also yields 0, the scan is not reading content* — investigate before trusting the 0.
- Assert **rejected**: `noreply@anthropic.com` trailer; both `Generated with [Claude Code]` URLs; a human `Co-authored-by:` (layer 2); identity `198982749+Copilot@users.noreply.github.com` (layer 1 only); `bot@` `Bot@` `BOT@` `ai-bot@` `bot.agent@` `my_bot@`.
- Assert **passed**: `abbot@` `botha@` `robot@` `chatbot@` `bots@`; human privacy address `12345+someone@users.noreply.github.com`; prose naming tools.
- Assert the hook still blocks when stderr is closed early (SIGPIPE, see below).
- **Perf**: full-history scan must be a single `git log --format` + `grep` pass (~0.2s). A per-commit loop over ~700 commits exceeds 2 minutes.
- Test in a scratch repo under the system temp dir — never against this repo's history.

## Wrong vs Correct

| Wrong | Correct |
|---|---|
| Hook writes multi-line stderr without `trap '' PIPE` | **`trap '' PIPE` first line** — see the SIGPIPE gotcha; without it the guard silently fails open |
| CI fallback range `${HEAD} --not --remotes=origin` | After `actions/checkout`, `origin/<branch>` already points at HEAD → range is **empty → silent pass**, precisely in the force-push case. Fall back to a full-history scan |
| Range resolution fails → skip the check | Fail **closed** (exit 1) |
| Apply the trailer regex `[A-Za-z][A-Za-z0-9_-]*:` to the subject line | Conventional Commits `fix:` / `feat:` are **the same shape as a trailer key**. Trailer rules apply to the body only |
| Match bare `cursor` / `codex` / `amp` / `continue` | Anchor on the trailer key + address. `android.database.Cursor` is real code here; `continue` is a keyword |
| Match a bare display name (`Claude`, `Devin`, `Jules`, `Cody`) | All are real human given names (GitHub user `claude`, id 81847, is `type: User`). Require the AI address to co-occur on the same line |
| Match `@users.noreply.github.com` alone | That is every privacy-enabled **human**'s address. Anchor on the numeric ID + login, or on `[bot]` |
| Assume layers 3/4 catch GitHub Copilot | `198982749+Copilot@users.noreply.github.com` has **no `[bot]` suffix and no `bot` token** — only the layer-1 enumeration catches it. Never drop the enumeration |
| Add `gemini-cli@google.com` | It is `SHADOW_REPO_AUTHOR_EMAIL` in gemini-cli's source — internal checkpoint shadow repo only, never reaches a user commit |
| `git add` a hook on Windows and assume it runs | Windows records `100644`; the hook then **silently never runs** on Linux. Use `git update-index --add --chmod=+x` |
| Ship hooks without `.gitattributes` | `core.autocrlf=true` checks them out CRLF → `bad interpreter: /bin/sh^M` on Linux. Pin `.githooks/** text eol=lf` |
| Put the check in `check.yml` | Its `push.paths-ignore` includes `**.md` and `.trellis/**` → doc-only commits skip it entirely. `commit-guard.yml` must stay standalone with **no `paths-ignore`** |
| Copy the trigger branch list from another workflow in this repo | Verify branches against the real remote — `git ls-remote --heads origin` / `gh repo view --json defaultBranchRef`. This repo's default branch is **`master`**; `main` does not exist. `commit-guard.yml` shipped as `[main, dev]`, leaving the release branch with **no guard at all** — implement, review, and coordination all missed it; only colliding the config with the real remote exposed it |
| `actions/checkout` with default depth | `fetch-depth: 0` is required; depth=1 makes `base..head` a `bad revision` |

---

## Gotcha: a hook killed by SIGPIPE is treated by git as SUCCESS

> **Warning**: If a hook process **dies from `SIGPIPE`**, git reads exit status 0 and **lets the commit through**. An explicit `exit 141` is correctly treated as failure — dying from the signal itself is not.

Measured on git 2.53:

| Hook ends by | git exit code | Commit |
|---|---|---|
| explicit `exit 141` | 1 | blocked |
| **killed by SIGPIPE** | **0** | **lands** |

Trigger: the hook writes several diagnostic lines to stderr and the reader closes the pipe early — `git commit 2>&1 | head -3`, or a GUI/IDE that reads only the first lines. Measured **5/5 leaks** without the guard, **0/5** with it. Clean commits are unaffected.

**Every hook in this repo must begin with:**

```sh
trap '' PIPE
```

## Gotcha: `git commit -v` puts the diff into the message file

> **Warning**: With `-v`, the file handed to `commit-msg` contains the entire diff after the scissors line. Because the rule library itself contains AI identifier strings, editing it would make the guard reject its own commit.

`_ai_sig_clean_message` strips the scissors section and comment lines before matching. Known consequence: content placed after the scissors line is invisible to `commit-msg`. `pre-push` and CI read the raw `git log --format=%B`, so both still catch it — the gap exists only at the layer `--no-verify` already bypasses. Accepted trade-off; do not "fix" it by removing the stripping (that reintroduces guaranteed self-rejection).

## Convention: rules live in exactly one file

`.githooks/lib/ai-signature-rules.sh` is the single source of truth. `commit-msg`, `pre-push`, and `commit-guard.yml` all **source** it. Never copy a pattern into a hook or the workflow — local/CI rule divergence is the failure mode this design exists to prevent.

## Convention: the identity list carries confirmed entries only

An entry earns a place only with tool source code, an authoritative GitHub Users API numeric ID, or ≥2 independent corroborating sources. Evidence table (per-entry source + confidence) lives in the task's `research/ai-signature-identities.md`.

> The GitHub **Commits** Search API does not do exact phrase matching and its `total_count` is unusable as evidence — a query for `"noreply@anthropic.com"` returns ~17.5M results whose top hit is an unrelated address. Use Code Search with a control group instead.

## Coverage limits (by design)

| Path | Caught by |
|---|---|
| `git commit --no-verify` | CI (`commit-guard.yml`) |
| GitHub web editor | CI |
| Clone that never set `core.hooksPath` | CI |
| Direct push to an unprotected branch | CI, **after the fact** — a required status check on a protected branch is what makes it pre-emptive |
| Squash-merge's generated message | **Nothing** — GitHub composes it after checks run |
| AI that writes no trailer, is not a GitHub bot, and whose address has no `bot` token | **Nothing** — closing this needs an author allowlist (rejected: it would block outside contributors) |
