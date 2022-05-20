package cn.cqautotest.sunnybeach.http.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import cn.cqautotest.sunnybeach.db.dao.PlaceDao
import cn.cqautotest.sunnybeach.execption.NotLoginException
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.ktx.getOrNull
import cn.cqautotest.sunnybeach.ktx.lowercaseMd5
import cn.cqautotest.sunnybeach.ktx.toJson
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.*
import cn.cqautotest.sunnybeach.model.wallpaper.WallpaperBean
import cn.cqautotest.sunnybeach.model.weather.Place
import cn.cqautotest.sunnybeach.model.weather.Weather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File
import kotlin.coroutines.CoroutineContext

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/09/07
 *    desc   : 存储库
 */
object Repository {

    private const val NOT_LOGIN_CODE = 11126

    private val cachePhotoIdList = arrayListOf<WallpaperBean.Res.Vertical>()

    fun getCoursePlayAuth(videoId: String) = launchAndGetData { CourseNetwork.getCoursePlayAuth(videoId) }

    fun modifyPasswordBySms(smsCode: String, user: User) = launchAndGetMsg { UserNetwork.modifyPasswordBySms(smsCode, user) }

    fun modifyPasswordByOldPwd(modifyPwd: ModifyPwd) = launchAndGetMsg { UserNetwork.modifyPasswordByOldPwd(modifyPwd) }

    fun checkSmsCode(phoneNumber: String, smsCode: String) = launchAndGetMsg { UserNetwork.checkSmsCode(phoneNumber, smsCode) }

    fun sendForgetSmsVerifyCode(smsInfo: SmsInfo) = launchAndGetMsg { UserNetwork.sendForgetSmsVerifyCode(smsInfo) }

    fun registerAccount(smsCode: String, user: User) = launchAndGetMsg { UserNetwork.registerAccount(smsCode, user) }

    fun sendRegisterSmsVerifyCode(smsInfo: SmsInfo) = launchAndGetMsg { UserNetwork.sendRegisterSmsVerifyCode(smsInfo) }

    fun getSobIEDetailList(userId: String, page: Int) = launchAndGetData { UserNetwork.getSobIEDetailList(userId, page) }

    fun getVipUserList() = launchAndGetData { UserNetwork.getVipUserList() }

    fun getAchievement() = launchAndGetData { UserNetwork.getAchievement() }

    fun queryTotalSobCount() = launchAndGetData { UserNetwork.queryTotalSobCount() }

    fun queryUserInfo() = launchAndGetData { UserNetwork.queryUserInfo() }

    fun getAllowance() = liveData(build = { UserNetwork.getAllowance() }) {
        when (it.getCode()) {
            // 未领取VIP津贴
            11129 -> Result.success(false)
            // 已经领取了VIP津贴
            11128 -> Result.success(true)
            else -> Result.failure(ServiceException(it.getMessage()))
        }
    }

    fun checkAllowance() = liveData(build = { UserNetwork.checkAllowance() }) {
        when (it.getCode()) {
            // 未领取VIP津贴
            11129 -> Result.success(false)
            // 已经领取了VIP津贴
            11128 -> Result.success(true)
            else -> Result.failure(ServiceException(it.getMessage()))
        }
    }

    fun unfollowUser(userId: String) = launchAndGetData { UserNetwork.unfollowUser(userId) }

    fun followUser(userId: String) = launchAndGetData { UserNetwork.followUser(userId) }

    fun followState(userId: String) = launchAndGetData { UserNetwork.followState(userId) }

    fun getUserInfo(userId: String) = launchAndGetData { UserNetwork.getUserInfo(userId) }

    fun getAchievement(userId: String) = launchAndGetData { UserNetwork.getAchievement(userId) }

    fun logout() = launchAndGetMsg { UserNetwork.logout() }

    suspend fun checkToken() = try {
        val result = UserNetwork.checkToken()
        val userBasicInfo = result.getOrNull() ?: throw ServiceException()
        UserManager.saveUserBasicInfo(userBasicInfo)
        UserManager.setupAutoLogin(true)
        userBasicInfo
    } catch (t: Throwable) {
        t.printStackTrace()
        UserManager.saveUserBasicInfo(null)
        UserManager.setupAutoLogin(false)
        null
    }

