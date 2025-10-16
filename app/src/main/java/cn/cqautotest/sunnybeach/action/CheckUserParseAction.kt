package cn.cqautotest.sunnybeach.action

interface CheckUserParseAction {

    /**
     * We only support http and https protocols.
     */
    fun checkScheme(scheme: String): Boolean

    fun checkAuthority(authority: String): Boolean

    /**
     * Sob site userId is long type, we need check.
     */
    fun checkUserId(userId: String): Boolean
}