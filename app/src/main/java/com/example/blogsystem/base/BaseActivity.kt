package com.example.blogsystem.base

import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.umeng.message.PushAgent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseActivity : AppCompatActivity(), Init {

    @CallSuper
    override fun initSDK() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                PushAgent.getInstance(App.get()).onAppStart()
            }
        }
    }
}