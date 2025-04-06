package cn.cqautotest.sunnybeach.event

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import cn.cqautotest.sunnybeach.event.LiveBusUtils.clearSticky
import cn.cqautotest.sunnybeach.ktx.ifNullOrEmpty
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jeremyliao.liveeventbus.core.LiveEventBusCore

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2025/03/25
 * desc   : LiveBus 工具类
 */
object LiveBusUtils {

    const val DEFAULT_KEY = "default_event"

    /**
     * 发送消息
     * [key] 消息标识 可不传(但是消息内容就必传data class类型，不然无法绑定消息)
     * [value] 发送的消息
     * [delay] 延迟毫秒数
     */
    fun busSend(key: String? = null, value: Any? = Unit, delay: Long = 0L) {
        LiveEventBus.get<Any>(key.ifNullOrEmpty { DEFAULT_KEY }).postDelay(value, delay)
    }

    /**
     * 接收消息
     * 如果在生命周期长期持有的类里面发送粘性事件需要在接手后调用[clearSticky]方式关闭粘性事件
     * [sender] 消息发送者
     * [key] 消息标识 可不传(事件泛型就必须是data class 类型，否则接收事件会出现异常情况)
     * [isSticky] 是否为粘性事件
     * [observer] 观察者
     */
    inline fun <reified T> busReceive(sender: LifecycleOwner, key: String? = null, isSticky: Boolean = false, observer: Observer<T>) {
        if (isSticky) {
            LiveEventBus.get(key.ifNullOrEmpty { DEFAULT_KEY }, T::class.java).observeSticky(sender, observer)
        } else {
            LiveEventBus.get(key.ifNullOrEmpty { DEFAULT_KEY }, T::class.java).observe(sender, observer)
        }
    }

    /**
     * 移除观察者
     * 根据给定的 [key] 移除关联的 [observer]
     * [key] 消息标识
     * [observer] 观察者
     */
    inline fun <reified T> busRemoveObserver(key: String? = null, observer: Observer<T>) {
        LiveEventBus.get(key.ifNullOrEmpty { DEFAULT_KEY }, T::class.java).removeObserver(observer)
    }

    /**
     * 根据给定的 [key] 清除粘性消息
     * 生命周期长期持有的类发送的粘性消息，建议在接收后调用此方法取消注册
     * [key] 消息标识
     */
    fun clearSticky(key: String) {
        LiveEventBusCore.get().config(key).autoClear(true)
    }
}