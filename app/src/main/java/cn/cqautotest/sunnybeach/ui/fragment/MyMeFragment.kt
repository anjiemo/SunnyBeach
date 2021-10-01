package cn.cqautotest.sunnybeach.ui.fragment

import androidx.fragment.app.viewModels
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.TitleBarFragment
import cn.cqautotest.sunnybeach.databinding.MyMeFragmentBinding
import cn.cqautotest.sunnybeach.ui.activity.BrowserActivity
import cn.cqautotest.sunnybeach.ui.activity.SettingActivity
import cn.cqautotest.sunnybeach.util.MAKE_COMPLAINTS_URL
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/6/20
 * desc   : 个人中心界面
 */
class MyMeFragment : TitleBarFragment<AppActivity>() {

    private var _binding: MyMeFragmentBinding? = null
    private val mBinding get() = _binding!!
    private val mUserViewModel by viewModels<UserViewModel>()

    override fun getLayoutId(): Int = R.layout.my_me_fragment

    override fun onBindingView() {
        _binding = MyMeFragmentBinding.bind(view)
    }

    override fun onFragmentResume(first: Boolean) {
        super.onFragmentResume(first)
        val userBasicInfo = mUserViewModel.loadUserBasicInfo()
        val meContent = mBinding.meContent
        Glide.with(this)
            .load(userBasicInfo?.avatar)
            .circleCrop()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(meContent.imageAvatar)
        meContent.textNickName.text = userBasicInfo?.nickname
    }

    override fun initObserver() {

    }

    override fun initEvent() {
        val meContent = mBinding.meContent
        meContent.feedbackContainer.setOnClickListener {
            BrowserActivity.start(context, MAKE_COMPLAINTS_URL)
        }
        meContent.settingContainer.setOnClickListener {
            startActivity(SettingActivity::class.java)
        }
    }

    override fun initData() {
        mUserViewModel.checkUserToken()
    }

    override fun initView() {}

    override fun isStatusBarDarkFont(): Boolean = false

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

    companion object {
        @JvmStatic
        fun newInstance(): MyMeFragment {
            return MyMeFragment()
        }
    }
}