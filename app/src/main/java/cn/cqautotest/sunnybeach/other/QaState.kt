package cn.cqautotest.sunnybeach.other

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/18
 * desc   : 关注/粉丝 PagingSource
 */
enum class QaState(val value: String) {

    // 最新的
    LATEST("lastest"),

    // 等待解决的
    NO_ANSWER("noanswer"),

    // 热门的
    HOT("hot")
}