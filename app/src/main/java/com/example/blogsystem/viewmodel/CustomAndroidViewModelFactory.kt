package com.example.blogsystem.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.blogsystem.base.App
import com.example.blogsystem.viewmodel.app.AppViewModel
import com.example.blogsystem.viewmodel.discover.DiscoverViewModel

/**
 * ViewModel 提供程序工厂以实例化 ViewModel。需要给定 ViewModel 有一个非空的构造函数
 */
class CustomAndroidViewModelFactory : ViewModelProvider.AndroidViewModelFactory(App.get()) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(application = App.get()) as T
        }
        if (modelClass.isAssignableFrom(CookiesViewModel::class.java)) {
            return CookiesViewModel(application = App.get()) as T
        }
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            return AppViewModel(application = App.get()) as T
        }
        if (modelClass.isAssignableFrom(DiscoverViewModel::class.java)) {
            return DiscoverViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}