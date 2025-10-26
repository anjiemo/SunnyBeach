package cn.cqautotest.sunnybeach.ui.fragment

import android.os.Bundle
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.databinding.TwoLevelContentFragmentBinding
import cn.cqautotest.sunnybeach.event.LiveBusKeyConfig
import cn.cqautotest.sunnybeach.event.LiveBusUtils
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import dev.androidbroadcast.vbpd.viewBinding

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2025/10/26
 * desc   : 二楼内容 Fragment
 */
class TwoLevelContentFragment : AppFragment<AppActivity>() {

    private val mBinding by viewBinding(TwoLevelContentFragmentBinding::bind)

    override fun getLayoutId(): Int = R.layout.two_level_content_fragment

    override fun initView() {

    }

    override fun initData() {

    }

    override fun initEvent() {
        mBinding.tvBackToHomePage.setFixOnClickListener {
            LiveBusUtils.busSend(LiveBusKeyConfig.BUS_TWO_LEVEL_BACK_TO_HOME_PAGE)
        }
    }

    companion object {

        fun newInstance(): TwoLevelContentFragment {
            val args = Bundle()
            val fragment = TwoLevelContentFragment()
            fragment.arguments = args
            return fragment
        }
    }
}