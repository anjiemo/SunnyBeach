package cn.android52.sunnybeach.skin.factory

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import cn.android52.sunnybeach.skin.factory.SkinConfigFactory.Companion.KEY_PLUGIN_PATH
import cn.android52.sunnybeach.skin.factory.SkinConfigFactory.Companion.KEY_PLUGIN_PKG
import cn.android52.sunnybeach.skin.factory.SkinConfigFactory.Companion.KEY_PLUGIN_SUFFIX
import cn.android52.sunnybeach.skin.factory.SkinConfigFactory.Companion.PREF_NAME

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/11/19
 *    desc   : 默认皮肤配置工厂
 */
class DefaultSkinConfigFactory(appContext: Application) : SkinConfigFactory {

    private val sp = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    override fun savePluginPath(path: String?) {
        sp.edit {
            putString(KEY_PLUGIN_PATH, path)
        }
    }

    override fun savePluginPkg(pkg: String?) {
        sp.edit {
            putString(KEY_PLUGIN_PKG, pkg)
        }
    }

    override fun saveSuffix(suffix: String?) {
        sp.edit {
            putString(KEY_PLUGIN_SUFFIX, suffix)
        }
    }

    override fun getPluginPath(): String = sp.getString(KEY_PLUGIN_PATH, null) ?: ""

    override fun getPluginPkg(): String = sp.getString(KEY_PLUGIN_PKG, null) ?: ""

    override fun getSuffix(): String = sp.getString(KEY_PLUGIN_SUFFIX, null) ?: ""

    override fun clearSkinConfig() {
        sp.edit {
            clear()
        }
    }
}