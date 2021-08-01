package cn.cqautotest.sunnybeach.app

import android.annotation.SuppressLint
import androidx.collection.arrayMapOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hjq.base.BaseActivity
import com.hjq.base.BaseFragment

class FragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment), LifecycleObserver {

    private val mFragmentOfMap = arrayMapOf<Int, Fragment>()
    private val mFragmentHashCodes = arrayMapOf<Int, Int>()

    @SuppressLint("NotifyDataSetChanged")
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

    @SuppressLint("NotifyDataSetChanged")
    fun putAllFragmentMap(fragmentMap: Map<Int, AppFragment<AppActivity>>) {
        mFragmentOfMap.putAll(fragmentMap)
        mFragmentOfMap.forEach { (index, fragment) ->
            mFragmentHashCodes[index] = fragment.hashCode()
        }
        if (mFragmentOfMap.isNotEmpty()) {
            val size = mFragmentOfMap.size
            notifyItemRangeInserted(size - 1, fragmentMap.size)
        } else {
            notifyDataSetChanged()
        }
    }

    fun replaceFragment(index: Int, baseFragment: BaseFragment<BaseActivity>) {
        mFragmentOfMap[index] = baseFragment
        mFragmentHashCodes[index] = baseFragment.hashCode()
        notifyItemChanged(index)
    }

    override fun getItemId(position: Int): Long = mFragmentOfMap[position].hashCode().toLong()

    override fun containsItem(itemId: Long): Boolean =
        mFragmentHashCodes.containsValue(itemId.toInt())

    override fun getItemCount(): Int = mFragmentOfMap.size

    override fun createFragment(position: Int): Fragment = mFragmentOfMap[position]!!
}
