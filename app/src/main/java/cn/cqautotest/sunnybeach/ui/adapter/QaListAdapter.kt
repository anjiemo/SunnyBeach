package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.databinding.QaListItemBinding
import cn.cqautotest.sunnybeach.ktx.asViewBinding
import cn.cqautotest.sunnybeach.ktx.context
import cn.cqautotest.sunnybeach.ktx.itemDiffCallback
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.QaInfo
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import com.blankj.utilcode.util.TimeUtils
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/18
 * desc   : 问答列表的适配器
 */
class QaListAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<QaInfo.QaInfoItem, QaListAdapter.QaListViewHolder>(diffCallback) {

    private val mSdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.SIMPLIFIED_CHINESE)

    inner class QaListViewHolder(val binding: QaListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.asViewBinding<QaListItemBinding>())

        @SuppressLint("SetTextI18n")
        fun onBinding(item: QaInfo.QaInfoItem?, position: Int) {
            item ?: return
            val answerCount = item.answerCount
            val hasAnswer = answerCount > 0
            val isResolve = item.isResolve.toIntOrNull() == 1
            val answerColor = "#48A868".toColorInt()
            with(binding) {
                tvAnswerCount.text = "${if (isResolve) "√" else ""} $answerCount 答案"
                val answerTextColor = when {
                    isResolve -> Color.WHITE
                    hasAnswer -> answerColor
                    else -> ContextCompat.getColor(context, R.color.default_font_color)
                }
                tvAnswerCount.setTextColor(answerTextColor)
                tvAnswerCount.shapeDrawableBuilder.apply {
                    strokeColor = if (hasAnswer) answerColor else Color.TRANSPARENT
                    solidColor = when {
                        isResolve -> answerColor
                        else -> Color.WHITE
                    }
                }.intoBackground()
                tvViewCount.text = item.viewCount.toString()
                tvGoldCount.text = item.sob.toString()

                val viewDrawable = ContextCompat.getDrawable(context, R.mipmap.ic_view)
                val viewTextSize = tvViewCount.textSize.toInt() * 2
                viewDrawable?.setBounds(0, 0, viewTextSize, viewTextSize)
                tvViewCount.setCompoundDrawables(viewDrawable, null, null, null)

                val goldDrawable = ContextCompat.getDrawable(context, R.mipmap.ic_gold_currency_1)
                val goldTextSize = tvGoldCount.textSize.toInt() * 2
                goldDrawable?.setBounds(0, 0, goldTextSize, goldTextSize)
                tvGoldCount.setCompoundDrawables(goldDrawable, null, null, null)

                val isVip = item.isVip.toIntOrNull() == 1
                // TODO: 头像控件在图标小尺寸下显示不正常，暂时不设置 VIP 标识
                ivQaAvatar.loadAvatar(false, item.avatar)

                tvQaNickName.text = item.nickname
                tvQaNickName.setTextColor(UserManager.getNickNameColor(isVip))

                tvQaTitle.text = item.title
                tvDesc.text = TimeUtils.getFriendlyTimeSpanByNow(item.createTime, mSdf)
                llQaLabelContainer.apply {
                    forEach {
                        it.isVisible = false
                        (it as? TextView)?.text = ""
                    }
                    item.labels.onEachIndexed { index, text ->
                        if (index < itemCount) {
                            val labelView = getChildAt(index) as? TextView
                            labelView?.isVisible = true
                            labelView?.text = text
                        }
                    }
                }
            }
        }
    }

    override fun onViewAttachedToWindow(holder: QaListViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    override fun onBindViewHolder(holder: QaListAdapter.QaListViewHolder, position: Int) {
        holder.itemView.setFixOnClickListener { adapterDelegate.onItemClick(it, position) }
        holder.onBinding(getItem(position), position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QaListViewHolder = QaListViewHolder(parent)

    companion object {

        private val diffCallback =
            itemDiffCallback<QaInfo.QaInfoItem>({ oldItem, newItem -> oldItem.id == newItem.id }) { oldItem, newItem -> oldItem == newItem }
    }
}