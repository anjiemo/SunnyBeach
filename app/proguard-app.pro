# 忽略警告
#-ignorewarning

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