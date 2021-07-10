package cn.cqautotest.sunnybeach.ui.fragment

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.databinding.FishPondFragmentBinding
import cn.cqautotest.sunnybeach.ui.activity.FishPondSettingActivity
import cn.cqautotest.sunnybeach.ui.adapter.FishPondAdapter
import cn.cqautotest.sunnybeach.utils.logByDebug
import cn.cqautotest.sunnybeach.utils.startActivity
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/7/7
 * desc   : 摸鱼列表 Fragment
 */
class FishPondFragment : AppFragment<AppActivity>() {

    private var _binding: FishPondFragmentBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var fishPondViewModel: FishPondViewModel
    private val mFishPondAdapter = FishPondAdapter()

    override fun getLayoutId(): Int = R.layout.fish_pond_fragment

    override fun onBindingView() {
        _binding = FishPondFragmentBinding.bind(view)
    }

    override fun initView() {
        fishPondViewModel = ViewModelProvider(this)[FishPondViewModel::class.java]
        mBinding.rvFishPondList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mFishPondAdapter
        }
    }

    override fun initData() {
        startActivity<FishPondSettingActivity>()
    }

    override fun initObserver() {
        fishPondViewModel.fishPond.observe(viewLifecycleOwner) { fishPond ->
            logByDebug(msg = "initObserver：===> fishPond is $fishPond")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}