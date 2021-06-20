package com.example.blogsystem.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.collection.arrayMapOf
import androidx.fragment.app.viewModels
import com.example.blogsystem.R
import com.example.blogsystem.base.BaseFragment
import com.example.blogsystem.base.FragmentAdapter
import com.example.blogsystem.databinding.FragmentHomeBinding
import com.example.blogsystem.ui.fragment.home.ArticleListFragment
import com.example.blogsystem.utils.onPageSelected
import com.example.blogsystem.utils.onTabSelected
import com.example.blogsystem.viewmodel.home.HomeViewModel

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val mBinding get() = _binding!!
    private val homeViewModel by viewModels<HomeViewModel>()
    private val mFragmentMap by lazy { arrayMapOf<Int, BaseFragment>() }
    private lateinit var mFragmentAdapter: FragmentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        callAllInit()
    }

    override fun initEvent() {
        lifecycle.addObserver(homeViewModel)
        val tabLayoutCategories = mBinding.tabLayoutCategories
        val vp2HomeArticleContainer = mBinding.vp2HomeArticleContainer
        tabLayoutCategories.onTabSelected {
            val tab = it ?: return@onTabSelected
            val position = tab.position
            vp2HomeArticleContainer.setCurrentItem(position, false)
        }
        vp2HomeArticleContainer.onPageSelected { position ->
            val tab = tabLayoutCategories.getTabAt(position)
            tabLayoutCategories.selectTab(tab)
        }
        homeViewModel.homeCategories.observe(this) { homeCategories ->
            tabLayoutCategories.newTab().apply {
                text = "推荐"
                tabLayoutCategories.addTab(this)
            }
            mFragmentMap[0] = ArticleListFragment().apply {
                title = "推荐"
            }
            // 动态创建 Tab 标签
            homeCategories.forEachIndexed { index, categoriesItem ->
                tabLayoutCategories.newTab().apply {
                    text = categoriesItem.categoryName
                    tabLayoutCategories.addTab(this)
                }
                mFragmentMap[index + 1] = ArticleListFragment().apply {
                    title = categoriesItem.categoryName
                    categoryId = categoriesItem.id
                }
            }
            vp2HomeArticleContainer.offscreenPageLimit = mFragmentMap.size
            mFragmentAdapter.setFragmentMap(mFragmentMap)
        }
    }

    override fun initData() {
        homeViewModel.getCategories()
    }

    override fun initView() {
        mFragmentAdapter = FragmentAdapter(this)
        mBinding.vp2HomeArticleContainer.apply {
            // TODO: 2021/6/20 暂时不允许左右滑动，因为ViewPager2的滑动比较灵敏，后期处理之后改为可以左右滑动
            isUserInputEnabled = false
            adapter = mFragmentAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        lifecycle.removeObserver(homeViewModel)
    }
}