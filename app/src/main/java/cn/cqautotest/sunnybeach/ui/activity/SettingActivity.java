package cn.cqautotest.sunnybeach.ui.activity;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.blankj.utilcode.util.GsonUtils;
import com.hjq.base.BaseDialog;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.widget.layout.SettingBar;
import com.hjq.widget.view.SwitchButton;

import cn.cqautotest.sunnybeach.R;
import cn.cqautotest.sunnybeach.aop.SingleClick;
import cn.cqautotest.sunnybeach.app.AppActivity;
import cn.cqautotest.sunnybeach.http.glide.GlideApp;
import cn.cqautotest.sunnybeach.http.model.HttpData;
import cn.cqautotest.sunnybeach.http.request.LogoutApi;
import cn.cqautotest.sunnybeach.manager.ActivityManager;
import cn.cqautotest.sunnybeach.manager.CacheDataManager;
import cn.cqautotest.sunnybeach.manager.ThreadPoolManager;
import cn.cqautotest.sunnybeach.model.AppUpdateInfo;
import cn.cqautotest.sunnybeach.other.AppConfig;
import cn.cqautotest.sunnybeach.ui.dialog.MenuDialog;
import cn.cqautotest.sunnybeach.ui.dialog.SafeDialog;
import cn.cqautotest.sunnybeach.ui.dialog.UpdateDialog;
import cn.cqautotest.sunnybeach.util.Constants;
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel;
import cn.cqautotest.sunnybeach.viewmodel.app.AppUpdateState;
import cn.cqautotest.sunnybeach.viewmodel.app.AppViewModel;
import timber.log.Timber;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2019/03/01
 * desc   : 设置界面
 */
