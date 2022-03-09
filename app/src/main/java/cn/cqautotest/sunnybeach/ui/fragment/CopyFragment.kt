package cn.cqautotest.sunnybeach.ui.fragment

import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.ui.activity.CopyActivity

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/11/12
 *    desc   : 可进行拷贝的副本
 */
class CopyFragment : AppFragment<CopyActivity>() {

    override fun getLayoutId(): Int = R.layout.copy_fragment

    override fun initView() {}

    override fun initData() {}

    companion object {
        fun newInstance(): CopyFragment {
            return CopyFragment()
        }
    }
}