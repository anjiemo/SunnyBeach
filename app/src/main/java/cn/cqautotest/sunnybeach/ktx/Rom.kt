package cn.cqautotest.sunnybeach.ktx

import android.os.Build
import android.os.Process
import androidx.annotation.RequiresApi

val is64Bit
    get() = Build.SUPPORTED_64_BIT_ABIS.isNotEmpty()

val is32Bit
    get() = is64Bit.not()

val is64BitRuntime
    @RequiresApi(Build.VERSION_CODES.M)
    get() = Process.is64Bit()