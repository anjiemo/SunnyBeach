package cn.android52.sunnybeach.skin.manager

import android.annotation.SuppressLint
import android.app.Application
import android.content.res.AssetManager
import android.content.res.Resources
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.arrayMapOf
import androidx.core.view.LayoutInflaterCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.coroutineScope
import cn.android52.sunnybeach.skin.attr.SkinView
import cn.android52.sunnybeach.skin.callback.ISkinChangedListener
import cn.android52.sunnybeach.skin.callback.ISkinChangingCallback
import cn.android52.sunnybeach.skin.factory.DefaultSkinConfigFactory
import cn.android52.sunnybeach.skin.factory.SkinConfigFactory
import cn.android52.sunnybeach.skin.factory.SkinFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.util.*

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/11/19
 *    desc   : 皮肤管理器，在 Application 中进行 init 初始化
 */
class SkinManager private constructor() : LifecycleObserver {

    private lateinit var mAppContext: Application
    private var mSkinResourcesManager: SkinResourcesManager? = null
    private val mListeners: MutableList<ISkinChangedListener> = arrayListOf()
    private val mSkinViewMaps: MutableMap<ISkinChangedListener?, MutableList<SkinView>> =
        arrayMapOf()
    private lateinit var mSkinConfigFactory: SkinConfigFactory
    private var mCurrentPath: String = ""
    private var mCurrentPkg: String = ""

    // 后缀
    private var mSuffix: String = ""

    val resourcesManager: SkinResourcesManager
        get() = if (!usePlugin()) {
            SkinResourcesManager(
                mAppContext.resources,
                mAppContext.packageName,
                mSuffix
            )
        } else mSkinResourcesManager!!

