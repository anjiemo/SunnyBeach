package cn.cqautotest.sunnybeach.ui.activity

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.viewbinding.ViewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.aop.CheckNet
import cn.cqautotest.sunnybeach.aop.DebugLog
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppApplication
import cn.cqautotest.sunnybeach.databinding.ArticleDetailActivityBinding
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.util.markown.MyGrammarLocator
import cn.cqautotest.sunnybeach.viewmodel.home.HomeViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import io.noties.markwon.Markwon
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin
import io.noties.markwon.syntax.Prism4jThemeDarkula
import io.noties.markwon.syntax.SyntaxHighlightPlugin
import io.noties.prism4j.Prism4j

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/6/24
 * desc   : 文章详情界面
 */
class ArticleDetailActivity : AppActivity(), StatusAction, OnRefreshListener {

    private lateinit var mBinding: ArticleDetailActivityBinding
    private val mHomeViewModel by viewModels<HomeViewModel>()
    private lateinit var mStatusLayout: StatusLayout
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mRefreshLayout: SmartRefreshLayout
    private var mArticleId = ""
    private var mArticleTitle = ""

    override fun getLayoutId(): Int = 0

    override fun onBindingView(): ViewBinding {
        mBinding = ArticleDetailActivityBinding.inflate(layoutInflater)
        return mBinding
    }

    override fun initObserver() {
        mHomeViewModel.articleDetail.observe(this) { articleDetail ->
            if (articleDetail == null) {
                showEmpty()
                return@observe
            }
            showComplete()
            val articleContent = articleDetail.content
            val appContext = AppApplication.getInstance()
            val prism4jTheme =
                Prism4jThemeDarkula.create(Color.parseColor(getString(R.string.markdown_bg_code_color)))
            val markwon = Markwon.builder(appContext)
                // Html 插件
                .usePlugin(HtmlPlugin.create())
                // 语法高亮插件
                .usePlugin(SyntaxHighlightPlugin.create(Prism4j(MyGrammarLocator()), prism4jTheme))
                // Glide 插件
                .usePlugin(GlideImagesPlugin.create(appContext))
                .build()
            markwon.setMarkdown(mBinding.emptyDescription, articleContent ?: "")
        }
    }

    override fun initData() {
        showLoading()
        intent.run {
            mArticleId = getStringExtra(IntentKey.ID) ?: ""
            mArticleTitle = getStringExtra(IntentKey.TITLE) ?: ""
        }
        mBinding.titleBar.title = mArticleTitle
        loadArticleDetail()
    }

    override fun initView() {
        mStatusLayout = mBinding.hlArticleDetailHint
        mProgressBar = mBinding.pbBrowserProgress
        mRefreshLayout = mBinding.slArticleDetailRefresh
    }

    /**
     * 根据文章id加载文章详情
     */
    private fun loadArticleDetail() {
        mHomeViewModel.getArticleDetailById(mArticleId)
    }

    override fun getStatusLayout(): StatusLayout {
        return mStatusLayout
    }

    override fun onLeftClick(view: View?) {
        finish()
    }

    /**
     * [OnRefreshListener]
     */
    override fun onRefresh(refreshLayout: RefreshLayout) {
        loadArticleDetail()
    }

    companion object {

        /**
         * 文章id、文章标题
         */
        @CheckNet
        @DebugLog
        fun start(articleId: String?, articleTitle: String?) {
            val context = AppApplication.getInstance()
            val intent = Intent(context, ArticleDetailActivity::class.java)
            intent.run {
                putExtra(IntentKey.ID, articleId)
                putExtra(IntentKey.TITLE, articleTitle)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}