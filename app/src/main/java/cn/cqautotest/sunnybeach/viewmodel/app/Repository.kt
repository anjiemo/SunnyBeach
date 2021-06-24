package cn.cqautotest.sunnybeach.viewmodel.app

import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.request.api.PhotoApi
import cn.cqautotest.sunnybeach.http.response.model.HomePhotoBean

object Repository {

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
}