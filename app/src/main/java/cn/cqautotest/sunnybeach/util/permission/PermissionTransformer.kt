package cn.cqautotest.sunnybeach.util.permission

import com.hjq.permissions.permission.PermissionLists
import com.hjq.permissions.permission.PermissionNames
import com.hjq.permissions.permission.base.IPermission

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2025/10/25
 * desc   : 权限映射
 */
object XXPermissionTransformer {

    /**
     * 需要 [PermissionNames] 中存在的权限才可以进行转换，且对应的权限获取方式无需参数，否则将 throw UnsupportedOperationException。
     */
    fun transform(permissions: List<String>): List<IPermission> {
        return permissions.map {
            when (it) {
                // 应用权限
                PermissionNames.GET_INSTALLED_APPS -> PermissionLists.getGetInstalledAppsPermission()
                PermissionNames.USE_FULL_SCREEN_INTENT -> PermissionLists.getUseFullScreenIntentPermission()
                PermissionNames.SCHEDULE_EXACT_ALARM -> PermissionLists.getScheduleExactAlarmPermission()
                PermissionNames.MANAGE_EXTERNAL_STORAGE -> PermissionLists.getManageExternalStoragePermission()
                PermissionNames.REQUEST_INSTALL_PACKAGES -> PermissionLists.getRequestInstallPackagesPermission()
                PermissionNames.PICTURE_IN_PICTURE -> PermissionLists.getPictureInPicturePermission()
                PermissionNames.SYSTEM_ALERT_WINDOW -> PermissionLists.getSystemAlertWindowPermission()
                PermissionNames.WRITE_SETTINGS -> PermissionLists.getWriteSettingsPermission()
                PermissionNames.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS -> PermissionLists.getRequestIgnoreBatteryOptimizationsPermission()
                PermissionNames.ACCESS_NOTIFICATION_POLICY -> PermissionLists.getAccessNotificationPolicyPermission()
                PermissionNames.PACKAGE_USAGE_STATS -> PermissionLists.getPackageUsageStatsPermission()
                // PermissionNames.BIND_NOTIFICATION_LISTENER_SERVICE -> PermissionLists.getBindNotificationListenerServicePermission(/* 需要 Class 参数 */)
                PermissionNames.BIND_VPN_SERVICE -> PermissionLists.getBindVpnServicePermission()
                PermissionNames.NOTIFICATION_SERVICE -> PermissionLists.getNotificationServicePermission()
                // PermissionNames.BIND_ACCESSIBILITY_SERVICE -> PermissionLists.getBindAccessibilityServicePermission(/* 需要 Class 参数 */)
                // PermissionNames.BIND_DEVICE_ADMIN -> PermissionLists.getBindDeviceAdminPermission(/* 需要 Class 和 String 参数 */)

                // Android 13+ 权限
                PermissionNames.READ_MEDIA_VISUAL_USER_SELECTED -> PermissionLists.getReadMediaVisualUserSelectedPermission()
                PermissionNames.POST_NOTIFICATIONS -> PermissionLists.getPostNotificationsPermission()
                PermissionNames.NEARBY_WIFI_DEVICES -> PermissionLists.getNearbyWifiDevicesPermission()
                PermissionNames.BODY_SENSORS_BACKGROUND -> PermissionLists.getBodySensorsBackgroundPermission()
                PermissionNames.READ_MEDIA_IMAGES -> PermissionLists.getReadMediaImagesPermission()
                PermissionNames.READ_MEDIA_VIDEO -> PermissionLists.getReadMediaVideoPermission()
                PermissionNames.READ_MEDIA_AUDIO -> PermissionLists.getReadMediaAudioPermission()
                PermissionNames.BLUETOOTH_SCAN -> PermissionLists.getBluetoothScanPermission()
                PermissionNames.BLUETOOTH_CONNECT -> PermissionLists.getBluetoothConnectPermission()
                PermissionNames.BLUETOOTH_ADVERTISE -> PermissionLists.getBluetoothAdvertisePermission()
                PermissionNames.ACCESS_BACKGROUND_LOCATION -> PermissionLists.getAccessBackgroundLocationPermission()
                PermissionNames.ACTIVITY_RECOGNITION -> PermissionLists.getActivityRecognitionPermission()
                PermissionNames.ACCESS_MEDIA_LOCATION -> PermissionLists.getAccessMediaLocationPermission()
                PermissionNames.ACCEPT_HANDOVER -> PermissionLists.getAcceptHandoverPermission()
                PermissionNames.READ_PHONE_NUMBERS -> PermissionLists.getReadPhoneNumbersPermission()
                PermissionNames.ANSWER_PHONE_CALLS -> PermissionLists.getAnswerPhoneCallsPermission()

                // 存储权限
                PermissionNames.READ_EXTERNAL_STORAGE -> PermissionLists.getReadExternalStoragePermission()
                PermissionNames.WRITE_EXTERNAL_STORAGE -> PermissionLists.getWriteExternalStoragePermission()

                // 基础权限
                PermissionNames.CAMERA -> PermissionLists.getCameraPermission()
                PermissionNames.RECORD_AUDIO -> PermissionLists.getRecordAudioPermission()
                PermissionNames.ACCESS_FINE_LOCATION -> PermissionLists.getAccessFineLocationPermission()
                PermissionNames.ACCESS_COARSE_LOCATION -> PermissionLists.getAccessCoarseLocationPermission()
                PermissionNames.READ_CONTACTS -> PermissionLists.getReadContactsPermission()
                PermissionNames.WRITE_CONTACTS -> PermissionLists.getWriteContactsPermission()
                PermissionNames.GET_ACCOUNTS -> PermissionLists.getGetAccountsPermission()
                PermissionNames.READ_CALENDAR -> PermissionLists.getReadCalendarPermission()
                PermissionNames.WRITE_CALENDAR -> PermissionLists.getWriteCalendarPermission()
                PermissionNames.READ_PHONE_STATE -> PermissionLists.getReadPhoneStatePermission()
                PermissionNames.CALL_PHONE -> PermissionLists.getCallPhonePermission()
                PermissionNames.READ_CALL_LOG -> PermissionLists.getReadCallLogPermission()
                PermissionNames.WRITE_CALL_LOG -> PermissionLists.getWriteCallLogPermission()
                PermissionNames.ADD_VOICEMAIL -> PermissionLists.getAddVoicemailPermission()
                PermissionNames.USE_SIP -> PermissionLists.getUseSipPermission()
                PermissionNames.PROCESS_OUTGOING_CALLS -> PermissionLists.getProcessOutgoingCallsPermission()
                PermissionNames.BODY_SENSORS -> PermissionLists.getBodySensorsPermission()

                // 短信权限
                PermissionNames.SEND_SMS -> PermissionLists.getSendSmsPermission()
                PermissionNames.RECEIVE_SMS -> PermissionLists.getReceiveSmsPermission()
                PermissionNames.READ_SMS -> PermissionLists.getReadSmsPermission()
                PermissionNames.RECEIVE_WAP_PUSH -> PermissionLists.getReceiveWapPushPermission()
                PermissionNames.RECEIVE_MMS -> PermissionLists.getReceiveMmsPermission()

                // 健康数据权限 - 通用
                PermissionNames.READ_HEALTH_DATA_IN_BACKGROUND -> PermissionLists.getReadHealthDataInBackgroundPermission()
                PermissionNames.READ_HEALTH_DATA_HISTORY -> PermissionLists.getReadHealthDataHistoryPermission()

                // 健康数据权限 - 具体类型（读取）
                PermissionNames.READ_ACTIVE_CALORIES_BURNED -> PermissionLists.getReadActiveCaloriesBurnedPermission()
                PermissionNames.READ_ACTIVITY_INTENSITY -> PermissionLists.getReadActivityIntensityPermission()
                PermissionNames.READ_BASAL_BODY_TEMPERATURE -> PermissionLists.getReadBasalBodyTemperaturePermission()
                PermissionNames.READ_BASAL_METABOLIC_RATE -> PermissionLists.getReadBasalMetabolicRatePermission()
                PermissionNames.READ_BLOOD_GLUCOSE -> PermissionLists.getReadBloodGlucosePermission()
                PermissionNames.READ_BLOOD_PRESSURE -> PermissionLists.getReadBloodPressurePermission()
                PermissionNames.READ_BODY_FAT -> PermissionLists.getReadBodyFatPermission()
                PermissionNames.READ_BODY_TEMPERATURE -> PermissionLists.getReadBodyTemperaturePermission()
                PermissionNames.READ_BODY_WATER_MASS -> PermissionLists.getReadBodyWaterMassPermission()
                PermissionNames.READ_BONE_MASS -> PermissionLists.getReadBoneMassPermission()
                PermissionNames.READ_CERVICAL_MUCUS -> PermissionLists.getReadCervicalMucusPermission()
                PermissionNames.READ_DISTANCE -> PermissionLists.getReadDistancePermission()
                PermissionNames.READ_ELEVATION_GAINED -> PermissionLists.getReadElevationGainedPermission()
                PermissionNames.READ_EXERCISE -> PermissionLists.getReadExercisePermission()
                PermissionNames.READ_EXERCISE_ROUTES -> PermissionLists.getReadExerciseRoutesPermission()
                PermissionNames.READ_FLOORS_CLIMBED -> PermissionLists.getReadFloorsClimbedPermission()
                PermissionNames.READ_HEART_RATE -> PermissionLists.getReadHeartRatePermission()
                PermissionNames.READ_HEART_RATE_VARIABILITY -> PermissionLists.getReadHeartRateVariabilityPermission()
                PermissionNames.READ_HEIGHT -> PermissionLists.getReadHeightPermission()
                PermissionNames.READ_HYDRATION -> PermissionLists.getReadHydrationPermission()
                PermissionNames.READ_INTERMENSTRUAL_BLEEDING -> PermissionLists.getReadIntermenstrualBleedingPermission()
                PermissionNames.READ_LEAN_BODY_MASS -> PermissionLists.getReadLeanBodyMassPermission()
                PermissionNames.READ_MENSTRUATION -> PermissionLists.getReadMenstruationPermission()
                PermissionNames.READ_MINDFULNESS -> PermissionLists.getReadMindfulnessPermission()
                PermissionNames.READ_NUTRITION -> PermissionLists.getReadNutritionPermission()
                PermissionNames.READ_OVULATION_TEST -> PermissionLists.getReadOvulationTestPermission()
                PermissionNames.READ_OXYGEN_SATURATION -> PermissionLists.getReadOxygenSaturationPermission()
                PermissionNames.READ_PLANNED_EXERCISE -> PermissionLists.getReadPlannedExercisePermission()
                PermissionNames.READ_POWER -> PermissionLists.getReadPowerPermission()
                PermissionNames.READ_RESPIRATORY_RATE -> PermissionLists.getReadRespiratoryRatePermission()
                PermissionNames.READ_RESTING_HEART_RATE -> PermissionLists.getReadRestingHeartRatePermission()
                PermissionNames.READ_SEXUAL_ACTIVITY -> PermissionLists.getReadSexualActivityPermission()
                PermissionNames.READ_SKIN_TEMPERATURE -> PermissionLists.getReadSkinTemperaturePermission()
                PermissionNames.READ_SLEEP -> PermissionLists.getReadSleepPermission()
                PermissionNames.READ_SPEED -> PermissionLists.getReadSpeedPermission()
                PermissionNames.READ_STEPS -> PermissionLists.getReadStepsPermission()
                PermissionNames.READ_TOTAL_CALORIES_BURNED -> PermissionLists.getReadTotalCaloriesBurnedPermission()
                PermissionNames.READ_VO2_MAX -> PermissionLists.getReadVo2MaxPermission()
                PermissionNames.READ_WEIGHT -> PermissionLists.getReadWeightPermission()
                PermissionNames.READ_WHEELCHAIR_PUSHES -> PermissionLists.getReadWheelchairPushesPermission()

                // 健康数据权限 - 具体类型（写入）
                PermissionNames.WRITE_ACTIVE_CALORIES_BURNED -> PermissionLists.getWriteActiveCaloriesBurnedPermission()
                PermissionNames.WRITE_ACTIVITY_INTENSITY -> PermissionLists.getWriteActivityIntensityPermission()
                PermissionNames.WRITE_BASAL_BODY_TEMPERATURE -> PermissionLists.getWriteBasalBodyTemperaturePermission()
                PermissionNames.WRITE_BASAL_METABOLIC_RATE -> PermissionLists.getWriteBasalMetabolicRatePermission()
                PermissionNames.WRITE_BLOOD_GLUCOSE -> PermissionLists.getWriteBloodGlucosePermission()
                PermissionNames.WRITE_BLOOD_PRESSURE -> PermissionLists.getWriteBloodPressurePermission()
                PermissionNames.WRITE_BODY_FAT -> PermissionLists.getWriteBodyFatPermission()
                PermissionNames.WRITE_BODY_TEMPERATURE -> PermissionLists.getWriteBodyTemperaturePermission()
                PermissionNames.WRITE_BODY_WATER_MASS -> PermissionLists.getWriteBodyWaterMassPermission()
                PermissionNames.WRITE_BONE_MASS -> PermissionLists.getWriteBoneMassPermission()
                PermissionNames.WRITE_CERVICAL_MUCUS -> PermissionLists.getWriteCervicalMucusPermission()
                PermissionNames.WRITE_DISTANCE -> PermissionLists.getWriteDistancePermission()
                PermissionNames.WRITE_ELEVATION_GAINED -> PermissionLists.getWriteElevationGainedPermission()
                PermissionNames.WRITE_EXERCISE -> PermissionLists.getWriteExercisePermission()
                PermissionNames.WRITE_EXERCISE_ROUTE -> PermissionLists.getWriteExerciseRoutePermission()
                PermissionNames.WRITE_FLOORS_CLIMBED -> PermissionLists.getWriteFloorsClimbedPermission()
                PermissionNames.WRITE_HEART_RATE -> PermissionLists.getWriteHeartRatePermission()
                PermissionNames.WRITE_HEART_RATE_VARIABILITY -> PermissionLists.getWriteHeartRateVariabilityPermission()
                PermissionNames.WRITE_HEIGHT -> PermissionLists.getWriteHeightPermission()
                PermissionNames.WRITE_HYDRATION -> PermissionLists.getWriteHydrationPermission()
                PermissionNames.WRITE_INTERMENSTRUAL_BLEEDING -> PermissionLists.getWriteIntermenstrualBleedingPermission()
                PermissionNames.WRITE_LEAN_BODY_MASS -> PermissionLists.getWriteLeanBodyMassPermission()
                PermissionNames.WRITE_MENSTRUATION -> PermissionLists.getWriteMenstruationPermission()
                PermissionNames.WRITE_MINDFULNESS -> PermissionLists.getWriteMindfulnessPermission()
                PermissionNames.WRITE_NUTRITION -> PermissionLists.getWriteNutritionPermission()
                PermissionNames.WRITE_OVULATION_TEST -> PermissionLists.getWriteOvulationTestPermission()
                PermissionNames.WRITE_OXYGEN_SATURATION -> PermissionLists.getWriteOxygenSaturationPermission()
                PermissionNames.WRITE_PLANNED_EXERCISE -> PermissionLists.getWritePlannedExercisePermission()
                PermissionNames.WRITE_POWER -> PermissionLists.getWritePowerPermission()
                PermissionNames.WRITE_RESPIRATORY_RATE -> PermissionLists.getWriteRespiratoryRatePermission()
                PermissionNames.WRITE_RESTING_HEART_RATE -> PermissionLists.getWriteRestingHeartRatePermission()
                PermissionNames.WRITE_SEXUAL_ACTIVITY -> PermissionLists.getWriteSexualActivityPermission()
                PermissionNames.WRITE_SKIN_TEMPERATURE -> PermissionLists.getWriteSkinTemperaturePermission()
                PermissionNames.WRITE_SLEEP -> PermissionLists.getWriteSleepPermission()
                PermissionNames.WRITE_SPEED -> PermissionLists.getWriteSpeedPermission()
                PermissionNames.WRITE_STEPS -> PermissionLists.getWriteStepsPermission()
                PermissionNames.WRITE_TOTAL_CALORIES_BURNED -> PermissionLists.getWriteTotalCaloriesBurnedPermission()
                PermissionNames.WRITE_VO2_MAX -> PermissionLists.getWriteVo2MaxPermission()
                PermissionNames.WRITE_WEIGHT -> PermissionLists.getWriteWeightPermission()
                PermissionNames.WRITE_WHEELCHAIR_PUSHES -> PermissionLists.getWriteWheelchairPushesPermission()

                // 医疗数据权限
                PermissionNames.READ_MEDICAL_DATA_ALLERGIES_INTOLERANCES -> PermissionLists.getReadMedicalDataAllergiesIntolerancesPermission()
                PermissionNames.READ_MEDICAL_DATA_CONDITIONS -> PermissionLists.getReadMedicalDataConditionsPermission()
                PermissionNames.READ_MEDICAL_DATA_LABORATORY_RESULTS -> PermissionLists.getReadMedicalDataLaboratoryResultsPermission()
                PermissionNames.READ_MEDICAL_DATA_MEDICATIONS -> PermissionLists.getReadMedicalDataMedicationsPermission()
                PermissionNames.READ_MEDICAL_DATA_PERSONAL_DETAILS -> PermissionLists.getReadMedicalDataPersonalDetailsPermission()
                PermissionNames.READ_MEDICAL_DATA_PRACTITIONER_DETAILS -> PermissionLists.getReadMedicalDataPractitionerDetailsPermission()
                PermissionNames.READ_MEDICAL_DATA_PREGNANCY -> PermissionLists.getReadMedicalDataPregnancyPermission()
                PermissionNames.READ_MEDICAL_DATA_PROCEDURES -> PermissionLists.getReadMedicalDataProceduresPermission()
                PermissionNames.READ_MEDICAL_DATA_SOCIAL_HISTORY -> PermissionLists.getReadMedicalDataSocialHistoryPermission()
                PermissionNames.READ_MEDICAL_DATA_VACCINES -> PermissionLists.getReadMedicalDataVaccinesPermission()
                PermissionNames.READ_MEDICAL_DATA_VISITS -> PermissionLists.getReadMedicalDataVisitsPermission()
                PermissionNames.READ_MEDICAL_DATA_VITAL_SIGNS -> PermissionLists.getReadMedicalDataVitalSignsPermission()
                PermissionNames.WRITE_MEDICAL_DATA -> PermissionLists.getWriteMedicalDataPermission()

                else -> throw UnsupportedOperationException("不支持的权限: $it")
            }
        }
    }
}