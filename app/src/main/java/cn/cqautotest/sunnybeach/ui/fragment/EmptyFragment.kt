package cn.cqautotest.sunnybeach.ui.fragment

import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppFragment

class EmptyFragment: AppFragment<AppActivity>() {

    override fun getLayoutId(): Int  = R.layout.empty_fragment

    override fun initView() {

    }

    override fun initData() {

    }

    companion object {
        @JvmStatic
        fun newInstance(): EmptyFragment {
            return EmptyFragment()
        }
    }
}