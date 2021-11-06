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
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import com.blankj.utilcode.util.KeyboardUtils
import com.bumptech.glide.Glide
import java.io.File

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
    private var mImages = arrayListOf<String>()

    override fun getLayoutId(): Int = R.layout.put_fish_activity

    override fun initSoftKeyboard() {
        super.initSoftKeyboard()
        registerSoftKeyboardListener(softKeyboardListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegisterSoftKeyboardListener(softKeyboardListener)
    }

    override fun initView() {
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
                        tvChooseFishPondDesc.text = ""
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
            // TODO: 2021/9/11 选择图片，跳转至图片选择界面
            simpleToast("暂不支持选择图片")
            if (true) {
                return@setFixOnClickListener
            }
            // 选择图片，跳转到图片选择界面
            ImageSelectActivity.start(this, 4, this)
        }
        mBinding.ivLink.setFixOnClickListener {
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
            val maxInputTextLength = 512
            // 最小字符输入长度
            val minInputTextLength = 5
            val inputLength = mBinding.etInputContent.length()
            // 判断输入的字符长度是否溢出
            val isOverflow = (maxInputTextLength - inputLength) < 0
            // 如果输入的字符长度溢出了，则为 -number 样式，否则为 number / maxInputTextLength 的样式
            val inputLengthTips =
                if (inputLength < minInputTextLength || isOverflow) (maxInputTextLength - inputLength).toString()
                else "${inputLength}/$maxInputTextLength"
            mBinding.tvInputLength.text = inputLengthTips
            // 判断输入的字符串长度是否超过最大长度
            mBinding.tvInputLength.setTextColor(if (isOverflow) overflowColor else normalColor)
        }
    }

    private fun resetTopic() {
        mBinding.tvChooseFishPond.text = "选择鱼塘"
        val tvChooseFishPondDesc = mBinding.tvChooseFishPondDesc
        tvChooseFishPondDesc.text = "放到合适的鱼塘会被更多的摸鱼人看见哟~"
    }

    override fun onRightClick(view: View?) {
        // 校验内容是否合法，发布信息
        val inputLength = mBinding.etInputContent.length()
        val textLengthIsOk = inputLength in 5..512
        takeIf { textLengthIsOk.not() }?.let {
            simpleToast("请输入[5, 512)个字符~")
            return
        }
        // 提交
        val content = mBinding.etInputContent.textString
        // 2021/9/12 填充 “链接”（客户端暂不支持），
        val map = mapOf(
            "content" to content,
            "topicId" to mTopicId,
            "linkUrl" to mLinkUrl,
            "images" to mImages,
        )
        showDialog()
        // 如果选中的图片个数等于上传成功的图片个数，则图片全部上传成功
        mFishPondViewModel.putFish(map).observe(this@PutFishActivity) {
            hideDialog()
            it.getOrElse { throwable ->
                simpleToast("发布失败😭 $throwable")
                return@observe
            }
            // 重置界面状态
            mTopicId = null
            mLinkUrl = null
            mImages.clear()
            mBinding.etInputContent.clearText()
            resetTopic()
            simpleToast("发布非常成功😃")
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun onSelected(data: MutableList<String>?) {
        mImages.clear()
        data ?: return
        // 此处的 path 为客户端本地的路径，需要上传到服务器上，获取 url 路径
        lifecycleScope.launchWhenCreated {
            val successImages = arrayListOf<String?>()
            mImages.forEach {
                mFishPondViewModel.uploadFishImage(File(it))
                    .observe(this@PutFishActivity) { result ->
                        successImages.add(result.getOrNull())
                        mPreviewAdapter.setData(successImages)
                    }
            }
            simpleToast("图片上传完成")
        }
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard()
    }

    companion object {

        private class ImagePreviewAdapter(private val mData: MutableList<String?> = arrayListOf()) :
            RecyclerView.Adapter<ImagePreviewViewHolder>() {

            private var previewImageListener: (view: View, position: Int) -> Unit = { _, _ -> }
            private var clearImageListener: (view: View, position: Int) -> Unit = { _, _ -> }

            @SuppressLint("NotifyDataSetChanged")
            fun setData(data: MutableList<String?>) {
                mData.clear()
                data.let {
                    mData.addAll(it)
                }
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