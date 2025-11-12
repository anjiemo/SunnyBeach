package com.hjq.base.ktx

import android.os.Bundle
import android.os.Parcelable
import com.hjq.base.action.BundleAction
import java.io.Serializable

inline fun <reified P : Parcelable> BundleAction.getParcelableWithCompat(name: String): P? {
    val bundle: Bundle = getBundle() ?: return null
    return bundle.getParcelableWithCompat(name)
}

inline fun <reified S : Serializable> BundleAction.getSerializableWithCompat(name: String): S? {
    val bundle: Bundle = getBundle() ?: return null
    return bundle.getSerializableWithCompat(name)
}