package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.databinding.QaListItemBinding
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.QaInfo
import com.blankj.utilcode.util.TimeUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/18
 * desc   : 问答列表的适配器
 */
class QaListAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<QaInfo.QaInfoItem, QaListAdapter.QaListViewHolder>(QaDiffCallback()) {

    class QaDiffCallback : DiffUtil.ItemCallback<QaInfo.QaInfoItem>() {
        override fun areItemsTheSame(
            oldItem: QaInfo.QaInfoItem,
            newItem: QaInfo.QaInfoItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: QaInfo.QaInfoItem,
            newItem: QaInfo.QaInfoItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val mSdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.SIMPLIFIED_CHINESE)

    inner class QaListViewHolder(val binding: QaListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onViewAttachedToWindow(holder: QaListViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: QaListAdapter.QaListViewHolder, position: Int) {
        val item = getItem(position) ?: return
        val itemView = holder.itemView
        val context = itemView.context
        val binding = holder.binding
        val tvAnswerCount = binding.tvAnswerCount
        val tvViewCount = binding.tvViewCount
        val tvGoldCount = binding.tvGoldCount
        val tvQaTitle = binding.tvQaTitle
        val ivQaAvatar = binding.ivQaAvatar
        val tvQaNickName = binding.tvQaNickName
        val tvDesc = binding.tvDesc
        val llQaLabelContainer = binding.llQaLabelContainer
        itemView.setFixOnClickListener {
            adapterDelegate.onItemClick(it, position)
        }
        val answerCount = item.answerCount
        val hasAnswer = answerCount > 0
        val isResolve = item.isResolve.toIntOrNull() == 1
        val answerColor = Color.parseColor("#48A868")
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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QaListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = QaListItemBinding.inflate(inflater, parent, false)
        return QaListViewHolder(binding)
    }
}