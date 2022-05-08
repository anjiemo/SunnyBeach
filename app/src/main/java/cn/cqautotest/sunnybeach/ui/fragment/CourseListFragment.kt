package cn.cqautotest.sunnybeach.ui.fragment

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.TitleBarFragment
import cn.cqautotest.sunnybeach.databinding.CourseListFragmentBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.loadStateListener
import cn.cqautotest.sunnybeach.ktx.setDoubleClickListener
import cn.cqautotest.sunnybeach.other.GridSpaceDecoration
import cn.cqautotest.sunnybeach.ui.activity.CourseDetailActivity
import cn.cqautotest.sunnybeach.ui.adapter.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.CourseListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.EmptyAdapter
import cn.cqautotest.sunnybeach.viewmodel.CourseViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/22
 * desc   : 课程列表 Fragment
 */
@AndroidEntryPoint
class CourseListFragment : TitleBarFragment<AppActivity>(), StatusAction, OnBack2TopListener {

    private val mBinding: CourseListFragmentBinding by viewBinding()

    private val mCourseViewModel by activityViewModels<CourseViewModel>()
    private val mCourseListAdapter = CourseListAdapter(AdapterDelegate())
    private val loadStateListener = loadStateListener(mCourseListAdapter) {
        mBinding.refreshLayout.finishRefresh()
    }

    override fun getLayoutId(): Int = R.layout.course_list_fragment

    override fun initView() {
        // This emptyAdapter is like a hacker.
        // Its existence allows the PagingAdapter to scroll to the top before being refreshed,
        // avoiding the problem that the PagingAdapter cannot return to the top after being refreshed.
        // But it needs to be used in conjunction with ConcatAdapter, and must appear before PagingAdapter.
        val emptyAdapter = EmptyAdapter(2)
        val concatAdapter = ConcatAdapter(emptyAdapter, mCourseListAdapter)
        mBinding.rvCourseList.apply {
            val spanCount = 2
            layoutManager = GridLayoutManager(context, spanCount)
            adapter = concatAdapter
            addItemDecoration(GridSpaceDecoration(6.dp))
        }
    }

    override fun initData() {
        loadCourseList()
    }

    private fun loadCourseList() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            mCourseViewModel.getCourseList().collectLatest {
                onBack2Top()
                mCourseListAdapter.submitData(it)
            }
        }
    }

    override fun initEvent() {
        mBinding.titleBar.setDoubleClickListener {
            onBack2Top()
        }
        mBinding.refreshLayout.setOnRefreshListener {
            mCourseListAdapter.refresh()
        }
        // 需要在 View 销毁的时候移除 listener
        mCourseListAdapter.addLoadStateListener(loadStateListener)
        mCourseListAdapter.setOnItemClickListener { item, _ ->
            CourseDetailActivity.start(requireContext(), item)
        }
    }

    override fun initObserver() {}

    override fun getStatusLayout(): StatusLayout = mBinding.hlCourseHint

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mCourseListAdapter.removeLoadStateListener(loadStateListener)
    }

    override fun onBack2Top() {
        mBinding.rvCourseList.scrollToPosition(0)
    }

    companion object {

        @JvmStatic
        fun newInstance() = CourseListFragment()
    }
}