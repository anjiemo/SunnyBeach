package cn.cqautotest.sunnybeach.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.List;

import cn.cqautotest.sunnybeach.R;
import cn.cqautotest.sunnybeach.action.StatusAction;
import cn.cqautotest.sunnybeach.aop.CheckNet;
import cn.cqautotest.sunnybeach.aop.DebugLog;
import cn.cqautotest.sunnybeach.app.AppActivity;
import cn.cqautotest.sunnybeach.app.AppApplication;
import cn.cqautotest.sunnybeach.db.CookieRoomDatabase;
import cn.cqautotest.sunnybeach.db.dao.CookieDao;
import cn.cqautotest.sunnybeach.manager.CookieStore;
import cn.cqautotest.sunnybeach.manager.ThreadPoolManager;
import cn.cqautotest.sunnybeach.other.FitScreen;
import cn.cqautotest.sunnybeach.other.IntentKey;
import cn.cqautotest.sunnybeach.util.Constants;
import cn.cqautotest.sunnybeach.util.StringUtil;
import cn.cqautotest.sunnybeach.widget.BrowserView;
import cn.cqautotest.sunnybeach.widget.StatusLayout;
import okhttp3.Cookie;
import timber.log.Timber;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 浏览器界面
 */
public final class BrowserActivity extends AppActivity
        implements StatusAction, OnRefreshListener {

    @CheckNet
    @DebugLog
    public static void start(Context context, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtra(IntentKey.URL, url);
        // 加载非反馈界面
        intent.putExtra(IntentKey.OTHER, false);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @CheckNet
    @DebugLog
    public static void start(Context context, String url, String openId, String nickName, String avatar) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtra(IntentKey.URL, url);
        intent.putExtra(IntentKey.ID, openId);
        intent.putExtra(IntentKey.NAME, nickName);
        intent.putExtra(IntentKey.AVATAR_URL, avatar);
        // 加载反馈界面
        intent.putExtra(IntentKey.OTHER, true);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    private StatusLayout mStatusLayout;
    private ProgressBar mProgressBar;
    private SmartRefreshLayout mRefreshLayout;
    private BrowserView mBrowserView;

    @Override
    protected int getLayoutId() {
        return R.layout.browser_activity;
    }

    @Override
    protected void initView() {
        mStatusLayout = findViewById(R.id.hl_browser_hint);
        mProgressBar = findViewById(R.id.pb_browser_progress);
        mRefreshLayout = findViewById(R.id.sl_browser_refresh);
        mBrowserView = findViewById(R.id.wv_browser_view);

        // 设置 WebView 生命管控
        mBrowserView.setLifecycleOwner(this);
        // 设置网页刷新监听
        mRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void initData() {
        showLoading();

        mBrowserView.setBrowserViewClient(new MyBrowserViewClient());
        mBrowserView.setBrowserChromeClient(new MyBrowserChromeClient(mBrowserView));
        boolean feedback = getBoolean(IntentKey.OTHER);
        if (feedback) {
            String openId = getString(IntentKey.ID);
            String nickName = getString(IntentKey.NAME);
            String avatar = getString(IntentKey.AVATAR_URL);
            Timber.d("initData：===> openId is " + openId + " nickName is " + nickName + " avatar is " + avatar);
            mBrowserView.postUrl(getString(IntentKey.URL), ("nickName=" +
                    nickName +
                    "&avatar=" +
                    avatar +
                    "&openid=" +
                    openId).getBytes());
        } else {
            mBrowserView.loadUrl(getString(IntentKey.URL));
        }
    }

    @Override
    public StatusLayout getStatusLayout() {
        return mStatusLayout;
    }

    @Override
    public void onLeftClick(View view) {
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mBrowserView.canGoBack()) {
            // 后退网页并且拦截该事件
            mBrowserView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 重新加载当前页
     */
    @CheckNet
    private void reload() {
        mBrowserView.reload();
    }

    /**
     * {@link OnRefreshListener}
     */

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        reload();
    }

    @Override
    protected boolean isStatusBarDarkFont() {
        return false;
    }

    private class MyBrowserViewClient extends BrowserView.BrowserViewClient {

        private final CookieRoomDatabase mDatabase = AppApplication.getDatabase();
        private final CookieDao mCookieDao = mDatabase.cookieDao();

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            ThreadPoolManager manager = ThreadPoolManager.getInstance();
            manager.execute(() -> {
                String domain = StringUtil.getTopDomain(Constants.SUNNY_BEACH_BASE_URL);
                Timber.d("===> domain is $domain");
                CookieManager cookieManager = CookieManager.getInstance();
                CookieStore cookieStore = mCookieDao.getCookiesByDomain(domain);
                if (cookieStore != null) {
                    List<Cookie> cookieStoreList = cookieStore.getCookies();
                    for (Cookie cookie : cookieStoreList) {
                        String cookieName = cookie.name();
                        String cookieValue = cookie.value();
                        String cookieDomain = cookie.domain();
                        String cookieStr = cookieName + "=" + cookieValue + "; path=/; domain=." + cookieDomain;
                        Timber.d("===> Set-Cookie is %s", cookieStr);
                        cookieManager.setCookie(url, cookieStr);
                    }
                }
                String newCookie = cookieManager.getCookie(url);
                if (newCookie != null) {
                    Timber.d("===> newCookie is %s", newCookie);
                }
                Timber.d("===> CookieManager is finish");
            });
            return super.shouldOverrideUrlLoading(view, url);
        }

        /**
         * 网页加载错误时回调，这个方法会在 onPageFinished 之前调用
         */
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            // 这里为什么要用延迟呢？因为加载出错之后会先调用 onReceivedError 再调用 onPageFinished
            post(() -> showError(v -> reload()));
        }

        /**
         * 开始加载网页
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        /**
         * 完成加载网页
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            mProgressBar.setVisibility(View.GONE);
            if (url.contains(Constants.SUNNY_BEACH_ARTICLE_URL_PRE)) {
                FitScreen fitScreen = new FitScreen(view);
                fitScreen.run();
            }
            mRefreshLayout.finishRefresh();
            showComplete();
        }
    }

    private class MyBrowserChromeClient extends BrowserView.BrowserChromeClient {

        private MyBrowserChromeClient(BrowserView view) {
            super(view);
        }

        /**
         * 收到网页标题
         */
        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (title != null) {
                setTitle(title);
            }
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            if (icon != null) {
                setRightIcon(new BitmapDrawable(getResources(), icon));
            }
        }

        /**
         * 收到加载进度变化
         */
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            mProgressBar.setProgress(newProgress);
        }
    }
}