package com.example.blogsystem.network.api

import com.example.blogsystem.network.model.music.LoginQrCheck
import com.example.blogsystem.network.model.music.LoginQrCreate
import com.example.blogsystem.network.model.music.LoginQrKey
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicApi {

    @GET("login/qr/key")
    suspend fun getLoginQrCodeKey(): LoginQrKey

    @GET("login/qr/create")
    suspend fun createLoginQrCode(
        @Query("key") key: String,
        @Query("qrimg") qrImg: String = "1"
    ): LoginQrCreate

    @GET("login/qr/check")
    suspend fun checkLoginQrCode(
        @Query("key") key: String
    ): LoginQrCheck
}