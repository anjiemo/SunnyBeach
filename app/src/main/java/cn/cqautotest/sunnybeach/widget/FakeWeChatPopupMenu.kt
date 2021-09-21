package cn.cqautotest.sunnybeach.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.annotation.ColorInt
import androidx.annotation.Size
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.databinding.MenuSimpleTextItemBinding
import cn.cqautotest.sunnybeach.util.*

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/09
 * desc   : 防微信 PopupMenu
 */
class FakeWeChatPopupMenu private constructor(context: Context) : PopupWindow(context) {

    fun post(action: Runnable) {
        contentView.post(action)
    }

    class Builder(private val context: Context) {

        private lateinit var pm: FakeWeChatPopupMenu

        // 默认颜色：#0277FE
        private var mColor = Color.parseColor("#0277FE")
        private var mCornerRadius = 4.dp
        private val mMenuActionList = arrayListOf<MenuItem>()
        private var mWidth = 130.dp
        private var mHeight = 72.dp
        private var mIsOutsideTouchable = true
        private var mClickListener: (menuItem: MenuItem, position: Int) -> Unit = { _, _ -> }
        private var mLongClickListener: (menuItem: MenuItem, position: Int) -> Unit = { _, _ -> }

        fun color(@ColorInt color: Int): Builder {
            mColor = color
            return this
        }

        @SuppressLint("SupportAnnotationUsage")
        @ColorInt
        fun color(@Size(min = 1) colorString: String): Builder {
            mColor = Color.parseColor(colorString)
            return this
        }

        fun cornerRadius(cornerRadius: Int): Builder {
            mCornerRadius = cornerRadius
            return this
        }

        fun menuActionList(menus: List<MenuItem>): Builder {
            logByDebug(msg = "$TAG menuActionList：===> menus size is ${menus.size}")
            mMenuActionList.clear()
            mMenuActionList.addAll(menus)
            return this
        }

        fun width(width: Int): Builder {
            mWidth = width
            return this
        }

        fun height(height: Int): Builder {
            mHeight = height
            return this
        }

        /**
         * 此函数必须在 create 函数之前调用
         */
        fun setOnItemClickListener(block: (MenuItem, Int) -> Unit): Builder {
            mClickListener = block
            return this
        }

        fun setOnItemLongClickListener(block: (menuItem: MenuItem, position: Int) -> Unit): Builder {
            mLongClickListener = block
            return this
        }

        @SuppressLint("InflateParams")
        fun create(): FakeWeChatPopupMenu {
            pm = FakeWeChatPopupMenu(context)
            val popupLayout =
                LayoutInflater.from(context).inflate(R.layout.menu_action, null)
            val menuRv: RecyclerView = popupLayout.findViewById(R.id.rv_menu)
            // 设置菜单的圆角
            menuRv.setRoundRectBg(color = mColor, cornerRadius = mCornerRadius)
            // 创建菜单列表适配器
            val menuListAdapter = MenuListAdapter()
            menuRv.layoutManager = GridLayoutManager(context, 3)
            menuRv.adapter = menuListAdapter
            menuListAdapter.setData(mMenuActionList)
            menuListAdapter.setOnItemClickListener { menuItem, position ->
                pm.dismiss()
                mClickListener.invoke(menuItem, position)
            }
            menuListAdapter.setOnItemLongClickListener(mLongClickListener)
            pm.width = mWidth
            menuRv.layoutParams =
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mHeight)
            // 设置 PopupWindow 没有背景
            pm.setBackgroundDrawable(null)
            // 设置可点击外部取消 PopupWindow
            pm.isOutsideTouchable = mIsOutsideTouchable
            // 设置 PopupWindow 的内容 View
            pm.contentView = popupLayout
            return pm
        }
    }

    companion object {

        private class MenuListAdapter : RecyclerView.Adapter<MenuListViewHolder>() {

            private val mMenuActionList = arrayListOf<MenuItem>()

            private var mItemClickListener: (menuItem: MenuItem, position: Int) -> Unit =
                { _, _ -> }
            private var mItemLongClickListener: (menuItem: MenuItem, position: Int) -> Unit =
                { _, _ -> }

            fun setOnItemClickListener(listener: (menuItem: MenuItem, position: Int) -> Unit) {
                mItemClickListener = listener
            }

            fun setOnItemLongClickListener(listener: (menuItem: MenuItem, position: Int) -> Unit) {
                mItemLongClickListener = listener
            }

            @SuppressLint("NotifyDataSetChanged")
            fun setData(data: List<MenuItem>) {
                mMenuActionList.clear()
                mMenuActionList.addAll(data)
                notifyDataSetChanged()
            }

            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): MenuListViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = MenuSimpleTextItemBinding.inflate(inflater, parent, false)
                return MenuListViewHolder(binding)
            }

            override fun onBindViewHolder(holder: MenuListViewHolder, position: Int) {
                val item = mMenuActionList[position]
                val itemView = holder.itemView
                itemView.setFixOnClickListener {
                    mItemClickListener.invoke(item, position)
                }
                val tvSimpleText = holder.tvSimpleText
                tvSimpleText.text = item.title
            }

            override fun getItemCount(): Int = mMenuActionList.size
        }
    }

    private class MenuListViewHolder(binding: MenuSimpleTextItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvSimpleText = binding.tvSimpleText
    }
}