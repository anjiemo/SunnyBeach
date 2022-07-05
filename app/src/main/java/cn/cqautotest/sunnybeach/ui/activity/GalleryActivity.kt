package cn.cqautotest.sunnybeach.ui.activity

import android.app.Activity
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.aop.Log
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.GalleryActivityBinding
import cn.cqautotest.sunnybeach.http.network.Repository
import cn.cqautotest.sunnybeach.ktx.simpleToast
import cn.cqautotest.sunnybeach.ktx.toJson
import cn.cqautotest.sunnybeach.manager.ThreadPoolManager
import cn.cqautotest.sunnybeach.model.wallpaper.WallpaperBean
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.adapter.PhotoAdapter
import cn.cqautotest.sunnybeach.util.DownloadHelper
import cn.cqautotest.sunnybeach.viewmodel.discover.DiscoverViewModel
import com.blankj.utilcode.util.IntentUtils
import com.blankj.utilcode.util.ScreenUtils
import com.dylanc.longan.activity
import com.dylanc.longan.context
import com.gyf.immersionbar.ImmersionBar
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.pow

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/06/18
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

    override fun initView() {
        // 隐藏状态栏，全屏浏览壁纸
        ImmersionBar.hideStatusBar(window)
        mBinding.galleryViewPager2.apply {
            orientation = ViewPager2.ORIENTATION_VERTICAL
            adapter = mPhotoAdapter
        }
    }

    override fun initData() {
        val intent = intent
        val photoId = intent.getStringExtra(IntentKey.ID)
        Timber.d("initData：===> photoId is $photoId")
        val cacheVerticalPhotoList = Repository.getPhotoList()
        mPhotoList.apply {
            Timber.d("initData：===> cacheVerticalPhotoList is $cacheVerticalPhotoList")
            addAll(cacheVerticalPhotoList)
        }
        mCurrentPageIndex = mPhotoList.indexOfFirst { photoId == it.id }
        mPhotoAdapter.setList(mPhotoList)
        mBinding.galleryViewPager2.setCurrentItem(mCurrentPageIndex, false)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun initEvent() {
        // mBinding.galleryViewPager2.doPageSelected { position ->
        //     mPhotoAdapter.getItemOrNull(position)?.let { wallpaperBean ->
        //         val thumb = wallpaperBean.thumb
        //         lifecycleScope.launchWhenCreated {
        //             DownloadHelper.ofType<Bitmap>(context, thumb.toUri())?.let { fixStatusBar(it) }
        //         }
        //     }
        // }
        mPhotoAdapter.loadMoreModule.run {
            setOnLoadMoreListener {
                isEnableLoadMore = false
                mDiscoverViewModel.loadMorePhotoList()
            }
        }
        mPhotoAdapter.setOnItemClickListener { _, _ -> toggleStatus() }
        with(mBinding) {
            shareTv.setOnClickListener {
                lifecycleScope.launchWhenCreated {
                    simpleToast("正在下载图片，请稍后...")
                    // 下载图片文件
                    val imageFile = DownloadHelper.ofType<File>(context, getImageUri())
                    simpleToast("图片下载完成，即将开始分享")
                    // 分享图片
                    val intent = IntentUtils.getShareImageIntent(imageFile)
                    startActivity(intent)
                }
            }
            downLoadPhotoTv.setOnClickListener {
                // 打开指定的一张照片
                val verticalPhoto = getCurrentVerticalPhotoBean()
                Intent(Intent.ACTION_VIEW).apply {
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    setDataAndType(Uri.parse(verticalPhoto.img), "image/*")
                    startActivity(this)
                }
            }
            val wallpaperManager = WallpaperManager.getInstance(context)
            settingWallpaperTv.setOnClickListener {
                toast("开始准备壁纸...")
                showDialog()
                lifecycleScope.launchWhenCreated {
                    val imageFile = DownloadHelper.ofType<File>(activity, getImageUri())
                    val success = imageFile.takeUnless { it == null }?.let { wallpaperManager.setWallpaper(it.inputStream()) } ?: false
                    hideDialog()
                    toast(if (success) "壁纸设置成功" else "壁纸设置失败")
                }
            }
        }
    }

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

    private fun fixStatusBar(bitmap: Bitmap) {
        val left = 0
        val top = 0
        val right = ScreenUtils.getAppScreenWidth()
        val bottom = ImmersionBar.getStatusBarHeight(this)
        Palette.from(bitmap)
            .maximumColorCount(3)
            .setRegion(left, top, right, bottom)
            .generate {
                it?.let { palette ->
                    var mostPopularSwatch: Palette.Swatch? = null
                    for (swatch in palette.swatches) {
                        if (mostPopularSwatch == null || swatch.population > mostPopularSwatch.population) {
                            mostPopularSwatch = swatch
                        }
                    }
                    mostPopularSwatch?.let { swatch ->
                        val luminance = ColorUtils.calculateLuminance(swatch.rgb)
                        // 当luminance小于0.5时，我们认为这是一个深色值.
                        val isDarkFont = luminance < 0.5
                        ImmersionBar.with(this)
                            .statusBarDarkFont(isDarkFont)
                    }
                }
            }
    }

    private suspend fun WallpaperManager.setWallpaper(inputStream: InputStream) = suspendCoroutine {
        ThreadPoolManager.getInstance().execute {
            try {
                setStream(inputStream)
                it.resume(true)
            } catch (e: IOException) {
                e.printStackTrace()
                it.resume(false)
            }
        }
    }

    private fun getImageUrl(): String {
        val verticalPhotoBean = getCurrentVerticalPhotoBean()
        Timber.d(verticalPhotoBean.toJson())
        return verticalPhotoBean.preview
    }

    private fun getImageUri(): Uri {
        val imageUrl = getImageUrl()
        Timber.d("getImageUri：===> imageUrl is $imageUrl")
        return Uri.parse(imageUrl)
    }

    private fun getCurrentVerticalPhotoBean() = mPhotoList[mBinding.galleryViewPager2.currentItem]

    private fun toggleStatus() {
        isShow = isShow.not()
        switchUIStatus()
    }

    private fun switchUIStatus() {
        mBinding.toolMenuGroup.isVisible = isShow
    }

    companion object {

        private const val SHARE_ELEMENT_NAME = "photo"

        @JvmStatic
        @Log
        fun start(context: Context, id: String) {
            val intent = Intent(context, GalleryActivity::class.java)
            intent.putExtra(IntentKey.ID, id)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

        @JvmStatic
        @Log
        fun smoothEntry(activity: Activity, id: String, shareElement: View) {
            val aos = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                shareElement,
                SHARE_ELEMENT_NAME
            )
            val intent = Intent(activity, GalleryActivity::class.java)
            intent.putExtra(IntentKey.ID, id)
            // 请求码必须在 2 的 16 次方以内
            val requestCode = Random().nextInt(2.0.pow(16.0).toInt())
            ActivityCompat.startActivityForResult(activity, intent, requestCode, aos.toBundle())
        }
    }
}