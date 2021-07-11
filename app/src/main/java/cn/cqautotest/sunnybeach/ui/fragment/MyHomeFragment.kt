package cn.cqautotest.sunnybeach.ui.fragment

import android.view.View
import androidx.collection.arrayMapOf
import androidx.fragment.app.viewModels
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.app.FragmentAdapter
import cn.cqautotest.sunnybeach.databinding.MyHomeFragmentBinding
import cn.cqautotest.sunnybeach.ui.activity.HomeActivity
import cn.cqautotest.sunnybeach.ui.fragment.home.ArticleListFragment
import cn.cqautotest.sunnybeach.utils.onPageSelected
import cn.cqautotest.sunnybeach.utils.onTabSelected
import cn.cqautotest.sunnybeach.viewmodel.home.HomeViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import com.blankj.utilcode.util.NetworkUtils

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/6/20
 * desc   : 首页 Fragment
 */
class MyHomeFragment : AppFragment<HomeActivity>(), StatusAction {

    private var _binding: MyHomeFragmentBinding? = null
    private val mBinding get() = _binding!!
    private val mHomeViewModel by viewModels<HomeViewModel>()
    private val mFragmentMap by lazy { arrayMapOf<Int, AppFragment<AppActivity>>() }
    private lateinit var mFragmentAdapter: FragmentAdapter

    override fun getLayoutId(): Int = R.layout.my_home_fragment

    override fun onBindingView() {
        _binding = MyHomeFragmentBinding.bind(view)
    }

    override fun onFragmentResume(first: Boolean) {
        super.onFragmentResume(first)
        if (first) {
            refreshArticleListData()
        } else {
            // 如果没有文章列表数据，则重新加载文章列表数据
            if (mFragmentMap.isNullOrEmpty()) {
                refreshArticleListData()
            }
        }
    }

    /**
     * 刷新文章列表数据
     */
    private fun refreshArticleListData() {
        NetworkUtils.isAvailableAsync { isAvailable ->
            if (isAvailable.not()) {
                showError {
                    mHomeViewModel.getCategories()
                }
            } else {
                showLoading()
                mHomeViewModel.getCategories()
            }
        }
    }

    override fun initObserver() {
        lifecycle.run {
            addObserver(mHomeViewModel)
            addObserver(mFragmentAdapter)
        }
        val tabLayoutCategories = mBinding.tabLayoutCategories
        val vp2HomeArticleContainer = mBinding.vp2HomeArticleContainer
        mHomeViewModel.homeCategories.observe(viewLifecycleOwner) { homeCategories ->
            // 显示分类标签
            tabLayoutCategories.visibility = View.VISIBLE
            // 重置数据
            tabLayoutCategories.removeAllTabs()
            mFragmentMap.clear()
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
            // vp2HomeArticleContainer.offscreenPageLimit = mFragmentMap.size
            mFragmentAdapter.setFragmentMap(mFragmentMap)
            showComplete()
        }
    }

    override fun initEvent() {
        val tabLayoutCategories = mBinding.tabLayoutCategories
        val vp2HomeArticleContainer = mBinding.vp2HomeArticleContainer
        tabLayoutCategories.onTabSelected {
            val tab = it ?: return@onTabSelected
            val position = tab.position
            showLoading()
            vp2HomeArticleContainer.setCurrentItem(position, false)
            if (mFragmentMap[position] == null) {
                showEmpty()
            } else {
                showComplete()
            }
        }
        vp2HomeArticleContainer.onPageSelected { position ->
            val tab = tabLayoutCategories.getTabAt(position)
            tabLayoutCategories.selectTab(tab)
        }
    }

    override fun initData() {}

    override fun initView() {
        showEmpty()
        mFragmentAdapter = FragmentAdapter(this)
        mBinding.vp2HomeArticleContainer.apply {
            adapter = mFragmentAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        lifecycle.run {
            removeObserver(mHomeViewModel)
            removeObserver(mFragmentAdapter)
        }
        mFragmentMap.values.forEach {
            it.onDestroy()
        }
        mFragmentMap.clear()
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlMyHomeFragmentHint
}