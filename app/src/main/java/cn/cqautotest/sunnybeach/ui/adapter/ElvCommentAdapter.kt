package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppApplication
import cn.cqautotest.sunnybeach.model.FishPondComment
import cn.cqautotest.sunnybeach.util.DateHelper
import cn.cqautotest.sunnybeach.util.dp
import cn.cqautotest.sunnybeach.util.setRoundRectBg
import com.bumptech.glide.Glide

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/7/11
 * desc   : 鱼塘详情评论列表的适配器
 */
class ElvCommentAdapter : BaseExpandableListAdapter() {

    private var mInflater: LayoutInflater =
        AppApplication.getInstance().applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val replyBgColor = Color.parseColor("#F5F5F5")
    private val mGroupData: MutableList<FishPondComment.FishPondCommentItem> = arrayListOf()
    private val mChildData: MutableList<List<FishPondComment.FishPondCommentItem.SubComment>> =
        arrayListOf()

    fun setData(
        groupData: List<FishPondComment.FishPondCommentItem>,
        childData: List<List<FishPondComment.FishPondCommentItem.SubComment>>
    ) {
        mGroupData.clear()
        mChildData.clear()
        mGroupData.addAll(groupData)
        mChildData.addAll(childData)
        notifyDataSetChanged()
    }

    override fun getGroupCount(): Int = mGroupData.size

    override fun getChildrenCount(groupPosition: Int): Int = mChildData[groupPosition].size

    override fun getGroup(groupPosition: Int): FishPondComment.FishPondCommentItem =
        mGroupData[groupPosition]

    override fun getChild(
        groupPosition: Int,
        childPosition: Int
    ): FishPondComment.FishPondCommentItem.SubComment =
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

    private fun newGroupView(parent: ViewGroup?): View =
        mInflater.inflate(R.layout.fish_pond_detail_commend_list, parent, false)

    private fun newChildView(parent: ViewGroup?): View =
        mInflater.inflate(R.layout.fish_pond_detail_commend_list, parent, false)

    @SuppressLint("SetTextI18n")
    private fun bindGroupView(
        v: View,
        isExpanded: Boolean,
        group: FishPondComment.FishPondCommentItem
    ) {
        v.run {
            val flAvatarContainer = findViewById<View>(R.id.fl_avatar_container)
            val ivAvatar = findViewById<ImageView>(R.id.iv_fish_pond_avatar)
            val cbNickname = findViewById<CheckBox>(R.id.cb_fish_pond_nick_name)
            val tvDesc = findViewById<TextView>(R.id.tv_fish_pond_desc)
            val tvReply = findViewById<TextView>(R.id.tv_reply_msg)
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
            cbNickname.isChecked = isExpanded
            if (group.subComments.isNullOrEmpty()) {
                cbNickname.clearArrowsCompoundDrawablesWithIntrinsicBounds()
            } else {
                cbNickname.setArrowsCompoundDrawablesWithIntrinsicBounds()
            }
            cbNickname.setTextColor(
                ContextCompat.getColor(
                    context, if (group.vip) {
                        R.color.pink
                    } else {
                        R.color.black
                    }
                )
            )
            cbNickname.text = group.getNickName()
            tvDesc.text = HtmlCompat.fromHtml(
                "${group.position} <font color=\"#0084ff\">@${group.company}</font> · "
                        + DateHelper.transform2FriendlyTimeSpanByNow("${group.createTime}:00"),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            tvReply.setRoundRectBg(color = replyBgColor, cornerRadius = 4.dp)
            tvReply.text = HtmlCompat.fromHtml(
                "回复 <font color=\"#0084ff\">@楼主</font> ：${group.content}",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindChildView(
        v: View,
        isLastChild: Boolean,
        child: FishPondComment.FishPondCommentItem.SubComment
    ) {
        v.run {
            setPadding(50.dp, 0, 0, 0)
            val flAvatarContainer = findViewById<View>(R.id.fl_avatar_container)
            val ivAvatar = findViewById<ImageView>(R.id.iv_fish_pond_avatar)
            val cbNickname = findViewById<TextView>(R.id.cb_fish_pond_nick_name)
            val tvDesc = findViewById<TextView>(R.id.tv_fish_pond_desc)
            val tvReply = findViewById<TextView>(R.id.tv_reply_msg)
            flAvatarContainer.background = if (child.vip) ContextCompat.getDrawable(
                context,
                R.drawable.avatar_circle_vip_ic
            ) else null
            Glide.with(v)
                .load(child.avatar)
                .placeholder(R.mipmap.ic_default_avatar)
                .error(R.mipmap.ic_default_avatar)
                .circleCrop()
                .into(ivAvatar)
            cbNickname.setTextColor(
                ContextCompat.getColor(
                    context, if (child.vip) {
                        R.color.pink
                    } else {
                        R.color.black
                    }
                )
            )
            cbNickname.clearArrowsCompoundDrawablesWithIntrinsicBounds()
            cbNickname.text = child.getNickName()
            tvDesc.text = HtmlCompat.fromHtml(
                "${child.position}<font color=\"#0084ff\">@${child.company}</font> · "
                        + DateHelper.transform2FriendlyTimeSpanByNow("${child.createTime}:00"),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            tvReply.setRoundRectBg(color = replyBgColor, cornerRadius = 4.dp)
            tvReply.text =
                HtmlCompat.fromHtml(
                    "回复 <font color=\"#0084ff\">@${child.getTargetUserNickname()}</font> ：${child.content}",
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
        }
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true

    private fun TextView.clearArrowsCompoundDrawablesWithIntrinsicBounds() {
        setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
    }

    private fun TextView.setArrowsCompoundDrawablesWithIntrinsicBounds() {
        setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            ContextCompat.getDrawable(context, R.drawable.elv_arrows_selector),
            null
        )
    }
}