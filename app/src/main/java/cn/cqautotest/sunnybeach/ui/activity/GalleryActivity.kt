package cn.cqautotest.sunnybeach.ui.activity

import android.Manifest
import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.getSystemService
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import cn.cqautotest.sunnybeach.BuildConfig
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.GalleryActivityBinding
import cn.cqautotest.sunnybeach.http.response.model.HomePhotoBean
import cn.cqautotest.sunnybeach.ui.adapter.PhotoAdapter
import cn.cqautotest.sunnybeach.utils.*
import cn.cqautotest.sunnybeach.viewmodel.SingletonManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/6/18
 * desc   : 发现图片列表查看界面
 */
class GalleryActivity : AppActivity() {

    private val discoverViewModel by lazy { SingletonManager.discoverViewModel }
    private lateinit var mBinding: GalleryActivityBinding
    private val mPhotoAdapter by lazy { PhotoAdapter(fillBox = true) }
    private val mPhotoList by lazy { arrayListOf<HomePhotoBean.Res.Vertical>() }

    override fun getLayoutId(): Int = R.layout.gallery_activity

    override fun onBindingView() {
        mBinding = GalleryActivityBinding.bind(contentView)
    }

    override fun initObserver() {
        val loadMoreModule = mPhotoAdapter.loadMoreModule
        discoverViewModel.verticalPhotoList.observe(this) { verticalPhotoList ->
            logByDebug(msg = "initEvent：===> " + verticalPhotoList.toJson())
            loadMoreModule.apply {
                mPhotoAdapter.addData(verticalPhotoList)
                isEnableLoadMore = true
                loadMoreComplete()
            }
        }
    }

    override fun initEvent() {
        mPhotoAdapter.setOnItemClickListener { _, _ ->
            //startActivity<ScrollingActivity>(this)
        }
        mPhotoAdapter.setOnItemLongClickListener { verticalPhoto, _ ->
            //打开指定的一张照片
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(Uri.parse(verticalPhoto.img), "image/*")
            startActivity(intent)
        }
        mPhotoAdapter.loadMoreModule.run {
            setOnLoadMoreListener {
                isEnableLoadMore = false
                discoverViewModel.loadMorePhotoList()
            }
        }
        mBinding.downLoadPhotoTv.setOnClickListener {
            XXPermissions.with(this)
                .permission(
                    // 适配权限
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                        arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                    else arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                        val dm = getSystemService<DownloadManager>() ?: return
                        val verticalPhotoBean = getCurrentVerticalPhotoBean()
                        logByDebug(msg = "hasPermission: ===>${Gson().toJson(verticalPhotoBean)}")
                        val previewUrl = verticalPhotoBean.preview
                        val sourceUri = Uri.parse(previewUrl)
                        val request = DownloadManager.Request(sourceUri)
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                            .setTitle(verticalPhotoBean.id)
                            .setDescription(verticalPhotoBean.id)
                            .setDestinationInExternalPublicDir(
                                Environment.DIRECTORY_DOWNLOADS,
                                "$previewUrl.png"
                            )
                        dm.enqueue(request)
                    }

                    override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                        simpleToast("请授予本APP读写外部存储权限")
                    }
                })
        }
        mBinding.settingWallpaperTv.setOnClickListener {
            // TODO: 2021/6/18 后期考虑迁移至ViewModel中进行处理
            val wallpaperManager = WallpaperManager.getInstance(this)
            val verticalPhotoBean = getCurrentVerticalPhotoBean()
            lifecycleScope.launch {
                runCatching {
                    val photoFile = withContext(Dispatchers.IO) {
                        Glide.with(this@GalleryActivity)
                            .asFile()
                            .load(verticalPhotoBean.preview)
                            .submit()
                            .get()
                    }
                    wallpaperManager.setStream(photoFile.inputStream())
                }.onSuccess {
                    simpleToast("壁纸设置成功")
                }.onFailure {
                    simpleToast("壁纸设置失败")
                }
            }
        }
    }

    private fun getCurrentVerticalPhotoBean() = mPhotoList[mBinding.galleryViewPager2.currentItem]

    override fun initData() {
        val intent = intent
        val id = intent.getStringExtra("id")
        mPhotoList.apply {
            val cacheVerticalPhotoList =
                discoverViewModel.cacheVerticalPhotoList.value ?: arrayListOf()
            addAll(cacheVerticalPhotoList)
        }
        mPhotoList.forEachIndexed { index, vertical ->
            if (id.equals(vertical.id)) {
                mCurrentPage = index
            }
        }
        mPhotoAdapter.setList(mPhotoList)
        mBinding.galleryViewPager2.setCurrentItem(mCurrentPage, false)
    }

    override fun initView() {
        fullWindow()
        mBinding.galleryViewPager2.let {
            it.orientation = ViewPager2.ORIENTATION_VERTICAL
            it.adapter = mPhotoAdapter
        }
        mBinding.settingWallpaperTv.setRoundRectBg(
            color = Color.parseColor("#66393939"),
            cornerRadius = 10.dp
        )
    }

    companion object {

        private var mCurrentPage = 0

        fun startActivity(context: Context, id: String) {
            val intent = Intent(context, GalleryActivity::class.java)
            intent.putExtra("id", id)
            context.startActivity(intent)
        }
    }
}