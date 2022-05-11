package cn.cqautotest.sunnybeach.ui.activity

import android.widget.SearchView
import androidx.activity.viewModels
import androidx.viewpager.widget.ViewPager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.databinding.SearchActivityBinding
import cn.cqautotest.sunnybeach.ktx.clearTooltipText
import cn.cqautotest.sunnybeach.ktx.hideKeyboard
import cn.cqautotest.sunnybeach.other.SearchType
import cn.cqautotest.sunnybeach.ui.fragment.SearchListFragment
import cn.cqautotest.sunnybeach.viewmodel.SearchViewModel
import com.hjq.base.FragmentPagerAdapter

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/05/11
 * desc   : 搜索界面
 */
class SearchActivity : AppActivity(), SearchView.OnQueryTextListener {

    private val mBinding by viewBinding<SearchActivityBinding>()
    private val mSearchViewModel by viewModels<SearchViewModel>()
    private val mPagerAdapter by lazy { FragmentPagerAdapter<AppFragment<AppActivity>>(this) }

    override fun getLayoutId(): Int = R.layout.search_activity

    override fun initView() {
        val tabLayout = mBinding.tabLayout
        val viewPager = mBinding.viewPager
        viewPager.apply {
            adapter = mPagerAdapter
            mPagerAdapter.startUpdate(this)
        }
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun initData() {
        mPagerAdapter.apply {
            addFragment(SearchListFragment.newInstance(SearchType.ALL), "全部")
            addFragment(SearchListFragment.newInstance(SearchType.ARTICLE), "文章")
            addFragment(SearchListFragment.newInstance(SearchType.QA), "问答")
            addFragment(SearchListFragment.newInstance(SearchType.SHARE), "分享")
        }
        val tabLayout = mBinding.tabLayout
        tabLayout.clearTooltipText()
    }

    override fun initEvent() {
        val searchView = mBinding.searchView
        searchView.setOnQueryTextListener(this)
        mBinding.viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                mSearchViewModel.setKeywords(mSearchViewModel.keywordsLiveData.value ?: "")
            }
        })
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        newText?.let { mSearchViewModel.setKeywords(it) }
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query.isNullOrEmpty()) {
            toast("关键字不能为空哦~")
            return true
        }
        mSearchViewModel.setKeywords(query)
        hideKeyboard()
        return true
    }
}