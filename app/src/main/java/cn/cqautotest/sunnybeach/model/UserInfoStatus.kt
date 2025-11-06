package cn.cqautotest.sunnybeach.model

sealed class UserInfoStatus {
    object Loading : UserInfoStatus()
    data class Success(val info: UserInfo) : UserInfoStatus()
    data class Error(val error: Throwable?) : UserInfoStatus()
}