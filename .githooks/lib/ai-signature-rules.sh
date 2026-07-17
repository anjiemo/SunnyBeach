#!/bin/sh
# AI 署名规则 —— 唯一事实来源。
#
# 本地 hook（.githooks/commit-msg、.githooks/pre-push）与 CI
# （.github/workflows/commit-guard.yml）**必须**复用本文件，禁止各自另写一份正则，
# 否则两处规则必然漂移。
#
# 零依赖：只用 git + grep + sh（Git Bash 与 ubuntu-latest 均自带）。
#
# ── 用法 ──────────────────────────────────────────────────────────────
#   . .githooks/lib/ai-signature-rules.sh
#
#   ai_sig_check_message_file <提交信息文件>       # commit-msg：$1 是文件路径
#   ai_sig_check_message_text "<提交信息全文>"
#   ai_sig_check_identity     "<Name <mail>>" [标签]
#   ai_sig_check_commit       <sha>               # 单个提交：消息 + 身份
#   ai_sig_check_range        <rev-range...>      # 一段提交（pre-push / CI）
#
#   退出码：0 = 干净；1 = 命中 AI 署名（详情打印到 stderr）
#
# ── 设计铁律（勿违背，每条都有实测依据）─────────────────────────────────
#   1. 只锚定「署名结构」——trailer 行首、身份字段 %an/%ae/%cn/%ce、固定生成语。
#      **绝不对提交正文做自由子串匹配。** 本仓库 fa776494 是合法人类提交，正文写着
#      「为 Claude Code, Cursor, GitHub Copilot, Gemini 和 Windsurf 提供集成…」；
#      且 android.database.Cursor 真实存在于 ImageSelectActivity.kt / VideoSelectActivity.kt。
#      裸词匹配在 736 条历史上产生 2 个误报。讨论 AI 的提交必须放行，只禁止「以 AI 身份署名」。
#   2. 优先匹配邮箱而非显示名。codex@openai.com 的显示名会漂移（GPT 5.5 / GPT 5.4 / Codex GPT-5）；
#      而 Claude / Devin / Jules / Cody 都是真人名（GitHub 上 `claude` id 81847 是 type:User 真人账号）。
#      显示名只在与 AI 邮箱**同行共现**时才作数。
#   3. 裸词 cursor / codex / amp / continue 禁止进入任何正则。
#   4. 绝不单独匹配 @users.noreply.github.com —— 那是所有开启邮箱隐私保护的**真人**用户的通用域，
#      单独匹配会误伤全部正常提交。必须锚定「数值 ID + login」或「[bot] 后缀」。
#   5. 不收录 gemini-cli@google.com —— 官方源码中它是 SHADOW_REPO_AUTHOR_EMAIL，
#      仅用于 gemini-cli 内部 checkpoint 影子仓库，永不进入用户真实提交历史。纯噪音规则。
#
# ── 四层防线 ──────────────────────────────────────────────────────────
#   第 1 层  精确名单（A 类域名 / B 类完整邮箱 / C 类 18 个 bot 数值 ID / 页脚前缀）
#   第 2 层  禁止一切 Co-authored-by: trailer（依据：本仓库 736 条历史 trailer 总数 = 0）
#   第 3 层  GitHub bot 结构匹配（身份侧）
#   第 4 层  bot 命名邮箱 token（身份侧 + 任何 trailer 形态的行）
#
#   ⚠ 第 1 层的 C 类枚举不可省略：198982749+Copilot@users.noreply.github.com 的 login 是
#     `Copilot`，**不含 [bot] 后缀**，第 3 层和第 4 层都抓不到它，只有这份枚举能拦住
#     GitHub Copilot coding agent。这是反直觉的例外。
#
# ── 如何扩展 ──────────────────────────────────────────────────────────
#   新 AI 工具通常无需改本文件——第 2/3/4 层是结构规则，零维护。
#   仅当新工具满足「既不写 trailer、又非 [bot] 账号、邮箱也不含 bot token」时，
#   才需要往下面的 AI_SIG_DOMAINS / AI_SIG_EXACT_MAILS / AI_SIG_BOT_IDS 里追加一行。
#   追加前请确认该标识有一手证据（官方源码 / GitHub Users API），不要凭印象添加。

# ════════════════════════════════════════════════════════════════════
# 第 1 层：精确名单
# ════════════════════════════════════════════════════════════════════

# A 类：AI 专属域名，可直接按域名匹配，误伤风险极低。
AI_SIG_DOMAINS='anthropic\.com|cursor\.com|aider\.chat|ampcode\.com'

