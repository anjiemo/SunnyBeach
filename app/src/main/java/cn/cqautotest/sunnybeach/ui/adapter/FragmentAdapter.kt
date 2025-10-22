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
import java.lang.ref.WeakReference
import kotlin.math.min

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/12/11
 * desc   : 适用于动态 添加、删除、替换 Fragment 的场景。
 */
class FragmentAdapter : FragmentStateAdapter {

    private var mLifecycle: Lifecycle
    private var mLifecycleObserver: LifecycleObserver? = null
    private var isAttachedToRecyclerView = false

    // key, Fragment
    private val mFragments = mutableListOf<Pair<String, Fragment>>()

    // ID缓存提升containsItem性能
    private val mItemIdCache = mutableSetOf<Long>()

    constructor(fragment: Fragment) : super(fragment) {
        mLifecycle = fragment.lifecycle
        initialize()
    }

    constructor(fragmentActivity: FragmentActivity) : super(fragmentActivity) {
        mLifecycle = fragmentActivity.lifecycle
        initialize()
    }

    constructor(fragmentManager: FragmentManager, lifecycle: Lifecycle) : super(fragmentManager, lifecycle) {
        mLifecycle = lifecycle
        initialize()
    }

    private fun initialize() {
        registerLifecycleObserver()
    }

