package cn.cqautotest.sunnybeach.viewmodel.app

import androidx.lifecycle.liveData
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.http.response.model.WallpaperBean
import com.blankj.utilcode.util.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/09/07
 *    desc   : 存储库
 */
object Repository {

    private val cachePhotoIdList = arrayListOf<WallpaperBean.Res.Vertical>()

    fun getAllowance() = liveData(Dispatchers.IO) {
        val result = try {
            coroutineScope {
                val result = UserNetwork.getAllowance()
                Timber.d("result is $result")
                when (result.getCode()) {
                    // 未领取VIP津贴
                    11129 -> Result.success(false)
                    // 已经领取了VIP津贴
                    11128 -> Result.success(true)
                    else -> Result.failure(ServiceException(result.getMessage()))
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
        emit(result)
    }

    fun checkAllowance() = liveData(Dispatchers.IO) {
        val result = try {
            coroutineScope {
                val result = UserNetwork.checkAllowance()
                Timber.d("result is $result")
                when (result.getCode()) {
                    // 未领取VIP津贴
                    11129 -> Result.success(false)
                    // 已经领取了VIP津贴
                    11128 -> Result.success(true)
                    else -> Result.failure(ServiceException(result.getMessage()))
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
        emit(result)
    }

    fun unfollowUser(userId: String) = liveData(Dispatchers.IO) {
        val result = try {
            coroutineScope {
                val result = UserNetwork.unfollowUser(userId)
                Timber.d("result is $result")
                if (result.isSuccess()) Result.success(result.getData())
                else Result.failure(ServiceException(result.getMessage()))
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
        emit(result)
    }

    fun followUser(userId: String) = liveData(Dispatchers.IO) {
        val result = try {
            coroutineScope {
                val result = UserNetwork.followUser(userId)
                Timber.d("result is $result")
                if (result.isSuccess()) Result.success(result.getData())
                else Result.failure(ServiceException(result.getMessage()))
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
        emit(result)
    }

    fun followState(userId: String) = liveData(Dispatchers.IO) {
        val result = try {
            coroutineScope {
                val result = UserNetwork.followState(userId)
                Timber.d("result is $result")
                if (result.isSuccess()) Result.success(result.getData())
                else Result.failure(ServiceException(result.getMessage()))
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
        emit(result)
    }

    fun getUserInfo(userId: String) = liveData(Dispatchers.IO) {
        val result = try {
            coroutineScope {
                val result = UserNetwork.getUserInfo(userId)
                Timber.d("result is $result")
                if (result.isSuccess()) Result.success(result.getData())
                else Result.failure(ServiceException(result.getMessage()))
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
        emit(result)
    }

    fun getAchievement(userId: String) = liveData(Dispatchers.IO) {
        val result = try {
            coroutineScope {
                val result = UserNetwork.getAchievement(userId)
                Timber.d("result is $result")
                if (result.isSuccess()) Result.success(result.getData())
                else Result.failure(ServiceException(result.getMessage()))
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
        emit(result)
    }

    suspend fun checkToken() = try {
        val result = UserNetwork.checkToken()
        if (result.isSuccess()) result.getData()
        else throw ServiceException()
    } catch (t: Throwable) {
        t.printStackTrace()
        null
    }

    fun queryUserAvatar(account: String) = liveData(Dispatchers.IO) {
        val result = try {
            coroutineScope {
                val result = UserNetwork.queryUserAvatar(account)
                Timber.d("result is $result")
                if (result.isSuccess()) result.getData()
                else ""
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            ""
        }
        emit(result)
    }

    fun checkAppUpdate() = liveData(Dispatchers.IO) {
        val result = try {
            coroutineScope {
                val result = AppNetwork.checkAppUpdate()
                Timber.d("result is $result")
                if (result.isSuccess()) Result.success(result.getData())
                else Result.failure(ServiceException(result.getMessage()))
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
                Timber.d("result is $result")
                if (result.isSuccess()) Result.success(result.getData())
                else Result.failure(ServiceException(result.getMessage()))
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
                if (result.isSuccess()) Result.success(result.getData())
                else Result.failure(ServiceException(result.getMessage()))
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
                Timber.d("result is $result")
                if (result.isSuccess()) Result.success(result.getData())
                else Result.failure(ServiceException(result.getMessage()))
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
                Timber.d("result is $result")
                if (result.isSuccess()) Result.success(result.getData())
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
                Timber.d("result is $result")
                if ("0" == result.errno) Result.success(result.data)
                else Result.failure(ServiceException())
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
        emit(result)
    }

    fun postComment(momentComment: Map<String, Any?>, isReply: Boolean) = liveData(Dispatchers.IO) {
        val result = try {
            coroutineScope {
                val result = if (isReply) FishNetwork.replyComment(momentComment)
                else FishNetwork.postComment(momentComment)
                Timber.d("result is $result")
                if (result.isSuccess()) Result.success(result.getData())
                else Result.failure(ServiceException(result.getMessage()))
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
        emit(result)
    }

    fun dynamicLikes(momentId: String) = liveData(Dispatchers.IO) {
        val result = try {
            coroutineScope {
                val result = FishNetwork.dynamicLikes(momentId)
                Timber.d("result is $result")
                if (result.isSuccess()) Result.success(result.getData())
                else Result.failure(ServiceException(result.getMessage()))
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
        emit(result)
    }

    fun loadTopicList() = liveData(Dispatchers.IO) {
        val result = try {
            coroutineScope {
                val result = FishNetwork.loadTopicList()
                Timber.d("result is $result")
                if (result.isSuccess()) Result.success(result.getData())
                else Result.failure(ServiceException(result.getMessage()))
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
        emit(result)
    }

    fun getRichList() = liveData(Dispatchers.IO) {
        val result = try {
            coroutineScope {
                val result = UserNetwork.getRichList()
                Timber.d("result is $result")
                if (result.isSuccess()) Result.success(result.getData())
                else Result.failure(ServiceException(result.getMessage()))
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
        emit(result)
    }

    fun readAllMsg() = liveData(Dispatchers.IO) {
        val result = try {
            coroutineScope {
                val result = MsgNetwork.readAllMsg()
                Timber.d("result is $result")
                if (result.isSuccess()) Result.success(result.getMessage())
                else Result.failure(ServiceException(result.getMessage()))
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
        emit(result)
    }
}