package cn.cqautotest.sunnybeach.ui.activity

import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.MessageCenterActivityBinding
import cn.cqautotest.sunnybeach.ktx.*
import cn.cqautotest.sunnybeach.model.msg.UnReadMsgCount
import cn.cqautotest.sunnybeach.ui.activity.msg.*
import cn.cqautotest.sunnybeach.viewmodel.MsgViewModel
import com.hjq.bar.TitleBar

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/24
 * desc   : 消息中心界面
 */
class MessageCenterActivity : AppActivity() {

    private val mBinding by viewBinding<MessageCenterActivityBinding>()
    private val mMsgViewModel by viewModels<MsgViewModel>()

    override fun getLayoutId(): Int = R.layout.message_center_activity

    override fun initView() {
        hideSupportActionBar()
    }

    override fun initData() {

    }

    override fun initEvent() {
        mBinding.articleContainer.setFixOnClickListener {
            ifLoginThen {
                // 文章评论消息列表
                startActivity<ArticleMsgListActivity>()
            }
        }
        mBinding.likeContainer.setFixOnClickListener {
            ifLoginThen {
                // 点赞消息列表
                startActivity<LikeMsgListActivity>()
            }
        }
        mBinding.fishContainer.setFixOnClickListener {
            ifLoginThen {
                // 摸鱼评论消息列表
                startActivity<FishMsgListActivity>()
            }
        }
        mBinding.atMeContainer.setFixOnClickListener {
            ifLoginThen {
                // @我消息列表
                startActivity<AtMeMsgListActivity>()
            }
        }
        mBinding.qaContainer.setFixOnClickListener {
            ifLoginThen {
                // 问答消息列表
                startActivity<QaMsgListActivity>()
            }
        }
        mBinding.systemContainer.setFixOnClickListener {
            ifLoginThen {
                // 系统消息列表
                startActivity<SystemMsgListActivity>()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mMsgViewModel.getUnReadMsgCount().observe(this) {
            val unReadMsgCount = it.getOrNull() ?: return@observe
            setUnReadCountByMenu(unReadMsgCount)
        }
    }

    private fun setUnReadCountByMenu(unReadMsgCount: UnReadMsgCount) {
        mBinding.ivArticle.setUnReadCount(unReadMsgCount.articleMsgCount)
        mBinding.ivLike.setUnReadCount(unReadMsgCount.thumbUpMsgCount)
        mBinding.ivFish.setUnReadCount(unReadMsgCount.momentCommentCount)
        mBinding.ivAtMe.setUnReadCount(unReadMsgCount.atMsgCount)
        mBinding.ivQa.setUnReadCount(unReadMsgCount.wendaMsgCount)
        mBinding.ivSystem.setUnReadCount(unReadMsgCount.systemMsgCount)
    }

    override fun onRightClick(titleBar: TitleBar) {
        ifLoginThen {
            // 先在 UI 上响应给用户，再去网络请求
            setUnReadCountByMenu(UnReadMsgCount())
            // 设置消息全部已读
            mMsgViewModel.readAllMsg().observe(this) {
                val tips = it.getOrNull() ?: return@observe
                toast(tips)
            }
        }
    }
}