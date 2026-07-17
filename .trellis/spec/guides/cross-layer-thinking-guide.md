# Cross-Layer Thinking Guide

> **Purpose**: Think through data flow across layers before implementing.

---

## The Problem

**Most bugs happen at layer boundaries**, not within layers.

In this app the layers are:

```
UI (ui/activity, ui/fragment, ui/dialog, ui/popup, ui/adapter — AppActivity / AppFragment over :library:base)
        │  by viewModels() + observe(LiveData)
        ▼
ViewModel (viewmodel/**  —  ViewModel / AndroidViewModel + LiveData)
        │
        ▼
Repository (repository/Repository.kt  —  exposes LiveData<Result<T>>)
        │
        ├──► Network (http/  —  *Network wrappers → *Api: Retrofit via ServiceCreator)
        └──► Room DB (db/  —  AppRoomDatabase, DAOs, entities, Converters)
```

Common cross-layer bugs here:

- Network returns an `ApiResponse<T>` DTO, UI expects the domain `model` — mapping lost in between
- A `LiveData` is observed with the wrong lifecycle owner, so a Fragment leaks or never updates
- The same data is cached in Room **and** re-fetched from the network — nobody owns the source of truth
- A DTO field is renamed but another parser / `model` mapper still reads the old name

---

## Before Implementing Cross-Layer Features

### Step 1: Map the Data Flow

Trace how data actually moves and **what type crosses each hop**:

```
UI  ──observe(LiveData<Result<T>>)──►  ViewModel  ──►  Repository  ──►  Network (*Api)
 ▲                                                          │              └─► API DTO: ApiResponse<T>
 │                                                          └─► Room DAO (db/)
 └────────────  model (model/**)  ◄──  map DTO → model  ◄────────────────────────┘
```

For each arrow, ask:

- What type is crossing here — a raw DTO, a domain `model`, or a `Result<T>`?
- Who owns the transform (usually the `Repository`, e.g. `refreshWeather` builds a `Weather` from two responses)?
- What happens on failure / null (does the UI call `result.getOrNull()` and handle the failure branch)?

### Step 2: Identify Boundaries

| Boundary                 | Common Issues                                                                                     |
|--------------------------|--------------------------------------------------------------------------------------------------|
| UI ↔ ViewModel           | LiveData observed with wrong owner (`this` in an Activity vs `viewLifecycleOwner` in a Fragment); observing a freshly-created LiveData each call; UI not unwrapping `Result` |
| ViewModel ↔ Repository   | ViewModel re-implements threading/mapping the Repository already owns; a `switchMap` trigger LiveData never fired, so nothing loads |
| Repository ↔ Network     | DTO (`ApiResponse<T>`) vs domain `model`; `getCode()` not `200` treated as success; token failure (`11126`) not routed to re-login |
| Repository ↔ Room        | Same data cached in Room and fetched from network (source-of-truth ambiguity); a derived table (`block_summary`) drifting from its base rows (`user_blocks`) |
| DTO ↔ model ↔ UI         | A DTO field renamed in one `*Api`, but other parsers / `model` mappers still read the old shape   |

### Step 3: Define Contracts

For each boundary:

- What is the exact input type (DTO vs `model`)?
- What is the exact output type the next layer expects?
- What errors can occur (business code, IO exception, null data), and who converts them into a `Result.failure`?

---

## Common Cross-Layer Mistakes

### Mistake 1: Implicit Format Assumptions

**Bad**: Reading `ApiResponse.getData()` without checking `isSuccess()` / `getCode()`

**Good**: Map DTO → `model` in the Repository only after the code check (see `launchAndGet`)

### Mistake 2: Wrong LiveData Lifecycle Owner

**Bad**: `viewModel.xxxLiveData.observe(this)` inside a Fragment

**Good**: Observe with `viewLifecycleOwner` in Fragments; `this` is correct only in an Activity (e.g. `WeatherActivity`)

### Mistake 3: Leaky Abstractions

**Bad**: An Activity/Fragment reaching into a Room DAO or `*Api` directly

**Good**: UI talks only to its ViewModel; the ViewModel talks only to the Repository; the Repository owns Network + Room

---

