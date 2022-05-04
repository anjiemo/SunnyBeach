package cn.cqautotest.sunnybeach.ui.activity

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
    }

    override fun initData() {

    }
}