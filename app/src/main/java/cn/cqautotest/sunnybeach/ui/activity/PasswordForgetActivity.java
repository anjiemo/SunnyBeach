package cn.cqautotest.sunnybeach.ui.activity;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hjq.widget.view.CountdownView;

import cn.cqautotest.sunnybeach.R;
import cn.cqautotest.sunnybeach.aop.SingleClick;
import cn.cqautotest.sunnybeach.app.AppActivity;
import cn.cqautotest.sunnybeach.http.glide.GlideApp;
import cn.cqautotest.sunnybeach.manager.InputTextManager;
import cn.cqautotest.sunnybeach.model.SmsInfo;
import cn.cqautotest.sunnybeach.util.Constants;
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2019/02/27
 * desc   : 忘记密码
 */
public final class PasswordForgetActivity extends AppActivity
        implements TextView.OnEditorActionListener {

    private UserViewModel mUserViewModel = null;

    private EditText mInputVerifyCodeView;
    private ImageView mVerifyCodeView;
    private EditText mPhoneView;
    private EditText mCodeView;
    private CountdownView mCountdownView;
    private Button mCommitView;

    @Override
    protected int getLayoutId() {
        return R.layout.password_forget_activity;
    }

    @Override
    protected void initView() {
        mInputVerifyCodeView = findViewById(R.id.et_password_forget_verify_code);
        mVerifyCodeView = findViewById(R.id.siv_password_forget_verify_code);
        mPhoneView = findViewById(R.id.et_password_forget_phone);
        mCodeView = findViewById(R.id.et_password_forget_code);
        mCountdownView = findViewById(R.id.cv_password_forget_countdown);
        mCommitView = findViewById(R.id.btn_password_forget_commit);

        setOnClickListener(mVerifyCodeView, mCountdownView, mCommitView);

        mCodeView.setOnEditorActionListener(this);

        InputTextManager.with(this)
                .addView(mInputVerifyCodeView)
                .addView(mPhoneView)
                .addView(mCodeView)
                .setMain(mCommitView)
                .build();
    }

    @Override
    protected void initData() {
        mUserViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication())
                .create(UserViewModel.class);
        loadVerifyCode();
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

            // 隐藏软键盘
            hideKeyboard(getCurrentFocus());

            SmsInfo smsInfo = new SmsInfo(mPhoneView.getText().toString(), mInputVerifyCodeView.getText().toString());

            // 发送验证码
            mUserViewModel.sendForgetSmsVerifyCode(smsInfo).observe(this, result -> {
                toast(result.getMessage());
                mCountdownView.start();
            });
        } else if (view == mCommitView) {
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

            if (mCodeView.getText().toString().length() != getResources().getInteger(R.integer.sms_code_length)) {
                mCodeView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                toast(R.string.common_code_error_hint);
                return;
            }

            String phoneNumber = mPhoneView.getText().toString();
            String smsCode = mCodeView.getText().toString();

            // 验证码校验
            mUserViewModel.checkSmsCode(phoneNumber, smsCode).observe(this, result -> {
                toast(result.getMessage());
                if (!result.isSuccess()) {
                    PasswordResetActivity.start(getContext(), phoneNumber, smsCode);
                    finish();
                }
            });
        }
    }

    /**
     * {@link TextView.OnEditorActionListener}
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE && mCommitView.isEnabled()) {
            // 模拟点击下一步按钮
            onClick(mCommitView);
            return true;
        }
        return false;
    }
}