package cn.cqautotest.sunnybeach.ui.fragment.weather

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.databinding.PlaceFragmentBinding
import cn.cqautotest.sunnybeach.ktx.simpleToast
import cn.cqautotest.sunnybeach.ui.activity.weather.MainActivity
import cn.cqautotest.sunnybeach.ui.activity.weather.WeatherActivity
import cn.cqautotest.sunnybeach.ui.adapter.weather.PlaceAdapter
import cn.cqautotest.sunnybeach.viewmodel.weather.PlaceViewModel

class PlaceFragment : Fragment(R.layout.place_fragment) {

    private val binding by viewBinding<PlaceFragmentBinding>()
    val viewModel by viewModels<PlaceViewModel>()
    private lateinit var adapter: PlaceAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (activity is MainActivity && viewModel.isSaved()) {
            val place = viewModel.getSavedPlace()
            val intent = Intent(context, WeatherActivity::class.java).apply {
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat", place.location.lat)
                putExtra("place_name", place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }
        val layoutManager = LinearLayoutManager(activity)
        binding.recyclerview.layoutManager = layoutManager
        adapter = PlaceAdapter(this, viewModel.placeList)
        binding.recyclerview.adapter = adapter
        binding.searchPlaceEdit.doAfterTextChanged { text ->
            val content = text.toString()
            if (content.isNotEmpty()) {
                viewModel.searchPlaces(content)
            } else {
                binding.recyclerview.visibility = View.GONE
                binding.bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
                simpleToast("想看天气就请输入地点哦")
            }
        }
        viewModel.placeLiveData.observe(viewLifecycleOwner) { result ->
            val place = result.getOrNull()
            if (place != null) {
                binding.recyclerview.visibility = View.VISIBLE
                binding.bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(place)
                adapter.notifyDataSetChanged()
            } else {
                simpleToast("未能查询到任何地点")
                result.exceptionOrNull()?.printStackTrace()
            }
        }
    }
}