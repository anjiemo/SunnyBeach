package cn.cqautotest.sunnybeach.ui.fragment

import androidx.fragment.app.activityViewModels
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.PagingTitleBarFragment
import cn.cqautotest.sunnybeach.databinding.CourseListFragmentBinding
import cn.cqautotest.sunnybeach.ktx.context
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.ktx.toJson
import cn.cqautotest.sunnybeach.model.RefreshStatus
import cn.cqautotest.sunnybeach.model.course.Course
import cn.cqautotest.sunnybeach.ui.activity.CourseDetailActivity
import cn.cqautotest.sunnybeach.ui.adapter.CourseBannerAdapter
import cn.cqautotest.sunnybeach.ui.adapter.CourseListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.EmptyAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.viewmodel.CourseViewModel
import cn.cqautotest.sunnybeach.widget.recyclerview.StaggeredGridSpaceDecoration
import com.youth.banner.indicator.CircleIndicator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/22
 * desc   : 课程列表 Fragment
 */
@AndroidEntryPoint
class CourseListFragment : PagingTitleBarFragment<AppActivity>() {

    private val mBinding: CourseListFragmentBinding by viewBinding()
    private val mCourseViewModel by activityViewModels<CourseViewModel>()
    private val mRefreshStatus = RefreshStatus()
    private val mCourseBannerAdapter = CourseBannerAdapter()
    private val mAdapterDelegate = AdapterDelegate()
    private val mCourseListAdapter = CourseListAdapter(mAdapterDelegate)

    override fun getPagingAdapter() = mCourseListAdapter

    override fun getLayoutId(): Int = R.layout.course_list_fragment

    override fun initView() {
        super.initView()
        // This emptyAdapter is like a hacker.
        // Its existence allows the PagingAdapter to scroll to the top before being refreshed,
        // avoiding the problem that the PagingAdapter cannot return to the top after being refreshed.
        // But it needs to be used in conjunction with ConcatAdapter, and must appear before PagingAdapter.
        val emptyAdapter = EmptyAdapter(2)
        val concatAdapter = ConcatAdapter(emptyAdapter, mCourseListAdapter)
        mBinding.apply {
            bannerCourse.setAdapter(mCourseBannerAdapter)
                .addBannerLifecycleObserver(viewLifecycleOwner).indicator = CircleIndicator(context)

            pagingRecyclerView.apply {
                val spanCount = 2
                layoutManager = StaggeredGridLayoutManager(spanCount, RecyclerView.VERTICAL)
                adapter = concatAdapter
                addItemDecoration(StaggeredGridSpaceDecoration(6.dp))
            }
        }
        mCourseListAdapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.NotLoading -> {
                    val courseBannerData = mCourseListAdapter.snapshotList.take(5)
                    Timber.d("addLoadStateListener：===> courseBannerData is ${courseBannerData.toJson()}")
                    mBinding.bannerCourse.setDatas(courseBannerData)
                }

                else -> {}
            }
        }
    }

    override suspend fun loadListData() {
        mCourseViewModel.courseListFlow.collectLatest {
            mCourseListAdapter.submitData(it)
        }
    }

    override fun initEvent() {
        super.initEvent()
        mBinding.apply {
            bannerCourse.setOnBannerListener { data, _ ->
                (data as? Course.CourseItem)?.let { CourseDetailActivity.start(requireContext(), it) }
            }
        }
        mAdapterDelegate.setOnItemClickListener { _, position ->
            mCourseListAdapter.snapshotList.getOrNull(position)?.let { CourseDetailActivity.start(requireContext(), it) }
        }
    }

    override fun showLoading(id: Int) {
        takeIf { mRefreshStatus.isFirstRefresh }?.let { super.showLoading(id) }
        mRefreshStatus.isFirstRefresh = false
    }

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

    companion object {

        @JvmStatic
        fun newInstance() = CourseListFragment()
    }
}