package com.example.blogsystem.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.blogsystem.R
import com.example.blogsystem.base.BaseFragment
import com.example.blogsystem.databinding.FragmentMusicBinding
import com.example.blogsystem.ui.activity.HomeActivity
import com.example.blogsystem.utils.logByDebug
import com.example.blogsystem.utils.simpleToast
import com.example.blogsystem.viewmodel.MusicViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MusicFragment : BaseFragment(R.layout.fragment_music) {

    private val musicViewModel by viewModels<MusicViewModel>()
    private var _binding: FragmentMusicBinding? = null
    private val mBinding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMusicBinding.bind(view)
        callAllInit()
    }

    override fun initEvent() {
        mBinding.refreshStateBtn.setOnClickListener {
            lifecycleScope.launch {
                musicViewModel.refreshQrCodeKey()
            }
        }
        mBinding.checkStateBtn.setOnClickListener {
            lifecycleScope.launch {
                musicViewModel.checkQrCodeState()
            }
        }
        // 二维码的 key 变了，重新获取二维码 bitmap
        musicViewModel.qrKeyLiveData.observe(this) { result ->
            lifecycleScope.launch {
                musicViewModel.loadQrBitmap(result)
            }
        }
        // 二维码的 bitmap 变了，重新加载二维码到界面上
        musicViewModel.qrBitmapLiveData.observe(this) { result ->
            logByDebug(msg = "initEvent: ===>qrCode bitmap state is changed...")
            Glide.with(this@MusicFragment).load(result).into(mBinding.qrCodeIv)
        }
        // 二维码的扫码状态变了，如果是扫码成功则更新二维码
        musicViewModel.qrStateLiveData.observe(this) { result ->
            logByDebug(msg = "initEvent: ===>qrCode scan state is changed...")
            // 待扫码状态不用更新二维码，待确认状态不用更新二维码（二维码失效：800、授权登录成功：803 均需要刷新二维码）
            if (result.code != 801 || result.code != 802) {
                if (result.code == 803) {
                    val activity = requireActivity()
                    if (activity is HomeActivity) {
                        activity.showHomeFragment()
                    }
                } else {
                    lifecycleScope.launch {
                        musicViewModel.refreshQrCodeKey()
                    }
                }
            }
            simpleToast(result.message)
        }
    }

    override fun initData() {
        lifecycleScope.launch {
            musicViewModel.refreshQrCodeKey()
            while (true) {
                musicViewModel.checkQrCodeState()
                delay(10000)
            }
        }
    }
}