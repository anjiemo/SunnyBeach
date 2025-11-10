package cn.cqautotest.sunnybeach.db.dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2025/11/03
 * desc   : 拉黑列表汇总表
 */
@Entity(tableName = "block_summary")
data class BlockSummary(
    @PrimaryKey
    @ColumnInfo(name = "uId")
    val uId: String,

    @ColumnInfo(name = "blocked_uids")
    val blockedUids: List<String>,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)