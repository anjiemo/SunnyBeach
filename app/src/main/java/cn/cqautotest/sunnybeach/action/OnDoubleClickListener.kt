package cn.cqautotest.sunnybeach.action

import android.view.View

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/14
 * desc   : 双击监听器
 */
interface OnDoubleClickListener {

    fun onDoubleClick(v: View, position: Int)
}