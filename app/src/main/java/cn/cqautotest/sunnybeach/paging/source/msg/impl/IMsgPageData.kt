package cn.cqautotest.sunnybeach.paging.source.msg.impl

interface IMsgPageData {

    fun isFirst(): Boolean

    fun isLast(): Boolean

    fun getMsgContentList(): List<IMsgContent>
}