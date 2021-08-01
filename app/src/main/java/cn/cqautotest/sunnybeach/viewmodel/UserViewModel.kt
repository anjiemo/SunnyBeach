package cn.cqautotest.sunnybeach.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppApplication
import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.request.api.UserApi
import cn.cqautotest.sunnybeach.model.BaseResponse
import cn.cqautotest.sunnybeach.model.User
import cn.cqautotest.sunnybeach.model.UserBasicInfo
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.util.*
import cn.cqautotest.sunnybeach.viewmodel.login.LoggedInUserView
import cn.cqautotest.sunnybeach.viewmodel.login.LoginFormState
import cn.cqautotest.sunnybeach.viewmodel.login.LoginResult
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.RegexUtils
import com.example.blogsystem.execption.LoginFailedException
import com.hjq.http.EasyConfig
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/6/18
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

    /**
     * 用户账号登录
     */
    fun login(userAccount: String, password: String, captcha: String) = viewModelScope.launch {
        cookieDao
        val available = withContext(Dispatchers.IO) { NetworkUtils.isAvailable() }
        if (available.not()) return@launch
        val context = getApplication<Application>()
        var userBasicInfoResponse: BaseResponse<UserBasicInfo>? = null
        runCatching {
            val user = User(
                phoneNum = userAccount, password = password.md5
                    .toLowerCase(Locale.SIMPLIFIED_CHINESE)
            )
            val loginResult = userApi.login(captcha = captcha, user = user)
            val tempUserBasicInfoResponse = userApi.checkToken()
            val tempUserBasicInfoResponseData = tempUserBasicInfoResponse.data
            // 只有Token校验成功获取到数据才算登录成功，否则也是属于登录失败了
            if (SUNNY_BEACH_HTTP_OK_CODE == loginResult.code && SUNNY_BEACH_HTTP_OK_CODE == tempUserBasicInfoResponse.code) {
                userBasicInfoResponse = tempUserBasicInfoResponse
                logByDebug(tag = "UserViewModel：", msg = "login：===>$tempUserBasicInfoResponseData")
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
                        displayName = userBasicInfoResponse?.data?.nickname ?: ""
                    )
                )
            setupAutoLogin(true)
        }.onFailure {
            _loginResult.value =
                LoginResult(error = context.getString(R.string.login_failed))
            logByDebug(tag = "UserViewModel：", msg = "login：====>登陆失败，error msg is $it")
            setupAutoLogin(false)
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
    fun checkUserToken() = viewModelScope.launch {
        val available = withContext(Dispatchers.IO) { NetworkUtils.isAvailable() }
        if (available.not()) {
            if (isLogin()) {
                setupAutoLogin(true)
            }
            return@launch
        }
        runCatching {
            userApi.checkToken()
        }.onSuccess { response ->
            val userBasicInfo = response.data
            val loginSuccess = SUNNY_BEACH_HTTP_OK_CODE == response.code
            _userBasicInfo.value = userBasicInfo
            if (loginSuccess) {
                saveUserBasicInfo(userBasicInfo)
            }
        }.onFailure {
            setupAutoLogin(false)
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
    fun checkUserState() = viewModelScope.launch {
        val available = withContext(Dispatchers.IO) { NetworkUtils.isAvailable() }
        if (available.not()) return@launch
        val userBasicInfoResponse = userApi.checkToken()
        val userBasicInfoResponseData = userBasicInfoResponse.data
        if (SUNNY_BEACH_HTTP_OK_CODE == userBasicInfoResponse.code) {
            saveUserBasicInfo(userBasicInfoResponseData)
            _loginResult.value =
                LoginResult(success = LoggedInUserView(displayName = userBasicInfoResponseData.nickname))
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