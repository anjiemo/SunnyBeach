package cn.cqautotest.sunnybeach.paging.source.msg

import cn.cqautotest.sunnybeach.paging.source.msg.factory.AbstractMsgListFactory
import cn.cqautotest.sunnybeach.paging.source.msg.factory.ArticleMsgListFactory
import cn.cqautotest.sunnybeach.paging.source.msg.factory.AtMeMsgListFactory
import cn.cqautotest.sunnybeach.paging.source.msg.factory.FishMsgListFactory
import cn.cqautotest.sunnybeach.paging.source.msg.factory.LikeMsgListFactory
import cn.cqautotest.sunnybeach.paging.source.msg.factory.QaMsgListFactory
import cn.cqautotest.sunnybeach.paging.source.msg.factory.SystemMsgListFactory
import cn.cqautotest.sunnybeach.paging.source.msg.impl.MsgType

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/27
 * desc   : 列表消息 PagingSource 工厂
 */
object MsgListFactory {

    fun createMsgListByType(msgType: MsgType): AbstractMsgListFactory {
        return when (msgType) {
            MsgType.ARTICLE -> ArticleMsgListFactory.create(msgType = msgType)
            MsgType.FISH -> FishMsgListFactory.create(msgType = msgType)
            MsgType.QA -> QaMsgListFactory.create(msgType = msgType)
            MsgType.LIKE -> LikeMsgListFactory.create(msgType = msgType)
            MsgType.SYSTEM -> SystemMsgListFactory.create(msgType = msgType)
            MsgType.AT -> AtMeMsgListFactory.create(msgType = msgType)
        }
    }
}