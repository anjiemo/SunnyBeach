package cn.cqautotest.sunnybeach.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.LevelListDrawable
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Html
import android.text.TextUtils
import android.view.GestureDetector
import android.view.ViewConfiguration
import androidx.activity.viewModels
import androidx.core.content.getSystemService
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.aop.DebugLog
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.FishPondDetailActivityBinding
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.other.KeyboardWatcher
import cn.cqautotest.sunnybeach.ui.adapter.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.FishPondDetailCommentListAdapter
import cn.cqautotest.sunnybeach.ui.fragment.SubmitCommentFragment
import cn.cqautotest.sunnybeach.util.*
import cn.cqautotest.sunnybeach.viewmodel.KeyboardViewModel
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import cn.cqautotest.sunnybeach.widget.SimpleGridLayout
import cn.cqautotest.sunnybeach.widget.StatusLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.coroutines.flow.collectLatest


/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/07/11
 * desc   : 鱼塘详情页
 */
class FishPondDetailActivity : AppActivity(), StatusAction, Html.ImageGetter,
    SimpleGesture.OnSlideListener, SimpleGridLayout.OnNineGridClickListener,
    KeyboardWatcher.SoftKeyboardStateListener {

    private val mBinding: FishPondDetailActivityBinding by viewBinding()
    private val mFishPondViewModel by viewModels<FishPondViewModel>()
    private val mKeyboardViewModel by viewModels<KeyboardViewModel>()
    private val mAdapterDelegate = AdapterDelegate()
    private val mFishPondDetailCommendListAdapter =
        FishPondDetailCommentListAdapter(mAdapterDelegate)
    private var mMomentId: String = ""
    private var mUserId: String = ""
    private var mNickName: String = ""

    override fun getLayoutId(): Int = R.layout.fish_pond_detail_activity

    override fun initView() {
        mBinding.rvFishPondDetailComment.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mFishPondDetailCommendListAdapter
            addItemDecoration(SimpleLinearSpaceItemDecoration(1.dp))
        }
        postDelayed({
            KeyboardWatcher.with(this)
                .setListener(this)
        }, 500)
    }

    override fun onSoftKeyboardOpened(keyboardHeight: Int) {
        mKeyboardViewModel.showKeyboard()
        mKeyboardViewModel.setKeyboardHeight(keyboardHeight)
    }

    override fun onSoftKeyboardClosed() {
        mKeyboardViewModel.hideKeyboard()
    }

    private fun safeShowFragment(targetUserName: String) {
        val args = SubmitCommentFragment.getCommentArgs(
            targetUserName,
            mMomentId,
            "",
            "",
            false
        )
        val dialogFragment = SubmitCommentFragment()
        dialogFragment.arguments = args
        dialogFragment.show(supportFragmentManager, dialogFragment.tag)
    }

    override fun initData() {
        showLoading()
        mMomentId = intent.getStringExtra(IntentKey.ID) ?: ""
        loadFishDetail()
        lifecycleScope.launchWhenCreated {
            mFishPondViewModel.getFishCommendListById(mMomentId).collectLatest {
                mFishPondDetailCommendListAdapter.submitData(it)
            }
        }
    }

    fun refreshFishPondDetailCommendList() {
        mFishPondDetailCommendListAdapter.refresh()
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun loadFishDetail() {
        mFishPondViewModel.getFishDetailById(mMomentId).observe(this) {
            mBinding.slFishDetailRefresh.finishRefresh()
            val item = it.getOrElse {
                showError {
                    initData()
                }
                return@observe
            }
            mNickName = item.nickname
            showComplete()
            val fishPond = mBinding.fishPond
            val flAvatarContainer = fishPond.flAvatarContainer
            val ivAvatar = fishPond.ivFishPondAvatar
            val tvNickname = fishPond.tvFishPondNickName
            val tvDesc = fishPond.tvFishPondDesc
            val tvContent = fishPond.tvFishPondContent
            val rrlContainer = fishPond.rrlContainer
            val simpleGridLayout = fishPond.simpleGridLayout
            val tvLabel = fishPond.tvFishPondLabel
            val llFishItemContainer = fishPond.llFishItemContainer
            val llLinkContainer = fishPond.llLinkContainer
            val ivLinkCover = fishPond.ivLinkCover
            val tvLinkTitle = fishPond.tvLinkTitle
            val tvLinkUrl = fishPond.tvLinkUrl
            val tvComment = fishPond.listMenuItem.tvComment
            val llGreat = fishPond.listMenuItem.llGreat
            val tvGreat = fishPond.listMenuItem.tvGreat
            val clReplyContainer = mBinding.commentContainer.clReplyContainer
            llFishItemContainer.setRoundRectBg(color = Color.WHITE, cornerRadius = 10.dp)
            flAvatarContainer.background = UserManager.getAvatarPendant(item.vip)
            Glide.with(this)
                .load(item.avatar)
                .placeholder(R.mipmap.ic_default_avatar)
                .error(R.mipmap.ic_default_avatar)
                .circleCrop()
                .into(ivAvatar)
            tvNickname.setTextColor(UserManager.getNickNameColor(item.vip))
            tvNickname.text = item.nickname
            tvDesc.text = "${item.position} · " +
                    DateHelper.getFriendlyTimeSpanByNow("${item.createTime}:00")
            val content = HtmlCompat.fromHtml(
                item.content
                    .replace("<br>", "\n")
                    .replace("</br>", "\n"),
                HtmlCompat.FROM_HTML_MODE_LEGACY, this,
                null
            )
            tvContent.maxLines = Int.MAX_VALUE
            tvContent.text = content
            tvContent.setOnCreateContextMenuListener { _, _, _ ->

            }
            mUserId = item.userId
            val topicName = item.topicName
            val images = item.images
            val imageCount = images.size
            rrlContainer.isVisible = images.isNotEmpty()
            simpleGridLayout.setSpanCount(
                when (imageCount) {
                    // 规避 0 ，避免导致：IllegalArgumentException，Span count should be at least 1. Provided 0.
                    in 1..3 -> imageCount
                    4 -> 2
                    else -> 3
                }
            ).setOnNineGridClickListener(this)
                .setData(images)
            simpleGridLayout.isVisible = imageCount != 0
            tvLabel.isVisible = TextUtils.isEmpty(topicName).not()
            tvLabel.text = topicName
            val linkUrl = item.linkUrl
            val hasLink = TextUtils.isEmpty(linkUrl).not()
            val hasLinkCover = TextUtils.isEmpty(item.linkCover).not()
            val linkCover = if (hasLinkCover) item.linkCover
            else R.mipmap.ic_link_default
            llLinkContainer.setRoundRectBg(color = Color.parseColor("#F5F5F8"), cornerRadius = 4.dp)
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
            tvComment.text = with(item.commentCount) {
                if (this == 0) {
                    "评论"
                } else {
                    toString()
                }
            }
            llGreat.setFixOnClickListener {
                dynamicLikes()
            }
            tvGreat.text = with(item.thumbUpCount) {
                if (this == 0) {
                    "点赞"
                } else {
                    toString()
                }
            }
            // 如果要使用 GestureDetector 手势检测器，则必须禁用点击事件，否则无法检测手势
            clReplyContainer.setOnClickListener(null)
            val minDistance = ViewConfiguration.get(this).scaledTouchSlop
            val sg = SimpleGesture(minDistance, this)
            val gestureDetector = GestureDetector(this, sg)
            clReplyContainer.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }
        }
    }

    @SuppressLint("MissingPermission")
    private fun dynamicLikes() {
        lifecycleScope.launchWhenCreated {
            mFishPondViewModel.dynamicLikes(mMomentId)
                .observe(this@FishPondDetailActivity) { _ ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        getSystemService<Vibrator>()?.let { vibrator ->
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
        }
    }

    override fun onNineGridClick(sources: List<String>, index: Int) {
        ImagePreviewActivity.start(this, sources, index)
    }

    override fun onSwipeUp() {
        goToPostComment(mNickName)
    }

    override fun initEvent() {
        mBinding.fishPond.llUserInfoContainer.setFixOnClickListener {
            if (TextUtils.isEmpty(mUserId)) {
                return@setFixOnClickListener
            }
            ViewUserActivity.start(this, mUserId)
        }
        mBinding.slFishDetailRefresh.setOnRefreshListener {
            // 加载摸鱼动态详情和摸鱼动态评论列表
            loadFishDetail()
            // TODO: 2021/9/13 加载摸鱼动态相关推荐列表
            mFishPondDetailCommendListAdapter.refresh()
        }
        mFishPondDetailCommendListAdapter.setOnVewMoreClickListener { item, _ ->
            val intent = FishCommendDetailActivity.getIntent(this, mMomentId, item)
            startActivityForResult(intent) { resultCode, _ ->
                if (resultCode == Activity.RESULT_OK) {
                    mFishPondDetailCommendListAdapter.refresh()
                }
            }
        }
        mFishPondDetailCommendListAdapter.setOnCommentClickListener { item, _ ->
            val intent = FishCommendDetailActivity.getIntent(this, mMomentId, item)
            startActivityForResult(intent) { resultCode, _ ->
                if (resultCode == Activity.RESULT_OK) {
                    mFishPondDetailCommendListAdapter.refresh()
                }
            }
        }
        mBinding.commentContainer.tvFishPondSubmitComment.setFixOnClickListener {
            goToPostComment(mNickName)
        }
    }

    private fun goToPostComment(targetUserName: String) {
        takeIfLogin {
            safeShowFragment(targetUserName)
        }
    }

    override fun getStatusLayout(): StatusLayout = mBinding.slFishDetailHint

    override fun isStatusBarDarkFont(): Boolean = false

    override fun getDrawable(source: String?): Drawable {
        source ?: return LevelListDrawable()
        // 1、加载本地的 emoji 表情图片（优点：理论上来说是比加载网络上的图片快的。
        // 缺点：无法做到及时更新，程序员需要重新打包apk发布更新，用户需要安装最新版本的app才可以享受最优的体验）
        // source ?: return null
        // if (source.contains("sunofbeaches.com/emoji/") && source.endsWith(".png")) {
        //     val emojiNum = Regex("\\d").find(source)?.value ?: ""
        //     val resId = context.resources.getIdentifier(
        //         "emoji_$emojiNum",
        //         "mipmap",
        //         context.packageName
        //     )
        //     val drawable = ContextCompat.getDrawable(context, resId)
        //     drawable?.let {
        //         val textSize = tvContent.textSize.toInt()
        //         it.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        //     }
        //     return drawable
        // }
        // logByDebug(msg = "covert：===> image url is $source")
        // return null

        // 2、直接获取网络的图片（优点：不需要在本地内置图片，直接使用在线图片。
        // 缺点：理论上来讲会比加载本地的图片慢一些）
        val drawable = LevelListDrawable()
        val fishPond = mBinding.fishPond
        val tvContent = fishPond.tvFishPondContent
        val textSize = tvContent.textSize.toInt()
        lifecycleScope.launchWhenCreated {
            val uri = Uri.parse(source)
            val resource = DownloadHelper.ofType<Drawable>(fishPond.llFishItemContainer, uri)
            drawable.addLevel(1, 1, resource)
            // 判断是否为表情包
            if (source.contains("sunofbeaches.com/emoji/") && source.endsWith(".png")) {
                drawable.setBounds(6, 0, textSize + 6, textSize)
            } else {
                drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            }
            drawable.level = 1
            tvContent.invalidate()
            tvContent.text = tvContent.text
        }
        return drawable
    }

    companion object {

        @JvmStatic
        @DebugLog
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