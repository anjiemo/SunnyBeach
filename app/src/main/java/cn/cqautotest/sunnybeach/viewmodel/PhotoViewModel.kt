package cn.cqautotest.sunnybeach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.cqautotest.sunnybeach.model.wallpaper.WallpaperBean
import cn.cqautotest.sunnybeach.paging.source.WallpaperPagingSource
import cn.cqautotest.sunnybeach.viewmodel.app.Repository
import kotlinx.coroutines.flow.Flow

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/06/18
 * desc   : 图片的 ViewModel
 */
class PhotoViewModel : ViewModel() {

    fun getWallpaperList(): Flow<PagingData<WallpaperBean.Res.Vertical>> {
        return Pager(config = PagingConfig(30),
            pagingSourceFactory = {
                WallpaperPagingSource()
            }).flow.cachedIn(viewModelScope)
    }

    fun getWallpaperBannerList() = Repository.loadWallpaperBannerList()
}