package cn.cqautotest.sunnybeach.ui.activity

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.Init
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.TestKeyboardActivityBinding
import cn.cqautotest.sunnybeach.other.DeferringInsetsAnimationCallback
import com.dylanc.longan.activity
import com.dylanc.longan.rootWindowInsetsCompat
import com.dylanc.longan.windowInsetsControllerCompat
import com.gyf.immersionbar.ImmersionBar
import timber.log.Timber

class TestKeyboardActivity : AppActivity(), Init {

    private val mBinding by viewBinding<TestKeyboardActivityBinding>()
    private var mKeyboardVisible = false

    override fun getLayoutId() = R.layout.test_keyboard_activity

    override fun initView() {

    }

    override fun initData() {

    }

    override fun initEvent() {
        mBinding.apply {
            val view = tvEmoji
            view.post {
                val statusBarHeight = ImmersionBar.getStatusBarHeight(activity)
                val navigationBarHeight = ImmersionBar.getNavigationBarHeight(activity)
                Timber.d("initEvent：===> view y is ${view.y}")
                Timber.d("initEvent：===> statusBarHeight is $statusBarHeight")
                Timber.d("initEvent：===> navigationBarHeight is $navigationBarHeight")
            }
            val callback = DeferringInsetsAnimationCallback(view)
            keyboardLayout.setKeyboardListener { isActive, keyboardHeight ->
                mKeyboardVisible = isActive
                Timber.d("initEvent：===> keyboardHeight is $keyboardHeight")
                if (isActive) {
                    llCommentContainer.updateLayoutParams {
                        height = keyboardHeight
                    }
                    ivEmoji.setImageResource(R.mipmap.ic_emoji_normal)
                } else {
                    ivEmoji.setImageResource(R.mipmap.ic_keyboard)
                }
            }
            etInputContent.setOnClickListener {
                // ivEmoji.setImageResource(R.mipmap.ic_emoji_normal)
            }
            ivEmoji.setOnClickListener {
                val keyBoardVisible = etInputContent.rootWindowInsetsCompat?.isVisible(WindowInsetsCompat.Type.ime()) ?: false
                if (keyBoardVisible) {
                    etInputContent.windowInsetsControllerCompat?.hide(WindowInsetsCompat.Type.ime())
                } else {
                    etInputContent.windowInsetsControllerCompat?.show(WindowInsetsCompat.Type.ime())
                }
            }
            ViewCompat.setWindowInsetsAnimationCallback(etInputContent, callback)
        }
    }
}