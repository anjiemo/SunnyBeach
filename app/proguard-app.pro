# 忽略警告
#-ignorewarning

# 混淆保护自己项目的部分代码以及引用的第三方jar包
#-libraryjars libs/umeng-analytics-v5.2.4.jar

-keep class cn.cqautotest.sunnybeach.** {*;}

# 不混淆这些包下的字段名
-keepclassmembernames class cn.cqautotest.sunnybeach.http.request.** {
    <fields>;
}
-keepclassmembernames class cn.cqautotest.sunnybeach.http.response.** {
    <fields>;
}
-keepclassmembernames class cn.cqautotest.sunnybeach.http.model.** {
    <fields>;
}

# 不混淆被 DebugLog 注解的方法信息
-keepclassmembernames class ** {
    @cn.cqautotest.sunnybeach.aop.DebugLog <methods>;
}