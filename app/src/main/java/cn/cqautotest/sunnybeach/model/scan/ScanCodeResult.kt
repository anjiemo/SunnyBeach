package cn.cqautotest.sunnybeach.model.scan

import com.huawei.hms.ml.scan.HmsScan

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
data class CanParseUserId(val userId: String, val hmsScan: HmsScan) : ScanCodeResult

/**
 * 扫码识别到的内容
 */
data class Content(val hmsScan: HmsScan) : ScanCodeResult

/**
 * 没有识别到内容
 */
data object NoContent : ScanCodeResult

/**
 * 取消了扫码
 */
data object CancelScan : ScanCodeResult