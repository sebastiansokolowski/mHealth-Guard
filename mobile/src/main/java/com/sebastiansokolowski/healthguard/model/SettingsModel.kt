package com.sebastiansokolowski.healthguard.model

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.sebastiansokolowski.shared.SettingsSharedPreferences
import com.sebastiansokolowski.shared.dataModel.HealthEventType
import com.sebastiansokolowski.shared.dataModel.settings.EpilepsySettings
import com.sebastiansokolowski.shared.dataModel.settings.FallSettings
import com.sebastiansokolowski.shared.dataModel.settings.HeartRateAnomalySettings
import com.sebastiansokolowski.shared.dataModel.settings.MeasurementSettings

/**
 * Created by Sebastian Sokołowski on 23.08.19.
 */
class SettingsModel(private val sharedPreferences: SharedPreferences) {

    init {
        setDefaultMeasurementSettings()
    }

    private fun setDefaultMeasurementSettings() {
        val defaultMeasurementSettings = MeasurementSettings()

        sharedPreferences.edit()?.apply {
            putSettingsWhenDoesNotExist(this, SettingsSharedPreferences.ANDROID_NOTIFICATIONS, true)
            putSettingsWhenDoesNotExist(this, SettingsSharedPreferences.SMS_NOTIFICATIONS, false)
            putSettingsWhenDoesNotExist(this, SettingsSharedPreferences.SMS_USER_LOCATION, false)
            putSettingsWhenDoesNotExist(this, SettingsSharedPreferences.SAMPLING_US, defaultMeasurementSettings.samplingMs)
            putSettingsWhenDoesNotExist(this, SettingsSharedPreferences.BATTERY_SAVER, defaultMeasurementSettings.batterySaver)
            putSettingsWhenDoesNotExist(this, SettingsSharedPreferences.TEST_MODE, defaultMeasurementSettings.testMode)
            //heart rate anomaly
            putSettingsWhenDoesNotExist(this, SettingsSharedPreferences.HEART_RATE_ANOMALY_ACTIVITY_DETECTOR_TIMEOUT_MIN, defaultMeasurementSettings.heartRateAnomalySettings.activityDetectorTimeoutMin)
            putSettingsWhenDoesNotExist(this, SettingsSharedPreferences.HEART_RATE_ANOMALY_ACTIVITY_DETECTOR_THRESHOLD, defaultMeasurementSettings.heartRateAnomalySettings.activityDetectorThreshold)
            putSettingsWhenDoesNotExist(this, SettingsSharedPreferences.HEART_RATE_ANOMALY_MIN_THRESHOLD, defaultMeasurementSettings.heartRateAnomalySettings.minThreshold)
            putSettingsWhenDoesNotExist(this, SettingsSharedPreferences.HEART_RATE_ANOMALY_MAX_THRESHOLD_DURING_INACTIVITY, defaultMeasurementSettings.heartRateAnomalySettings.maxThresholdDuringInactivity)
            putSettingsWhenDoesNotExist(this, SettingsSharedPreferences.HEART_RATE_ANOMALY_MAX_THRESHOLD_DURING_ACTIVITY, defaultMeasurementSettings.heartRateAnomalySettings.maxThresholdDuringActivity)
            //fall
            putSettingsWhenDoesNotExist(this, SettingsSharedPreferences.FALL_THRESHOLD, defaultMeasurementSettings.fallSettings.threshold)
            putSettingsWhenDoesNotExist(this, SettingsSharedPreferences.FALL_SAMPLE_COUNT, defaultMeasurementSettings.fallSettings.sampleCount)
            putSettingsWhenDoesNotExist(this, SettingsSharedPreferences.FALL_STEP_DETECTOR, defaultMeasurementSettings.fallSettings.stepDetector)
            putSettingsWhenDoesNotExist(this, SettingsSharedPreferences.FALL_STEP_DETECTOR_TIMEOUT_S, defaultMeasurementSettings.fallSettings.stepDetectorTimeoutS)
            putSettingsWhenDoesNotExist(this, SettingsSharedPreferences.FALL_INACTIVITY_DETECTOR, defaultMeasurementSettings.fallSettings.inactivityDetector)
            putSettingsWhenDoesNotExist(this, SettingsSharedPreferences.FALL_INACTIVITY_DETECTOR_TIMEOUT_S, defaultMeasurementSettings.fallSettings.inactivityDetectorTimeoutS)
            putSettingsWhenDoesNotExist(this, SettingsSharedPreferences.FALL_INACTIVITY_DETECTOR_THRESHOLD, defaultMeasurementSettings.fallSettings.inactivityDetectorThreshold)
            //epilepsy
            putSettingsWhenDoesNotExist(this, SettingsSharedPreferences.EPILEPSY_THRESHOLD, defaultMeasurementSettings.epilepsySettings.threshold)
            putSettingsWhenDoesNotExist(this, SettingsSharedPreferences.EPILEPSY_TIME, defaultMeasurementSettings.epilepsySettings.timeS)
            putSettingsWhenDoesNotExist(this, SettingsSharedPreferences.EPILEPSY_PERCENT_OF_POSITIVE_EVENTS, defaultMeasurementSettings.epilepsySettings.percentOfPositiveSignals)

            apply()
        }
    }

