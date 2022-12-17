package cn.cqautotest.sunnybeach.ui.activity

import android.content.Context
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.PagingDataAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.PagingActivity
import cn.cqautotest.sunnybeach.databinding.FishTopicActivityBinding
import cn.cqautotest.sunnybeach.ktx.*
import cn.cqautotest.sunnybeach.model.FishPondTopicList
import cn.cqautotest.sunnybeach.model.RefreshStatus
import cn.cqautotest.sunnybeach.ui.adapter.FishListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import com.bumptech.glide.Glide
import com.dylanc.longan.lifecycleOwner
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/12/17
 * desc   : 鱼塘话题 Activity
 */
class FishTopicActivity : PagingActivity() {

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

    companion object {

        private const val FISH_TOPIC_ITEM = "FISH_TOPIC_ITEM"

        fun start(context: Context, item: FishPondTopicList.TopicItem) {
            context.startActivity<FishTopicActivity> {
                putExtra(FISH_TOPIC_ITEM, item.toJson())
            }
        }
    }
}