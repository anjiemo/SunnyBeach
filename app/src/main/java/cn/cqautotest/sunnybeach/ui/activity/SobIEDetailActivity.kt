package cn.cqautotest.sunnybeach.ui.activity

import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.SobIeDeatilActivityBinding
import cn.cqautotest.sunnybeach.ktx.simpleToast
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/07/17
 * desc   : 用户 Sob 币的收支（Income & Expenditures）明细界面
 */
class SobIEDetailActivity : AppActivity() {

    private val mBinding by viewBinding<SobIeDeatilActivityBinding>()
    private val mUserViewModel by viewModels<UserViewModel>()

    override fun getLayoutId() = R.layout.sob_ie_deatil_activity

    override fun initView() {

    }

    override fun initData() {
        mUserViewModel.getMeSobIEDetailList(1).observe(this) { result ->
            result.onSuccess {
                Timber.d("initData：===> $it")
            }.onFailure {
                simpleToast("获取失败 ${it.message}")
            }
        }
    }

    override fun initEvent() {

    }
}