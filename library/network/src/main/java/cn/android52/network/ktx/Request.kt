package cn.android52.network.ktx

import okhttp3.Request
import retrofit2.Invocation

inline fun <reified T : Annotation> Request.getCustomAnnotation(): T? =
    tag(Invocation::class.java)?.method()?.getAnnotation(T::class.java)