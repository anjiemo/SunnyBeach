package cn.cqautotest.sunnybeach.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.view.updateLayoutParams
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.ImageChooseItemBinding
import cn.cqautotest.sunnybeach.databinding.PutFishActivityBinding
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.http.network.Repository
import cn.cqautotest.sunnybeach.ktx.*
import cn.cqautotest.sunnybeach.model.FishPondTopicList
import cn.cqautotest.sunnybeach.other.GridSpaceDecoration
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.dialog.InputDialog
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import com.blankj.utilcode.constant.MemoryConstants
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.bumptech.glide.Glide
import com.gyf.immersionbar.ImmersionBar
import com.hjq.bar.TitleBar
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
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

    private val mBinding: PutFishActivityBinding by viewBinding()
    private val mFishPondViewModel by viewModels<FishPondViewModel>()
    private val mPreviewAdapter by lazy { ImagePreviewAdapter() }
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
        val etInputContent = mBinding.etInputContent
        etInputContent.requestFocus()
        postDelayed({ showKeyboard(etInputContent) }, 100)
        mBinding.rlChooseFishPond.setFixOnClickListener {
            // 选择鱼塘
            startActivityForResult(FishPondSelectionActivity::class.java) { resultCode, data ->
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        val fishPondTopicListItem = fromJsonByTypeToken<FishPondTopicList.TopicItem>(data.getStringExtra(IntentKey.OTHER))
                        mTopicId = fishPondTopicListItem.id
                        val tvChooseFishPondDesc = mBinding.tvChooseFishPondDesc
                        mBinding.tvChooseFishPond.text = "#${fishPondTopicListItem.topicName}#"
                        tvChooseFishPondDesc.clearText()
                    } else {
                        resetTopicSelection()
                    }
                }
            }
        }
        mBinding.ivEmoji.setFixOnClickListener {
            // 键盘显示的时候隐藏表情列表，键盘隐藏的时候显示表情列表
            toggleSoftInput(etInputContent)
        }
        mBinding.keyboardLayout.setKeyboardListener { isActive, _ ->
            val navigationBarHeight = ImmersionBar.getNavigationBarHeight(this)
            // Timber.d("initEvent：===> navigationBarHeight is $navigationBarHeight")

            val keyboardHeight = etInputContent.requireKeyboardHeight()
            // Timber.d("initEvent：===> keyboardHeight is $keyboardHeight")
            val rvEmojiList = mBinding.rvEmojiList
            if (isActive) {
                rvEmojiList.updateLayoutParams {
                    // 此处应该减去底部导航栏的高度，否则在经典导航栏模式下高度过剩
                    height = keyboardHeight - navigationBarHeight
                }
            }
            val emojiIcon = if (isActive) R.mipmap.ic_emoji_normal else R.mipmap.ic_keyboard
            Glide.with(this)
                .load(emojiIcon)
                .into(mBinding.ivEmoji)
        }
        mBinding.rvEmojiList.setOnEmojiClickListener { emoji, _ ->
            val cursor = etInputContent.selectionStart
            etInputContent.text.insert(cursor, emoji)
        }
        mBinding.ivImage.setFixOnClickListener {
            // 选择图片，跳转至图片选择界面
            ImageSelectActivity.start(this, MAX_SELECT_IMAGE_COUNT, this)
        }
        mBinding.ivLink.setFixOnClickListener {
            // 弹出链接输入对话框，添加 url 链接
            InputDialog.Builder(this)
                .setTitle("添加链接")
                .setHint("http(s)://")
                .setContent(mLinkUrl)
                .setCanceledOnTouchOutside(false)
                .setListener { _, content ->
                    mLinkUrl = content
                    simpleToast(content)
                }.show()
        }
        mBinding.etInputContent.setFixOnClickListener {
            mBinding.keyboardLayout.postDelayed({
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            }, 250)
        }
        val normalColor = Color.parseColor("#CBD0D3")
        val overflowColor = Color.RED
        mBinding.etInputContent.addTextChangedListener {
            // 最大字符输入长度
            val maxInputTextLength = INPUT_MAX_LENGTH
            // 最小字符输入长度
            val minInputTextLength = 5
            val inputLength = mBinding.etInputContent.length()
            // 判断输入的字符长度是否溢出
            val isOverflow = (maxInputTextLength - inputLength) < 0
            mBinding.tvInputLength.text = "${inputLength}/$maxInputTextLength"
            // 判断输入的字符串长度是否超过最大长度
            mBinding.tvInputLength.setTextColor(if (inputLength < minInputTextLength || isOverflow) overflowColor else normalColor)
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
        val images = mPreviewAdapter.getData().toList()
        showDialog()
        val dispatcher = Dispatchers.IO
        val exceptionHandler = CoroutineExceptionHandler { _, cause ->
            hideDialog()
            view?.isEnabled = true
            when (cause) {
                is CancellationException -> {}
                else -> toast("发布失败\uD83D\uDE2D ${cause.message}")
            }
        }
        // 上传图片，此处的 path 为客户端本地的路径，需要上传到服务器上，获取网络 url 路径
        val uploadedImages = arrayListOf<String>()
        lifecycleScope.launchWhenCreated {
            // 阻塞当前协程，直到内部的协程结束任务或引发异常，以便我们在图片上传之前不会执行发布动态的操作
            coroutineScope { zipAndUploadImages(dispatcher, images, uploadedImages, exceptionHandler) }
            Timber.d("onRightClick：===> uploadedImages size is ${uploadedImages.size}")
            // 3、发布摸鱼
            putFish(content, uploadedImages)
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
        uploadedImages: ArrayList<String>,
        exceptionHandler: CoroutineExceptionHandler
    ) {
        for (image in images) {
            flowOf(image)
                // 把需要上传的图片文件复制到缓存目录
                .map { filePath ->
                    Timber.d("onRightClick：===> filePath is $filePath")
                    File(filePath).copyToCacheDirOrThrow()
                }
                // 压缩图片文件
                .map { zipImageFile(it).getOrThrow() }
                // 上传摸鱼图片
                .map { Repository.uploadFishImage(it).getOrThrow() }
                .flowOn(dispatcher)
                // 添加到已上传的图片 url 集合
                .onEach { uploadedImages.add(it) }
                // 服务器错误时，重试三次，每次间隔 100ms
                .retryWhen { cause, attempt ->
                    val retry = (cause is ServiceException) && attempt < 3
                    takeIf { retry }?.let { delay(100) }
                    retry
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
        // 2021/9/12 填充 “链接”（客户端暂不支持），
        val map = mapOf(
            "content" to content,
            "topicId" to mTopicId,
            "linkUrl" to mLinkUrl,
            "images" to imageUrls,
        )
        // 图片上传完成，可以发布摸鱼
        mFishPondViewModel.putFish(map).observe(this) { result ->
            hideDialog()
            getTitleBar()?.rightView?.isEnabled = true
            result.onSuccess {
                // 重置界面状态
                mTopicId = null
                mLinkUrl = null
                mPreviewAdapter.setData(listOf())
                mBinding.etInputContent.clearText()
                resetTopicSelection()
                simpleToast("发布非常成功😃")
                setResult(Activity.RESULT_OK)
                finish()
            }.onFailure {
                simpleToast("发布失败😭 ${it.message}")
            }
        }
    }

    /**
     * 根据原始图片文件路径压缩图片文件到指定路径
     */
    private suspend fun zipImageFile(imgFile: File): Result<File> = suspendCoroutine { con ->
        Luban.with(this)
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
        mPreviewAdapter.setData(data.toMutableList())
        Timber.d("===> images path is $data")
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard()
    }

    companion object {

        private const val MAX_SELECT_IMAGE_COUNT = 9

        // 图片文件大小的阈值（4MB）
        private const val IMAGE_FILE_MAX_SIZE = 4 * MemoryConstants.MB

        // 计算出图片文件的阈值是 KB 的多少倍
        private const val TIMES = IMAGE_FILE_MAX_SIZE / MemoryConstants.KB

        private const val INPUT_MAX_LENGTH = 1024

        private class ImagePreviewAdapter(private val mData: MutableList<String> = arrayListOf()) :
            RecyclerView.Adapter<ImagePreviewViewHolder>() {

            private var previewImageListener: (view: View, position: Int) -> Unit = { _, _ -> }
            private var clearImageListener: (view: View, position: Int) -> Unit = { _, _ -> }

            @SuppressLint("NotifyDataSetChanged")
            fun setData(data: List<String>) {
                mData.clear()
                mData.addAll(data)
                notifyDataSetChanged()
            }

            fun getData() = mData.toList()

            fun setOnItemClickListener(
                previewImage: (view: View, position: Int) -> Unit,
                clearImage: (view: View, position: Int) -> Unit
            ) {
                previewImageListener = previewImage
                clearImageListener = clearImage
            }

            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): ImagePreviewViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ImageChooseItemBinding.inflate(inflater, parent, false)
                return ImagePreviewViewHolder(binding)
            }

            override fun onBindViewHolder(holder: ImagePreviewViewHolder, position: Int) {
                val item = mData.getOrNull(position) ?: return
                val ivPhoto = holder.binding.ivPhoto
                val ivClear = holder.binding.ivClear
                Glide.with(holder.itemView)
                    .load(item)
                    .into(ivPhoto)
                Glide.with(holder.itemView)
                    .load(R.drawable.clear_ic)
                    .into(ivClear)
                ivPhoto.setFixOnClickListener { previewImageListener.invoke(it, holder.bindingAdapterPosition) }
                ivClear.setFixOnClickListener {
                    mData.removeAt(holder.bindingAdapterPosition)
                    notifyItemRemoved(holder.bindingAdapterPosition)
                    clearImageListener.invoke(it, holder.bindingAdapterPosition)
                }
            }

            override fun getItemCount(): Int = mData.size
        }

        private class ImagePreviewViewHolder(val binding: ImageChooseItemBinding) : RecyclerView.ViewHolder(binding.root)
    }
}