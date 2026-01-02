package cn.cqautotest.sunnybeach.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.text.parseAsHtml
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.aop.Log
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.FishPondDetailActivityBinding
import cn.cqautotest.sunnybeach.databinding.FishPondListItemBinding
import cn.cqautotest.sunnybeach.ktx.clearItemAnimator
import cn.cqautotest.sunnybeach.ktx.context
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.getLocationInWindowPoint
import cn.cqautotest.sunnybeach.ktx.ifNullOrEmpty
import cn.cqautotest.sunnybeach.ktx.setDefaultEmojiParser
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.Fish
import cn.cqautotest.sunnybeach.model.FishPondComment
import cn.cqautotest.sunnybeach.model.ReportType
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.adapter.FishPondDetailCommentListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.fragment.SubmitCommentFragment
import cn.cqautotest.sunnybeach.util.EmojiImageGetter
import cn.cqautotest.sunnybeach.util.MultiOperationHelper
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import cn.cqautotest.sunnybeach.widget.recyclerview.UniversalSpaceDecoration
import com.blankj.utilcode.util.TimeUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.dylanc.longan.intentExtras
import com.hjq.bar.TitleBar
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/07/11
 * desc   : 鱼塘详情页
 */
@AndroidEntryPoint
class FishPondDetailActivity : AppActivity(), StatusAction {

