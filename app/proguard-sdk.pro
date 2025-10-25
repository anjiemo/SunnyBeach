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

-keepnames @org.aspectj.lang.annotation.Aspect class * {
    public <methods>;
}

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