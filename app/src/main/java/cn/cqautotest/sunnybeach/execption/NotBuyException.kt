package cn.cqautotest.sunnybeach.execption

class NotBuyException(override val message: String = "未购买") : RuntimeException(message)