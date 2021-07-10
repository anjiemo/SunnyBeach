package cn.cqautotest.sunnybeach.ui.fragment

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.TitleBarFragment
import cn.cqautotest.sunnybeach.databinding.FishPondFragmentBinding
import cn.cqautotest.sunnybeach.model.FishPondTopicIndex
import cn.cqautotest.sunnybeach.ui.activity.FishPondSettingActivity
import cn.cqautotest.sunnybeach.ui.adapter.FishPondAdapter
import cn.cqautotest.sunnybeach.utils.logByDebug
import cn.cqautotest.sunnybeach.utils.startActivity
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/7/7
 * desc   : 摸鱼列表 Fragment
 */
class FishPondFragment : TitleBarFragment<AppActivity>(), StatusAction {

    private var _binding: FishPondFragmentBinding? = null
    private val mBinding get() = _binding!!
    private val fishPondViewModel by viewModels<FishPondViewModel>()
    private val mFishPondAdapter = FishPondAdapter()

    override fun getLayoutId(): Int = R.layout.fish_pond_fragment

    override fun onBindingView() {
        _binding = FishPondFragmentBinding.bind(view)
    }

    override fun initView() {
        mBinding.rvFishPondList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mFishPondAdapter
        }
    }

    override fun initData() {
        fishPondViewModel.loadTopicListByIndex()
        showLoading()
    }

    override fun initObserver() {
        fishPondViewModel.fishPondTopicIndex.observe(viewLifecycleOwner) { fishPondTopicIndex ->
            logByDebug(msg = "initObserver：===> fishPondTopicIndex is $fishPondTopicIndex")
            if (fishPondTopicIndex.isNullOrEmpty()) {
                showEmpty()
                return@observe
            }
            setupTopicLabels(fishPondTopicIndex)
            showComplete()
        }
    }

    private fun setupTopicLabels(fishPondTopicIndex: FishPondTopicIndex) {
        val tabLayoutTopicList = mBinding.tabLayoutTopicList
        tabLayoutTopicList.removeAllTabs()
        for (topicIndex in fishPondTopicIndex) {
            val tab = tabLayoutTopicList.newTab()
            tab.text = topicIndex.topicName
            tabLayoutTopicList.addTab(tab)
            tab.view.setOnLongClickListener {
                startActivity<FishPondSettingActivity>()
                true
            }
        }
    }

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlFishPondHint
}