    private fun putSettingsWhenDoesNotExist(editor: SharedPreferences.Editor, key: String, value: Boolean) {
        if (!sharedPreferences.contains(key)) {
            editor.putBoolean(key, value)
        }
    }

    private fun putSettingsWhenDoesNotExist(editor: SharedPreferences.Editor, key: String, value: Int) {
        if (!sharedPreferences.contains(key)) {
            editor.putInt(key, value)
        }
    }

    //get

    fun getMeasurementSettings(): MeasurementSettings {
        val defaultMeasurementSettings = MeasurementSettings()

        val samplingMs = sharedPreferences.getInt(SettingsSharedPreferences.SAMPLING_US, defaultMeasurementSettings.samplingMs)
        val batterySaver = sharedPreferences.getBoolean(SettingsSharedPreferences.BATTERY_SAVER, defaultMeasurementSettings.batterySaver)
        val testMode = sharedPreferences.getBoolean(SettingsSharedPreferences.TEST_MODE, defaultMeasurementSettings.testMode)
        //heart rate anomaly
        val heartRateActivityDetectorTimeoutInMin = sharedPreferences.getInt(SettingsSharedPreferences.HEART_RATE_ANOMALY_ACTIVITY_DETECTOR_TIMEOUT_MIN, defaultMeasurementSettings.heartRateAnomalySettings.activityDetectorTimeoutMin)
        val heartRateActivityDetectorThreshold = sharedPreferences.getInt(SettingsSharedPreferences.HEART_RATE_ANOMALY_ACTIVITY_DETECTOR_THRESHOLD, defaultMeasurementSettings.heartRateAnomalySettings.activityDetectorThreshold)
        val heartRateMinThreshold = sharedPreferences.getInt(SettingsSharedPreferences.HEART_RATE_ANOMALY_MIN_THRESHOLD, defaultMeasurementSettings.heartRateAnomalySettings.minThreshold)
        val heartRateMaxThresholdDuringInactivity = sharedPreferences.getInt(SettingsSharedPreferences.HEART_RATE_ANOMALY_MAX_THRESHOLD_DURING_INACTIVITY, defaultMeasurementSettings.heartRateAnomalySettings.maxThresholdDuringInactivity)
        val heartRateMaxThresholdDuringActivity = sharedPreferences.getInt(SettingsSharedPreferences.HEART_RATE_ANOMALY_MAX_THRESHOLD_DURING_ACTIVITY, defaultMeasurementSettings.heartRateAnomalySettings.maxThresholdDuringActivity)
        //fall
        val fallThreshold = sharedPreferences.getInt(SettingsSharedPreferences.FALL_THRESHOLD, defaultMeasurementSettings.fallSettings.threshold)
        val fallSampleCount = sharedPreferences.getInt(SettingsSharedPreferences.FALL_SAMPLE_COUNT, defaultMeasurementSettings.fallSettings.sampleCount)
        val fallStepDetector = sharedPreferences.getBoolean(SettingsSharedPreferences.FALL_STEP_DETECTOR, defaultMeasurementSettings.fallSettings.stepDetector)
        val fallStepDetectorTimeoutS = sharedPreferences.getInt(SettingsSharedPreferences.FALL_STEP_DETECTOR_TIMEOUT_S, defaultMeasurementSettings.fallSettings.stepDetectorTimeoutS)
        val fallActivityDetector = sharedPreferences.getBoolean(SettingsSharedPreferences.FALL_INACTIVITY_DETECTOR, defaultMeasurementSettings.fallSettings.inactivityDetector)
        val fallActivityDetectorTimeoutS = sharedPreferences.getInt(SettingsSharedPreferences.FALL_INACTIVITY_DETECTOR_TIMEOUT_S, defaultMeasurementSettings.fallSettings.inactivityDetectorTimeoutS)
        val fallActivityDetectorThreshold = sharedPreferences.getInt(SettingsSharedPreferences.FALL_INACTIVITY_DETECTOR_THRESHOLD, defaultMeasurementSettings.fallSettings.inactivityDetectorThreshold)
        //epilepsy
        val epilepsyThreshold = sharedPreferences.getInt(SettingsSharedPreferences.EPILEPSY_THRESHOLD, defaultMeasurementSettings.epilepsySettings.threshold)
        val epilepsyTime = sharedPreferences.getInt(SettingsSharedPreferences.EPILEPSY_TIME, defaultMeasurementSettings.epilepsySettings.timeS)
        val epilepsyPercentOfPositiveEvents = sharedPreferences.getInt(SettingsSharedPreferences.EPILEPSY_PERCENT_OF_POSITIVE_EVENTS, defaultMeasurementSettings.epilepsySettings.percentOfPositiveSignals)


        val healthEvents = getHealthEvents()

        val heartRateAnomalySettings = HeartRateAnomalySettings(heartRateActivityDetectorTimeoutInMin, heartRateActivityDetectorThreshold,
                heartRateMinThreshold,
                heartRateMaxThresholdDuringInactivity, heartRateMaxThresholdDuringActivity)
        val fallSettings = FallSettings(fallThreshold, fallSampleCount,
                fallStepDetector, fallStepDetectorTimeoutS,
                fallActivityDetector, fallActivityDetectorTimeoutS, fallActivityDetectorThreshold)
        val epilepsySettings = EpilepsySettings(epilepsyThreshold, epilepsyTime, epilepsyPercentOfPositiveEvents)

        return MeasurementSettings(samplingMs, healthEvents, batterySaver, testMode,
                heartRateAnomalySettings,
                fallSettings,
                epilepsySettings)
    }

