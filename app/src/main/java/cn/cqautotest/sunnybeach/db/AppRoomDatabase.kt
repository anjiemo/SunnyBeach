package cn.cqautotest.sunnybeach.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cn.cqautotest.sunnybeach.db.dao.BlockSummary
import cn.cqautotest.sunnybeach.db.dao.CookieDao
import cn.cqautotest.sunnybeach.db.dao.UserBlock
import cn.cqautotest.sunnybeach.db.dao.UserBlockDao
import cn.cqautotest.sunnybeach.manager.CookieStore
import com.blankj.utilcode.util.FileUtils

@Database(
    entities = [
        CookieStore::class,
        UserBlock::class,
        BlockSummary::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun cookieDao(): CookieDao

    abstract fun userBlockDao(): UserBlockDao

    companion object {

        // 单例可防止同时打开多个数据库实例（双重校验锁式（Double Check)单例）。
        @Volatile
        private var sINSTANCE: AppRoomDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): AppRoomDatabase {
            val appContext = context.applicationContext
            // 如果 INSTANCE 不为空，则返回它，如果是，则创建数据库
            return sINSTANCE ?: synchronized(this) {
                val oldDbFile = appContext.getDatabasePath("cookie_database")
                val newDbFile = appContext.getDatabasePath("app_db")
                val instance = Room.databaseBuilder(appContext, AppRoomDatabase::class.java, "app_db").apply {
                    if (FileUtils.isFileExists(oldDbFile) && FileUtils.isFileExists(newDbFile).not()) {
                        // 旧数据库文件存在，则从旧数据库文件中创建数据库
                        createFromFile(oldDbFile)
                    }
                }
                    .addMigrations(MIGRATION_2_3)
                    .build()
                sINSTANCE = instance
                // 返回实例
                instance
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {

            override fun migrate(db: SupportSQLiteDatabase) {
                // 创建用户拉黑关系表
                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS `user_blocks` (
                `uuid` TEXT NOT NULL,
                `uId` TEXT NOT NULL,
                `targetUId` TEXT NOT NULL,
                `created_at` INTEGER NOT NULL,
                `reason` TEXT,
                PRIMARY KEY(`uuid`)
            )
            """.trimIndent()
                )

                // 显式创建所有需要的索引
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_user_blocks_uId` ON `user_blocks` (`uId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_user_blocks_targetUId` ON `user_blocks` (`targetUId`)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_user_blocks_uId_targetUId` ON `user_blocks` (`uId`, `targetUId`)")

                // 创建拉黑汇总表
                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS `block_summary` (
                `uId` TEXT NOT NULL,
                `blocked_uids` TEXT NOT NULL,
                `updated_at` INTEGER NOT NULL,
                PRIMARY KEY(`uId`)
            )
            """.trimIndent()
                )
            }
        }
    }
}