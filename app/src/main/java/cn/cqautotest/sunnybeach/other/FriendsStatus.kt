package cn.cqautotest.sunnybeach.other

import androidx.core.graphics.toColorInt

/**
 * 与某用户的关系
 */
enum class FriendsStatus(val code: Int, val desc: String, val color: Int) {

    FOLLOW(0, "+ 关注", "#1D7DFA".toColorInt()),
    BACK_FANS(1, "回粉", "#F56C6C".toColorInt()),
    FOLLOWED(2, "已关注", "#67C23A".toColorInt()),
    MUTUAL_ATTENTION(3, "相互关注", "#67C23A".toColorInt());

    val isNeedFollow: Boolean
        get() = when (this) {
            FOLLOW, BACK_FANS -> true
            FOLLOWED, MUTUAL_ATTENTION -> false
        }

    companion object {

        @JvmStatic
        fun valueOfCode(code: Int): FriendsStatus {
            return when (code) {
                0 -> FOLLOW
                1 -> BACK_FANS
                2 -> FOLLOWED
                3 -> MUTUAL_ATTENTION
                else -> FOLLOW
            }
        }
    }
}




