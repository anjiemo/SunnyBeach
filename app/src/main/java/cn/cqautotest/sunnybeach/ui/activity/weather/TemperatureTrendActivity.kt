package cn.cqautotest.sunnybeach.ui.activity.weather

import android.content.Context
import android.content.Intent
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.model.weather.DailyResponse
import cn.cqautotest.sunnybeach.util.fromJson
import timber.log.Timber

class TemperatureTrendActivity : AppActivity() {

    override fun getLayoutId(): Int = R.layout.temperature_trend_activity

    override fun initView() {

    }

    override fun initData() {
        val intent = intent
        val dataForJson = intent.getStringExtra("data") ?: return
        val dataFromJson: List<DailyResponse.Skycon> = fromJson(dataForJson)
        for (data in dataFromJson) {
            Timber.d("initData: ===>date：${data.date} value：${data.value}")
        }
    }

    companion object {
        fun startActivity(context: Context, data: String) {
            val intent = Intent(context, TemperatureTrendActivity::class.java)
            intent.putExtra("data", data)
            context.startActivity(intent)
        }
    }
}