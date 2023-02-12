package cn.cqautotest.sunnybeach.ui.activity

import androidx.activity.viewModels
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.PagingActivity
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.model.RefreshStatus
import cn.cqautotest.sunnybeach.model.UserArticle
import cn.cqautotest.sunnybeach.ui.adapter.UserArticleAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.dialog.ShareDialog
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_ARTICLE_URL_PRE
import cn.cqautotest.sunnybeach.viewmodel.ArticleViewModel
import com.hjq.umeng.Platform
import com.hjq.umeng.UmengShare
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/07/17
 * desc   : 热门（小默）文章列表界面
 */
class HotArticleListActivity : PagingActivity() {

    private val mHotArticleViewModel by viewModels<ArticleViewModel>()
    private val mRefreshStatus = RefreshStatus()
    private val mAdapterDelegate = AdapterDelegate()
    private val mHotArticleAdapter = UserArticleAdapter(mAdapterDelegate)

    override fun getPagingAdapter() = mHotArticleAdapter

    override fun getLayoutId() = R.layout.hot_article_list_activity

    override suspend fun loadListData() {
        mHotArticleViewModel.getUserArticleList("1204736502274318336").collectLatest {
            mHotArticleAdapter.submitData(it)
        }
    }

    override fun initEvent() {
        super.initEvent()
        mAdapterDelegate.setOnItemClickListener { _, position ->
            mHotArticleAdapter.snapshotList[position]?.let {
                val url = SUNNY_BEACH_ARTICLE_URL_PRE + it.id
                BrowserActivity.start(this, url)
            }
        }
        mHotArticleAdapter.setOnMenuItemClickListener { view, item, _ ->
            when (view.id) {
                R.id.ll_share -> shareArticle(item)
                // R.id.ll_great -> articleLikes(item, position)
            }
        }
        mHotArticleAdapter.setOnNineGridClickListener { sources, index ->
            ImagePreviewActivity.start(this, sources.toMutableList(), index)
        }
    }

    private fun shareArticle(item: UserArticle.UserArticleItem) {
        val articleId = item.id
        val content = UMWeb(SUNNY_BEACH_ARTICLE_URL_PRE + articleId)
        content.title = item.title
        content.setThumb(UMImage(this, R.mipmap.launcher_ic))
        content.description = getString(R.string.app_name)
        // 分享
        ShareDialog.Builder(this)
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
}