package cn.cqautotest.sunnybeach.ui.fragment.user.content

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.paging.PagingDataAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.PagingFragment
import cn.cqautotest.sunnybeach.databinding.UserArticleListFragmentBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.ktx.toJson
import cn.cqautotest.sunnybeach.model.ArticleSearchFilter
import cn.cqautotest.sunnybeach.model.RefreshStatus
import cn.cqautotest.sunnybeach.model.UserArticle
import cn.cqautotest.sunnybeach.ui.activity.BrowserActivity
import cn.cqautotest.sunnybeach.ui.activity.ImagePreviewActivity
import cn.cqautotest.sunnybeach.ui.adapter.UserArticleAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.dialog.ShareDialog
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_ARTICLE_URL_PRE
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.viewmodel.ArticleViewModel
import com.dylanc.longan.arguments
import com.hjq.umeng.Platform
import com.hjq.umeng.UmengShare
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/11/27
 * desc   : 用户文章列表管理 Fragment
 */
class UserArticleListManagerFragment : PagingFragment<AppActivity>() {

    private val mBinding by viewBinding<UserArticleListFragmentBinding>()
    private val mArticleViewModel by activityViewModels<ArticleViewModel>()
    private val mRefreshStatus = RefreshStatus()
    private val mAdapterDelegate = AdapterDelegate()
    private val mUserArticleAdapter = UserArticleAdapter(mAdapterDelegate)
    private val searchFilter by arguments(SEARCH_FILTER, ArticleSearchFilter())

    override fun getPagingAdapter(): PagingDataAdapter<*, *> = mUserArticleAdapter

    override fun getLayoutId(): Int = R.layout.published_article_list_fragment

    override fun initView() {
        super.initView()
        mBinding.pagingRecyclerView.addItemDecoration(SimpleLinearSpaceItemDecoration(4.dp))
    }

    override suspend fun loadListData() {
        mArticleViewModel.searchUserArticleList(searchFilter).collectLatest { mUserArticleAdapter.submitData(it) }
    }

    override fun initEvent() {
        super.initEvent()
        mAdapterDelegate.setOnItemClickListener { _, position ->
            // 跳转到文章详情界面
            mUserArticleAdapter.snapshotList.getOrNull(position)?.let {
                val url = "$SUNNY_BEACH_ARTICLE_URL_PRE${it.id}"
                BrowserActivity.start(requireContext(), url)
            }
        }
        mUserArticleAdapter.setOnMenuItemClickListener { view, item, _ ->
            when (view.id) {
                R.id.ll_share -> shareArticle(item)
            }
        }
        mUserArticleAdapter.setOnNineGridClickListener { sources, index ->
            ImagePreviewActivity.start(requireContext(), sources.toMutableList(), index)
        }
    }

    private fun shareArticle(item: UserArticle.UserArticleItem) {
        val articleId = item.id
        val content = UMWeb(SUNNY_BEACH_ARTICLE_URL_PRE + articleId)
        content.title = item.title
        content.setThumb(UMImage(requireContext(), R.mipmap.launcher_ic))
        content.description = getString(R.string.app_name)
        // 分享
        ShareDialog.Builder(requireActivity())
            .setShareLink(content)
            .setListener(object : UmengShare.OnShareListener {
                override fun onSucceed(platform: Platform?) {
                    toast("分享成功")
                }

                override fun onError(platform: Platform?, t: Throwable) {
                    toast(t.message)
                }

                override fun onCancel(platform: Platform?) {
                    toast("分享取消")
                }
            })
            .show()
    }

    override fun showLoading(id: Int) {
        takeIf { mRefreshStatus.isFirstRefresh }?.let { super.showLoading(id) }
        mRefreshStatus.isFirstRefresh = false
    }

    companion object {

        private const val SEARCH_FILTER = "SEARCH_FILTER"

        fun newInstance(articleSearchFilter: ArticleSearchFilter = ArticleSearchFilter()): UserArticleListManagerFragment {
            val args = Bundle().apply {
                putString(SEARCH_FILTER, articleSearchFilter.toJson())
            }
            val fragment = UserArticleListManagerFragment()
            fragment.arguments = args
            return fragment
        }
    }
}