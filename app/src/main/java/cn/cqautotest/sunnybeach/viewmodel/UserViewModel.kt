package cn.cqautotest.sunnybeach.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppApplication
import cn.cqautotest.sunnybeach.execption.LoginFailedException
import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.request.api.UserApi
import cn.cqautotest.sunnybeach.model.*
import cn.cqautotest.sunnybeach.other.FollowState
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.paging.source.RichPagingSource
import cn.cqautotest.sunnybeach.paging.source.UserFollowPagingSource
import cn.cqautotest.sunnybeach.util.*
import cn.cqautotest.sunnybeach.viewmodel.app.Repository
import cn.cqautotest.sunnybeach.viewmodel.login.LoggedInUserView
import cn.cqautotest.sunnybeach.viewmodel.login.LoginFormState
import cn.cqautotest.sunnybeach.viewmodel.login.LoginResult
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.RegexUtils
import com.hjq.http.EasyConfig
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/06/18
 * desc   : 用户 ViewModel
 */
class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val cookieDao = AppApplication.getDatabase().cookieDao()

    // 登录状态表单
    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> get() = _loginForm

    // 登录结果
    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    // 用户基础数据信息
    private val _userBasicInfo = MutableLiveData<UserBasicInfo>()
    val userBasicInfo: LiveData<UserBasicInfo> get() = _userBasicInfo

    // userApi
    private val userApi by lazy { ServiceCreator.create<UserApi>() }

    private val phoneLiveData = MutableLiveData<String>()

    val userAvatarLiveData = Transformations.switchMap(phoneLiveData) { account ->
        Repository.queryUserAvatar(account)
    }

    /**
     * 检查是否领取过津贴
     */
    fun checkAllowance() = Repository.checkAllowance()

    /**
     * 领取津贴
     */
    fun getAllowance() = Repository.getAllowance()

    /**
     * 获取用户的关注/粉丝列表
     */
    fun getUserFollowList(
        userId: String,
        followState: FollowState
    ): Flow<PagingData<UserFollow.UserFollowItem>> {
        return Pager(config = PagingConfig(30),
            pagingSourceFactory = {
                UserFollowPagingSource(userId, followState)
            }).flow.cachedIn(viewModelScope)
    }

    /**
     * 取消关注用户
     */
    fun unfollowUser(userId: String) = Repository.unfollowUser(userId)

    /**
     * 关注用户
     */
    fun followUser(userId: String) = Repository.followUser(userId)

    /**
     * 自己与目标用户的关注状态
     *
     * 0：表示没有关注对方，可以显示为：关注
     * 1：表示对方关注自己，可以显示为：回粉
     * 2：表示已经关注对方，可以显示为：已关注
     * 3：表示相互关注，可以显示为：相互关注
     */
    fun followState(userId: String) = Repository.followState(userId)

    /**
     * 获取用户信息
     */
    fun getUserInfo(userId: String) = Repository.getUserInfo(userId)

    /**
     * 获取当前用户的成就
     */
    fun getAchievement(userId: String = "") = Repository.getAchievement(userId)

    /**
     * 通过账号（手机号）获取用户头像
     */
    fun queryUserAvatar(account: String) {
        phoneLiveData.value = account
    }

    /**
     * 获取富豪榜
     */
    fun getRichList(): Flow<PagingData<RichList.RichUserItem>> {
        return Pager(config = PagingConfig(30),
            pagingSourceFactory = {
                RichPagingSource()
            }).flow.cachedIn(viewModelScope)
    }

    /**
     * 用户账号登录
     */
    fun login(userAccount: String, password: String, captcha: String) {
        viewModelScope.launch {
            val context = getApplication<Application>()
            var userBasicInfoResponse: ApiResponse<UserBasicInfo>? = null
            runCatching {
                val user = User(
                    phoneNum = userAccount, password = password.md5
                        .toLowerCase(Locale.SIMPLIFIED_CHINESE)
                )
                val loginResult = userApi.login(captcha = captcha, user = user)
                val tempUserBasicInfoResponse = withContext(Dispatchers.IO) { userApi.checkToken() }
                val tempUserBasicInfoResponseData = tempUserBasicInfoResponse.getData()
                // 只有Token校验成功获取到数据才算登录成功，否则也是属于登录失败了
                if (SUNNY_BEACH_HTTP_OK_CODE == loginResult.getCode() && SUNNY_BEACH_HTTP_OK_CODE == tempUserBasicInfoResponse.getCode()) {
                    userBasicInfoResponse = tempUserBasicInfoResponse
                    Timber.d("userBasicInfoResponseData is ${tempUserBasicInfoResponseData.toJson()}")
                    _userBasicInfo.value = tempUserBasicInfoResponseData
                    // 将基本信息缓存到本地
                    saveUserBasicInfo(tempUserBasicInfoResponseData)
                    // 更新 Token
                    EasyConfig.getInstance()
                        .addParam(IntentKey.TOKEN, tempUserBasicInfoResponseData.token)
                    loginResult
                } else {
                    throw LoginFailedException()
                }
            }.onSuccess {
                _loginResult.value =
                    LoginResult(
                        success = LoggedInUserView(
                            displayName = userBasicInfoResponse?.getData()?.nickname ?: ""
                        )
                    )
                setupAutoLogin(true)
            }.onFailure {
                it.printStackTrace()
                _loginResult.value =
                    LoginResult(error = context.getString(R.string.login_failed))
                Timber.d("登陆失败，error msg is $it")
                setupAutoLogin(false)
            }
        }
    }

    /**
     * 设置用户是否自动登录
     */
    fun setupAutoLogin(autoLogin: Boolean) {
        val mmkv = MMKV.defaultMMKV() ?: return
        mmkv.putBoolean(AUTO_LOGIN, autoLogin)
    }

    /**
     * 获取是否自动登录
     */
    fun isAutoLogin(): Boolean {
        val mmkv = MMKV.defaultMMKV() ?: return false
        return mmkv.getBoolean(AUTO_LOGIN, false)
    }

    /**
     * 检查用户 Token
     */
    fun checkUserToken() {
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    userApi.checkToken()
                }
            }.onSuccess { response ->
                val userBasicInfo = response.getData()
                val loginSuccess = SUNNY_BEACH_HTTP_OK_CODE == response.getCode()
                _userBasicInfo.value = userBasicInfo
                if (loginSuccess) {
                    saveUserBasicInfo(userBasicInfo)
                }
            }.onFailure {
                it.printStackTrace()
                setupAutoLogin(false)
            }
        }
    }

    /**
     * 用户是否已经成功登录过一次了
     */
    fun isLogin() = loadUserBasicInfo() != null

    /**
     * 登录状态改变
     */
    fun loginDataChanged(userAccount: String, password: String, captcha: String) {
        _loginForm.value = when {
            isUserAccountValid(userAccount).not() -> LoginFormState(userAccountError = R.string.invalid_username)
            isPasswordValid(password).not() -> LoginFormState(passwordError = R.string.invalid_password)
            isVerifyCodeValid(captcha).not() -> LoginFormState(verifyError = R.string.invalid_verify_code)
            else -> LoginFormState(isDataValid = true)
        }
    }

    /**
     * 检查用户登录状态
     */
    fun checkUserState() {
        viewModelScope.launch {
            val userBasicInfoResponse = withContext(Dispatchers.IO) { userApi.checkToken() }
            val userBasicInfoResponseData = userBasicInfoResponse.getData()
            if (SUNNY_BEACH_HTTP_OK_CODE == userBasicInfoResponse.getCode()) {
                saveUserBasicInfo(userBasicInfoResponseData)
                _loginResult.value =
                    LoginResult(success = LoggedInUserView(displayName = userBasicInfoResponseData.nickname))
            }
        }
    }

    /**
     * 保存用户基本信息
     */
    private fun saveUserBasicInfo(userBasicInfo: UserBasicInfo) {
        val mmkv = MMKV.defaultMMKV() ?: return
        mmkv.putString(SUNNY_BEACH_USER_BASIC_INFO, userBasicInfo.toJson())
    }

    /**
     * 清除用户基本信息数据
     */
    fun logoutUserAccount() {
        // 使用异步清空浏览器的 Cookies 缓存
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                android.webkit.CookieManager.getInstance().removeAllCookies(null)
            }
            withContext(Dispatchers.IO) {
                cookieDao.clearCookies()
            }
        }
        _loginResult.value = LoginResult()
        MMKV.defaultMMKV()?.removeValueForKey(SUNNY_BEACH_USER_BASIC_INFO)
    }

    /**
     * 获取用户基本信息
     */
    fun loadUserBasicInfo(): UserBasicInfo? {
        val mmkv = MMKV.defaultMMKV() ?: return null
        return runCatching {
            val jsonByUserBasicInfo = mmkv.getString(SUNNY_BEACH_USER_BASIC_INFO, null)
            GsonUtils.fromJson(jsonByUserBasicInfo, UserBasicInfo::class.java)
        }.getOrNull()
    }

    /**
     * 手机号码格式检查
     */
    private fun isUserAccountValid(userAccount: String): Boolean =
        RegexUtils.isMobileExact(userAccount)

    /**
     * 账号密码长度检查
     */
    private fun isPasswordValid(password: String): Boolean = password.length > 5

    /**
     * 验证码检查
     */
    private fun isVerifyCodeValid(verifyCode: String): Boolean = verifyCode.isNotEmpty()
}