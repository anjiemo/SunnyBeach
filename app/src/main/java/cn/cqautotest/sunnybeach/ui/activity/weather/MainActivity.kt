package cn.cqautotest.sunnybeach.ui.activity.weather

import android.content.Intent
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.updatePadding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.ktx.dp
import com.gyf.immersionbar.ImmersionBar

class MainActivity : AppActivity() {

    override fun getLayoutId() = R.layout.main_activity

    override fun initActivity() {
        super.initActivity()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {
                overridePendingTransition(R.anim.left_in_activity, R.anim.left_out_activity)
            }
        })
    }

    override fun initView() {
        val actionBarHeight = ImmersionBar.getActionBarHeight(this)
        val statusBarHeight = ImmersionBar.getStatusBarHeight(this)
        val paddingTop = actionBarHeight - statusBarHeight + 10.dp
        findViewById<View>(R.id.actionBarLayout)?.updatePadding(0, paddingTop, 0, 10.dp)
    }

    override fun initData() {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        overridePendingTransition(R.anim.left_in_activity, R.anim.left_out_activity)
    }
}