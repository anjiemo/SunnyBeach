package com.example.blogsystem.ui.activity

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import androidx.annotation.WorkerThread
import androidx.lifecycle.lifecycleScope
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ObjectUtils
import com.bumptech.glide.Glide
import com.example.blogsystem.base.BaseActivity
import com.example.blogsystem.databinding.ActivityBrowserBinding
import com.example.blogsystem.utils.*
import com.hjq.bar.OnTitleBarListener
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BrowserActivity : BaseActivity() {

    private lateinit var binding: ActivityBrowserBinding
    private var mTitle: String? = ""
    private var mUrl: String? = ""
    private var uploadMessage: ValueCallback<Uri>? = null
    private var uploadMessageAboveL: ValueCallback<Array<Uri>>? = null
    private var mOpenId: String? = null // 用户的openid

    private var mNickName: String? = "default_feedback_nick_name" // 用户的nickname

    // 用户的头像url
    private var mHeadImgUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBrowserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        callAllInit()
    }

    override fun initEvent() {
        val titleBar = binding.titleBar
        val webView = binding.webView
        titleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(v: View) {
                finish()
            }

            override fun onTitleClick(v: View) {}

            override fun onRightClick(v: View) {
                webView.reload()
            }
        })
        webView.setOnLongClickListener {
            val result: WebView.HitTestResult = webView.hitTestResult
            when (result.type) {
                WebView.HitTestResult.IMAGE_TYPE, WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE -> lifecycleScope.launch {
                    runCatching {
                        withContext(Dispatchers.IO) { parseQrImage(result) }
                    }.onSuccess { message ->
                        simpleToast(message ?: "没有识别到任何内容")
                    }
                }
            }
            true
        }
    }

    /**
     * 只能在子线程中调用
     */
    @WorkerThread
    @Throws(Exception::class)
    private fun parseQrImage(result: WebView.HitTestResult): String? {
        val extra = result.extra
        val drawable = Glide.with(this).asDrawable().load(extra).submit().get()
        return QRCodeDecoder.syncDecodeQRCode(ConvertUtils.drawable2Bitmap(drawable))
    }

    override fun initView() {
        fullWindow()
        initWebClient()
        val webView = binding.webView
        intent.let {
            mUrl = it.getStringExtra(STR_URL)
            logByDebug(msg = "initView：===> url： $mUrl")
            mTitle = it.getStringExtra(STR_TITLE)
            mOpenId = it.getStringExtra(OPEN_ID)
            mNickName = it.getStringExtra(NICK_NAME)
            mHeadImgUrl = it.getStringExtra(HEADER_IMG_URL)
        }
        /* 准备post参数 */
        webView.postUrl(
            mUrl, StringBuilder()
                .append("nickname=")
                .append(mNickName)
                .append("&avatar=")
                .append(mHeadImgUrl)
                .append("&openid=")
                .append(mOpenId).toString().toByteArray()
        )
    }

    private fun initWebClient() {
        val titleBar = binding.titleBar
        val progressBar = binding.progressBar
        val webView = binding.webView
        window.setFormat(PixelFormat.TRANSLUCENT)
        webView.webViewClient = object : WebViewClient() {
            /**
             * 防止加载网页时调起系统浏览器
             */
            override fun shouldOverrideUrlLoading(webView: WebView, url: String?): Boolean {
                url ?: return false
                try {
                    if (url.startsWith("weixin://")) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                        return true
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    return false
                }
                webView.loadUrl(url)
                return true
            }

            override fun onPageStarted(webView: WebView, s: String?, bitmap: Bitmap?) {}

            override fun onPageFinished(webView: WebView, s: String?) {
                if (TextUtils.isEmpty(mTitle)) {
                    mTitle = webView.title ?: ""
                }
                titleBar.title = mTitle
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                uploadMessageAboveL = filePathCallback
                openImageChooserActivity()
                return true
            }

            override fun onProgressChanged(webView: WebView, progress: Int) {
                progressBar.visibility = View.VISIBLE
                progressBar.progress = progress
                if (progress >= 100) {
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun openImageChooserActivity() {
        //调用自己的图库
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.type = "image/*"
        startActivityForResult(
            Intent.createChooser(i, "Image Chooser"),
            FILE_CHOOSER_RESULT_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_CHOOSER_RESULT_CODE) { //处理返回的图片，并进行上传
            if (null == uploadMessage && null == uploadMessageAboveL) return
            val result = if (data == null || resultCode != RESULT_OK) null else data.data
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data)
            } else if (uploadMessage != null) {
                uploadMessage?.onReceiveValue(result)
                uploadMessage = null
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun onActivityResultAboveL(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE) return
        if (resultCode == RESULT_OK) {
            intent?.let {
                val dataString = intent.dataString
                val clipData = intent.clipData
                var results: Array<Uri>? = null
                if (clipData != null) {
                    results = Array(clipData.itemCount) {
                        clipData.getItemAt(it).uri
                    }
                }
                if (dataString != null) results = arrayOf(Uri.parse(dataString))
                uploadMessageAboveL?.onReceiveValue(results)
            }
        }
        uploadMessageAboveL = null
    }

    /**
     * 拦截back键，改为切换到上一次浏览的网页
     *
     * @param keyCode
     * @param event
     * @return
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val webView = binding.webView
        if (ObjectUtils.isNotEmpty(webView)) {
            if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                // 后退网页并且拦截该事件
                webView.goBack()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.webView.destroy()
    }

    companion object {
        const val STR_TITLE = "title"
        const val STR_URL = "url"
        const val FILE_CHOOSER_RESULT_CODE = 10000

        fun startActivity(
            context: Context,
            url: String,
            title: String? = null,
            openId: String,
            nickName: String,
            avatar: String
        ) {
            val intent = Intent(context, BrowserActivity::class.java).apply {
                putExtra(STR_URL, url)
                putExtra(STR_TITLE, title)
                putExtra(OPEN_ID, openId)
                putExtra(NICK_NAME, nickName)
                putExtra(HEADER_IMG_URL, avatar)
            }
            context.startActivity(intent)
        }
    }
}