# B 类：域名过宽（github.com / openai.com 属于正常人类），**必须连 local-part 一起匹配**。
AI_SIG_EXACT_MAILS='copilot@github\.com|codex@openai\.com'

# C 类：GitHub bot noreply 邮箱，**必须锚定数值 ID**（下列 18 个均经 GitHub Users API 复核）。
AI_SIG_BOT_IDS='209825114\+claude\[bot\]'
AI_SIG_BOT_IDS="${AI_SIG_BOT_IDS}|215619710\+anthropic-claude\[bot\]"
AI_SIG_BOT_IDS="${AI_SIG_BOT_IDS}|208546643\+claude-code-action\[bot\]"
AI_SIG_BOT_IDS="${AI_SIG_BOT_IDS}|198982749\+Copilot"          # ← login 不含 [bot]，只有本层能拦
AI_SIG_BOT_IDS="${AI_SIG_BOT_IDS}|167198135\+copilot\[bot\]"
AI_SIG_BOT_IDS="${AI_SIG_BOT_IDS}|206951365\+cursor\[bot\]"
AI_SIG_BOT_IDS="${AI_SIG_BOT_IDS}|215057067\+openai-codex\[bot\]"
AI_SIG_BOT_IDS="${AI_SIG_BOT_IDS}|199175422\+chatgpt-codex-connector\[bot\]"
AI_SIG_BOT_IDS="${AI_SIG_BOT_IDS}|176961590\+gemini-code-assist\[bot\]"
AI_SIG_BOT_IDS="${AI_SIG_BOT_IDS}|161369871\+google-labs-jules\[bot\]"
AI_SIG_BOT_IDS="${AI_SIG_BOT_IDS}|158243242\+devin-ai-integration\[bot\]"
AI_SIG_BOT_IDS="${AI_SIG_BOT_IDS}|205137888\+cline\[bot\]"
AI_SIG_BOT_IDS="${AI_SIG_BOT_IDS}|230936708\+continue\[bot\]"
AI_SIG_BOT_IDS="${AI_SIG_BOT_IDS}|201248094\+sourcegraph-cody\[bot\]"
AI_SIG_BOT_IDS="${AI_SIG_BOT_IDS}|220155983\+jetbrains-ai\[bot\]"
AI_SIG_BOT_IDS="${AI_SIG_BOT_IDS}|208079219\+amazon-q-developer\[bot\]"
AI_SIG_BOT_IDS="${AI_SIG_BOT_IDS}|136622811\+coderabbitai\[bot\]"
AI_SIG_BOT_IDS="${AI_SIG_BOT_IDS}|189301087\+windsurf-bot\[bot\]"

# 域名尾部边界：避免 @anthropic.community 这类误伤（后接字母/数字/连字符即不算命中）。
AI_SIG_TAIL='([^A-Za-z0-9-]|$)'

# 三条邮箱规则（消息侧与身份侧共用）。均要求 @ 前有 local-part，
# 故正文里的 https://cursor.com 这类纯链接不会命中。
AI_SIG_MAIL_RE="[A-Za-z0-9._%+-]+@(${AI_SIG_DOMAINS})${AI_SIG_TAIL}"
AI_SIG_MAIL_RE="${AI_SIG_MAIL_RE}|(^|[^A-Za-z0-9._%+-])(${AI_SIG_EXACT_MAILS})${AI_SIG_TAIL}"
AI_SIG_MAIL_RE="${AI_SIG_MAIL_RE}|(^|[^0-9])(${AI_SIG_BOT_IDS})@users\.noreply\.github\.com${AI_SIG_TAIL}"

# 页脚 / 前缀类（第 1 层，消息侧）。
#   - Generated with [Claude Code] —— 只锚定「行首 + 可选 🤖 + 固定生成语 + [claude code]」，
#     不匹配其后的 URL，故 claude.ai/code 与 claude.com/claude-code 两种写法都被覆盖，
#     同时不会误伤正文里引用该文档链接的合法提交。
#   - Replit-Commit-Author: Agent|Assistant，及其伴随的 Replit-Commit-Session-Id:。
AI_SIG_FOOTER_RE='^[[:space:]]*(🤖[[:space:]]*)?generated with[[:space:]]+\[claude code\]'
AI_SIG_FOOTER_RE="${AI_SIG_FOOTER_RE}|^[[:space:]]*replit-commit-author:[[:space:]]*(agent|assistant)([[:space:]]|$)"
AI_SIG_FOOTER_RE="${AI_SIG_FOOTER_RE}|^[[:space:]]*replit-commit-session-id:[[:space:]]*[^[:space:]]"

