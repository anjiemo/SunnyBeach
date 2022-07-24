package cn.cqautotest.sunnybeach.ui.activity

import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.SobIeDeatilActivityBinding
import cn.cqautotest.sunnybeach.ktx.simpleToast
import cn.cqautotest.sunnybeach.ui.fragment.sobie.SobIEDetailListFragment
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/07/17
 * desc   : 用户 Sob 币的收支（Income & Expenditures）明细界面
 */
class SobIEDetailActivity : AppActivity() {

    private val mBinding by viewBinding<SobIeDeatilActivityBinding>()
    private val mUserViewModel by viewModels<UserViewModel>()

    override fun getLayoutId() = R.layout.sob_ie_deatil_activity

    override fun initView() {
        with(mBinding.lineChart) {
            initLineChart()
            initLineData()
            supportFragmentManager.beginTransaction().apply {
                add(SobIEDetailListFragment.newInstance(), "SobIEDetailListFragmentTag")
            }.commitAllowingStateLoss()
        }
    }

    private fun LineChart.initLineData() {
        val yValues = arrayListOf<Entry>()
        repeat(20) {
            yValues.add(Entry(it * 1f, (10..20).random() * 1f))
        }
        val lineDataSet = LineDataSet(yValues, "收入").also {
            it.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        }
        data = LineData(lineDataSet)
        invalidate()
    }

    private fun LineChart.initLineChart() {
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            labelCount = 20
            val xLabels = arrayListOf<String>()
            repeat(20) { xLabels.add((it + 1).toString()) }
            valueFormatter = IndexAxisValueFormatter(xLabels)
        }
        axisLeft.setDrawGridLines(false)
        axisRight.isEnabled = false
    }

    override fun initData() {
        mUserViewModel.getMeSobIEDetailList(1).observe(this) { result ->
            result.onSuccess {
                Timber.d("initData：===> $it")
            }.onFailure {
                simpleToast("获取失败 ${it.message}")
            }
        }
    }

    override fun initEvent() {

    }
}