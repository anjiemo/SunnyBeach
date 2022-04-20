package cn.cqautotest.sunnybeach.util

import android.app.Activity
import android.content.Intent
import cn.cqautotest.sunnybeach.ui.activity.ScanCodeActivity
import com.hjq.gson.factory.GsonFactory
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions

val Any.TAG
    get() = javaClass.simpleName

fun Any.toJson() = GsonFactory.getSingletonGson().toJson(this)

inline fun <reified T> Any.fromJson(json: String?) =
    GsonFactory.getSingletonGson().fromJson(json, T::class.java)

fun startScan(activity: Activity, requestCode: Int, options: HmsScanAnalyzerOptions) {
    val intent = Intent(activity, ScanCodeActivity::class.java)
    activity.startActivityForResult(intent, requestCode)
}