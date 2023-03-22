package cn.cqautotest.sunnybeach.ui.fragment

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.TitleBarFragment
import cn.cqautotest.sunnybeach.databinding.SearchListFragmentBinding
import cn.cqautotest.sunnybeach.ktx.*
import cn.cqautotest.sunnybeach.other.SearchType
import cn.cqautotest.sunnybeach.other.SortType
import cn.cqautotest.sunnybeach.ui.activity.BrowserActivity
import cn.cqautotest.sunnybeach.ui.adapter.SearchResultListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_ARTICLE_URL_PRE
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_QA_URL_PRE
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_SHARE_URL_PRE
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.viewmodel.SearchViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import com.dylanc.longan.safeArguments
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/05/11
 * desc   : 搜索列表 Fragment
 */
class SearchListFragment : TitleBarFragment<AppActivity>(), StatusAction, OnBack2TopListener {

    private val mBinding by viewBinding<SearchListFragmentBinding>()
    private val mSearchViewModel by activityViewModels<SearchViewModel>()
    private val mAdapterDelegate = AdapterDelegate()
    private val mSearchResultListAdapter = SearchResultListAdapter(mAdapterDelegate)
    private val loadStateListener = loadStateListener(mSearchResultListAdapter) { mBinding.refreshLayout.finishRefresh() }
    private val searchTypeJson by safeArguments<String>(SEARCH_TYPE)
    private val searchType by lazy { fromJson<SearchType>(searchTypeJson) }

    override fun getLayoutId(): Int = R.layout.search_list_fragment

    override fun initView() {
        mBinding.rvSearchList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mSearchResultListAdapter
            addItemDecoration(SimpleLinearSpaceItemDecoration(4.dp))
        }
    }

    override fun initData() {
        showEmpty()
    }

    override fun initEvent() {
        mBinding.refreshLayout.setOnRefreshListener { mSearchResultListAdapter.refresh() }
        // 需要在 View 销毁的时候移除 listener
        mSearchResultListAdapter.addLoadStateListener(loadStateListener)
        mAdapterDelegate.setOnItemClickListener { _, position ->
            // 跳转到搜索详情界面
            mSearchResultListAdapter.snapshotList.getOrNull(position)?.let {
                val url = when (SearchType.valueOfType(it.type)) {
                    SearchType.ARTICLE -> "$SUNNY_BEACH_ARTICLE_URL_PRE${it.id}"
                    SearchType.QA -> "$SUNNY_BEACH_QA_URL_PRE${it.id}"
                    SearchType.SHARE -> "$SUNNY_BEACH_SHARE_URL_PRE${it.id}"
                    else -> null
                }
                url?.let { BrowserActivity.start(requireContext(), url) }
            }
        }
    }

    override fun initObserver() {
        mSearchViewModel.keywordsLiveData.observe(viewLifecycleOwner) {
            loadSearchResultList(it)
        }
    }

    private fun loadSearchResultList(keywords: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                mSearchViewModel.searchByKeywords(keyword = keywords, searchType = searchType, sortType = SortType.NO_SORT).collectLatest {
                    mSearchResultListAdapter.submitData(it)
                }
            }
        }
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlSearchHint

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mSearchResultListAdapter.removeLoadStateListener(loadStateListener)
    }

    override fun onBack2Top() {
        mBinding.rvSearchList.scrollToPosition(0)
    }

    companion object {

        private const val SEARCH_TYPE = "search_type"

        fun newInstance(searchType: SearchType): SearchListFragment {
            val fragment = SearchListFragment()
            fragment.arguments = Bundle().apply {
                putString(SEARCH_TYPE, searchType.toJson())
            }
            return fragment
        }
    }
}