package cn.android52.sunnybeach.skin.manager

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.res.Resources
import android.content.res.Resources.NotFoundException
import android.graphics.drawable.Drawable
import android.text.TextUtils
import timber.log.Timber

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/11/19
 *    desc   : 皮肤资源管理器
 */
class SkinResourcesManager(
    private val resources: Resources,
    private val pkgName: String,
    private val suffix: String = ""
) {

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getDrawableByName(resType: String?, name: String): Drawable? {
        Timber.d("getDrawableByName：===> pkgName is %s", pkgName)
        try {
            val fixName = appendSuffix(name)
            val resId = resources.getIdentifier(fixName, resType, pkgName)
            Timber.d(
                "getDrawableByName：===> resType is %s name is %s resId is %s",
                resType,
                fixName,
                resId
            )
            return resources.getDrawable(resId)
        } catch (e: NotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    fun getColorByResName(resType: String?, name: String): ColorStateList? {
        Timber.d("getColorByResName：===> pkgName is %s", pkgName)
        try {
            val fixName = appendSuffix(name)
            val resId = resources.getIdentifier(fixName, resType, pkgName)
            Timber.d(
                "getColorByResName：===> resType is %s name is %s resId is %s",
                resType,
                fixName,
                resId
            )
            return resources.getColorStateList(resId)
        } catch (e: NotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 追加后缀
     */
    private fun appendSuffix(name: String): String {
        var fixName = name
        if (!TextUtils.isEmpty(suffix)) {
            fixName += "_$suffix"
        }
        return fixName
    }
}