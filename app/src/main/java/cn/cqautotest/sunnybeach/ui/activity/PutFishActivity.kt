package cn.cqautotest.sunnybeach.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.ImageChooseItemBinding
import cn.cqautotest.sunnybeach.databinding.PutFishActivityBinding
import cn.cqautotest.sunnybeach.model.FishPondTopicList
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.dialog.InputDialog
import cn.cqautotest.sunnybeach.util.*
import cn.cqautotest.sunnybeach.viewmodel.app.Repository
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import com.blankj.utilcode.constant.MemoryConstants
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.PathUtils
import com.bumptech.glide.Glide
import com.hjq.bar.TitleBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import kotlin.coroutines.resume
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
    private val softKeyboardListener = getSoftKeyboardListener()
    private var mTopicId: String? = null
    private var mLinkUrl: String? = null

    override fun getLayoutId(): Int = R.layout.put_fish_activity

    override fun initSoftKeyboard() {
        super.initSoftKeyboard()
        registerSoftKeyboardListener(softKeyboardListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegisterSoftKeyboardListener(softKeyboardListener)
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        mBinding.etInputContent.setDefaultEmojiParser()
        mBinding.tvInputLength.text = "0/$INPUT_MAX_LENGTH"
        val rvPreviewImage = mBinding.rvPreviewImage
        rvPreviewImage.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = mPreviewAdapter
        }
    }

    override fun initData() {

    }

    @SuppressLint("SetTextI18n")
    override fun initEvent() {
        postDelayed({
            showKeyboard(mBinding.etInputContent)
        }, 200)
        mBinding.rlChooseFishPond.setFixOnClickListener {
            // 选择鱼塘
            startActivityForResult(FishPondSelectionActivity::class.java) { resultCode, data ->
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        val fishPondTopicListItem =
                            fromJson<FishPondTopicList.TopicItem>(data.getStringExtra(IntentKey.OTHER))
                        mTopicId = fishPondTopicListItem?.id
                        val tvChooseFishPondDesc = mBinding.tvChooseFishPondDesc
                        mBinding.tvChooseFishPond.text = "#${fishPondTopicListItem.topicName}#"
                        tvChooseFishPondDesc.clearText()
                    } else {
                        resetTopic()
                    }
                }
            }
        }
        mBinding.ivEmoji.setFixOnClickListener {
            // 选择表情，弹出表情选择列表
            val keyboardIsShowing = KeyboardUtils.isSoftInputVisible(this)
            if (keyboardIsShowing) {
                postDelayed({
                    mBinding.rvEmojiList.isVisible = true
                    mBinding.rvEmojiList.layoutParams =
                        LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 310.dp)
                }, 200)
                hideKeyboard()
            } else {
                mBinding.rvEmojiList.isVisible = false
                showKeyboard(mBinding.etInputContent)
            }
            val emojiIcon = if (keyboardIsShowing) {
                R.mipmap.ic_keyboard
            } else {
                R.mipmap.ic_emoji_normal
            }
            Glide.with(this)
                .load(emojiIcon)
                .into(mBinding.ivEmoji)
        }
        mBinding.rvEmojiList.setOnEmojiClickListener { emoji, _ ->
            val etInputContent = mBinding.etInputContent
            val cursor = etInputContent.selectionStart
            etInputContent.text.insert(cursor, emoji)
        }
        mBinding.ivImage.setFixOnClickListener {
            // 选择图片，跳转至图片选择界面
            ImageSelectActivity.start(this, 9, this)
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
        val clMenuContainer = mBinding.clMenuContainer
        mBinding.keyboardLayout.setKeyboardListener { isActive, keyboardHeight ->
            val height = if (isActive) {
                mBinding.rvEmojiList.isVisible = false
                keyboardHeight
            } else {
                -(clMenuContainer.height + 10.dp)
            }
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val realHeight = height + clMenuContainer.height + 10.dp
            layoutParams.bottomMargin = realHeight
            clMenuContainer.layoutParams = layoutParams
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

    private fun resetTopic() {
        mBinding.tvChooseFishPond.text = "选择鱼塘"
        val tvChooseFishPondDesc = mBinding.tvChooseFishPondDesc
        tvChooseFishPondDesc.text = "放到合适的鱼塘会被更多的摸鱼人看见哟~"
    }

    override fun onRightClick(titleBar: TitleBar) {
        val view = titleBar?.rightView
        view?.isEnabled = false
        // 校验内容是否合法，发布信息
        val inputLength = mBinding.etInputContent.length()
        val textLengthIsOk = inputLength in 5..INPUT_MAX_LENGTH
        takeIf { textLengthIsOk.not() }?.let {
            simpleToast("请输入[5, $INPUT_MAX_LENGTH]个字符~")
            view?.isEnabled = true
            return
        }

        // 摸鱼内容
        val content = mBinding.etInputContent.textString
        val images = arrayListOf<String>()
        images.addAll(mPreviewAdapter.getData())
        showDialog()
        // 上传图片，此处的 path 为客户端本地的路径，需要上传到服务器上，获取网络 url 路径
        lifecycleScope.launchWhenCreated {
            val successImages = arrayListOf<String>()
            var hasOutOfSizeImg = false
            withContext(Dispatchers.IO) {
                run {
                    // 预处理，先判断压缩后的图片是否有超过阈值
                    images.forEachIndexed { index, path ->
                        // 压缩图片文件
                        val zipImgFile = zipImageFile(File(path))
                        // 压缩后的图片文件路径
                        val zippedPath = zipImgFile?.path ?: path
                        images[index] = zippedPath
                        Timber.d("onRightClick：===> path is $path zippedPath is $zippedPath")
                        val fileSize = FileUtils.getFileLength(zippedPath)
                        if (fileSize >= IMAGE_FILE_MAX_SIZE) {
                            val currImgFileSize = FileUtils.getSize(zippedPath)
                            Timber.d("onRightClick：===> imageFile：$path file size max is $IMAGE_FILE_MAX_SIZE, but curr zipped size is $currImgFileSize")
                            hasOutOfSizeImg = true
                            return@run
                        }
                    }
                    images.forEach {
                        val imageUrl = withContext(Dispatchers.Default) {
                            val imageFile = File(it)
                            Repository.uploadFishImage(imageFile)
                            // 直接 return 只有 continue 的效果，此处需要使用 lambda 进行 return （相当于 break）
                        } ?: return@run
                        successImages.add(imageUrl)
                        Timber.d("===> imageUrl is $imageUrl")
                    }
                }
            }
            Timber.d("===> successImages is $successImages")
            if (successImages.size != images.size) {
                val tips = if (hasOutOfSizeImg) "当前仅支持上传小于${ConvertUtils.byte2FitMemorySize(IMAGE_FILE_MAX_SIZE.toLong(), 0)}的图片"
                else "图片上传失败，请稍后重试"
                simpleToast(tips)
                hideDialog()
                view?.isEnabled = true
                return@launchWhenCreated
            }
            // 2021/9/12 填充 “链接”（客户端暂不支持），
            val map = mapOf(
                "content" to content,
                "topicId" to mTopicId,
                "linkUrl" to mLinkUrl,
                "images" to successImages,
            )

            // If you want to debug, uncomment the next line of code.
            // if (true) return@launchWhenCreated

            // 图片上传完成，可以发布摸鱼
            mFishPondViewModel.putFish(map).observe(this@PutFishActivity) {
                hideDialog()
                view?.isEnabled = true
                it.getOrElse { throwable ->
                    simpleToast("发布失败😭 $throwable")
                    return@observe
                }
                // 重置界面状态
                mTopicId = null
                mLinkUrl = null
                mPreviewAdapter.setData(arrayListOf())
                mBinding.etInputContent.clearText()
                resetTopic()
                simpleToast("发布非常成功😃")
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private suspend fun zipImageFile(imgFile: File) = suspendCoroutine<File?> { con ->
        Luban.with(this)
            .load(imgFile)
            .ignoreBy(TIMES)
            .setTargetDir(PathUtils.getExternalAppCachePath())
            .filter { it.isNotBlank() }
            .setCompressListener(object : OnCompressListener {
                override fun onStart() {
                    // 压缩开始前调用
                    // Ignore this callback, because we don't want to do anything.
                }

                override fun onSuccess(file: File?) {
                    // 压缩成功后调用，返回压缩后的图片文件
                    // We need to rename the image file name to end with png to overcome the server limit.
                    // Define the extension function inside the function for us to call.
                    fun String.fixSuffix() = replace("jpeg", "png").replace("jpg", "png")
                    val destFile = File(file?.parent, imgFile.name.fixSuffix())
                    // 删除以存在的文件以确保能够正常重命名
                    FileUtils.delete(destFile)
                    // 重命名文件
                    val renameSuccess = FileUtils.rename(file, destFile.name)
                    con.resume(if (renameSuccess) destFile else file)
                }

                override fun onError(e: Throwable?) {
                    con.resume(null)
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
            fun setData(data: MutableList<String>) {
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
                val item = mData[position]
                val ivPhoto = holder.binding.ivPhoto
                val ivClear = holder.binding.ivClear
                Glide.with(holder.itemView)
                    .load(item)
                    .into(ivPhoto)
                Glide.with(holder.itemView)
                    .load(R.drawable.clear_ic)
                    .into(ivClear)
                ivPhoto.setFixOnClickListener {
                    previewImageListener.invoke(it, position)
                }
                ivClear.setFixOnClickListener {
                    mData.removeAt(position)
                    notifyItemRemoved(position)
                    clearImageListener.invoke(it, position)
                }
            }

            override fun getItemCount(): Int = mData.size
        }

        private class ImagePreviewViewHolder(val binding: ImageChooseItemBinding) :
            RecyclerView.ViewHolder(binding.root)
    }
}