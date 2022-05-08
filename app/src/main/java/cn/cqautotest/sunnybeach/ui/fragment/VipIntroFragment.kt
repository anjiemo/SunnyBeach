package cn.cqautotest.sunnybeach.ui.fragment

import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.databinding.VipIntroFragmentBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.other.GridSpaceDecoration
import cn.cqautotest.sunnybeach.ui.activity.CopyActivity
import cn.cqautotest.sunnybeach.ui.adapter.VipIntroAdapter

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2022/02/03
 *    desc   : Vip 特权介绍界面
 */
class VipIntroFragment : AppFragment<CopyActivity>() {

    private val mBinding: VipIntroFragmentBinding by viewBinding()
    private val mVipIntroAdapter = VipIntroAdapter()

    override fun getLayoutId(): Int = R.layout.vip_intro_fragment

    override fun initView() {
        mBinding.rvVipIntroduction.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = mVipIntroAdapter
            addItemDecoration(GridSpaceDecoration(4.dp))
        }
    }

    override fun initData() {
        val data = arrayListOf<VipIntroAdapter.VipIntro>()
        data.apply {
            add(R.drawable.ic_free, "免积分", "下载课程资料免积分")
            add(R.drawable.ic_vip, "尊贵标志", "尊贵的VIP身份标志")
            add(R.drawable.ic_crowd, "交流群", "吹牛顺便聊点技术")
            add(R.drawable.ic_youxiangou, "优先解答", "提问优先详细解答")
            add(R.drawable.ic_integral, "双倍积分", "登录签到双倍积分")
            add(R.drawable.ic_audit, "无须审核", "发表文章不用审核")
            add(R.drawable.ic_tax, "不扣税", "打赏/最佳答案不扣税")
            add(R.drawable.ic_coin, "积分补贴", "每月可领100Sunof币")
        }
        mVipIntroAdapter.setData(data)
    }

    private fun ArrayList<VipIntroAdapter.VipIntro>.add(resId: Int, title: String, desc: String) {
        add(VipIntroAdapter.VipIntro(resId, title, desc))
    }

    companion object {
        fun newInstance(): VipIntroFragment {
            return VipIntroFragment()
        }
    }
}