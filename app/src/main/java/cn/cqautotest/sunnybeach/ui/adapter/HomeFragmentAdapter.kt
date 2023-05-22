package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.cqautotest.sunnybeach.ui.fragment.*

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/12/11
 * desc   : 首页 Fragment 适配器
 */
@SuppressLint("UnsafeOptInUsageError")
class HomeFragmentAdapter : FragmentStateAdapter {

    private lateinit var fragmentManager: FragmentManager

    constructor(fragmentActivity: FragmentActivity) : super(fragmentActivity)

    constructor(fragment: Fragment) : super(fragment)

    constructor(fragmentManager: FragmentManager, lifecycle: Lifecycle) : super(fragmentManager, lifecycle) {
        this.fragmentManager = fragmentManager
    }

    /**
     * 获取某个 Fragment 的索引（没有就返回 -1）
     */
    fun getFragmentIndex(clazz: Class<out Fragment?>?): Int {
        return when (clazz?.name) {
            FishListFragment::class.java.name -> 0
            QaListFragment::class.java.name -> 1
            ArticleListFragment::class.java.name -> 2
            CourseListFragment::class.java.name -> 3
            MyMeFragment::class.java.name -> 4
            else -> -1
        }
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FishListFragment.newInstance()
            1 -> QaListFragment.newInstance()
            2 -> ArticleListFragment.newInstance()
            3 -> CourseListFragment.newInstance()
            4 -> MyMeFragment.newInstance()
            else -> Fragment()
        }
    }

    fun getItem(position: Int): Fragment? = fragmentManager.findFragmentByTag("f${getItemId(position)}")

    override fun getItemCount(): Int = 5
}