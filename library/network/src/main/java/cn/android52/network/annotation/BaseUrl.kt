package cn.android52.network.annotation

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/06/14
 * desc   : BaseUrl 注解，接口请求方法如需使用不同的 BaseUrl ，可自定义注解并将本注解作用于自定义注解。
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class BaseUrl(val value: String)
