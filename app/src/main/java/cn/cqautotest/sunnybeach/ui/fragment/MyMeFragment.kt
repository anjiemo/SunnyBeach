package cn.cqautotest.sunnybeach.ui.fragment

import androidx.fragment.app.viewModels
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.TitleBarFragment
import cn.cqautotest.sunnybeach.databinding.MyMeFragmentBinding
import cn.cqautotest.sunnybeach.model.UserBasicInfo
import cn.cqautotest.sunnybeach.ui.activity.BrowserActivity
import cn.cqautotest.sunnybeach.ui.activity.ImagePreviewActivity
import cn.cqautotest.sunnybeach.ui.activity.SettingActivity
import cn.cqautotest.sunnybeach.util.DEFAULT_AVATAR_URL
import cn.cqautotest.sunnybeach.util.MAKE_COMPLAINTS_URL
import cn.cqautotest.sunnybeach.util.logByDebug
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel
import com.bumptech.glide.Glide

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/6/20
 * desc   : 个人中心界面
 */
class MyMeFragment : TitleBarFragment<AppActivity>() {

    private var _binding: MyMeFragmentBinding? = null
    private val binding get() = _binding!!
    private val mUserViewModel by viewModels<UserViewModel>()

    override fun getLayoutId(): Int = R.layout.my_me_fragment

    override fun onBindingView() {
        _binding = MyMeFragmentBinding.bind(view)
    }

    override fun initEvent() {
        val meContent = binding.meContent
        meContent.run {
            imageAvatar.setOnClickListener {
                ImagePreviewActivity.start(requireContext(), DEFAULT_AVATAR_URL)
            }
            feedbackContainer.setOnClickListener {
                BrowserActivity.start(requireContext(), MAKE_COMPLAINTS_URL)
            }
            settingContainer.setOnClickListener {
                startActivity(SettingActivity::class.java)
            }
        }
    }

    override fun initData() {}

    override fun initView() {
        mUserViewModel.userBasicInfo.observe(viewLifecycleOwner) { userBasicInfo ->
            setupUserInfo(userBasicInfo)
        }
    }

    private fun setupUserInfo(userBasicInfo: UserBasicInfo?) {
        val meContent = binding.meContent
        val avatar = userBasicInfo?.avatar
        Glide.with(this)
            .load(avatar)
            .placeholder(R.mipmap.ic_default_avatar)
            .error(R.mipmap.ic_default_avatar)
            .circleCrop()
            .into(meContent.imageAvatar)
        meContent.textNickName.text = userBasicInfo?.nickname ?: ""
    }

    override fun onFragmentResume(first: Boolean) {
        super.onFragmentResume(first)
        val userBasicInfo = mUserViewModel.loadUserBasicInfo()
        logByDebug(msg = "onFragmentResume：userBasicInfo is ===>$userBasicInfo")
        setupUserInfo(userBasicInfo)
    }

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}