package cn.cqautotest.sunnybeach.manager

import cn.cqautotest.sunnybeach.util.APP_AUTO_CLEAN_CACHE
import cn.cqautotest.sunnybeach.util.USER_SETTING
import com.tencent.mmkv.MMKV

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/14
 * desc   : APP 管理者
 */
object AppManager {

    /**
     * 设置自动清理缓存
     */
    fun setAutoCleanCache(autoClean: Boolean) {
        val mmkv = MMKV.mmkvWithID(USER_SETTING, MMKV.MULTI_PROCESS_MODE) ?: return
        mmkv.putBoolean(APP_AUTO_CLEAN_CACHE, autoClean)
    }

    /**
     * 是否自动清理缓存
     */
    fun isAutoCleanCache(): Boolean {
        val mmkv = MMKV.mmkvWithID(USER_SETTING, MMKV.MULTI_PROCESS_MODE) ?: return true
        return mmkv.getBoolean(APP_AUTO_CLEAN_CACHE, true)
    }
}