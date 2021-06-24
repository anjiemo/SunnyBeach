package cn.cqautotest.sunnybeach.viewmodel

import cn.cqautotest.sunnybeach.viewmodel.app.AppViewModel
import cn.cqautotest.sunnybeach.viewmodel.discover.DiscoverViewModel

/**
 * 通过 CustomAndroidViewModelFactory 创建的 ViewModel 需要注册
 */
object SingletonManager {

    val userViewModel: UserViewModel =
        CustomAndroidViewModelFactory().create(UserViewModel::class.java)
    val cookiesViewModel: CookiesViewModel =
        CustomAndroidViewModelFactory().create(CookiesViewModel::class.java)
    val appViewModel: AppViewModel =
        CustomAndroidViewModelFactory().create(AppViewModel::class.java)
    val discoverViewModel: DiscoverViewModel =
        CustomAndroidViewModelFactory().create(DiscoverViewModel::class.java)
}