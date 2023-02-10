package cn.cqautotest.sunnybeach.ui.activity

import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.AboutActivityBinding
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_GITHUB_URL
import com.blankj.utilcode.util.ClipboardUtils

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2018/10/18
 *    desc   : 关于界面
 */
class AboutActivity : AppActivity() {

    private val mBinding by viewBinding(AboutActivityBinding::bind)

    override fun getLayoutId(): Int = R.layout.about_activity

    override fun initView() {}

    override fun initData() {}

    override fun initEvent() {
        mBinding.tvCopyLink.setFixOnClickListener {
            ClipboardUtils.copyText(SUNNY_BEACH_GITHUB_URL)
            toast("已复制项目地址到剪贴板")
        }
    }
}