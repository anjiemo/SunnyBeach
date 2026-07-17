# Database Guidelines

> Persistence conventions. Two mechanisms coexist: **Room** (relational, `db/`) and
> **MMKV** (key-value, used for lightweight/config data).

---

## Overview

| Concern | Choice |
|---|---|
| Relational DB | Room (`androidx.room`), file name `app_db` |
| DB class | `db/AppRoomDatabase.kt` — `version = 3`, `exportSchema = false` |
| Type converters | `db/Converters.kt` (Gson via `GsonFactory.getSingletonGson()`) |
| Key-value store | Tencent MMKV — `db/SobCacheManager.kt`, `db/dao/PlaceDao.kt` |
| JSON | Always `GsonFactory.getSingletonGson()`, never a bare `Gson()` |

`AppRoomDatabase` is a double-checked-lock singleton (`getDatabase(context)`), built once in
`AppApplication.initSdk()` and exposed via `AppApplication.getDatabase()`.

---

## Entities & DAOs

| Entity | `@Entity` table | Defined in | DAO | DAO style |
|---|---|---|---|---|
| `CookieStore` | `tb_cookies` | `manager/LocalCookieManager.kt` (⚠ not under `db/`) | `db/dao/CookieDao.kt` | **blocking / synchronous** (no `suspend`) |
| `UserBlock` | `user_blocks` | `db/dao/UserBlock.kt` | `db/dao/UserBlockDao.kt` | `suspend` + `Flow` |
| `BlockSummary` | `block_summary` | `db/dao/BlockSummary.kt` | `db/dao/UserBlockDao.kt` | `suspend` |

Registered in `AppRoomDatabase`:

```kotlin
@Database(entities = [CookieStore::class, UserBlock::class, BlockSummary::class],
          version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppRoomDatabase : RoomDatabase()
```

> **Reality note — `PlaceDao` is not Room.** `db/dao/PlaceDao.kt` is an `object` backed by
> `MMKV.defaultMMKV(..., "sunny_weather")`. It sits in the `dao` package but has no `@Dao`/`@Entity`.
> Do not assume everything in `db/dao/` is Room.

---

## DAO Patterns

Follow the pattern of the DAO nearest your table. Observed conventions:

- Every query/insert carries `@Transaction` (see `UserBlockDao.kt`).
- Conflict strategy is explicit: `@Insert(onConflict = OnConflictStrategy.REPLACE)` for upserts
  (`CookieDao.save`), `IGNORE` for idempotent inserts (`UserBlockDao.insertBlock`).
- **New DAOs use `suspend`**; reactive reads return `Flow<List<T>>`:

```kotlin
@Transaction @Insert(onConflict = OnConflictStrategy.IGNORE)
suspend fun insertBlock(userBlock: UserBlock): Long

@Transaction @Query("SELECT * FROM user_blocks WHERE uId = :uId")
fun getUserBlocksByFlow(uId: String): Flow<List<UserBlock>>
```

- Multi-step writes are default-method `@Transaction suspend fun`s that compose the primitives and
  wrap failures locally (`UserBlockDao.blockUser` → `insertBlock` + `syncBlockSummary`, `try/catch` → `Timber.e`).
- `CookieDao` is **synchronous** — callers must invoke it off the main thread. It is reached through
  `LocalCookieManager` (a `CookieJar`) → `CookiesViewModel` → `CookieDao`.

---

## Migrations

- Strategy is **manual raw-SQL `Migration` objects**, added via `.addMigrations(...)`.
- Only `MIGRATION_2_3` exists today (creates `user_blocks` + `block_summary` and their indices with
  `CREATE TABLE/INDEX IF NOT EXISTS`). There is no `MIGRATION_1_2`.
- The v1→v2 step is a **file rename**: if legacy `cookie_database` exists and `app_db` does not, the
  builder calls `createFromFile(oldDbFile)` to seed the new DB.
- `exportSchema = false` — no schema JSON is generated, so Room cannot auto-verify migrations. Bumping
  `version` without a matching `Migration` crashes at runtime with `IllegalStateException`.

```kotlin
private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""CREATE TABLE IF NOT EXISTS `user_blocks` ( ... PRIMARY KEY(`uuid`) )""".trimIndent())
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_user_blocks_uId_targetUId` ON `user_blocks` (`uId`, `targetUId`)")
        // ...
    }
}
```

**Adding a table/column requires 3 edits:** (1) entity + DAO, (2) add to `entities` and bump `version`,
(3) write a new `Migration(old, new)` and register it in `addMigrations(...)`.

---

## Naming Conventions

| Item | Rule | Example |
|---|---|---|
| Table | snake_case, sometimes `tb_` prefix | `tb_cookies`, `user_blocks`, `block_summary` |
| Column | snake_case via `@ColumnInfo(name = ...)` | `created_at`, `blocked_uids`, `updated_at` |
| Index | `@Index(value = [...], unique = ...)` | `Index(value = ["uId", "targetUId"], unique = true)` |
| Primary key | `@PrimaryKey`; generated ids use `UUID.randomUUID().toString()` | `UserBlock.uuid` |
| MMKV id | descriptive string per store | `SOB_ACCOUNT_MAP`, `http_cache_id`, `sunny_weather` |

`List<String>` / `List<Cookie>` columns are persisted as JSON strings through `Converters`
(`jsonToStringList` swallows parse errors and returns `emptyList()`).

---

## Common Mistakes

| Wrong | Correct |
|---|---|
| Bump `@Database(version=...)` and ship | Also add a `Migration` and register it (`exportSchema=false` gives no compile-time guard) |
| Call `CookieDao` on the main thread | It is blocking — dispatch to a background thread / go through the ViewModel |
| Use `new Gson()` / `GsonUtils` in a converter | Use `GsonFactory.getSingletonGson()` (matches network parsing & fault tolerance) |
| Store transient config in Room | Use MMKV (`SobCacheManager` / `PlaceDao`) for tokens, flags, last-place |
