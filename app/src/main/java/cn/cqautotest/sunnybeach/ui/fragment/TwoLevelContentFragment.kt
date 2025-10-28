package cn.cqautotest.sunnybeach.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.databinding.TwoLevelContentFragmentBinding
import cn.cqautotest.sunnybeach.event.LiveBusKeyConfig
import cn.cqautotest.sunnybeach.event.LiveBusUtils
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.viewmodel.HomeViewModel
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2025/10/26
 * desc   : 二楼内容 Fragment
 */
class TwoLevelContentFragment : AppFragment<AppActivity>() {

    private val mBinding by viewBinding(TwoLevelContentFragmentBinding::bind)
    private val mHomeViewModel by activityViewModels<HomeViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = activity ?: return
        val fragmentOwner = this
        // 使用此方法替代 onActivityCreated()
        val lifecycleObserver = object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                owner.lifecycle.removeObserver(this)
                activity.onBackPressedDispatcher.addCallback(fragmentOwner, object : OnBackPressedCallback(true) {

                    override fun handleOnBackPressed() {
                        LiveBusUtils.busSend(LiveBusKeyConfig.BUS_HOME_PAGE_TWO_LEVEL_PAGE_STATE, false)
                    }
                })
            }
        }
        activity.lifecycle.addObserver(lifecycleObserver)
    }

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

    override fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            mHomeViewModel.bottomNavigationHeightFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.CREATED)
                .collectLatest {
                    mBinding.tvBackToHomePage.updateLayoutParams {
                        height = it + 10.dp
                    }
                }
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