package cn.cqautotest.sunnybeach.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/12/11
 * desc   : 适用于动态 添加、删除、替换 Fragment 的场景。
 */
class FragmentAdapter : FragmentStateAdapter {

    private var mLifecycle: Lifecycle
    private var mLifecycleObserver: LifecycleObserver? = null

    // key, Fragment
    private val mFragments = mutableListOf<Pair<String, Fragment>>()

    constructor(fragment: Fragment) : super(fragment) {
        mLifecycle = fragment.lifecycle
    }

    constructor(fragmentActivity: FragmentActivity) : super(fragmentActivity) {
        mLifecycle = fragmentActivity.lifecycle
    }

    constructor(fragmentManager: FragmentManager, lifecycle: Lifecycle) : super(fragmentManager, lifecycle) {
        mLifecycle = lifecycle
    }

    override fun createFragment(position: Int): Fragment {
        return mFragments[position].second
    }

    override fun getItemCount(): Int {
        return mFragments.count()
    }

    override fun getItemId(position: Int): Long {
        return System.identityHashCode(mFragments[position].first).toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        return mFragments.find { System.identityHashCode(it.first).toLong() == itemId } != null
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mLifecycleObserver = object : DefaultLifecycleObserver {

            override fun onDestroy(owner: LifecycleOwner) {
                mFragments.clear()
            }
        }.also { mLifecycle.addObserver(it) }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        mLifecycleObserver?.let { mLifecycle.removeObserver(it) }
        mLifecycleObserver = null
    }

    private fun safeOperation(action: () -> Unit) {
        if (mLifecycle.currentState == Lifecycle.State.DESTROYED) {
            Timber.w("are you ok? You try to manipulate the fragment during the destroyed lifecycle!")
            return
        }
        action.invoke()
    }

    fun setFragmentList(fragments: List<Pair<String, Fragment>>) = safeOperation {
        mFragments.clear()
        mFragments.addAll(fragments)
        notifyItemRangeChanged(0, itemCount)
    }

    fun addFragment(fragment: Pair<String, Fragment>) = safeOperation {
        mFragments.add(fragment)
        notifyItemInserted(itemCount)
    }

    fun addFragment(position: Int, fragment: Pair<String, Fragment>) = safeOperation {
        mFragments.add(position, fragment)
        notifyItemInserted(position)
    }

    fun addFragment(position: Int, fragments: List<Pair<String, Fragment>>) = safeOperation {
        mFragments.addAll(position, fragments)
        notifyItemRangeInserted(position, fragments.count())
    }

    fun addFragment(fragments: List<Pair<String, Fragment>>) = safeOperation {
        mFragments.addAll(itemCount, fragments)
        notifyItemRangeInserted(itemCount, fragments.count())
    }

    fun removeFragmentAt(position: Int) = safeOperation {
        mFragments.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeFragment(key: String) = safeOperation {
        val index = mFragments.indexOfFirst { it.first == key }
        if (index != -1) {
            removeFragmentAt(index)
        }
    }

    fun replaceFragment(index: Int, fragment: Pair<String, Fragment>) = safeOperation {
        mFragments[index] = fragment
        notifyItemChanged(index)
    }

    fun replaceFragment(key: String, fragment: Pair<String, Fragment>) = safeOperation {
        val index = mFragments.indexOfFirst { it.first == key }
        if (index != -1) {
            replaceFragment(index, fragment)
        }
    }

    fun replaceFragment(fragment: Pair<String, Fragment>) = safeOperation {
        val index = mFragments.indexOfFirst { it.first == fragment.first }
        if (index != -1) {
            replaceFragment(index, fragment)
        }
    }

    fun getFragmentByKey(key: String): Fragment? {
        return mFragments.find { it.first == key }?.second
    }

    fun getFragment(position: Int): Fragment? = mFragments.getOrNull(position)?.second

    fun getFragmentIndex(clazz: Class<out Fragment?>?): Int {
        return mFragments.indexOfFirst { it.second.javaClass == clazz }
    }
}