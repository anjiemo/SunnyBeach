package cn.cqautotest.sunnybeach.ui.fragment

import android.graphics.Color
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.databinding.EmptyFragmentBinding
import cn.cqautotest.sunnybeach.ktx.setDefaultEmojiParser
import cn.cqautotest.sunnybeach.ktx.simpleToast
import cn.cqautotest.sunnybeach.util.EmojiMapHelper

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/06/18
 * desc   : 空 Fragment
 */
class EmptyFragment : AppFragment<AppActivity>() {

    private val mBinding: EmptyFragmentBinding by viewBinding()

    override fun getLayoutId(): Int = R.layout.empty_fragment

    override fun initView() {
        mBinding.emptyDescription.setOnItemClickListener { menuItem, position ->
            simpleToast("我是${menuItem.title}操作菜单，index：$position")
        }
        mBinding.ivAvatarDecorView.loadAvatar(resource = R.drawable.login_qq_ic, vip = true)
    }

    override fun initData() {
        mBinding.emptyDescription.setDefaultEmojiParser()
        val sb = buildString {
            EmojiMapHelper.emojiMap.keys.onEach {
                append(it)
            }
        }
        mBinding.emptyDescription.text = sb
    }

    override fun initEvent() {
        val keyboardView = mBinding.numberKeyboardView.keyboardView
        val keyboard = Keyboard(requireContext(), R.xml.number_keyboard_layout)
        keyboardView.keyboard = keyboard
        val keys = keyboard.keys
        keys.forEachIndexed { index, key ->
            if (index == keys.lastIndex) {
                val icon = key.icon
                icon.setTint(Color.BLACK)
                // val bitmap = ImageUtils.drawable2Bitmap(icon)
                // val newBitmap = ImageUtils.scale(
                //     bitmap,
                //     (icon.minimumWidth * 1.5f).toInt(),
                //     (icon.minimumHeight * 1.5f).toInt()
                // )
                // key.icon = newBitmap.toDrawable(resources)
            }
        }
        keyboardView.setOnKeyboardActionListener(object : KeyboardView.OnKeyboardActionListener {
            override fun onPress(primaryCode: Int) {
                // 键盘按下的时候
            }

            override fun onRelease(primaryCode: Int) {
                // 键盘释放的时候
            }

            override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
                // 点击了某一个键
            }

            override fun onText(text: CharSequence?) {

            }

            // 下面几个是在键盘上面滑动的监听
            override fun swipeLeft() {

            }

            override fun swipeRight() {

            }

            override fun swipeDown() {

            }

            override fun swipeUp() {

            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = EmptyFragment()
    }
}