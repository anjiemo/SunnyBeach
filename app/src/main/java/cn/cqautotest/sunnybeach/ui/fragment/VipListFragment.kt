package cn.cqautotest.sunnybeach.ui.fragment

import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.databinding.VipListFragmentBinding
import cn.cqautotest.sunnybeach.other.GridSpaceDecoration
import cn.cqautotest.sunnybeach.ui.activity.CopyActivity
import cn.cqautotest.sunnybeach.ui.adapter.VipUserListAdapter
import cn.cqautotest.sunnybeach.util.dp
import cn.cqautotest.sunnybeach.util.isEmpty
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2022/02/04
 *    desc   : 贵宾列界面
 */
class VipListFragment : AppFragment<CopyActivity>(), StatusAction, OnRefreshListener {

    private val mBinding: VipListFragmentBinding by viewBinding()
    private val mUserViewModel by viewModels<UserViewModel>()
    private val mVipUserListAdapter = VipUserListAdapter()

    override fun getLayoutId(): Int = R.layout.vip_list_fragment

    override fun initView() {
        mBinding.rvVipUserList.apply {
            adapter = mVipUserListAdapter
            addItemDecoration(GridSpaceDecoration(4.dp))
        }
    }

    override fun initData() {
        loadVipUserList()
    }

    private fun loadVipUserList() {
        showLoading()
        mUserViewModel.getVipUserList().observe(viewLifecycleOwner) {
            mBinding.refreshLayout.finishRefresh()
            val vipUserSummaryList = it.getOrElse {
                showError {
                    loadVipUserList()
                }
                return@observe
            }
            mVipUserListAdapter.setData(vipUserSummaryList)
            if (mVipUserListAdapter.isEmpty()) {
                showEmpty()
            } else {
                showComplete()
            }
        }
    }

    override fun initEvent() {
        mBinding.refreshLayout.setOnRefreshListener(this)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        loadVipUserList()
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlVipUserListHint

    companion object {
        fun newInstance(): VipListFragment {
            return VipListFragment()
        }
    }
}