package com.sebastiansokolowski.shared

/**
 * Created by Sebastian Sokołowski on 07.07.19.
 */
class SettingsSharedPreferences {
    companion object {
        // mobile
        const val CONTACTS = "contacts"
        const val HEALTH_EVENTS = "health_events"
        const val SUPPORTED_HEALTH_EVENTS = "supported_health_events"
        const val FIRST_SETUP_COMPLETED = "first_setup_completed"
        const val ANDROID_NOTIFICATIONS = "android_notifications_enabled"
        const val SMS_NOTIFICATIONS = "sms_notifications_enabled"
        const val SMS_USER_LOCATION = "sms_user_location_enabled"
        // mobile and wear
        const val SAMPLING_US = "sampling_us"
        const val HEART_RATE_ANOMALY_STEP_DETECTOR_TIMEOUT_MIN = "heart_rate_anomaly_step_detector_timeout_min"
        const val HEART_RATE_ANOMALY_MIN_THRESHOLD = "heart_rate_anomaly_min_threshold"
        const val HEART_RATE_ANOMALY_MAX_THRESHOLD_DURING_INACTIVITY = "heart_rate_anomaly_max_threshold_during_inactivity"
        const val HEART_RATE_ANOMALY_MAX_THRESHOLD_DURING_ACTIVITY = "heart_rate_anomaly_max_threshold_during_activity"
        const val FALL_THRESHOLD = "fall_threshold"
        const val FALL_SAMPLE_COUNT = "fall_sample_count"
        const val FALL_STEP_DETECTOR = "fall_step_detector"
        const val FALL_STEP_DETECTOR_TIMEOUT_S = "fall_step_detector_timeout_s"
        const val FALL_TIME_OF_INACTIVITY_S = "fall_time_of_inactivity_s"
        const val FALL_ACTIVITY_THRESHOLD = "fall_activity_threshold"
        const val EPILEPSY_THRESHOLD = "epilepsy_threshold"
        const val EPILEPSY_TIME = "epilepsy_time"
        const val EPILEPSY_PERCENT_OF_POSITIVE_EVENTS = "epilepsy_percent_of_positive_events"


        // wear
        const val SENSORS = "sensors"
    }
}