    suspend fun login(userAccount: String, password: String, captcha: String) = try {
        val user = User(userAccount, password.lowercaseMd5)
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

    fun queryUserAvatar(account: String) = launchAndGetData { UserNetwork.queryUserAvatar(account) }

    fun checkAppUpdate(): LiveData<Result<AppUpdateInfo>> = launchAndGetData { AppNetwork.checkAppUpdate() }

    fun getMourningCalendar(): LiveData<Result<List<MourningCalendar>>> = launchAndGetData { AppNetwork.getMourningCalendar() }

    fun setPhotoIdList(photoIdList: List<WallpaperBean.Res.Vertical>) {
        if (photoIdList !== cachePhotoIdList) {
            cachePhotoIdList.clear()
            if (photoIdList.isEmpty().not()) {
                cachePhotoIdList.addAll(photoIdList)
            }
        } else {
            if (photoIdList.isEmpty().not()) {
                val newList = photoIdList.toList()
                cachePhotoIdList.clear()
                cachePhotoIdList.addAll(newList)
            } else {
                cachePhotoIdList.clear()
            }
        }
    }

    fun getPhotoList() = cachePhotoIdList.toList()

    fun getFishCommendListById(momentId: String, page: Int) = launchAndGetData { FishNetwork.getFishCommendListById(momentId, page) }

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

    fun putFish(moment: Map<String, Any?>) = launchAndGetData { FishNetwork.putFish(moment) }

    fun loadFishDetailById(momentId: String) = launchAndGetData { FishNetwork.loadFishDetailById(momentId) }

    fun loadWallpaperBannerList() = liveData(build = { PhotoNetwork.loadWallpaperBannerList() }) {
        if ("0" == it.errno) Result.success(it.data)
        else Result.failure(ServiceException(it.errmsg))
    }

    fun postComment(momentComment: Map<String, Any?>, isReply: Boolean) = launchAndGetData {
        if (isReply) FishNetwork.replyComment(momentComment)
        else FishNetwork.postComment(momentComment)
    }

    fun dynamicLikes(momentId: String) = launchAndGetMsg { FishNetwork.dynamicLikes(momentId) }

    fun loadTopicList() = launchAndGetData { FishNetwork.loadTopicList() }

    fun getRichList() = launchAndGetData { UserNetwork.getRichList() }

    fun readQaMsg(msgId: String) = launchAndGetMsg { MsgNetwork.readQaMsg(msgId) }

    fun readAtMeMsg(msgId: String) = launchAndGetMsg { MsgNetwork.readAtMeMsg(msgId) }

    fun readMomentMsg(msgId: String) = launchAndGetMsg { MsgNetwork.readMomentMsg(msgId) }

    /**
     * 更新文章消息为：已回复
     */
    fun replyArticleMsg(msgId: String) = launchAndGetMsg { MsgNetwork.readArticleMsg(msgId, 2) }

    /**
     * 更新文章消息为：已读
     */
    fun readArticleMsg(msgId: String) = launchAndGetMsg { MsgNetwork.readArticleMsg(msgId, 1) }

    /**
     * 一键已读所有消息
     */
    fun readAllMsg() = launchAndGetMsg { MsgNetwork.readAllMsg() }

    /**
     * 获取未读消息数量
     */
    fun getUnReadMsgCount() = launchAndGetData { MsgNetwork.getUnReadMsgCount() }

    fun searchPlaces(query: String) = liveData(build = { WeatherNetwork.searchPlace(query) }) { placeResponse ->
        Timber.d("${placeResponse.status}|${placeResponse.places[0].name}")
        if (placeResponse.status == "ok") {
            Timber.d(placeResponse.status)
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }

    /**
     * 刷新天气
     */
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
                    val weather = Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)
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

    /**
     * 保存地点
     */
    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    /**
     * 获取保存的地点
     */
    fun getSavedPlace() = PlaceDao.getSavedPlace()

    /**
     * 是否保存了地点
     */
    fun isSaved() = PlaceDao.isSaved()

    private inline fun <R, T> liveData(
        context: CoroutineContext = Dispatchers.IO,
        crossinline build: suspend () -> R,
        crossinline onError: (Throwable) -> Unit = { it.printStackTrace() },
        crossinline action: (R) -> Result<T>
    ) = liveData(context) {
        val result = try {
            coroutineScope {
                action.invoke(build.invoke())
            }
        } catch (t: Throwable) {
            onError.invoke(t)
            Result.failure(t)
        }
        emit(result)
    }

    /**
     * 启动并获取数据
     */
    private inline fun <reified T> launchAndGetData(
        context: CoroutineContext = Dispatchers.IO,
        crossinline action: suspend () -> ApiResponse<T>
    ) =
        launchAndGet(context = context, action = action, onSuccess = { it.getData() })

    /**
     * 启动并获取消息
     */
    private inline fun launchAndGetMsg(context: CoroutineContext = Dispatchers.IO, crossinline action: suspend () -> ApiResponse<Any>) =
        launchAndGet(context = context, action = action, onSuccess = { it.getMessage() })

    /**
     * 启动并获取
     * 返回一个
     */
    private inline fun <reified T> launchAndGet(
        context: CoroutineContext = Dispatchers.IO,
        // 需要调用的接口
        crossinline action: suspend () -> ApiResponse<T>,
        // 请求成功时的回调
        crossinline onSuccess: (ApiResponse<T>) -> T
    ) = liveData(context = context) {
        val result = try {
            coroutineScope {
                val result = action.invoke()
                Timber.d("launchAndGet：===> result is ${result.toJson()}")
                if (result.isSuccess()) Result.success(onSuccess.invoke(result))
                else when (result.getCode()) {
                    NOT_LOGIN_CODE -> Result.failure(NotLoginException(result.getMessage()))
                    else -> Result.failure(ServiceException(result.getMessage()))
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
        emit(result)
    }
}