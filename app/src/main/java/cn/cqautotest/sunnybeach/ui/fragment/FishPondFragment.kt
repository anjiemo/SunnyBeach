package cn.cqautotest.sunnybeach.ui.fragment

import android.view.View
import androidx.collection.arrayMapOf
import androidx.fragment.app.viewModels
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.app.FragmentAdapter
import cn.cqautotest.sunnybeach.app.TitleBarFragment
import cn.cqautotest.sunnybeach.databinding.FishPondFragmentBinding
import cn.cqautotest.sunnybeach.model.FishPondTopicIndex
import cn.cqautotest.sunnybeach.ui.activity.FishPondSettingActivity
import cn.cqautotest.sunnybeach.ui.fragment.fishpond.FishPondListFragment
import cn.cqautotest.sunnybeach.utils.logByDebug
import cn.cqautotest.sunnybeach.utils.onPageSelected
import cn.cqautotest.sunnybeach.utils.onTabSelected
import cn.cqautotest.sunnybeach.utils.startActivity
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/7/7
 * desc   : 摸鱼列表管理 Fragment
 */
class FishPondFragment : TitleBarFragment<AppActivity>(), StatusAction {

    private var _binding: FishPondFragmentBinding? = null
    private val mBinding get() = _binding!!
    private val fishPondViewModel by viewModels<FishPondViewModel>()
    private val mFragmentMap by lazy { arrayMapOf<Int, AppFragment<AppActivity>>() }
    private lateinit var mFragmentAdapter: FragmentAdapter

    override fun getLayoutId(): Int = R.layout.fish_pond_fragment

    override fun onBindingView() {
        _binding = FishPondFragmentBinding.bind(view)
    }

    override fun initView() {
        showEmpty()
        mFragmentAdapter = FragmentAdapter(this)
        mBinding.vp2FishPond.apply {
            adapter = mFragmentAdapter
        }
    }

    override fun initData() {
        fishPondViewModel.loadTopicListByIndex()
        showLoading()
    }

    override fun initEvent() {
        val tabLayoutTopicList = mBinding.tabLayoutTopicList
        val vp2FishPond = mBinding.vp2FishPond
        tabLayoutTopicList.onTabSelected {
            val tab = it ?: return@onTabSelected
            val position = tab.position
            showLoading()
            vp2FishPond.setCurrentItem(position, false)
            if (mFragmentMap[position] == null) {
                showEmpty()
            } else {
                showComplete()
            }
        }
        vp2FishPond.onPageSelected { position ->
            val tab = tabLayoutTopicList.getTabAt(position)
            tabLayoutTopicList.selectTab(tab)
        }
    }

    override fun initObserver() {
        fishPondViewModel.fishPondTopicIndex.observe(viewLifecycleOwner) { fishPondTopicIndex ->
            logByDebug(msg = "initObserver：===> fishPondTopicIndex is $fishPondTopicIndex")
            if (fishPondTopicIndex.isNullOrEmpty()) {
                showEmpty()
                return@observe
            }
            setupTopicLabels(fishPondTopicIndex)
        }
    }

    private fun setupTopicLabels(fishPondTopicIndex: FishPondTopicIndex) {
        val tabLayoutTopicList = mBinding.tabLayoutTopicList
        // 显示分类标签
        tabLayoutTopicList.visibility = View.VISIBLE
        // 重置数据
        tabLayoutTopicList.removeAllTabs()
        mFragmentMap.clear()
        tabLayoutTopicList.newTab().apply {
            text = "推荐"
            tabLayoutTopicList.addTab(this)
        }
        mFragmentMap[0] = FishPondListFragment().apply {
            title = "推荐"
            topicId = "recommend"
        }
        tabLayoutTopicList.newTab().apply {
            text = "关注"
            tabLayoutTopicList.addTab(this)
        }
        mFragmentMap[1] = FishPondListFragment().apply {
            title = "关注"
            topicId = "follow"
        }
        // 动态创建 Tab 标签
        fishPondTopicIndex.forEachIndexed { index, topicIndex ->
            val tab = tabLayoutTopicList.newTab()
            tab.text = topicIndex.topicName
            tabLayoutTopicList.addTab(tab)
            tab.view.setOnLongClickListener {
                startActivity<FishPondSettingActivity>()
                true
            }
            mFragmentMap[index + 2] = FishPondListFragment().apply {
                title = topicIndex.topicName
                topicId = topicIndex.id
                logByDebug(msg = "setupTopicLabels：===> topicName is ${topicIndex.topicName} topicId is $topicId")
            }
        }
        mFragmentAdapter.setFragmentMap(mFragmentMap)
        showComplete()
    }

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlFishPondHint

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}