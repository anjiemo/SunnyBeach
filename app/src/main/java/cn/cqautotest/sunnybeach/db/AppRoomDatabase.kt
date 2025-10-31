package cn.cqautotest.sunnybeach.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cn.cqautotest.sunnybeach.db.dao.CookieDao
import cn.cqautotest.sunnybeach.manager.CookieStore
import com.blankj.utilcode.util.FileUtils

@Database(entities = [CookieStore::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun cookieDao(): CookieDao

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
                }.build()
                sINSTANCE = instance
                // 返回实例
                instance
            }
        }
    }
}