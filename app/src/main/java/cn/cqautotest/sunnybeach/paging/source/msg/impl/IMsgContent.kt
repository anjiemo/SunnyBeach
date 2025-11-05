package cn.cqautotest.sunnybeach.paging.source.msg.impl

interface IMsgContent

interface IUserMsgContent : IMsgContent {

    fun getUId(): String
}

interface INormalMsgContent : IMsgContent