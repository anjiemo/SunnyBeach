package com.example.blogsystem.network.model.music

import com.google.gson.annotations.SerializedName

data class LoginQrCreate(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: Data
) {
    data class Data(
        @SerializedName("qrimg")
        val qrimg: String,
        @SerializedName("qrurl")
        val qrurl: String
    )
}