# aider 的提交信息前缀 —— 只作用于**主题行**（第一行），见 ai_sig_check_message_text。
AI_SIG_SUBJECT_RE='^[[:space:]]*aider:'

# aider 会把 author/committer 名改写成 `<用户名> (aider)` —— 只作用于身份侧。
AI_SIG_NAME_RE='\(aider\)'

# ════════════════════════════════════════════════════════════════════
# 第 2 层：禁止一切 Co-authored-by: trailer
# 依据：本仓库 736 条历史中 trailer 总数为 0，从未出现过任何 Co-authored-by。
# 故可以直接全禁，从而拦住**未知的新 AI 工具**（无需维护名单）。
# 范围仅限 Co-authored-by:，不触碰 Signed-off-by: 等其他 trailer 类型。
# ════════════════════════════════════════════════════════════════════
AI_SIG_COAUTHOR_RE='^[[:space:]]*co-authored-by:'

# ════════════════════════════════════════════════════════════════════
# 第 3 层：GitHub bot 结构匹配（身份侧）
# 形态固定为 <数值ID>+<login>[bot]@users.noreply.github.com。
# 真人的隐私邮箱是 <数值ID>+<用户名>@users.noreply.github.com，**不带 [bot]** → 零误伤。
# ════════════════════════════════════════════════════════════════════
AI_SIG_GH_BOT_RE='[0-9]+\+[^@<>]*\[bot\]@users\.noreply\.github\.com'

# ════════════════════════════════════════════════════════════════════
# 第 4 层：bot 命名邮箱 token
# local-part 中 `bot` 作为 `._+-` 分隔的独立 token（大小写不敏感，靠 grep -i）。
# 锚定在尖括号邮箱 <...> 内，避免正文里的自由文本误伤。
#   ✗ 拦：bot@ Bot@ BOT@ ai-bot@ bot.agent@ my_bot@ bot-01@
#   ✓ 放：abbot@（姓氏 Abbot）botha@（姓氏 Botha）robot@ chatbot@ bots@
# ════════════════════════════════════════════════════════════════════
AI_SIG_BOT_TOKEN_RE='<([^<>@]*[^[:alnum:]<>@])?bot([._+-][^@<>[:space:]]*)?@'

# ── 组合正则 ────────────────────────────────────────────────────────
# 消息侧：trailer 全禁 + 页脚 + AI 邮箱。三条都作用于整条消息。
AI_SIG_MSG_RE="${AI_SIG_COAUTHOR_RE}"
AI_SIG_MSG_RE="${AI_SIG_MSG_RE}|${AI_SIG_FOOTER_RE}"
AI_SIG_MSG_RE="${AI_SIG_MSG_RE}|${AI_SIG_MAIL_RE}"

# 「trailer 形态的行里出现 bot 命名邮箱」——只作用于**正文（第 2 行起）**，不含主题行。
# 补的是第 2 层的缺口：第 2 层只禁 Co-authored-by:，而 `Signed-off-by: X <bot@foo.com>`
# 这类**其他** trailer 里的 bot 邮箱要靠本条拦。
#
# 为什么必须排除主题行：本条用 `[A-Za-z][A-Za-z0-9_-]*:` 匹配 trailer 键，
# 而 Conventional Commits 的主题前缀（fix: / feat: / chore:）**形态与 trailer 键完全相同**。
# 若作用于主题行，`fix: 联系 <bot@foo.com> 报告问题` 会被误判成 trailer（已实测会误伤）。
# trailer 按定义只出现在正文末尾段，主题行不可能是 trailer → 排除主题行既不丢覆盖也消除误伤。
AI_SIG_MSG_BODY_RE="^[[:space:]]*[A-Za-z][A-Za-z0-9_-]*:[^<]*${AI_SIG_BOT_TOKEN_RE}"

# 身份侧：只作用于 %an <%ae> / %cn <%ce>，绝不碰正文。
AI_SIG_IDENT_RE="${AI_SIG_MAIL_RE}"
AI_SIG_IDENT_RE="${AI_SIG_IDENT_RE}|${AI_SIG_NAME_RE}"
AI_SIG_IDENT_RE="${AI_SIG_IDENT_RE}|${AI_SIG_GH_BOT_RE}"
AI_SIG_IDENT_RE="${AI_SIG_IDENT_RE}|${AI_SIG_BOT_TOKEN_RE}"

