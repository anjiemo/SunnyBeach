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

import com.hjq.umeng.Platform;
import com.hjq.umeng.UmengShare;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.util.List;

import cn.cqautotest.sunnybeach.R;
import cn.cqautotest.sunnybeach.action.StatusAction;
import cn.cqautotest.sunnybeach.aop.CheckNet;
import cn.cqautotest.sunnybeach.aop.DebugLog;
import cn.cqautotest.sunnybeach.app.AppActivity;
import cn.cqautotest.sunnybeach.app.AppApplication;
import cn.cqautotest.sunnybeach.db.CookieRoomDatabase;
import cn.cqautotest.sunnybeach.db.SobCacheManager;
import cn.cqautotest.sunnybeach.db.dao.CookieDao;
import cn.cqautotest.sunnybeach.manager.CookieStore;
import cn.cqautotest.sunnybeach.manager.ThreadPoolManager;
import cn.cqautotest.sunnybeach.other.FitScreen;
import cn.cqautotest.sunnybeach.other.IntentKey;
import cn.cqautotest.sunnybeach.ui.dialog.ShareDialog;
import cn.cqautotest.sunnybeach.util.Constants;
import cn.cqautotest.sunnybeach.util.StringUtil;
import cn.cqautotest.sunnybeach.widget.BrowserView;
import cn.cqautotest.sunnybeach.widget.StatusLayout;
import okhttp3.Cookie;
import timber.log.Timber;

/**
 * author : Android 轮子哥 & A Lonely Cat
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 浏览器界面
 */
public final class BrowserActivity extends AppActivity implements StatusAction, OnRefreshListener {

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

        mBrowserView.setBrowserViewClient(new AppBrowserViewClient());
        mBrowserView.setBrowserChromeClient(new AppBrowserChromeClient(mBrowserView));
        boolean feedback = getBoolean(IntentKey.OTHER);
        if (feedback) {
            String openId = getString(IntentKey.ID);
            String nickName = getString(IntentKey.NAME);
            String avatar = getString(IntentKey.AVATAR_URL);
            Timber.d("initData：===> openId is " + openId + " nickName is " + nickName + " avatar is " + avatar);
            String postData = "nickname=" + nickName + "&avatar=" + avatar + "&openid=" + openId;
            mBrowserView.postUrl(getString(IntentKey.URL), postData.getBytes());
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
    public void onRightClick(View view) {
        UMWeb content = new UMWeb(mBrowserView.getUrl());
        content.setTitle(mBrowserView.getTitle());
        content.setThumb(new UMImage(this, R.mipmap.launcher_ic));
        content.setDescription(getString(R.string.app_name));
        // 分享
        new ShareDialog.Builder(this)
                .setShareLink(content)
                .setListener(new UmengShare.OnShareListener() {

                    @Override
                    public void onSucceed(Platform platform) {
                        toast("分享成功");
                    }

                    @Override
                    public void onError(Platform platform, Throwable t) {
                        toast(t.getMessage());
                    }

                    @Override
                    public void onCancel(Platform platform) {
                        toast("分享取消");
                    }
                })
                .show();
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

    private class AppBrowserViewClient extends BrowserView.BrowserViewClient {

        private final CookieRoomDatabase mDatabase = AppApplication.getDatabase();
        private final CookieDao mCookieDao = mDatabase.cookieDao();

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
            injectCookie(url);
        }

        private void injectCookie(String url) {
            String domain = StringUtil.getTopDomain(Constants.SUNNY_BEACH_API_BASE_URL);
            ThreadPoolManager manager = ThreadPoolManager.getInstance();
            manager.execute(() -> {
                Timber.d("hookUrlLoad：===> domain is %s", domain);
                CookieManager cookieManager = CookieManager.getInstance();
                CookieStore cookieStore = mCookieDao.getCookiesByDomain(domain);
                if (cookieStore != null) {
                    List<Cookie> cookieStoreList = cookieStore.getCookies();
                    for (Cookie cookie : cookieStoreList) {
                        String cookieName = cookie.name();
                        String cookieValue = cookie.value();
                        String cookieDomain = cookie.domain();
                        String cookieStr = new Cookie.Builder()
                                .name(cookieName)
                                .value(cookieValue)
                                .domain(cookieDomain)
                                .path("/")
                                .build()
                                .toString();
                        Timber.d("hookUrlLoad：===> Set-Cookie is %s", cookieStr);
                        cookieManager.setCookie(url, cookieStr);
                    }
                }
                String newCookie = cookieManager.getCookie(url);
                if (newCookie != null) {
                    Timber.d("hookUrlLoad：===> newCookie is %s", newCookie);
                }
                String currUrlTopDomain = StringUtil.getTopDomain(url);
                String apiTopDomain = StringUtil.getTopDomain(Constants.SUNNY_BEACH_API_BASE_URL);
                String siteTopDomain = StringUtil.getTopDomain(Constants.SUNNY_BEACH_SITE_BASE_URL);
                if (currUrlTopDomain.equals(apiTopDomain) || currUrlTopDomain.equals(siteTopDomain)) {
                    String cookieName = SobCacheManager.SOB_TOKEN_NAME;
                    String cookieValue = SobCacheManager.INSTANCE.getSobToken();
                    String apiCookie = new Cookie.Builder()
                            .name(cookieName)
                            .value(cookieValue)
                            .domain(apiTopDomain)
                            .path("/")
                            .build()
                            .toString();
                    String siteCookie = new Cookie.Builder()
                            .name(cookieName)
                            .value(cookieValue)
                            .domain(siteTopDomain)
                            .path("/")
                            .build()
                            .toString();
                    Timber.d("===> Set-Cookie：apiCookie is %s", apiCookie);
                    Timber.d("===> Set-Cookie：siteCookie is %s", siteCookie);
                    cookieManager.setCookie(url, apiCookie);
                    cookieManager.setCookie(url, siteCookie);
                }
                Timber.d("===> CookieManager is finish");
            });
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

    private class AppBrowserChromeClient extends BrowserView.BrowserChromeClient {

        private AppBrowserChromeClient(BrowserView view) {
            super(view);
        }

        /**
         * 收到网页标题
         */
        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (title == null) {
                return;
            }
            setTitle(title);
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            if (icon == null) {
                return;
            }
            setRightIcon(new BitmapDrawable(getResources(), icon));
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