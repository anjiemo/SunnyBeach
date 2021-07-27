package cn.cqautotest.sunnybeach.ui.fragment.fishpond

import android.graphics.Rect
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.databinding.FishPondListFragmentBinding
import cn.cqautotest.sunnybeach.model.Fish
import cn.cqautotest.sunnybeach.ui.activity.FishPondDetailActivity
import cn.cqautotest.sunnybeach.ui.activity.ImagePreviewActivity
import cn.cqautotest.sunnybeach.ui.adapter.FishPondListAdapter
import cn.cqautotest.sunnybeach.util.*
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/7/11
 * desc   : 摸鱼列表 Fragment
 */
class FishPondListFragment : AppFragment<AppActivity>(), StatusAction {

    private var _binding: FishPondListFragmentBinding? = null
    private val mBinding get() = _binding!!
    private val fishPondViewModel by viewModels<FishPondViewModel>()
    private val mFishPondListAdapter = FishPondListAdapter()
    private val mFishPage = PageBean()
    private var isRefresh = false

    var title: String = ""
    var topicId: String = ""

    override fun getLayoutId(): Int = R.layout.fish_pond_list_fragment

    override fun onBindingView() {
        _binding = FishPondListFragmentBinding.bind(view)
    }

    override fun onFragmentResume(first: Boolean) {
        super.onFragmentResume(first)
        if (first) {
            // 只有第一次可见的时候才加载数据，懒加载
            refreshFishPondData()
            logByDebug(msg = "initData：===> title is $title topicId is $topicId")
        }
    }

    private fun refreshFishPondData() {
        isRefresh = true
        mFishPage.startLoading()
        mFishPage.resetPage()
        mFishPage.nextPage()
        showLoading()
        fishPondViewModel.loadFishPondListById(topicId, mFishPage.currentPage)
    }

    private fun loadMoreFishPondData() {
        isRefresh = false
        mFishPage.startLoading()
        mFishPage.nextPage()
        fishPondViewModel.loadFishPondListById(topicId, mFishPage.currentPage)
    }

    override fun initView() {
        mBinding.rvFishPondList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mFishPondListAdapter
            addItemDecoration(object : RecyclerView.ItemDecoration() {

                // 单位间距（实际间距的一半）
                private val unit = 4.dp

                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    equilibriumAssignmentOfLinear(unit, outRect, view, parent)
                    view.setRoundRectBg(cornerRadius = 16.dp)
                }
            })
        }
    }

    override fun initData() {}

    override fun initEvent() {
        mFishPondListAdapter.addChildClickViewIds(R.id.fl_avatar_container)
        mFishPondListAdapter.setOnItemClickListener { _, _, position ->
            val item = mFishPondListAdapter.getItem(position)
            FishPondDetailActivity.start(requireContext(), item)
        }
        mFishPondListAdapter.setOnItemChildClickListener { _, view, position ->
            val item = mFishPondListAdapter.getItem(position)
            if (view.id == R.id.fl_avatar_container) {
                ImagePreviewActivity.start(requireContext(), item.avatar)
            }
        }
        val rlStatusRefresh = mBinding.rlStatusRefresh
        val loadMoreModule = mFishPondListAdapter.loadMoreModule
        rlStatusRefresh.setOnRefreshListener {
            refreshFishPondData()
        }
        loadMoreModule.setOnLoadMoreListener {
            loadMoreFishPondData()
        }
    }

    override fun initObserver() {
        fishPondViewModel.fishPond.observe(viewLifecycleOwner) { fishPondList ->
            setupFishPondList(fishPondList)
        }
    }

    private fun setupFishPondList(fish: Fish?) {
        val rlStatusRefresh = mBinding.rlStatusRefresh
        val loadMoreModule = mFishPondListAdapter.loadMoreModule
        val fishPondList = fish?.list ?: listOf()
        val hasNext = fish?.hasNext ?: false
        logByDebug(msg = "setupFishPondList：===> fishPondList size is $fishPondList")
        if (isRefresh) {
            mFishPondListAdapter.setList(arrayListOf())
            isRefresh = false
        }
        mFishPondListAdapter.addData(fishPondList)
        if (mFishPondListAdapter.data.isNullOrEmpty()) {
            showEmpty()
        } else {
            showComplete()
        }
        rlStatusRefresh.finishRefresh()
        loadMoreModule.loadMoreComplete()
        if (hasNext.not()) {
            logByDebug(msg = "setupFishPondList：hasNext is ===> $hasNext")
            loadMoreModule.isEnableLoadMore = false
        }
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlFishPondListHint

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}