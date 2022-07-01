package cn.cqautotest.sunnybeach.ui.activity

import android.annotation.SuppressLint
import android.view.View
import androidx.activity.viewModels
import androidx.collection.SparseArrayCompat
import androidx.collection.set
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.MessageCenterActivityBinding
import cn.cqautotest.sunnybeach.ktx.createDefaultStyleBadge
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.ktx.startActivity
import cn.cqautotest.sunnybeach.ktx.takeIfLogin
import cn.cqautotest.sunnybeach.model.msg.UnReadMsgCount
import cn.cqautotest.sunnybeach.ui.activity.msg.*
import cn.cqautotest.sunnybeach.viewmodel.MsgViewModel
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
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
    private val mDrawableCacheMap = SparseArrayCompat<BadgeDrawable>(6)

    override fun getLayoutId(): Int = R.layout.message_center_activity

    override fun initView() {
        supportActionBar?.hide()
    }

    override fun initData() {

    }

    override fun initEvent() {
        mBinding.articleContainer.setFixOnClickListener {
            takeIfLogin {
                // 文章评论消息列表
                startActivity<ArticleMsgListActivity>()
            }
        }
        mBinding.likeContainer.setFixOnClickListener {
            takeIfLogin {
                // 点赞消息列表
                startActivity<LikeMsgListActivity>()
            }
        }
        mBinding.fishContainer.setFixOnClickListener {
            takeIfLogin {
                // 摸鱼评论消息列表
                startActivity<FishMsgListActivity>()
            }
        }
        mBinding.atMeContainer.setFixOnClickListener {
            takeIfLogin {
                // @我消息列表
                startActivity<AtMeMsgListActivity>()
            }
        }
        mBinding.qaContainer.setFixOnClickListener {
            takeIfLogin {
                // 问答消息列表
                startActivity<QaMsgListActivity>()
            }
        }
        mBinding.systemContainer.setFixOnClickListener {
            takeIfLogin {
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

    @SuppressLint("UnsafeOptInUsageError")
    private fun View.setUnReadCount(unReadCount: Int) {
        val anchor = this
        val viewId = id
        // 通过 View 的 id 从缓存里获取 BadgeDrawable
        val drawable = mDrawableCacheMap[viewId]
        if (drawable == null) {
            // 如果缓存里没有，则创建 BadgeDrawable
            createDefaultStyleBadge(context, unReadCount).apply {
                BadgeUtils.attachBadgeDrawable(this, anchor)
                mDrawableCacheMap[viewId] = this
            }
        } else {
            // 否则更新 BadgeDrawable 的状态
            drawable.isVisible = unReadCount > 0
            drawable.number = unReadCount
        }
    }

    override fun onRightClick(titleBar: TitleBar) {
        takeIfLogin {
            // 先在 UI 上响应给用户，再去网络请求
            setUnReadCountByMenu(UnReadMsgCount())
            // 设置消息全部已读
            mMsgViewModel.readAllMsg().observe(this) {
                val tips = it.getOrNull() ?: return@observe
                toast(tips)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // BadgeDrawable 内部是弱引用持有 View，我们不关心 View 的释放问题
        mDrawableCacheMap.clear()
    }
}