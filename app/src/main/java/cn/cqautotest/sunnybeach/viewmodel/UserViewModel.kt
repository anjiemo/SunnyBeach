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
import cn.cqautotest.sunnybeach.utils.SUNNY_BEACH_HTTP_OK_CODE
import cn.cqautotest.sunnybeach.utils.SUNNY_BEACH_USER_BASIC_INFO
import cn.cqautotest.sunnybeach.utils.md5
import cn.cqautotest.sunnybeach.utils.toJson
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

    // 自动登录
    private val _autoLogin = MutableLiveData<Boolean>()
    val autoLogin: LiveData<Boolean> get() = _autoLogin

    // 登录状态表单
    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> get() = _loginForm

    // 登录结果
    private val _loginResult = MutableLiveData<LoginResult?>()
    val loginResult: LiveData<LoginResult?> = _loginResult

    // 用户基础数据信息
    private val _userBasicInfo = MutableLiveData<UserBasicInfo?>()
    val userBasicInfo: LiveData<UserBasicInfo?> get() = _userBasicInfo

    // userApi
    private val userApi by lazy { ServiceCreator.create<UserApi>() }

    /**
     * 用户账号登录
     */
    fun login(userAccount: String, password: String, captcha: String) = viewModelScope.launch {
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
                _userBasicInfo.value = tempUserBasicInfoResponseData
                // 更新 Token
                EasyConfig.getInstance()
                    .addParam("token", tempUserBasicInfoResponseData.token)
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
        }.onFailure {
            _loginResult.value =
                LoginResult(error = context.getString(R.string.login_failed))
        }
    }

    /**
     * 检查用户 Token
     */
    fun checkUserToken() = viewModelScope.launch {
        val available = withContext(Dispatchers.IO) { NetworkUtils.isAvailable() }
        if (available.not()) {
            if (getUserBasicInfo() != null) {
                _autoLogin.value = true
            }
            return@launch
        }
        runCatching {
            userApi.checkToken()
        }.onSuccess { response ->
            val userBasicInfo = response.data
            val loginSuccess = SUNNY_BEACH_HTTP_OK_CODE == response.code
            _autoLogin.value = loginSuccess
            _userBasicInfo.value = userBasicInfo
            if (loginSuccess) {
                saveUserBasicInfo(userBasicInfo)
            }
        }.onFailure {
            _autoLogin.value = false
        }
    }

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
        val mmkv = MMKV.defaultMMKV() ?: return
        mmkv.removeValueForKey(SUNNY_BEACH_USER_BASIC_INFO)
        _loginResult.value = null
        _userBasicInfo.value = null
    }

    /**
     * 获取用户基本信息
     */
    private fun getUserBasicInfo(): UserBasicInfo? {
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