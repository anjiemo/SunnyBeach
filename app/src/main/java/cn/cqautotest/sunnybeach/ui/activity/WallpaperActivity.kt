package cn.cqautotest.sunnybeach.ui.activity

import android.content.Intent
import android.view.View
import androidx.core.app.SharedElementCallback
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.ui.fragment.DiscoverFragment

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/26
 * desc   : 发现 Activity
 */
class WallpaperActivity : AppActivity() {

    override fun getLayoutId(): Int = R.layout.wallpaper_activity

    override fun initView() {
        val fragment = supportFragmentManager.findFragmentById(R.id.wallpaper_fragment_container)
        // 避免 Activity 重建后，重复 add Fragment 到容器里
        takeIf { fragment == null }?.let {
            val ft = supportFragmentManager.beginTransaction()
            ft.add(R.id.wallpaper_fragment_container, DiscoverFragment.newInstance())
            ft.commitAllowingStateLoss()
        }
        // 关键：在 Activity 级别设置映射回调，并委托给 Fragment 处理
        setExitSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(names: MutableList<String>, sharedElements: MutableMap<String, View>) {
                val fragment = supportFragmentManager.findFragmentById(R.id.wallpaper_fragment_container) as? DiscoverFragment
                fragment?.onMapSharedElements(names, sharedElements)
            }
        })
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        // 1. 立即推迟 Activity 动画（至关重要）
        supportPostponeEnterTransition()
        super.onActivityReenter(resultCode, data)
        // 2. 转发给内部的 DiscoverFragment 处理滚动
        val fragment = supportFragmentManager.findFragmentById(R.id.wallpaper_fragment_container) as? DiscoverFragment
        fragment?.handleActivityReenter(resultCode, data)
    }

    override fun initData() {

    }
}