package cn.cqautotest.sunnybeach.ui.fragment

import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.TitleBarFragment
import cn.cqautotest.sunnybeach.contract.GetScanContent
import cn.cqautotest.sunnybeach.databinding.FishListFragmentBinding
import cn.cqautotest.sunnybeach.event.LiveBusKeyConfig
import cn.cqautotest.sunnybeach.event.LiveBusUtils
import cn.cqautotest.sunnybeach.ktx.addAfterNextUpdateUIDefaultItemAnimator
import cn.cqautotest.sunnybeach.ktx.clearItemAnimator
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.ifLogin
import cn.cqautotest.sunnybeach.ktx.loadStateListener
import cn.cqautotest.sunnybeach.ktx.otherwise
import cn.cqautotest.sunnybeach.ktx.removeMourningStyle
import cn.cqautotest.sunnybeach.ktx.setDoubleClickListener
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.ktx.setMourningStyle
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.ktx.tryShowLoginDialog
import cn.cqautotest.sunnybeach.model.Fish
import cn.cqautotest.sunnybeach.model.MourningCalendar
import cn.cqautotest.sunnybeach.model.RefreshStatus
import cn.cqautotest.sunnybeach.model.scan.CanParseUserId
import cn.cqautotest.sunnybeach.model.scan.CancelScan
import cn.cqautotest.sunnybeach.model.scan.Content
import cn.cqautotest.sunnybeach.model.scan.NoContent
import cn.cqautotest.sunnybeach.ui.activity.FishPondDetailActivity
import cn.cqautotest.sunnybeach.ui.activity.FishTopicActivity
import cn.cqautotest.sunnybeach.ui.activity.ImagePreviewActivity
import cn.cqautotest.sunnybeach.ui.activity.PutFishActivity
import cn.cqautotest.sunnybeach.ui.activity.ScanResultActivity
import cn.cqautotest.sunnybeach.ui.activity.ViewUserActivity
import cn.cqautotest.sunnybeach.ui.adapter.EmptyAdapter
import cn.cqautotest.sunnybeach.ui.adapter.FishCategoryAdapter
import cn.cqautotest.sunnybeach.ui.adapter.FishListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.RecommendFishTopicListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.util.MultiOperationHelper
import cn.cqautotest.sunnybeach.util.MyScanUtil
import cn.cqautotest.sunnybeach.viewmodel.HomeViewModel
import cn.cqautotest.sunnybeach.viewmodel.app.AppViewModel
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import cn.cqautotest.sunnybeach.widget.recyclerview.SimpleLinearSpaceItemDecoration
import com.dylanc.longan.startActivity
import com.dylanc.longan.viewLifecycleScope
import com.hjq.bar.TitleBar
import com.hjq.permissions.permission.PermissionNames
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.simple.SimpleMultiListener
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/07/07
 * desc   : 摸鱼动态列表 Fragment
 */
@AndroidEntryPoint
class FishListFragment : TitleBarFragment<AppActivity>(), StatusAction, OnBack2TopListener {

    private val mBinding by viewBinding(FishListFragmentBinding::bind)

    private val mHomeViewModel by activityViewModels<HomeViewModel>()

    @Inject
    lateinit var mAppViewModel: AppViewModel

    @Inject
    lateinit var mMultiOperationHelper: MultiOperationHelper

    private val mFishPondViewModel by activityViewModels<FishPondViewModel>()
    private val mRefreshStatus = RefreshStatus()
    private val mRecommendFishTopicListAdapterDelegate = AdapterDelegate()
    private val mFishListAdapterDelegate = AdapterDelegate()
    private val mFishCategoryAdapter = FishCategoryAdapter(mRecommendFishTopicListAdapterDelegate)
    private val mRecommendFishTopicListAdapter = RecommendFishTopicListAdapter(mFishCategoryAdapter)
    private val mFishListAdapter = FishListAdapter(mFishListAdapterDelegate)
    private val loadStateListener = loadStateListener(mFishListAdapter) {
        mBinding.refreshLayout.finishRefresh()
        mBinding.rvFishPondList.addAfterNextUpdateUIDefaultItemAnimator()
    }
    private val mScanLauncher = registerForActivityResult(GetScanContent()) { result ->
        when (result) {
            is CanParseUserId -> {
                ViewUserActivity.start(requireContext(), result.userId)
            }

            is Content -> {
                ScanResultActivity.start(requireContext(), result.hmsScan)
            }

            is NoContent -> {
                showNoContentTips()
            }

            is CancelScan -> {
                // Nothing to do.
            }
        }
    }
    private val mOnLayoutChangedListener = View.OnLayoutChangeListener { _, _, top, _, bottom, _, _, _, _ ->
        val newHeight = bottom - top
        mFishPondViewModel.updateFishPondTitleBarHeight(height = newHeight)
    }

