package cn.android52.sunnybeach.skin.activity

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import cn.android52.sunnybeach.skin.callback.ISkinChangedListener
import cn.android52.sunnybeach.skin.util.hookActivity
import cn.android52.sunnybeach.skin.util.removeActivityHook

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/11/19
 *    desc   : 支持换肤的 Activity 基类，使用者可以继承该类以获得换肤的实现
 */
open class SupportSkinActivity : AppCompatActivity(), ISkinChangedListener {

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        hookActivity(this)
        super.onCreate(savedInstanceState)
    }

    override fun onSkinChanged() {

    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        removeActivityHook(this)
    }
}