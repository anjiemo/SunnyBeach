package cn.cqautotest.sunnybeach.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import cn.cqautotest.sunnybeach.R;
import cn.cqautotest.sunnybeach.aop.DebugLog;
import cn.cqautotest.sunnybeach.aop.SingleClick;
import cn.cqautotest.sunnybeach.app.AppActivity;
import cn.cqautotest.sunnybeach.manager.InputTextManager;
import cn.cqautotest.sunnybeach.model.User;
import cn.cqautotest.sunnybeach.other.IntentKey;
import cn.cqautotest.sunnybeach.ui.dialog.HintDialog;
import cn.cqautotest.sunnybeach.util.StringKt;
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel;

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/05
 * desc   : 修改密码
 */
public final class PasswordModifyActivity extends AppActivity
        implements TextView.OnEditorActionListener {

    @DebugLog
    public static void start(Context context, String phone, String code) {
        Intent intent = new Intent(context, PasswordModifyActivity.class);
        intent.putExtra(IntentKey.PHONE, phone);
        intent.putExtra(IntentKey.CODE, code);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    private UserViewModel mUserViewModel = null;

    private EditText mPhoneView;
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
        return R.layout.password_modify_activity;
    }

    @Override
    protected void initView() {
        mPhoneView = findViewById(R.id.et_password_modify_phone);
        mFirstPassword = findViewById(R.id.et_password_modify_password1);
        mSecondPassword = findViewById(R.id.et_password_modify_password2);
        mCommitView = findViewById(R.id.btn_password_modify_commit);

        setOnClickListener(mCommitView);

        mSecondPassword.setOnEditorActionListener(this);

        InputTextManager.with(this)
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

        mPhoneView.setText(manicured(mPhoneNumber));
    }

    private String manicured(String phoneNum) {
        return phoneNum.replaceAll("(\\d{3})(\\d{4})(\\d{4})", "$1****$2");
    }

    @Override
    public void onClick(View view) {
        onSingleClick(view);
    }

    @SingleClick
    private void onSingleClick(View view) {
        if (view == mCommitView) {
            mCommitView.setEnabled(false);
            if (!mFirstPassword.getText().toString().equals(mSecondPassword.getText().toString())) {
                mFirstPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mSecondPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                toast(R.string.common_password_input_unlike);
                mCommitView.setEnabled(true);
                return;
            }

            // 隐藏软键盘
            hideKeyboard(getCurrentFocus());

            String phone = mPhoneNumber;
            String newPwd = mFirstPassword.getText().toString();
            String smsCode = mVerifyCode;
            User user = new User(phone, StringKt.getLowercaseMd5(newPwd), "");

            // 修改密码
            mUserViewModel.modifyPasswordBySms(smsCode, user).observe(this, result -> {
                mCommitView.setEnabled(true);
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