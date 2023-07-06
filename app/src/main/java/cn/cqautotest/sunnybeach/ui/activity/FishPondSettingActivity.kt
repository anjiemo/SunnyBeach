package cn.cqautotest.sunnybeach.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.aop.Log
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.FishPondSettingActivityBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.model.FishPondTopicList
import cn.cqautotest.sunnybeach.ui.adapter.FishPondAdapter
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import cn.cqautotest.sunnybeach.widget.recyclerview.GridSpaceDecoration
import com.blankj.utilcode.util.DeviceUtils
import com.chad.library.adapter.base.module.BaseDraggableModule
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/07/10
 * desc   : 摸鱼设置界面
 */
class FishPondSettingActivity : AppActivity(), StatusAction, OnRefreshListener {

    private val mBinding: FishPondSettingActivityBinding by viewBinding()
    private val mFishPondViewModel by viewModels<FishPondViewModel>()
    private lateinit var mStatusLayout: StatusLayout
    private lateinit var mRefreshLayout: SmartRefreshLayout
    private val mFishPondAdapter = FishPondAdapter()

    override fun getLayoutId(): Int = R.layout.fish_pond_setting_activity

    override fun initObserver() {
        // mFishPondViewModel.fishFishPondTopicList.observe(this) { fishTopic ->
        //     setupTopicUI(fishTopic)
        // }
    }

    private fun setupTopicUI(fishFishPondTopicList: FishPondTopicList?) {
        mRefreshLayout.finishRefresh()
        if (fishFishPondTopicList == null) {
            showEmpty()
            return
        }
        showComplete()
        Timber.d("topic is $fishFishPondTopicList")
        mFishPondAdapter.setList(fishFishPondTopicList)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        loadTopic()
    }

    override fun initEvent() {
        mRefreshLayout.setOnRefreshListener(this)
    }

    override fun initData() {
        loadTopic()
        showLoading()
    }

    private fun loadTopic() {
        // mFishPondViewModel.loadTopicList()
    }

    override fun initView() {
        mStatusLayout = mBinding.hlFishPondHint
        mRefreshLayout = mBinding.rlStatusRefresh
        mBinding.rvFishPondLabelsList.apply {
            // 如果是平板则展示两列，否则展示一列
            val spanCount = if (DeviceUtils.isTablet()) 6 else 3
            layoutManager = GridLayoutManager(this@FishPondSettingActivity, spanCount)
            adapter = mFishPondAdapter
            val draggableModule = BaseDraggableModule(mFishPondAdapter)
            draggableModule.attachToRecyclerView(this)
            draggableModule.isDragEnabled = true
            addItemDecoration(GridSpaceDecoration(8.dp))
        }
    }

    override fun getStatusLayout(): StatusLayout {
        return mStatusLayout
    }

    companion object {

        @JvmStatic
        @Log
        fun start(context: Context) {
            val intent = Intent(context, FishPondSettingActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}