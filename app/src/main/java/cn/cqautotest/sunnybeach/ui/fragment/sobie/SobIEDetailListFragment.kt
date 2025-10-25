package cn.cqautotest.sunnybeach.ui.fragment.sobie

import androidx.fragment.app.viewModels
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.databinding.SobIeDeatilListFragmentBinding
import cn.cqautotest.sunnybeach.ktx.simpleToast
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel
import dev.androidbroadcast.vbpd.viewBinding
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/07/24
 * desc   : sob 币收支详情列表 Fragment
 */
open class SobIEDetailListFragment : AppFragment<AppActivity>() {

    private val mBinding by viewBinding(SobIeDeatilListFragmentBinding::bind)
    private val mUserViewModel by viewModels<UserViewModel>()

    override fun getLayoutId() = R.layout.sob_ie_deatil_list_fragment

    override fun initView() {}

    override fun initData() {
        mUserViewModel.getMeSobIEDetailList(1).observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Timber.d("initData：===> $it")
            }.onFailure {
                simpleToast("获取失败 ${it.message}")
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(): SobIEDetailListFragment = SobIEDetailListFragment()
    }
}