# 快速通道用的超集正则（见 ai_sig_check_range）。
# 必须是各层规则的**超集**：宁可假阳性（后续定位循环会用精确规则复核并放行），
# 绝不可假阴性（那会直接漏掉违规提交）。故此处照收 AI_SIG_MSG_BODY_RE，
# 不做「排除主题行」的收窄——收窄属于精确层的职责。
AI_SIG_ANY_RE="${AI_SIG_MSG_RE}|${AI_SIG_MSG_BODY_RE}|${AI_SIG_IDENT_RE}|${AI_SIG_SUBJECT_RE}"

# ── 内部工具 ────────────────────────────────────────────────────────

_ai_sig_err() {
    printf '%s\n' "$*" >&2
}

# 打印命中的行（带行号），便于开发者一眼看出是哪一行触发的。
# $1 = 待查文本，$2 = 正则
_ai_sig_report() {
    printf '%s\n' "$1" | grep -inE "$2" 2>/dev/null | sed 's/^/      /' >&2
}

# 清理提交信息文件：剥掉 scissors 分割线及其之后的内容，再剥掉注释行。
#
# 为什么必须做（已实测证实，不是臆想的风险）：commit-msg hook 拿到的文件里注释尚未被 git 清理；
# 且 `git commit -v` 会把**原始 diff** 附在 scissors 之后。本文件第 27、42 行的注释里
# 就含有 codex@openai.com 与 198982749+Copilot@users.noreply.github.com 两个真标识——
# 一旦改动落在它们附近，diff 上下文就会带上这些行，不剥掉就会**自我误报**、把正当提交卡死。
# （实测：在第 27 行附近改动后不剥离 → 必然自我拦截；剥离后 `git commit -v` 正常通过。）
#
# ⚠ 已知代价（刻意接受，勿当成 bug 重复"修"）：本剥离比 git 自己更激进。
#   git 只在 cleanup=scissors/default（即消息经编辑器）时才剥 scissors 与注释；
#   而 `git commit -F file` / `-m` 默认 cleanup=whitespace，**什么都不剥**。
#   于是「手工构造一条含假 scissors 行、把署名藏在其后的 -F 消息」能骗过本函数：
#   本函数看不到那段署名，但它会**原样落进历史**（已实测复现）。
#
#   为什么不修：commit-msg 本就是可被 `--no-verify` 一键跳过的最内层防线，
#   能构造假 scissors 的人直接用 --no-verify 更省事——堵这个洞不增加任何真实安全性。
#   而 pre-push 与 CI 走的是 ai_sig_check_range → `git log --format=%B`，
#   读的是**已落盘的原始消息**、不经本函数，两者均已实测能抓住这条绕过。
#   即：洞只存在于最内层，外两层封死；这正是分层防御的预期分工。
_ai_sig_clean_message() {
    sed -e '/^#[[:space:]]*-\{1,\}[[:space:]]*>8[[:space:]]*-\{1,\}[[:space:]]*$/,$d' \
        -e '/^#/d' "$1"
}

# ── 对外接口 ────────────────────────────────────────────────────────

# 校验提交信息全文。$1 = 信息文本（不是文件路径）
ai_sig_check_message_text() {
    _ai_sig_mtext="$1"
    _ai_sig_mrc=0

    if printf '%s\n' "$_ai_sig_mtext" | grep -qiE "$AI_SIG_MSG_RE"; then
        _ai_sig_err "  ✗ 提交信息含 AI 署名（行号: 内容）："
        _ai_sig_report "$_ai_sig_mtext" "$AI_SIG_MSG_RE"
        _ai_sig_mrc=1
    fi

    # 「其他 trailer 里的 bot 邮箱」只查正文（第 2 行起）——主题行的 `fix:` 与 trailer 键同形，
    # 放进来会把 `fix: 联系 <bot@foo.com>` 误判成 trailer。
    _ai_sig_body="$(printf '%s\n' "$_ai_sig_mtext" | tail -n +2)"
    if [ -n "$_ai_sig_body" ] && printf '%s\n' "$_ai_sig_body" | grep -qiE "$AI_SIG_MSG_BODY_RE"; then
        _ai_sig_err "  ✗ 提交信息的 trailer 含 bot 命名邮箱："
        _ai_sig_report "$_ai_sig_body" "$AI_SIG_MSG_BODY_RE"
        _ai_sig_mrc=1
    fi

    # aider 前缀只作用于主题行；正文里出现 `aider:` 不算。
    _ai_sig_subj="$(printf '%s\n' "$_ai_sig_mtext" | head -n 1)"
    if printf '%s\n' "$_ai_sig_subj" | grep -qiE "$AI_SIG_SUBJECT_RE"; then
        _ai_sig_err "  ✗ 提交信息主题行是 aider 前缀：${_ai_sig_subj}"
        _ai_sig_mrc=1
    fi

    return $_ai_sig_mrc
}

