package cn.cqautotest.sunnybeach.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppApplication
import cn.cqautotest.sunnybeach.databinding.ImageChooseItemBinding
import cn.cqautotest.sunnybeach.databinding.PutFishActivityBinding
import cn.cqautotest.sunnybeach.event.LiveBusKeyConfig
import cn.cqautotest.sunnybeach.event.LiveBusUtils
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.ktx.asViewBinding
import cn.cqautotest.sunnybeach.ktx.clearText
import cn.cqautotest.sunnybeach.ktx.context
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.fromJson
import cn.cqautotest.sunnybeach.ktx.hideKeyboard
import cn.cqautotest.sunnybeach.ktx.requireKeyboardHeight
import cn.cqautotest.sunnybeach.ktx.setDefaultEmojiParser
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.ktx.simpleToast
import cn.cqautotest.sunnybeach.ktx.textString
import cn.cqautotest.sunnybeach.ktx.toJson
import cn.cqautotest.sunnybeach.model.FishPondTopicList
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.repository.Repository
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.dialog.InputDialog
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import cn.cqautotest.sunnybeach.widget.recyclerview.GridSpaceDecoration
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.bumptech.glide.Glide
import com.hjq.bar.TitleBar
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/11
 * desc   : 发布摸鱼的界面
 */
class PutFishActivity : AppActivity(), ImageSelectActivity.OnPhotoSelectListener {

    private val mBinding by viewBinding(PutFishActivityBinding::bind)
    private val mFishPondViewModel by viewModels<FishPondViewModel>()
    private val mAdapterDelegate = AdapterDelegate()
    private val mPreviewAdapter = ImagePreviewAdapter(mAdapterDelegate)
    private var mTopicId: String? = null
    private var mLinkUrl: String? = null

    override fun getLayoutId(): Int = R.layout.put_fish_activity

    @SuppressLint("SetTextI18n")
    override fun initView() {
        mBinding.etInputContent.setDefaultEmojiParser()
        mBinding.tvInputLength.text = "0/$INPUT_MAX_LENGTH"
        val rvPreviewImage = mBinding.rvPreviewImage
        rvPreviewImage.apply {
            layoutManager = GridLayoutManager(context, 4)
            adapter = mPreviewAdapter
            addItemDecoration(GridSpaceDecoration(4.dp))
        }
    }

    override fun initData() {

    }

