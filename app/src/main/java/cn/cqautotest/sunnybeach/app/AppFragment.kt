package cn.cqautotest.sunnybeach.app

import android.os.Bundle
import android.view.View
import cn.cqautotest.sunnybeach.action.Init
import cn.cqautotest.sunnybeach.action.ToastAction
import cn.cqautotest.sunnybeach.http.model.HttpData
import com.hjq.base.BaseFragment
import com.hjq.http.config.IRequestApi
import com.hjq.http.listener.OnHttpListener

/**
 *    author : Android 轮子哥 & A Lonely Cat
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2018/10/18
 *    desc   : Fragment 业务基类
 */
abstract class AppFragment<A : AppActivity> : BaseFragment<A>(),
    ToastAction, OnHttpListener<Any>, Init {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        initObserver()
    }

    override fun initEvent() {}

    override fun initObserver() {}

    /**
     * 当前加载对话框是否在显示中
     */
    open fun isShowDialog(): Boolean {
        val activity: A = getAttachActivity() ?: return false
        return activity.isShowDialog()
    }

    /**
     * 显示加载对话框
     */
    open fun showDialog() {
        getAttachActivity()?.showDialog()
    }

    /**
     * 隐藏加载对话框
     */
    open fun hideDialog() {
        getAttachActivity()?.hideDialog()
    }

    /**
     * [OnHttpListener]
     */
    override fun onHttpStart(api: IRequestApi) {
        showDialog()
    }

    override fun onHttpSuccess(result: Any) {
        if (result !is HttpData<*>) {
            return
        }
        toast(result.getMessage())
    }

    override fun onHttpFail(t: Throwable) {
        toast(t.message)
    }

    override fun onHttpEnd(api: IRequestApi) {
        hideDialog()
    }
}