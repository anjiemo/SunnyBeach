package com.example.blogsystem.ui.activity

import android.content.SharedPreferences
import android.os.Bundle
import com.example.blogsystem.base.BaseActivity
import com.example.blogsystem.databinding.ActivitySplashBinding
import com.example.blogsystem.utils.ActivityManager
import com.example.blogsystem.utils.USER_SETTING
import com.example.blogsystem.utils.fullWindow
import com.example.blogsystem.utils.startActivity
import com.example.blogsystem.viewmodel.SingletonManager

class SplashActivity : BaseActivity() {

    private lateinit var mBinding: ActivitySplashBinding
    private lateinit var sp: SharedPreferences
    private val userViewModel by lazy { SingletonManager.userViewModel }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        callAllInit()
    }

    override fun initEvent() {
        userViewModel.autoLogin.observe(this) { autoLogin ->
            if (autoLogin) {
                enterHomePage()
            } else {
                enterLoginPage()
            }
        }
    }

    override fun initData() {
        sp = getSharedPreferences(USER_SETTING, MODE_PRIVATE)
        userViewModel.checkUserToken()
    }

    override fun initView() {
        fullWindow()
    }

    private fun enterHomePage() {
        ActivityManager.finishAllActivities()
        startActivity<HomeActivity>()
        finish()
    }

    private fun enterLoginPage() {
        ActivityManager.finishAllActivities()
        startActivity<LoginActivity>()
        finish()
    }
}