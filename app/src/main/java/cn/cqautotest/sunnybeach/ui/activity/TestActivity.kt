package cn.cqautotest.sunnybeach.ui.activity

import android.os.Bundle
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.android52.sunnybeach.skin.activity.SupportSkinActivity
import cn.android52.sunnybeach.skin.callback.ISkinChangingCallback
import cn.android52.sunnybeach.skin.manager.SkinManager
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.Init
import cn.cqautotest.sunnybeach.aop.Permissions
import cn.cqautotest.sunnybeach.databinding.TestActivityBinding
import com.hjq.permissions.Permission
import timber.log.Timber

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/11/16
 *    desc   : 用于测试的界面
 */
class TestActivity : SupportSkinActivity(), Init, ISkinChangingCallback {

    private val mBinding by viewBinding<TestActivityBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("pre onCreate：===>")
        super.onCreate(savedInstanceState)
        Timber.d("after onCreate：===>")
        setContentView(getLayoutId())
        initView()
        initData()
        initEvent()
        initObserver()
    }

    private fun getLayoutId(): Int {
        return R.layout.test_activity
    }

    private fun initView() {}

    private fun initData() {}

    override fun initEvent() {
        mBinding.btnChange.setOnClickListener {
            skinChange()
        }
        mBinding.btnReset.setOnClickListener {
            val manager = SkinManager.instance
            manager.resetSkin()
        }
    }

    @Permissions(Permission.MANAGE_EXTERNAL_STORAGE)
    private fun skinChange() {
        val manager = SkinManager.instance
        manager.changeSkin(lifecycle, this)
    }

    override fun onSkinChanged() {
        Timber.d("onSkinChanged：===> skin change...")
    }

    override fun onStartChangeSkin() {
        Timber.d("onStartChangeSkin：===> skin start change...")
    }

    override fun onChangeSkinError(e: Exception) {
        Timber.d("onChangeSkinError：===> skin error...")
    }

    override fun onChangeSkinComplete() {
        Timber.d("onChangeSkinComplete：===> skin complete...")
    }
}