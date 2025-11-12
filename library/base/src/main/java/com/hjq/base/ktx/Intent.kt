package com.hjq.base.ktx

import android.content.Intent
import android.os.Parcelable
import androidx.core.content.IntentCompat

inline fun <reified T : Parcelable> Intent.getParcelableExtraWithCompat(name: String): T? {
    return IntentCompat.getParcelableExtra(this, name, T::class.java)
}

inline fun <reified P : Parcelable> Intent.getParcelableArrayExtraWithCompat(name: String): Array<Parcelable>? {
    return IntentCompat.getParcelableArrayExtra(this, name, P::class.java)
}

inline fun <reified P : Parcelable> Intent.getParcelableArrayListExtraWithCompat(name: String): ArrayList<P>? {
    return IntentCompat.getParcelableArrayListExtra(this, name, P::class.java)
}