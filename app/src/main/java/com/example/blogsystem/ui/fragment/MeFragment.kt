package com.example.blogsystem.ui.fragment

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.example.blogsystem.R
import com.example.blogsystem.base.BaseFragment
import com.example.blogsystem.databinding.FragmentMeBinding
import com.example.blogsystem.ui.activity.BrowserActivity
import com.example.blogsystem.utils.MAKE_COMPLAINTS_URL
import com.example.blogsystem.utils.simpleToast
import com.example.blogsystem.viewmodel.SingletonManager
import com.example.blogsystem.viewmodel.UserViewModel

class MeFragment : BaseFragment(R.layout.fragment_me) {

    private var _binding: FragmentMeBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by lazy { SingletonManager.userViewModel }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMeBinding.bind(view)
        callAllInit()
    }

    override fun initEvent() {
        binding.contentMe.feedbackContainer.setOnClickListener {
            val userBasicInfo = userViewModel.userBasicInfo.value
            if (userBasicInfo == null) {
                simpleToast("请先登录")
                return@setOnClickListener
            }
            BrowserActivity.startActivity(
                requireContext(),
                url = MAKE_COMPLAINTS_URL,
                title = null,
                openId = userBasicInfo.id,
                nickName = userBasicInfo.nickname,
                avatar = userBasicInfo.avatar
            )
        }
    }

    override fun initView() {
        userViewModel.userBasicInfo.observe(this) {
            Glide.with(this)
                .load(it.avatar)
                .placeholder(R.mipmap.ic_default_avatar)
                .error(R.mipmap.ic_default_avatar)
                .circleCrop()
                .into(binding.contentMe.imageAvatar)
            binding.contentMe.textNickName.text = it.nickname
        }
    }

    override fun initData() {
        val userBasicInfo = userViewModel.userBasicInfo.value
        binding.contentMe.textNickName.text = userBasicInfo?.nickname
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}