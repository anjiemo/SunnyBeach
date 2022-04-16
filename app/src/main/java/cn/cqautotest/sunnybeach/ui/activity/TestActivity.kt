package cn.cqautotest.sunnybeach.ui.activity

import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.Init
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.TestActivityBinding

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/11/16
 *    desc   : 用于测试的界面
 */
class TestActivity : AppActivity(), Init {

    private val mBinding by viewBinding<TestActivityBinding>()

    override fun getLayoutId(): Int = R.layout.test_activity

    override fun initView() {

    }

    override fun initData() {

    }
}