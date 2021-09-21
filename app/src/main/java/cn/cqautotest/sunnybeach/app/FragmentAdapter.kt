package cn.cqautotest.sunnybeach.app

import android.os.Bundle
import androidx.collection.arrayMapOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

abstract class FragmentAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    private val mArguments = arrayMapOf<Int, Bundle>()
    private val mFragmentHashCodes = arrayMapOf<Int, Int>()

    fun addArguments(index: Int, arguments: Bundle) {
        mArguments[index] = arguments
        mFragmentHashCodes[index] = arguments.hashCode()
    }

    fun getArguments(index: Int): Bundle? {
        return mArguments[index]
    }

    override fun getItemCount(): Int {
        return mArguments.size
    }
}
