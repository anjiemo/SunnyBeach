package cn.cqautotest.sunnybeach.ui.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.paging.PagingData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.FloatWindowAction
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.aop.Permissions
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.TitleBarFragment
import cn.cqautotest.sunnybeach.databinding.FishListFragmentBinding
import cn.cqautotest.sunnybeach.ktx.*
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.Fish
import cn.cqautotest.sunnybeach.model.MourningCalendar
import cn.cqautotest.sunnybeach.model.RefreshStatus
import cn.cqautotest.sunnybeach.ui.activity.*
import cn.cqautotest.sunnybeach.ui.adapter.EmptyAdapter
import cn.cqautotest.sunnybeach.ui.adapter.FishCategoryAdapter
import cn.cqautotest.sunnybeach.ui.adapter.FishListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.RecommendFishTopicListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.dialog.ShareDialog
import cn.cqautotest.sunnybeach.util.*
import cn.cqautotest.sunnybeach.viewmodel.app.AppViewModel
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import com.blankj.utilcode.util.VibrateUtils
import com.dylanc.longan.viewLifecycleScope
import com.hjq.bar.TitleBar
import com.hjq.permissions.Permission
import com.hjq.umeng.Platform
import com.hjq.umeng.UmengShare
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/07/07
 * desc   : 摸鱼动态列表 Fragment
 */
@AndroidEntryPoint
class FishListFragment : TitleBarFragment<AppActivity>(), StatusAction, OnBack2TopListener, FloatWindowAction {

    private val mBinding: FishListFragmentBinding by viewBinding()

    @Inject
    lateinit var mAppViewModel: AppViewModel

