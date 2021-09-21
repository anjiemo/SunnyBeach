package cn.cqautotest.sunnybeach.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.LevelListDrawable
import android.net.Uri
import android.text.TextUtils
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.activity.viewModels
import androidx.core.app.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.aop.DebugLog
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.FishPondDetailActivityBinding
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.adapter.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.FishPondDetailCommendListAdapter
import cn.cqautotest.sunnybeach.util.*
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import com.blankj.utilcode.util.ScreenUtils
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.collectLatest
import java.lang.ref.WeakReference


/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/07/11
 * desc   : 鱼塘详情页
 */
class FishPondDetailActivity : AppActivity(), StatusAction {

    private lateinit var mBinding: FishPondDetailActivityBinding
    private val mFishPondViewModel by viewModels<FishPondViewModel>()
    private val mFishPondDetailCommendListAdapter =
        FishPondDetailCommendListAdapter(AdapterDelegate())
    private var mMomentId: String = ""

    override fun getLayoutId(): Int = 0

    override fun onBindingView(): ViewBinding {
        mBinding = FishPondDetailActivityBinding.inflate(layoutInflater)
        return mBinding
    }

    override fun initView() {
        mBinding.rvFishPondDetailComment.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mFishPondDetailCommendListAdapter
        }
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
            showComplete()
            val fishPond = mBinding.fishPond
            val flAvatarContainer = fishPond.flAvatarContainer
            val ivAvatar = fishPond.ivFishPondAvatar
            val tvNickname = fishPond.tvFishPondNickName
            val tvDesc = fishPond.tvFishPondDesc
            val tvContent = fishPond.tvFishPondContent
            val rrlContainer = fishPond.rrlContainer
            val llImagesContainer = fishPond.llImagesContainer
            val tvLabel = fishPond.tvFishPondLabel
            val llFishItemContainer = fishPond.llFishItemContainer
            val tvComment = fishPond.listMenuItem.tvComment
            val tvGreat = fishPond.listMenuItem.tvGreat
            val ivShare = fishPond.listMenuItem.ivShare
            val clReplyContainer = mBinding.clReplyContainer
            llFishItemContainer.setRoundRectBg(color = Color.WHITE, cornerRadius = 10.dp)
            flAvatarContainer.background = if (item.vip) ContextCompat.getDrawable(
                context,
                R.drawable.avatar_circle_vip_ic
            ) else null
            Glide.with(this)
                .load(item.avatar)
                .placeholder(R.mipmap.ic_default_avatar)
                .error(R.mipmap.ic_default_avatar)
                .circleCrop()
                .into(ivAvatar)
            val nickNameColor = if (item.vip) {
                R.color.pink
            } else {
                R.color.black
            }
            tvNickname.setTextColor(ContextCompat.getColor(context, nickNameColor))
            tvNickname.text = item.nickname
            tvDesc.text = "${item.position} · " +
                    DateHelper.transform2FriendlyTimeSpanByNow("${item.createTime}:00")
            tvContent.text = HtmlCompat.fromHtml(
                item.content
                    .replace("<br>", "\n")
                    .replace("</br>", "\n"),
                HtmlCompat.FROM_HTML_MODE_LEGACY,
                { source ->
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
                    val textSize = tvContent.textSize.toInt()
                    (context as? ComponentActivity)?.lifecycleScope?.launchWhenCreated {
                        val resource =
                            DownloadHelper.getTypeByUri<Drawable>(
                                llFishItemContainer,
                                Uri.parse(source)
                            )
                        drawable.addLevel(1, 1, resource)
                        // 判断是否为表情包
                        if (source.contains("sunofbeaches.com/emoji/") && source.endsWith(".png")) {
                            drawable.setBounds(6, 0, textSize + 6, textSize)
                        } else {
                            drawable.setBounds(
                                0,
                                0,
                                drawable.intrinsicWidth,
                                drawable.intrinsicHeight
                            )
                        }
                        drawable.level = 1
                        tvContent.invalidate()
                        tvContent.text = tvContent.text
                    }
                    drawable
                },
                null
            )
            tvContent.setOnCreateContextMenuListener { menu, v, menuInfo ->

            }
            val topicName = item.topicName
            val images = item.images
            rrlContainer.visibility = if (images.isNullOrEmpty()) View.GONE else View.VISIBLE
            llImagesContainer.layoutParams = RelativeLayout.LayoutParams(
                (ScreenUtils.getScreenWidth() - 40.dp) / 3 * images.size,
                (ScreenUtils.getScreenWidth() - 40.dp) / 3
            )
            val imageUrls = arrayListOf<String>()
            repeat(llImagesContainer.childCount) { index ->
                // childView 只能是 ImageView 或其子类，否则会强转异常
                val imageView = llImagesContainer.getChildAt(index) as ImageView
                imageView.layoutParams = LinearLayout.LayoutParams(
                    (ScreenUtils.getScreenWidth() - 40.dp) / 3,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                val imageUrl = images.getOrNull(index)
                imageView.visibility = if (imageUrl != null) {
                    // 如果是有效链接或者能获取到链接，则加载图片
                    Glide.with(this).load(imageUrl).into(imageView)
                    // 显示该位置的图片
                    View.VISIBLE
                } else {
                    // 隐藏该位置的图片
                    View.GONE
                }
                imageUrl?.let {
                    imageUrls.add(imageUrl)
                }
                imageView.setFixOnClickListener {
                    ImagePreviewActivity.start(this, imageUrls, index)
                }
            }
            tvLabel.visibility = if (TextUtils.isEmpty(topicName)) View.GONE else View.VISIBLE
            tvLabel.text = topicName
            tvComment.text = with(item.commentCount) {
                if (this == 0) {
                    "评论"
                } else {
                    toString()
                }
            }
            tvGreat.text = with(item.thumbUpCount) {
                if (this == 0) {
                    "点赞"
                } else {
                    toString()
                }
            }
            val gestureDetector = GestureDetector(
                this, PullUpGesture(this, open = {
                    goToPostComment()
                }, close = {
                    // Nothing to do.
                })
            )
            // 如果要使用 GestureDetector 手势检测器，则必须禁用点击事件，否则无法检测手势
            clReplyContainer.setOnClickListener(null)
            clReplyContainer.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }
        }
    }

    override fun initEvent() {
        mBinding.slFishDetailRefresh.setOnRefreshListener {
            // 加载摸鱼动态详情和摸鱼动态评论列表
            loadFishDetail()
            // TODO: 2021/9/13 加载摸鱼动态相关推荐列表
            mFishPondDetailCommendListAdapter.refresh()
        }
        mFishPondDetailCommendListAdapter.setOnVewMoreClickListener { item, _ ->
            FishCommendDetailActivity.start(this, item)
        }
        mBinding.tvFishPondSubmitComment.setFixOnClickListener {
            goToPostComment()
        }
    }

    /**
     * 去发表评论
     */
    private fun goToPostComment() {
        val intent = SubmitCommendActivity.getCommentIntent(this, mMomentId)
        startActivityForResult(intent) { resultCode, _ ->
            if (resultCode == RESULT_OK) {
                // 如果提交了评论且成功了，需要刷新界面
                initData()
            }
        }
    }

    override fun initObserver() {}

    override fun getStatusLayout(): StatusLayout = mBinding.slFishDetailHint

    override fun isStatusBarDarkFont(): Boolean = false

    companion object {

        private class PullUpGesture(
            context: Context,
            private val open: () -> Unit,
            private val close: () -> Unit
        ) :
            GestureDetector.SimpleOnGestureListener() {

            private val mContext = WeakReference(context)

            // 在我们认为用户正在滚动之前，触摸就可以移动以像素为单位的距离
            val minDistance = ViewConfiguration.get(mContext.get()).scaledTouchSlop

            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1.y - e2.y > minDistance) {
                    // 上滑，展开 View
                    open.invoke()
                } else if (e1.y - e2.y < minDistance) {
                    // 下滑，收起 View
                    close.invoke()
                }
                return true
            }
        }

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