package com.example.blogsystem.base

interface Init {

    fun callAllInit() {
        initView()
        initData()
        initEvent()
        initSDK()
    }

    fun initSDK() {

    }

    fun initView() {

    }

    fun initData() {

    }

    fun initEvent() {

    }
}