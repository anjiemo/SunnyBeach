package com.example.blogsystem.ui.fragment

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
import com.example.blogsystem.databinding.FragmentHomeBinding
import com.example.blogsystem.utils.dp
import com.example.blogsystem.utils.equilibriumAssignmentOfLinear
import com.example.blogsystem.utils.setRoundRectBg
import com.example.blogsystem.utils.setTintColor
import com.example.blogsystem.viewmodel.home.HomeViewModel

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val mBinding get() = _binding!!
    private val articleAdapter by lazy { ArticleAdapter() }
    private val homeViewModel by viewModels<HomeViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        callAllInit()
    }

    override fun initEvent() {
        lifecycle.addObserver(homeViewModel)
        homeViewModel.homeCategories.observe(this) { homeCategories ->
            val tabLayoutCategories = mBinding.tabLayoutCategories
            tabLayoutCategories.newTab().apply {
                text = "推荐"
                tabLayoutCategories.addTab(this)
            }
            // 动态创建 Tab 标签
            for (categoriesItem in homeCategories) {
                tabLayoutCategories.newTab().apply {
                    text = categoriesItem.categoryName
                    tabLayoutCategories.addTab(this)
                }
            }
        }
        val loadMoreModule = articleAdapter.loadMoreModule
        articleAdapter.loadMoreModule.setOnLoadMoreListener {
            loadMoreModule.isEnableLoadMore = false
            homeViewModel.loadMoreRecommendContent(2)
        }
        homeViewModel.recommendList.observe(this) { recommendList ->
            val data = recommendList?.list ?: listOf()
            articleAdapter.addData(data)
            loadMoreModule.loadMoreComplete()
            loadMoreModule.isEnableLoadMore = true
        }
    }

    override fun initData() {
        homeViewModel.getCategories()
        homeViewModel.loadMoreRecommendContent(1)
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
        articleAdapter.addHeaderView(
            LayoutInflater.from(requireContext())
                .inflate(R.layout.content_home_discover_more_author, null)
        )
        mBinding.articleListRv.let {
            it.adapter = articleAdapter
            it.layoutManager = LinearLayoutManager(context)
            it.addItemDecoration(object : RecyclerView.ItemDecoration() {

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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        lifecycle.removeObserver(homeViewModel)
    }
}