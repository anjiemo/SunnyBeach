package cn.cqautotest.sunnybeach.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.aop.CheckNet
import cn.cqautotest.sunnybeach.aop.Log
import cn.cqautotest.sunnybeach.app.PagingActivity
import cn.cqautotest.sunnybeach.databinding.CourseDetailActivityBinding
import cn.cqautotest.sunnybeach.ktx.*
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.course.Course
import cn.cqautotest.sunnybeach.model.course.CourseChapter
import cn.cqautotest.sunnybeach.ui.adapter.CourseChapterListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.viewmodel.CourseViewModel
import com.dylanc.longan.intentExtras
import kotlinx.coroutines.flow.collectLatest

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
            mCourseChapterListAdapter.snapshotList[position]?.let {
                when (it) {
                    is CourseChapter.CourseChapterItem.Children -> PlayerActivity.start(this, it)
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