# 校验提交信息文件。$1 = 文件路径（commit-msg hook 的 $1 就是路径，不是内容）
ai_sig_check_message_file() {
    ai_sig_check_message_text "$(_ai_sig_clean_message "$1")"
}

# 校验身份字符串。$1 = 形如 `Name <mail@host>` 的文本（可多行），$2 = 可选标签
ai_sig_check_identity() {
    _ai_sig_ident="$1"
    _ai_sig_tag="${2:-身份}"

    if printf '%s\n' "$_ai_sig_ident" | grep -qiE "$AI_SIG_IDENT_RE"; then
        _ai_sig_err "  ✗ ${_ai_sig_tag}含 AI 署名："
        _ai_sig_report "$_ai_sig_ident" "$AI_SIG_IDENT_RE"
        return 1
    fi
    return 0
}

# 校验单个提交（消息 + author/committer 身份）。$1 = sha
# 先把两侧的报错文本收集起来，命中时才打印「sha + 主题」表头，使输出可读。
ai_sig_check_commit() {
    _ai_sig_sha="$1"
    _ai_sig_crc=0

    _ai_sig_detail="$(ai_sig_check_message_text \
        "$(git log -1 --format='%B' "$_ai_sig_sha")" 2>&1)" || _ai_sig_crc=1
    _ai_sig_detail_id="$(ai_sig_check_identity \
        "$(git log -1 --format='%an <%ae>%n%cn <%ce>' "$_ai_sig_sha")" \
        'author/committer 身份' 2>&1)" || _ai_sig_crc=1

    if [ "$_ai_sig_crc" -ne 0 ]; then
        _ai_sig_err "✗ ${_ai_sig_sha}  $(git log -1 --format='%s' "$_ai_sig_sha")"
        [ -n "$_ai_sig_detail" ] && _ai_sig_err "$_ai_sig_detail"
        [ -n "$_ai_sig_detail_id" ] && _ai_sig_err "$_ai_sig_detail_id"
    fi

    return $_ai_sig_crc
}

# 校验一段提交。参数原样透传给 git log / git rev-list，例如：
#   ai_sig_check_range abc123..def456
#   ai_sig_check_range HEAD --not --remotes
#
# 性能：先走「单趟 git log + grep」快速通道（736 条全历史实测 0.23s）。
# 逐提交 for 循环跑全历史会超时（实测 >2min，每条要 fork 数个 git 进程），
# 故只有快速通道命中后才进入定位循环——此时提交数必然很少。
# 快速通道用的是各层规则的**超集**，可能有假阳性；定位循环会用精确规则复核，
# 复核不出问题就照常放行，不会产生误报。
ai_sig_check_range() {
    if [ "$#" -eq 0 ]; then
        return 0
    fi

    # 故意不吞掉 git 的错误：范围解析失败必须**报错退出**，而不是当作「干净」放行。
    if ! _ai_sig_stream="$(git log --format='%an <%ae>%n%cn <%ce>%n%s%n%B' "$@")"; then
        _ai_sig_err "✗ 无法解析提交范围：$*"
        return 1
    fi

    if ! printf '%s\n' "$_ai_sig_stream" | grep -qiE "$AI_SIG_ANY_RE"; then
        return 0
    fi

    _ai_sig_rrc=0
    for _ai_sig_s in $(git rev-list "$@"); do
        ai_sig_check_commit "$_ai_sig_s" || _ai_sig_rrc=1
    done
    return $_ai_sig_rrc
}

# 供 hook / CI 复用的修复指引。
ai_sig_print_help() {
    _ai_sig_err ''
    _ai_sig_err '  提交历史只应体现真实人类作者。请删除 AI 署名后重试：'
    _ai_sig_err '    · 提交信息里的 Co-authored-by: / Generated with … 行 → 直接删掉'
    _ai_sig_err '    · author/committer 身份异常 → 检查 git config user.name / user.email'
    _ai_sig_err '    · 已落盘的提交 → git commit --amend 或 git rebase -i 清理后重推'
    _ai_sig_err '  规则定义见 .githooks/lib/ai-signature-rules.sh'
}
