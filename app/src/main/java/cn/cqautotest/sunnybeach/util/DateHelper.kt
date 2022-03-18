package cn.cqautotest.sunnybeach.util

import com.blankj.utilcode.util.TimeUtils
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/07/24
 * desc   : 日期格式化工具类
 */
object DateHelper {

    /**
     * @Description: 转换为友好型与当前时间的差
     * @author: anjiemo
     *
     * Return the friendly time span by now.
     * <p>The pattern is {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param time The formatted time string.
     * @return the friendly time span by now
     * <ul>
     * <li>如果小于 1 秒钟内，显示刚刚</li>
     * <li>如果在 1 分钟内，显示 XXX秒前</li>
     * <li>如果在 1 小时内，显示 XXX分钟前</li>
     * <li>如果在 1 小时外的今天内，显示今天15:32</li>
     * <li>如果是昨天的，显示昨天15:32</li>
     * <li>其余显示，2016-10-15</li>
     * <li>时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007</li>
     * </ul>
     */
    fun getFriendlyTimeSpanByNow(time: String): String {
        val transformTime = transform2LocalDateTime(time)
        return try {
            TimeUtils.getFriendlyTimeSpanByNow(transformTime)
        } catch (t: Throwable) {
            t.printStackTrace()
            transformTime
        }
    }

    /**
     * @Description: 将带有小数点的日期格式（2021-07-23 21:23:59.0）
     * 转换为 yyyy-MM-dd HH:mm:ss（2021-07-23 21:23:59） 格式的字符串
     * @author: anjiemo
     */
    private fun transform2LocalDateTime(date: String): String {
        val lastIndexOf = date.lastIndexOf(".")
        return try {
            date.replaceRange(lastIndexOf, date.length, "")
        } catch (e: IndexOutOfBoundsException) {
            Timber.d("传入的参数不是 yyyy-MM-dd HH:mm:ss.0 格式")
            // e.printStackTrace()
            date
        }
    }
}