    /**
     * 请在 Application 中初始化 SkinManager
     * SkinConfigFactory 是可选项，若未指定皮肤配置工厂则使用默认皮肤工厂，该工厂使用 SharedPreferences 存储皮肤插件信息
     */
    @JvmOverloads
    fun init(
        appContext: Application,
        factory: SkinConfigFactory = DefaultSkinConfigFactory(appContext)
    ) {
        mAppContext = appContext
        mSkinConfigFactory = factory
        try {
            val pluginPath = mSkinConfigFactory.getPluginPath()
            val pluginPkg = mSkinConfigFactory.getPluginPkg()
            mSuffix = mSkinConfigFactory.getSuffix()
            val file = File(pluginPath)
            if (file.exists()) {
                loadPlugin(pluginPath, pluginPkg)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mSkinConfigFactory.clearSkinConfig()
        }
    }

    /**
     * 是否已经初始化
     */
    private fun isInitialized() = ::mAppContext.isInitialized && ::mSkinConfigFactory.isInitialized

    /**
     * 加载插件
     */
    @Throws(Exception::class)
    fun loadPlugin(skinPluginPath: String, skinPluginPkg: String) {
        if (skinPluginPath == mCurrentPath && skinPluginPkg == mCurrentPkg) {
            return
        }
        val assetManager = AssetManager::class.java.newInstance()
        // 获取addAssetPath方法
        val addAssetPathMethod =
            assetManager.javaClass.getMethod("addAssetPath", String::class.java)
        // 调用addAssetPath方法，第一个参数是当前对象，第二个参数是插件包的路径
        addAssetPathMethod.invoke(assetManager, skinPluginPath)
        val superResources = mAppContext.resources
        val displayMetrics = superResources.displayMetrics
        val configuration = superResources.configuration
        val resources = Resources(assetManager, displayMetrics, configuration)
        mSkinResourcesManager = SkinResourcesManager(resources, skinPluginPkg)
        mCurrentPath = skinPluginPath
        mCurrentPkg = skinPluginPkg
    }

    /**
     * 获取皮肤视图
     */
    fun getSkinView(listener: ISkinChangedListener?): MutableList<SkinView>? {
        return mSkinViewMaps[listener]
    }

    /**
     * 添加皮肤视图
     */
    fun addSkinView(listener: ISkinChangedListener?, views: MutableList<SkinView>) {
        mSkinViewMaps[listener] = views
    }

    /**
     * 注册监听器
     */
    fun registerListener(listener: ISkinChangedListener) {
        mListeners.add(listener)
    }

    /**
     * 取消注册监听器，避免内存泄漏
     * 1、移除监听回调
     * 2、移除 View 集合
     */
    fun unRegisterListener(listener: ISkinChangedListener) {
        mListeners.remove(listener)
        mSkinViewMaps.remove(listener)
    }

    /**
     * 换皮肤
     */
    fun changeSkin(suffix: String): SkinManager {
        clearPluginInfo()
        mSuffix = suffix
        mSkinConfigFactory.saveSuffix(suffix)
        notifyChangedListener()
        return this
    }

    /**
     * 重置皮肤状态
     */
    fun resetSkin() {
        clearPluginInfo()
        notifyChangedListener()
    }

    /**
     * 清除皮肤插件信息
     */
    private fun clearPluginInfo() {
        mCurrentPath = ""
        mCurrentPkg = ""
        mSuffix = ""
        mSkinConfigFactory.clearSkinConfig()
        updatePluginInfo(mCurrentPath, mCurrentPkg)
    }

    /**
     * 使用默认换肤配置
     */
    fun changeSkin(lifecycle: Lifecycle, callback: ISkinChangingCallback?) {
        changeSkin(
            SkinConfigFactory.SKIN_PLUGIN_PATH,
            SkinConfigFactory.SKIN_PLUGIN_PKG,
            lifecycle,
            callback
        )
    }

    /**
     * 改变皮肤
     */
    fun changeSkin(
        skinPluginPath: String,
        skinPluginPkg: String,
        lifecycle: Lifecycle,
        callback: ISkinChangingCallback?
    ) {
        check(isInitialized()) { "Please initialize in Application!" }
        performChangeSkin(this, skinPluginPath, skinPluginPkg, lifecycle, callback)
    }

    /**
     * 执行换肤
     */
    private fun performChangeSkin(
        skinManager: SkinManager,
        pluginPath: String,
        pluginPkg: String,
        lifecycle: Lifecycle,
        callback: ISkinChangingCallback?
    ) {
        lifecycle.coroutineScope.launchWhenResumed {
            callback?.onStartChangeSkin()
            try {
                withContext(Dispatchers.IO) { skinManager.loadPlugin(pluginPath, pluginPkg) }
            } catch (e: Exception) {
                e.printStackTrace()
                callback?.onChangeSkinError(e)
                return@launchWhenResumed
            }
            skinManager.notifyChangedListener()
            skinManager.updatePluginInfo(pluginPath, pluginPkg)
            callback?.onChangeSkinComplete()
        }
    }

    /**
     * 保存插件信息
     */
    private fun updatePluginInfo(path: String?, pkg: String?) {
        mSkinConfigFactory.savePluginPath(path)
        mSkinConfigFactory.savePluginPkg(pkg)
    }

    /**
     * 通知更新
     */
    private fun notifyChangedListener() {
        mListeners.forEach {
            skinChange(it)
            it.onSkinChanged()
        }
    }

    /**
     * 皮肤变了
     */
    fun skinChange(listener: ISkinChangedListener?) {
        val skinViews = mSkinViewMaps[listener]
        if (skinViews == null) {
            Timber.d("skinChange：===> skinViews is null")
            //此处必须返回，否则无法换肤成功
            return
        }
        skinViews.forEach {
            it.apply()
        }
    }

    /**
     * 是否需要换肤
     */
    fun isNeedChangeSkin(): Boolean = usePlugin() || useSuffix()

    /**
     * 使用插件
     */
    private fun usePlugin(): Boolean = mCurrentPath.trim().isNotEmpty()

    /**
     * 使用后缀
     */
    private fun useSuffix(): Boolean = mSuffix.trim().isNotEmpty()

    companion object {

        @JvmStatic
        val instance by lazy { SkinManager() }

        @JvmStatic
        fun hookActivity(activity: AppCompatActivity, listener: ISkinChangedListener?) {
            val inflater = LayoutInflater.from(activity)
            hookFactorySet(inflater)
            val factory = SkinFactory(activity.delegate, listener)
            LayoutInflaterCompat.setFactory2(inflater, factory)
        }

        private fun hookFactorySet(inflater: LayoutInflater) {
            // 利用反射去修改mFactorySet的值为false，防止抛出 A factory has already been set on this...
            try {
                @SuppressLint("SoonBlockedPrivateApi") val mFactorySet =
                    LayoutInflater::class.java.getDeclaredField("mFactorySet")
                mFactorySet.isAccessible = true
                mFactorySet[inflater] = false
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
    }
}