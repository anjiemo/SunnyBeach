package com.example.blogsystem.network

import com.example.blogsystem.network.api.MusicApi
import com.example.blogsystem.network.api.PhotoApi
import com.example.blogsystem.network.model.HomePhotoBean

object Repository {

    private val mMusicApi = ServiceCreator.create<MusicApi>()
    private val mPhotoApi = ServiceCreator.create<PhotoApi>()
    private var mPhotoBean: HomePhotoBean? = null
    private val mPhotoList = arrayListOf<HomePhotoBean.Res.Vertical>()
    private var mCurrentPage = 0

    fun getPhotoBean(): HomePhotoBean? = mPhotoBean

    fun setLocalPhotoList(photoList: List<HomePhotoBean.Res.Vertical>) {
        mPhotoList.run {
            clear()
            addAll(photoList)
        }
    }

    fun addLocalPhotoList(photoList: List<HomePhotoBean.Res.Vertical>) {
        mPhotoList.addAll(photoList)
    }

    fun loadCachePhotoList() = mPhotoList

    suspend fun loadPhotoList(limit: Int = 10): HomePhotoBean {
        val skip = mCurrentPage * 10
        val api = ServiceCreator.create<PhotoApi>()
        val photoBean = api.loadPhotoList(limit = limit, skip = skip)
        mPhotoBean = photoBean
        mCurrentPage++
        return photoBean
    }

    suspend fun loadHomeBannerList() = mPhotoApi.listsHomeBannerList()

    suspend fun getLoginQrKey() = mMusicApi.getLoginQrCodeKey()

    suspend fun createLoginQrCode(key: String) = mMusicApi.createLoginQrCode(key = key)

    suspend fun checkLoginQrCode(key: String) = mMusicApi.checkLoginQrCode(key = key)
}