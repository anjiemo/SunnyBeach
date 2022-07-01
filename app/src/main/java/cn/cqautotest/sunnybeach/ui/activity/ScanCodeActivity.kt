package cn.cqautotest.sunnybeach.ui.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.Init
import cn.cqautotest.sunnybeach.http.glide.GlideApp
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.manager.ThreadPoolManager
import com.dylanc.longan.toast
import com.huawei.hms.hmsscankit.ScanKitActivity
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/20
 * desc   : 自定义的扫码界面，暂支持扫描二维码
 */
class ScanCodeActivity : ScanKitActivity(), Init {

    private var instanceState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instanceState = savedInstanceState
        initEvent()
    }

    override fun initEvent() {
        findViewById<View>(R.id.img_btn).setFixOnClickListener {
            selectImage()
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data ?: run {
                toast("未选择图片")
                return
            }
            parseScanResult(uri)
        } else {
            toast("未选择图片")
        }
    }

    private fun parseScanResult(uri: Uri) {
        ThreadPoolManager.getInstance().execute {
            val bitmap = GlideApp.with(this)
                .asBitmap()
                .load(uri)
                .submit()
                .get()
            // “QRCODE_SCAN_TYPE”和“DATAMATRIX_SCAN_TYPE”表示只扫描QR和Data Matrix的码
            val options = HmsScanAnalyzerOptions.Creator()
                .setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE)
                .create()
            val hmsScans = ScanUtil.decodeWithBitmap(this@ScanCodeActivity, bitmap, options)
            setResultAndFinish(hmsScans)
        }
    }

    private fun setResultAndFinish(hmsScans: Array<out HmsScan>?) {
        val intent = Intent()
        val hmsScan = if (hmsScans.isNullOrEmpty()) null else hmsScans[0]
        intent.putExtra(ScanUtil.RESULT, hmsScan)
        setResult(RESULT_OK, intent)
        finish()
    }

    companion object {
        private const val REQUEST_CODE = 0x123456
    }
}