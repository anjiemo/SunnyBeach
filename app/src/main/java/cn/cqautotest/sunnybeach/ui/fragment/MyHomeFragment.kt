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
import cn.cqautotest.sunnybeach.util.onPageSelected
import cn.cqautotest.sunnybeach.util.onTabSelected
import cn.cqautotest.sunnybeach.viewmodel.home.HomeViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout

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
    private val mFragmentMap = arrayMapOf<Int, AppFragment<AppActivity>>()
    private lateinit var mFragmentAdapter: FragmentAdapter
    private val mRecommendFragment by lazy { ArticleListFragment() }
    private var mInitArticleCategoriesData = false

    override fun getLayoutId(): Int = R.layout.my_home_fragment

    override fun onBindingView() {
        _binding = MyHomeFragmentBinding.bind(view)
    }

    override fun onFragmentResume(first: Boolean) {
        super.onFragmentResume(first)
        // 如果不是首次加载，且没有初始化文章分类列表数据，则重新加载文章列表数据
        if (first.not() && mInitArticleCategoriesData.not()) {
            loadArticleCategoriesData()
        }
    }

    /**
     * 加载文章分类列表数据
     */
    private fun loadArticleCategoriesData() {
        mHomeViewModel.getCategories()
    }

    override fun initObserver() {
        val tabLayoutCategories = mBinding.tabLayoutCategories
        mHomeViewModel.homeCategories.observe(viewLifecycleOwner) { homeCategories ->
            if (mInitArticleCategoriesData) {
                return@observe
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
            mInitArticleCategoriesData = homeCategories.isNotEmpty()
            showComplete()
        }
    }

    override fun initEvent() {
        val tabLayoutCategories = mBinding.tabLayoutCategories
        val vp2HomeArticleContainer = mBinding.vp2HomeArticleContainer
        tabLayoutCategories.onTabSelected {
            val tab = it ?: return@onTabSelected
            val position = tab.position
            vp2HomeArticleContainer.setCurrentItem(position, false)
            if (mFragmentMap[position] == null) {
                showLoading()
            } else {
                showComplete()
            }
        }
        vp2HomeArticleContainer.onPageSelected { position ->
            val tab = tabLayoutCategories.getTabAt(position)
            tabLayoutCategories.selectTab(tab)
        }
    }

    override fun initData() {
        loadDefaultArticleData()
        loadArticleCategoriesData()
    }

    private fun loadDefaultArticleData() {
        val tabLayoutCategories = mBinding.tabLayoutCategories
        // 显示分类标签
        tabLayoutCategories.visibility = View.VISIBLE
        // 重置数据
        tabLayoutCategories.removeAllTabs()
        mFragmentMap.clear()
        tabLayoutCategories.newTab().apply {
            text = "推荐"
            tabLayoutCategories.addTab(this)
        }
        mFragmentMap[0] = mRecommendFragment.apply {
            title = "推荐"
        }
        mFragmentAdapter.setFragmentMap(mFragmentMap)
    }

    override fun initView() {
        mFragmentAdapter = FragmentAdapter(this)
        mBinding.vp2HomeArticleContainer.apply {
            adapter = mFragmentAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlMyHomeFragmentHint

    companion object {
        @JvmStatic
        fun newInstance(): AppFragment<*> {
            return MyHomeFragment()
        }
    }
}