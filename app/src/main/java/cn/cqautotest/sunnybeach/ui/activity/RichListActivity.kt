package cn.cqautotest.sunnybeach.ui.activity

import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.RichListActivityBinding
import cn.cqautotest.sunnybeach.ui.adapter.RichListAdapter
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout

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

    override fun getLayoutId(): Int = R.layout.rich_list_activity

    override fun initView() {
        mBinding.rvRichList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mRichListAdapter
        }
    }

    override fun initData() {
        showLoading()
        val refreshLayout = mBinding.refreshLayout
        mUserViewModel.getRichList().observe(this) {
            refreshLayout.finishRefresh()
            val data = it.getOrElse {
                showError {
                    initData()
                }
                return@observe
            }
            if (data.isEmpty()) {
                showEmpty()
            } else {
                showComplete()
            }
            mRichListAdapter.setData(data)
        }
    }

    override fun initEvent() {
        mBinding.refreshLayout.setOnRefreshListener {
            initData()
        }
    }

    override fun getStatusLayout(): StatusLayout = mBinding.slRichListLayout
}