package cn.cqautotest.sunnybeach.util

import cn.cqautotest.sunnybeach.R

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/03/24
 * desc   : emoji 文字表情映射帮助类
 */
object EmojiMapHelper {

    // B站 emoji 表情映射集合
    val bilibiliEmojiMap = mapOf(
        "[微笑]" to R.mipmap.emoji_bilibili_001,
        "[呲牙]" to R.mipmap.emoji_bilibili_002,
        "[OK]" to R.mipmap.emoji_bilibili_003,
        "[星星眼]" to R.mipmap.emoji_bilibili_004,
        "[哦呼]" to R.mipmap.emoji_bilibili_005,
        "[歪嘴]" to R.mipmap.emoji_bilibili_006,
        "[嫌弃]" to R.mipmap.emoji_bilibili_007,
        "[喜欢]" to R.mipmap.emoji_bilibili_008,
        "[酸了]" to R.mipmap.emoji_bilibili_009,
        "[大哭]" to R.mipmap.emoji_bilibili_010,
        "[害羞]" to R.mipmap.emoji_bilibili_011,
        "[疑惑]" to R.mipmap.emoji_bilibili_012,
        "[辣眼睛]" to R.mipmap.emoji_bilibili_013,
        "[调皮]" to R.mipmap.emoji_bilibili_014,
        "[喜极而泣]" to R.mipmap.emoji_bilibili_015,
        "[奸笑]" to R.mipmap.emoji_bilibili_016,
        "[笑]" to R.mipmap.emoji_bilibili_017,
        "[偷笑]" to R.mipmap.emoji_bilibili_018,
        "[大笑]" to R.mipmap.emoji_bilibili_019,
        "[阴险]" to R.mipmap.emoji_bilibili_020,
        "[捂脸]" to R.mipmap.emoji_bilibili_021,
        "[囧]" to R.mipmap.emoji_bilibili_022,
        "[呆]" to R.mipmap.emoji_bilibili_023,
        "[抠鼻]" to R.mipmap.emoji_bilibili_024,
        "[惊喜]" to R.mipmap.emoji_bilibili_025,
        "[惊讶]" to R.mipmap.emoji_bilibili_026,
        "[笑哭]" to R.mipmap.emoji_bilibili_027,
        "[妙啊]" to R.mipmap.emoji_bilibili_028,
        "[doge]" to R.mipmap.emoji_bilibili_029,
        "[滑稽]" to R.mipmap.emoji_bilibili_030,
        "[吃瓜]" to R.mipmap.emoji_bilibili_031,
        "[打call]" to R.mipmap.emoji_bilibili_032,
        "[点赞]" to R.mipmap.emoji_bilibili_033,
        "[鼓掌]" to R.mipmap.emoji_bilibili_034,
        "[无语]" to R.mipmap.emoji_bilibili_035,
        "[尴尬]" to R.mipmap.emoji_bilibili_036,
        "[冷]" to R.mipmap.emoji_bilibili_037,
        "[灵魂出窍]" to R.mipmap.emoji_bilibili_038,
        "[委屈]" to R.mipmap.emoji_bilibili_039,
        "[傲娇]" to R.mipmap.emoji_bilibili_040,
        "[疼]" to R.mipmap.emoji_bilibili_041,
        "[吓]" to R.mipmap.emoji_bilibili_042,
        "[生病]" to R.mipmap.emoji_bilibili_043,
        "[吐]" to R.mipmap.emoji_bilibili_044,
        "[嘘声]" to R.mipmap.emoji_bilibili_045,
        "[捂眼]" to R.mipmap.emoji_bilibili_046,
        "[思考]" to R.mipmap.emoji_bilibili_047,
        "[再见]" to R.mipmap.emoji_bilibili_048,
        "[翻白眼]" to R.mipmap.emoji_bilibili_049,
        "[哈欠]" to R.mipmap.emoji_bilibili_050,
        "[奋斗]" to R.mipmap.emoji_bilibili_051,
        "[墨镜]" to R.mipmap.emoji_bilibili_052,
        "[难过]" to R.mipmap.emoji_bilibili_053,
        "[撇嘴]" to R.mipmap.emoji_bilibili_054,
        "[抓狂]" to R.mipmap.emoji_bilibili_055,
        "[生气]" to R.mipmap.emoji_bilibili_056,
        "[口罩]" to R.mipmap.emoji_bilibili_057,
        "[月饼]" to R.mipmap.emoji_bilibili_058,
        "[视频卫星]" to R.mipmap.emoji_bilibili_059,
        "[11周年]" to R.mipmap.emoji_bilibili_060,
        "[鸡腿]" to R.mipmap.emoji_bilibili_061,
        "[干杯]" to R.mipmap.emoji_bilibili_062,
        "[爱心]" to R.mipmap.emoji_bilibili_063,
        "[锦鲤]" to R.mipmap.emoji_bilibili_064,
        "[胜利]" to R.mipmap.emoji_bilibili_065,
        "[加油]" to R.mipmap.emoji_bilibili_066,
        "[保佑]" to R.mipmap.emoji_bilibili_067,
        "[抱拳]" to R.mipmap.emoji_bilibili_068,
        "[响指]" to R.mipmap.emoji_bilibili_069,
        "[支持]" to R.mipmap.emoji_bilibili_070,
        "[拥抱]" to R.mipmap.emoji_bilibili_071,
        "[怪我咯]" to R.mipmap.emoji_bilibili_072,
        "[跪了]" to R.mipmap.emoji_bilibili_073,
        "[黑洞]" to R.mipmap.emoji_bilibili_074,
        "[老鼠]" to R.mipmap.emoji_bilibili_075,
        "[2020]" to R.mipmap.emoji_bilibili_076,
        "[福到了]" to R.mipmap.emoji_bilibili_077,
        "[高兴]" to R.mipmap.emoji_bilibili_078,
        "[气愤]" to R.mipmap.emoji_bilibili_079,
        "[耍帅]" to R.mipmap.emoji_bilibili_080,
    )

    val emojiMap get() = bilibiliEmojiMap

    fun getEmojiValue(key: String) = emojiMap[key] ?: 0
}