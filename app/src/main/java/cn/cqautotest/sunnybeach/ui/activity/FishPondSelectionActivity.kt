package cn.cqautotest.sunnybeach.ui.activity

import android.app.Activity
import android.content.Intent
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.FishPondSelectionActivityBinding
import cn.cqautotest.sunnybeach.ktx.clearText
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.hideKeyboard
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.ktx.textString
import cn.cqautotest.sunnybeach.ktx.toJson
import cn.cqautotest.sunnybeach.model.RefreshStatus
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.adapter.FishPondSelectionAdapter
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import com.gyf.immersionbar.ImmersionBar
import com.hjq.bar.TitleBar

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/20
 * desc   : 鱼塘列表选择界面
 */
class FishPondSelectionActivity : AppActivity(), StatusAction {

    private val mBinding: FishPondSelectionActivityBinding by viewBinding()
    private val mFishPondViewModel by viewModels<FishPondViewModel>()
    private val mRefreshStatus = RefreshStatus()
    private val mFishPondSelectionAdapter = FishPondSelectionAdapter()
    private val mFishPondSearchAdapter = FishPondSelectionAdapter()

    override fun getLayoutId(): Int = R.layout.fish_pond_selection_activity

    override fun initView() {
        val statusBarHeight = ImmersionBar.getStatusBarHeight(this)
        val margin = 10.dp
        mBinding.clSearchContainer.updatePadding(top = statusBarHeight + margin, bottom = margin)
        mBinding.rvFishPondLabelsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mFishPondSelectionAdapter
        }
        mBinding.rvFishPondSearchLabelsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mFishPondSearchAdapter
        }
    }

    override fun initData() {
        showLoading()
        mFishPondViewModel.loadTopicList().observe(this) {
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
        mBinding.apply {
            llSearchPick.setFixOnClickListener {
                groupSearch.isVisible = true
                searchView.requestFocus()
                showKeyboard(searchView)
            }
            tvCancel.setFixOnClickListener {
                searchView.clearText()
                groupSearch.isVisible = false
                hideKeyboard()
            }
            searchView.doAfterTextChanged {
                val inputContent = searchView.textString
                val data = mFishPondSelectionAdapter.getData()
                val filteredData = data.filter {
                    it.topicName.contains(inputContent, true) ||
                            it.description.contains(inputContent, true)
                }
                mFishPondSearchAdapter.setData(filteredData)
            }
        }
        mFishPondSelectionAdapter.setOnItemClickListener { item, _ ->
            val data = Intent().apply {
                putExtra(IntentKey.OTHER, item.toJson())
            }
            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }

    override fun showLoading(id: Int) {
        takeIf { mRefreshStatus.isFirstRefresh }?.let { super.showLoading(id) }
        mRefreshStatus.isFirstRefresh = false
    }

    override fun onRightClick(titleBar: TitleBar) {
        setResult(Activity.RESULT_OK, null)
        finish()
    }

    override fun getStatusLayout(): StatusLayout = mBinding.slFishPondSelectionLayout

    override fun isStatusBarDarkFont(): Boolean = true
}