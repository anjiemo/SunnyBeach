package cn.cqautotest.sunnybeach.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.ktx.createIntent
import cn.cqautotest.sunnybeach.ktx.fromJson
import cn.cqautotest.sunnybeach.ktx.startActivity
import cn.cqautotest.sunnybeach.ktx.toJson
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions

class ScanProxyActivity : AppActivity() {

    override fun getLayoutId() = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val options = fromJson<HmsScanAnalyzerOptions>(intent.getStringExtra(HMS_SCAN_ANALYZER_OPTIONS))
        ScanUtil.startScan(this, 1, options)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            setResult(resultCode, data)
            finish()
        }
    }

    companion object {

        private const val HMS_SCAN_ANALYZER_OPTIONS = "HMS_SCAN_ANALYZER_OPTIONS"

        fun start(context: Context, options: HmsScanAnalyzerOptions) {
            context.startActivity<ScanProxyActivity> {
                putExtra(HMS_SCAN_ANALYZER_OPTIONS, options.toJson())
            }
        }

        fun createIntent(context: Context, options: HmsScanAnalyzerOptions): Intent {
            return context.createIntent(context, ScanProxyActivity::class.java)
                .putExtra(HMS_SCAN_ANALYZER_OPTIONS, options.toJson())
        }
    }
}