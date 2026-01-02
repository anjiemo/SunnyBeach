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
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.activity.BrowserActivity
import cn.cqautotest.sunnybeach.ui.adapter.ShareListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_SHARE_URL_PRE
import cn.cqautotest.sunnybeach.viewmodel.ShareViewModel
import cn.cqautotest.sunnybeach.widget.recyclerview.UniversalSpaceDecoration
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/12
 * desc   : 用户分享列表 Fragment
 */
class UserShareListFragment : PagingFragment<AppActivity>() {

    private val mBinding by viewBinding(UserFishListFragmentBinding::bind)
    private val mAdapterDelegate = AdapterDelegate()
    private val mShareListAdapter = ShareListAdapter(mAdapterDelegate)
    private val mShareViewModel by activityViewModels<ShareViewModel>()

    override fun getPagingAdapter() = mShareListAdapter

    override fun getLayoutId(): Int = R.layout.user_fish_list_fragment

    override fun initView() {
        super.initView()
        mBinding.pagingRecyclerView.addItemDecoration(UniversalSpaceDecoration(mainSpace = 4.dp, crossSpace = 0))
    }

    override suspend fun loadListData() {
        mShareViewModel.userShareListFlow
            .flowWithLifecycle(lifecycle)
            .collectLatest {
                mShareListAdapter.submitData(it)
            }
    }

    override fun initEvent() {
        super.initEvent()
        mAdapterDelegate.setOnItemClickListener { _, position ->
            // 跳转到分享详情界面
            mShareListAdapter.snapshotList.getOrNull(position)?.let {
                val url = "$SUNNY_BEACH_SHARE_URL_PRE${it.id}"
                BrowserActivity.start(requireContext(), url)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(userId: String): UserShareListFragment {
            val fragment = UserShareListFragment()
            val args = Bundle().apply {
                putString(IntentKey.ID, userId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}