package cn.cqautotest.sunnybeach.ui.fragment.user.media

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.databinding.UserFishListFragmentBinding
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.adapter.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.QaListAdapter
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.util.dp
import cn.cqautotest.sunnybeach.util.isEmpty
import cn.cqautotest.sunnybeach.viewmodel.QaViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/31
 * desc   : 用户回答列表 Fragment
 */
class UserQaListFragment : AppFragment<AppActivity>(), StatusAction {

    private val mBinding by viewBinding<UserFishListFragmentBinding>()
    private val mQaListAdapter = QaListAdapter(AdapterDelegate())
    private val mQaViewModel by activityViewModels<QaViewModel>()
    private val loadStateListener = { cls: CombinedLoadStates ->
        when (cls.refresh) {
            is LoadState.NotLoading -> {
                mBinding.refreshLayout.finishRefresh()
                if (mQaListAdapter.isEmpty()) {
                    showEmpty()
                } else {
                    showComplete()
                }
            }
            is LoadState.Loading -> showLoading()
            is LoadState.Error -> showError { mQaListAdapter.refresh() }
        }
    }

    override fun getLayoutId(): Int = R.layout.user_fish_list_fragment

    override fun initView() {
        mBinding.rvFishPondList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mQaListAdapter
            addItemDecoration(SimpleLinearSpaceItemDecoration(4.dp))
        }
    }

    override fun initData() {
        val userId = arguments?.getString(IntentKey.ID, "") ?: ""
        loadUserQaList(userId)
    }

    private fun loadUserQaList(userId: String) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            mQaViewModel.getUserQaList(userId).collectLatest {
                mQaListAdapter.submitData(it)
            }
        }
    }

    override fun initEvent() {
        mBinding.refreshLayout.setOnRefreshListener {
            mQaListAdapter.refresh()
        }
        // 需要在 View 销毁的时候移除 listener
        mQaListAdapter.addLoadStateListener(loadStateListener)
        mQaListAdapter.setOnItemClickListener { item, _ ->
            // 跳转到问答详情界面
        }
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlFishPondHint

    override fun onDestroyView() {
        super.onDestroyView()
        mQaListAdapter.removeLoadStateListener(loadStateListener)
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