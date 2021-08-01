package cn.cqautotest.sunnybeach.ui.fragment.home

import android.annotation.SuppressLint
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
import cn.cqautotest.sunnybeach.util.dp
import cn.cqautotest.sunnybeach.util.equilibriumAssignmentOfLinear
import cn.cqautotest.sunnybeach.util.logByDebug
import cn.cqautotest.sunnybeach.util.setRoundRectBg
import cn.cqautotest.sunnybeach.viewmodel.home.HomeViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
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
    private val mArticleAdapter = ArticleAdapter()
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
        val loadMoreModule = mArticleAdapter.loadMoreModule
        if (isRecommendType()) {
            mHomeViewModel.recommendList.observe(viewLifecycleOwner) { recommendList ->
                val data = recommendList?.list ?: listOf()
                mArticleAdapter.addData(data)
                refreshComplete(refreshLayout, loadMoreModule)
            }
        } else {
            mHomeViewModel.categoriesMap.observe(viewLifecycleOwner) { articleMap ->
                val data = articleMap?.get(categoryId) ?: listOf()
                mArticleAdapter.addData(data)
                refreshComplete(refreshLayout, loadMoreModule)
            }
        }
    }

    override fun initEvent() {
        val refreshLayout = mBinding.rlStatusRefresh
        val loadMoreModule = mArticleAdapter.loadMoreModule
        refreshLayout.setOnRefreshListener {
            refreshArticleData()
            // 下拉刷新的时候禁用上拉加载
            loadMoreModule.isEnableLoadMore = false
        }
        // 设置文章列表项的点击事件
        mArticleAdapter.setOnItemClickListener { _, _, position ->
            val articleItem = mArticleAdapter.getItem(position)
            ArticleDetailActivity.start(requireContext(), articleItem.id, articleItem.title)
        }
        mArticleAdapter.addChildClickViewIds(
            R.id.iv_avatar,
            R.id.article_cover_one,
            R.id.article_cover_two,
            R.id.article_cover_there,
        )
        mArticleAdapter.setOnItemChildClickListener { _, view, position ->
            if (view.id == R.id.iv_avatar) {
                val articleItem = mArticleAdapter.getItem(position)
                ImagePreviewActivity.start(requireContext(), articleItem.avatar)
            }
            if (view.id == R.id.article_cover_one
                || view.id == R.id.article_cover_two
                || view.id == R.id.article_cover_there
            ) {
                val articleItem = mArticleAdapter.getItem(position)
                ImagePreviewActivity.start(requireContext(), articleItem.covers)
            }
        }
        loadMoreModule.setOnLoadMoreListener {
            loadMoreModule.isEnableLoadMore = false
            loadArticleData()
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
        val data = mArticleAdapter.data
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
    private fun loadArticleData() {
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
        mArticleAdapter.setList(listOf())
        loadArticleData()
    }

    override fun initData() {
        showLoading()
        _homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
    }

    @SuppressLint("InflateParams")
    override fun initView() {
        if (isRecommendType()) {
            mArticleAdapter.removeAllHeaderView()
            mArticleAdapter.addHeaderView(
                LayoutInflater.from(requireContext())
                    .inflate(R.layout.home_discover_more_author_content, null)
            )
        }
        mBinding.rvArticleList.apply {
            adapter = mArticleAdapter
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}