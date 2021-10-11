package cn.cqautotest.sunnybeach.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.util.dp
import cn.cqautotest.sunnybeach.util.setFixOnClickListener
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
    private var mColumns = 1
    private var mRows = 0
    private val mManager = GridLayoutManager(context, mColumns)

    init {
        layoutManager = mManager
        adapter = mAdapter
    }

    fun setSpanCount(spanCount: Int): SimpleGridLayout {
        mColumns = spanCount
        mManager.spanCount = spanCount
        return this
    }

    fun setData(data: List<String>?) {
        if (data == null) {
            mAdapter.setData(null)
        } else {
            val dataCount = data.size
            mRows = (dataCount / mColumns.toDouble()).toInt()
            mAdapter.setData(data)
        }
    }

    fun setOnNineGridClickListener(listener: OnNineGridClickListener): SimpleGridLayout {
        mAdapter.setOnNineGridClickListener(listener)
        return this
    }

    interface OnNineGridClickListener {
        fun onNineGridClick(sources: List<String>, index: Int)
    }

    companion object {
        class GridLayoutAdapter(private val mData: MutableList<String> = arrayListOf()) :
            RecyclerView.Adapter<GridViewHolder>() {

            private lateinit var mListener: OnNineGridClickListener

            fun setOnNineGridClickListener(listener: OnNineGridClickListener) {
                mListener = listener
            }

            fun setData(data: List<String>?) {
                mData.clear()
                data?.let {
                    mData.addAll(it)
                }
                notifyItemRangeChanged(0, itemCount)
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
                val itemView = createLayout(parent, parent.context, viewType)
                return GridViewHolder(itemView)
            }

            private fun createLayout(parent: ViewGroup, context: Context, viewType: Int): View {
                return ImageView(context).apply {
                    tag = "imageView"
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    layoutParams = LinearLayout.LayoutParams(120.dp, 120.dp)
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
    }
}