package cn.cqautotest.sunnybeach.paging.source.msg.factory

import cn.cqautotest.sunnybeach.http.api.sob.MsgApi
import cn.cqautotest.sunnybeach.model.ApiResponse
import cn.cqautotest.sunnybeach.model.IApiResponse
import cn.cqautotest.sunnybeach.paging.source.msg.impl.IMsgPageData

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/27
 * desc   : 摸鱼列表消息 PagingSource 工厂
 */
class FishMsgListFactory private constructor() : AbstractMsgListFactory() {

    @Suppress("UNCHECKED_CAST")
    override suspend fun createMsgListByType(page: Int): IApiResponse<IMsgPageData> {
        return MsgApi.getMomentMsgList(page) as ApiResponse<IMsgPageData>
    }

    companion object {

        fun create() = FishMsgListFactory()
    }
}