package com.example.blogsystem.network.model.music

import com.google.gson.annotations.SerializedName

data class LoginQrKey(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: Data
) {
    data class Data(
        @SerializedName("code")
        val code: Int,
        @SerializedName("unikey")
        val unikey: String
    )
}