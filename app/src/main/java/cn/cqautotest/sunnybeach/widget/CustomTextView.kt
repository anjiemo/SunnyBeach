package cn.cqautotest.sunnybeach.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.ActionMode
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.appcompat.widget.AppCompatTextView
import cn.cqautotest.sunnybeach.util.dp

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/09
 * desc   : 自定义 TextView 实现长按选中弹出菜单
 */
class CustomTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs), ActionMode.Callback {

    private var mClickListener: (menuItem: MenuItem, position: Int) -> Unit = { _, _ -> }
    private var mLongClickListener: (menuItem: MenuItem, position: Int) -> Unit = { _, _ -> }

    init {
        customSelectionActionModeCallback = this
    }

    @SuppressLint("RestrictedApi")
    private val mMenuList = arrayListOf<MenuItem>().apply {
        add(ActionMenuItem(context, Menu.NONE, 1234, 0, 0, "全选"))
        add(ActionMenuItem(context, Menu.NONE, 1234, 0, 1, "复制"))
        add(ActionMenuItem(context, Menu.NONE, 1234, 0, 2, "粘贴"))
        add(ActionMenuItem(context, Menu.NONE, 1234, 0, 3, "转发"))
        add(ActionMenuItem(context, Menu.NONE, 1234, 0, 4, "打开"))
    }

    override fun showContextMenu(x: Float, y: Float): Boolean {
        // createContextMenuAndShow()
        return true
    }

    override fun showContextMenu(): Boolean {
        // createContextMenuAndShow()
        return true
    }

    fun setOnItemClickListener(block: (menuItem: MenuItem, position: Int) -> Unit) {
        mClickListener = block
    }

    fun setOnItemLongClickListener(block: (menuItem: MenuItem, position: Int) -> Unit) {
        mLongClickListener = block
    }

    private fun createContextMenuAndShow() {
        val containerHeight = 90.dp
        FakeWeChatPopupMenu.Builder(context)
            .menuActionList(mMenuList)
            .setOnItemClickListener(mClickListener)
            .setOnItemLongClickListener(mLongClickListener)
            .create()
            .showAsDropDown(this, 0, -(containerHeight), Gravity.TOP)
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {

    }
}