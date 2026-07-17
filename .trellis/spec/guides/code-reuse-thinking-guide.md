# Code Reuse Thinking Guide

> **Purpose**: Stop and think before creating new code - does it already exist?

---

## The Problem

**Duplicated code is the #1 source of inconsistency bugs.**

When you copy-paste or rewrite existing logic:

- Bug fixes don't propagate
- Behavior diverges over time
- Codebase becomes harder to understand

In this app the most common duplication is a second helper that should have been a shared
extension, a new Activity/Fragment/Adapter re-written instead of extending the base, or a second
`*Api`/`*Network` method for a route that already exists.

---

## Before Writing New Code

### Step 1: Search First

```bash
# Is there already a shared extension or base helper? (ktx/ + :library:base)
grep -rn "fun RecyclerView" app/src/main/java/cn/cqautotest/sunnybeach/ktx/
grep -rn "fun .*(" library/base/src/main/java/com/hjq/base/

# Is there already an Api / Network / Repository method for this endpoint?
grep -rn "queryUserInfo" app/src/main/java/cn/cqautotest/sunnybeach/

# Before changing a route / header / constant, find every user of it
grep -rn "uc/user/login" app/src/main/java/cn/cqautotest/sunnybeach/
```

### Step 2: Ask These Questions

| Question                                              | If Yes...                                         |
|-------------------------------------------------------|---------------------------------------------------|
| Does a similar `ktx/` extension or base helper exist? | Use or extend it                                  |
| Is there already an `*Api`/`*Network`/`Repository` method? | Call it — don't re-declare the endpoint       |
| Is this Activity/Fragment/Adapter ~80% like another?  | Extend the base (`AppActivity`/`AppFragment`/`AppAdapter`) or reuse `AdapterDelegate` |
| Am I copying code from another file?                  | **STOP** - extract to `ktx/` or `:library:base`   |

---

## Common Duplication Patterns

### Pattern 1: Copy-Paste Helpers

**Bad**: Copying a `RecyclerView` / `View` / `ApiResponse` helper into another class

**Good**: Extract it to a `ktx/` extension (e.g. `ktx/RecyclerView.kt`, `ktx/View.kt`,
`ktx/ApiResponse.kt`); if it is UI-framework-level, put it in `:library:base` (`com.hjq.base`)

### Pattern 2: Similar Activities / Fragments / Adapters

**Bad**: Creating a new screen or list adapter that is 80% identical to an existing one

**Good**: Extend the shared base — `AppActivity`, `AppFragment`, `AppAdapter` (over
`BaseActivity`/`BaseFragment`/`BaseAdapter` in `:library:base`) — and reuse `AdapterDelegate`
for item click / animation instead of re-implementing it

### Pattern 3: Redefining an Endpoint

**Bad**: Writing a second Retrofit interface (or `*Network` method) for a route that
already has one

**Good**: Reuse the existing `*Api` → `*Network` → `Repository` chain; add one method to each,
not a parallel interface

### Pattern 4: Repeated Constants

**Bad**: Defining the same base URL, header name, or response code in multiple files

**Good**: Single source of truth (e.g. `util/*` URLs, `SobCacheManager.SOB_TOKEN_NAME`,
`Repository.NOT_LOGIN_CODE`), imported everywhere

---

## When to Abstract

**Abstract when**:

- Same code appears 3+ times
- Logic is complex enough to have bugs (e.g. DTO → `model` mapping, item-diff callbacks)
- Multiple screens might need this

**Don't abstract when**:

- Only used once
- Trivial one-liner
- Abstraction would be more complex than duplication

---

## After Batch Modifications

When you've made similar changes to multiple files:

1. **Review**: Did you catch all instances (all `*Api`, all adapters, all observers)?
2. **Search**: Run `grep` to find any missed
3. **Consider**: Should this be abstracted into `ktx/` or `:library:base`?

---

## Gotcha: Two Copies of the Same State That Must Stay in Sync

**Problem**: When one representation is **derived** from another (a denormalized cache next to the
base rows), every mutation of the base must refresh the derived copy — or it silently drifts.

**Symptom**: The base data is correct, but a summary/count read from the cache is stale.

**Real-world example**: `db/dao/UserBlockDao.kt` keeps the `user_blocks` rows **and** a denormalized
`block_summary`. `blockUser` / `unblockUser` reconcile them by calling `syncBlockSummary(uId)` inside
the same `@Transaction`. A new write path that inserts/deletes a block but forgets `syncBlockSummary`
would leave the summary wrong while the rows look fine.

**Prevention checklist**:

- [ ] When one table/field is derived from another, every base mutation must refresh the derived copy
- [ ] Keep the reconciliation inside the transactional method (`blockUser`/`unblockUser`), not at the
  call site where it can be forgotten
- [ ] Prefer reusing the existing transactional DAO method over ad-hoc `insert`/`delete` calls

---

## Checklist Before Commit

- [ ] Searched `ktx/`, `:library:base`, and `*Api`/`*Network`/`Repository` for existing code
- [ ] No copy-pasted logic that should be a shared extension or base method
- [ ] Constants (URLs, headers, response codes) defined in one place
- [ ] Similar screens/adapters extend the shared base instead of duplicating it
