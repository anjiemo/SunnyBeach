package cn.cqautotest.sunnybeach.ui.fragment

import androidx.fragment.app.activityViewModels
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.PagingTitleBarFragment
import cn.cqautotest.sunnybeach.databinding.CourseListFragmentBinding
import cn.cqautotest.sunnybeach.ktx.*
import cn.cqautotest.sunnybeach.model.RefreshStatus
import cn.cqautotest.sunnybeach.model.course.Course
import cn.cqautotest.sunnybeach.other.GridSpaceDecoration
import cn.cqautotest.sunnybeach.ui.activity.CourseDetailActivity
import cn.cqautotest.sunnybeach.ui.adapter.CourseBannerAdapter
import cn.cqautotest.sunnybeach.ui.adapter.CourseListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.EmptyAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.viewmodel.CourseViewModel
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
class CourseListFragment : PagingTitleBarFragment<AppActivity>(), OnBack2TopListener {

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
                layoutManager = GridLayoutManager(context, spanCount)
                adapter = concatAdapter
                addItemDecoration(GridSpaceDecoration(6.dp))
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
        mCourseViewModel.getCourseList().collectLatest {
            onBack2Top()
            mCourseListAdapter.submitData(it)
        }
    }

    override fun initEvent() {
        super.initEvent()
        mBinding.apply {
            titleBar.setDoubleClickListener { onBack2Top() }
            bannerCourse.setOnBannerListener { data, _ ->
                (data as? Course.CourseItem)?.let { CourseDetailActivity.start(requireContext(), it) }
            }
        }
        mAdapterDelegate.setOnItemClickListener { _, position ->
            mCourseListAdapter.snapshotList[position]?.let { CourseDetailActivity.start(requireContext(), it) }
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

    override fun onBack2Top() {
        mBinding.pagingRecyclerView.scrollToPosition(0)
    }

    companion object {

        @JvmStatic
        fun newInstance() = CourseListFragment()
    }
}