package cn.cqautotest.sunnybeach.http.request.api

import cn.cqautotest.sunnybeach.http.response.model.WallpaperBannerBean
import cn.cqautotest.sunnybeach.http.response.model.WallpaperBean
import retrofit2.http.GET
import retrofit2.http.Query

interface PhotoApi {

    @GET(
        "http://service.picasso.adesk.com/v1/vertical/vertical?" +
                "disorder=true&adult=false&" +
                "first=1&url=http%3A%2F%2Fservice.picasso.adesk.com%2Fv1%2Fvertical%2Fvertical&order=hot"
    )
    suspend fun loadWallpaperList(
        @Query("limit") limit: Int, @Query("skip") skip: Int
    ): WallpaperBean

    @GET("http://wallpaper.apc.360.cn/index.php?c=WallPaper&a=getAppsByOrder&order=create_time&count=5&from=360chrome")
    suspend fun loadWallpaperBannerList(@Query("start") start: Int): WallpaperBannerBean
}