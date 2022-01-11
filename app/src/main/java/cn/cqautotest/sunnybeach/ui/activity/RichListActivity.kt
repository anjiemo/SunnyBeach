package cn.cqautotest.sunnybeach.ui.activity

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.RichListActivityBinding
import cn.cqautotest.sunnybeach.ui.adapter.RichListAdapter
import cn.cqautotest.sunnybeach.util.loadStateListener
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/23
 * desc   : 富豪榜列表界面
 */
class RichListActivity : AppActivity(), StatusAction {

    private val mBinding by viewBinding<RichListActivityBinding>()
    private val mUserViewModel by viewModels<UserViewModel>()
    private val mRichListAdapter = RichListAdapter()
    private val loadStateListener = loadStateListener(mRichListAdapter) {
        mBinding.refreshLayout.finishRefresh()
    }

    override fun getLayoutId(): Int = R.layout.rich_list_activity

    override fun initView() {
        mBinding.rvRichList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mRichListAdapter
        }
    }

    override fun initData() {
        loadRichList()
    }

    private fun loadRichList() {
        lifecycleScope.launchWhenCreated {
            mUserViewModel.getRichList().collectLatest {
                mRichListAdapter.submitData(it)
            }
        }
    }

    override fun initEvent() {
        mBinding.refreshLayout.setOnRefreshListener {
            mRichListAdapter.refresh()
        }
        // 需要在 View 销毁的时候移除 listener
        mRichListAdapter.addLoadStateListener(loadStateListener)
    }

    override fun getStatusLayout(): StatusLayout = mBinding.slRichListLayout
}