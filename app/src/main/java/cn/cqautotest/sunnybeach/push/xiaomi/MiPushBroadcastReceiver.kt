package cn.cqautotest.sunnybeach.push.xiaomi

import android.content.Context
import android.text.TextUtils
import cn.cqautotest.sunnybeach.ktx.toJson
import com.xiaomi.mipush.sdk.*
import org.json.JSONObject
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/05/24
 * desc   : 小米推送 BroadcastReceiver 类
 */
class MiPushBroadcastReceiver : PushMessageReceiver() {

    private var mRegId: String? = null
    private val mResultCode: Long = -1
    private val mReason: String? = null
    private val mCommand: String? = null
    private var mMessage: String? = null
    private var mTopic: String? = null
    private var mAlias: String? = null
    private var mUserAccount: String? = null
    private var mStartTime: String? = null
    private var mEndTime: String? = null

    override fun onReceivePassThroughMessage(context: Context?, message: MiPushMessage) {
        Timber.d("onReceivePassThroughMessage：===> message is ${message.toJson()}")
        mMessage = message.content
        when {
            !TextUtils.isEmpty(message.topic) -> mTopic = message.topic
            !TextUtils.isEmpty(message.alias) -> mAlias = message.alias
            !TextUtils.isEmpty(message.userAccount) -> mUserAccount = message.userAccount
        }
        mMessage?.let { parseMessage(it) }
    }

    /**
     * 解析透传消息内容
     * 消息结构：
     * {
     *     "type": "update",
     *     "data": {
     *         "title": "更新标题",
     *         "content": "更新内容",
     *         "url": "http://www.baidu.com",
     *         "force": true
     *     }
     * }
     */
    private fun parseMessage(message: String) {
        val jsonObject = JSONObject(message)
        when (jsonObject.optString("type")) {
            "update" -> {
                // 更新

            }
        }
    }

    override fun onNotificationMessageClicked(context: Context?, message: MiPushMessage) {
        Timber.d("onNotificationMessageClicked：===> message is ${message.toJson()}")
        mMessage = message.content
        when {
            !TextUtils.isEmpty(message.topic) -> mTopic = message.topic
            !TextUtils.isEmpty(message.alias) -> mAlias = message.alias
            !TextUtils.isEmpty(message.userAccount) -> mUserAccount = message.userAccount
        }
    }

    override fun onNotificationMessageArrived(context: Context?, message: MiPushMessage) {
        Timber.d("onNotificationMessageArrived：===> message is ${message.toJson()}")
        mMessage = message.content
        when {
            !TextUtils.isEmpty(message.topic) -> mTopic = message.topic
            !TextUtils.isEmpty(message.alias) -> mAlias = message.alias
            !TextUtils.isEmpty(message.userAccount) -> mUserAccount = message.userAccount
        }
    }

    override fun onCommandResult(context: Context?, message: MiPushCommandMessage) {
        Timber.d("onCommandResult：===> message is ${message.toJson()}")
        val command = message.command
        val arguments = message.commandArguments
        val cmdArg1 = arguments.getOrNull(0)
        val cmdArg2 = arguments.getOrNull(1)
        takeIf { message.resultCode == ErrorCode.SUCCESS.toLong() }?.let {
            when (command) {
                MiPushClient.COMMAND_REGISTER -> mRegId = cmdArg1
                MiPushClient.COMMAND_SET_ALIAS, MiPushClient.COMMAND_UNSET_ALIAS -> mAlias = cmdArg1
                MiPushClient.COMMAND_SUBSCRIBE_TOPIC, MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC -> mTopic = cmdArg1
                MiPushClient.COMMAND_SET_ACCEPT_TIME -> {
                    mStartTime = cmdArg1
                    mEndTime = cmdArg2
                }
            }
        }
        Timber.d("onCommandResult：===> mRegId is $mRegId")
    }

    override fun onReceiveRegisterResult(context: Context?, message: MiPushCommandMessage) {
        Timber.d("onReceiveRegisterResult：===> message is ${message.toJson()}")
        val command = message.command
        val arguments = message.commandArguments
        val cmdArg1 = arguments.getOrNull(0)
        val cmdArg2 = arguments.getOrNull(1)
        if (MiPushClient.COMMAND_REGISTER == command) {
            if (message.resultCode == ErrorCode.SUCCESS.toLong()) {
                mRegId = cmdArg1
            }
        }
        Timber.d("onReceiveRegisterResult：===> mRegId is $mRegId")
    }
}