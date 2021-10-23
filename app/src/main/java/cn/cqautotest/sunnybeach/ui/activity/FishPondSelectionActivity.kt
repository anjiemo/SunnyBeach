package cn.cqautotest.sunnybeach.ui.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.FishPondSelectionActivityBinding
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.adapter.FishPondSelectionAdapter
import cn.cqautotest.sunnybeach.util.toJson
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/20
 * desc   : 鱼塘列表选择界面
 */
class FishPondSelectionActivity : AppActivity(), StatusAction {

    private val mBinding: FishPondSelectionActivityBinding by viewBinding()
    private val mFishPondViewModel by viewModels<FishPondViewModel>()
    private val mFishPondSelectionAdapter = FishPondSelectionAdapter()

    override fun getLayoutId(): Int = R.layout.fish_pond_selection_activity

    override fun initView() {
        mBinding.rvFishPondLabelsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mFishPondSelectionAdapter
        }
    }

    override fun initData() {
        showLoading()
        val refreshLayout = mBinding.refreshLayout
        mFishPondViewModel.loadTopicList().observe(this) {
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
            mFishPondSelectionAdapter.setData(data)
        }
    }

    override fun initEvent() {
        mBinding.refreshLayout.setOnRefreshListener {
            initData()
        }
        mFishPondSelectionAdapter.setOnItemClickListener { item, _ ->
            val data = Intent().apply {
                putExtra(IntentKey.OTHER, item.toJson())
            }
            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }

    override fun onRightClick(view: View?) {
        setResult(Activity.RESULT_OK, null)
        finish()
    }

    override fun getStatusLayout(): StatusLayout = mBinding.slFishPondSelectionLayout
}