package cn.android52.sunnybeach.skin.factory

import android.content.Context
import android.util.AttributeSet
import android.view.InflateException
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.collection.arrayMapOf
import cn.android52.sunnybeach.skin.attr.SkinAttr
import cn.android52.sunnybeach.skin.attr.SkinAttrSupport
import cn.android52.sunnybeach.skin.attr.SkinView
import cn.android52.sunnybeach.skin.callback.ISkinChangedListener
import cn.android52.sunnybeach.skin.manager.SkinManager
import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/11/19
 *    desc   : 皮肤工厂
 */
class SkinFactory(
    private val delegate: AppCompatDelegate,
    private val listener: ISkinChangedListener?
) : LayoutInflater.Factory2 {

    private val sConstructorSignature = arrayOf(Context::class.java, AttributeSet::class.java)
    private val sClassPrefixList = arrayOf("android.widget.", "android.view.", "android.webkit.")
    private val sConstructorMap: MutableMap<String, Constructor<out View?>> = arrayMapOf()
    private val mConstructorArgs = arrayOfNulls<Any?>(2)
    private var mCreateViewMethod: Method? = null
    private val sCreateViewSignature = arrayOf(
        View::class.java,
        String::class.java,
        Context::class.java,
        AttributeSet::class.java
    )
    private val mCreateViewArgs = arrayOfNulls<Any>(4)

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return null
    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        // 系统有没有使用setFactory
        // 完成 AppCompat factory的工作
        var view: View? = null
        // 请参阅 AppCompatDelegateImpl 类，使用反射调用 createView 方法
        try {
            if (mCreateViewMethod == null) {
                mCreateViewMethod =
                    delegate.javaClass.getMethod("createView", *sCreateViewSignature)
            }
            // mCreateViewArgs
            mCreateViewArgs[0] = parent
            mCreateViewArgs[1] = name
            mCreateViewArgs[2] = context
            mCreateViewArgs[3] = attrs
            view = mCreateViewMethod!!.invoke(delegate, *mCreateViewArgs) as View?
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // 收集当前 View 需要换肤的属性集合
        val skinAttrs: List<SkinAttr> = SkinAttrSupport.getSkinAttrs(context.resources, attrs)
        // 如果当前 View 需要换肤的属性集合为空，则代表该 View 不需要换肤，直接返回该 View
        if (skinAttrs.isEmpty()) {
            return null
        }
        if (view == null) {
            view = createViewFromTag(context, name, attrs)
        }
        if (view != null) {
            // 当前 View 具有需要换肤的属性，将该 View 和 需要换肤的属性集合保存起来
            injectSkin(view, skinAttrs)
        }
        return view
    }

    /**
     * 请参阅 AppCompatViewInflater 类中的实现
     */
    private fun createViewFromTag(context: Context, name: String, attrs: AttributeSet): View? {
        var attrName = name
        if (attrName == "view") {
            attrName = attrs.getAttributeValue(null, "class")
        }
        return try {
            mConstructorArgs[0] = context
            mConstructorArgs[1] = attrs
            if (-1 == attrName.indexOf('.')) {
                for (i in sClassPrefixList.indices) {
                    val view: View? = createViewByPrefix(context, attrName, sClassPrefixList[i])
                    if (view != null) {
                        return view
                    }
                }
                null
            } else {
                createViewByPrefix(context, name, null)
            }
        } catch (e: Exception) {
            // We do not want to catch these, lets return null and let the actual LayoutInflater
            // try
            null
        } finally {
            // Don't retain references on context.
            mConstructorArgs[0] = null
            mConstructorArgs[1] = null
        }
    }

    /**
     * 请参阅 AppCompatViewInflater 类中的实现
     */
    @Throws(ClassNotFoundException::class, InflateException::class)
    private fun createViewByPrefix(context: Context, name: String, prefix: String?): View? {
        var constructor = sConstructorMap[name]
        return try {
            if (constructor == null) {
                // Class not found in the cache, see if it's real, and try to add it
                val clazz = Class.forName(
                    if (prefix != null) prefix + name else name,
                    false,
                    context.classLoader
                ).asSubclass(View::class.java)
                constructor = clazz.getConstructor(*sConstructorSignature)
                sConstructorMap[name] = constructor
            }
            constructor!!.isAccessible = true
            constructor.newInstance(*mConstructorArgs)
        } catch (e: Exception) {
            // We do not want to catch these, lets return null and let the actual LayoutInflater
            // try
            null
        }
    }

    /**
     * 将该 View 和 需要换肤的属性集合保存起来
     */
    private fun injectSkin(view: View, skinAttrs: List<SkinAttr>) {
        val manager = SkinManager.instance
        var skinViews: MutableList<SkinView>? = manager.getSkinView(listener)
        if (skinViews == null) {
            skinViews = ArrayList<SkinView>()
            manager.addSkinView(listener, skinViews)
        }
        skinViews.add(SkinView(view, skinAttrs))
        // 检测当前是否需要自动换肤，如果需要则换肤
        if (manager.isNeedChangeSkin()) {
            manager.skinChange(listener)
        }
    }
}