package com.example.blogsystem.viewmodel

import com.example.blogsystem.viewmodel.app.AppViewModel

/**
 * 通过 CustomAndroidViewModelFactory 创建的 ViewModel 需要注册
 */
object SingletonManager {

    val userViewModel: UserViewModel =
        CustomAndroidViewModelFactory().create(UserViewModel::class.java)
    val cookiesViewModel: CookiesViewModel =
        CustomAndroidViewModelFactory().create(CookiesViewModel::class.java)
    val APP_VIEW_MODEL: AppViewModel =
        CustomAndroidViewModelFactory().create(AppViewModel::class.java)
}