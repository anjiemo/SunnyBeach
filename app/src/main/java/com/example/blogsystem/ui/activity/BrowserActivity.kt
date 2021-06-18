package com.example.blogsystem.ui.activity

import android.os.Bundle
import com.example.blogsystem.base.BaseActivity
import com.example.blogsystem.databinding.ActivityBrowserBinding

class BrowserActivity : BaseActivity() {

    private lateinit var binding: ActivityBrowserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBrowserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        callAllInit()
    }

    override fun initView() {
        val webView = binding.webView

    }
}