package cn.cqautotest.sunnybeach.ui.activity

import androidx.activity.viewModels
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.PagingActivity
import cn.cqautotest.sunnybeach.ui.adapter.RichListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/23
 * desc   : 富豪榜列表界面
 */
class RichListActivity : PagingActivity() {

    private val mUserViewModel by viewModels<UserViewModel>()
    private val mRichListAdapter = RichListAdapter(AdapterDelegate())

    override fun getPagingAdapter() = mRichListAdapter

    override fun getLayoutId(): Int = R.layout.rich_list_activity

    override suspend fun loadListData() {
        mUserViewModel.getRichList().collectLatest { mRichListAdapter.submitData(it) }
    }
}