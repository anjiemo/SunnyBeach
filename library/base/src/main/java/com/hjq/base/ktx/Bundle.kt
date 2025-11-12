package com.hjq.base.ktx

import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.BundleCompat
import java.io.Serializable

inline fun <reified P : Parcelable> Bundle.getParcelableWithCompat(name: String): P? {
    return BundleCompat.getParcelable<P>(this, name, P::class.java)
}

inline fun <reified S : Serializable> Bundle.getSerializableWithCompat(name: String): S? {
    return BundleCompat.getSerializable(this, name, S::class.java)
}

inline fun <reified P : Parcelable> Bundle.getParcelableArrayWithCompat(name: String): Array<Parcelable>? {
    return BundleCompat.getParcelableArray(this, name, P::class.java)
}

inline fun <reified P : Parcelable> Bundle.getParcelableArrayListWithCompat(name: String): ArrayList<P>? {
    return BundleCompat.getParcelableArrayList(this, name, P::class.java)
}