# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

# Bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

# AOP
-adaptclassstrings
-keepattributes InnerClasses, EnclosingMethod, Signature, *Annotation*

# OkHttp3
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.conscrypt.**

# QMUI Android
-keep class **_FragmentFinder { *; }
-keep class androidx.fragment.app.* { *; }

#-keep class com.qmuiteam.qmui.arch.record.RecordIdClassMap { *; }
#-keep class com.qmuiteam.qmui.arch.record.RecordIdClassMapImpl { *; }
#
#-keep class com.qmuiteam.qmui.arch.scheme.SchemeMap {*;}
#-keep class com.qmuiteam.qmui.arch.scheme.SchemeMapImpl {*;}

# HMS Core SDK
-dontwarn com.huawei.hms.**
-dontwarn org.chromium.net.**
-ignorewarnings
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keep class com.huawei.hianalytics.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}

# XPopup：https://github.com/junixapp/XPopup
-dontwarn com.lxj.xpopup.widget.**
-keep class com.lxj.xpopup.widget.**{*;}

# TitleBar：https://github.com/getActivity/TitleBar
-keep class com.hjq.bar.** {*;}

# EasyHttp：https://github.com/getActivity/EasyHttp
# EasyHttp 框架混淆规则
-keep class com.hjq.http.** {*;}
# 必须要加上此规则，否则会导致泛型解析失败
-keep class * implements com.hjq.http.listener.OnHttpListener {
    *;
}
-keep class * extends com.hjq.http.model.ResponseClass {
    *;
}

# LiveEventBus：https://github.com/JeremyLiao/LiveEventBus
-dontwarn com.jeremyliao.liveeventbus.**
# 保留LiveEventBus核心类
-keep class com.jeremyliao.liveeventbus.LiveEventBus { *; }
-keep interface com.jeremyliao.liveeventbus.core.LiveEvent { *; }
-keep class com.jeremyliao.liveeventbus.core.Observable { *; }
-keep class com.jeremyliao.liveeventbus.core.LiveEventBusCore { *; }
# 保留Lifecycle相关类（根据是否使用AndroidX选择）
-keep class androidx.lifecycle.LiveData { *; }
-keep class androidx.lifecycle.Observer { *; }
-keep class androidx.arch.core.** { *; }

# 阿里云播放器：https://help.aliyun.com/zh/vod/developer-reference/quick-integration-1?spm=a2c4g.11186623.0.0.7bd24addVQH3VE#16a8a40341cma
-keep class com.alivc.**{*;}
-keep class com.aliyun.**{*;}
-keep class com.cicada.**{*;}

-dontwarn com.alivc.**
-dontwarn com.aliyun.**
-dontwarn com.cicada.**