package com.example.blogsystem.base

import androidx.collection.arrayMapOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentAdapter : FragmentStateAdapter {

    private val mFragmentOfMap = arrayMapOf<Int, Fragment>()
    private val mFragmentHashCodes = arrayMapOf<Int, Int>()

    fun setFragmentMap(fragmentMap: Map<Int, BaseFragment>) {
        mFragmentOfMap.run {
            clear()
            putAll(fragmentMap)
        }
        mFragmentOfMap.forEach { (index, fragment) ->
            mFragmentHashCodes[index] = fragment.hashCode()
        }
        notifyDataSetChanged()
    }

    fun replaceFragment(index: Int, baseFragment: BaseFragment) {
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
