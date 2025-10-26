package cn.cqautotest.sunnybeach.event

/**
 * LiveBusKey 配置
 */
object LiveBusKeyConfig {

    // 发布摸鱼成功
    const val BUS_PUT_FISH_SUCCESS = "BUS_PUT_FISH_SUCCESS"

    // 账号登录成功
    const val BUS_LOGIN_SUCCESS = "BUS_LOGIN_SUCCESS"

    // 登录信息更新
    const val BUS_LOGIN_INFO_UPDATE = "BUS_LOGIN_INFO_UPDATE"

    // 从二楼界面回到首页
    const val BUS_TWO_LEVEL_BACK_TO_HOME_PAGE = "BUS_TWO_LEVEL_BACK_TO_HOME_PAGE"

    // 首页二楼状态（true：已经打开，false：已经关闭）
    const val BUS_HOME_PAGE_TWO_LEVEL_PAGE_STATE = "BUS_HOME_PAGE_TWO_LEVEL_PAGE_STATE"
}