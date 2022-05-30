package cn.cqautotest.sunnybeach.ktx

import android.content.Context
import androidx.viewbinding.ViewBinding

val ViewBinding.context: Context
    get() = root.context