package cn.cqautotest.sunnybeach.util

import android.net.Uri
import java.util.regex.Pattern

/**
 * Created by FancyLou on 2015/11/4.
 */
object StringUtil {

    private val emailer = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")
    private val phone = Pattern.compile("^(1)\\d{10}$")
    private val telephone = Pattern.compile("^(0\\d{2}-\\d{8}(-\\d{1,4})?)|(0\\d{3}-\\d{7,8}(-\\d{1,4})?)$")
    private val HK_MACAO_phone = Pattern.compile("^((\\+00)?(852|853)\\d{8})$")
    private val ipPattern = Pattern.compile("((25[0-5])|(2[0-4]\\d)|(1\\d\\d)|([1-9]\\d)|\\d)(\\.((25[0-5])|(2[0-4]\\d)|(1\\d\\d)|([1-9]\\d)|\\d)){3}")
    private const val domainStr =
        "[\\w-]+\\.(com.cn|net.cn|gov.cn|org\\.nz|org.cn|com|net|org|gov|cc|biz|info|cn|co|io|tech|me|nl|eu|xyz|mobi|website|world|tv|la|love|technology|club|online|store|studio)\\b()*"
    private val domainPattern = Pattern.compile(domainStr, Pattern.CASE_INSENSITIVE)

    /**
     * 是否url
     *
     * @param str
     * @return
     */
    fun isUrl(str: String): Boolean {
        // val regex = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?"
        val regex = "((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,5})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?"
        return match(regex, str)
    }

    private fun match(regex: String, str: String): Boolean {
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(str)
        return matcher.matches()
    }

    /**
     * 截取数字
     */
    fun getNumbers(content: String?): String {
        content ?: return ""
        val pattern = Pattern.compile("\\d+")
        val matcher = pattern.matcher(content)
        while (matcher.find()) {
            return matcher.group(0).orEmpty()
        }
        return ""
    }

    /**
     * 判断给定的字符串中是否有中文
     */
    fun hasChinese(string: String?): Boolean {
        string ?: return false
        val chineseP = Pattern.compile("[\u4e00-\u9fa5]")
        val matcher = chineseP.matcher(string)
        return matcher.find()
    }

    /**
     * 去掉Uri 获取的路径前缀
     * 一般都是 file:///  或 content:/// 开头的
     *
     * @param filePath
     * @return
     */
    fun removeFilePathPrefix(filePath: String?): String? {
        if (filePath.isNullOrEmpty()) {
            return null
        }
        return when {
            filePath.startsWith("file://") -> filePath.substring(7)
            filePath.startsWith("content://") -> filePath.substring(10)
            else -> filePath
        }
    }

    /**
     * 获取顶级域名
     *
     * @param url
     * @return
     */
    fun getTopDomain(url: String?): String {
        var result = url ?: return ""
        try {
            val matcher = domainPattern.matcher(url)
            matcher.find()
            result = matcher.group()
        } catch (e: Exception) {
            // println("[getTopDomain ERROR]====>")
            // e.printStackTrace()
        }
        return result
    }

    /**
     * 验证是否是ip地址
     *
     * @param ip
     * @return
     */
    fun isIp(ip: CharSequence?): Boolean = if (ip.isNullOrBlank()) false else ipPattern.matcher(ip).matches()

    /**
     * 判断是不是一个合法的电子邮件地址
     */
    fun isEmail(email: CharSequence?): Boolean = if (email.isNullOrBlank()) false else emailer.matcher(email).matches()

    /**
     * 判断是不是一个合法的手机号码
     */
    fun isPhone(phoneNum: CharSequence?): Boolean = if (phoneNum.isNullOrBlank()) false else phone.matcher(phoneNum).matches()

    /**
     * 判断是否是手机号码 包括香港和澳门的
     * 香港 852(8位)
     * 澳门 853(8位)
     *
     * @param phoneNum
     * @return
     */
    fun isPhoneWithHKandMACAO(phoneNum: CharSequence?): Boolean {
        if (phoneNum.isNullOrBlank()) {
            return false
        }
        return phone.matcher(phoneNum).matches() || HK_MACAO_phone.matcher(phoneNum).matches()
    }

    /**
     * 判断两个 url 是否同源
     */
    fun isSameOrigin(firstUrl: String, secondUrl: String): Boolean {
        takeIf { firstUrl.isNotBlank() && firstUrl == secondUrl }?.let { return true }
        val firstUri = Uri.parse(firstUrl)
        val secondUri = Uri.parse(secondUrl)
        return firstUri.scheme == secondUri.scheme
                && firstUri.host == secondUri.host
                && firstUri.port == secondUri.port
    }
}