package cn.cqautotest.sunnybeach.viewmodel

import cn.cqautotest.sunnybeach.app.AppApplication
import cn.cqautotest.sunnybeach.viewmodel.app.AppViewModel
import cn.cqautotest.sunnybeach.viewmodel.discover.DiscoverViewModel

/**
 * 通过 CustomAndroidViewModelFactory 创建的 ViewModel 需要注册
 */
object SingletonManager {

    private val appApplication by lazy { AppApplication.getInstance() }

    val userViewModel: UserViewModel =
        CustomAndroidViewModelFactory(appApplication).create(UserViewModel::class.java)
    val cookiesViewModel: CookiesViewModel =
        CustomAndroidViewModelFactory(appApplication).create(CookiesViewModel::class.java)
    val appViewModel: AppViewModel =
        CustomAndroidViewModelFactory(appApplication).create(AppViewModel::class.java)
    val discoverViewModel: DiscoverViewModel =
        CustomAndroidViewModelFactory(appApplication).create(DiscoverViewModel::class.java)
}