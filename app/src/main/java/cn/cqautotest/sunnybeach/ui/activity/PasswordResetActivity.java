package cn.cqautotest.sunnybeach.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import cn.cqautotest.sunnybeach.R;
import cn.cqautotest.sunnybeach.aop.DebugLog;
import cn.cqautotest.sunnybeach.aop.SingleClick;
import cn.cqautotest.sunnybeach.app.AppActivity;
import cn.cqautotest.sunnybeach.http.glide.GlideApp;
import cn.cqautotest.sunnybeach.manager.InputTextManager;
import cn.cqautotest.sunnybeach.model.ModifyPwd;
import cn.cqautotest.sunnybeach.other.IntentKey;
import cn.cqautotest.sunnybeach.ui.dialog.HintDialog;
import cn.cqautotest.sunnybeach.util.Constants;
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2019/02/27
 * desc   : 重置密码
 */
public final class PasswordResetActivity extends AppActivity
        implements TextView.OnEditorActionListener {

    @DebugLog
    public static void start(Context context, String phone, String code) {
        Intent intent = new Intent(context, PasswordResetActivity.class);
        intent.putExtra(IntentKey.PHONE, phone);
        intent.putExtra(IntentKey.CODE, code);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    private UserViewModel mUserViewModel = null;

    private EditText mInputResetVerifyCode;
    private ImageView mResetVerifyCode;
    private EditText mOldPassword;
    private EditText mFirstPassword;
    private EditText mSecondPassword;
    private Button mCommitView;

    /**
     * 手机号
     */
    private String mPhoneNumber;
    /**
     * 验证码
     */
    private String mVerifyCode;

    @Override
    protected int getLayoutId() {
        return R.layout.password_reset_activity;
    }

    @Override
    protected void initView() {
        mInputResetVerifyCode = findViewById(R.id.et_password_reset_verify_code);
        mResetVerifyCode = findViewById(R.id.siv_password_reset_verify_code);
        mOldPassword = findViewById(R.id.et_password_reset_old_password);
        mFirstPassword = findViewById(R.id.et_password_reset_password1);
        mSecondPassword = findViewById(R.id.et_password_reset_password2);
        mCommitView = findViewById(R.id.btn_password_reset_commit);

        setOnClickListener(mResetVerifyCode, mCommitView);

        mSecondPassword.setOnEditorActionListener(this);

        InputTextManager.with(this)
                .addView(mInputResetVerifyCode)
                .addView(mOldPassword)
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
        mPhoneNumber = getString(IntentKey.PHONE);
        mVerifyCode = getString(IntentKey.CODE);
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
                .into(mResetVerifyCode);
    }

    @Override
    public void onClick(View view) {
        if (view == mResetVerifyCode) {
            loadVerifyCode();
            return;
        }
        onSingleClick(view);
    }

    @SingleClick
    private void onSingleClick(View view) {
        if (view == mCommitView) {
            if (TextUtils.isEmpty(mOldPassword.getText())) {
                mOldPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                toast(R.string.password_reset_phone_old_password_hint);
                return;
            }

            if (!mFirstPassword.getText().toString().equals(mSecondPassword.getText().toString())) {
                mFirstPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mSecondPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                toast(R.string.common_password_input_unlike);
                return;
            }

            // 隐藏软键盘
            hideKeyboard(getCurrentFocus());

            String oldPwd = mOldPassword.getText().toString();
            String newPwd = mFirstPassword.getText().toString();
            String captcha = mInputResetVerifyCode.getText().toString();

            ModifyPwd modifyPwd = new ModifyPwd(oldPwd, newPwd, captcha);

            // 修改密码
            mUserViewModel.modifyPassword(modifyPwd).observe(this, result -> {
                if (result.isSuccess()) {
                    new HintDialog.Builder(getContext())
                            .setIcon(HintDialog.ICON_FINISH)
                            .setMessage(R.string.password_reset_success)
                            .setDuration(2000)
                            .addOnDismissListener(dialog -> finish())
                            .show();
                } else {
                    toast(result.getMessage());
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
            // 模拟点击提交按钮
            onClick(mCommitView);
            return true;
        }
        return false;
    }
}