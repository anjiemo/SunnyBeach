package cn.cqautotest.sunnybeach.ui.fragment

import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.databinding.EmptyFragmentBinding

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/6/18
 * desc   : 空 Fragment
 */
class EmptyFragment : AppFragment<AppActivity>() {

    private var _binding: EmptyFragmentBinding? = null
    private val mBinding get() = _binding!!

    override fun getLayoutId(): Int = R.layout.empty_fragment

    override fun onBindingView() {
        _binding = EmptyFragmentBinding.bind(view)
    }

    override fun initView() {

    }

    override fun initData() {

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}