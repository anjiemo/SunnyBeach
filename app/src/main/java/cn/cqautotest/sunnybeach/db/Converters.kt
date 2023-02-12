package cn.cqautotest.sunnybeach.db

import androidx.room.TypeConverter
import cn.cqautotest.sunnybeach.ktx.toJson
import cn.cqautotest.sunnybeach.manager.CookieStore
import com.google.gson.reflect.TypeToken
import com.hjq.gson.factory.GsonFactory
import okhttp3.Cookie

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/06/18
 * desc   : 数据库类型转换
 */
class Converters {

    @TypeConverter
    fun cookieStoreToJson(cookieStore: CookieStore): String = cookieStore.toJson().orEmpty()

    @TypeConverter
    fun jsonFromCookieStore(json: String): CookieStore =
        GsonFactory.getSingletonGson().fromJson(json, CookieStore::class.java)

    @TypeConverter
    fun cookieToJson(cookie: Cookie): String = cookie.toJson().orEmpty()

    @TypeConverter
    fun jsonFromCookie(json: String): Cookie =
        GsonFactory.getSingletonGson().fromJson(json, Cookie::class.java)

    @TypeConverter
    fun cookiesToJson(cookies: List<Cookie>): String = cookies.toJson().orEmpty()

    @TypeConverter
    fun jsonFromCookies(json: String): List<Cookie> =
        GsonFactory.getSingletonGson()
            .fromJson(json, object : TypeToken<List<Cookie>>() {}.type)
}