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
import cn.cqautotest.sunnybeach.ui.activity.HomeActivity
import cn.cqautotest.sunnybeach.ui.adapter.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.ArticleAdapter
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.util.dp
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
    private val mArticleAdapter = ArticleAdapter(AdapterDelegate())
    private val loadStateListener = { cls: CombinedLoadStates ->
        if (cls.refresh is LoadState.NotLoading) {
            showComplete()
            mBinding.refreshLayout.finishRefresh()
        }
        if (cls.refresh is LoadState.Loading) {
            showLoading()
        }
        if (cls.refresh is LoadState.Error) {
            showError {
                mArticleAdapter.refresh()
            }
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
        // 双击标题栏回到顶部，先滚动到第10条，然后平滑滚动到顶部
        if (mArticleAdapter.itemCount > 10) {
            mBinding.rvArticleList.scrollToPosition(10)
        }
        postDelayed({
            mBinding.rvArticleList.smoothScrollToPosition(0)
        }, 100)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ArticleListFragment()
    }
}