package cn.cqautotest.sunnybeach.other

import android.app.Activity
import android.content.Context
import android.os.Build
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.manager.ActivityManager
import cn.cqautotest.sunnybeach.ui.dialog.MessageDialog
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.permission.PermissionLists
import com.hjq.permissions.permission.base.IPermission
import com.hjq.toast.Toaster

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2020/10/24
 *    desc   : 权限申请回调封装
 */
abstract class PermissionCallback : OnPermissionCallback {

    override fun onResult(grantedList: List<IPermission>, deniedList: List<IPermission>) {
        if (deniedList.isNotEmpty()) {
            val activity = ActivityManager.getInstance().getTopActivity() ?: return
            val never = deniedList.any { permission -> permission.isDoNotAskAgainPermission(activity) }
            onDenied(deniedList, never)
        }
    }

    private fun onDenied(permissions: List<IPermission>, never: Boolean) {
        if (never) {
            showPermissionDialog(permissions)
            return
        }
        if (permissions.size == 1 && (PermissionLists.getAccessBackgroundLocationPermission() == permissions[0])) {
            Toaster.show(R.string.common_permission_fail_4)
            return
        }
        Toaster.show(R.string.common_permission_fail_1)
    }

    /**
     * 显示授权对话框
     */
    protected fun showPermissionDialog(permissions: List<IPermission>) {
        val activity: Activity? = ActivityManager.getInstance().getTopActivity()
        if ((activity == null) || activity.isFinishing || activity.isDestroyed) {
            return
        }
        MessageDialog.Builder(activity)
            .setTitle(R.string.common_permission_alert)
            .setMessage(getPermissionHint(activity, permissions))
            .setConfirm(R.string.common_permission_goto)
            .setCancel(null)
            .setCancelable(false)
            .setListener {
                XXPermissions.startPermissionActivity(activity, permissions)
            }
            .show()
    }

    /**
     * 根据权限获取提示
     */
    protected fun getPermissionHint(context: Context, permissions: List<IPermission>): String {
        if (permissions.isEmpty()) {
            return context.getString(R.string.common_permission_fail_2)
        }
        val hints: MutableList<String> = ArrayList()
        for (permission: IPermission? in permissions) {
            when (permission) {
                PermissionLists.getReadExternalStoragePermission(),
                PermissionLists.getWriteExternalStoragePermission(),
                PermissionLists.getManageExternalStoragePermission() -> {
                    val hint: String = context.getString(R.string.common_permission_storage)
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }

                PermissionLists.getCameraPermission() -> {
                    val hint: String = context.getString(R.string.common_permission_camera)
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }

                PermissionLists.getRecordAudioPermission() -> {
                    val hint: String = context.getString(R.string.common_permission_microphone)
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }

                PermissionLists.getAccessFineLocationPermission(),
                PermissionLists.getAccessCoarseLocationPermission(),
                PermissionLists.getAccessBackgroundLocationPermission() -> {
                    val hint: String = if (!permissions.contains(PermissionLists.getAccessFineLocationPermission()) &&
                        !permissions.contains(PermissionLists.getAccessCoarseLocationPermission())
                    ) {
                        context.getString(R.string.common_permission_location_background)
                    } else {
                        context.getString(R.string.common_permission_location)
                    }
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }

                PermissionLists.getReadPhoneStatePermission(),
                PermissionLists.getCallPhonePermission(),
                PermissionLists.getAddVoicemailPermission(),
                PermissionLists.getUseSipPermission(),
                PermissionLists.getReadPhoneNumbersPermission(),
                PermissionLists.getAnswerPhoneCallsPermission() -> {
                    val hint: String = context.getString(R.string.common_permission_phone)
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }

                PermissionLists.getGetAccountsPermission(),
                PermissionLists.getReadContactsPermission(),
                PermissionLists.getWriteContactsPermission() -> {
                    val hint: String = context.getString(R.string.common_permission_contacts)
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }

                PermissionLists.getReadCalendarPermission(),
                PermissionLists.getWriteCalendarPermission() -> {
                    val hint: String = context.getString(R.string.common_permission_calendar)
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }

                PermissionLists.getReadCallLogPermission(),
                PermissionLists.getWriteCallLogPermission(),
                PermissionLists.getProcessOutgoingCallsPermission() -> {
                    val hint: String =
                        context.getString(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) R.string.common_permission_call_log else R.string.common_permission_phone)
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }

                PermissionLists.getBodySensorsPermission() -> {
                    val hint: String = context.getString(R.string.common_permission_sensors)
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }

                PermissionLists.getActivityRecognitionPermission() -> {
                    val hint: String = context.getString(R.string.common_permission_activity_recognition)
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }

                PermissionLists.getSendSmsPermission(),
                PermissionLists.getReceiveSmsPermission(),
                PermissionLists.getReadSmsPermission(),
                PermissionLists.getReceiveWapPushPermission(),
                PermissionLists.getReceiveMmsPermission() -> {
                    val hint: String = context.getString(R.string.common_permission_sms)
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }

                PermissionLists.getRequestInstallPackagesPermission() -> {
                    val hint: String = context.getString(R.string.common_permission_install)
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }

                PermissionLists.getNotificationServicePermission() -> {
                    val hint: String = context.getString(R.string.common_permission_notification)
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }

                PermissionLists.getSystemAlertWindowPermission() -> {
                    val hint: String = context.getString(R.string.common_permission_window)
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }

                PermissionLists.getWriteSettingsPermission() -> {
                    val hint: String = context.getString(R.string.common_permission_setting)
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }
            }
        }
        if (hints.isNotEmpty()) {
            val builder: StringBuilder = StringBuilder()
            for (text: String? in hints) {
                if (builder.isEmpty()) {
                    builder.append(text)
                } else {
                    builder.append("、")
                        .append(text)
                }
            }
            builder.append(" ")
            return context.getString(R.string.common_permission_fail_3, builder.toString())
        }
        return context.getString(R.string.common_permission_fail_2)
    }
}