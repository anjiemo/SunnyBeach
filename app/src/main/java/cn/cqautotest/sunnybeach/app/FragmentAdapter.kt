package cn.cqautotest.sunnybeach.app

import androidx.collection.arrayMapOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hjq.base.BaseActivity
import com.hjq.base.BaseFragment

class FragmentAdapter : FragmentStateAdapter, LifecycleObserver {

    private val mFragmentOfMap = arrayMapOf<Int, Fragment>()
    private val mFragmentHashCodes = arrayMapOf<Int, Int>()

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun resetFragmentData() {
        mFragmentOfMap.clear()
        mFragmentHashCodes.clear()
    }

    fun setFragmentMap(fragmentMap: Map<Int, AppFragment<AppActivity>>) {
        mFragmentOfMap.run {
            clear()
            putAll(fragmentMap)
        }
        mFragmentOfMap.forEach { (index, fragment) ->
            mFragmentHashCodes[index] = fragment.hashCode()
        }
        notifyDataSetChanged()
    }

    fun replaceFragment(index: Int, baseFragment: BaseFragment<BaseActivity>) {
        mFragmentOfMap[index] = baseFragment
        mFragmentHashCodes[index] = baseFragment.hashCode()
        notifyItemChanged(index)
    }

    constructor(fragmentActivity: FragmentActivity) : super(fragmentActivity)
    constructor(fragment: Fragment) : super(fragment)
    constructor(fragmentManager: FragmentManager, lifecycle: Lifecycle) : super(
        fragmentManager,
        lifecycle
    )

    override fun getItemId(position: Int): Long = mFragmentOfMap[position].hashCode().toLong()

    override fun containsItem(itemId: Long): Boolean =
        mFragmentHashCodes.containsValue(itemId.toInt())

    override fun getItemCount(): Int = mFragmentOfMap.size

    override fun createFragment(position: Int): Fragment = mFragmentOfMap[position] ?: Fragment()
}
