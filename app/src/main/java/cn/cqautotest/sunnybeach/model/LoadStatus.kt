package cn.cqautotest.sunnybeach.model

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2025/11/10
 * desc   : 加载状态密封类
 */
sealed class LoadStatus {

    object Loading : LoadStatus()

    data class Success<T>(val data: List<T>) : LoadStatus()

    object Empty : LoadStatus()

    data class Error(val message: String? = null, val throwable: Throwable? = null) : LoadStatus()
}