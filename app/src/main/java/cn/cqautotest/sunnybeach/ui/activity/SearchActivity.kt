package cn.cqautotest.sunnybeach.ui.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.SearchActivityBinding
import cn.cqautotest.sunnybeach.ktx.hideKeyboard
import cn.cqautotest.sunnybeach.ktx.reduceDragSensitivity
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.ktx.textString
import cn.cqautotest.sunnybeach.model.SearchFilterItem
import cn.cqautotest.sunnybeach.other.SearchType
import cn.cqautotest.sunnybeach.ui.fragment.SearchListFragment
import cn.cqautotest.sunnybeach.ui.popup.SearchFilterPopup
import cn.cqautotest.sunnybeach.viewmodel.SearchViewModel
import com.hjq.bar.TitleBar
import com.lxj.xpopup.XPopup

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/05/11
 * desc   : 搜索界面
 */
class SearchActivity : AppActivity() {

    private val mBinding by viewBinding<SearchActivityBinding>()
    private val mSearchViewModel by viewModels<SearchViewModel>()

    override fun getLayoutId(): Int = R.layout.search_activity

    override fun initView() {
        with(mBinding) {
            showKeyboard(mBinding.searchView)
            viewPager2.apply {
                reduceDragSensitivity()
                adapter = object : FragmentStateAdapter(this@SearchActivity) {

                    private val typeList = listOf(
                        SearchType.ALL,
                        SearchType.ARTICLE,
                        SearchType.QA,
                        SearchType.SHARE,
                    )
                    private val mFragmentList = typeList.map { SearchListFragment.newInstance(it) }

                    override fun getItemCount() = typeList.size

                    override fun createFragment(position: Int): Fragment = mFragmentList[position]
                }
            }
        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        val searchView = mBinding.searchView
        searchView.doOnTextChanged { newText, _, _, _ ->
            newText?.let { mSearchViewModel.setKeywords(it.toString()) }
        }
        searchView.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    doSearch()
                    hideKeyboard()
                }
            }
            true
        }
        val data = listOf("全部", "文章", "问答", "分享").mapIndexed { index, text -> SearchFilterItem(text = text, isChecked = index == 0) }
        var searchFilterPopup: SearchFilterPopup? = null
        SearchFilterPopup(this)
            .setData(data)
            .setOnItemCheckedListener { item, position ->
                mBinding.viewPager2.setCurrentItem(position, false)
                searchFilterPopup?.dismiss()
            }.also { searchFilterPopup = it }
        val searchFilterPopupView = XPopup.Builder(this)
            .dismissOnBackPressed(true)
            .dismissOnTouchOutside(true)
            .isDestroyOnDismiss(false)
            .atView(mBinding.vUiLine)
            .asCustom(searchFilterPopup)
        mBinding.tvAdvancedSearch.setFixOnClickListener {
            searchFilterPopupView.show()
        }
    }

    override fun onLeftClick(titleBar: TitleBar) {
        hideKeyboard()
        super.onLeftClick(titleBar)
    }

    override fun onRightClick(titleBar: TitleBar) {
        doSearch()
    }

    private fun doSearch() {
        with(mBinding.searchView.textString) {
            takeUnless { isEmpty() }?.let { mSearchViewModel.setKeywords(this) } ?: toast("关键字不能为空哦~")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.searchView.clearFocus()
    }

    override fun isStatusBarDarkFont() = true

    companion object {

        private const val SHARED_ELEMENT_NAME = "searchView"

        fun start(activity: Activity, sharedElement: View) {
            Intent(activity, SearchActivity::class.java).apply {
                val activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, sharedElement, SHARED_ELEMENT_NAME)
                ActivityCompat.startActivity(activity, this, activityOptionsCompat.toBundle())
            }
        }
    }
}