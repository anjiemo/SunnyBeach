package cn.cqautotest.sunnybeach.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.collection.arrayMapOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import net.cachapa.expandablelayout.ExpandableLayout
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/7/25
 * desc   : 可折叠的评论列表
 *
 * 具体功能描述：
 * 1、可滚动的列表：√
 * 2、group item：√
 * 3、child item：√
 * 4、可折叠（group item 和 child item 可以感知当前 group item 是否展开）：×
 * 5、支持自定义设置 group item 分割线（样式、高度）：×
 * 6、支持自定义设置 child item 分割线（样式，高度）：×
 * 7、当前 group item 是否可以响应点击事件：×
 * 8、当前 child item 是否可以响应点击事件：×
 * 8、可以在任意位置追加 group item 和 child item：×
 */
class CollapsibleCommentList @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private var mRecyclerView: RecyclerView = RecyclerView(context)

    init {
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = SimpleCollapsibleCommentListAdapter()
        mRecyclerView.adapter = adapter
        mRecyclerView.background = ColorDrawable(Color.LTGRAY)
        addView(mRecyclerView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        val groupData = arrayListOf<String>()
        val childData = arrayListOf<List<String>>()
        repeat(10) { groupIndex ->
            val child = arrayListOf<String>()
            repeat(3) { childIndex ->
                child.add("杀杀杀")
            }
            groupData.add("萨达")
            childData.add(child)
        }
        adapter.setData(groupData, childData)
    }

    inner class SimpleCollapsibleCommentListAdapter :
        CollapsibleCommentListAdapter<String, String, BaseCollapsibleCommentListViewHolder>() {
        override fun onCreateChildViewHolder(
            context: Context,
            parent: ViewGroup
        ): BaseCollapsibleCommentListViewHolder {
            val itemView = TextView(context)
            itemView.text = "相亲相爱一家人"
            // LayoutInflater.from(context).inflate(R.layout.fish_pond_list_item, parent, false)
            return CollapsibleCommentListChildViewHolder(itemView)
        }

        override fun onCreateGroupViewHolder(
            context: Context,
            parent: ViewGroup
        ): BaseCollapsibleCommentListViewHolder {
            val itemView =
                LayoutInflater.from(context)
                    .inflate(R.layout.collapsible_comment_list_item, parent, false)
            return CollapsibleCommentListGroupViewHolder(itemView)
        }

        override fun onBindChildViewHolder(
            holder: CollapsibleCommentListChildViewHolder,
            position: Int,
            isExpanded: Boolean
        ) {
            Timber.d("child position is $position")
        }

        override fun onBindGroupViewHolder(
            holder: CollapsibleCommentListGroupViewHolder,
            position: Int,
            isExpanded: Boolean
        ) {
            if (isExpanded) holder.expandableLayout.collapse() else holder.expandableLayout.expand()
            holder.itemView.background = ColorDrawable(if (isExpanded) Color.RED else Color.GREEN)
            Timber.d("group position is $position isExpanded is $isExpanded")
        }
    }

    abstract inner class CollapsibleCommentListAdapter<G, C, VH : BaseCollapsibleCommentListViewHolder> :
        RecyclerView.Adapter<VH>() {

        private val mItemMetadata: MutableList<CollapsibleCommentListType> = arrayListOf()
        private val mGroupData: MutableList<G> = arrayListOf()
        private val mChildData: MutableList<List<C>> = arrayListOf()

        // position -> realPosition
        private val mIndexMap = arrayMapOf<Int, Pair<Int, Boolean>>()

        // position -> (groupId: isExpanded)
        private val mGroupIdMap = arrayMapOf<Int, Pair<Int, Boolean>>()
        private val mGroupMetaDataMap = arrayMapOf<Int, GroupMetadata>()

        @SuppressLint("NotifyDataSetChanged")
        fun setData(groupData: List<G>, childData: List<List<C>>) {
            mGroupData.clear()
            mChildData.clear()
            mGroupData.addAll(groupData)
            mChildData.addAll(childData)
            var index = 0
            mGroupData.forEachIndexed { groupIndex, _ ->
                mItemMetadata.add(CollapsibleCommentListType.GROUP)
                // mGroupMetaDataMap[index] =
                //     GroupMetadata.obtain(groupIndex, index, index + mChildData[groupIndex].size)
                mIndexMap[index] = Pair(groupIndex, false)
                mGroupIdMap[index] = Pair(groupIndex, false)
                index++
                mChildData[groupIndex].forEachIndexed { childIndex, _ ->
                    mItemMetadata.add(CollapsibleCommentListType.CHILD)
                    mIndexMap[index] = Pair(childIndex, false)
                    mGroupIdMap[index] = Pair(groupIndex, false)
                    index++
                }
            }
            Timber.d("mItemMetadata size is ${mItemMetadata.size}")
            Timber.d("mGroupData size is ${mGroupData.size}")
            Timber.d("mChildData size is ${mChildData.size}")
            notifyDataSetChanged()
        }

        @SuppressLint("NotifyDataSetChanged")
        fun clearData() {
            mGroupData.clear()
            mChildData.clear()
            notifyDataSetChanged()
        }

        final override fun getItemViewType(position: Int): Int = mItemMetadata[position].type

        final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val context = parent.context
            Timber.d("viewType is $viewType")
            return when (viewType) {
                CollapsibleCommentListType.GROUP.type -> onCreateGroupViewHolder(context, parent)
                CollapsibleCommentListType.CHILD.type -> onCreateChildViewHolder(context, parent)
                else -> throw UnsupportedOperationException("This type of item Type is not supported!")
            }
        }

        abstract fun onCreateChildViewHolder(context: Context, parent: ViewGroup): VH

        abstract fun onCreateGroupViewHolder(context: Context, parent: ViewGroup): VH

        override fun onBindViewHolder(holder: VH, position: Int) {
            when (holder) {
                is CollapsibleCommentListChildViewHolder -> {
                    val pair = mGroupIdMap[position]
                    val isExpanded = pair?.second ?: false
                    onBindChildViewHolder(holder, getRealPosition(position), isExpanded)
                }
                is CollapsibleCommentListGroupViewHolder -> {
                    val pair = mGroupIdMap[position]
                    val isExpanded = pair?.second ?: false
                    holder.itemView.setOnClickListener {
                        mGroupIdMap[position] = Pair(pair?.first ?: -1, isExpanded.not())
                        // 刷新当前 groupItem 的状态
                        notifyItemChanged(position)
                        // TODO: 2021/7/25 此处不够严谨，有可能该组下没有 childItem
                        val childStartPosition = position + 1
                        // TODO: 2021/7/25 删除该组下的数据，并通知删除了数据
                    }
                    onBindGroupViewHolder(holder, getRealPosition(position), isExpanded)
                }
                else -> throw UnsupportedOperationException("This type of item Type is not supported!")
            }
        }

        abstract fun onBindChildViewHolder(
            holder: CollapsibleCommentListChildViewHolder,
            position: Int,
            isExpanded: Boolean
        )

        abstract fun onBindGroupViewHolder(
            holder: CollapsibleCommentListGroupViewHolder,
            position: Int,
            isExpanded: Boolean
        )

        final override fun getItemCount(): Int = mItemMetadata.size

        fun findItemInfoByPosition(position: Int): Pair<Int, Boolean>? = mIndexMap[position]

        fun getRealPosition(position: Int): Int = findItemInfoByPosition(position)?.first ?: -1

        fun getGroupId(position: Int): Int = mGroupIdMap[position]?.first ?: -1

        fun checkGroupIsExpanded(groupPosition: Int): Boolean =
            mGroupIdMap[groupPosition]?.second ?: false

        fun getChild(position: Int): List<C> = mChildData[position]

        fun getGroup(position: Int): G = mGroupData[position]

        fun getChildCount(position: Int): Int = mChildData[position].size

        fun getGroupCount(): Int = mGroupData.size
    }

    enum class CollapsibleCommentListType(val type: Int) {
        GROUP(2), CHILD(1)
    }

    class GroupMetadata private constructor() {

        // 组的 id
        var groupId: Int = 0

        // 组的位置
        var groupPosition: Int = 0

        // 最后一个孩子所在的位置
        var lastChildPosition: Int = 0

        companion object {
            fun obtain(groupId: Int, groupPosition: Int, lastChildPosition: Int): GroupMetadata {
                val gm = GroupMetadata()
                gm.groupId = groupId
                gm.groupPosition = groupPosition
                gm.lastChildPosition = lastChildPosition
                return gm
            }
        }
    }

    /**
     * group ViewHolder
     */
    inner class CollapsibleCommentListGroupViewHolder(itemView: View) :
        BaseCollapsibleCommentListViewHolder(itemView) {
        val expandableLayout: ExpandableLayout = itemView.findViewById(R.id.expandable_layout)
    }

    /**
     * child ViewHolder
     */
    inner class CollapsibleCommentListChildViewHolder(itemView: View) :
        BaseCollapsibleCommentListViewHolder(itemView)

    abstract inner class BaseCollapsibleCommentListViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView)
}