package cn.cqautotest.sunnybeach.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import timber.log.Timber

@Dao
interface UserBlockDao {

    // region 基础CRUD操作
    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBlock(userBlock: UserBlock): Long

    @Transaction
    @Query("DELETE FROM user_blocks WHERE uId = :uId AND targetUId = :targetUId")
    suspend fun deleteBlock(uId: String, targetUId: String): Int

    @Transaction
    @Query("SELECT * FROM user_blocks WHERE uId = :uId AND targetUId = :targetUId")
    suspend fun getBlockRecord(uId: String, targetUId: String): UserBlock?
    // endregion

    // region 查询操作
    @Transaction
    @Query("SELECT COUNT(*) FROM user_blocks WHERE uId = :uId AND targetUId = :targetUId")
    suspend fun isBlocked(uId: String, targetUId: String): Int

    @Transaction
    @Query("SELECT targetUId FROM user_blocks WHERE uId = :uId")
    suspend fun getBlockedUsers(uId: String): List<String>

    @Transaction
    @Query("SELECT * FROM user_blocks WHERE uId = :uId")
    suspend fun getUserBlocks(uId: String): List<UserBlock>

    @Transaction
    @Query("SELECT uId FROM user_blocks WHERE targetUId = :targetUId")
    suspend fun getBlockedByUsers(targetUId: String): List<String>
    // endregion

    // region 统计操作
    @Transaction
    @Query("SELECT COUNT(*) FROM user_blocks WHERE uId = :uId")
    suspend fun getBlockCount(uId: String): Int

    @Transaction
    @Query("SELECT COUNT(*) FROM user_blocks WHERE targetUId = :targetUId")
    suspend fun getBlockedCount(targetUId: String): Int
    // endregion

    // region BlockSummary 表操作
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBlockSummary(summary: BlockSummary)

    @Transaction
    @Query("SELECT * FROM block_summary WHERE uId = :uId")
    suspend fun getBlockSummary(uId: String): BlockSummary?
    // endregion

    // region 事务操作
    @Transaction
    suspend fun blockUser(uId: String, targetUId: String, reason: String? = null): Boolean {
        return try {
            val userBlock = UserBlock(uId = uId, targetUId = targetUId, reason = reason)
            val result = insertBlock(userBlock) > 0
            if (result) {
                syncBlockSummary(uId)
            }
            result
        } catch (e: Exception) {
            Timber.e(e)
            false
        }
    }

    @Transaction
    suspend fun unblockUser(uId: String, targetUId: String): Boolean {
        val result = deleteBlock(uId, targetUId) > 0
        if (result) {
            syncBlockSummary(uId)
        }
        return result
    }

    @Transaction
    suspend fun syncBlockSummary(uId: String) {
        val blockedUids = getBlockedUsers(uId)
        val summary = BlockSummary(uId = uId, blockedUids = blockedUids)
        upsertBlockSummary(summary)
    }
    // endregion

    // region 批量操作
    @Transaction
    suspend fun batchBlockUsers(uId: String, targetUIds: List<String>): List<Boolean> {
        return targetUIds.map { targetUId ->
            blockUser(uId, targetUId)
        }
    }

    @Transaction
    suspend fun batchUnblockUsers(uId: String, targetUIds: List<String>): List<Boolean> {
        return targetUIds.map { targetUId ->
            unblockUser(uId, targetUId)
        }
    }
    // endregion
}