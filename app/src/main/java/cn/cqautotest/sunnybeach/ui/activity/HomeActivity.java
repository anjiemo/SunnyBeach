package cn.cqautotest.sunnybeach.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.SharedElementCallback;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hjq.base.FragmentPagerAdapter;
import com.hjq.gson.factory.GsonFactory;

import java.util.List;
import java.util.Map;

import cn.cqautotest.sunnybeach.R;
import cn.cqautotest.sunnybeach.action.OnBack2TopListener;
import cn.cqautotest.sunnybeach.action.OnDoubleClickListener;
import cn.cqautotest.sunnybeach.app.AppActivity;
import cn.cqautotest.sunnybeach.app.AppApplication;
import cn.cqautotest.sunnybeach.app.AppFragment;
import cn.cqautotest.sunnybeach.manager.ActivityManager;
import cn.cqautotest.sunnybeach.model.AppUpdateInfo;
import cn.cqautotest.sunnybeach.other.AppConfig;
import cn.cqautotest.sunnybeach.other.DoubleClickHelper;
import cn.cqautotest.sunnybeach.other.IntentKey;
import cn.cqautotest.sunnybeach.ui.adapter.NavigationAdapter;
import cn.cqautotest.sunnybeach.ui.dialog.UpdateDialog;
import cn.cqautotest.sunnybeach.ui.fragment.ArticleListFragment;
import cn.cqautotest.sunnybeach.ui.fragment.DiscoverFragment;
import cn.cqautotest.sunnybeach.ui.fragment.FishListFragment;
import cn.cqautotest.sunnybeach.ui.fragment.MyMeFragment;
import cn.cqautotest.sunnybeach.util.FragmentActivityKt;
import cn.cqautotest.sunnybeach.util.KotlinResult;
import cn.cqautotest.sunnybeach.viewmodel.app.AppViewModel;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 首页界面
 */
public final class HomeActivity extends AppActivity implements NavigationAdapter.OnNavigationListener, OnDoubleClickListener {

    private static final String INTENT_KEY_IN_FRAGMENT_INDEX = "fragmentIndex";
    private static final String INTENT_KEY_IN_FRAGMENT_CLASS = "fragmentClass";

    private ViewPager mViewPager;
    private RecyclerView mNavigationView;

    private NavigationAdapter mNavigationAdapter;
    private FragmentPagerAdapter<AppFragment<?>> mPagerAdapter;
    private final AppViewModel mAppViewModel = AppApplication.getAppViewModel();
    private final MutableLiveData<AppUpdateInfo> mAppVersionLiveData = new MutableLiveData<>();

    public static void start(Context context) {
        start(context, MyMeFragment.class);
    }

