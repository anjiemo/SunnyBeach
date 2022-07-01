package cn.cqautotest.sunnybeach.http.network

import cn.cqautotest.sunnybeach.http.api.photo.PhotoApi

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/09/07
 *    desc   : 图片获取
 */
object PhotoNetwork {

    suspend fun loadWallpaperBannerList() = PhotoApi.loadWallpaperBannerList((0..175).random())
}