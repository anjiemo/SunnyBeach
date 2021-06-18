package com.example.blogsystem.viewmodel.login

/**
 * 向 UI 公开的用户详细信息发布身份验证
 */
data class LoggedInUserView(
    val displayName: String
    //... UI 可访问的其他数据字段
)