package cn.cqautotest.sunnybeach.viewmodel.app

import androidx.lifecycle.liveData
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.http.response.model.WallpaperBean
import cn.cqautotest.sunnybeach.util.TAG
import cn.cqautotest.sunnybeach.util.logByDebug
import com.blankj.utilcode.util.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/09/07
 *    desc   : 存储库
 */
object Repository {

    private val cachePhotoIdList = arrayListOf<WallpaperBean.Res.Vertical>()

    fun checkAppUpdate() = liveData(Dispatchers.IO) {
        val result = try {
            coroutineScope {
                val result = AppNetwork.checkAppUpdate()
                logByDebug(tag = TAG, msg = "getFishCommendListById：===> result is $result")
                if (result.success) Result.success(result.data)
                else Result.failure(ServiceException(result.message))
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
        emit(result)
    }

    fun setPhotoIdList(photoIdList: List<WallpaperBean.Res.Vertical>) {
        if (photoIdList !== cachePhotoIdList) {
            cachePhotoIdList.clear()
            if (photoIdList.isNullOrEmpty().not()) {
                cachePhotoIdList.addAll(photoIdList)
            }
        } else {
            if (photoIdList.isNullOrEmpty().not()) {
                val newList = photoIdList.toList()
                cachePhotoIdList.clear()
                cachePhotoIdList.addAll(newList)
            } else {
                cachePhotoIdList.clear()
            }
        }
    }

    fun getPhotoList() = cachePhotoIdList.toList()

    fun getFishCommendListById(momentId: String, page: Int) = liveData(Dispatchers.IO) {
        val result = try {
            coroutineScope {
                val result = FishNetwork.getFishCommendListById(momentId, page)
                logByDebug(tag = TAG, msg = "getFishCommendListById：===> result is $result")
                if (result.success) Result.success(result.data)
                else Result.failure(ServiceException(result.message))
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
        emit(result)
    }

    suspend fun uploadFishImage(image: File) = liveData(Dispatchers.IO) {
        val result = try {
            coroutineScope {
                val fileName = FileUtils.getFileMD5ToString(image)
                val requestBody = image.asRequestBody()
                val body = MultipartBody.Part.createFormData("image", fileName, requestBody)
                val result = FishNetwork.uploadFishImage(body)
                if (result.success) Result.success(result.data)
                else Result.failure(ServiceException(result.message))
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
        emit(result)
    }

    fun putFish(moment: Map<String, Any?>) = liveData(Dispatchers.IO) {
        val result = try {
            coroutineScope {
                val result = FishNetwork.putFish(moment)
                logByDebug(tag = TAG, msg = "putFish：===> result is $result")
                if (result.success) Result.success(result.data)
                else Result.failure(ServiceException(result.message))
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
        emit(result)
    }

    fun loadFishDetailById(momentId: String) = liveData(Dispatchers.IO) {
        val result = try {
            coroutineScope {
                val result = FishNetwork.loadFishDetailById(momentId)
                logByDebug(tag = TAG, msg = "loadFishDetailById：===> result is $result")
                if (result.success) Result.success(result.data)
                else Result.failure(ServiceException())
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
        emit(result)
    }

    fun loadWallpaperBannerList() = liveData(Dispatchers.IO) {
        val result = try {
            coroutineScope {
                val result = PhotoNetwork.loadWallpaperBannerList()
                logByDebug(tag = TAG, msg = "loadWallpaperBannerList：===> result is $result")
                if ("0" == result.errno) Result.success(result.data)
                else Result.failure(ServiceException())
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
        emit(result)
    }

    fun postComment(momentComment: Map<String, Any?>) = liveData(Dispatchers.IO) {
        val result = try {
            coroutineScope {
                val result = FishNetwork.postComment(momentComment)
                logByDebug(tag = TAG, msg = "submitComment：===> result is $result")
                if (result.success) Result.success(result.data)
                else Result.failure(ServiceException(result.message))
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
        emit(result)
    }
}