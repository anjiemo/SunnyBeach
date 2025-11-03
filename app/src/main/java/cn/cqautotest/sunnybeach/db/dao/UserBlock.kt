package cn.cqautotest.sunnybeach.db.dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2025/11/03
 * desc   : 用户拉黑关系表
 */
@Entity(
    tableName = "user_blocks",
    indices = [
        Index(value = ["uId", "targetUId"], unique = true),
        Index(value = ["uId"]),
        Index(value = ["targetUId"])
    ]
)
data class UserBlock(
    @PrimaryKey
    val uuid: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "uId")
    val uId: String,

    @ColumnInfo(name = "targetUId")
    val targetUId: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "reason")
    val reason: String? = null
)