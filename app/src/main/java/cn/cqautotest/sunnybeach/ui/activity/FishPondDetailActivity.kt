package cn.cqautotest.sunnybeach.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.aop.Log
import cn.cqautotest.sunnybeach.app.PagingActivity
import cn.cqautotest.sunnybeach.databinding.FishPondDetailActivityBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.Fish
import cn.cqautotest.sunnybeach.model.FishPondComment
import cn.cqautotest.sunnybeach.model.ReportType
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.adapter.EmptyAdapter
import cn.cqautotest.sunnybeach.ui.adapter.FishListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.FishPondDetailCommentListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.dialog.ShareDialog
import cn.cqautotest.sunnybeach.ui.fragment.SubmitCommentFragment
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_FISH_URL_PRE
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import com.blankj.utilcode.util.VibrateUtils
import com.dylanc.longan.intentExtras
import com.hjq.bar.TitleBar
import com.hjq.umeng.Platform
import com.hjq.umeng.UmengShare
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/07/11
 * desc   : 鱼塘详情页
 */
class FishPondDetailActivity : PagingActivity() {

    private val mBinding: FishPondDetailActivityBinding by viewBinding()
    private val mFishPondViewModel by viewModels<FishPondViewModel>()
    private val mAdapterDelegate = AdapterDelegate()
    private val mFishListAdapter = FishListAdapter(AdapterDelegate(), true)
    private val mFishPondDetailCommendListAdapter = FishPondDetailCommentListAdapter(mAdapterDelegate)
    private val mMomentId: String by intentExtras(IntentKey.ID, "")
    private var mNickName: String = ""
    private val mListener: (CombinedLoadStates) -> Unit = { cls ->
        when (cls.refresh) {
            is LoadState.NotLoading -> {
                mNickName = mFishListAdapter.snapshotList.firstOrNull()?.nickname.orEmpty()
                mBinding.commentContainer.clReplyContainer.setFixOnClickListener { showCommentPopupFragment(mNickName) }
            }
            else -> {}
        }
    }
    private val mSubmitCommentFragment = SubmitCommentFragment()

    override fun getPagingAdapter() = mFishListAdapter

    override fun getLayoutId(): Int = R.layout.fish_pond_detail_activity

    override fun initView() {
        super.initView()
        // This emptyAdapter is like a hacker.
        // Its existence allows the PagingAdapter to scroll to the top before being refreshed,
        // avoiding the problem that the PagingAdapter cannot return to the top after being refreshed.
        // But it needs to be used in conjunction with ConcatAdapter, and must appear before PagingAdapter.
        val emptyAdapter = EmptyAdapter()
        val concatAdapter = ConcatAdapter(emptyAdapter, mFishListAdapter, mFishPondDetailCommendListAdapter)
        mBinding.pagingRecyclerView.apply {
            adapter = concatAdapter
            addItemDecoration(SimpleLinearSpaceItemDecoration(1.dp))
        }
    }

    override fun initData() {
        super.initData()
        showLoading()
        loadFishDetail()
    }

    override suspend fun loadListData() {
        mFishPondViewModel.getFishCommendListById(mMomentId).collectLatest {
            mFishPondDetailCommendListAdapter.submitData(it)
            mBinding.pagingRecyclerView.scrollToPosition(0)
        }
    }

    fun refreshFishPondDetailCommendList() {
        mFishPondDetailCommendListAdapter.refresh()
    }

    private fun loadFishDetail() {
        lifecycleScope.launchWhenCreated {
            mFishPondViewModel.getFishDetailById(mMomentId).collectLatest { pagingData -> mFishListAdapter.submitData(pagingData) }
        }
    }

    private fun dynamicLikes(item: Fish.FishItem, position: Int) {
        val thumbUpList = item.thumbUpList
        val currUserId = UserManager.loadCurrUserId()
        if (thumbUpList.contains(currUserId)) {
            toast("请不要重复点赞")
            return
        } else {
            thumbUpList.add(currUserId)
            mFishListAdapter.notifyItemChanged(position)
        }
        VibrateUtils.vibrate(80)
        mFishPondViewModel.dynamicLikes(mMomentId).observe(this) {}
    }

    private fun shareFish(item: Fish.FishItem) {
        val momentId = item.id
        val content = UMWeb(SUNNY_BEACH_FISH_URL_PRE + momentId)
        content.title = "我分享了一条摸鱼动态，快来看看吧~"
        content.setThumb(UMImage(this, R.mipmap.launcher_ic))
        content.description = getString(R.string.app_name)
        // 分享
        ShareDialog.Builder(this)
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

    override fun initEvent() {
        super.initEvent()
        mFishListAdapter.addLoadStateListener(mListener)
        mFishListAdapter.setOnMenuItemClickListener { view, item, position ->
            when (view.id) {
                R.id.ll_share -> shareFish(item)
                R.id.ll_great -> dynamicLikes(item, position)
            }
        }
        mFishListAdapter.setOnNineGridClickListener { sources, index ->
            ImagePreviewActivity.start(this, sources.toMutableList(), index)
        }
        mBinding.pagingRefreshLayout.setOnRefreshListener {
            // 加载摸鱼动态详情和摸鱼动态评论列表
            loadFishDetail()
            // TODO: 2021/9/13 加载摸鱼动态相关推荐列表
            mFishPondDetailCommendListAdapter.refresh()
        }
        mFishPondDetailCommendListAdapter.setOnVewMoreClickListener { item, _ ->
            viewMoreDetail(item)
        }
        mFishPondDetailCommendListAdapter.setOnCommentClickListener { item, _ ->
            viewMoreDetail(item)
        }
        mBinding.commentContainer.tvFishPondSubmitComment.setFixOnClickListener { showCommentPopupFragment(mNickName) }
    }

    private fun showCommentPopupFragment(targetUserName: String) {
        val args = SubmitCommentFragment.getCommentArgs(targetUserName, mMomentId, "", "", false)
        val fragmentTag = SubmitCommentFragment.getFragmentTag(mSubmitCommentFragment)
        SubmitCommentFragment.show(this, mSubmitCommentFragment, fragmentTag, args)
    }

    private fun viewMoreDetail(item: FishPondComment.FishPondCommentItem) {
        val intent = FishCommendDetailActivity.getIntent(this, mMomentId, item)
        startActivityForResult(intent) { resultCode, _ ->
            if (resultCode == RESULT_OK) {
                mFishPondDetailCommendListAdapter.refresh()
            }
        }
    }

    override fun onRightClick(titleBar: TitleBar) {
        ReportActivity.start(this, ReportType.FISH, mMomentId)
    }

    override fun isStatusBarDarkFont() = false

    override fun onDestroy() {
        super.onDestroy()
        mFishListAdapter.removeLoadStateListener(mListener)
    }

    companion object {

        @JvmStatic
        @Log
        fun start(context: Context, momentId: String) {
            val intent = Intent(context, FishPondDetailActivity::class.java)
            intent.putExtra(IntentKey.ID, momentId)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}