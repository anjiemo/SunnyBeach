package cn.cqautotest.sunnybeach.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.aop.CheckNet
import cn.cqautotest.sunnybeach.aop.Log
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.CourseDetailActivityBinding
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.course.Course
import cn.cqautotest.sunnybeach.util.*
import cn.cqautotest.sunnybeach.viewmodel.CourseViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import com.dylanc.longan.intentExtras

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/23
 * desc   : 课程详情页
 */
class CourseDetailActivity : AppActivity(), StatusAction {

    private val mBinding by viewBinding<CourseDetailActivityBinding>()
    private val mCourseViewModel by viewModels<CourseViewModel>()
    private val courseItemJson by intentExtras<String>(COURSE_ITEM)
    private val courseItem by lazy { fromJson<Course.CourseItem>(courseItemJson) }
    // private val mCourseChapterListAdapter = CourseChapterListAdapter()

    override fun getLayoutId(): Int = R.layout.course_detail_activity

    @SuppressLint("SetTextI18n")
    override fun initView() {
        val price = courseItem.price
        mBinding.tvBuyCourse.text = if (price.isZero) "已购买" else "购买 ¥ ${courseItem.price}"
        mBinding.rvCourseDetailList.apply {
            layoutManager = LinearLayoutManager(context)
            // adapter = mCourseChapterListAdapter
            addItemDecoration(SimpleLinearSpaceItemDecoration(6.dp))
        }
    }

    override fun initData() {
        loadCourseList()
    }

    private fun loadCourseList() {
        showLoading()
        val courseId = courseItem.id
        mCourseViewModel.getCourseChapterList(courseId).observe(this) {
            val courseChapterList = it.getOrElse {
                showError { loadCourseList() }
                return@observe
            }
            if (courseChapterList.isNullOrEmpty()) showEmpty() else showComplete()
            // mCourseChapterListAdapter.setData(courseChapterList)
        }
    }

    override fun initEvent() {
        mBinding.tvVipFree.setFixOnClickListener {
            checkCanStudy()
        }
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

    override fun getStatusLayout(): StatusLayout = mBinding.hlCourseDetailHint

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
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