package cn.cqautotest.sunnybeach.http.network

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/06/08
 * desc   : 收藏信息获取
 */
object CollectionNetwork : INetworkApi {

    suspend fun getCollectionList(page: Int) = collectionApi.getCollectionList(page)
}