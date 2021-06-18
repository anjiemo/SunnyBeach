package com.example.blogsystem.utils

import kotlin.random.Random

const val USER_SETTING = "userSetting"
const val IS_FIRST = "isFirst"

const val BASE_URL = "http://192.168.123.159:3000/"

const val DEFAULT_URL =
    "http://service.picasso.adesk.com/v1/vertical/vertical?disorder=true&limit=10&skip=0&adult=false&first=1&url=http%3A%2F%2Fservice.picasso.adesk.com%2Fv1%2Fvertical%2Fvertical&order=hot"
const val DEFAULT_BANNER_URL =
    "http://wallpaper.apc.360.cn/index.php?c=WallPaper&a=getAppsByOrder&order=create_time&start=55&count=5&from=360chrome"

const val BLOG_BASE_URL = "https://api.sunofbeach.net/"

const val DEFAULT_AVATAR_URL = "https://cdn.sunofbeaches.com/images/default_avatar.png"

val VERIFY_CODE_URL =
    "${BLOG_BASE_URL}uc/ut/captcha?code=${System.currentTimeMillis()}${Random.nextInt(99999)}"

const val DEFAULT_HTTP_OK_CODE = 200

const val BLOG_HTTP_OK_CODE = 10000

const val APP_INFO_URL = "http://hm03097.h37.hmie.cn/blog/app/appconfig.json"