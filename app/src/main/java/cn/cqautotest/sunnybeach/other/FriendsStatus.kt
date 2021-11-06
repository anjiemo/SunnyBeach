package cn.cqautotest.sunnybeach.other

import android.graphics.Color

/**
 * 与某用户的关系
 */
enum class FriendsStatus(val code: Int, val desc: String, val color: Int) {

    FOLLOW(0, "+ 关注", Color.parseColor("#1D7DFA")),
    BACK_FANS(1, "回粉", Color.parseColor("#F56C6C")),
    FOLLOWED(2, "已关注", Color.parseColor("#67C23A")),
    MUTUAL_ATTENTION(3, "相互关注", Color.parseColor("#67C23A"));

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




