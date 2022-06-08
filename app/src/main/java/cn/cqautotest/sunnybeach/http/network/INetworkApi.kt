package cn.cqautotest.sunnybeach.http.network

import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.api.app.AppApi
import cn.cqautotest.sunnybeach.http.api.photo.PhotoApi
import cn.cqautotest.sunnybeach.http.api.sob.*
import cn.cqautotest.sunnybeach.http.api.weather.PlaceApi
import cn.cqautotest.sunnybeach.http.api.weather.WeatherApi

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/18
 * desc   : 网络请求 Api
 */
interface INetworkApi {

    val appApi get() = ServiceCreator.create<AppApi>()

    val articleApi get() = ServiceCreator.create<ArticleApi>()

    val fansApi get() = ServiceCreator.create<FansApi>()

    val fishPondApi get() = ServiceCreator.create<FishPondApi>()

    val followApi get() = ServiceCreator.create<FollowApi>()

    val msgApi get() = ServiceCreator.create<MsgApi>()

    val photoApi get() = ServiceCreator.create<PhotoApi>()

    val qaApi get() = ServiceCreator.create<QaApi>()

    val shareApi get() = ServiceCreator.create<ShareApi>()

    val userApi get() = ServiceCreator.create<UserApi>()

    val placeApi get() = ServiceCreator.create<PlaceApi>()

    val weatherApi get() = ServiceCreator.create<WeatherApi>()

    val courseApi get() = ServiceCreator.create<CourseApi>()

    val collectionApi get() = ServiceCreator.create<CollectionApi>()
}