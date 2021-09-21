package cn.cqautotest.sunnybeach.ui.fragment

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.TitleBarFragment
import cn.cqautotest.sunnybeach.databinding.FishPondFragmentBinding
import cn.cqautotest.sunnybeach.ui.activity.FishPondDetailActivity
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

    private var _binding: FishPondFragmentBinding? = null
    private val mBinding get() = _binding!!
    private val mFishPondViewModel by viewModels<FishPondViewModel>()
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

    override fun getLayoutId(): Int = R.layout.fish_pond_fragment

    override fun onBindingView() {
        _binding = FishPondFragmentBinding.bind(view)
    }

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
    }

    override fun initData() {
        loadFishList()
    }

    private fun loadFishList() {
        lifecycleScope.launchWhenCreated {
            mFishPondViewModel.getFishListByCategoryId("recommend").collectLatest {
                mFishListAdapter.submitData(it)
            }
        }
    }

    override fun initView() {
        mBinding.rvFishPondList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mFishListAdapter
            addItemDecoration(SimpleLinearSpaceItemDecoration(4.dp))
        }
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlFishPondHint

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

    companion object {

        @JvmStatic
        fun newInstance(): FishListFragment {
            return FishListFragment()
        }
    }
}