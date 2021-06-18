package com.example.blogsystem.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.blogsystem.R
import com.example.blogsystem.base.BaseFragment
import com.example.blogsystem.databinding.FragmentLoginBinding
import com.example.blogsystem.ui.activity.HomeActivity
import com.example.blogsystem.utils.*
import com.example.blogsystem.viewmodel.SingletonManager
import com.example.blogsystem.viewmodel.UserViewModel
import com.example.blogsystem.viewmodel.login.LoggedInUserView
import com.google.android.material.textfield.TextInputEditText

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class LoginFragment : BaseFragment() {

    private var _binding: FragmentLoginBinding? = null

    // 此属性仅在 onCreateView 和 onDestroyView 之间有效
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by lazy {
        SingletonManager.userViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callAllInit()
    }

    override fun initData() {
        userViewModel.checkUserState()
        loadVerifyCode()
    }

    override fun initEvent() {
        val account = binding.editAccount
        val password = binding.editPassword
        val verifyCodeEt = binding.editVerifyCode
        val verifyCodeIv = binding.imageVerifyCode
        val login = binding.buttonLogin
        account.afterTextChanged {
            loginChanged(account, password, verifyCodeEt)
        }
        password.afterTextChanged {
            loginChanged(account, password, verifyCodeEt)
        }
        verifyCodeEt.afterTextChanged {
            loginChanged(account, password, verifyCodeEt)
        }
        verifyCodeEt.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    login(account, password, verifyCodeEt)
                }
            }
            false
        }
        verifyCodeIv.setOnClickListener {
            loadVerifyCode()
        }
        login.setOnClickListener {
            login(account, password, verifyCodeEt)
            loadVerifyCode()
        }
        binding.buttonRegister.setOnClickListener {
            findNavController().navigate(R.id.action_LoginFragment_to_RegisterFragment)
        }
        userViewModel.loginFormState.observe(this) { loginState ->
            // 除非用户名、密码都有效且输入了验证码，否则禁用登录按钮
            login.isEnabled = loginState.isDataValid
            if (loginState.userAccountError != null) {
                account.error = getString(loginState.userAccountError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
            if (loginState.verifyError != null) {
                verifyCodeEt.error = getString(loginState.verifyError)
            }
        }
        userViewModel.loginResult.observe(this) { loginResult ->
            // 隐藏加载状态
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
                requireActivity().apply {
                    setResult(Activity.RESULT_OK)
                    startActivity<HomeActivity>()
                    finish()
                }
            }
        }
    }

    private fun login(
        account: TextInputEditText,
        password: TextInputEditText,
        verifyCodeEt: TextInputEditText
    ) {
        userViewModel.login(
            account.textString,
            password.textString,
            verifyCodeEt.textString
        )
    }

    private fun loginChanged(
        account: TextInputEditText,
        password: TextInputEditText,
        verifyCodeEt: TextInputEditText
    ) {
        userViewModel.loginDataChanged(
            account.textString,
            password.textString,
            verifyCodeEt.textString
        )
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // 登录成功的提示
        simpleToast("$welcome，$displayName")
    }

    private fun showLoginFailed(errorString: String) {
        loadVerifyCode()
        simpleToast(errorString)
    }

    /**
     * 以下情况会更新验证码图片
     * 1、首次进入
     * 2、点击验证码
     * 3、登录失败
     */
    private fun loadVerifyCode() {
        Glide.with(this)
            .load(VERIFY_CODE_URL)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.mipmap.ic_no_image)
            .error(R.mipmap.ic_no_image)
            .into(binding.imageVerifyCode)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}