package cn.cqautotest.sunnybeach.http.network

import cn.cqautotest.sunnybeach.http.api.sob.FishPondApi
import okhttp3.MultipartBody

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/09/09
 *    desc   : 摸鱼获取
 */
object FishNetwork {

    suspend fun getFishCommendListById(momentId: String, page: Int) = FishPondApi.getFishCommendListById(momentId, page)

    suspend fun loadFishDetailById(momentId: String) = FishPondApi.loadFishDetailById(momentId)

    suspend fun putFish(moment: Map<String, Any?>) = FishPondApi.putFish(moment)

    suspend fun uploadFishImage(part: MultipartBody.Part) = FishPondApi.uploadFishImage(part)

    suspend fun postComment(momentComment: Map<String, Any?>) = FishPondApi.postComment(momentComment)

    suspend fun replyComment(momentComment: Map<String, Any?>) = FishPondApi.replyComment(momentComment)

    suspend fun dynamicLikes(momentId: String) = FishPondApi.dynamicLikes(momentId)

    suspend fun loadTopicList() = FishPondApi.loadTopicList()

    suspend fun loadUserFishList(userId: String, page: Int) = FishPondApi.loadUserFishList(userId, page)

    suspend fun unfollowFishTopic(topicId: String) = FishPondApi.unfollowFishTopic(topicId)

    suspend fun followFishTopic(topicId: String) = FishPondApi.followFishTopic(topicId)

    suspend fun getFollowedTopicList() = FishPondApi.getFollowedTopicList()
}