    private val mBinding by viewBinding(FishPondDetailActivityBinding::bind)
    private val mFishPondViewModel by viewModels<FishPondViewModel>()
    private val mSdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.SIMPLIFIED_CHINESE)

    @Inject
    lateinit var mMultiOperationHelper: MultiOperationHelper
    private val mAdapterDelegate = AdapterDelegate()
    private val mFishPondDetailCommendListAdapter = FishPondDetailCommentListAdapter(mAdapterDelegate)
    private val mMomentId: String by intentExtras(IntentKey.ID, "")
    private var mNickName: String = ""
    private val mSubmitCommentFragment = SubmitCommentFragment()

    override fun getLayoutId(): Int = R.layout.fish_pond_detail_activity

    override fun initView() {
        mBinding.apply {
            pagingRecyclerView.apply {
                adapter = mFishPondDetailCommendListAdapter
                layoutManager = LinearLayoutManager(context)
                addItemDecoration(UniversalSpaceDecoration(mainSpace = 1.dp, crossSpace = 0))
                // 移除 RecyclerView 的默认动画
                clearItemAnimator()
            }
            pagingRecyclerViewFloat.apply {
                adapter = mFishPondDetailCommendListAdapter
                layoutManager = LinearLayoutManager(context)
                addItemDecoration(UniversalSpaceDecoration(mainSpace = 1.dp, crossSpace = 0))
                // 移除 RecyclerView 的默认动画
                clearItemAnimator()
            }
            // 吸顶的关键（此处需要使用 pagingRecyclerView post ，否则计算出的宽高会不对）
            pagingRecyclerView.post {
                val commendListHeight = nsvContainer.height - llBottomSheet.height
                pagingRecyclerView.updateLayoutParams { height = commendListHeight }
            }
        }
    }

    override fun initData() {
        showLoading()
        loadFishDetail()
        lifecycleScope.launch {
            loadListData()
        }
    }

    private suspend fun loadListData() {
        mFishPondViewModel.fishCommendListFlow
            .flowWithLifecycle(lifecycle)
            .collectLatest {
                mBinding.pagingRecyclerView.scrollToPosition(0)
                mFishPondDetailCommendListAdapter.submitData(it)
            }
    }

    fun refreshFishPondDetailCommendList() {
        mFishPondDetailCommendListAdapter.refresh()
    }

    private fun loadFishDetail() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mFishPondViewModel.getFishDetailById(mMomentId)
                    .catch { showError { loadFishDetail() } }
                    .collectLatest {
                        mBinding.fishPondDetail.updateFishDetailUI(it)
                        mBinding.commentContainer.clReplyContainer.setFixOnClickListener { showCommentPopupFragment(mNickName) }
                        showComplete()
                    }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun FishPondListItemBinding.updateFishDetailUI(item: Fish.FishItem) {
        val userId = item.userId
        mNickName = item.nickname
        ivFishPondAvatar.setFixOnClickListener { takeIf { userId.isNotEmpty() }?.let { ViewUserActivity.start(context, userId) } }
        ivFishPondAvatar.loadAvatar(item.vip, item.avatar)
        tvFishPondNickName.setTextColor(UserManager.getNickNameColor(item.vip))
        tvFishPondNickName.text = item.nickname
        val job = item.position.ifNullOrEmpty { "滩友" }
        tvFishPondDesc.text = "$job · " + TimeUtils.getFriendlyTimeSpanByNow(item.createTime, mSdf)
        tvFishPondContent.setTextIsSelectable(false)
        tvFishPondContent.apply {
            maxLines = Int.MAX_VALUE
            ellipsize = null
        }
        val content = item.content
        // 设置默认表情符号解析器
        tvFishPondContent.setDefaultEmojiParser()
        tvFishPondContent.text = content.replace("\n", "<br>", true)
            .parseAsHtml(imageGetter = EmojiImageGetter(tvFishPondContent.textSize.toInt()))
        val topicName = item.topicName
        simpleGridLayout.setData(item.images)
            .setOnNineGridClickListener { sources, index ->
                ImagePreviewActivity.start(context, sources, index)
            }
        tvFishPondLabel.isVisible = TextUtils.isEmpty(topicName).not()
        tvFishPondLabel.text = topicName
        val linkUrl = item.linkUrl
        val hasLink = TextUtils.isEmpty(linkUrl).not()
        val hasLinkCover = TextUtils.isEmpty(item.linkCover).not()
        val linkCover = if (hasLinkCover) item.linkCover
        else R.mipmap.ic_link_default
        llLinkContainer.isVisible = hasLink
        llLinkContainer.setFixOnClickListener {
            BrowserActivity.start(context, linkUrl)
        }
        Glide.with(context)
            .load(linkCover)
            .placeholder(R.mipmap.ic_link_default)
            .error(R.mipmap.ic_link_default)
            .transform(RoundedCorners(3.dp))
            .into(ivLinkCover)
        tvLinkTitle.text = item.linkTitle
        tvLinkUrl.text = linkUrl
        val currUserId = UserManager.loadCurrUserId()
        val like = currUserId in item.thumbUpList
        val defaultColor = ContextCompat.getColor(context, R.color.menu_default_font_color)
        val likeColor = ContextCompat.getColor(context, R.color.menu_like_font_color)
        with(listMenuItem) {
            llShare.setFixOnClickListener { shareFish(item) }
            llGreat.setFixOnClickListener { dynamicLikes(item) }
            ivGreat.imageTintList = ColorStateList.valueOf(if (like) likeColor else defaultColor)
            tvComment.text = item.commentCount.takeIf { it != 0 }?.toString() ?: "评论"
            tvGreat.text = item.thumbUpList.size.takeIf { it != 0 }?.toString() ?: "点赞"
        }
    }

    private fun dynamicLikes(item: Fish.FishItem) {
        mMultiOperationHelper.dynamicLikes(this, mFishPondViewModel, item) {
            val ivGreat = mBinding.fishPondDetail.listMenuItem.ivGreat
            val likeColor = ContextCompat.getColor(this, R.color.menu_like_font_color)
            ivGreat.imageTintList = ColorStateList.valueOf(likeColor)
        }
    }

    private fun shareFish(item: Fish.FishItem) {
        mMultiOperationHelper.shareFish(item.id)
    }

    override fun initEvent() {
        mBinding.titleBar.setOnClickListener {}
        // mBinding.pagingRefreshLayout.setOnRefreshListener {
        //     // 加载摸鱼动态详情和摸鱼动态评论列表
        //     loadFishDetail()
        //     // TODO: 2021/9/13 加载摸鱼动态相关推荐列表
        //     mFishPondDetailCommendListAdapter.refresh()
        // }
        mFishPondDetailCommendListAdapter.setOnVewMoreClickListener { item, _ ->
            viewMoreDetail(item)
        }
        mFishPondDetailCommendListAdapter.setOnCommentClickListener { item, _ ->
            viewMoreDetail(item)
        }
        mBinding.commentContainer.tvFishPondSubmitComment.setFixOnClickListener { showCommentPopupFragment(mNickName) }
        mBinding.appBarLayout.addOnOffsetChangedListener { _, _ ->
            val fixedBarPoint = mBinding.llFixedBarLayout.getLocationInWindowPoint()
            val bottomSheetPoint = mBinding.llBottomSheet.getLocationInWindowPoint()
            mBinding.llFixedContainer.isInvisible = fixedBarPoint.y > bottomSheetPoint.y
        }
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

    override fun getStatusLayout(): StatusLayout = mBinding.statusLayout

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