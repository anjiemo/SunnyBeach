package com.example.blogsystem.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.blogsystem.db.dao.CookieDao
import com.example.blogsystem.utils.CookieStore

// 将类注释为带有 CookieStore 类的表（实体）的 Room 数据库
@Database(entities = [CookieStore::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class CookieRoomDatabase : RoomDatabase() {

    abstract fun cookieDao(): CookieDao

    companion object {
        // 单例可防止同时打开多个数据库实例。
        @Volatile
        private var sINSTANCE: CookieRoomDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): CookieRoomDatabase {
            //如果 INSTANCE 不为空，则返回它，如果是，则创建数据库
            return sINSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CookieRoomDatabase::class.java,
                    "cookie_database"
                ).build()
                sINSTANCE = instance
                // 返回实例
                instance
            }
        }
    }
}