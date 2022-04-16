package cn.cqautotest.sunnybeach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cn.cqautotest.sunnybeach.app.AppApplication
import cn.cqautotest.sunnybeach.viewmodel.app.AppViewModel
import cn.cqautotest.sunnybeach.viewmodel.discover.DiscoverViewModel

/**
 * ViewModel 提供程序工厂以实例化 ViewModel。需要给定 ViewModel 有一个非空的构造函数
 */
class CustomAndroidViewModelFactory(private val appApplication: AppApplication) :
    ViewModelProvider.AndroidViewModelFactory(appApplication) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(application = appApplication) as T
        }
        // if (modelClass.isAssignableFrom(CookiesViewModel::class.java)) {
        //     return CookiesViewModel(application = appApplication) as T
        // }
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            return AppViewModel(application = appApplication) as T
        }
        if (modelClass.isAssignableFrom(DiscoverViewModel::class.java)) {
            return DiscoverViewModel() as T
        }
        throw IllegalArgumentException(
            "Unknown ViewModel class，" +
                    "请检查 CustomAndroidViewModelFactory 是否包含该 ViewModel 的创建程序"
        )
    }
}