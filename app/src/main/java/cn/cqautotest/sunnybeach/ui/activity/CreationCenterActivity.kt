package cn.cqautotest.sunnybeach.ui.activity

import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.CreationCenterActivityBinding
import timber.log.Timber

class CreationCenterActivity : AppActivity() {

    private val mBinding by viewBinding(CreationCenterActivityBinding::bind)

    override fun getLayoutId(): Int = R.layout.creation_center_activity

    override fun initView() {
        mBinding.refreshLayout.apply {

        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        mBinding.llHeaderView.setOnClickListener {
            toast("headerView click...")
            Timber.d("initEvent：===> headerView is click...")
        }
        mBinding.scrollView.setOnTouchListener(null)
    }
}