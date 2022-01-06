package cn.cqautotest.sunnybeach.viewmodel.app

import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.request.api.FishPondApi
import okhttp3.MultipartBody

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/09/09
 *    desc   : 摸鱼获取
 */
object FishNetwork {

    private val fishPondApi = ServiceCreator.create<FishPondApi>()

    suspend fun getFishCommendListById(momentId: String, page: Int) =
        fishPondApi.getFishCommendListById(momentId, page)

    suspend fun loadFishDetailById(momentId: String) = fishPondApi.loadFishDetailById(momentId)

    suspend fun putFish(moment: Map<String, Any?>) = fishPondApi.putFish(moment)

    suspend fun uploadFishImage(part: MultipartBody.Part) = fishPondApi.uploadFishImage(part)

    suspend fun postComment(momentComment: Map<String, Any?>) =
        fishPondApi.postComment(momentComment)

    suspend fun replyComment(momentComment: Map<String, Any?>) =
        fishPondApi.replyComment(momentComment)

    suspend fun dynamicLikes(momentId: String) = fishPondApi.dynamicLikes(momentId)

    suspend fun loadTopicList() = fishPondApi.loadTopicList()

    suspend fun loadUserFishList(userId: String, page: Int) =
        fishPondApi.loadUserFishList(userId, page)
}