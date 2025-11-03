package cn.cqautotest.sunnybeach.paging.source.msg.factory

import cn.cqautotest.sunnybeach.http.api.sob.MsgApi
import cn.cqautotest.sunnybeach.model.ApiResponse
import cn.cqautotest.sunnybeach.model.IApiResponse
import cn.cqautotest.sunnybeach.paging.source.msg.impl.IMsgPageData
import cn.cqautotest.sunnybeach.paging.source.msg.impl.MsgType

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/27
 * desc   : 系统列表消息 PagingSource 工厂
 */
class AtMeMsgListFactory private constructor(msgType: MsgType) : AbstractMsgListFactory(msgType = msgType) {

    @Suppress("UNCHECKED_CAST")
    override suspend fun createMsgListByType(page: Int): IApiResponse<IMsgPageData> {
        return MsgApi.getAtMeMsgList(page) as ApiResponse<IMsgPageData>
    }

    companion object : Creator {

        override fun create(msgType: MsgType) = AtMeMsgListFactory(msgType = msgType)
    }
}