    public static void start(Context context, Class<? extends AppFragment<?>> fragmentClass) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(IntentKey.INDEX, fragmentClass);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.home_activity;
    }

    @Override
    protected void initView() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mViewPager = findViewById(R.id.vp_home_pager);
        mNavigationView = findViewById(R.id.rv_home_navigation);

        mNavigationAdapter = new NavigationAdapter(this);
        mNavigationAdapter.addItem(new NavigationAdapter.MenuItem(getString(R.string.home_fish_pond_message),
                ContextCompat.getDrawable(this, R.drawable.home_fish_pond_selector)));
        mNavigationAdapter.addItem(new NavigationAdapter.MenuItem(getString(R.string.home_nav_found),
                ContextCompat.getDrawable(this, R.drawable.home_found_selector)));
        mNavigationAdapter.addItem(new NavigationAdapter.MenuItem(getString(R.string.home_nav_index),
                ContextCompat.getDrawable(this, R.drawable.home_home_selector)));
        mNavigationAdapter.addItem(new NavigationAdapter.MenuItem(getString(R.string.home_nav_me),
                ContextCompat.getDrawable(this, R.drawable.home_me_selector)));
        mNavigationAdapter.setOnNavigationListener(this);
        mNavigationView.setAdapter(mNavigationAdapter);
    }

    @Override
    protected void initData() {
        mPagerAdapter = new FragmentPagerAdapter<>(this);
        mPagerAdapter.addFragment(FishListFragment.newInstance());
        mPagerAdapter.addFragment(DiscoverFragment.newInstance());
        mPagerAdapter.addFragment(ArticleListFragment.newInstance());
        // mPagerAdapter.addFragment(new EmptyFragment());
        mPagerAdapter.addFragment(MyMeFragment.newInstance());
        mViewPager.setAdapter(mPagerAdapter);

        onNewIntent(getIntent());

        toast("若发现BUG，可在意见反馈界面中反馈");

        // 检查更新
        mAppViewModel.checkAppUpdate().observe(this, result -> {
            Gson gson = GsonFactory.getSingletonGson();
            String jsonValue = KotlinResult.INSTANCE.toJson(gson, result);
            AppUpdateInfo appUpdateInfo = gson.fromJson(jsonValue, new TypeToken<AppUpdateInfo>() {
            }.getType());
            mAppVersionLiveData.setValue(appUpdateInfo);
        });
    }

    @Override
    public void initEvent() {
        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                mNavigationView.setVisibility(View.GONE);
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mNavigationAdapter.setSelectedPosition(position);
            }
        });
        mNavigationAdapter.setOnDoubleClickListener(this);
    }

    @Override
    public void initObserver() {
        mAppVersionLiveData.observe(this, appUpdateInfo -> {
            // 是否需要强制更新（当前版本低于最低版本，强制更新）
            boolean needForceUpdate = AppConfig.getVersionCode() < appUpdateInfo.minVersionCode;
            if (needForceUpdate) {
                showAppUpdateDialog(appUpdateInfo, true);
                return;
            }
            // 当前版本是否低于最新版本
            if (AppConfig.getVersionCode() < appUpdateInfo.versionCode) {
                showAppUpdateDialog(appUpdateInfo, appUpdateInfo.forceUpdate);
            }
        });
    }

    private void showAppUpdateDialog(AppUpdateInfo appUpdateInfo, boolean forceUpdateApp) {
        new UpdateDialog.Builder(getContext())
                .setFileMd5(appUpdateInfo.apkHash)
                .setDownloadUrl(appUpdateInfo.url)
                .setForceUpdate(forceUpdateApp)
                .setUpdateLog(appUpdateInfo.updateLog)
                .setVersionName(appUpdateInfo.versionName)
                .show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        switchFragment(mPagerAdapter.getFragmentIndex(getSerializable(INTENT_KEY_IN_FRAGMENT_CLASS)));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存当前 Fragment 索引位置
        outState.putInt(INTENT_KEY_IN_FRAGMENT_INDEX, mViewPager.getCurrentItem());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // 恢复当前 Fragment 索引位置
        switchFragment(savedInstanceState.getInt(INTENT_KEY_IN_FRAGMENT_INDEX));
    }

    private void switchFragment(int fragmentIndex) {
        if (fragmentIndex == -1) {
            return;
        }

        switch (fragmentIndex) {
            case 0:
            case 1:
            case 2:
            case 3:
                mViewPager.setCurrentItem(fragmentIndex, false);
                mNavigationAdapter.setSelectedPosition(fragmentIndex);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDoubleClick(@NonNull View v, int position) {
        AppFragment<?> fragment = mPagerAdapter.getItem(position);
        // 如果当前显示的 Fragment 是可以回到顶部的，则调用回到顶部的方法
        if (fragment instanceof OnBack2TopListener) {
            ((OnBack2TopListener) fragment).onBack2Top();
        }
    }

    @Override
    public boolean onNavigationItemSelected(int position) {
        switch (position) {
            case 0:
            case 1:
            case 2:
            case 3:
                mViewPager.setCurrentItem(position, false);
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FragmentActivityKt.checkToken(this, (result, continuation) -> null);
    }

    @Override
    public void onBackPressed() {
        if (!DoubleClickHelper.isOnDoubleClick()) {
            toast(R.string.home_exit_hint);
            return;
        }

        // 移动到上一个任务栈，避免侧滑引起的不良反应
        moveTaskToBack(false);
        postDelayed(() -> {
            // 进行内存优化，销毁掉所有的界面
            ActivityManager.getInstance().finishAllActivities();
            // 销毁进程（注意：调用此 API 可能导致当前 Activity onDestroy 方法无法正常回调）
            // System.exit(0);
        }, 300);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPagerAdapter.getShowFragment().onActivityResult(requestCode, resultCode, data);
        if (mNavigationView.getVisibility() != View.VISIBLE) {
            mNavigationView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager.setAdapter(null);
        mNavigationView.setAdapter(null);
        mNavigationAdapter.setOnNavigationListener(null);
    }

    @Override
    protected boolean isStatusBarDarkFont() {
        return true;
    }
}