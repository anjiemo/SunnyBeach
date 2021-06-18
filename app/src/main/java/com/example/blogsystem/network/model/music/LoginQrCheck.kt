package com.example.blogsystem.network.model.music

import com.google.gson.annotations.SerializedName

data class LoginQrCheck(
    @SerializedName("code")
    val code: Int,
    @SerializedName("cookie")
    val cookie: String,
    @SerializedName("message")
    val message: String
)