package cn.cqautotest.sunnybeach.paging.source.msg.factory

import cn.cqautotest.sunnybeach.model.IApiResponse
import cn.cqautotest.sunnybeach.paging.source.msg.impl.IMsgPageData

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/27
 * desc   : 抽象列表消息 PagingSource 工厂
 */
abstract class AbstractMsgListFactory {

    abstract suspend fun createMsgListByType(page: Int): IApiResponse<IMsgPageData>
}