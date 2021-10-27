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
            MsgType.ARTICLE -> ArticleMsgListFactory()
            MsgType.FISH -> FishMsgListFactory()
            MsgType.QA -> QAMsgListFactory()
            MsgType.LIKE -> LikeMsgListFactory()
            MsgType.SYSTEM -> SystemMsgListFactory()
            MsgType.AT -> AtMeMsgListFactory()
        }
    }
}