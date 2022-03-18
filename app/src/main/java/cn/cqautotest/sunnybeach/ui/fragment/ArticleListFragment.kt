package cn.cqautotest.sunnybeach.ui.fragment

import android.annotation.SuppressLint
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.TitleBarFragment
import cn.cqautotest.sunnybeach.databinding.ArticleListFragmentBinding
import cn.cqautotest.sunnybeach.model.ArticleInfo
import cn.cqautotest.sunnybeach.ui.activity.BrowserActivity
import cn.cqautotest.sunnybeach.ui.activity.HomeActivity
import cn.cqautotest.sunnybeach.ui.activity.ImagePreviewActivity
import cn.cqautotest.sunnybeach.ui.adapter.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.ArticleAdapter
import cn.cqautotest.sunnybeach.ui.dialog.ShareDialog
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_ARTICLE_URL_PRE
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.util.dp
import cn.cqautotest.sunnybeach.util.loadStateListener
import cn.cqautotest.sunnybeach.viewmodel.ArticleViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import com.hjq.umeng.Platform
import com.hjq.umeng.UmengShare
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
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
    private val loadStateListener = loadStateListener(mArticleAdapter) {
        mBinding.refreshLayout.finishRefresh()
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
        mArticleAdapter.setOnMenuItemClickListener { view, item, _ ->
            when (view.id) {
                R.id.ll_share -> shareArticle(item)
            }
        }
        mArticleAdapter.setOnNineGridClickListener { sources, index ->
            ImagePreviewActivity.start(requireContext(), sources, index)
        }
    }

    private fun shareArticle(item: ArticleInfo.ArticleItem) {
        val articleId = item.id
        val content = UMWeb(SUNNY_BEACH_ARTICLE_URL_PRE + articleId)
        content.title = item.title
        content.setThumb(UMImage(requireContext(), R.mipmap.launcher_ic))
        content.description = getString(R.string.app_name)
        // 分享
        ShareDialog.Builder(requireActivity())
            .setShareLink(content)
            .setListener(object : UmengShare.OnShareListener {
                override fun onSucceed(platform: Platform) {
                    toast("分享成功")
                }

                override fun onError(platform: Platform, t: Throwable) {
                    toast(t.message)
                }

                override fun onCancel(platform: Platform) {
                    toast("分享取消")
                }
            })
            .show()
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