package cn.android52.sunnybeach.skin.attr

import android.content.res.Resources
import android.util.AttributeSet
import cn.android52.sunnybeach.skin.factory.SkinConfigFactory
import timber.log.Timber
import java.util.*

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/11/19
 *    desc   : 皮肤属性支持
 */
object SkinAttrSupport {

    /**
     * 获取皮肤属性
     */
    fun getSkinAttrs(resources: Resources, attrs: AttributeSet): List<SkinAttr> {
        val skinAttrs: MutableList<SkinAttr> = ArrayList()
        var i = 0
        val n = attrs.attributeCount
        while (i < n) {
            // 例：id、layout_width、layout_height、background、color、drawable
            val attrName = attrs.getAttributeName(i)
            // 例：@2131296320、@2131297009、@2131230904
            val attrValue = attrs.getAttributeValue(i)
            Timber.d("getSkinAttrs：===> attrName is %s attrValue is %s", attrName, attrValue)
            if (attrValue.startsWith("@")) {
                var resId = 0
                try {
                    resId = attrValue.substring(1).toInt()
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }
                // 如果是无效的资源 id 则跳过
                if (resId == Resources.ID_NULL) {
                    i++
                    continue
                }
                // 获取资源类型名称
                val resType = resources.getResourceTypeName(resId)
                // 获取资源的名称
                val resName = resources.getResourceEntryName(resId)
                Timber.d("getSkinAttrs：===> resType is %s resName is %s", resType, resName)
                // 具有换肤的前缀，是支持的换肤资源类型
                if (resName.startsWith(SkinConfigFactory.SKIN_PREFIX)) {
                    val attrType = getSupportAttrType(attrName)
                    // 如果不是支持换肤的属性类型，则跳过
                    if (attrType == null) {
                        i++
                        continue
                    }
                    skinAttrs.add(SkinAttr(resName, resType, attrType))
                }
            }
            i++
        }
        return skinAttrs
    }

    /**
     * 通过资源属性名称，获取支持换肤的属性类型
     */
    private fun getSupportAttrType(attrName: String): SkinAttrType? {
        for (attrType in SkinAttrType.values()) {
            if (attrType.resType == attrName) {
                return attrType
            }
        }
        return null
    }
}