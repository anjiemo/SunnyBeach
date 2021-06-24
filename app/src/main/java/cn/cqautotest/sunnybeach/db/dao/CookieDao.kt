package cn.cqautotest.sunnybeach.db.dao

import androidx.room.*
import cn.cqautotest.sunnybeach.manager.CookieStore

@Dao
interface CookieDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(cookieStoreSet: List<CookieStore>)

    @Transaction
    @Query("SELECT * FROM tb_cookies WHERE host = :host")
    fun getCookiesByHost(host: String): CookieStore?

    @Transaction
    @Query("SELECT * FROM tb_cookies")
    fun getCookies(): List<CookieStore>
}