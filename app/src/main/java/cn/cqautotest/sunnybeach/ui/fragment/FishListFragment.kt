package cn.cqautotest.sunnybeach.ui.fragment

import android.app.Activity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.TitleBarFragment
import cn.cqautotest.sunnybeach.databinding.FishListFragmentBinding
import cn.cqautotest.sunnybeach.ui.activity.FishPondDetailActivity
import cn.cqautotest.sunnybeach.ui.activity.ImagePreviewActivity
import cn.cqautotest.sunnybeach.ui.activity.PutFishActivity
import cn.cqautotest.sunnybeach.ui.adapter.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.FishListAdapter
import cn.cqautotest.sunnybeach.util.*
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/07/07
 * desc   : 摸鱼动态列表 Fragment
 */
class FishListFragment : TitleBarFragment<AppActivity>(), StatusAction, OnBack2TopListener {

    private val mBinding: FishListFragmentBinding by viewBinding()
    private val mFishPondViewModel by activityViewModels<FishPondViewModel>()
    private val mFishListAdapter = FishListAdapter(AdapterDelegate())
    private val loadStateListener = { cls: CombinedLoadStates ->
        when (cls.refresh) {
            is LoadState.NotLoading -> {
                mBinding.refreshLayout.finishRefresh()
                if (mFishListAdapter.isEmpty()) {
                    showEmpty()
                } else {
                    showComplete()
                }
            }
            is LoadState.Loading -> showLoading()
            is LoadState.Error -> showError { mFishListAdapter.refresh() }
        }
    }

    override fun getLayoutId(): Int = R.layout.fish_list_fragment

    override fun initObserver() {}

    override fun initEvent() {
        val ivPublishContent = mBinding.ivPublishContent
        titleBar?.setDoubleClickListener {
            onBack2Top()
        }
        mBinding.refreshLayout.setOnRefreshListener {
            mFishListAdapter.refresh()
        }
        // 需要在 View 销毁的时候移除 listener
        mFishListAdapter.addLoadStateListener(loadStateListener)
        mFishListAdapter.setOnItemClickListener { item, _ ->
            val momentId = item.id
            FishPondDetailActivity.start(requireContext(), momentId)
        }
        ivPublishContent.setFixOnClickListener {
            startActivityForResult(PutFishActivity::class.java) { resultCode, _ ->
                if (resultCode == Activity.RESULT_OK) {
                    initData()
                }
            }
        }
        mFishListAdapter.setOnNineGridClickListener { sources, index ->
            ImagePreviewActivity.start(requireContext(), sources, index)
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

    override fun initData() {
        loadFishList()
    }

    private fun loadFishList() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            mFishPondViewModel.getFishListByCategoryId("recommend").collectLatest {
                mFishListAdapter.submitData(it)
            }
        }
    }

    override fun initView() {
        mBinding.rvFishPondList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mFishListAdapter
            addItemDecoration(SimpleLinearSpaceItemDecoration(4.dp))
        }
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

    companion object {
        @JvmStatic
        fun newInstance() = FishListFragment()
    }
}