    private fun getHealthEvents(): Set<HealthEventType> {
        val healthEvents = mutableSetOf<HealthEventType>()

        val healthEventsStringSet = sharedPreferences.getStringSet(SettingsSharedPreferences.HEALTH_EVENTS, emptySet())
                ?: emptySet()
        healthEventsStringSet.forEach { healthEventTypeName ->
            try {
                HealthEventType.valueOf(healthEventTypeName).let { healthEventType ->
                    healthEvents.add(healthEventType)
                }
            } catch (e: IllegalArgumentException) {
            }
        }

        return healthEvents
    }

    fun getSupportedHealthEventTypes(): List<HealthEventType> {
        val healthEventsName = sharedPreferences
                .getStringSet(SettingsSharedPreferences.SUPPORTED_HEALTH_EVENTS, emptySet())
                ?: emptySet()

        return healthEventsName.mapNotNull {
            try {
                HealthEventType.valueOf(it)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }

    fun getPhoneNumbers(): MutableSet<String>? {
        return sharedPreferences.getStringSet(SettingsSharedPreferences.CONTACTS, mutableSetOf())
    }

    fun isFirstSetupCompleted(): Boolean {
        return sharedPreferences.getBoolean(SettingsSharedPreferences.FIRST_SETUP_COMPLETED, false)
    }

    fun isAndroidNotificationEnabled(): Boolean {
        return sharedPreferences.getBoolean(SettingsSharedPreferences.ANDROID_NOTIFICATIONS, true)
    }

    fun isSmsNotificationEnabled(): Boolean {
        return sharedPreferences.getBoolean(SettingsSharedPreferences.SMS_NOTIFICATIONS, false)
    }

    fun isSmsUserLocationEnabled(): Boolean {
        return sharedPreferences.getBoolean(SettingsSharedPreferences.SMS_USER_LOCATION, false)
    }

    //set

    @SuppressLint("ApplySharedPref")
    fun saveSetting(key: String, value: Boolean) {
        sharedPreferences.edit()?.apply {
            putBoolean(key, value)
            commit()
        }
    }

    @SuppressLint("ApplySharedPref")
    fun saveSupportedHealthEvents(healthEvents: Set<HealthEventType>) {
        sharedPreferences.edit()?.apply {
            val values = healthEvents.map { sensor -> sensor.name }.toSet()
            putStringSet(SettingsSharedPreferences.SUPPORTED_HEALTH_EVENTS, values)
            commit()
        }
    }

    @SuppressLint("ApplySharedPref")
    fun saveHealthEvents(healthEvents: List<HealthEventType>) {
        sharedPreferences.edit()?.apply {
            val values = healthEvents.map { sensor -> sensor.name }.toSet()
            putStringSet(SettingsSharedPreferences.HEALTH_EVENTS, values)
            commit()
        }
    }

    @SuppressLint("ApplySharedPref")
    fun setFirstSetupCompleted() {
        sharedPreferences.edit()?.apply {
            putBoolean(SettingsSharedPreferences.FIRST_SETUP_COMPLETED, true)
            commit()
        }
    }
}