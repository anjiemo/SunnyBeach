package com.example.blogsystem.ui.activity

import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.core.content.getSystemService
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.blogsystem.adapter.PhotoAdapter
import com.example.blogsystem.base.BaseActivity
import com.example.blogsystem.databinding.ActivityGalleryBinding
import com.example.blogsystem.network.Repository
import com.example.blogsystem.network.model.HomePhotoBean
import com.example.blogsystem.utils.fullWindow
import com.example.blogsystem.utils.logByDebug
import com.example.blogsystem.utils.simpleToast
import com.google.gson.Gson
import com.hjq.permissions.OnPermission
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryActivity : BaseActivity() {

    private lateinit var mBinding: ActivityGalleryBinding
    private val mPhotoAdapter by lazy { PhotoAdapter(fillBox = true) }
    private val mPhotoList = arrayListOf<HomePhotoBean.Res.Vertical>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        callAllInit()
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
                lifecycleScope.launch {
                    val photoBean = Repository.loadPhotoList()
                    val verticalPhotoList = photoBean.res.vertical
                    logByDebug(msg = "initData: ===>${mPhotoList.size}")
                    mPhotoList.addAll(verticalPhotoList)
                    Repository.addLocalPhotoList(verticalPhotoList)
                    mPhotoAdapter.addData(verticalPhotoList)
                    isEnableLoadMore = true
                    loadMoreComplete()
                }
            }
        }
        mBinding.downLoadPhotoTv.setOnClickListener {
            XXPermissions.with(this)
                .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                .request(object : OnPermission {
                    override fun hasPermission(granted: MutableList<String>?, all: Boolean) {
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

                    override fun noPermission(denied: MutableList<String>?, quick: Boolean) {
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
        mPhotoList.run {
            clear()
            addAll(Repository.loadCachePhotoList())
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