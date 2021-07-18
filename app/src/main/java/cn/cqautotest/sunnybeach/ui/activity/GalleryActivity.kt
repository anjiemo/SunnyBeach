package cn.cqautotest.sunnybeach.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import androidx.activity.viewModels
import androidx.core.content.getSystemService
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.GalleryActivityBinding
import cn.cqautotest.sunnybeach.http.response.model.HomePhotoBean
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.adapter.PhotoAdapter
import cn.cqautotest.sunnybeach.util.*
import cn.cqautotest.sunnybeach.viewmodel.app.Repository
import cn.cqautotest.sunnybeach.viewmodel.discover.DiscoverViewModel
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.hjq.permissions.OnPermissionCallback
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

    private lateinit var mBinding: GalleryActivityBinding
    private val mPhotoAdapter = PhotoAdapter(fillBox = true)
    private val mPhotoList = arrayListOf<HomePhotoBean.Res.Vertical>()
    private val mDiscoverViewModel by viewModels<DiscoverViewModel>()
    private var mCurrentPage = 0

    override fun getLayoutId(): Int = 0

    override fun onBindingView(): ViewBinding {
        mBinding = GalleryActivityBinding.inflate(layoutInflater)
        return mBinding
    }

    override fun initObserver() {
        val loadMoreModule = mPhotoAdapter.loadMoreModule
        mDiscoverViewModel.verticalPhotoList.observe(this) { verticalPhotoList ->
            logByDebug(msg = "initEvent：===> " + verticalPhotoList.toJson())
            loadMoreModule.apply {
                mPhotoAdapter.addData(verticalPhotoList)
                isEnableLoadMore = true
                loadMoreComplete()
            }
        }
    }

    @SuppressLint("InlinedApi")
    override fun initEvent() {
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
                mDiscoverViewModel.loadMorePhotoList()
            }
        }
        mBinding.downLoadPhotoTv.setOnClickListener {
            // 权限框架内部已经做了适配，直接申请 Manifest.permission.MANAGE_EXTERNAL_STORAGE 权限即可
            XXPermissions.with(this)
                .permission(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
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
        val photoId = intent.getStringExtra(IntentKey.ID)
        logByDebug(msg = "initData：===>photoId is $photoId")
        mPhotoList.apply {
            clear()
            val cacheVerticalPhotoList = Repository.loadCachePhotoList()
            logByDebug(msg = "initData：===> cacheVerticalPhotoList is $cacheVerticalPhotoList")
            addAll(cacheVerticalPhotoList)
        }
        mPhotoList.forEachIndexed { index, vertical ->
            if (photoId == vertical.id) {
                mCurrentPage = index
            }
        }
        mPhotoAdapter.setList(mPhotoList)
        mBinding.galleryViewPager2.setCurrentItem(mCurrentPage, false)
    }

    override fun initView() {
        mBinding.galleryViewPager2.let {
            it.orientation = ViewPager2.ORIENTATION_VERTICAL
            it.adapter = mPhotoAdapter
        }
        mBinding.settingWallpaperTv.setRoundRectBg(
            color = Color.parseColor("#66393939"),
            cornerRadius = 10.dp
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        mPhotoList.clear()
        mPhotoAdapter.data.clear()
    }

    companion object {

        @JvmStatic
        fun start(context: Context, id: String) {
            val intent = Intent(context, GalleryActivity::class.java)
            intent.putExtra(IntentKey.ID, id)
            context.startActivity(intent)
        }
    }
}