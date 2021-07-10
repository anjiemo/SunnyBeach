package cn.cqautotest.sunnybeach.ui.fragment.home

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.databinding.ArticleListFragmentBinding
import cn.cqautotest.sunnybeach.ui.activity.ArticleDetailActivity
import cn.cqautotest.sunnybeach.ui.activity.ImagePreviewActivity
import cn.cqautotest.sunnybeach.ui.adapter.ArticleAdapter
import cn.cqautotest.sunnybeach.utils.*
import cn.cqautotest.sunnybeach.viewmodel.home.HomeViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import com.blankj.utilcode.util.NetworkUtils
import com.chad.library.adapter.base.module.BaseLoadMoreModule
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/6/20
 * desc   : 文章列表 Fragment
 */
class ArticleListFragment : AppFragment<AppActivity>(), StatusAction {

    private var _binding: ArticleListFragmentBinding? = null
    private val mBinding get() = _binding!!
    private val articleAdapter by lazy { ArticleAdapter() }
    private var _homeViewModel: HomeViewModel? = null
    private val mHomeViewModel get() = _homeViewModel!!

    var title: String = ""
    var categoryId: String = ""

    override fun getLayoutId(): Int = R.layout.article_list_fragment

    override fun onBindingView() {
        _binding = ArticleListFragmentBinding.bind(view)
    }

    override fun initObserver() {
        val refreshLayout = mBinding.rlStatusRefresh
        val loadMoreModule = articleAdapter.loadMoreModule
        if (isRecommendType()) {
            mHomeViewModel.recommendList.observe(viewLifecycleOwner) { recommendList ->
                val data = recommendList?.list ?: listOf()
                articleAdapter.addData(data)
                refreshComplete(refreshLayout, loadMoreModule)
            }
        } else {
            mHomeViewModel.categoriesMap.observe(viewLifecycleOwner) { articleMap ->
                val data = articleMap?.get(categoryId) ?: listOf()
                articleAdapter.addData(data)
                refreshComplete(refreshLayout, loadMoreModule)
            }
        }
    }

    override fun initEvent() {
        val refreshLayout = mBinding.rlStatusRefresh
        val loadMoreModule = articleAdapter.loadMoreModule
        refreshLayout.setOnRefreshListener {
            NetworkUtils.isAvailableAsync { available ->
                if (available.not()) {
                    // 无网络时显示的状态
                    showError { refreshArticleData() }
                    refreshLayout.finishRefresh()
                    return@isAvailableAsync
                }
                refreshArticleData()
            }
            // 下拉刷新的时候禁用上拉加载
            loadMoreModule.isEnableLoadMore = false
        }
        // 设置文章列表项的点击事件
        articleAdapter.setOnItemClickListener { _, _, position ->
            val articleItem = articleAdapter.getItem(position)
            ArticleDetailActivity.start(articleItem.id, articleItem.title)
        }
        articleAdapter.addChildClickViewIds(
            R.id.iv_avatar,
            R.id.article_cover_one,
            R.id.article_cover_two,
            R.id.article_cover_there,
        )
        articleAdapter.setOnItemChildClickListener { _, view, position ->
            if (view.id == R.id.iv_avatar) {
                val articleItem = articleAdapter.getItem(position)
                ImagePreviewActivity.start(requireContext(), articleItem.avatar)
            }
            if (view.id == R.id.article_cover_one
                || view.id == R.id.article_cover_two
                || view.id == R.id.article_cover_there
            ) {
                val articleItem = articleAdapter.getItem(position)
                ImagePreviewActivity.start(requireContext(), articleItem.covers)
            }
        }
        loadMoreModule.setOnLoadMoreListener {
            loadMoreModule.isEnableLoadMore = false
            loadMoreArticleData()
        }
    }

    /**
     * 刷新结束，统一处理
     */
    private fun refreshComplete(
        refreshLayout: SmartRefreshLayout,
        loadMoreModule: BaseLoadMoreModule
    ) {
        // 刷新结束
        refreshLayout.finishRefresh()
        // 显示加载完成状态
        loadMoreModule.loadMoreComplete()
        // 获取适配器里的数据，因为这里面的数据才是该分类下的全部列表数据
        val data = articleAdapter.data
        // 判断列表显示的数据是否为空
        if (data.isNullOrEmpty()) {
            // 显示空提示
            showEmpty()
        } else {
            // 显示加载完成
            showComplete()
        }
        // 启用下拉刷新
        loadMoreModule.isEnableLoadMore = true
    }

    /**
     * 加载更多文章列表数据
     */
    private fun loadMoreArticleData() {
        if (isRecommendType()) {
            mHomeViewModel.loadMoreRecommendContent()
        } else {
            mHomeViewModel.loadMoreArticleListByCategoryId(categoryId = categoryId)
        }
    }

    /**
     * 刷新文章列表数据
     */
    private fun refreshArticleData() {
        // 清空原来的数据
        articleAdapter.setList(arrayListOf())
        NetworkUtils.isAvailableAsync { isAvailable ->
            if (isAvailable.not()) {
                showError {
                    refreshArticleData()
                }
            } else {
                showLoading()
            }
        }
        if (isRecommendType()) {
            mHomeViewModel.refreshRecommendContent()
        } else {
            mHomeViewModel.refreshArticleListByCategoryId(categoryId = categoryId)
        }
    }

    override fun initData() {
        _homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
    }

    override fun initView() {
        if (isRecommendType()) {
            articleAdapter.addHeaderView(
                LayoutInflater.from(requireContext())
                    .inflate(R.layout.home_discover_more_author_content, null)
            )
        }
        mBinding.rvArticleList.apply {
            adapter = articleAdapter
            layoutManager = LinearLayoutManager(context)
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

    override fun onFragmentResume(first: Boolean) {
        super.onFragmentResume(first)
        if (first) {
            // 只有第一次可见的时候才加载数据，懒加载
            refreshArticleData()
            logByDebug(msg = "initData：===> $title")
        }
    }

    private fun isRecommendType() = "" == categoryId

    override fun getStatusLayout(): StatusLayout = mBinding.hlArticleListHint

    override fun onDestroy() {
        super.onDestroy()
        _homeViewModel = null
    }

    companion object {
        @JvmStatic
        fun newInstance(): ArticleListFragment {
            return ArticleListFragment()
        }
    }
}