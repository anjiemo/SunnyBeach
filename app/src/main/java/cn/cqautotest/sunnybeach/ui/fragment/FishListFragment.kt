package cn.cqautotest.sunnybeach.ui.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.content.getSystemService
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
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
import cn.cqautotest.sunnybeach.ktx.*
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.Fish
import cn.cqautotest.sunnybeach.model.MourningCalendar
import cn.cqautotest.sunnybeach.ui.activity.FishPondDetailActivity
import cn.cqautotest.sunnybeach.ui.activity.ImagePreviewActivity
import cn.cqautotest.sunnybeach.ui.activity.PutFishActivity
import cn.cqautotest.sunnybeach.ui.activity.ViewUserActivity
import cn.cqautotest.sunnybeach.ui.adapter.EmptyAdapter
import cn.cqautotest.sunnybeach.ui.adapter.FishListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.dialog.ShareDialog
import cn.cqautotest.sunnybeach.util.*
import cn.cqautotest.sunnybeach.viewmodel.app.AppViewModel
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
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
class FishListFragment : TitleBarFragment<AppActivity>(), StatusAction, OnBack2TopListener {

    private val mBinding: FishListFragmentBinding by viewBinding()

    @Inject
    lateinit var mAppViewModel: AppViewModel
    private val mFishPondViewModel by activityViewModels<FishPondViewModel>()
    private val mAdapterDelegate = AdapterDelegate()
    private val mFishListAdapter = FishListAdapter(mAdapterDelegate)
    private val loadStateListener = loadStateListener(mFishListAdapter) { mBinding.refreshLayout.finishRefresh() }

    override fun getLayoutId(): Int = R.layout.fish_list_fragment

    override fun initView() {
        // This emptyAdapter is like a hacker.
        // Its existence allows the PagingAdapter to scroll to the top before being refreshed,
        // avoiding the problem that the PagingAdapter cannot return to the top after being refreshed.
        // But it needs to be used in conjunction with ConcatAdapter, and must appear before PagingAdapter.
        val emptyAdapter = EmptyAdapter()
        val concatAdapter = ConcatAdapter(emptyAdapter, mFishListAdapter)
        mBinding.rvFishPondList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = concatAdapter
            addItemDecoration(SimpleLinearSpaceItemDecoration(6.dp))
        }
    }

    override fun initData() {
        loadFishList()
        mAppViewModel.getMourningCalendar().observe(viewLifecycleOwner) {
            val result = it.getOrNull() ?: return@observe
            setMourningStyleByDate(result)
        }
    }

    private fun loadFishList() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            mFishPondViewModel.getFishListByCategoryId("recommend").collectLatest {
                onBack2Top()
                mFishListAdapter.submitData(it)
            }
        }
    }

    override fun initEvent() {
        val ivPublishContent = mBinding.ivPublishContent
        mBinding.titleBar.setDoubleClickListener {
            onBack2Top()
        }
        mBinding.refreshLayout.setOnRefreshListener {
            mFishListAdapter.refresh()
        }
        // 需要在 View 销毁的时候移除 listener
        mFishListAdapter.addLoadStateListener(loadStateListener)
        mAdapterDelegate.setOnItemClickListener { _, position ->
            mFishListAdapter.snapshotList[position]?.let { FishPondDetailActivity.start(requireContext(), it.id) }
        }
        mFishListAdapter.setOnMenuItemClickListener { view, item, position ->
            when (view.id) {
                R.id.ll_share -> shareFish(item)
                R.id.ll_great -> dynamicLikes(item, position)
            }
        }
        ivPublishContent.setFixOnClickListener {
            takeIfLogin {
                startActivityForResult(PutFishActivity::class.java) { resultCode, _ ->
                    if (resultCode == Activity.RESULT_OK) {
                        mFishListAdapter.refresh()
                    }
                }
            }
        }
        mFishListAdapter.setOnNineGridClickListener { sources, index ->
            ImagePreviewActivity.start(requireContext(), sources.toMutableList(), index)
        }
        mBinding.rvFishPondList.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            /**
             * 当前是否显示
             */
            private var mIsShowing = true

            /**
             * 当前是否向上滑动
             */
            private var mIsUp = false

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    // RecyclerView 当前未滚动
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        // 如果手指是向下滑动且当前没有显示 --> 显示悬浮按钮
                        if (mIsUp.not() && mIsShowing.not()) {
                            ivPublishContent.show()
                            mIsShowing = true
                        }
                    }
                    // 1、RecyclerView 当前正被外部输入（例如用户触摸输入）拖动
                    // 2、RecyclerView 当前正在动画到最终位置，而不受外部控制
                    RecyclerView.SCROLL_STATE_DRAGGING, RecyclerView.SCROLL_STATE_SETTLING -> {
                        // 如果当前已经显示了悬浮按钮 --> 隐藏
                        if (mIsShowing) {
                            ivPublishContent.hide()
                        }
                        mIsShowing = false
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // Timber.d("onScrolled：===> dy is $dy")
                mIsUp = dy > 0
            }
        })
    }

    private fun dynamicLikes(item: Fish.FishItem, position: Int) {
        val thumbUpList = item.thumbUpList
        val currUserId = UserManager.loadCurrUserId()
        takeIf { thumbUpList.contains(currUserId) }?.let { toast("请不要重复点赞") }?.also { return }
        thumbUpList.add(currUserId)
        mFishListAdapter.notifyItemChanged(position)
        tryVibrate()
        mFishPondViewModel.dynamicLikes(item.id).observe(viewLifecycleOwner) {}
    }

    private fun tryVibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().getSystemService<Vibrator>()?.let { vibrator ->
                if (vibrator.hasVibrator()) {
                    val ve = VibrationEffect.createOneShot(
                        80,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                    vibrator.vibrate(ve)
                }
            }
        }
    }

    private fun shareFish(item: Fish.FishItem) {
        val momentId = item.id
        val content = UMWeb(SUNNY_BEACH_FISH_URL_PRE + momentId)
        content.title = "我发布了一条摸鱼动态，快来看看吧~"
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
    }

    private fun setMourningStyleByDate(mourningCalendarList: List<MourningCalendar>) {
        val sdf = SimpleDateFormat("MM月dd日", Locale.getDefault())
        val formatDate = sdf.format(System.currentTimeMillis())
        val rootView = requireView()
        mourningCalendarList.find { it.date == formatDate }?.let { rootView.setMourningStyle() } ?: rootView.removeMourningStyle()
    }

    @Permissions(Permission.CAMERA)
    override fun onRightClick(titleBar: TitleBar) {
        // “QRCODE_SCAN_TYPE”和“DATAMATRIX_SCAN_TYPE”表示只扫描QR和Data Matrix的码
        val options = HmsScanAnalyzerOptions.Creator()
            .setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE)
            .create()
        MyScanUtil.startScan(requireActivity(), REQUEST_CODE_SCAN_ONE, options)
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlFishPondHint

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mFishListAdapter.removeLoadStateListener(loadStateListener)
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
        val result = hmsScan?.showResult ?: ""
        if (result.isBlank()) {
            showNoContentTips()
            return
        }

        // result can never be null.
        val uri = Uri.parse(result)
        val scheme = uri.scheme ?: ""
        val authority = uri.authority ?: ""
        val userId = uri.lastPathSegment ?: ""

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