public final class SettingActivity extends AppActivity
        implements SwitchButton.OnCheckedChangeListener {

    private SettingBar mLanguageView;
    private SettingBar mPhoneView;
    private SettingBar mPasswordView;
    private SettingBar mCleanCacheView;
    private SwitchButton mAutoSwitchView;
    private TextView mSettingUpdate;
    private AppViewModel mAppViewModel = null;
    private UserViewModel mUserViewModel = null;
    private boolean isShowing = false;

    @Override
    protected int getLayoutId() {
        return R.layout.setting_activity;
    }

    @Override
    public void initObserver() {
        mAppViewModel.getAppUpdateState().observe(this, appUpdateState -> {
            checkAppUpdate(appUpdateState, true);
        });
    }

    @Override
    protected void initView() {
        mLanguageView = findViewById(R.id.sb_setting_language);
        mPhoneView = findViewById(R.id.sb_setting_phone);
        mPasswordView = findViewById(R.id.sb_setting_password);
        mCleanCacheView = findViewById(R.id.sb_setting_cache);
        mAutoSwitchView = findViewById(R.id.sb_setting_switch);
        mSettingUpdate = findViewById(R.id.tv_setting_update);

        // 设置切换按钮的监听
        mAutoSwitchView.setOnCheckedChangeListener(this);

        setOnClickListener(R.id.sb_setting_language, R.id.sb_setting_update, R.id.sb_setting_phone,
                R.id.sb_setting_password, R.id.sb_setting_agreement, R.id.sb_setting_about,
                R.id.sb_setting_cache, R.id.sb_setting_auto, R.id.sb_setting_exit);
    }

    @Override
    protected void initData() {
        mAppViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(AppViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        // 检查APP版本更新
        mAppViewModel.checkAppVersionUpdate(Constants.APP_INFO_URL);

        // 获取应用缓存大小
        mCleanCacheView.setRightText(CacheDataManager.getTotalCacheSize(this));

        mLanguageView.setRightText("简体中文");
        mPhoneView.setRightText("181****1413");
        mPasswordView.setRightText("密码强度较低");
        mAutoSwitchView.setChecked(mUserViewModel.isAutoLogin());
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.sb_setting_language) {

            // 底部选择框
            new MenuDialog.Builder(this)
                    // 设置点击按钮后不关闭对话框
                    //.setAutoDismiss(false)
                    .setList(R.string.setting_language_simple, R.string.setting_language_complex)
                    .setListener((MenuDialog.OnListener<String>) (dialog, position, string) -> {
                        mLanguageView.setRightText(string);
                        BrowserActivity.start(getActivity(), "https://github.com/getActivity/MultiLanguages");
                    })
                    .setGravity(Gravity.BOTTOM)
                    .setAnimStyle(BaseDialog.ANIM_BOTTOM)
                    .show();

        } else if (viewId == R.id.sb_setting_update) {
            AppUpdateState appUpdateState = mAppViewModel.getAppUpdateState().getValue();
            checkAppUpdate(appUpdateState, false);

        } else if (viewId == R.id.sb_setting_phone) {

            new SafeDialog.Builder(this)
                    .setListener((dialog, phone, code) -> PhoneResetActivity.start(getActivity(), code))
                    .show();

        } else if (viewId == R.id.sb_setting_password) {

            new SafeDialog.Builder(this)
                    .setListener((dialog, phone, code) -> PasswordResetActivity.start(getActivity(), phone, code))
                    .show();

        } else if (viewId == R.id.sb_setting_agreement) {

            BrowserActivity.start(this, "https://github.com/anjiemo/SunnyBeach");

        } else if (viewId == R.id.sb_setting_about) {

            startActivity(AboutActivity.class);

        } else if (viewId == R.id.sb_setting_auto) {

            // 自动登录
            mAutoSwitchView.setChecked(!mAutoSwitchView.isChecked());

        } else if (viewId == R.id.sb_setting_cache) {

            // 清除内存缓存（必须在主线程）
            GlideApp.get(getActivity()).clearMemory();
            ThreadPoolManager.getInstance().execute(() -> {
                CacheDataManager.clearAllCache(this);
                // 清除本地缓存（必须在子线程）
                GlideApp.get(getActivity()).clearDiskCache();
                post(() -> {
                    // 重新获取应用缓存大小
                    mCleanCacheView.setRightText(CacheDataManager.getTotalCacheSize(getActivity()));
                });
            });

        } else if (viewId == R.id.sb_setting_exit) {

            if (true) {
                LoginActivity.start(this, "", "");
                // 进行内存优化，销毁除登录页之外的所有界面
                ActivityManager.getInstance().finishAllActivities(LoginActivity.class);
                // 清除用户基本信息数据
                mUserViewModel.logoutUserAccount();
                return;
            }

            // 退出登录
            EasyHttp.post(this)
                    .api(new LogoutApi())
                    .request(new HttpCallback<HttpData<Void>>(this) {

                        @Override
                        public void onSucceed(HttpData<Void> data) {
                            startActivity(LoginActivity.class);
                            // 进行内存优化，销毁除登录页之外的所有界面
                            ActivityManager.getInstance().finishAllActivities(LoginActivity.class);
                        }
                    });

        }
    }

    /**
     * 检查App版本更新
     *
     * @param appUpdateState app的更新状态
     */
    private void checkAppUpdate(AppUpdateState appUpdateState, boolean isAutoCheck) {
        Timber.d("===>%s", GsonUtils.toJson(appUpdateState));
        if (appUpdateState != null) {
            if (!appUpdateState.isDataValid) {
                if (appUpdateState.networkError != null) {
                    hideUpdateIcon();
                    toast(getString(appUpdateState.networkError));
                    return;
                }
                if (appUpdateState.checkUpdateError != null) {
                    hideUpdateIcon();
                    toast(getString(appUpdateState.checkUpdateError));
                    return;
                }
            }
            AppUpdateInfo appUpdateInfo = appUpdateState.appUpdateInfo;
            // 本地的版本码和服务器的进行比较
            if (appUpdateInfo != null && appUpdateInfo.versionCode > AppConfig.getVersionCode()) {
                showAppUpdateDialog(appUpdateInfo);
            } else {
                hideUpdateIcon();
                // 非自动检查时才提示
                if (!isAutoCheck) {
                    toast(R.string.update_no_update);
                }
            }
        }
    }

    private void showAppUpdateDialog(AppUpdateInfo appUpdateInfo) {
        showUpdateIcon();
        // 避免显示多个更新对话框
        if (!isShowing) {
            new UpdateDialog.Builder(this)
                    .setVersionName(appUpdateInfo.versionName)
                    .setForceUpdate(appUpdateInfo.forceUpdate)
                    .setUpdateLog(appUpdateInfo.updateLog)
                    .setDownloadUrl(appUpdateInfo.url)
                    .setFileMd5(appUpdateInfo.apkHash)
                    .show();
            isShowing = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isShowing = false;
    }

    /**
     * 隐藏更新提示图标
     */
    private void hideUpdateIcon() {
        mSettingUpdate.setVisibility(View.GONE);
    }

    /**
     * 显示更新提示图标
     */
    private void showUpdateIcon() {
        mSettingUpdate.setVisibility(View.VISIBLE);
    }

    /**
     * {@link SwitchButton.OnCheckedChangeListener}
     */

    @Override
    public void onCheckedChanged(SwitchButton button, boolean checked) {
        // 设置是否自动登录
        mUserViewModel.setupAutoLogin(checked);
        toast("此功能快马加鞭实现中...");
    }
}