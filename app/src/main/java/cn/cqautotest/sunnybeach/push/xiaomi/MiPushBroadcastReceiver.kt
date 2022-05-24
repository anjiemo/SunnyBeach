package cn.cqautotest.sunnybeach.push.xiaomi

import android.content.Context
import android.text.TextUtils
import com.xiaomi.mipush.sdk.*
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
        mMessage = message.content
        if (!TextUtils.isEmpty(message.topic)) {
            mTopic = message.topic
        } else if (!TextUtils.isEmpty(message.alias)) {
            mAlias = message.alias
        } else if (!TextUtils.isEmpty(message.userAccount)) {
            mUserAccount = message.userAccount
        }
    }

    override fun onNotificationMessageClicked(context: Context?, message: MiPushMessage) {
        mMessage = message.content
        if (!TextUtils.isEmpty(message.topic)) {
            mTopic = message.topic
        } else if (!TextUtils.isEmpty(message.alias)) {
            mAlias = message.alias
        } else if (!TextUtils.isEmpty(message.userAccount)) {
            mUserAccount = message.userAccount
        }
    }

    override fun onNotificationMessageArrived(context: Context?, message: MiPushMessage) {
        mMessage = message.content
        if (!TextUtils.isEmpty(message.topic)) {
            mTopic = message.topic
        } else if (!TextUtils.isEmpty(message.alias)) {
            mAlias = message.alias
        } else if (!TextUtils.isEmpty(message.userAccount)) {
            mUserAccount = message.userAccount
        }
    }

    override fun onCommandResult(context: Context?, message: MiPushCommandMessage) {
        val command = message.command
        val arguments = message.commandArguments
        val cmdArg1 = if (arguments != null && arguments.size > 0) arguments[0] else null
        val cmdArg2 = if (arguments != null && arguments.size > 1) arguments[1] else null
        if (MiPushClient.COMMAND_REGISTER == command) {
            if (message.resultCode == ErrorCode.SUCCESS.toLong()) {
                mRegId = cmdArg1
                Timber.d("onCommandResult：===> mRegId is $mRegId")
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS == command) {
            if (message.resultCode == ErrorCode.SUCCESS.toLong()) {
                mAlias = cmdArg1
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS == command) {
            if (message.resultCode == ErrorCode.SUCCESS.toLong()) {
                mAlias = cmdArg1
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC == command) {
            if (message.resultCode == ErrorCode.SUCCESS.toLong()) {
                mTopic = cmdArg1
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC == command) {
            if (message.resultCode == ErrorCode.SUCCESS.toLong()) {
                mTopic = cmdArg1
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME == command) {
            if (message.resultCode == ErrorCode.SUCCESS.toLong()) {
                mStartTime = cmdArg1
                mEndTime = cmdArg2
            }
        }
    }

    override fun onReceiveRegisterResult(context: Context?, message: MiPushCommandMessage) {
        val command = message.command
        val arguments = message.commandArguments
        val cmdArg1 = if (arguments != null && arguments.size > 0) arguments[0] else null
        val cmdArg2 = if (arguments != null && arguments.size > 1) arguments[1] else null
        if (MiPushClient.COMMAND_REGISTER == command) {
            if (message.resultCode == ErrorCode.SUCCESS.toLong()) {
                mRegId = cmdArg1
                Timber.d("onReceiveRegisterResult：===> mRegId is $mRegId")
            }
        }
    }
}