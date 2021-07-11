package cn.cqautotest.sunnybeach.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppApplication
import cn.cqautotest.sunnybeach.model.FishPondRecommend
import com.bumptech.glide.Glide

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/7/11
 * desc   : 鱼塘详情评论列表的适配器
 */
class ElvRecommendAdapter : BaseExpandableListAdapter() {

    private var mInflater: LayoutInflater =
        AppApplication.getInstance()
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val mGroupData: MutableList<FishPondRecommend.FishPondRecommendItem> = arrayListOf()
    private val mChildData: MutableList<List<FishPondRecommend.FishPondRecommendItem.SubComment>> =
        arrayListOf()

    fun setData(
        groupData: List<FishPondRecommend.FishPondRecommendItem>,
        childData: List<List<FishPondRecommend.FishPondRecommendItem.SubComment>>
    ) {
        mGroupData.clear()
        mChildData.clear()
        mGroupData.addAll(groupData)
        mChildData.addAll(childData)
        notifyDataSetChanged()
    }

    override fun getGroupCount(): Int = mGroupData.size

    override fun getChildrenCount(groupPosition: Int): Int = mChildData[groupPosition].size

    override fun getGroup(groupPosition: Int): FishPondRecommend.FishPondRecommendItem =
        mGroupData[groupPosition]

    override fun getChild(
        groupPosition: Int,
        childPosition: Int
    ): FishPondRecommend.FishPondRecommendItem.SubComment =
        mChildData[groupPosition][childPosition]

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun hasStableIds(): Boolean = true

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val v: View = convertView ?: newGroupView(parent)
        bindGroupView(v, isExpanded, getGroup(groupPosition))
        return v
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val v: View = convertView ?: newChildView(parent)
        bindChildView(v, isLastChild, getChild(groupPosition, childPosition))
        return v
    }

    private fun bindGroupView(
        v: View,
        isExpanded: Boolean,
        group: FishPondRecommend.FishPondRecommendItem
    ) {
        v.run {
            val flAvatarContainer = findViewById<View>(R.id.fl_avatar_container)
            val ivAvatar = findViewById<ImageView>(R.id.iv_fish_pond_avatar)
            val tvNickname = findViewById<TextView>(R.id.tv_fish_pond_nick_name)
            val tvDesc = findViewById<TextView>(R.id.tv_fish_pond_desc)
            val tvContent = findViewById<TextView>(R.id.tv_fish_pond_recommend_content)
            flAvatarContainer.background = if (group.vip) ContextCompat.getDrawable(
                context,
                R.drawable.avatar_circle_vip_ic
            ) else null
            Glide.with(v)
                .load(group.avatar)
                .placeholder(R.mipmap.ic_default_avatar)
                .error(R.mipmap.ic_default_avatar)
                .circleCrop()
                .into(ivAvatar)
            tvNickname.setTextColor(
                ContextCompat.getColor(
                    context, if (group.vip) {
                        R.color.pink
                    } else {
                        R.color.black
                    }
                )
            )
            tvNickname.text = group.nickname
            tvDesc.text = group.position
            tvContent.text = group.content
        }
    }

    private fun bindChildView(
        v: View,
        isLastChild: Boolean,
        child: FishPondRecommend.FishPondRecommendItem.SubComment
    ) {

    }

    private fun newGroupView(parent: ViewGroup?): View =
        mInflater.inflate(R.layout.fish_pond_detail_commend_list, parent, false)

    private fun newChildView(parent: ViewGroup?): View =
        mInflater.inflate(android.R.layout.simple_list_item_1, parent, false)

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true
}