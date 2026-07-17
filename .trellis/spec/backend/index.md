# Backend Development Guidelines

> Best practices for backend development in this project.

---

## Overview

This directory documents the **data/network/persistence ("backend") conventions of the SunnyBeach
Android app** — a multi-module Kotlin project. There is no server-side code in this repo; these guides
cover the HTTP, Room/MMKV, error, logging, and quality practices the app's code actually follows.

---

## Guidelines Index

| Guide                                           | Description                                          | Status |
|-------------------------------------------------|------------------------------------------------------|--------|
| [Directory Structure](./directory-structure.md) | Multi-module layout + app package map, naming rules | Filled |
| [Database Guidelines](./database-guidelines.md) | Room (entities/DAO/migrations) + MMKV               | Filled |
| [Error Handling](./error-handling.md)           | HTTP error handling, `execption` package, token expiry | Filled |
| [Quality Guidelines](./quality-guidelines.md)   | Toolchain, lint, architecture, testing reality, debt | Filled |
| [Logging Guidelines](./logging-guidelines.md)   | Timber + AOP `@Log`, what NOT to log                | Filled |
| [Feishu Notify](./github-actions-feishu-notify.md) | GitHub Actions Feishu webhook cards               | Filled |
| [Git Hooks / Commit Guard](./git-hooks-commit-guard.md) | Versioned hooks + CI guard; **SIGPIPE / fail-open contracts — read before writing any hook** | Filled |

---

## How to Fill These Guidelines

For each guideline file:

1. Document your project's **actual conventions** (not ideals)
2. Include **code examples** from your codebase
3. List **forbidden patterns** and why
4. Add **common mistakes** your team has made

The goal is to help AI assistants and new team members understand how YOUR project works.

---

**Language**: Spec / guideline documents (this `.trellis/spec/` tree) are written in **English**.
User-facing documentation and task PRDs are written in **Chinese**. Match the convention of the
document type you are editing rather than assuming everything is English.
