package cn.cqautotest.sunnybeach.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Images
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.ScankitZxlCaptureNewBinding
import cn.cqautotest.sunnybeach.util.DownloadHelper
import cn.cqautotest.sunnybeach.util.setFixOnClickListener
import com.huawei.hms.hmsscankit.RemoteView
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/20
 * desc   : 自定义的扫码界面，暂支持扫描二维码
 */
class ScanCodeActivity : AppActivity() {

    private val mBinding by viewBinding<ScankitZxlCaptureNewBinding>()
    private var instanceState: Bundle? = null
    private lateinit var remoteView: RemoteView

    override fun getLayoutId(): Int = R.layout.scankit_zxl_capture_new

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instanceState = savedInstanceState
    }

    override fun initView() {
        remoteView = RemoteView.Builder()
            .setContext(this)
            .build()
        remoteView.setOnResultCallback {
            setResultAndFinish(it)
        }
        remoteView.onCreate(instanceState)
        mBinding.flContainer.addView(remoteView)
    }

    override fun initData() {

    }

    override fun initEvent() {
        mBinding.backImgIn.setFixOnClickListener {
            finish()
        }
        mBinding.imgBtn.setFixOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent) { resultCode, data ->
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val uri = data.data ?: run {
                        toast("未选择图片")
                        return@startActivityForResult
                    }
                    parseScanResult(uri)
                } else {
                    setResultAndFinish(null)
                }
            }
        }
    }

    private fun parseScanResult(uri: Uri) {
        lifecycleScope.launchWhenCreated {
            withContext(Dispatchers.IO) {
                val bitmap = DownloadHelper.ofType<Bitmap>(this@ScanCodeActivity, uri)
                // “QRCODE_SCAN_TYPE”和“DATAMATRIX_SCAN_TYPE”表示只扫描QR和Data Matrix的码
                val options = HmsScanAnalyzerOptions.Creator()
                    .setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE)
                    .create()
                val hmsScans = ScanUtil.decodeWithBitmap(this@ScanCodeActivity, bitmap, options)
                setResultAndFinish(hmsScans)
            }
        }
    }

    private fun setResultAndFinish(hmsScans: Array<out HmsScan>?) {
        val intent = Intent()
        val hmsScan = if (hmsScans.isNullOrEmpty()) null else hmsScans[0]
        intent.putExtra(ScanUtil.RESULT, hmsScan)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        remoteView.onStart()
    }

    override fun onPause() {
        super.onPause()
        remoteView.onPause()
    }

    override fun onResume() {
        super.onResume()
        remoteView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        remoteView.onDestroy()
    }

    override fun isStatusBarEnabled(): Boolean = false
}