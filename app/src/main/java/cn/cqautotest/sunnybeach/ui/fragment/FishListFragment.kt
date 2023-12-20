package cn.cqautotest.sunnybeach.ui.fragment

import android.app.Activity
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.paging.PagingData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.aop.Permissions
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.TitleBarFragment
import cn.cqautotest.sunnybeach.databinding.FishListFragmentBinding
import cn.cqautotest.sunnybeach.ktx.addAfterNextUpdateUIDefaultItemAnimator
import cn.cqautotest.sunnybeach.ktx.clearItemAnimator
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.ifLogin
import cn.cqautotest.sunnybeach.ktx.isNotEmpty
import cn.cqautotest.sunnybeach.ktx.loadStateListener
import cn.cqautotest.sunnybeach.ktx.orEmpty
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
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel
import cn.cqautotest.sunnybeach.viewmodel.app.AppViewModel
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import cn.cqautotest.sunnybeach.widget.recyclerview.SimpleLinearSpaceItemDecoration
import com.dylanc.longan.viewLifecycleScope
import com.hjq.bar.TitleBar
import com.hjq.permissions.Permission
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
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

    private val mBinding: FishListFragmentBinding by viewBinding()

    @Inject
    lateinit var mAppViewModel: AppViewModel

    @Inject
    lateinit var mMultiOperationHelper: MultiOperationHelper

    private val mFishPondViewModel by activityViewModels<FishPondViewModel>()
    private val mUserViewModel by activityViewModels<UserViewModel>()
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
    private val mFishCategoryAdapterDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            mBinding.tvRecommend.isVisible = mFishCategoryAdapter.isNotEmpty()
        }
    }
    private val mScanLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val resultData = result.data
        if (result.resultCode != Activity.RESULT_OK || resultData == null) return@registerForActivityResult
        resultData.getParcelableExtra<HmsScan>(ScanUtil.RESULT)?.let { hmsScan ->
            Timber.d("mScanLauncher：===> hmsScan originalValue is ${hmsScan.originalValue}")
            val (canBeParse, userId) = canBeParse(hmsScan)
            if (canBeParse) {
                ViewUserActivity.start(requireContext(), userId)
            } else {
                ScanResultActivity.start(requireContext(), hmsScan)
            }
        } ?: showNoContentTips()
    }

    override fun getLayoutId(): Int = R.layout.fish_list_fragment

    override fun initView() {
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
        viewLifecycleScope.launchWhenCreated {
            mFishPondViewModel.getFishListByCategoryId("recommend").collectLatest {
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
            refreshLayout.setOnRefreshListener {
                loadCategoryList()
                mFishListAdapter.refresh()
            }
            ivPublish.setFixOnClickListener {
                viewLifecycleScope.launch {
                    // 操作按钮点击回调，判断是否已经登录过账号
                    ifLogin {
                        startActivityForResult(PutFishActivity::class.java) { resultCode, _ ->
                            if (resultCode == AppCompatActivity.RESULT_OK) {
                                mFishPondViewModel.refreshFishList()
                            }
                        }
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

    private fun dynamicLikes(item: Fish.FishItem, position: Int) {
        mMultiOperationHelper.dynamicLikes(viewLifecycleOwner, mFishPondViewModel, mFishListAdapter, item, position)
    }

    private fun shareFish(item: Fish.FishItem) {
        mMultiOperationHelper.shareFish(item.id)
    }

    override fun initObserver() {
        mAppViewModel.mourningCalendarListLiveData.observe(viewLifecycleOwner) { setMourningStyleByDate(it) }
        mFishPondViewModel.fishListStateLiveData.observe(viewLifecycleOwner) { mFishListAdapter.refresh() }
        mFishCategoryAdapter.registerAdapterDataObserver(mFishCategoryAdapterDataObserver)
    }

    private fun setMourningStyleByDate(mourningCalendarList: List<MourningCalendar>) {
        val sdf = SimpleDateFormat("MM月dd日", Locale.getDefault())
        val formatDate = sdf.format(System.currentTimeMillis())
        val rootView = requireView()
        mourningCalendarList.find { it.date == formatDate }?.let { rootView.setMourningStyle() }
            ?: rootView.removeMourningStyle()
    }

    @Permissions(Permission.CAMERA)
    override fun onRightClick(titleBar: TitleBar) {
        // “QRCODE_SCAN_TYPE”和“DATAMATRIX_SCAN_TYPE”表示只扫描QR和Data Matrix的码
        val options = HmsScanAnalyzerOptions.Creator()
            .setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE)
            .create()
        MyScanUtil.startScan(this, options, mScanLauncher)
    }

    private fun canBeParse(hmsScan: HmsScan?): Pair<Boolean, String> {
        val content = hmsScan?.getShowResult() ?: return false to ""
        // content can never be null.
        val uri = Uri.parse(content)
        val scheme = uri.scheme.orEmpty()
        val authority = uri.authority.orEmpty()
        val userId = uri.lastPathSegment.orEmpty()
        Timber.d("showResult：===> scheme is $scheme authority is $authority userId is $userId")
        Timber.d("showResult：===> content is $content")
        return when {
            !mUserViewModel.checkScheme(uri.scheme.orEmpty()) -> false to ""
            !mUserViewModel.checkAuthority(uri.authority.orEmpty()) -> false to ""
            !mUserViewModel.checkUserId(userId) -> false to ""
            else -> true to userId
        }
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
        mFishListAdapter.removeLoadStateListener(loadStateListener)
        mFishCategoryAdapter.unregisterAdapterDataObserver(mFishCategoryAdapterDataObserver)
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