package com.example.blogsystem.viewmodel.login

/**
 * 登录表单的数据验证状态
 */
data class LoginFormState(
    val userAccountError: Int? = null,
    val passwordError: Int? = null,
    val verifyError: Int? = null,
    val isDataValid: Boolean = false
)