package cn.cqautotest.sunnybeach.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.PagingDataAdapter
import androidx.palette.graphics.Palette
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.PagingActivity
import cn.cqautotest.sunnybeach.databinding.FishTopicActivityBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.fromJson
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.ktx.startActivity
import cn.cqautotest.sunnybeach.ktx.toJson
import cn.cqautotest.sunnybeach.model.Fish
import cn.cqautotest.sunnybeach.model.FishPondTopicList
import cn.cqautotest.sunnybeach.model.RefreshStatus
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.other.RoundRectDrawable
import cn.cqautotest.sunnybeach.ui.adapter.FishListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.util.MultiOperationHelper
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import cn.cqautotest.sunnybeach.widget.recyclerview.SimpleLinearSpaceItemDecoration
import com.blankj.utilcode.util.ConvertUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.flyjingfish.android_aop_core.annotations.CheckNetwork
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/12/17
 * desc   : 鱼塘话题 Activity
 */
@AndroidEntryPoint
class FishTopicActivity : PagingActivity(), RequestListener<Drawable> {

    private val mBinding by viewBinding(FishTopicActivityBinding::bind)
    private val mFishPondViewModel by viewModels<FishPondViewModel>()

    @Inject
    lateinit var mMultiOperationHelper: MultiOperationHelper
    private val mRefreshStatus = RefreshStatus()
    private val mTopicItemJson by lazy { intent.getStringExtra(FISH_TOPIC_ITEM) }
    private val mTopicItem by lazy { fromJson<FishPondTopicList.TopicItem>(mTopicItemJson) }
    private val mFishListAdapterDelegate = AdapterDelegate()
    private val mFishListAdapter = FishListAdapter(mFishListAdapterDelegate)

    override fun getPagingAdapter(): PagingDataAdapter<*, *> = mFishListAdapter

    override fun getLayoutId(): Int = R.layout.fish_topic_activity

    override fun initView() {
        super.initView()
        Timber.d("initView：===> mTopicItem is $mTopicItem")
        Glide.with(this)
            .load(mTopicItem.cover)
            .transform(RoundedCorners(4.dp))
            .addListener(this)
            .into(mBinding.ivCover)
        mBinding.apply {
            tvTopicName.text = mTopicItem.topicName
            tvTopicDesc.text = mTopicItem.description
            updateTopicSimpleInfo()
            pagingRecyclerView.addItemDecoration(SimpleLinearSpaceItemDecoration(6.dp))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateTopicSimpleInfo() {
        mBinding.tvSummary.text = "🐟 ${mTopicItem.contentCount} · 滩友 ${mTopicItem.followCount}"
    }

    override fun initData() {

        lifecycleScope.launch { repeatOnLifecycle(Lifecycle.State.STARTED) { getFollowedTopicList() } }
        lifecycleScope.launch { repeatOnLifecycle(Lifecycle.State.STARTED) { loadListData() } }
    }

    @CheckNetwork(invokeListener = true)
    private fun getFollowedTopicList() {
        mFishPondViewModel.getFollowedTopicList().observe(this) { result ->
            result.onSuccess { topicList ->
                topicList.firstOrNull { mTopicItem.id == it.id }?.let {
                    mTopicItem.hasFollowed = true
                    mTopicItem.followCount = it.followCount
                } ?: run { mTopicItem.hasFollowed = false }
                updateTopicFollowState()
                updateTopicSimpleInfo()
            }.onFailure {
                Timber.e(it)
            }
        }
    }

    override suspend fun loadListData() {
        mFishPondViewModel.fishListFlow.collectLatest {
            mFishListAdapter.submitData(it)
        }
    }

    override fun initEvent() {
        super.initEvent()
        mBinding.apply {
            tvJoin.setFixOnClickListener {
                toggleFollow()
            }
        }
        mFishListAdapterDelegate.setOnItemClickListener { _, position ->
            mFishListAdapter.snapshotList.getOrNull(position)?.let { FishPondDetailActivity.start(this, it.id) }
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
        mMultiOperationHelper.dynamicLikes(this, mFishPondViewModel, mFishListAdapter, item, position)
    }

    private fun shareFish(item: Fish.FishItem) {
        mMultiOperationHelper.shareFish(item.id)
    }

    private fun toggleFollow() {
        if (hasFollow()) {
            mFishPondViewModel.unfollowFishTopic(mTopicItem.id).observe(this) {
                getFollowedTopicList()
            }
        } else {
            mFishPondViewModel.followFishTopic(mTopicItem.id).observe(this) {
                getFollowedTopicList()
            }
        }
    }

    private fun updateTopicFollowState() {
        val hasFollow = hasFollow()
        mBinding.apply {
            tvJoin.text = if (hasFollow) "已加入" else "加入"
            tvJoin.isSelected = hasFollow
        }
    }

    private fun hasFollow() = mTopicItem.hasFollowed

    override fun showLoading(id: Int) {
        takeIf { mRefreshStatus.isFirstRefresh }?.let { super.showLoading(id) }
        mRefreshStatus.isFirstRefresh = false
    }

    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
        // There is no processing here.
        return false
    }

    override fun onResourceReady(
        resource: Drawable,
        model: Any,
        target: Target<Drawable>?,
        dataSource: DataSource,
        isFirstResource: Boolean
    ): Boolean {
        // We're not dealing with that here, we just want to get the resource at the end of the load, that's all.
        onResourceChanged(resource)
        return false
    }

    private fun onResourceChanged(resource: Drawable?) {
        lifecycleScope.launch {
            flow {
                val bitmap = ConvertUtils.drawable2Bitmap(resource)
                emit(bitmap)
            }
                .map { Palette.from(it).generate() }
                .map { it.getLightVibrantColor(Color.TRANSPARENT) }
                .catch { Timber.e(it) }
                .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
                // 以上 Palette.from(it).generate() 为同步方法，需要在子线程调用
                .flowOn(Dispatchers.IO)
                .collectLatest { bgColor -> mBinding.ivCover.background = RoundRectDrawable(4.dp, bgColor) }
        }
    }

    companion object {

        private const val FISH_TOPIC_ITEM = "FISH_TOPIC_ITEM"

        fun start(context: Context, item: FishPondTopicList.TopicItem) {
            context.startActivity<FishTopicActivity> {
                putExtra(IntentKey.ID, item.id)
                putExtra(FISH_TOPIC_ITEM, item.toJson())
            }
        }
    }
}