## Checklist for Cross-Layer Features

Before implementation:

- [ ] Mapped the complete data flow (UI → ViewModel → Repository → Network/Room)
- [ ] Identified every boundary the change crosses
- [ ] Decided the exact type at each boundary (DTO vs `model` vs `Result<T>`)
- [ ] Decided who maps DTO → `model` and who converts errors to `Result.failure`

After implementation:

- [ ] Tested edge cases (null `data`, empty list, non-200 code, IO exception)
- [ ] Verified LiveData is observed with the correct lifecycle owner
- [ ] Checked data survives the round-trip DTO → `model` → UI without losing fields

---

## Parallel Layer Consistency: Api ↔ Network ↔ Repository

Every SOB feature is wired through **three parallel layers that must move in lock-step**. Touch only
one and the others silently drift:

- `http/api/sob/UserApi.kt` — the Retrofit interface (endpoint + DTO return type)
- `http/network/UserNetwork.kt` — thin wrapper that forwards to the `*Api`
- `repository/Repository.kt` — exposes `LiveData<Result<T>>` to ViewModels

### Checklist: After Adding / Renaming / Retyping an Endpoint

- [ ] Declared the `@GET/@POST/@PUT/@DELETE` method in the `*Api` interface
- [ ] Forwarded it from the matching `*Network` object
- [ ] Exposed/updated the `Repository` method (and picked the right helper: `launchAndGetData` for
  `data`, `launchAndGetMsg` for `message`)
- [ ] The DTO type on the `*Api` matches the `model` the Repository hands to the ViewModel

**Real-world example**: `Repository.getVipUserList()` → `UserNetwork.getVipUserList()` →
`UserApi.getVipUserList(): ApiResponse<List<VipUserInfoSummary>>`. Changing that DTO shape means
editing the `*Api` return type **and** every place the `model` is consumed — not just one file.

---

## Room Schema Upgrade Consistency (fresh install vs migration)

`db/AppRoomDatabase.kt` is both the schema (`@Database(entities = [...], version = N)`) and the
upgrade path (`addMigrations(...)`). A **fresh install** builds the newest schema directly; an
**existing user** only reaches it through a `Migration`. Change an entity and forget the migration →
fresh installs work, upgrades crash.

### Checklist: After Changing Any `@Entity` or DAO Schema

- [ ] Bumped `version` in `@Database`
- [ ] Added a `Migration(old, new)` and registered it in `addMigrations(...)`
- [ ] Updated `@TypeConverters` / `Converters` if a new column type was introduced
- [ ] Verified **both** a fresh install and an upgrade from the previous version

**Real-world example**: `MIGRATION_2_3` creates the `user_blocks` and `block_summary` tables for
users upgrading from version 2. Without it, only fresh installs would have those tables and every
upgraded user would crash on first DAO access.

---

## Response-Code Branch Checklist

`Repository.launchAndGet` branches on the DTO's **numeric code**, not just a success/fail boolean:
`isSuccess()` (200) → emit data; `NOT_LOGIN_CODE` (11126) → run `checkToken()` then fail with
`NotLoginException`; otherwise fail with `ServiceException`. Other coded branches exist too
(`toAllowanceResult` maps `11128` / `11129`).

### Before Adding a New Coded Response

- [ ] Every caller path handles the non-200 branch, not only the happy path
- [ ] Token-failure codes route to re-login / `checkToken()`, not a generic error toast
- [ ] A network/IO exception is distinguished from a business error code (both end as
  `Result.failure`, but only one should trigger retry/login)
- [ ] The UI unwraps the `Result` (`getOrNull()` + failure branch) and never assumes success

**Real-world example**: `login()` combines a login call and a `checkToken()` call, then folds four
code combinations into one `ApiResponse`. Any new "special" code must be decided here, in the
Repository, before it reaches a ViewModel.

---

## When to Create Flow Documentation

Create detailed flow docs when:

- A feature spans 3+ layers (UI → ViewModel → Repository → Network **and** Room)
- The DTO → `model` mapping is non-trivial (e.g. merging two responses like `refreshWeather`)
- Data is cached in Room and also fetched from the network (document who is the source of truth)
- The feature has caused a boundary bug before
