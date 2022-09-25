package cn.cqautotest.sunnybeach.util

import android.app.Activity
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.updatePadding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import com.blankj.utilcode.util.ScreenUtils
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.SidePattern
import com.lzf.easyfloat.utils.DisplayUtils

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/09/22
 * desc   : 悬浮窗助手
 */
object FloatWindowHelper {

    /**
     * 附加到指定 Activity，action 为按钮点击的回调
     */
    fun attachTo(activity: Activity, action: (View) -> Unit) {
        EasyFloat.with(activity)
            .setLayout(ImageView(activity).apply {
                layoutParams = ViewGroup.LayoutParams(58.dp, 59.dp)
                updatePadding(4.dp, 4.dp, 4.dp, 4.dp)
                setImageResource(R.mipmap.ic_publish_content)
                setFixOnClickListener { action.invoke(it) }
            })
            // 设置浮窗的标签，用于区分多个浮窗
            .setTag(activity::class.java.simpleName)
            // 设置吸附方式，共15种模式，详情参考SidePattern
            .setSidePattern(SidePattern.RESULT_HORIZONTAL)
            // 设置浮窗固定坐标，ps：设置固定坐标，Gravity属性和offset属性将无效
            .setLocation(ScreenUtils.getAppScreenWidth() - 58.dp, ScreenUtils.getAppScreenHeight() / 4 * 3)
            // 设置当布局大小变化后，整体view的位置对齐方式
            .setLayoutChangedGravity(Gravity.END)
            // 设置系统浮窗的有效显示高度（不包含虚拟导航栏的高度），基本用不到，除非有虚拟导航栏适配问题
            .setDisplayHeight { context ->
                val homeNavBar = activity.layoutInflater.inflate(R.layout.home_navigation_item, null)
                homeNavBar.measure(0, 0)
                val homeNavBarHeight = homeNavBar.measuredHeight
                // 减去首页底部导航菜单的高度，避免悬浮窗显示到底部导航菜单上方
                DisplayUtils.rejectedNavHeight(context) - homeNavBarHeight
            }
            .show()
    }

    /**
     * 显示来自 Activity 的悬浮窗
     */
    fun showFrom(activity: Activity) {
        EasyFloat.show(activity::class.java.simpleName)
    }

    /**
     * 隐藏来自 Activity 的悬浮窗
     */
    fun hideFrom(activity: Activity) {
        EasyFloat.hide(activity::class.java.simpleName)
    }

    /**
     * 从 Activity 中分离
     */
    fun deathFrom(activity: Activity) {
        EasyFloat.dismiss(activity::class.java.simpleName, true)
    }
}