# 忽略警告
#-ignorewarning

# 全局优化配置
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# 混淆保护自己项目的部分代码以及引用的第三方jar包
#-libraryjars libs/xxxxxxxxx.jar

# 不混淆这个包下的类
-keep class cn.cqautotest.sunnybeach.model.** {
    <fields>;
}
-keep class cn.cqautotest.sunnybeach.http.api.** {
    <fields>;
}
-keep class cn.cqautotest.sunnybeach.http.response.** {
    <fields>;
}
-keep class cn.cqautotest.sunnybeach.http.model.** {
    <fields>;
}

# 不混淆被 Log 注解的方法信息
-keepclassmembernames class ** {
    @cn.cqautotest.sunnybeach.aop.Log <methods>;
}

# EasyWindow 混淆规则：https://github.com/getActivity/EasyWindow
-keep class com.hjq.window.** {*;}

# 保留 Retrofit 服务接口，避免 release 混淆后运行时反射拿不到接口注解信息，
# 导致 BaseUrlInterceptor 无法替换到正确的域名。
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# 保留 BaseUrl 机制依赖的运行时注解定义。
# 这里不保留业务实现类，只保留注解类型本身，尽量缩小 keep 范围。
-keep @interface cn.funkt.annotation.BaseUrl
-keep @interface cn.funkt.annotation.Ignore
-keep @interface cn.cqautotest.sunnybeach.http.annotation.baseurl.**