package cn.cqautotest.sunnybeach.ui.fragment.fishpond

import android.graphics.Rect
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.databinding.FishPondListFragmentBinding
import cn.cqautotest.sunnybeach.model.Fish
import cn.cqautotest.sunnybeach.ui.adapter.FishPondListAdapter
import cn.cqautotest.sunnybeach.utils.dp
import cn.cqautotest.sunnybeach.utils.equilibriumAssignmentOfLinear
import cn.cqautotest.sunnybeach.utils.logByDebug
import cn.cqautotest.sunnybeach.utils.setRoundRectBg
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/7/11
 * desc   : 摸鱼列表 Fragment
 */
class FishPondListFragment : AppFragment<AppActivity>(), StatusAction {

    private var _binding: FishPondListFragmentBinding? = null
    private val mBinding get() = _binding!!
    private val fishPondViewModel by viewModels<FishPondViewModel>()
    private val mFishPondListAdapter = FishPondListAdapter()

    var title: String = ""
    var topicId: String = ""

    override fun getLayoutId(): Int = R.layout.fish_pond_list_fragment

    override fun onBindingView() {
        _binding = FishPondListFragmentBinding.bind(view)
    }

    override fun onFragmentResume(first: Boolean) {
        super.onFragmentResume(first)
        if (first) {
            // 只有第一次可见的时候才加载数据，懒加载
            refreshFishPondData()
            logByDebug(msg = "initData：===> title is $title topicId is $topicId")
        }
    }

    private fun refreshFishPondData() {
        fishPondViewModel.loadFishPondListById(topicId)
    }

    override fun initView() {
        mBinding.rvFishPondList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mFishPondListAdapter
            addItemDecoration(object : RecyclerView.ItemDecoration() {

                // 单位间距（实际间距的一半）
                private val unit = 4.dp

                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    equilibriumAssignmentOfLinear(unit, outRect, view, parent)
                    view.setRoundRectBg(cornerRadius = 16.dp)
                }
            })
        }
    }

    override fun initData() {
        showLoading()
    }

    override fun initObserver() {
        fishPondViewModel.fishPond.observe(this) { fishPondList ->
            setupFishPondList(fishPondList)
        }
    }

    private fun setupFishPondList(fish: Fish?) {
        val fishPondList = if (fish == null) {
            showEmpty()
            return
        } else {
            fish.list
        }
        if (fishPondList.isNullOrEmpty()) {
            showEmpty()
            return
        }
        logByDebug(msg = "setupFishPondList：===> fishPondList size is $fishPondList")
        mFishPondListAdapter.setList(fishPondList)
        showComplete()
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlFishPondListHint

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}