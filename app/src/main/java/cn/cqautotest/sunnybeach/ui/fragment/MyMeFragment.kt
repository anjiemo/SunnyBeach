package cn.cqautotest.sunnybeach.ui.fragment

import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.TitleBarFragment
import cn.cqautotest.sunnybeach.databinding.MyMeFragmentBinding
import cn.cqautotest.sunnybeach.ui.activity.BrowserActivity
import cn.cqautotest.sunnybeach.ui.activity.ImagePreviewActivity
import cn.cqautotest.sunnybeach.ui.activity.SettingActivity
import cn.cqautotest.sunnybeach.utils.DEFAULT_AVATAR_URL
import cn.cqautotest.sunnybeach.utils.MAKE_COMPLAINTS_URL
import cn.cqautotest.sunnybeach.viewmodel.SingletonManager
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
    private val userViewModel: UserViewModel by lazy { SingletonManager.userViewModel }

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

    override fun initView() {
        val meContent = binding.meContent
        userViewModel.userBasicInfo.observe(this) {
            Glide.with(this)
                .load(it.avatar)
                .placeholder(R.mipmap.ic_default_avatar)
                .error(R.mipmap.ic_default_avatar)
                .circleCrop()
                .into(meContent.imageAvatar)
            meContent.textNickName.text = it.nickname
        }
    }

    override fun initData() {
        val userBasicInfo = userViewModel.userBasicInfo.value
        userBasicInfo?.let {
            binding.meContent.textNickName.text = it.nickname
        }
    }

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(): MyMeFragment {
            return MyMeFragment()
        }
    }
}