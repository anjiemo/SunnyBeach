package cn.cqautotest.sunnybeach.event

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.milliseconds

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2024/04/29
 * desc   : 基于 SharedFlow 封装的现代化消息总线
 */
object FlowBus {

    private val busMap = ConcurrentHashMap<String, MutableSharedFlow<Any>>()

    /**
     * 获取一个事件流
     * [key] 消息标识
     * [sticky] 是否为粘性事件
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> with(key: String, sticky: Boolean = false): MutableSharedFlow<T> {
        return busMap.getOrPut(key) {
            MutableSharedFlow(
                replay = if (sticky) 1 else 0,
                extraBufferCapacity = 64,
                onBufferOverflow = BufferOverflow.DROP_OLDEST
            )
        } as MutableSharedFlow<T>
    }

    /**
     * 发送消息
     * [key] 消息标识
     * [value] 发送的消息内容
     * [delayTime] 延迟时间（毫秒）
     */
    fun post(key: String, value: Any = Unit, delayTime: Long = 0L) {
        val bus = with<Any>(key)
        if (delayTime > 0) {
            ProcessLifecycleOwner.get().lifecycleScope.launch {
                delay(delayTime.milliseconds)
                bus.emit(value)
            }
        } else {
            bus.tryEmit(value)
        }
    }

    /**
     * 观察消息
     * [owner] 生命周期所有者
     * [key] 消息标识
     * [isSticky] 是否为粘性事件
     * [observer] 观察者回调
     */
    inline fun <reified T : Any> observe(
        owner: LifecycleOwner,
        key: String,
        isSticky: Boolean = false,
        crossinline observer: (T) -> Unit
    ): Job {
        return owner.lifecycleScope.launch {
            with<T>(key, isSticky).collect {
                observer(it)
            }
        }
    }

    /**
     * 移除事件流
     */
    fun remove(key: String) {
        busMap.remove(key)
    }

    /**
     * 清除粘性事件（通过重置流实现）
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun clearSticky(key: String) {
        busMap[key]?.resetReplayCache()
    }
}