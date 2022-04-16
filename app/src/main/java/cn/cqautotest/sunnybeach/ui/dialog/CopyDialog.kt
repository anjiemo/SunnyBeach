package cn.cqautotest.sunnybeach.ui.dialog

import android.content.Context
import android.view.Gravity
import cn.cqautotest.sunnybeach.R
import com.hjq.base.BaseDialog
import com.hjq.base.action.AnimAction

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2018/10/18
 *    desc   : 可进行拷贝的副本
 */
class CopyDialog {

    class Builder(context: Context) : BaseDialog.Builder<Builder>(context) {

        init {
            setContentView(R.layout.copy_dialog)
            setAnimStyle(AnimAction.ANIM_BOTTOM)
            setGravity(Gravity.BOTTOM)
        }
    }
}