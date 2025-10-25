package cn.cqautotest.sunnybeach.ui.fragment.user.media

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.PagingFragment
import cn.cqautotest.sunnybeach.databinding.UserFishListFragmentBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.activity.BrowserActivity
import cn.cqautotest.sunnybeach.ui.adapter.UserQaListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_QA_URL_PRE
import cn.cqautotest.sunnybeach.viewmodel.QaViewModel
import cn.cqautotest.sunnybeach.widget.recyclerview.SimpleLinearSpaceItemDecoration
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/31
 * desc   : 用户回答列表 Fragment
 */
class UserQaListFragment : PagingFragment<AppActivity>() {

    private val mBinding by viewBinding(UserFishListFragmentBinding::bind)
    private val mQaViewModel by activityViewModels<QaViewModel>()
    private val mAdapterDelegate = AdapterDelegate()
    private val mUserQaListAdapter = UserQaListAdapter(mAdapterDelegate)

    override fun getPagingAdapter() = mUserQaListAdapter

    override fun getLayoutId(): Int = R.layout.user_fish_list_fragment

    override fun initView() {
        super.initView()
        mBinding.pagingRecyclerView.addItemDecoration(SimpleLinearSpaceItemDecoration(4.dp))
    }

    override suspend fun loadListData() {
        mQaViewModel.userQaListFlow.collectLatest {
            mUserQaListAdapter.submitData(it)
        }
    }

    override fun initEvent() {
        super.initEvent()
        mAdapterDelegate.setOnItemClickListener { _, position ->
            // 跳转到问答详情界面
            mUserQaListAdapter.snapshotList.getOrNull(position)?.let {
                val url = "$SUNNY_BEACH_QA_URL_PRE${it.wendaComment.wendaId}"
                BrowserActivity.start(requireContext(), url)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(userId: String): UserQaListFragment {
            val fragment = UserQaListFragment()
            val args = Bundle().apply {
                putString(IntentKey.ID, userId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}