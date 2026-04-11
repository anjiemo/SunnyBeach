package cn.cqautotest.sunnybeach.model.scan

import android.os.Parcel
import android.os.Parcelable

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2025/10/17
 * desc   : 扫码结果
 */
sealed interface ScanCodeResult

/**
 * 可以解析 userId
 */
data class CanParseUserId(val userId: String, val scanResult: ScanResult) : ScanCodeResult

/**
 * 扫码识别到的内容
 */
data class Content(val scanResult: ScanResult) : ScanCodeResult

/**
 * 没有识别到内容
 */
data object NoContent : ScanCodeResult

/**
 * 取消了扫码
 */
data object CancelScan : ScanCodeResult

/**
 * 通用的扫码结果包装类，用于替代 HmsScan
 */
data class ScanResult(
    val searchValue: String,
    val type: Int = PURE_TEXT_FORM
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(searchValue)
        parcel.writeInt(type)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ScanResult> {
        override fun createFromParcel(parcel: Parcel): ScanResult = ScanResult(parcel)
        override fun newArray(size: Int): Array<ScanResult?> = arrayOfNulls(size)

        // 模仿 HmsScan 的类型常量，用于对齐 ScanResultActivity 的逻辑
        const val PURE_TEXT_FORM = 0
        const val URL_FORM = 1
    }

    fun getShowResult() = searchValue
    fun getOriginalValue() = searchValue
    fun getScanTypeForm() = type
}