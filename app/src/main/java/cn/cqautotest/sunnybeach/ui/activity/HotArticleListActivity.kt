package cn.cqautotest.sunnybeach.ui.activity

import androidx.activity.viewModels
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.PagingActivity
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.ui.adapter.UserArticleAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_ARTICLE_URL_PRE
import cn.cqautotest.sunnybeach.viewmodel.ArticleViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/07/17
 * desc   : 热门（小默）文章列表界面
 */
class HotArticleListActivity : PagingActivity() {

    private val mHotArticleViewModel by viewModels<ArticleViewModel>()
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
    }
}