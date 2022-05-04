package cn.cqautotest.sunnybeach.http.network

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/09/07
 *    desc   : 图片获取
 */
object PhotoNetwork : INetworkApi {

    suspend fun loadWallpaperBannerList() = photoApi.loadWallpaperBannerList((0..175).random())
}