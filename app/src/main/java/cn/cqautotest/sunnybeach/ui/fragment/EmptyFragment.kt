package cn.cqautotest.sunnybeach.ui.fragment

import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.databinding.EmptyFragmentBinding
import dev.androidbroadcast.vbpd.viewBinding

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/06/18
 * desc   : 空 Fragment
 */
class EmptyFragment : AppFragment<AppActivity>() {

    private val mBinding by viewBinding(EmptyFragmentBinding::bind)

    override fun getLayoutId(): Int = R.layout.empty_fragment

    override fun initView() {

    }

    override fun initData() {

    }

    override fun initEvent() {

    }

    companion object {
        @JvmStatic
        fun newInstance() = EmptyFragment()
    }
}