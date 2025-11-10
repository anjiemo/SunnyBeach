package cn.cqautotest.sunnybeach.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.paging.PagingData
import cn.cqautotest.sunnybeach.db.AppRoomDatabase
import cn.cqautotest.sunnybeach.model.msg.ArticleMsg
import cn.cqautotest.sunnybeach.model.msg.AtMeMsg
import cn.cqautotest.sunnybeach.model.msg.LikeMsg
import cn.cqautotest.sunnybeach.model.msg.MomentMsg
import cn.cqautotest.sunnybeach.model.msg.QaMsg
import cn.cqautotest.sunnybeach.model.msg.SystemMsg
import cn.cqautotest.sunnybeach.paging.source.msg.MsgListFactory
import cn.cqautotest.sunnybeach.paging.source.msg.impl.MsgType
import cn.cqautotest.sunnybeach.repository.Repository
import com.blankj.utilcode.util.Utils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/24
 * desc   : 消息的 ViewModel
 */
class MsgViewModel(application: Application = Utils.getApp()) : AndroidViewModel(application = application) {

    private val userBlockDao by lazy { AppRoomDatabase.getDatabase(application).userBlockDao() }
    private val articleMsgListFactory = MsgListFactory.createMsgListByType(MsgType.ARTICLE)

    private val fishMsgListFactory = MsgListFactory.createMsgListByType(MsgType.FISH)

    private val qaMsgListFactory = MsgListFactory.createMsgListByType(MsgType.QA)

    private val likeMsgListFactory = MsgListFactory.createMsgListByType(MsgType.LIKE)

    private val systemMsgListFactory = MsgListFactory.createMsgListByType(MsgType.SYSTEM)

    private val atMeMsgListFactory = MsgListFactory.createMsgListByType(MsgType.AT)

    fun readQaMsg(msgId: String) = Repository.readQaMsg(msgId)

    fun readAtMeMsg(msgId: String) = Repository.readAtMeMsg(msgId)

    fun readMomentMsg(msgId: String) = Repository.readMomentMsg(msgId)

    fun replyArticleMsg(msgId: String) = Repository.replyArticleMsg(msgId)

    fun readArticleMsg(msgId: String) = Repository.readArticleMsg(msgId)

    fun readAllMsg() = Repository.readAllMsg()

    fun getUnReadMsgCount() = Repository.getUnReadMsgCount()
        .catch { Timber.e(it) }

    fun getArticleMsgList(): Flow<PagingData<ArticleMsg.Content>> = articleMsgListFactory.get(userBlockDao)

    fun getMomentMsgList(): Flow<PagingData<MomentMsg.Content>> = fishMsgListFactory.get(userBlockDao)

    fun getQaMsgList(): Flow<PagingData<QaMsg.Content>> = qaMsgListFactory.get(userBlockDao)

    fun getLikeMsgList(): Flow<PagingData<LikeMsg.Content>> = likeMsgListFactory.get(userBlockDao)

    fun getSystemMsgList(): Flow<PagingData<SystemMsg.Content>> = systemMsgListFactory.get(userBlockDao)

    fun getAtMeMsgList(): Flow<PagingData<AtMeMsg.Content>> = atMeMsgListFactory.get(userBlockDao)
}