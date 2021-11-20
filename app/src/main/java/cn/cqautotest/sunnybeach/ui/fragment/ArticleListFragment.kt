package cn.cqautotest.sunnybeach.ui.fragment

import android.annotation.SuppressLint
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.TitleBarFragment
import cn.cqautotest.sunnybeach.databinding.ArticleListFragmentBinding
import cn.cqautotest.sunnybeach.ui.activity.BrowserActivity
import cn.cqautotest.sunnybeach.ui.activity.HomeActivity
import cn.cqautotest.sunnybeach.ui.adapter.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.ArticleAdapter
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_ARTICLE_URL_PRE
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.util.dp
import cn.cqautotest.sunnybeach.util.isEmpty
import cn.cqautotest.sunnybeach.viewmodel.ArticleViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/6/20
 * desc   : 首页 Fragment
 */
class ArticleListFragment : TitleBarFragment<HomeActivity>(), StatusAction, OnBack2TopListener {

    private val mBinding: ArticleListFragmentBinding by viewBinding()
    private val mArticleViewModel by activityViewModels<ArticleViewModel>()
    private val mAdapterDelegate = AdapterDelegate()
    private val mArticleAdapter = ArticleAdapter(mAdapterDelegate)
    private val loadStateListener = { cls: CombinedLoadStates ->
        when (cls.refresh) {
            is LoadState.NotLoading -> {
                mBinding.refreshLayout.finishRefresh()
                if (mArticleAdapter.isEmpty()) {
                    showEmpty()
                } else {
                    showComplete()
                }
            }
            is LoadState.Loading -> showLoading()
            is LoadState.Error -> showError { mArticleAdapter.refresh() }
        }
    }

    override fun getLayoutId(): Int = R.layout.article_list_fragment

    @SuppressLint("InflateParams")
    override fun initEvent() {
        mBinding.topLayout.setOnClickListener {

        }
        mBinding.refreshLayout.setOnRefreshListener {
            mArticleAdapter.refresh()
        }
        // 需要在 View 销毁的时候移除 listener
        mArticleAdapter.addLoadStateListener(loadStateListener)
        mAdapterDelegate.setOnItemClickListener { _, position ->
            val item = mArticleAdapter.snapshot()[position] ?: return@setOnItemClickListener
            val url = "$SUNNY_BEACH_ARTICLE_URL_PRE${item.id}"
            BrowserActivity.start(requireContext(), url)
        }
    }

    override fun initData() {
        loadArticleList()
    }

    private fun loadArticleList() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            mArticleViewModel.getArticleListByCategoryId("recommend").collectLatest {
                mArticleAdapter.submitData(it)
            }
        }
    }

    override fun initView() {
        mBinding.rvArticleList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mArticleAdapter
            addItemDecoration(SimpleLinearSpaceItemDecoration(4.dp))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mArticleAdapter.removeLoadStateListener(loadStateListener)
    }

    override fun getStatusLayout(): StatusLayout = mBinding.slArticleLayout

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

    override fun onBack2Top() {
        mBinding.rvArticleList.scrollToPosition(0)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ArticleListFragment()
    }
}