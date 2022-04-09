package cn.cqautotest.sunnybeach.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.BaseActivity;
import com.hjq.widget.view.CountdownView;
import com.hjq.widget.view.SubmitButton;

import cn.cqautotest.sunnybeach.R;
import cn.cqautotest.sunnybeach.aop.DebugLog;
import cn.cqautotest.sunnybeach.aop.SingleClick;
import cn.cqautotest.sunnybeach.app.AppActivity;
import cn.cqautotest.sunnybeach.http.glide.GlideApp;
import cn.cqautotest.sunnybeach.manager.InputTextManager;
import cn.cqautotest.sunnybeach.model.SmsInfo;
import cn.cqautotest.sunnybeach.model.User;
import cn.cqautotest.sunnybeach.other.IntentKey;
import cn.cqautotest.sunnybeach.util.Constants;
import cn.cqautotest.sunnybeach.util.StringKt;
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 注册界面
 */
public final class RegisterActivity extends AppActivity
        implements TextView.OnEditorActionListener {

    @DebugLog
    public static void start(BaseActivity activity, String phone, String password, OnRegisterListener listener) {
        Intent intent = new Intent(activity, RegisterActivity.class);
        intent.putExtra(IntentKey.PHONE, phone);
        intent.putExtra(IntentKey.PASSWORD, password);
        activity.startActivityForResult(intent, (resultCode, data) -> {

            if (listener == null || data == null) {
                return;
            }

            if (resultCode == RESULT_OK) {
                listener.onSucceed(data.getStringExtra(IntentKey.PHONE), data.getStringExtra(IntentKey.PASSWORD));
            } else {
                listener.onCancel();
            }
        });
    }

    private UserViewModel mUserViewModel = null;

    private EditText mInputVerifyCodeView;
    private ImageView mVerifyCodeView;
    private EditText mPhoneView;
    private CountdownView mCountdownView;

    private EditText mCodeView;
    private EditText mNickNameView;

    private EditText mFirstPassword;
    private EditText mSecondPassword;

    private SubmitButton mCommitView;

    @Override
    protected int getLayoutId() {
        return R.layout.register_activity;
    }

    @Override
    protected void initView() {
        mInputVerifyCodeView = findViewById(R.id.et_register_verify_code);
        mVerifyCodeView = findViewById(R.id.siv_register_verify_code);
        mPhoneView = findViewById(R.id.et_register_phone);
        mCountdownView = findViewById(R.id.cv_register_countdown);
        mCodeView = findViewById(R.id.et_register_code);
        mNickNameView = findViewById(R.id.et_register_nick_name);
        mFirstPassword = findViewById(R.id.et_register_password1);
        mSecondPassword = findViewById(R.id.et_register_password2);
        mCommitView = findViewById(R.id.btn_register_commit);

        setOnClickListener(mVerifyCodeView, mCountdownView, mCommitView);

        mSecondPassword.setOnEditorActionListener(this);

        // 给这个 View 设置沉浸式，避免状态栏遮挡
        ImmersionBar.setTitleBar(this, findViewById(R.id.tv_register_title));

        InputTextManager.with(this)
                .addView(mInputVerifyCodeView)
                .addView(mPhoneView)
                .addView(mCodeView)
                .addView(mNickNameView)
                .addView(mFirstPassword)
                .addView(mSecondPassword)
                .setMain(mCommitView)
                .build();
    }

    @Override
    protected void initData() {
        mUserViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication())
                .create(UserViewModel.class);
        loadVerifyCode();
        // 自动填充手机号和密码
        mPhoneView.setText(getString(IntentKey.PHONE));
        mFirstPassword.setText(getString(IntentKey.PASSWORD));
        mSecondPassword.setText(getString(IntentKey.PASSWORD));
    }

    /**
     * 加载验证码图片
     */
    private void loadVerifyCode() {
        GlideApp.with(this)
                .load(Constants.VERIFY_CODE_URL)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(mVerifyCodeView);
    }

    @Override
    public void onClick(View view) {
        if (view == mVerifyCodeView) {
            loadVerifyCode();
            return;
        }
        onSingleClick(view);
    }

    @SingleClick
    private void onSingleClick(View view) {
        if (view == mCountdownView) {
            if (TextUtils.isEmpty(mInputVerifyCodeView.getText())) {
                mInputVerifyCodeView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                toast(R.string.common_verify_code_input_hint);
                return;
            }

            if (mPhoneView.getText().toString().length() != 11) {
                mPhoneView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                toast(R.string.common_phone_input_error);
                return;
            }

            SmsInfo smsInfo = new SmsInfo(mPhoneView.getText().toString(), mInputVerifyCodeView.getText().toString());
            // 发送验证码
            mUserViewModel.sendRegisterSmsVerifyCode(smsInfo).observe(this, result -> {
                toast(result.getMessage());
                mCountdownView.start();
            });
        } else if (view == mCommitView) {
            if (mPhoneView.getText().toString().length() != 11) {
                mPhoneView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mCommitView.showError(3000);
                toast(R.string.common_phone_input_error);
                return;
            }

            if (mCodeView.getText().toString().length() != getResources().getInteger(R.integer.sms_code_length)) {
                mCodeView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mCommitView.showError(3000);
                toast(R.string.common_code_error_hint);
                return;
            }

            if (!mFirstPassword.getText().toString().equals(mSecondPassword.getText().toString())) {
                mFirstPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mSecondPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mCommitView.showError(3000);
                toast(R.string.common_password_input_unlike);
                return;
            }

            // 隐藏软键盘
            hideKeyboard(getCurrentFocus());

            mCommitView.showProgress();

            String phoneNum = mPhoneView.getText().toString();
            String verifyCode = mCodeView.getText().toString();
            String nickName = mNickNameView.getText().toString();
            String password = mFirstPassword.getText().toString();

            User user = new User(phoneNum, StringKt.getLowercaseMd5(password), nickName);

            mUserViewModel.registerAccount(verifyCode, user).observe(this, result -> postDelayed(() -> {
                toast(result.getMessage());
                if (result.isSuccess()) {
                    mCommitView.showSucceed();
                    setResult(RESULT_OK, new Intent()
                            .putExtra(IntentKey.PHONE, mPhoneView.getText().toString())
                            .putExtra(IntentKey.PASSWORD, mFirstPassword.getText().toString()));
                    finish();
                } else {
                    mCommitView.showError(3000);
                }
            }, 1000));
        }
    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 不要把整个布局顶上去
                .keyboardEnable(true);
    }

    /**
     * {@link TextView.OnEditorActionListener}
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE && mCommitView.isEnabled()) {
            // 模拟点击注册按钮
            onClick(mCommitView);
            return true;
        }
        return false;
    }

    @Override
    protected boolean isStatusBarDarkFont() {
        return true;
    }

    /**
     * 注册监听
     */
    public interface OnRegisterListener {

        /**
         * 注册成功
         *
         * @param phone    手机号
         * @param password 密码
         */
        void onSucceed(String phone, String password);

        /**
         * 取消注册
         */
        default void onCancel() {
        }
    }
}