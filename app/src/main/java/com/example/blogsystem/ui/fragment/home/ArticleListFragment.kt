package com.example.blogsystem.ui.fragment.home

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blogsystem.R
import com.example.blogsystem.adapter.ArticleAdapter
import com.example.blogsystem.base.BaseFragment
import com.example.blogsystem.databinding.FragmentArticleListBinding
import com.example.blogsystem.utils.*
import com.example.blogsystem.viewmodel.home.HomeViewModel

class ArticleListFragment : BaseFragment(R.layout.fragment_article_list) {

    private var _binding: FragmentArticleListBinding? = null
    private val mBinding get() = _binding!!
    private val articleAdapter by lazy { ArticleAdapter() }
    private val homeViewModel by viewModels<HomeViewModel>()

    var title: String = ""
    var categoryId: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentArticleListBinding.bind(view)
        callAllInit()
    }

    @SuppressLint("NewApi")
    override fun initEvent() {
        val loadMoreModule = articleAdapter.loadMoreModule
        articleAdapter.loadMoreModule.setOnLoadMoreListener {
            loadMoreModule.isEnableLoadMore = false
            if (isRecommendType()) {
                homeViewModel.loadMoreRecommendContent()
            } else {
                homeViewModel.loadMoreArticleListByCategoryId(categoryId = categoryId)
            }
        }
        if (isRecommendType()) {
            homeViewModel.recommendList.observe(this) { recommendList ->
                val data = recommendList?.list ?: listOf()
                articleAdapter.addData(data)
                loadMoreModule.loadMoreComplete()
                loadMoreModule.isEnableLoadMore = true
            }
        } else {
            homeViewModel.categoriesMap.observe(this) { articleMap ->
                val data = articleMap?.getOrDefault(categoryId, listOf()) ?: listOf()
                articleAdapter.addData(data)
                loadMoreModule.loadMoreComplete()
                loadMoreModule.isEnableLoadMore = true
            }
        }
    }

    override fun initData() {
        if (isRecommendType()) {
            homeViewModel.refreshRecommendContent()
        } else {
            homeViewModel.refreshArticleListByCategoryId(categoryId = categoryId)
        }
        logByDebug(msg = "initData：===> $title")
    }

    override fun initView() {
        val progressBar = ProgressBar(requireContext()).apply {
            setTintColor(ContextCompat.getColor(requireContext(), R.color.pink))
        }
        progressBar.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ).apply {
            gravity = Gravity.CENTER
        }
        articleAdapter.setEmptyView(progressBar)
        if (isRecommendType()) {
            articleAdapter.addHeaderView(
                LayoutInflater.from(requireContext())
                    .inflate(R.layout.content_home_discover_more_author, null)
            )
        }
        mBinding.articleListRv.apply {
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
                    view.setRoundRectBg(cornerRadius = 8.dp)
                }
            })
        }
    }

    private fun isRecommendType() = "" == categoryId
}