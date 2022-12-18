package cn.cqautotest.sunnybeach.ui.activity

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.PagingDataAdapter
import androidx.palette.graphics.Palette
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.PagingActivity
import cn.cqautotest.sunnybeach.databinding.FishTopicActivityBinding
import cn.cqautotest.sunnybeach.ktx.*
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.Fish
import cn.cqautotest.sunnybeach.model.FishPondTopicList
import cn.cqautotest.sunnybeach.model.RefreshStatus
import cn.cqautotest.sunnybeach.other.RoundRectDrawable
import cn.cqautotest.sunnybeach.ui.adapter.FishListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.dialog.ShareDialog
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_FISH_URL_PRE
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.VibrateUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.dylanc.longan.lifecycleOwner
import com.hjq.umeng.Platform
import com.hjq.umeng.UmengShare
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/12/17
 * desc   : 鱼塘话题 Activity
 */
class FishTopicActivity : PagingActivity(), RequestListener<Drawable> {

    private val mBinding by viewBinding(FishTopicActivityBinding::bind)
    private val mFishPondViewModel by viewModels<FishPondViewModel>()
    private val mRefreshStatus = RefreshStatus()
    private val mTopicItemJson by lazy { intent.getStringExtra(FISH_TOPIC_ITEM) }
    private val mTopicItem by lazy { fromJson<FishPondTopicList.TopicItem>(mTopicItemJson) }
    private var mHasFollowed = false
    private val mFishListAdapterDelegate = AdapterDelegate()
    private val mFishListAdapter = FishListAdapter(mFishListAdapterDelegate)

    override fun getPagingAdapter(): PagingDataAdapter<*, *> = mFishListAdapter

    override fun getLayoutId(): Int = R.layout.fish_topic_activity

    override fun initView() {
        super.initView()
        mHasFollowed = mTopicItem.hasFollowed
        Timber.d("initView：===> mTopicItem is ${mTopicItem.toJson()}")
        mBinding.apply {
            Glide.with(context)
                .load(mTopicItem.cover)
                .transform(RoundedCorners(4.dp))
                .addListener(this@FishTopicActivity)
                .into(ivCover)
            tvTopicName.text = mTopicItem.topicName
            tvSummary.text = "🐟 ${mTopicItem.contentCount} · 滩友 ${mTopicItem.followCount}"
            tvTopicDesc.text = mTopicItem.description
            updateTopicFollowState()
            pagingRecyclerView.addItemDecoration(SimpleLinearSpaceItemDecoration(6.dp))
        }
    }

    override fun initData() {
        lifecycleScope.launch { repeatOnLifecycle(Lifecycle.State.STARTED) { loadListData() } }
    }

    override suspend fun loadListData() {
        mFishPondViewModel.getFishListByCategoryId(mTopicItem.id).collectLatest {
            mFishListAdapter.submitData(it)
        }
    }

    override fun initEvent() {
        mBinding.apply {
            tvJoin.setFixOnClickListener {
                toggleFollow()
            }
        }
        mFishListAdapterDelegate.setOnItemClickListener { _, position ->
            mFishListAdapter.snapshotList[position]?.let { FishPondDetailActivity.start(this, it.id) }
        }
        mFishListAdapter.setOnMenuItemClickListener { view, item, position ->
            when (view.id) {
                R.id.ll_share -> shareFish(item)
                R.id.ll_great -> dynamicLikes(item, position)
            }
        }
        mFishListAdapter.setOnNineGridClickListener { sources, index ->
            ImagePreviewActivity.start(this, sources.toMutableList(), index)
        }
    }

    private fun dynamicLikes(item: Fish.FishItem, position: Int) {
        val thumbUpList = item.thumbUpList
        val currUserId = UserManager.loadCurrUserId()
        takeIf { thumbUpList.contains(currUserId) }?.let { toast("请不要重复点赞") }?.also { return }
        thumbUpList.add(currUserId)
        mFishListAdapter.notifyItemChanged(position)
        VibrateUtils.vibrate(80)
        mFishPondViewModel.dynamicLikes(item.id).observe(lifecycleOwner) {}
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

    private fun toggleFollow() {
        if (mHasFollowed) {
            mFishPondViewModel.unfollowFishTopic(mTopicItem.id).observe(lifecycleOwner) { result ->
                result.onSuccess {
                    mHasFollowed = false
                    updateTopicFollowState()
                }.onFailure {
                    toast(it.message)
                }
            }
        } else {
            mFishPondViewModel.followFishTopic(mTopicItem.id).observe(lifecycleOwner) { result ->
                result.onSuccess {
                    mHasFollowed = true
                    updateTopicFollowState()
                }.onFailure {
                    toast(it.message)
                }
            }
        }
    }

    private fun updateTopicFollowState() {
        mBinding.apply {
            tvJoin.text = if (mHasFollowed) "已加入" else "加入"
            tvJoin.isSelected = mHasFollowed
        }
    }

    override fun showLoading(id: Int) {
        takeIf { mRefreshStatus.isFirstRefresh }?.let { super.showLoading(id) }
        mRefreshStatus.isFirstRefresh = false
    }

    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
        // There is no processing here.
        return false
    }

    override fun onResourceReady(
        resource: Drawable?,
        model: Any?,
        target: Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        // We're not dealing with that here, we just want to get the resource at the end of the load, that's all.
        onResourceChanged(resource)
        return false
    }

    private fun onResourceChanged(resource: Drawable?) {
        lifecycleScope.launchWhenCreated {
            flow {
                val bitmap = ConvertUtils.drawable2Bitmap(resource)
                emit(bitmap)
            }
                .map { Palette.from(it).generate() }
                .map { it.getLightVibrantColor(Color.TRANSPARENT) }
                .catch { Timber.e(it) }
                // 以上 Palette.from(it).generate() 为同步方法，需要在子线程调用
                .flowOn(Dispatchers.IO)
                .collectLatest { bgColor -> mBinding.ivCover.background = RoundRectDrawable(4.dp, bgColor) }
        }
    }

    companion object {

        private const val FISH_TOPIC_ITEM = "FISH_TOPIC_ITEM"

        fun start(context: Context, item: FishPondTopicList.TopicItem) {
            context.startActivity<FishTopicActivity> {
                putExtra(FISH_TOPIC_ITEM, item.toJson())
            }
        }
    }
}