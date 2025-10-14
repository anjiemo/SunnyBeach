package cn.cqautotest.sunnybeach.other

import android.os.Build
import timber.log.Timber
import timber.log.Timber.DebugTree
import timber.log.Timber.Tree
import kotlin.jvm.java

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2020/08/12
 *    desc   : 自定义日志打印规则
 */
class DebugLoggerTree : DebugTree() {

    private val fqcnIgnore = listOf(
        Timber::class.java.name,
        Timber.Forest::class.java.name,
        Tree::class.java.name,
        DebugTree::class.java.name,
        DebugLoggerTree::class.java.name
    )

    /**
     * 创建日志堆栈 TAG
     */
    override fun createStackElementTag(element: StackTraceElement): String {
        val tag: String = "(" + element.fileName + ":" + element.lineNumber + ")"
        // 日志 TAG 长度限制已经在 Android 8.0 被移除
        if (tag.length <= MAX_TAG_LENGTH || Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return tag
        }
        return tag.substring(0, MAX_TAG_LENGTH)
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (!AppConfig.isLogEnable()) {
            super.log(priority, tag, message, t)
            return
        }
        val prefix = Throwable().stackTrace
            .first { it.className !in fqcnIgnore }
            .let(::createStackElementTag)
        val realMessage = buildString {
            appendLine(prefix)
            appendLine("----------------------------------------")
            append(message)
        }
        super.log(priority, tag, realMessage, t)
    }

    companion object {
        private const val MAX_TAG_LENGTH: Int = 23
    }
}