    @SuppressLint("SetTextI18n")
    override fun initEvent() {
        with(mBinding) {
            etInputContent.requestFocus()
            postDelayed({ showKeyboard(etInputContent) }, 100)
            rlChooseFishPond.setFixOnClickListener {
                // 选择鱼塘
                startActivityForResult(FishPondSelectionActivity::class.java) { resultCode, data ->
                    takeUnless { resultCode == Activity.RESULT_OK }?.let { return@startActivityForResult }
                    if (data != null) {
                        val fishPondTopicListItem =
                            fromJson<FishPondTopicList.TopicItem>(data.getStringExtra(IntentKey.OTHER))
                        mTopicId = fishPondTopicListItem.id
                        tvChooseFishPond.text = "#${fishPondTopicListItem.topicName}#"
                        tvChooseFishPondDesc.clearText()
                    } else {
                        resetTopicSelection()
                    }
                }
            }
            ivEmoji.setFixOnClickListener {
                // 键盘显示的时候隐藏表情列表，键盘隐藏的时候显示表情列表
                toggleSoftInput(etInputContent)
            }
            keyboardLayout.setKeyboardListener { isActive, _ ->
                val navigationBarHeight =
                    ViewCompat.getRootWindowInsets(mBinding.root)?.getInsets(WindowInsetsCompat.Type.navigationBars())?.bottom ?: 0
                // Timber.d("initEvent：===> navigationBarHeight is $navigationBarHeight")

                val keyboardHeight = mBinding.root.requireKeyboardHeight()
                // Timber.d("initEvent：===> keyboardHeight is $keyboardHeight")
                if (isActive) {
                    rvEmojiList.updateLayoutParams {
                        // 此处应该减去底部导航栏的高度，否则在经典导航栏模式下高度过剩
                        height = keyboardHeight - navigationBarHeight
                    }
                }
                val emojiIcon = if (isActive) R.mipmap.ic_emoji_normal else R.mipmap.ic_keyboard
                Glide.with(context)
                    .load(emojiIcon)
                    .into(ivEmoji)
            }
            rvEmojiList.setOnEmojiClickListener { emoji, _ ->
                val cursor = etInputContent.selectionStart
                etInputContent.text.insert(cursor, emoji)
            }
            ivImage.setFixOnClickListener {
                // 选择图片，跳转至图片选择界面
                ImageSelectActivity.start(this@PutFishActivity, MAX_SELECT_IMAGE_COUNT, this@PutFishActivity)
            }
            ivLink.setFixOnClickListener {
                // 弹出链接输入对话框，添加 url 链接
                InputDialog.Builder(context)
                    .setTitle("添加链接")
                    .setHint("http(s)://")
                    .setContent(mLinkUrl)
                    .setCanceledOnTouchOutside(false)
                    .setListener { _, content ->
                        mLinkUrl = content
                        simpleToast(content)
                    }.show()
            }
            etInputContent.setFixOnClickListener {
                keyboardLayout.postDelayed({
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                }, 250)
            }
            val normalColor = "#CBD0D3".toColorInt()
            val overflowColor = Color.RED
            // 最大字符输入长度
            val maxInputTextLength = INPUT_MAX_LENGTH
            // 最小字符输入长度
            val minInputTextLength = 5
            etInputContent.addTextChangedListener {
                val inputLength = etInputContent.length()
                // 判断输入的字符长度是否溢出
                val isOverflow = (maxInputTextLength - inputLength) < 0
                mBinding.tvInputLength.text = "${inputLength}/$maxInputTextLength"
                // 判断输入的字符串长度是否超过最大长度
                mBinding.tvInputLength.setTextColor(if (inputLength < minInputTextLength || isOverflow) overflowColor else normalColor)
            }
        }
        mAdapterDelegate.setOnItemClickListener { _, position ->
            ImagePreviewActivity.start(this, mPreviewAdapter.getData(), position)
        }
    }

    private fun resetTopicSelection() {
        mBinding.tvChooseFishPond.text = "选择鱼塘"
        val tvChooseFishPondDesc = mBinding.tvChooseFishPondDesc
        tvChooseFishPondDesc.text = "放到合适的鱼塘会被更多的摸鱼人看见哟~"
    }

    override fun onRightClick(titleBar: TitleBar) {
        val view = titleBar.rightView
        view?.isEnabled = false

        // 校验内容是否合法，发布信息
        val inputLength = mBinding.etInputContent.length()
        val textLengthIsOk = inputLength in 5..INPUT_MAX_LENGTH
        takeUnless { textLengthIsOk }?.let {
            simpleToast("请输入[5, $INPUT_MAX_LENGTH]个字符~")
            view?.isEnabled = true
            return
        }

        // 摸鱼内容
        val content = mBinding.etInputContent.textString
        val images = mPreviewAdapter.getData()
        showDialog()
        val dispatcher = Dispatchers.IO
        // 异常处理
        val exceptionHandler = CoroutineExceptionHandler { _, cause ->
            finishPutFish()
            when (cause) {
                is CancellationException -> {}
                else -> toast("发布失败\uD83D\uDE2D ${cause.message}")
            }
        }
        // 上传图片，此处的 path 为客户端本地的路径，需要上传到服务器上，获取网络 url 路径
        val uploadedImages = arrayListOf<Pair<Int, String>>()
        lifecycleScope.launch {
            withContext(dispatcher) {
                // 1、压缩图片
                // 2、上传图片
                // 阻塞当前协程，直到内部的协程结束任务或引发异常，以便我们在图片上传之前不会执行发布动态的操作
                coroutineScope { zipAndUploadImages(dispatcher, images, uploadedImages, exceptionHandler) }
                Timber.d("onRightClick：===> uploadedImages size is ${uploadedImages.size}")
                // 如果待上传的图片列表大小和已上传的图片列表大小不相等则代表有图片上传失败了，我们直接终止摸鱼动态的发布
                if (images.size != uploadedImages.size) {
                    finishPutFish()
                    // 有图片上传失败，取消当前协程
                    cancel()
                }
                // 3、发布摸鱼（需要按照选择的图片顺序进行排序，否则图片列表是乱序的）
                withContext(Dispatchers.Main) {
                    putFish(content, uploadedImages.sortedBy { it.first }.map { it.second })
                }
            }
        }
    }

