package cn.cqautotest.sunnybeach.viewmodel.app

import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.api.PhotoApi

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/09/07
 *    desc   : 图片获取
 */
object PhotoNetwork {

    private val photoApi = ServiceCreator.create<PhotoApi>()

    suspend fun loadWallpaperBannerList() = photoApi.loadWallpaperBannerList((0..175).random())
}