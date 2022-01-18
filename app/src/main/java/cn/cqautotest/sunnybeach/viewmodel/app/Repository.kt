package cn.cqautotest.sunnybeach.viewmodel.app

import androidx.lifecycle.liveData
import cn.cqautotest.sunnybeach.db.dao.PlaceDao
import cn.cqautotest.sunnybeach.execption.NotLoginException
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.http.response.model.WallpaperBean
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.User
import cn.cqautotest.sunnybeach.model.weather.Place
import cn.cqautotest.sunnybeach.util.md5
import cn.cqautotest.sunnybeach.util.toJson
import cn.cqautotest.sunnyweather.logic.model.Weather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    fun logout() = liveData(Dispatchers.IO) {
        val result = try {
            coroutineScope {
                val result = UserNetwork.logout()
                Timber.d("result is $result")
                System.currentTimeMillis()
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            System.currentTimeMillis()
        }
        emit(result)
    }

    suspend fun checkToken() = try {
        val result = UserNetwork.checkToken()
        if (result.isSuccess()) {
            val userBasicInfo = result.getData()
            UserManager.saveUserBasicInfo(userBasicInfo)
            UserManager.setupAutoLogin(true)
            userBasicInfo
        } else throw ServiceException()
    } catch (t: Throwable) {
        t.printStackTrace()
        UserManager.setupAutoLogin(false)
        null
    }

    suspend fun login(userAccount: String, password: String, captcha: String) = try {
        val user = User(userAccount, password.md5.lowercase())
        val result = UserNetwork.login(captcha, user)
        if (result.isSuccess()) {
            val userBasicInfo = checkToken()
            // 将基本信息缓存到本地
            UserManager.saveUserBasicInfo(userBasicInfo)
            UserManager.setupAutoLogin(userBasicInfo != null)
            userBasicInfo ?: throw NotLoginException()
        } else throw ServiceException()
    } catch (t: Throwable) {
        t.printStackTrace()
        UserManager.setupAutoLogin(false)
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

    suspend fun uploadFishImage(imageFile: File): String? {
        return try {
            val fileName = imageFile.name
            Timber.d("===> fileName is $fileName")
            val requestBody = RequestBody.create("image/png".toMediaType(), imageFile)
            val part = MultipartBody.Part.createFormData("image", fileName, requestBody)
            val result = FishNetwork.uploadFishImage(part)
            Timber.d("result is ${result.toJson()}")
            // 此处不能返回 Result<T> ，详见：https://github.com/mockk/mockk/issues/443
            // Result getOrNull give ClassCastException：https://stackoverflow.com/questions/68016267/result-getornull-give-classcastexception
            if (result.isSuccess()) result.getData()
            else null
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        }
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

    fun searchPlaces(query: String) = liveData(Dispatchers.IO) {
        Timber.d(query)
        val result = try {
            val placeResponse = WeatherNetwork.searchPlace(query)
            Timber.d("${placeResponse.status}|${placeResponse.places[0].name}")
            if (placeResponse.status == "ok") {
                Timber.d(placeResponse.status)
                val places = placeResponse.places
                Result.success(places)
            } else {
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
        emit(result)
    }

    fun refreshWeather(lng: String, lat: String) = liveData(Dispatchers.IO) {
        val result = try {
            coroutineScope {
                val deferredRealtime = async {
                    WeatherNetwork.getRealtimeWeather(lng, lat)
                }
                val deferredDaily = async {
                    WeatherNetwork.getDailyWeather(lng, lat)
                }
                val realtimeResponse = deferredRealtime.await()
                val dailyResponse = deferredDaily.await()
                if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                    val weather =
                        Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)
                    Result.success(weather)
                } else {
                    Result.failure(
                        RuntimeException(
                            "realtimeWeather status is ${realtimeResponse.status}\n" +
                                    "dailyWeather status is ${dailyResponse.status}"
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
        emit(result)
    }

    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    fun getSavedPlace() = PlaceDao.getSavedPlace()

    fun isSaved() = PlaceDao.isSaved()
}