    private fun registerLifecycleObserver() {
        mLifecycleObserver = object : DefaultLifecycleObserver {

            override fun onDestroy(owner: LifecycleOwner) {
                mFragments.clear()
                Timber.d("Lifecycle destroyed, cleared fragments list")
            }
        }.also {
            mLifecycle.addObserver(it)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        isAttachedToRecyclerView = true
        Timber.d("Adapter attached to RecyclerView")
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        isAttachedToRecyclerView = false
        mLifecycleObserver?.let {
            mLifecycle.removeObserver(it)
            mLifecycleObserver = null
        }
        Timber.d("Adapter detached from RecyclerView")
    }

    override fun createFragment(position: Int): Fragment {
        return mFragments[position].second
    }

    override fun getItemCount(): Int = mFragments.count()

    override fun getItemId(position: Int): Long {
        return mFragments[position].first.hashCode().toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        return mItemIdCache.contains(itemId)
    }

    private fun safeOperation(operationName: String, action: () -> Unit) {
        if (mLifecycle.currentState == Lifecycle.State.DESTROYED) {
            Timber.w("Operation '$operationName' skipped: Lifecycle is destroyed")
            return
        }
        try {
            action.invoke()
        } catch (e: Exception) {
            Timber.e(e, "Operation '$operationName' failed")
        }
    }

    /**
     * 更新ID缓存
     */
    private fun updateItemIdCache() {
        mItemIdCache.clear()
        mItemIdCache.addAll(mFragments.map { it.first.hashCode().toLong() })
    }

    /**
     * 安全地通知数据变化
     */
    private fun safeNotifyDataSetChanged() {
        if (isAttachedToRecyclerView) {
            notifyDataSetChanged()
        }
    }

    private fun safeNotifyItemChanged(position: Int) {
        if (isAttachedToRecyclerView) {
            notifyItemChanged(position)
        }
    }

    private fun safeNotifyItemRangeChanged(positionStart: Int, itemCount: Int) {
        if (isAttachedToRecyclerView) {
            notifyItemRangeChanged(positionStart, itemCount)
        }
    }

    private fun safeNotifyItemInserted(position: Int) {
        if (isAttachedToRecyclerView) {
            notifyItemInserted(position)
        }
    }

    private fun safeNotifyItemRangeInserted(positionStart: Int, itemCount: Int) {
        if (isAttachedToRecyclerView) {
            notifyItemRangeInserted(positionStart, itemCount)
        }
    }

    private fun safeNotifyItemRemoved(position: Int) {
        if (isAttachedToRecyclerView) {
            notifyItemRemoved(position)
        }
    }

    private fun safeNotifyItemRangeRemoved(positionStart: Int, itemCount: Int) {
        if (isAttachedToRecyclerView) {
            notifyItemRangeRemoved(positionStart, itemCount)
        }
    }

    private fun safeNotifyItemMoved(fromPosition: Int, toPosition: Int) {
        if (isAttachedToRecyclerView) {
            notifyItemMoved(fromPosition, toPosition)
        }
    }

    // region Public API
    /**
     * 设置Fragment列表（全量替换）
     * [fragments] 新的Fragment列表
     */
    fun setFragmentList(fragments: List<Pair<String, Fragment>>) = safeOperation("setFragmentList") {
        val oldCount = mFragments.count()
        mFragments.clear()
        mFragments.addAll(fragments)
        updateItemIdCache()

        if (oldCount > 0 || mFragments.isNotEmpty()) {
            safeNotifyDataSetChanged()
        }
        Timber.d("Fragment list set with ${fragments.count()} items")
    }

    /**
     * 添加单个Fragment到末尾
     * [fragment] 要添加的Fragment
     */
    fun addFragment(fragment: Pair<String, Fragment>) = safeOperation("addFragment") {
        mFragments.add(fragment)
        updateItemIdCache()
        safeNotifyItemInserted(mFragments.count() - 1)
        Timber.d("Fragment added: ${fragment.first}")
    }

    /**
     * 在指定位置添加单个Fragment
     * [position] 插入位置
     * [fragment] 要添加的Fragment
     */
    fun addFragment(position: Int, fragment: Pair<String, Fragment>) = safeOperation("addFragment") {
        val safePosition = min(position, mFragments.count())
        mFragments.add(safePosition, fragment)
        updateItemIdCache()
        safeNotifyItemInserted(safePosition)
        Timber.d("Fragment added at position $safePosition: ${fragment.first}")
    }

    /**
     * 在指定位置添加多个Fragment
     * [position] 插入位置
     * [fragments] 要添加的Fragment列表
     */
    fun addFragment(position: Int, fragments: List<Pair<String, Fragment>>) = safeOperation("addFragment") {
        if (mFragments.isEmpty()) return@safeOperation

        val safePosition = min(position, mFragments.count())
        mFragments.addAll(safePosition, fragments)
        updateItemIdCache()
        safeNotifyItemRangeInserted(safePosition, fragments.count())
        Timber.d("${fragments.count()} fragments added at position $safePosition")
    }

    /**
     * 在末尾添加多个Fragment
     * [fragments] 要添加的Fragment列表
     */
    fun addFragment(fragments: List<Pair<String, Fragment>>) = safeOperation("addFragment") {
        if (fragments.isEmpty()) return@safeOperation

        val startPosition = mFragments.count()
        mFragments.addAll(fragments)
        updateItemIdCache()
        safeNotifyItemRangeInserted(startPosition, fragments.count())
        Timber.d("${fragments.count()} fragments added at the end")
    }

    /**
     * 移除指定位置的Fragment
     * [position] 要移除的位置
     */
    fun removeFragmentAt(position: Int) = safeOperation("removeFragmentAt") {
        if (position in 0..mFragments.lastIndex) {
            val removedKey = mFragments[position].first
            mFragments.removeAt(position)
            updateItemIdCache()
            safeNotifyItemRemoved(position)
            Timber.d("Fragment removed at position $position: $removedKey")
        } else {
            Timber.w("Remove fragment failed: position $position out of bounds")
        }
    }

    /**
     * 根据key移除Fragment
     * [key] 要移除的Fragment的key
     */
    fun removeFragment(key: String) = safeOperation("removeFragment") {
        val index = mFragments.indexOfFirst { it.first == key }
        if (index != -1) {
            removeFragmentAt(index)
        } else {
            Timber.w("Remove fragment failed: key '$key' not found")
        }
    }

    /**
     * 替换指定位置的Fragment
     * [index] 要替换的位置
     * [fragment] 新的Fragment
     */
    fun replaceFragment(index: Int, fragment: Pair<String, Fragment>) = safeOperation("replaceFragment") {
        if (index in 0..mFragments.lastIndex) {
            val oldKey = mFragments[index].first
            mFragments[index] = fragment
            updateItemIdCache()
            safeNotifyItemChanged(index)
            Timber.d("Replaced fragment at position $index: $oldKey -> ${fragment.first}")
        } else {
            Timber.w("Replace fragment failed: position $index out of bounds")
        }
    }

    /**
     * 根据key替换Fragment
     * [key] 要替换的Fragment的key
     * [fragment] 新的Fragment
     */
    fun replaceFragment(key: String, fragment: Pair<String, Fragment>) = safeOperation("replaceFragment") {
        val index = mFragments.indexOfFirst { it.first == key }
        if (index != -1) {
            replaceFragment(index, fragment)
        } else {
            Timber.w("Replace fragment failed: key '$key' not found")
        }
    }

    /**
     * 根据Fragment的key替换（使用新pair的key）
     * [fragment] 新的Fragment
     */
    fun replaceFragment(fragment: Pair<String, Fragment>) = safeOperation("replaceFragment") {
        val index = mFragments.indexOfFirst { it.first == fragment.first }
        if (index != -1) {
            replaceFragment(index, fragment)
        } else {
            Timber.w("Replace fragment failed: key '${fragment.first}' not found")
        }
    }

    /**
     * 获取Fragment的弱引用（通过key）
     * [key] Fragment的key
     * 返回 Fragment的弱引用，如果找不到返回null
     */
    fun getFragmentByKey(key: String): WeakReference<Fragment>? {
        return mFragments.find { it.first == key }?.second?.let { WeakReference(it) }
    }

    /**
     * 获取Fragment的弱引用（通过位置）
     * [position] Fragment的位置
     * 返回 Fragment的弱引用，如果位置无效返回null
     */
    fun getFragment(position: Int): WeakReference<Fragment>? = mFragments.getOrNull(position)?.second?.let { WeakReference(it) }

    /**
     * 根据Fragment类获取位置
     * [clazz] Fragment的类对象
     * 返回 Fragment的位置，如果找不到返回-1
     */
    fun getFragmentIndex(clazz: Class<out Fragment?>?): Int {
        if (clazz == null) return -1
        return mFragments.indexOfFirst { it.second.javaClass == clazz }
    }

    /**
     * 获取所有Fragment的key列表
     * 返回 Fragment的key列表
     */
    fun getFragmentKeys(): List<String> {
        return mFragments.map { it.first }
    }

    /**
     * 检查是否包含指定key的Fragment
     * [key] 要检查的key
     * 如果包含返回true，否则返回false
     */
    fun containsFragment(key: String): Boolean {
        return mFragments.any { it.first == key }
    }

    /**
     * 获取Fragment位置
     * [key] Fragment的key
     * 返回 Fragment的位置，如果找不到返回-1
     */
    fun getFragmentPosition(key: String): Int {
        return mFragments.indexOfFirst { it.first == key }
    }

    /**
     * 清空所有Fragment
     */
    fun clearAllFragments() = safeOperation("clearAllFragments") {
        val count = mFragments.count()
        if (count > 0) {
            mFragments.clear()
            mItemIdCache.clear()
            safeNotifyItemRangeRemoved(0, count)
            Timber.d("Cleared all $count fragments")
        }
    }

    /**
     * 批量更新Fragment（优化性能）
     * [updater] 更新操作，接收当前的Fragment列表
     */
    fun updateFragments(updater: (MutableList<Pair<String, Fragment>>) -> Unit) = safeOperation("updateFragments") {
        val oldCount = mFragments.count()
        updater(mFragments)
        updateItemIdCache()
        if (mFragments.count() != oldCount) {
            safeNotifyDataSetChanged()
        } else {
            safeNotifyItemRangeChanged(0, mFragments.count())
        }
        Timber.d("Fragments updated via batch operation")
    }

    /**
     * 交换两个Fragment的位置
     * [fromPosition] 起始位置
     * [toPosition] 目标位置
     */
    fun swapFragments(fromPosition: Int, toPosition: Int) = safeOperation("swapFragments") {
        if (fromPosition in 0..mFragments.lastIndex && toPosition in 0..mFragments.lastIndex) {
            val formFragment = mFragments[fromPosition]
            val toFragment = mFragments[toPosition]

            mFragments[fromPosition] = toFragment
            mFragments[toPosition] = formFragment
            updateItemIdCache()

            safeNotifyItemChanged(fromPosition)
            safeNotifyItemChanged(toPosition)
            Timber.d("Swapped fragments at positions $fromPosition and $toPosition")
        } else {
            Timber.w("Swap fragments failed: positions out of bounds (from=$fromPosition, to=$toPosition)")
        }
    }

    /**
     * 移动Fragment到新位置
     * [fromPosition] 起始位置
     * [toPosition] 目标位置
     */
    fun moveFragment(fromPosition: Int, toPosition: Int) = safeOperation("moveFragment") {
        if (fromPosition in 0..mFragments.lastIndex && toPosition in 0..mFragments.lastIndex) {
            val fragment = mFragments.removeAt(fromPosition)
            mFragments.add(toPosition, fragment)
            updateItemIdCache()
            safeNotifyItemMoved(fromPosition, toPosition)
            Timber.d("Moved fragment from position $fromPosition to $toPosition")
        } else {
            Timber.w("Move fragment failed: positions out of bounds (from=$fromPosition, to=$toPosition)")
        }
    }
    // endregion
}