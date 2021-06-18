package com.example.blogsystem.model

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: T,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Boolean
)