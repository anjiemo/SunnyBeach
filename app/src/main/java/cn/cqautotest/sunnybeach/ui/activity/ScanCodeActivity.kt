package cn.cqautotest.sunnybeach.ui.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.http.glide.GlideApp
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.model.scan.ScanResult
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.google.mlkit.vision.barcode.common.Barcode
import com.king.camera.scan.AnalyzeResult
import com.king.mlkit.vision.barcode.BarcodeCameraScanActivity
import com.king.mlkit.vision.barcode.BarcodeDecoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/20
 * desc   : 自定义的扫码界面，适配 MLKit 2.4.0 架构
 */
class ScanCodeActivity : BarcodeCameraScanActivity() {

    private var isFlashlightOpen = false

    override fun getLayoutId(): Int = R.layout.activity_scan_code

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initEvent()
    }

    private fun initEvent() {
        val ivPhoto = findViewById<ImageView>(R.id.ivPhoto)
        ivPhoto?.setFixOnClickListener {
            selectImage()
        }
        val ivFlash = findViewById<ImageView>(R.id.ivFlash)
        ivFlash?.setFixOnClickListener {
            toggleFlashlight(ivFlash)
        }
    }

    private fun toggleFlashlight(ivFlash: ImageView) {
        val isTorch = cameraScan.isTorchEnabled
        cameraScan.enableTorch(!isTorch)
        ivFlash.setImageResource(if (!isTorch) R.drawable.ic_flash else R.drawable.ic_flash_off)
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        }
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data ?: run {
                ToastUtils.showShort("未选择图片")
                return
            }
            parseScanResult(uri)
        }
    }

    private fun parseScanResult(uri: Uri) {
        Timber.d("parseScanResult：===> uri is $uri")
        lifecycleScope.launch {
            val bitmap = withContext(Dispatchers.IO) {
                try {
                    GlideApp.with(Utils.getApp())
                        .asBitmap()
                        .load(uri)
                        .submit()
                        .get()
                } catch (e: Exception) {
                    null
                }
            }
            if (bitmap == null) {
                ToastUtils.showShort("图片加载失败")
                return@launch
            }
            // MLKit 2.4.0 使用异步 process 方法
            BarcodeDecoder.process(BarcodeDecoder.fromBitmap(bitmap))
                .addOnSuccessListener { results ->
                    setResultAndFinish(results?.firstOrNull())
                }
                .addOnFailureListener {
                    ToastUtils.showShort("识别失败")
                }
        }
    }

    override fun onScanResultCallback(result: AnalyzeResult<MutableList<Barcode>>) {
        // 扫码结果回调，基类会自动处理停止扫描
        val barcode = result.result.firstOrNull()
        setResultAndFinish(barcode)
    }

    private fun setResultAndFinish(barcode: Barcode?) {
        val intent = Intent()
        val scanValue = barcode?.displayValue
        Timber.d("setResultAndFinish：===> scanValue is $scanValue")
        if (scanValue != null) {
            val type = when (barcode.valueType) {
                Barcode.TYPE_URL -> ScanResult.URL_FORM
                else -> ScanResult.PURE_TEXT_FORM
            }
            val scanResult = ScanResult(scanValue, type)
            intent.putExtra("SCAN_RESULT", scanResult)
            setResult(RESULT_OK, intent)
            finish()
        } else {
            // 如果是从相册识别失败，给予提示
            ToastUtils.showShort("未识别到二维码内容")
        }
    }

    companion object {
        private const val REQUEST_CODE = 0x123456
    }
}