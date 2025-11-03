package cn.cqautotest.sunnybeach.repository

import cn.cqautotest.sunnybeach.db.dao.UserBlock
import cn.cqautotest.sunnybeach.db.dao.UserBlockDao

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2025/11/03
 * desc   : 用户拉黑 Repository
 */
class UserBlockRepository(private val userBlockDao: UserBlockDao) {

    suspend fun blockUser(uId: String, targetUId: String, reason: String? = null): Boolean {
        return userBlockDao.blockUser(uId, targetUId, reason)
    }

    suspend fun unblockUser(uId: String, targetUId: String): Boolean {
        return userBlockDao.unblockUser(uId, targetUId)
    }

    suspend fun isUserBlocked(uId: String, targetUId: String): Boolean {
        return userBlockDao.isBlocked(uId, targetUId) > 0
    }

    suspend fun getBlockList(uId: String): List<String> {
        return userBlockDao.getBlockedUsers(uId)
    }

    suspend fun getBlockListDetails(uId: String): List<UserBlock> {
        return userBlockDao.getUserBlocks(uId)
    }
}