    override fun getLayoutId(): Int = R.layout.fish_list_fragment

    override fun initView() {
        val fragment = childFragmentManager.findFragmentById(R.id.fragment_container_view_tag)
        takeIf { fragment == null }?.let {
            childFragmentManager.beginTransaction()
                .add(R.id.fragment_container_view_tag, TwoLevelContentFragment.newInstance())
                .commitAllowingStateLoss()
        }
        // fix: 修复在应用配置（如深色模式切换）发生变化时 expandableLayout 的显示状态错误问题
        mBinding.expandableLayout.collapse(false)
        mBinding.expandableLayout.expand()
        mBinding.twoLevelHeader.setEnablePullToCloseTwoLevel(false)

        // This emptyAdapter is like a hacker.
        // Its existence allows the PagingAdapter to scroll to the top before being refreshed,
        // avoiding the problem that the PagingAdapter cannot return to the top after being refreshed.
        // But it needs to be used in conjunction with ConcatAdapter, and must appear before PagingAdapter.
        val emptyAdapter = EmptyAdapter()
        val concatAdapter = ConcatAdapter(emptyAdapter, mRecommendFishTopicListAdapter, mFishListAdapter)
        mBinding.apply {
            rvFishPondList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = concatAdapter
                addItemDecoration(SimpleLinearSpaceItemDecoration(6.dp))
                clearItemAnimator()
            }
        }
    }

    override fun initData() {
        loadCategoryList()
        loadFishList()
        mAppViewModel.getMourningCalendar().observe(viewLifecycleOwner) { mourningCalendarList ->
            mourningCalendarList.getOrNull()?.let { setMourningStyleByDate(it) }
        }
    }

    private fun loadCategoryList() {
        mFishPondViewModel.loadTopicList().observe(viewLifecycleOwner) {
            mFishCategoryAdapter.submitData(viewLifecycleOwner.lifecycle, PagingData.from(it.getOrNull().orEmpty()))
        }
    }

    private fun loadFishList() {
        viewLifecycleScope.launch {
            mFishPondViewModel.fishListFlow.collectLatest {
                onBack2Top()
                mFishListAdapter.submitData(it)
            }
        }
    }

    override fun initEvent() {
        mBinding.apply {
            titleBar.setDoubleClickListener {
                onBack2Top()
            }
            titleBar.addOnLayoutChangeListener(mOnLayoutChangedListener)
            mBinding.expandableLayout.setOnExpansionUpdateListener { expansionFraction, state ->
                titleBar.alpha = expansionFraction
            }
            refreshLayout.setOnMultiListener(object : SimpleMultiListener() {

                override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {
                    when (newState) {
                        RefreshState.TwoLevelReleased -> {
                            onTwoLevelPageOpened()
                        }

                        RefreshState.TwoLevelFinish -> {
                            onTwoLevelPageClosed()
                        }

                        else -> {
                            // Nothing to do.
                        }
                    }
                }
            })
            refreshLayout.setOnRefreshListener {
                loadCategoryList()
                mFishListAdapter.refresh()
            }
            refreshLayout.setOnLoadMoreListener {
                mFishListAdapter.retry()
            }
            mFishListAdapter.addLoadStateListener { states ->
                when (states.append) {
                    !is LoadState.Loading -> {
                        refreshLayout.finishLoadMore()
                    }

                    else -> {
                        // Nothing to do.
                    }
                }
            }
            ivPublish.setFixOnClickListener {
                viewLifecycleScope.launch {
                    // 操作按钮点击回调，判断是否已经登录过账号
                    ifLogin {
                        startActivity<PutFishActivity>()
                    } otherwise {
                        requireActivity().tryShowLoginDialog()
                    }
                }
            }
        }
        // 需要在 View 销毁的时候移除 listener
        mFishListAdapter.addLoadStateListener(loadStateListener)
        mRecommendFishTopicListAdapterDelegate.setOnItemClickListener { _, position ->
            mFishCategoryAdapter.snapshotList.getOrNull(position)?.let { FishTopicActivity.start(requireContext(), it) }
        }
        mFishListAdapterDelegate.setOnItemClickListener { _, position ->
            mFishListAdapter.snapshotList.getOrNull(position)?.let { FishPondDetailActivity.start(requireContext(), it.id) }
        }
        mFishListAdapter.setOnMenuItemClickListener { view, item, position ->
            when (view.id) {
                R.id.ll_share -> shareFish(item)
                R.id.ll_great -> dynamicLikes(item, position)
            }
        }
        mFishListAdapter.setOnNineGridClickListener { sources, index ->
            ImagePreviewActivity.start(requireContext(), sources.toMutableList(), index)
        }
    }

    private fun onTwoLevelPageOpened() {
        mHomeViewModel.setTwoLevelPageShowing(true)
        mBinding.expandableLayout.collapse()
        val topHeight = mFishPondViewModel.fishPondTitleBarHeightFlow.value
        val bottomHeight = mHomeViewModel.bottomNavigationHeightFlow.value
        mBinding.llFishPondListContainer.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = topHeight + bottomHeight
        }
        LiveBusUtils.busSend(LiveBusKeyConfig.BUS_HOME_PAGE_TWO_LEVEL_PAGE_STATE, true)
        hidePublishButton()
        toast("打开二楼")
    }

    private fun onTwoLevelPageClosed() {
        mHomeViewModel.setTwoLevelPageShowing(false)
        mBinding.expandableLayout.expand()
        mBinding.llFishPondListContainer.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = 0
        }
        LiveBusUtils.busSend(LiveBusKeyConfig.BUS_HOME_PAGE_TWO_LEVEL_PAGE_STATE, false)
        showPublishButton()
        toast("关闭二楼")
    }

    private fun showPublishButton() {
        mBinding.ivPublish.show()
    }

    private fun hidePublishButton() {
        mBinding.ivPublish.hide()
    }

    private fun dynamicLikes(item: Fish.FishItem, position: Int) {
        mMultiOperationHelper.dynamicLikes(viewLifecycleOwner, mFishPondViewModel, mFishListAdapter, item, position)
    }

    private fun shareFish(item: Fish.FishItem) {
        mMultiOperationHelper.shareFish(item.id)
    }

    override fun initObserver() {
        mAppViewModel.mourningCalendarListLiveData.observe(viewLifecycleOwner) { setMourningStyleByDate(it) }
        mFishPondViewModel.fishListStateLiveData.observe(viewLifecycleOwner) { mFishListAdapter.refresh() }
        LiveBusUtils.busReceive<Unit>(viewLifecycleOwner, LiveBusKeyConfig.BUS_PUT_FISH_SUCCESS) {
            mFishListAdapter.refresh()
        }
        LiveBusUtils.busReceive<Unit>(viewLifecycleOwner, LiveBusKeyConfig.BUS_TWO_LEVEL_BACK_TO_HOME_PAGE) {
            mBinding.twoLevelHeader.finishTwoLevel()
        }
    }

    private fun setMourningStyleByDate(mourningCalendarList: List<MourningCalendar>) {
        val sdf = SimpleDateFormat("MM月dd日", Locale.getDefault())
        val formatDate = sdf.format(System.currentTimeMillis())
        val rootView = requireView()
        mourningCalendarList.find { it.date == formatDate }?.let { rootView.setMourningStyle() }
            ?: rootView.removeMourningStyle()
    }

    @com.flyjingfish.android_aop_core.annotations.Permission(PermissionNames.CAMERA)
    override fun onRightClick(titleBar: TitleBar) {
        // “QRCODE_SCAN_TYPE”和“DATAMATRIX_SCAN_TYPE”表示只扫描QR和Data Matrix的码
        val options = HmsScanAnalyzerOptions.Creator()
            .setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE)
            .create()
        MyScanUtil.startScan(mScanLauncher, options)
    }

    override fun showLoading(id: Int) {
        takeIf { mRefreshStatus.isFirstRefresh }?.let { super.showLoading(id) }
        mRefreshStatus.isFirstRefresh = false
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlFishPondHint

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding.titleBar.removeOnLayoutChangeListener(mOnLayoutChangedListener)
        mFishListAdapter.removeLoadStateListener(loadStateListener)
    }

    override fun onBack2Top() {
        mBinding.rvFishPondList.scrollToPosition(0)
    }

    private fun showNoContentTips() {
        toast("什么内容也没有~")
    }

    companion object {

        @JvmStatic
        fun newInstance() = FishListFragment()
    }
}