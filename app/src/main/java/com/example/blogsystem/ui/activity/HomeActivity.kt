package com.example.blogsystem.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.collection.arrayMapOf
import androidx.core.view.get
import androidx.core.view.size
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.AppUtils
import com.example.blogsystem.R
import com.example.blogsystem.base.BaseActivity
import com.example.blogsystem.base.BaseFragment
import com.example.blogsystem.base.FragmentAdapter
import com.example.blogsystem.databinding.ActivityHomeBinding
import com.example.blogsystem.model.AppUpdateInfo
import com.example.blogsystem.ui.fragment.*
import com.example.blogsystem.utils.clearLongClickTips
import com.example.blogsystem.utils.fullWindow
import com.example.blogsystem.utils.simpleToast
import com.example.blogsystem.viewmodel.SingletonManager
import java.io.File

class HomeActivity : BaseActivity() {

    private val appUpdateViewModel by lazy { SingletonManager.APP_VIEW_MODEL }
    private lateinit var mBinding: ActivityHomeBinding
    private val mFragmentMap = arrayMapOf<Int, BaseFragment>()
    private lateinit var mFragmentAdapter: FragmentAdapter

    private val homeFragment = HomeFragment()
    private val musicFragment = MusicFragment()
    private val discoverFragment = DiscoverFragment()
    private val meFragment = MeFragment()
    private var isCancel: Boolean = false
    private lateinit var appUpdateDialogBuilder: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        callAllInit()
    }

    override fun initData() {
//        fragmentList.add(HomeFragment())
//        mFragmentMap[0] = musicFragment
        mFragmentMap[0] = musicFragment
        mFragmentMap[1] = discoverFragment
        mFragmentMap[2] = meFragment
        mFragmentAdapter.setFragmentMap(mFragmentMap)
    }

    fun showHomeFragment() {
        mFragmentAdapter.replaceFragment(0, homeFragment)
    }

    fun showScanQrFragment() {
        mFragmentAdapter.replaceFragment(0, musicFragment)
    }

    override fun initView() {
        fullWindow()
        mBinding.homeBottomNav.clearLongClickTips()
        mFragmentAdapter = FragmentAdapter(supportFragmentManager, lifecycle)
        mBinding.homeViewPager2.let {
            it.isUserInputEnabled = false
            it.adapter = mFragmentAdapter
        }
        appUpdateDialogBuilder = AlertDialog.Builder(this)

        // val dialog = BeautifulDialog(this)
        // dialog.create().apply {
        //     setContentView(R.layout.content_login)
        // }.show()
    }

    override fun initEvent() {
        mBinding.homeViewPager2.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val menu = mBinding.homeBottomNav.menu
                for (index in 0 until menu.size) {
                    if (index == position) {
                        val menuItem = menu[index]
                        menuItem.isChecked = true
                        break
                    }
                }
            }
        })
        mBinding.homeBottomNav.setOnNavigationItemSelectedListener {
            val viewPager2 = mBinding.homeViewPager2
            if (it.itemId == R.id.home) {
                viewPager2.currentItem = 0
            }
            if (it.itemId == R.id.discover) {
                viewPager2.currentItem = 1
            }
            if (it.itemId == R.id.me) {
                viewPager2.currentItem = 2
            }
            true
        }

        appUpdateViewModel.checkAppVersionUpdate()
        appUpdateViewModel.appUpdateState.observe(this) {
            if (it.isDataValid.not()) return@observe
            val appUpdateInfo = it.appUpdateInfo ?: return@observe
            if (isCancel) return@observe
            isCancel = true
            if (appUpdateInfo.forceUpdate.not()) return@observe
            appUpdateViewModel.downloadApk(appUpdateInfo = appUpdateInfo,
                onComplete = {
                    simpleToast(getString(R.string.install_app_tips))
                },
                onEnd = { file: File?, newAppInfo: AppUpdateInfo ->
                    appUpdateDialogBuilder.setTitle(getString(R.string.install_now_app_tips))
                        .setMessage(newAppInfo.updateLog)
                        .setPositiveButton(getString(R.string.install_now)) { dialog, _ ->
                            AppUtils.installApp(file)
                            dialog.dismiss()
                        }
                        .setNegativeButton(getString(R.string.install_later)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setCancelable(newAppInfo.forceUpdate.not())
                        .create().apply {
                            dismiss()
                            show()
                        }
                }
            ).start()
        }
    }
}