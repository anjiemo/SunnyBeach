package cn.cqautotest.sunnybeach.aop

import android.os.Looper
import android.os.Trace
import com.flyjingfish.android_aop_annotation.ProceedJoinPoint
import com.flyjingfish.android_aop_annotation.base.BasePointCut
import timber.log.Timber
import java.util.concurrent.TimeUnit

class LogInterceptCut : BasePointCut<Log> {

    override fun invoke(joinPoint: ProceedJoinPoint, anno: Log): Any? {
        enterMethod(joinPoint, anno)
        val startNanos: Long = System.nanoTime()
        val result: Any? = joinPoint.proceed()
        val stopNanos: Long = System.nanoTime()
        exitMethod(joinPoint, anno, result, TimeUnit.NANOSECONDS.toMillis(stopNanos - startNanos))
        return result
    }

    /**
     * 方法执行前切入
     */
    private fun enterMethod(joinPoint: ProceedJoinPoint, log: Log) {
        val targetClass = joinPoint.targetClass
        val targetMethod = joinPoint.targetMethod
        // 方法所在类
        val className: String = targetClass.name
        // 方法名
        val methodName: String = targetMethod.name
        // 方法参数值集合
        val parameterValues: Array<out Any?> = joinPoint.args.orEmpty()

        // 记录并打印方法的信息
        val builder: StringBuilder = getMethodLogInfo(className, methodName, parameterValues)
        log(log.value, builder.toString())
        val section: String = builder.substring(2)
        Trace.beginSection(section)
    }

    /**
     * 获取方法的日志信息
     *
     * [className]：类名
     * [methodName]：方法名
     * [parameterNames]：方法参数名集合
     * [parameterValues]：方法参数值集合
     */
    private fun getMethodLogInfo(
        className: String,
        methodName: String,
        parameterValues: Array<out Any?>
    ): StringBuilder {
        val builder: StringBuilder = StringBuilder("\u21E2 ")
        builder.append(className)
            .append(".")
            .append(methodName)
            .append('(')
        for (i in parameterValues.indices) {
            if (i > 0) {
                builder.append(", ")
            }
            builder.append("param${i}")
                .append('=')
                .append(parameterValues[i].toString())
        }
        builder.append(')')
        if (Looper.myLooper() != Looper.getMainLooper()) {
            builder.append(" [Thread:\"").append(Thread.currentThread().name).append("\"]")
        }
        return builder
    }

    /**
     * 方法执行完毕，切出
     *
     * [result]：方法执行后的结果
     * [lengthMillis]：执行方法所需要的时间
     */
    private fun exitMethod(joinPoint: ProceedJoinPoint, log: Log, result: Any?, lengthMillis: Long) {
        Trace.endSection()
        val targetMethod = joinPoint.targetMethod
        val className: String? = joinPoint.targetClass.name
        val methodName: String? = targetMethod.name
        val builder: StringBuilder = StringBuilder("\u21E0 ")
            .append(className)
            .append(".")
            .append(methodName)
            .append(" [")
            .append(lengthMillis)
            .append("ms]")

        //  判断方法是否有返回值
        if (targetMethod.returnType != Void.TYPE) {
            builder.append(" = ")
            builder.append(result.toString())
        }
        log(log.value, builder.toString())
    }

    private fun log(tag: String, msg: String?) {
        Timber.tag(tag)
        Timber.d(msg)
    }
}