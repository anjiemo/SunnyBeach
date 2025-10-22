package cn.cqautotest.sunnybeach.util.toast

import cn.cqautotest.sunnybeach.action.ToastAction
import com.hjq.toast.ToastLogInterceptor

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2025/06/27
 * desc   : App 自定义 Toast 拦截器（用于追踪 Toast 调用的位置）
 */
class AppToastLogInterceptor : ToastLogInterceptor() {

    override fun filterClass(clazz: Class<*>?): Boolean {
        return super.filterClass(clazz) || getDefaultImplClass(ToastAction::class.java) == clazz
    }

    private fun getDefaultImplClass(interfaceClazz: Class<*>): Class<*>? {
        val className = interfaceClazz.name + "\$DefaultImpls"
        return try {
            Class.forName(className)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            null
        }
    }
}