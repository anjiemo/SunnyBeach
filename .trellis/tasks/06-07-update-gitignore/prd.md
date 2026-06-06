# 补充 .gitignore 文件内容

## Goal

将 nowinandroid 项目（Google Android 示例项目）的 .gitignore 规则补充到当前项目中，确保忽略所有标准的 Android 构建产物和开发环境文件。

## What I already know

**当前项目 (SunnyBeach) 的 .gitignore 现状：**
- 34 行，包含基本 Android 忽略规则
- 已有：`.gradle`, `.idea`, `.cxx`, `.externalNativeBuild`, `build`, `captures`, `*.iml`, `.DS_Store`, `local.properties`
- 已有 Trellis 相关规则（21-33 行）

**nowinandroid 的 .gitignore 特点：**
- 50 行，更完善的 Android 开发规则
- 包含详细注释说明每类规则的用途
- 对 `.idea` 目录有细粒度控制（排除大部分，但保留 codeStyles）

**当前项目缺少的重要规则：**
- `*.apk`, `*.ap_`, `*.dex`, `*.class` - Android 构建产物
- `bin/`, `gen/`, `out/`, `generated/` - 生成目录
- `.classpath`, `.project` - Eclipse 项目文件
- `.idea` 的细粒度控制（保留 codeStyles，忽略其他）
- `_sandbox` - Sandbox 相关
- `.kotlin` - Kotlin 编译缓存（当前项目已有 `/.kotlin/`，但 nowinandroid 用 `.kotlin` 更通用）

## Assumptions (temporary)

- 当前项目使用 Android Studio + Kotlin（已确认）
- 需要遵循 Google Android 开发最佳实践
- 不破坏现有规则，只做补充

## Open Questions

*无待解决问题*

## Requirements

1. 补充缺失的 Android 构建产物忽略规则
   - `*.apk`, `*.ap_` - Android 应用包文件
   - `*.dex` - Dalvik 可执行文件
   - `*.class` - Java 类文件

2. 补充缺失的生成目录规则
   - `bin/`, `gen/`, `out/` - 通用生成目录
   - `generated/` - 生成代码目录

3. 补充 Eclipse 项目文件规则
   - `.classpath`, `.project`

4. 补充 Sandbox 相关规则
   - `_sandbox`

5. **保持 `.idea` 全部忽略**（用户决策：方案 1）
   - 不采用 nowinandroid 的细粒度配置
   - 维持当前简单清晰的做法

6. 添加适当的分组注释，提升可读性

## Acceptance Criteria

- [ ] 所有 nowinandroid 中有价值的规则已补充到当前项目
- [ ] 无重复或冲突的规则
- [ ] 保留当前项目的 Trellis 相关规则
- [ ] 文件有清晰的分类和注释（如果采用注释风格）
- [ ] 规则按类型分组（构建产物、IDE 配置、生成文件等）

## Definition of Done

- .gitignore 文件更新完成
- 规则无冗余和冲突
- 代码审查通过（如需要）
- 验证现有已忽略的文件不受影响

## Out of Scope

- 不修改现有的 Trellis 相关规则
- 不删除当前项目已有的规则
- 不处理 `.gitignore` 之外的 Git 配置
- 不采用 nowinandroid 的细粒度 `.idea` 配置

## Decision (ADR-lite)

**Context**: 需要决定是否采用 nowinandroid 对 `.idea` 目录的细粒度控制（保留 codeStyles）。

**Decision**: 保持现状，继续全部忽略 `.idea` 目录（不保留 codeStyles）。

**Consequences**:
- ✅ 保持配置简单，易于理解
- ✅ 适合个人项目或小团队
- ⚠️ 无法通过 Git 共享团队代码风格配置（需通过其他方式同步，如 EditorConfig）
- ✅ 避免 `.idea` 目录的部分文件变化导致的合并冲突

## Technical Approach

**分析对比**：
1. 逐行对比两个 `.gitignore` 文件
2. 识别 nowinandroid 有而当前项目缺失的规则
3. 过滤掉与决策冲突的规则（如细粒度 `.idea` 配置）

**合并策略**：
1. 保留当前所有规则（34 行）
2. 在适当位置插入缺失的规则，按类型分组
3. 添加注释说明每组规则的用途（参考 nowinandroid 风格）
4. 去重（如 `.kotlin` vs `/.kotlin/`，选择更通用的版本）

**文件结构**（合并后）：
```
# Android 构建产物
*.apk, *.ap_, *.dex, *.class

# 生成目录
bin/, gen/, out/, build/, generated/

# Gradle
.gradle

# IDE 配置
.idea, *.iml, .classpath, .project

# 系统文件
.DS_Store, ._*

# 本地配置
local.properties

# 其他
captures/, _sandbox

# Kotlin
.kotlin (或 /.kotlin/)

# Trellis（保持不变）
...
```

## Technical Notes

- 源文件：`D:\ProgramFiles\StudioProjects\nowinandroid\.gitignore` (50 行)
- 目标文件：`.gitignore` (34 行)
- 两个项目都是 Android 项目，使用 Gradle 构建系统
- nowinandroid 使用更细粒度的 `.idea` 配置管理（本次不采用）
- 需处理路径差异：`.kotlin` (通用) vs `/.kotlin/` (当前项目)
