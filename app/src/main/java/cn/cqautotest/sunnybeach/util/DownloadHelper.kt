package cn.cqautotest.sunnybeach.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object DownloadHelper {

    suspend inline fun <reified T> getTypeByUri(fragment: Fragment, uri: Uri) =
        getTypeByUri(fragment, uri, T::class.java)

    suspend fun <T> getTypeByUri(fragment: Fragment, uri: Uri, resourceClass: Class<T>) =
        getTypeByUri(fragment.requireContext(), uri, resourceClass)

    suspend inline fun <reified T> getTypeByUri(view: View, uri: Uri) =
        getTypeByUri(view, uri, T::class.java)

    suspend fun <T> getTypeByUri(view: View, uri: Uri, resourceClass: Class<T>) =
        getTypeByUri(view.context, uri, resourceClass)

    suspend inline fun <reified T> getTypeByUri(context: Context, uri: Uri) =
        getTypeByUri(context, uri, T::class.java)

    /**
     * 根据类型获取下载的 uri
     */
    suspend fun <T> getTypeByUri(context: Context, uri: Uri, resourceClass: Class<T>) =
        suspendCoroutine { cont: Continuation<T?> ->
            var isResume = false
            Glide.with(context)
                .`as`(resourceClass)
                .load(uri)
                .into(object : CustomTarget<T>() {
                    override fun onResourceReady(
                        resource: T,
                        transition: Transition<in T>?
                    ) {
                        takeIf { isResume.not() }?.let {
                            cont.resume(resource)
                            isResume = true
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        takeIf { isResume.not() }?.let {
                            cont.resume(null)
                            isResume = true
                        }
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        takeIf { isResume.not() }?.let {
                            cont.resume(null)
                            isResume = true
                        }
                    }

                    override fun onDestroy() {
                        super.onDestroy()
                        takeIf { isResume.not() }?.let {
                            cont.resume(null)
                            isResume = true
                        }
                    }
                })
        }
}