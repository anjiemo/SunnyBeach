package cn.cqautotest.sunnybeach.ui.fragment

import androidx.collection.arrayMapOf
import androidx.fragment.app.viewModels
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.app.FragmentAdapter
import cn.cqautotest.sunnybeach.databinding.MyHomeFragmentBinding
import cn.cqautotest.sunnybeach.ui.activity.HomeActivity
import cn.cqautotest.sunnybeach.ui.fragment.home.ArticleListFragment
import cn.cqautotest.sunnybeach.utils.onPageSelected
import cn.cqautotest.sunnybeach.utils.onTabSelected
import cn.cqautotest.sunnybeach.viewmodel.home.HomeViewModel

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/6/20
 * desc   : 首页 Fragment
 */
class MyHomeFragment : AppFragment<HomeActivity>() {

    private var _binding: MyHomeFragmentBinding? = null
    private val mBinding get() = _binding!!
    private val homeViewModel by viewModels<HomeViewModel>()
    private val mFragmentMap by lazy { arrayMapOf<Int, AppFragment<AppActivity>>() }
    private lateinit var mFragmentAdapter: FragmentAdapter

    override fun getLayoutId(): Int = R.layout.my_home_fragment

    override fun onBindingView() {
        _binding = MyHomeFragmentBinding.bind(view)
    }

    override fun initView() {
        _binding = MyHomeFragmentBinding.bind(view)
        mFragmentAdapter = FragmentAdapter(this)
        mBinding.vp2HomeArticleContainer.apply {
            // TODO: 2021/6/20 暂时不允许左右滑动，因为ViewPager2的滑动比较灵敏，后期处理之后改为可以左右滑动
            isUserInputEnabled = false
            adapter = mFragmentAdapter
        }
    }

    override fun initData() {
        homeViewModel.getCategories()
    }

    override fun initEvent() {
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
    }

    override fun initObserver() {
        lifecycle.addObserver(homeViewModel)
        val tabLayoutCategories = mBinding.tabLayoutCategories
        val vp2HomeArticleContainer = mBinding.vp2HomeArticleContainer
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        lifecycle.removeObserver(homeViewModel)
    }

    companion object {
        @JvmStatic
        fun newInstance(): MyHomeFragment {
            return MyHomeFragment()
        }
    }
}