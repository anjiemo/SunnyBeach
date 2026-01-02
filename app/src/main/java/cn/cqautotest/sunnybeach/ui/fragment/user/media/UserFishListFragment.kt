package cn.cqautotest.sunnybeach.ui.fragment.user.media

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.PagingFragment
import cn.cqautotest.sunnybeach.databinding.UserFishListFragmentBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.model.Fish
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.activity.FishPondDetailActivity
import cn.cqautotest.sunnybeach.ui.activity.ImagePreviewActivity
import cn.cqautotest.sunnybeach.ui.adapter.FishListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.util.MultiOperationHelper
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import cn.cqautotest.sunnybeach.widget.recyclerview.UniversalSpaceDecoration
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/31
 * desc   : 用户摸鱼列表 Fragment
 */
@AndroidEntryPoint
class UserFishListFragment : PagingFragment<AppActivity>() {

    private val mBinding by viewBinding(UserFishListFragmentBinding::bind)
    private val mFishPondViewModel by activityViewModels<FishPondViewModel>()

    @Inject
    lateinit var mMultiOperationHelper: MultiOperationHelper
    private val mAdapterDelegate = AdapterDelegate()
    private val mFishListAdapter = FishListAdapter(mAdapterDelegate)

    override fun getPagingAdapter() = mFishListAdapter

    override fun getLayoutId(): Int = R.layout.user_fish_list_fragment

    override fun initView() {
        super.initView()
        mBinding.pagingRecyclerView.addItemDecoration(UniversalSpaceDecoration(mainSpace = 4.dp, crossSpace = 0))
    }

    override suspend fun loadListData() {
        mFishPondViewModel.userFishFlow
            .flowWithLifecycle(lifecycle)
            .collectLatest { mFishListAdapter.submitData(it) }
    }

    override fun initEvent() {
        super.initEvent()
        mAdapterDelegate.setOnItemClickListener { _, position ->
            mFishListAdapter.snapshotList.getOrNull(position)?.let { FishPondDetailActivity.start(requireContext(), it.id) }
        }
        mFishListAdapter.setOnNineGridClickListener { sources, index ->
            ImagePreviewActivity.start(requireContext(), sources.toMutableList(), index)
        }
        mFishListAdapter.setOnMenuItemClickListener { view, item, position ->
            when (view.id) {
                R.id.ll_share -> shareFish(item)
                R.id.ll_great -> dynamicLikes(item, position)
            }
        }
    }

    private fun dynamicLikes(item: Fish.FishItem, position: Int) {
        mMultiOperationHelper.dynamicLikes(viewLifecycleOwner, mFishPondViewModel, mFishListAdapter, item, position)
    }

    private fun shareFish(item: Fish.FishItem) {
        mMultiOperationHelper.shareFish(item.id)
    }

    companion object {

        @JvmStatic
        fun newInstance(userId: String): UserFishListFragment {
            val fragment = UserFishListFragment()
            val args = Bundle().apply {
                putString(IntentKey.ID, userId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}