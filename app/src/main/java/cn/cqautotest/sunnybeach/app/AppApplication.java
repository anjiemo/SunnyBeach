package cn.cqautotest.sunnybeach.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.hjq.bar.TitleBar;
import com.hjq.bar.initializer.TransparentBarInitializer;
import com.hjq.http.EasyConfig;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;
import com.hjq.toast.style.ToastBlackStyle;
import com.hjq.umeng.UmengClient;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mmkv.MMKV;

import java.io.InputStream;

import cn.cqautotest.sunnybeach.R;
import cn.cqautotest.sunnybeach.aop.DebugLog;
import cn.cqautotest.sunnybeach.db.CookieRoomDatabase;
import cn.cqautotest.sunnybeach.http.ServiceCreator;
import cn.cqautotest.sunnybeach.http.glide.GlideApp;
import cn.cqautotest.sunnybeach.http.model.RequestHandler;
import cn.cqautotest.sunnybeach.http.model.RequestServer;
import cn.cqautotest.sunnybeach.manager.ActivityManager;
import cn.cqautotest.sunnybeach.other.AppConfig;
import cn.cqautotest.sunnybeach.other.CrashHandler;
import cn.cqautotest.sunnybeach.other.DebugLoggerTree;
import cn.cqautotest.sunnybeach.other.SmartBallPulseFooter;
import cn.cqautotest.sunnybeach.other.ToastInterceptor;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import timber.log.Timber;

/**
 * author : Android 轮子哥 & A Lonely Cat
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 应用入口
 */
public final class AppApplication extends Application {

    private static AppApplication INSTANCE;
    private static CookieRoomDatabase sDatabase;

    @DebugLog("启动耗时")
    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        initSdk(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        // 清理所有图片内存缓存
        GlideApp.get(this).onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        // 根据手机内存剩余情况清理图片内存缓存
        GlideApp.get(this).onTrimMemory(level);
    }

    public static AppApplication getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化一些第三方框架
     */
    public static void initSdk(Application application) {
        // 设置调试模式
        XXPermissions.setDebugMode(AppConfig.isDebug());

        // 初始化吐司
        ToastUtils.init(application, new ToastBlackStyle(application) {

            @Override
            public int getCornerRadius() {
                return (int) application.getResources().getDimension(R.dimen.button_round_size);
            }
        });

        // 设置 Toast 拦截器
        ToastUtils.setToastInterceptor(new ToastInterceptor());

        // 设置标题栏初始化器
        TitleBar.setDefaultInitializer(new TransparentBarInitializer() {
            @Override
            public Drawable getBackgroundDrawable(Context context) {
                return getDrawableResources(context, R.drawable.shape_gradient);
            }
        });

        // 本地异常捕捉
        CrashHandler.register(application);

        // 友盟统计、登录、分享 SDK
        UmengClient.init(application);

        // Bugly 异常捕捉
        CrashReport.initCrashReport(application, AppConfig.getBuglyId(), AppConfig.isDebug());

        // 设置全局的 Header 构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) ->
                new MaterialHeader(context).setColorSchemeColors(ContextCompat.getColor(context, R.color.common_accent_color)));
        // 设置全局的 Footer 构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> new SmartBallPulseFooter(context));
        // 设置全局初始化器
        SmartRefreshLayout.setDefaultRefreshInitializer((context, layout) -> {
            // 刷新头部是否跟随内容偏移
            layout.setEnableHeaderTranslationContent(true)
                    // 刷新尾部是否跟随内容偏移
                    .setEnableFooterTranslationContent(true)
                    // 加载更多是否跟随内容偏移
                    .setEnableFooterFollowWhenNoMoreData(true)
                    // 内容不满一页时是否可以上拉加载更多
                    .setEnableLoadMoreWhenContentNotFull(false)
                    // 仿苹果越界效果开关
                    .setEnableOverScrollDrag(false);
        });

        // Activity 栈管理初始化
        ActivityManager.getInstance().init(application);

        // 网络请求框架初始化
        OkHttpClient okHttpClient = ServiceCreator.INSTANCE.getClient();

        EasyConfig.with(okHttpClient)
                // 是否打印日志
                .setLogEnabled(AppConfig.isLogEnable())
                // 设置服务器配置
                .setServer(new RequestServer())
                // 设置请求处理策略
                .setHandler(new RequestHandler(application))
                // 设置请求重试次数
                .setRetryCount(1)
                // 添加全局请求参数
                //.addParam("token", "6666666")
                // 添加全局请求头
                //.addHeader("time", "20191030")
                // 启用配置
                .into();

        // 初始化日志打印
        if (AppConfig.isLogEnable()) {
            Timber.plant(new DebugLoggerTree());
        }

        // 注册网络状态变化监听
        ConnectivityManager connectivityManager = ContextCompat.getSystemService(application, ConnectivityManager.class);
        if (connectivityManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
                @Override
                public void onLost(@NonNull Network network) {
                    Activity topActivity = ActivityManager.getInstance().getTopActivity();
                    if (topActivity instanceof LifecycleOwner) {
                        LifecycleOwner lifecycleOwner = ((LifecycleOwner) topActivity);
                        if (lifecycleOwner.getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                            ToastUtils.show(R.string.common_network_error);
                        }
                    }
                }
            });
        }

        // 初始化 Room 数据库
        sDatabase = CookieRoomDatabase.getDatabase(application);
        // MMKV初始化
        MMKV.initialize(application);
        // 初始化 Glide 的 Cookie 管理
        Glide.get(application)
                .getRegistry()
                .replace(
                        GlideUrl.class,
                        InputStream.class,
                        new OkHttpUrlLoader.Factory(castOrNull(okHttpClient))
                );
        // UMConfigure.setLogEnabled(AppConfig.isDebug())
        // // 客户端用户同意隐私政策后，正式初始化友盟+SDK
        // UMConfigure.init(
        //     getApplication(),
        //     "60c8883fe044530ff0a58a52",
        //     "XiaoMi",
        //     0,
        //     "7c6ef7a280231b605cc9d597471db50d"
        // )
        // 选择AUTO页面采集模式，统计SDK基础指标无需手动埋点可自动采集。
        // 建议在宿主App的Application.onCreate函数中调用此函数。
        // MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)

        // Push注册
        // PushHelper.init(application);
        // 在此初始化其它依赖库
    }

    private static Call.Factory castOrNull(OkHttpClient okHttpClient) {
        if (okHttpClient instanceof Call.Factory) {
            return (Call.Factory) okHttpClient;
        }
        return null;
    }

    public static CookieRoomDatabase getDatabase() {
        return sDatabase;
    }
}