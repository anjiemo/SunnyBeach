package cn.android52.sunnybeach.skin.factory

import android.os.Environment
import java.io.File

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/11/19
 *    desc   : 皮肤配置工厂
 */
interface SkinConfigFactory {

    /**
     * 保存皮肤插件的路径
     */
    fun savePluginPath(path: String?)

    /**
     * 保存皮肤插件的包名
     */
    fun savePluginPkg(pkg: String?)

    /**
     * 保存皮肤的后缀
     */
    fun saveSuffix(suffix: String?)

    /**
     * 获取皮肤插件的路径
     */
    fun getPluginPath(): String

    /**
     * 获取皮肤插件的包名
     */
    fun getPluginPkg(): String

    /**
     * 获取皮肤的后缀
     */
    fun getSuffix(): String

    /**
     * 清空皮肤配置
     */
    fun clearSkinConfig()

    companion object {

        /**
         * 插件换肤相关
         */
        const val SKIN_PREFIX = "skin_"
        const val PREF_NAME = "skin_plugin"
        const val KEY_PLUGIN_PATH = "plugin_path"
        const val KEY_PLUGIN_PKG = "plugin_pkg"
        const val KEY_PLUGIN_SUFFIX = "plugin_suffix"

        /**
         * 插件包的路径（默认在设备 sdcard 的根目录下，可根据实际情况进行设置）
         */
        val SKIN_PLUGIN_PATH = Environment.getExternalStorageDirectory()
            .toString() + File.separator + "sunnybeach.skin"

        /**
         * 插件包的包名
         */
        const val SKIN_PLUGIN_PKG = "cn.android52.sunnybeach.skin"
    }
}