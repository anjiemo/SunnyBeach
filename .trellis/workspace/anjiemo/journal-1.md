# Journal - anjiemo (Part 1)

> AI development session journal
> Started: 2026-05-27

---



## Session 1: MD 文档分析与整改：图片死链、backend 规范、README、思考指南

**Date**: 2026-07-17
**Task**: MD 文档分析与整改：图片死链、backend 规范、README、思考指南
**Branch**: `dev`

### Summary

分析全项目 MD 文档并结合代码整改：并发派发 4 个文件集互斥的子代理，修复 docs 图片相对路径死链 72 处（含大小写校正）、用真实代码补齐 5 份 backend 空规范、README 补出真实多模块结构与 JDK 21 硬性要求、思考指南对齐 Android 真实分层（UI→ViewModel→Repository→Network/Room）。另归档两个已完成的历史任务。

### Main Changes

(Add details)

### Git Commits

| Hash | Message |
|------|---------|
| `0059dbf2` | (see git log) |
| `616b49d3` | (see git log) |
| `797da93c` | (see git log) |
| `f3fcb3bf` | (see git log) |

### Testing

- [OK] (Add test results)

### Status

[OK] **Completed**

### Next Steps

- None - task complete


## Session 2: 禁止 AI 署名的提交检查：本地 hook + CI 四层防线

**Date**: 2026-07-17
**Task**: 禁止 AI 署名的提交检查：本地 hook + CI 四层防线
**Branch**: `dev`

### Summary

实现禁止 AI 署名进入提交历史的四层防线：本地 .githooks（commit-msg + pre-push，规则集中于 lib/ai-signature-rules.sh 单一事实来源）+ 独立 commit-guard.yml CI 兜底，零新依赖（git+grep+sh）。经查证 20+ 工具的确切署名标识后，规则按邮箱/结构锚定而非关键词匹配——朴素正则在 736 条历史上有 2 条误报。实测修复两处 fail-open：hook 死于 SIGPIPE 被 git 当作成功（IDE/管道场景 5/5 漏）、CI 回退范围 --not --remotes=origin 在 checkout 后恒为空集。736 条全历史回归 0 命中，对照组 2 命中完成证伪。可执行契约沉淀至 spec/backend/git-hooks-commit-guard.md。

### Main Changes

(Add details)

### Git Commits

| Hash | Message |
|------|---------|
| `5b2697e5` | (see git log) |
| `aa579f4b` | (see git log) |

### Testing

- [OK] (Add test results)

### Status

[OK] **Completed**

### Next Steps

- None - task complete


## Session 3: 修正工作流触发分支：补齐 master

**Date**: 2026-07-17
**Task**: 修正工作流触发分支：补齐 master
**Branch**: `dev`

### Summary

发现并修复 commit-guard.yml 与 check.yml 的触发分支写成 [main, dev]，而本仓库根本没有 main 分支（默认分支为 master）——导致署名校验与编译检查在发布分支上从未触发。改为 [main, master, dev]（保留 main 为将来重命名留后路）。该 bug 躲过了实现、复核与协调三方审查，只有拿配置与真实远端 git ls-remote 对撞才暴露，已将此教训写入 spec 的 Wrong vs Correct。

### Main Changes

(Add details)

### Git Commits

| Hash | Message |
|------|---------|
| `ef503a9f` | (see git log) |
| `d3ed0d37` | (see git log) |

### Testing

- [OK] (Add test results)

### Status

[OK] **Completed**

### Next Steps

- None - task complete
