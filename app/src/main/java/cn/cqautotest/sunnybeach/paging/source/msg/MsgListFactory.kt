package cn.cqautotest.sunnybeach.paging.source.msg

import cn.cqautotest.sunnybeach.paging.source.msg.factory.*
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
            MsgType.ARTICLE -> ArticleMsgListFactory.create()
            MsgType.FISH -> FishMsgListFactory.create()
            MsgType.QA -> QaMsgListFactory.create()
            MsgType.LIKE -> LikeMsgListFactory.create()
            MsgType.SYSTEM -> SystemMsgListFactory.create()
            MsgType.AT -> AtMeMsgListFactory.create()
        }
    }
}