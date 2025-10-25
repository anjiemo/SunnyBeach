package cn.cqautotest.sunnybeach.ui.activity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaScannerConnection
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.SobCardActivityBinding
import cn.cqautotest.sunnybeach.ktx.context
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.setTintColor
import cn.cqautotest.sunnybeach.ktx.startActivity
import cn.cqautotest.sunnybeach.ktx.toQrCodeBitmapOrNull
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.util.PosterShareDelegate
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_VIEW_USER_URL_PRE
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.IntentUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.SizeUtils
import com.bumptech.glide.Glide
import com.dylanc.longan.context
import com.dylanc.longan.intentExtras
import com.hjq.bar.TitleBar
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.regex.Pattern

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/07/07
 * desc   : 阳光沙滩证查看界面
 */
class SobCardActivity : AppActivity() {

    private val mBinding by viewBinding(SobCardActivityBinding::bind)
    private val mSobId by intentExtras(SOB_ID, "")
    private var mShareSobCardJob: Job? = null

    override fun getLayoutId() = R.layout.sob_card_activity

    override fun initView() {
        val nickName = UserManager.loadUserBasicInfo()?.nickname.orEmpty().ifEmpty { "未获取到昵称" }
        with(mBinding.includeSobCardFront) {
            tvNickName.text = nickName
            tvSobId.text = mSobId.manicured()
            val qrContent = "$SUNNY_BEACH_VIEW_USER_URL_PRE${mSobId}"
            val bitmap = qrContent.toQrCodeBitmapOrNull(size = 100.dp, bgColor = Color.TRANSPARENT, qrColor = Color.WHITE, margin = 0)
            Glide.with(context)
                .load(bitmap.setTintColor(Color.WHITE))
                .into(ivSobQrCode)
        }
    }

    private fun String.manicured(): String {
        val matcher = pattern.matcher(this)
        return matcher.replaceAll("$1\u3000$2\u3000$3\u3000$4\u3000$5\u3000$6")
    }

    override fun initData() {

    }

    override fun initEvent() {

    }

    override fun onRightClick(titleBar: TitleBar) {
        mShareSobCardJob?.cancel()
        mShareSobCardJob = lifecycleScope.launch {
            PosterShareDelegate((window?.decorView as? ViewGroup) ?: return@launch)
                .setOnPosterShareListener {
                    updateViewConfig { view ->
                        mBinding.includeSobCardFront.run {
                            view.findViewById<ImageView>(R.id.iv_sob_qr_code).setImageDrawable(ivSobQrCode.drawable)
                            view.findViewById<TextView>(R.id.tv_sob_id).text = tvSobId.text.replace("\u3000".toRegex(), "  ")
                        }
                    }
                    updateViewLayoutParams {
                        updateLayoutParams<ViewGroup.MarginLayoutParams> {
                            width = SizeUtils.dp2px(300f)
                            height = ViewGroup.LayoutParams.WRAP_CONTENT
                        }
                    }
                    onComplete { bitmap ->
                        val sobCardFile = saveShareQrCardToFile(bitmap)
                        val intent = IntentUtils.getShareImageIntent(sobCardFile)
                        startActivity(intent)
                    }
                }
                .launch(R.layout.share_sob_card)
        }
    }

    private suspend fun saveShareQrCardToFile(bitmap: Bitmap?): File {
        return withContext(Dispatchers.IO) {
            File(PathUtils.getExternalDcimPath(), "sob_image_share_${System.currentTimeMillis()}.png").also {
                ImageUtils.save(bitmap, it, Bitmap.CompressFormat.PNG)
                withContext(Dispatchers.Main) { toast("图片已保存到本地相册") }
            }
        }.also { MediaScannerConnection.scanFile(context, arrayOf(it.path), arrayOf("image/*"), null) }
    }

    companion object {

        private const val SOB_ID = "sob_id"

        // 分隔规则
        private const val REGEX = "(\\w{4})(\\w{3})(\\w{3})(\\w{3})(\\w{3})(\\w{3})"
        private val pattern = Pattern.compile(REGEX)

        fun start(context: Context, sobId: String) {
            context.startActivity<SobCardActivity> {
                putExtra(SOB_ID, sobId)
            }
        }
    }
}