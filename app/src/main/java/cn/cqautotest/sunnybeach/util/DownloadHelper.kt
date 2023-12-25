package cn.cqautotest.sunnybeach.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import timber.log.Timber
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2023/11/14
 * desc   : 下载帮助类（使用 Glide 进行下载）
 */
object DownloadHelper {

    suspend inline fun <reified T : Any> ofType(fragment: Fragment, uri: Uri) =
        ofType(fragment, uri, T::class.java)

    suspend fun <T : Any> ofType(fragment: Fragment, uri: Uri, resourceClass: Class<T>) =
        ofType(fragment.requireContext(), uri, resourceClass)

    suspend inline fun <reified T : Any> ofType(view: View, uri: Uri) =
        ofType(view, uri, T::class.java)

    suspend fun <T : Any> ofType(view: View, uri: Uri, resourceClass: Class<T>) =
        ofType(view.context, uri, resourceClass)

    suspend inline fun <reified T : Any> ofType(context: Context, uri: Uri) =
        ofType(context, uri, T::class.java)

    /**
     * 根据类型获取下载的 uri
     */
    suspend fun <T : Any> ofType(context: Context, uri: Uri, resourceClass: Class<T>) =
        suspendCoroutine { cont: Continuation<T?> ->
            var isResume = false
            Timber.d("ofType：===> url is $uri")
            Glide.with(context)
                .`as`(resourceClass)
                .load(uri)
                .into(object : CustomTarget<T>() {
                    override fun onResourceReady(resource: T, transition: Transition<in T>?) {
                        takeUnless { isResume }?.let {
                            cont.resume(resource)
                            isResume = true
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        takeUnless { isResume }?.let {
                            cont.resume(null)
                            isResume = true
                        }
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        takeUnless { isResume }?.let {
                            cont.resume(null)
                            isResume = true
                        }
                    }

                    override fun onDestroy() {
                        super.onDestroy()
                        takeUnless { isResume }?.let {
                            cont.resume(null)
                            isResume = true
                        }
                    }
                })
        }
}