    private val mFishPondViewModel by activityViewModels<FishPondViewModel>()
    private val mRefreshStatus = RefreshStatus()
    private val mRecommendFishTopicListAdapterDelegate = AdapterDelegate()
    private val mFishListAdapterDelegate = AdapterDelegate()
    private val mFishCategoryAdapter = FishCategoryAdapter(mRecommendFishTopicListAdapterDelegate)
    private val mRecommendFishTopicListAdapter = RecommendFishTopicListAdapter(mFishCategoryAdapter)
    private val mFishListAdapter = FishListAdapter(mFishListAdapterDelegate)
    private val loadStateListener = loadStateListener(mFishListAdapter) { mBinding.refreshLayout.finishRefresh() }
    private val mFishCategoryAdapterDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            mBinding.tvRecommend.isVisible = mFishCategoryAdapter.isNotEmpty()
        }
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
            }
        }
        requireActivity().attachFloatWindow {
            viewLifecycleScope.launchWhenCreated {
                // 操作按钮点击回调，判断是否已经登录过账号
                ifLogin {
                    startActivityForResult(PutFishActivity::class.java) { resultCode, _ ->
                        if (resultCode == Activity.RESULT_OK) {
                            mFishPondViewModel.refreshFishList()
                        }
                    }
                } otherwise {
                    requireActivity().tryShowLoginDialog()
                }
            }
        }
    }

    override fun initData() {
        loadCategoryList()
        loadFishList()
        mAppViewModel.getMourningCalendar().observe(viewLifecycleOwner) {
            val result = it.getOrNull() ?: return@observe
            setMourningStyleByDate(result)
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
        }
        // 需要在 View 销毁的时候移除 listener
        mFishListAdapter.addLoadStateListener(loadStateListener)
        mRecommendFishTopicListAdapterDelegate.setOnItemClickListener { _, position ->
            mFishCategoryAdapter.snapshotList[position]?.let { FishTopicActivity.start(requireContext(), it) }
        }
        mFishListAdapterDelegate.setOnItemClickListener { _, position ->
            mFishListAdapter.snapshotList[position]?.let { FishPondDetailActivity.start(requireContext(), it.id) }
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
        val thumbUpList = item.thumbUpList
        val currUserId = UserManager.loadCurrUserId()
        takeIf { thumbUpList.contains(currUserId) }?.let { toast("请不要重复点赞") }?.also { return }
        thumbUpList.add(currUserId)
        mFishListAdapter.notifyItemChanged(position)
        VibrateUtils.vibrate(80)
        mFishPondViewModel.dynamicLikes(item.id).observe(viewLifecycleOwner) {}
    }

    private fun shareFish(item: Fish.FishItem) {
        val momentId = item.id
        val content = UMWeb(SUNNY_BEACH_FISH_URL_PRE + momentId)
        content.title = "我分享了一条摸鱼动态，快来看看吧~"
        content.setThumb(UMImage(requireContext(), R.mipmap.launcher_ic))
        content.description = getString(R.string.app_name)
        // 分享
        ShareDialog.Builder(requireActivity())
            .setShareLink(content)
            .setListener(object : UmengShare.OnShareListener {
                override fun onSucceed(platform: Platform?) {
                    toast("分享成功")
                }

                override fun onError(platform: Platform?, t: Throwable) {
                    toast(t.message)
                }

                override fun onCancel(platform: Platform?) {
                    toast("分享取消")
                }
            })
            .show()
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
        MyScanUtil.startScan(requireActivity(), REQUEST_CODE_SCAN_ONE, options)
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

    override fun onResume() {
        super.onResume()
        activity?.showFloatWindow()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mFishListAdapter.removeLoadStateListener(loadStateListener)
        mFishCategoryAdapter.unregisterAdapterDataObserver(mFishCategoryAdapterDataObserver)
        activity?.deathFloatWindow()
    }

    override fun onBack2Top() {
        mBinding.rvFishPondList.scrollToPosition(0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data == null) return
        if (requestCode == REQUEST_CODE_SCAN_ONE) {
            // 导入图片扫描返回结果
            val hmsScan = data.getParcelableExtra(ScanUtil.RESULT) as HmsScan?
            if (hmsScan != null) {
                // 展示解码结果
                showResult(hmsScan)
            } else {
                showNoContentTips()
            }
        }
    }

    private fun showNoContentTips() {
        toast("什么内容也没有~")
    }

    private fun showResult(hmsScan: HmsScan?) {
        val result = hmsScan?.showResult.orEmpty()
        if (result.isBlank()) {
            showNoContentTips()
            return
        }

        // result can never be null.
        val uri = Uri.parse(result)
        val scheme = uri.scheme.orEmpty()
        val authority = uri.authority.orEmpty()
        val userId = uri.lastPathSegment.orEmpty()

        Timber.d("showResult：===> scheme is $scheme authority is $authority userId is $userId")
        Timber.d("showResult：===> result is $result")
        // toast(userId)

        when {
            checkScheme(scheme).not() -> unsupportedParsedContent()
            checkAuthority(authority).not() -> unsupportedParsedContent()
            checkUserId(userId).not() -> unsupportedParsedContent()
            else -> ViewUserActivity.start(requireContext(), userId)
        }
    }

    private fun unsupportedParsedContent() = toast("不支持解析的内容")

    /**
     * We only support http and https protocols.
     */
    private fun checkScheme(scheme: String) = scheme == "http" || scheme == "https"

    private fun checkAuthority(authority: String): Boolean {
        val sobSiteTopDomain = StringUtil.getTopDomain(SUNNY_BEACH_SITE_BASE_URL)
        val loveSiteTopDomain = StringUtil.getTopDomain(I_LOVE_ANDROID_SITE_BASE_URL)

        Timber.d("checkAuthority：===> authority is $authority")
        Timber.d("checkAuthority：===> sobSiteTopDomain is $sobSiteTopDomain")
        Timber.d("checkAuthority：===> loveSiteTopDomain is $loveSiteTopDomain")

        fun String.delete3W() = replace("www.", "")
        val sobAuthority = authority.delete3W() == sobSiteTopDomain
        val loveAuthority = authority.delete3W() == loveSiteTopDomain
        return sobAuthority || loveAuthority
    }

    /**
     * Sob site userId is long type, we need check.
     */
    private fun checkUserId(userId: String) = userId.isNotBlank() && userId.toLongOrNull() != null

    companion object {

        private val REQUEST_CODE_SCAN_ONE = hashCode()

        @JvmStatic
        fun newInstance() = FishListFragment()
    }
}