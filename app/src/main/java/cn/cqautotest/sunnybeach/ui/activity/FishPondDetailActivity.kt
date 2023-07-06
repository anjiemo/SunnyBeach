package cn.cqautotest.sunnybeach.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.text.parseAsHtml
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.aop.Log
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.FishPondDetailActivityBinding
import cn.cqautotest.sunnybeach.databinding.FishPondListItemBinding
import cn.cqautotest.sunnybeach.ktx.*
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
import cn.cqautotest.sunnybeach.widget.recyclerview.SimpleLinearSpaceItemDecoration
import com.blankj.utilcode.util.TimeUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.dylanc.longan.intentExtras
import com.hjq.bar.TitleBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/07/11
 * desc   : 鱼塘详情页
 */
@AndroidEntryPoint
class FishPondDetailActivity : AppActivity(), StatusAction {

    private val mBinding: FishPondDetailActivityBinding by viewBinding()
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
                addItemDecoration(SimpleLinearSpaceItemDecoration(1.dp))
                // 移除 RecyclerView 的默认动画
                itemAnimator = null
            }
            pagingRecyclerViewFloat.apply {
                adapter = mFishPondDetailCommendListAdapter
                layoutManager = LinearLayoutManager(context)
                addItemDecoration(SimpleLinearSpaceItemDecoration(1.dp))
                // 移除 RecyclerView 的默认动画
                itemAnimator = null
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
        lifecycleScope.launch { repeatOnLifecycle(Lifecycle.State.CREATED) { loadListData() } }
    }

    private suspend fun loadListData() {
        mFishPondViewModel.getFishCommendListById(mMomentId).collectLatest {
            mBinding.pagingRecyclerView.scrollToPosition(0)
            mFishPondDetailCommendListAdapter.submitData(it)
        }
    }

    fun refreshFishPondDetailCommendList() {
        mFishPondDetailCommendListAdapter.refresh()
    }

    private fun loadFishDetail() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
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
        tvFishPondContent.text = content.parseAsHtml(imageGetter = EmojiImageGetter(tvFishPondContent.textSize.toInt()))
        val topicName = item.topicName
        val images = item.images
        val imageCount = images.size
        llPhotoContainer.removeAllViews()
        images.forEachIndexed { index, imageUrl ->
            val imageView = ImageView(context).apply {
                layoutParams = MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    updateMargins(top = 6.dp, bottom = 6.dp)
                }
            }
            llPhotoContainer.addView(imageView)
            imageView.setFixOnClickListener { ImagePreviewActivity.start(context, images, index) }
            Glide.with(context)
                .load(imageUrl)
                .centerCrop()
                .transform(RoundedCorners(6.dp))
                .addListener(object : RequestListener<Drawable> {
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        resource ?: return false
                        val newDrawable = adjustDrawable(resource, llPhotoContainer.width, 10.dp)
                        imageView.setImageDrawable(newDrawable)
                        return true
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return true
                    }
                })
                .into(imageView)
        }
        simpleGridLayout.isVisible = imageCount != 0
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

    fun adjustDrawable(drawable: Drawable, width: Int, radius: Int): Drawable {
        val height = (drawable.intrinsicHeight * 1.618).toInt().coerceAtMost(width)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)
        val roundedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val roundedCanvas = Canvas(roundedBitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        roundedCanvas.drawRoundRect(rectF, radius.toFloat(), radius.toFloat(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        roundedCanvas.drawBitmap(bitmap, rect, rect, paint)
        return BitmapDrawable(resources, roundedBitmap)
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