    /**
     * 1、压缩图片
     * 2、上传图片
     * images：待上传图片文件路径集合
     * uploadedImages：上传后的图片 url 集合
     */
    private fun CoroutineScope.zipAndUploadImages(
        dispatcher: CoroutineDispatcher,
        images: List<String>,
        uploadedImages: ArrayList<Pair<Int, String>>,
        exceptionHandler: CoroutineExceptionHandler
    ) {
        images.withIndex()
            .forEach { imageMap ->
                flowOf(imageMap)
                    .onEach { (index, filePath) ->
                        Timber.d("zipAndUploadImages：===> filePath is $filePath")
                        // 把需要上传的图片文件复制到缓存目录
                        val cacheFilePath = File(filePath).copyToCacheDirOrThrow()
                        // 压缩图片文件
                        val zippedFilePath = zipImageFile(cacheFilePath).getOrThrow()
                        // 上传摸鱼图片
                        val onlineImgUrl = Repository.uploadFishImage(zippedFilePath).getOrThrow()
                        // 添加到已上传的图片 url 集合
                        uploadedImages.add(index to onlineImgUrl)
                    }
                    .flowOn(dispatcher)
                    // 服务器错误时，重试三次，每次间隔 100ms
                    .retryWhen { cause, attempt ->
                        val needRetry = (cause is ServiceException) && attempt < 3
                        if (needRetry) delay(100L)
                        needRetry
                    }
                    .catch { exceptionHandler.handleException(dispatcher, it) }
                    .launchIn(this)
            }
    }

    /**
     * 复制到缓存目录或抛出异常
     */
    private fun File.copyToCacheDirOrThrow(): File {
        val targetFile = File(PathUtils.getExternalAppCachePath(), name.fixSuffix())
        val copySuccess = FileUtils.copy(this, targetFile)
        takeIf { copySuccess }?.let { return targetFile } ?: throw RuntimeException("文件拷贝失败")
    }

    /**
     * 重命名后缀
     */
    private fun String.fixSuffix() = replace("jpeg", "png").replace("jpg", "png")

    /**
     * 发布动态内容（包括文字和图片）
     */
    private fun putFish(content: String, imageUrls: List<String>) {
        Timber.d("putFish：===> imageUrls is ${imageUrls.toJson()}")
        // 2021/9/12 填充 “链接”（客户端暂不支持）
        val map = mapOf(
            "content" to content,
            "topicId" to mTopicId,
            "linkUrl" to mLinkUrl,
            "images" to imageUrls,
        )
        // 图片上传完成，可以发布摸鱼
        mFishPondViewModel.putFish(map).observe(this) { result ->
            finishPutFish()
            result.onSuccess {
                // 重置界面状态
                mTopicId = null
                mLinkUrl = null
                mPreviewAdapter.setData(listOf())
                mBinding.etInputContent.clearText()
                resetTopicSelection()
                simpleToast("发布非常成功😃")
                LiveBusUtils.busSend(LiveBusKeyConfig.BUS_PUT_FISH_SUCCESS)
                setResult(Activity.RESULT_OK)
                finish()
            }.onFailure {
                simpleToast("发布失败😭 ${it.message}")
            }
        }
    }

