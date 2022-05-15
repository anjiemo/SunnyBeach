package cn.cqautotest.sunnybeach.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Outline
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import com.bumptech.glide.Glide

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/22
 * desc   : 简单宫格视图
 */
class SimpleGridLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {

    private val mAdapter = GridLayoutAdapter()
    private val mManager = GridLayoutManager(context, DEFAULT_SPAN_COUNT)

    init {
        layoutManager = mManager
        adapter = mAdapter
        val radius = 10.dp.toFloat()
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                outline?.setRoundRect(0, 0, width, height, radius)
            }
        }
        clipToOutline = true
    }

    fun setData(data: List<String> = listOf()) {
        val dataCount = data.size
        mAdapter.setData(data)
        mManager.spanCount = if (dataCount % 2 == 0) 2 else 3
        mManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int = when (dataCount) {
                1 -> 3
                else -> 1
            }
        }
        giveUpFocus()
    }

    private fun giveUpFocus() {
        // 清除焦点，避免与外层的 RecyclerView 抢占焦点
        isFocusable = false
        isFocusableInTouchMode = false
        clearFocus()
    }

    fun setOnNineGridClickListener(listener: OnNineGridClickListener): SimpleGridLayout {
        mAdapter.setOnNineGridClickListener(listener)
        return this
    }

    interface OnNineGridClickListener {
        fun onNineGridClick(sources: List<String>, index: Int)
    }

    class GridLayoutAdapter(private val mData: MutableList<String> = arrayListOf()) :
        RecyclerView.Adapter<GridViewHolder>() {

        private lateinit var mListener: OnNineGridClickListener

        fun setOnNineGridClickListener(listener: OnNineGridClickListener) {
            mListener = listener
        }

        @SuppressLint("NotifyDataSetChanged")
        fun setData(data: List<String>) {
            mData.clear()
            mData.addAll(data)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
            val itemView = createLayout(parent, parent.context, viewType)
            return GridViewHolder(itemView)
        }

        private fun createLayout(parent: ViewGroup, context: Context, viewType: Int): View {
            return ImageView(context).apply {
                tag = "imageView"
                scaleType = ImageView.ScaleType.CENTER_CROP
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 120.dp)
                maxHeight = 120.dp
            }
        }

        private fun getItem(position: Int) = mData[position]

        override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
            val item = getItem(position)
            val imageView = holder.imageView
            holder.itemView.setFixOnClickListener {
                if (::mListener.isInitialized) {
                    mListener.onNineGridClick(mData, position)
                }
            }
            Glide.with(imageView)
                .load(item)
                .into(imageView)
        }

        override fun getItemCount(): Int = mData.size
    }

    class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewWithTag("imageView")
    }

    companion object {
        private const val DEFAULT_SPAN_COUNT = 3
    }
}