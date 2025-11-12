package cn.cqautotest.sunnybeach.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.net.toUri
import cn.cqautotest.sunnybeach.action.CheckUserParseAction
import cn.cqautotest.sunnybeach.ktx.orEmpty
import cn.cqautotest.sunnybeach.model.scan.CanParseUserId
import cn.cqautotest.sunnybeach.model.scan.CancelScan
import cn.cqautotest.sunnybeach.model.scan.Content
import cn.cqautotest.sunnybeach.model.scan.NoContent
import cn.cqautotest.sunnybeach.model.scan.ScanCodeResult
import cn.cqautotest.sunnybeach.repository.CheckUserParseRepository
import cn.cqautotest.sunnybeach.ui.activity.ScanCodeActivity
import com.hjq.base.ktx.getParcelableExtraWithCompat
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2025/10/16
 * desc   : 获取扫描内容
 */
class GetScanContent(checkUserRepository: CheckUserParseRepository = CheckUserParseRepository()) :
    ActivityResultContract<HmsScanAnalyzerOptions, ScanCodeResult>(), CheckUserParseAction by checkUserRepository {

    override fun createIntent(context: Context, input: HmsScanAnalyzerOptions): Intent {
        return Intent(context, ScanCodeActivity::class.java).apply {
            putExtra("ScanFormatValue", input.mode)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): ScanCodeResult {
        if (resultCode != Activity.RESULT_OK || intent == null) {
            return CancelScan
        }
        return intent.getParcelableExtraWithCompat<HmsScan>(ScanUtil.RESULT)?.let {
            Timber.d("parseResult：===> hmsScan originalValue is ${it.originalValue}")
            val (canBeParse, userId) = canBeParse(it)
            if (canBeParse) {
                CanParseUserId(userId, it)
            } else {
                Content(it)
            }
        } ?: NoContent
    }

    private fun canBeParse(hmsScan: HmsScan?): Pair<Boolean, String> {
        val content = hmsScan?.getShowResult() ?: return false to ""
        // content can never be null.
        val uri = content.toUri()
        val scheme = uri.scheme.orEmpty()
        val authority = uri.authority.orEmpty()
        val userId = uri.lastPathSegment.orEmpty()
        Timber.d("canBeParse：===> scheme is $scheme authority is $authority userId is $userId")
        Timber.d("canBeParse：===> content is $content")
        return when {
            !checkScheme(uri.scheme.orEmpty()) -> false to ""
            !checkAuthority(uri.authority.orEmpty()) -> false to ""
            !checkUserId(userId) -> false to ""
            else -> true to userId
        }
    }
}