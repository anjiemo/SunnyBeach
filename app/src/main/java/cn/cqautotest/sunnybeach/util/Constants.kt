@file:JvmName("Constants")

package cn.cqautotest.sunnybeach.util

// 阳光沙滩 GitHub 项目地址
const val SUNNY_BEACH_GITHUB_URL = "https://github.com/anjiemo/SunnyBeach"

const val USER_SETTING = "userSetting"

const val IS_FIRST = "isFirst"

const val APP_AUTO_CLEAN_CACHE = "appAutoCleanCache"

const val BASE_URL = "http://192.168.123.159:3000/"

const val DEFAULT_URL =
    "http://service.picasso.adesk.com/v1/vertical/vertical?disorder=true&limit=10&skip=0&adult=false&first=1&url=http%3A%2F%2Fservice.picasso.adesk.com%2Fv1%2Fvertical%2Fvertical&order=hot"

const val DEFAULT_BANNER_URL =
    "http://wallpaper.apc.360.cn/index.php?c=WallPaper&a=getAppsByOrder&order=create_time&start=55&count=5&from=360chrome"

const val CAI_YUN_BASE_URL = "https://api.caiyunapp.com/"

const val I_LOVE_ANDROID_SITE_BASE_URL = "http://www.52android.cn/"

const val SUNNY_BEACH_SITE_BASE_URL = "https://www.sunofbeach.net/"

const val SUNNY_BEACH_ARTICLE_URL_PRE = "${SUNNY_BEACH_SITE_BASE_URL}a/"

const val SUNNY_BEACH_FISH_URL_PRE = "${SUNNY_BEACH_SITE_BASE_URL}m/"

const val SUNNY_BEACH_QA_URL_PRE = "${SUNNY_BEACH_SITE_BASE_URL}qa/"

const val SUNNY_BEACH_SHARE_URL_PRE = "${SUNNY_BEACH_SITE_BASE_URL}s/"

const val SUNNY_BEACH_VIEW_USER_URL_PRE = "${SUNNY_BEACH_SITE_BASE_URL}u/"

const val SUNNY_BEACH_API_BASE_URL = "https://api.sunofbeaches.com/"

const val DEFAULT_AVATAR_URL = "https://cdn.sunofbeaches.com/images/default_avatar.png"

const val VERIFY_CODE_URL = "${SUNNY_BEACH_API_BASE_URL}uc/ut/captcha?code=1204736502274318336"

const val DEFAULT_HTTP_OK_CODE = 200

const val SUNNY_BEACH_HTTP_OK_CODE = 10000

const val GITEE_BASE_URL = "https://gitee.com/anjiemo/sunny-beach-data/raw/master/"

const val APP_INFO_URL = "${GITEE_BASE_URL}appconfig.json"

const val MOURNING_CALENDAR_URL = "${GITEE_BASE_URL}mourning_calendar.json"

const val SUNNY_BEACH_USER_BASIC_INFO = "sunny_beach_user_basic_info"

const val SUNNY_BEACH_USER_ACCOUNT_ENCRYPT_INFO = "sunny_beach_user_account_encrypt_info"

// 阳光沙滩淘宝店铺地址
const val SUNNY_BEACH_TAOBAO_SHOP_VIP = "https://item.taobao.com/item.htm?id=673527744707&mt="

// ==================反馈相关==================
// 反馈Url地址
const val MAKE_COMPLAINTS_URL = "https://support.qq.com/product/333302"

// 反馈者的Id
const val OPEN_ID = "open_id"

// 反馈者的昵称
const val NICK_NAME = "nick_name"

// 反馈者的头像
const val HEADER_IMG_URL = DEFAULT_AVATAR_URL

// 反馈者的地区
const val ADDRESS = "address"

// 反馈者的QQ号码
const val QQ = "qq_number"

// 自动登录
const val AUTO_LOGIN = "auto_login"

// 用户账号
const val SOB_ACCOUNT = "sob_account"

// 用户密码
const val SOB_PASSWORD = "sob_password"

// 记住密码
const val SOB_REMEMBER_PWD = "sob_remember_pwd"

/**
 * 友盟上报 Key
 */
interface UmengReportKey {

    companion object {

        // 首页搜索文章
        const val HOME_SEARCH = "HOME_SEARCH"

        // 用户中心
        const val USER_CENTER = "USER_CENTER"

        // 购买VIP
        const val BUY_VIP = "BUY_VIP"

        // 加入VIP
        const val JOIN_VIP = "JOIN_VIP"

        // 沙滩证
        const val VIEW_SOB_CARD = "VIE_SOB_CARD"

        // 富豪榜
        const val RICH_LIST = "RICH_LIST"

        // 天气预报
        const val WEATHER_FORECAST = "WEATHER_FORECAST"

        // 小默文章
        const val ANJIEMO_ARTICLE = "ANJIEMO_ARTICLE"

        // 我的文章
        const val MINE_ARTICLE = "MINE_ARTICLE"

        // 创作中心
        const val CREATION_CENTER = "CREATION_CENTER"

        // 我的收藏
        const val COLLECTIONS = "COLLECTIONS"

        // 查看高清壁纸
        const val VIEW_HD_WALLPAPER = "VIEW_HD_WALLPAPER"

        // 加入QQ群
        const val JOIN_QQ_GROUP = "JOIN_QQ_GROUP"
    }
}

interface CacheKey {

    companion object {

        const val CACHE_RICH_LIST = "CACHE_RICH_LIST_"
    }
}