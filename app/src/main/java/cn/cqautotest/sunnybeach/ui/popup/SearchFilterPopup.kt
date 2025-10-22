package cn.cqautotest.sunnybeach.ui.popup

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.Init
import cn.cqautotest.sunnybeach.databinding.SearchFilterPopupBinding
import cn.cqautotest.sunnybeach.ktx.clearItemAnimator
import cn.cqautotest.sunnybeach.model.SearchFilterItem
import cn.cqautotest.sunnybeach.ui.adapter.SearchFilterAdapter
import com.lxj.xpopup.impl.PartShadowPopupView

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2025/07/08
 * desc   : 搜索过滤条件Popup
 */
class SearchFilterPopup(context: Context) : PartShadowPopupView(context), Init {

    private val mBinding by lazy { SearchFilterPopupBinding.bind(attachPopupContainer.getChildAt(0)) }
    private val mSearchFilterAdapter by lazy { SearchFilterAdapter() }
    private var mOnItemClickListener: (item: SearchFilterItem, position: Int) -> Unit = { _, _ -> }

    override fun getImplLayoutId(): Int = R.layout.search_filter_popup

    override fun onCreate() {
        super.onCreate()
        callAllInit()
    }

    override fun initView() {
        mBinding.recyclerView.apply {
            adapter = mSearchFilterAdapter
            layoutManager = LinearLayoutManager(context)
            clearItemAnimator()
        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        mSearchFilterAdapter.setOnItemClickListener { item, position ->
            val newItem = item.copy(isChecked = item.isChecked.not())
            mSearchFilterAdapter.setItem(newItem, position)
            onItemChecked(newItem, position)
        }
    }

    override fun initObserver() {

    }

    override fun getPopupWidth(): Int = LayoutParams.MATCH_PARENT

    override fun getPopupHeight(): Int = LayoutParams.WRAP_CONTENT

    fun setData(data: List<SearchFilterItem>) = apply {
        mSearchFilterAdapter.setData(data)
    }

    private fun onItemChecked(item: SearchFilterItem, position: Int) {
        mOnItemClickListener.invoke(item, position)
    }

    fun setOnItemCheckedListener(listener: (item: SearchFilterItem, position: Int) -> Unit) = apply {
        mOnItemClickListener = listener
    }
}