    /**
     * 结束发布摸鱼操作
     */
    private fun finishPutFish() {
        post {
            hideDialog()
            getTitleBar()?.rightView?.isEnabled = true
        }
    }

    /**
     * 根据原始图片文件路径压缩图片文件到指定路径
     */
    private suspend fun zipImageFile(imgFile: File): Result<File> = suspendCoroutine { con ->
        // val unZipFileSize = Formatter.formatFileSize(this, imgFile.length())
        // Timber.d("zipImageFile：===> unZipFileSize is $unZipFileSize")
        Luban.with(AppApplication.getInstance())
            .load(imgFile)
            .ignoreBy(TIMES)
            .filter { it.isNotBlank() }
            .setTargetDir(PathUtils.getExternalAppCachePath())
            .setCompressListener(object : OnCompressListener {

                override fun onStart() {
                    // 压缩开始前调用
                    // Ignore this callback, because we don't want to do anything.
                }

                override fun onSuccess(file: File) {
                    // val zippedFileSize = Formatter.formatFileSize(context, file.length())
                    // Timber.d("zipImageFile：===> zippedFileSize is $zippedFileSize")
                    con.resume(Result.success(file))
                }

                override fun onError(e: Throwable?) {
                    con.resumeWithException(e ?: RuntimeException("图片压缩失败"))
                    // 当压缩过程出现问题时调用
                    e?.printStackTrace()
                }
            }).launch()
    }

    override fun onSelected(data: MutableList<String>) {
        mPreviewAdapter.setData(data.toList())
        Timber.d("===> images path is $data")
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard()
    }

    private class ImagePreviewViewHolder(val binding: ImageChooseItemBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.asViewBinding<ImageChooseItemBinding>())

        fun onBinding(item: String?, position: Int) {
            item ?: return
            with(binding) {
                Glide.with(itemView)
                    .load(item)
                    .into(ivPhoto)
                Glide.with(itemView)
                    .load(R.drawable.clear_ic)
                    .into(ivClear)
                ivClear.setFixOnClickListener { v ->
                    (bindingAdapter as? ImagePreviewAdapter)?.let {
                        val data = it.getDataSource()
                        data.removeAt(bindingAdapterPosition)
                        it.notifyItemRemoved(bindingAdapterPosition)
                        it.clearImageListener.invoke(v, bindingAdapterPosition)
                    }
                }
            }
        }
    }

    private class ImagePreviewAdapter(
        private val adapterDelegate: AdapterDelegate,
        private val mData: MutableList<String> = arrayListOf()
    ) :
        RecyclerView.Adapter<ImagePreviewViewHolder>() {

        var previewImageListener: (view: View, position: Int) -> Unit = { _, _ -> }
        var clearImageListener: (view: View, position: Int) -> Unit = { _, _ -> }

        @SuppressLint("NotifyDataSetChanged")
        fun setData(data: List<String>) {
            mData.clear()
            mData.addAll(data)
            notifyDataSetChanged()
        }

        fun getData() = mData.toList()

        context (_: RecyclerView.ViewHolder)
        fun getDataSource() = mData

        fun setOnItemClickListener(
            previewImage: (view: View, position: Int) -> Unit,
            clearImage: (view: View, position: Int) -> Unit
        ) {
            previewImageListener = previewImage
            clearImageListener = clearImage
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagePreviewViewHolder =
            ImagePreviewViewHolder(parent)

        override fun onBindViewHolder(holder: ImagePreviewViewHolder, position: Int) {
            holder.itemView.setFixOnClickListener { adapterDelegate.onItemClick(it, holder.bindingAdapterPosition) }
            holder.onBinding(mData.getOrNull(position), position)
        }

        override fun getItemCount(): Int = mData.size
    }

    companion object {

        private const val MAX_SELECT_IMAGE_COUNT = 9

        // 超过 800 kb 的图片将会被压缩（目前网站最大只支持 1000 kb）
        private const val TIMES = 800

        private const val INPUT_MAX_LENGTH = 1024
    }
}