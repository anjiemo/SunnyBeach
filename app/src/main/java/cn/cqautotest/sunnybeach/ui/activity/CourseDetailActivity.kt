package cn.cqautotest.sunnybeach.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.aop.CheckNet
import cn.cqautotest.sunnybeach.aop.Log
import cn.cqautotest.sunnybeach.app.PagingActivity
import cn.cqautotest.sunnybeach.databinding.CourseDetailActivityBinding
import cn.cqautotest.sunnybeach.execption.NotBuyException
import cn.cqautotest.sunnybeach.execption.NotLoginException
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.http.network.CourseNetwork
import cn.cqautotest.sunnybeach.http.network.Repository
import cn.cqautotest.sunnybeach.ktx.*
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.course.Course
import cn.cqautotest.sunnybeach.model.course.CourseChapter
import cn.cqautotest.sunnybeach.ui.adapter.CourseChapterListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.viewmodel.CourseViewModel
import cn.cqautotest.sunnybeach.widget.recyclerview.SimpleLinearSpaceItemDecoration
import com.bumptech.glide.Glide
import com.dylanc.longan.intentExtras
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/23
 * desc   : 课程详情页
 */
class CourseDetailActivity : PagingActivity() {

    private val mBinding by viewBinding<CourseDetailActivityBinding>()
    private val mCourseViewModel by viewModels<CourseViewModel>()
    private val courseItemJson by intentExtras<String>(COURSE_ITEM)
    private val courseItem by lazy { fromJson<Course.CourseItem>(courseItemJson) }
    private val mAdapterDelegate = AdapterDelegate()
    private val mCourseChapterListAdapter = CourseChapterListAdapter(mAdapterDelegate)

    override fun getPagingAdapter() = mCourseChapterListAdapter

    override fun getLayoutId(): Int = R.layout.course_detail_activity

    @SuppressLint("SetTextI18n")
    override fun initView() {
        super.initView()
        Glide.with(this)
            .load(courseItem.cover)
            .into(mBinding.ivCover)
        mBinding.pagingRecyclerView.addItemDecoration(SimpleLinearSpaceItemDecoration(1.dp))
        val price = courseItem.price
        mBinding.tvBuyCourse.text = if (price.isZero) "已购买" else "购买 ¥ ${courseItem.price}"
    }

    override suspend fun loadListData() {
        val courseId = courseItem.id
        mCourseViewModel.getCourseChapterList(courseId).collectLatest { mCourseChapterListAdapter.submitData(it) }
    }

    override fun initEvent() {
        super.initEvent()
        mAdapterDelegate.setOnItemClickListener { _, position ->
            mCourseChapterListAdapter.snapshotList.getOrNull(position)?.let {
                when (it) {
                    is CourseChapter.CourseChapterItem.Children -> playCourseVideo(it.courseId, it.id, it.title)
                    else -> {
                        /* do nothing */
                    }
                }
            }
        }
        mBinding.tvVipFree.setFixOnClickListener { checkCanStudy() }
        mBinding.tvBuyCourse.setFixOnClickListener {
            val price = courseItem.price
            if (price.isZero) {
                goToStudy()
            } else {
                showPayDialog()
            }
        }
    }

    private fun playCourseVideo(courseId: String, videoId: String, videoTitle: String) {
        lifecycleScope.launchWhenCreated {
            flow {
                // 1、先校验是否有登录
                Repository.checkToken() ?: throw NotLoginException()
                // 2、校验是否有购买该课程
                Repository.checkCourseHasBuy(courseId).getOrThrow()
                // 3、获取课程视频播放凭证
                val result = CourseNetwork.getCoursePlayAuth(videoId)
                val coursePlayAuth = result.getOrNull() ?: throw ServiceException(result.getMessage())
                emit(coursePlayAuth)
            }.flowOn(Dispatchers.IO)
                .catch {
                    Timber.e(it)
                    when (it) {
                        is NotLoginException -> tryShowLoginDialog()
                        // 可以提示并引导用户去购买课程
                        is NotBuyException -> toast(it.message)
                        else -> toast(it.message)
                    }
                }.collectLatest {
                    PlayerActivity.start(this@CourseDetailActivity, it, videoTitle)
                }
        }
    }

    private fun showPayDialog() {

    }

    private fun checkCanStudy() {
        val currIsVip = UserManager.currUserIsVip()
        if (currIsVip) {
            goToStudy()
        } else {
            toast("您还未开通会员")
        }
    }

    private fun goToStudy() {

    }

    companion object {

        private const val COURSE_ITEM = "courseItem"

        @CheckNet
        @Log
        fun start(context: Context, courseItem: Course.CourseItem) {
            context.startActivity<CourseDetailActivity> {
                putExtra(COURSE_ITEM, courseItem.toJson())
            }
        }
    }
}