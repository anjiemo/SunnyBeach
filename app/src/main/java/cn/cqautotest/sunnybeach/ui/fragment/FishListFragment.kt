package cn.cqautotest.sunnybeach.ui.fragment

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.TitleBarFragment
import cn.cqautotest.sunnybeach.databinding.FishListFragmentBinding
import cn.cqautotest.sunnybeach.ui.activity.FishPondDetailActivity
import cn.cqautotest.sunnybeach.ui.activity.ImagePreviewActivity
import cn.cqautotest.sunnybeach.ui.activity.PutFishActivity
import cn.cqautotest.sunnybeach.ui.adapter.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.FishListAdapter
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.util.dp
import cn.cqautotest.sunnybeach.util.setFixOnClickListener
import cn.cqautotest.sunnybeach.util.startActivity
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/07/07
 * desc   : 摸鱼列表管理 Fragment
 */
class FishListFragment : TitleBarFragment<AppActivity>(), StatusAction {

    private val mBinding: FishListFragmentBinding by viewBinding()
    private val mFishPondViewModel by activityViewModels<FishPondViewModel>()
    private val mFishListAdapter = FishListAdapter(AdapterDelegate())
    private val loadStateListener = { cls: CombinedLoadStates ->
        if (cls.refresh is LoadState.NotLoading) {
            showComplete()
            mBinding.refreshLayout.finishRefresh()
        }
        if (cls.refresh is LoadState.Loading) {
            showLoading()
        }
        if (cls.refresh is LoadState.Error) {
            showError {
                mFishListAdapter.refresh()
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.fish_list_fragment

    override fun initObserver() {}

    override fun initEvent() {
        mBinding.refreshLayout.setOnRefreshListener {
            mFishListAdapter.refresh()
        }
        // 需要在 View 销毁的时候移除 listener
        mFishListAdapter.addLoadStateListener(loadStateListener)
        mFishListAdapter.setOnItemClickListener { item, _ ->
            val momentId = item.id
            FishPondDetailActivity.start(requireContext(), momentId)
        }
        mBinding.ivPublishContent.setFixOnClickListener {
            requireContext().startActivity<PutFishActivity>()
        }
        mFishListAdapter.setOnNineGridClickListener { sources, index ->
            ImagePreviewActivity.start(requireContext(), sources, index)
        }
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

    companion object {
        @JvmStatic
        fun newInstance() = FishListFragment()
    }
}