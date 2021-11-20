package cn.cqautotest.sunnybeach.ui.activity

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.aop.DebugLog
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.GalleryActivityBinding
import cn.cqautotest.sunnybeach.http.response.model.WallpaperBean
import cn.cqautotest.sunnybeach.manager.ThreadPoolManager
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.other.PermissionCallback
import cn.cqautotest.sunnybeach.ui.adapter.PhotoAdapter
import cn.cqautotest.sunnybeach.util.*
import cn.cqautotest.sunnybeach.viewmodel.app.Repository
import cn.cqautotest.sunnybeach.viewmodel.discover.DiscoverViewModel
import com.blankj.utilcode.util.IntentUtils
import com.hjq.permissions.XXPermissions
import timber.log.Timber
import java.io.File
import java.io.InputStream
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/6/18
 * desc   : 发现图片列表查看界面
 */
class GalleryActivity : AppActivity() {

    private val mBinding: GalleryActivityBinding by viewBinding()
    private val mPhotoAdapter = PhotoAdapter(fillBox = true)
    private val mPhotoList = arrayListOf<WallpaperBean.Res.Vertical>()
    private val mDiscoverViewModel by viewModels<DiscoverViewModel>()
    private var mCurrentPageIndex = 0
    private var isShow = true

    override fun getLayoutId(): Int = R.layout.gallery_activity

    override fun initObserver() {
        val loadMoreModule = mPhotoAdapter.loadMoreModule
        mDiscoverViewModel.verticalPhotoList.observe(this) { verticalPhotoList ->
            Timber.d(verticalPhotoList.toJson())
            loadMoreModule.apply {
                mPhotoAdapter.addData(verticalPhotoList.toList())
                isEnableLoadMore = true
                loadMoreComplete()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
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
        mPhotoAdapter.setOnItemClickListener { _, _ ->
            toggleStatus()
        }
        mBinding.shareTv.setOnClickListener {
            val intent = IntentUtils.getShareImageIntent(getImageUri())
            startActivity(intent)
        }
        mBinding.downLoadPhotoTv.setOnClickListener {
            // 权限框架内部已经做了适配，直接申请 Manifest.permission.MANAGE_EXTERNAL_STORAGE 权限即可
            lifecycleScope.launchWhenCreated {
                val hasPermission =
                    requestXXPermissions(activity, Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                takeIf { hasPermission }?.let {
                    val dm = getSystemService<DownloadManager>() ?: return@let
                    val sourceUri = getImageUri()
                    val verticalPhotoBean = getCurrentVerticalPhotoBean()
                    val request = DownloadManager.Request(sourceUri)
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setTitle(verticalPhotoBean.id)
                        .setDescription(verticalPhotoBean.id)
                        .setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_PICTURES,
                            "阳光沙滩${File.pathSeparator}${verticalPhotoBean.preview}.png"
                        )
                    dm.enqueue(request)
                    simpleToast("已加入下载队列，请查看通知栏")
                }
            }
        }
        mBinding.settingWallpaperTv.setOnClickListener {
            val wallpaperManager = WallpaperManager.getInstance(this)
            lifecycleScope.launchWhenCreated {
                val inputStream =
                    DownloadHelper.ofType<InputStream>(this@GalleryActivity, getImageUri())
                ThreadPoolManager.getInstance().execute {
                    wallpaperManager.setStream(inputStream)
                }
            }
        }
    }

    /**
     * 请求权限
     */
    private suspend fun requestXXPermissions(context: Context, permission: String) =
        suspendCoroutine { cont: Continuation<Boolean> ->
            XXPermissions.with(context)
                .permission(permission)
                .request(object : PermissionCallback() {
                    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                        cont.resume(true)
                    }

                    override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                        super.onDenied(permissions, never)
                        cont.resume(false)
                    }
                })
        }

    private fun getImageUri(): Uri {
        val verticalPhotoBean = getCurrentVerticalPhotoBean()
        Timber.d(verticalPhotoBean.toJson())
        val previewUrl = verticalPhotoBean.preview
        return Uri.parse(previewUrl)
    }

    private fun getCurrentVerticalPhotoBean() = mPhotoList[mBinding.galleryViewPager2.currentItem]

    override fun initData() {
        val intent = intent
        val photoId = intent.getStringExtra(IntentKey.ID)
        Timber.d("photoId is $photoId")
        val cacheVerticalPhotoList = Repository.getPhotoList()
        mPhotoList.apply {
            Timber.d("cacheVerticalPhotoList is $cacheVerticalPhotoList")
            addAll(cacheVerticalPhotoList)
        }
        mCurrentPageIndex = mPhotoList.indexOfFirst { photoId == it.id }
        mPhotoAdapter.setList(mPhotoList)
        mBinding.galleryViewPager2.setCurrentItem(mCurrentPageIndex, false)
    }

    override fun initView() {
        mBinding.galleryViewPager2.apply {
            orientation = ViewPager2.ORIENTATION_VERTICAL
            adapter = mPhotoAdapter
        }
        mBinding.settingWallpaperTv.apply {
            setRoundRectBg(color = Color.parseColor("#66393939"), cornerRadius = 8.dp)
        }
    }

    private fun toggleStatus() {
        isShow = isShow.not()
        switchUIStatus()
    }

    private fun switchUIStatus() {
        mBinding.toolMenuGroup.isVisible = isShow
    }

    companion object {

        @JvmStatic
        @DebugLog
        fun start(context: Context, id: String) {
            val intent = Intent(context, GalleryActivity::class.java)
            intent.putExtra(IntentKey.ID, id)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}