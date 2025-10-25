package cn.cqautotest.sunnybeach.ui.activity.weather

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.core.view.updatePadding
import androidx.drawerlayout.widget.DrawerLayout
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.WeatherActivityBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.simpleToast
import cn.cqautotest.sunnybeach.ktx.toJson
import cn.cqautotest.sunnybeach.model.weather.DailyResponse
import cn.cqautotest.sunnybeach.model.weather.Weather
import cn.cqautotest.sunnybeach.model.weather.getSky
import cn.cqautotest.sunnybeach.viewmodel.weather.WeatherViewModel
import com.gyf.immersionbar.ImmersionBar
import dev.androidbroadcast.vbpd.viewBinding

class WeatherActivity : AppActivity() {

    val viewModel by viewModels<WeatherViewModel>()
    val binding by viewBinding(WeatherActivityBinding::bind)
    private val mSkyConList = arrayListOf<DailyResponse.Skycon>()

    override fun getLayoutId() = R.layout.weather_activity

    override fun initActivity() {
        super.initActivity()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {
                overridePendingTransition(R.anim.left_in_activity, R.anim.left_out_activity)
                finish()
            }
        })
    }

    override fun initView() {
        val actionBarHeight = ImmersionBar.getActionBarHeight(this)
        val statusBarHeight = ImmersionBar.getStatusBarHeight(this)
        val paddingTop = actionBarHeight - statusBarHeight + 10.dp
        findViewById<View>(R.id.actionBarLayout)?.updatePadding(0, paddingTop, 0, 10.dp)
    }

    override fun initData() {
        viewModel.locationLng = viewModel.locationLng.ifEmpty { intent.getStringExtra("location_lng").orEmpty() }
        viewModel.locationLat = viewModel.locationLat.ifEmpty { intent.getStringExtra("location_lat").orEmpty() }
        viewModel.placeName = viewModel.placeName.ifEmpty { intent.getStringExtra("placeName").orEmpty() }
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
    }

    override fun initEvent() {
        binding.now.navBtn.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {

            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(
                    drawerView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }

            override fun onDrawerOpened(drawerView: View) {

            }
        })
        viewModel.weatherLiveData.observe(this) { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeather(weather)
            } else {
                simpleToast("无法获取天气QWQ")
                result.exceptionOrNull()?.printStackTrace()
            }
            binding.swipeRefresh.finishRefresh()
        }
        binding.swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }
        binding.forecast.forecastLayout.setOnClickListener {
            val dataForJson = mSkyConList.toJson()
            // TemperatureTrendActivity.startActivity(this, dataForJson)
        }
    }

    fun refreshWeather() {
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
    }

    private fun showWeather(weather: Weather) {
        binding.now.placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        val currentTempText = "${realtime.temperature.toInt()}℃"
        binding.now.currentTemp.text = currentTempText
        binding.now.currentSky.text = getSky(realtime.skycon)!!.info
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        binding.now.currentAQI.text = currentPM25Text
        binding.weatherLayout.setBackgroundResource(getSky(realtime.skycon)!!.bg)
        binding.forecast.forecastLayout.removeAllViews()
        val days = daily.skycon.size
        mSkyConList.run {
            clear()
            addAll(daily.skycon)
        }
        for (i in 1 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val inflate = LayoutInflater.from(this)
            val view =
                inflate.inflate(R.layout.forecast_item, binding.forecast.forecastLayout, false)
            val dateInfo = view.findViewById<TextView>(R.id.dateInfo)
            val skyInfo = view.findViewById<TextView>(R.id.skyInfo)
            val skyIcon = view.findViewById<ImageView>(R.id.skyIcon)
            val temperatureInfo = view.findViewById<TextView>(R.id.temperatureInfo)
            dateInfo.text = skycon.date.subSequence(0, 10)
            val sky = getSky(skycon.value)!!
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()}~${temperature.max.toInt()}℃"
            temperatureInfo.text = tempText
            binding.forecast.forecastLayout.addView(view)
        }
        val lifeIndex = daily.lifeIndex
        binding.life.coldRiskText.text = lifeIndex.coldRisk[0].desc
        binding.life.dressingText.text = lifeIndex.dressing[0].desc
        binding.life.ultravioletText.text = lifeIndex.ultraviolet[0].desc
        binding.life.carWashingText.text = lifeIndex.carWashing[0].desc

        binding.weatherLayout.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        overridePendingTransition(R.anim.left_in_activity, R.anim.left_out_activity)
    }
}