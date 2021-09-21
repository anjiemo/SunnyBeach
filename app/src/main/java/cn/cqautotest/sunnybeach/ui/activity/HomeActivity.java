package cn.cqautotest.sunnybeach.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hjq.base.FragmentPagerAdapter;

import cn.cqautotest.sunnybeach.R;
import cn.cqautotest.sunnybeach.app.AppActivity;
import cn.cqautotest.sunnybeach.app.AppFragment;
import cn.cqautotest.sunnybeach.manager.ActivityManager;
import cn.cqautotest.sunnybeach.other.DoubleClickHelper;
import cn.cqautotest.sunnybeach.other.IntentKey;
import cn.cqautotest.sunnybeach.ui.fragment.ArticleListFragment;
import cn.cqautotest.sunnybeach.ui.fragment.DiscoverFragment;
import cn.cqautotest.sunnybeach.ui.fragment.FishListFragment;
import cn.cqautotest.sunnybeach.ui.fragment.HomeFragment;
import cn.cqautotest.sunnybeach.ui.fragment.MyMeFragment;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 首页界面
 */
public final class HomeActivity extends AppActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    private ViewPager mViewPager;
    private BottomNavigationView mBottomNavigationView;

    private FragmentPagerAdapter<AppFragment<?>> mPagerAdapter;

    public static void start(Context context) {
        start(context, HomeFragment.class);
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
        mViewPager = findViewById(R.id.vp_home_pager);
        mBottomNavigationView = findViewById(R.id.bv_home_navigation);

        // 不使用图标默认变色
        mBottomNavigationView.setItemIconTintList(null);
        // 设置导航栏条目点击事件
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);

        // 屏蔽底部导航栏长按文本提示
        Menu menu = mBottomNavigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            mBottomNavigationView.findViewById(menu.getItem(i).getItemId()).setOnLongClickListener(v -> true);
        }
    }

    @Override
    protected void initData() {
        mPagerAdapter = new FragmentPagerAdapter<>(this);
        mPagerAdapter.addFragment(ArticleListFragment.newInstance());
        mPagerAdapter.addFragment(DiscoverFragment.newInstance());
        mPagerAdapter.addFragment(FishListFragment.newInstance());
        // mPagerAdapter.addFragment(EmptyFragment.newInstance());
        mPagerAdapter.addFragment(MyMeFragment.newInstance());
        mViewPager.setAdapter(mPagerAdapter);

        onNewIntent(getIntent());

        toast("若发现BUG，可在意见反馈界面中反馈");
    }

    @Override
    public void initEvent() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                MenuItem menuItem = mBottomNavigationView.getMenu().getItem(position);
                mBottomNavigationView.setSelectedItemId(menuItem.getItemId());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int fragmentIndex = mPagerAdapter.getFragmentIndex(getSerializable(IntentKey.INDEX));
        if (fragmentIndex == -1) {
            return;
        }

        mViewPager.setCurrentItem(fragmentIndex);
        switch (fragmentIndex) {
            case 0:
                mBottomNavigationView.setSelectedItemId(R.id.menu_home);
                break;
            case 1:
                mBottomNavigationView.setSelectedItemId(R.id.home_found);
                break;
            case 2:
                mBottomNavigationView.setSelectedItemId(R.id.home_fish_pond);
                break;
            case 3:
                mBottomNavigationView.setSelectedItemId(R.id.home_message);
                break;
            case 4:
                mBottomNavigationView.setSelectedItemId(R.id.home_me);
                break;
            default:
                break;
        }
    }

    /**
     * {@link BottomNavigationView.OnNavigationItemSelectedListener}
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_home) {
            mViewPager.setCurrentItem(0, false);
            return true;
        } else if (itemId == R.id.home_found) {
            mViewPager.setCurrentItem(1, false);
            return true;
        } else if (itemId == R.id.home_fish_pond) {
            mViewPager.setCurrentItem(2, false);
            return true;
            // } else if (itemId == R.id.home_message) {
            //     mViewPager.setCurrentItem(3, false);
            //     toast("该界面正在装修中...");
            //     return true;
        } else if (itemId == R.id.home_me) {
            mViewPager.setCurrentItem(4, false);
            return true;
        }
        return false;
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
    protected void onDestroy() {
        super.onDestroy();
        mViewPager.setAdapter(null);
        mBottomNavigationView.setOnNavigationItemSelectedListener(null);
    }

    @Override
    protected boolean isStatusBarDarkFont() {
        return true;
    }
}