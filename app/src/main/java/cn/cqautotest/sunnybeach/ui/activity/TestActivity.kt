package cn.cqautotest.sunnybeach.ui.activity

import android.os.Build
import android.text.StaticLayout
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.Init
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.TestActivityBinding
import dev.androidbroadcast.vbpd.viewBinding
import timber.log.Timber

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/11/16
 *    desc   : 用于测试的界面
 */
class TestActivity : AppActivity(), Init {

    private val mBinding by viewBinding(TestActivityBinding::bind)

    override fun getLayoutId(): Int = R.layout.test_activity

    override fun initView() {

    }

    override fun initData() {
        mBinding.apply {
            tvMsg.post { calculate() }
            tvMsg.text = MSG
        }
    }

    private fun calculate() {
        val width = mBinding.tvMsg.width
        val paint = mBinding.tvMsg.paint
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val staticLayout = StaticLayout.Builder.obtain(MSG, 0, MSG.length, paint, width)
                .build()
            val lineCount = staticLayout.lineCount
            Timber.d("calculate：===> lineCount is $lineCount")
        }
    }

    companion object {

        private const val MSG =
            "一般情况下，TextView的行数要等到其布局完成后才能获取到，否则如果直接调用textView.getLineCount()函数获取到的结果只会为0，那能不能提前获取到TextView的行数呢，当然是肯定的。TextView内部的换行是通过一个StaticLayout的类来处理的，而且我们调用的getLineCount()方法最后也是调用的StaticLayout类中的getLineCount()方法，所以我们只需要创建一个和TextView内部一样的StaticLayout就可以了，然后调用staticLayout.getLineCount()方法就可以获取到和当前TextView行数一样的值了，但要注意一个前提条件就是保证TextView的width是已知的，否则也无法获取到正确的行数。"
    }
}