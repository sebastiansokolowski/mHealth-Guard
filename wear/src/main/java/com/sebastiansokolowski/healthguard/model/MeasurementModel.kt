package com.sebastiansokolowski.healthguard.model

import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.PowerManager
import android.util.Log
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.DataMapItem
import com.google.gson.Gson
import com.sebastiansokolowski.healthguard.client.WearableClient
import com.sebastiansokolowski.shared.DataClientPaths
import com.sebastiansokolowski.shared.dataModel.SupportedHealthEventTypes
import com.sebastiansokolowski.shared.dataModel.settings.MeasurementSettings
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.ReplaySubject
import java.util.concurrent.TimeUnit

/**
 * Created by Sebastian Sokołowski on 06.07.19.
 */
class MeasurementModel(val sensorDataModel: SensorDataModel, val healthGuardModel: HealthGuardModel, private val sensorManager: SensorManager, powerManager: PowerManager, private val wearableClient: WearableClient) {
    private val TAG = javaClass.canonicalName

    var measurementRunning = false
    var wakeLock: PowerManager.WakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "HealthGuard::MeasurementWakeLock")

    val measurementStateObservable: BehaviorSubject<Boolean> = BehaviorSubject.create()

    fun notifySupportedHealthEvents() {
        val sensors = sensorManager.getSensorList(Sensor.TYPE_ALL)
        val supportedHealthEvents = healthGuardModel.getSupportedHealthEvents(sensors)
        wearableClient.sendSupportedHealthEvents(SupportedHealthEventTypes(supportedHealthEvents))
    }

    private fun changeMeasurementState(state: Boolean) {
        if (measurementRunning == state) {
            return
        }
        measurementRunning = state
        if (state) {
            sensorDataModel.heartRateObservable = ReplaySubject.createWithSize(10)
        } else {
            sensorDataModel.heartRateObservable.onComplete()
        }
        measurementStateObservable.onNext(measurementRunning)
        notifyMeasurementState()
    }

    fun notifyMeasurementState() {
        wearableClient.sendMeasurementEvent(measurementRunning)
    }

    fun toggleMeasurementState() {
        if (measurementRunning) {
            stopMeasurement()
        } else {
            wearableClient.requestStartMeasurement()
        }
    }

    @SuppressLint("WakelockTimeout")
    fun startMeasurement(measurementSettings: MeasurementSettings) {
        if (measurementRunning) {
            return
        }

        val healthEngines = healthGuardModel.getHealthEngines(measurementSettings.healthEvents)
        val sensors = healthEngines.flatMap { it.requiredSensors() }.toSet()
        val samplingPeriodUs = TimeUnit.MILLISECONDS.toMicros(measurementSettings.samplingMs.toLong()).toInt()

        changeMeasurementState(true)
        healthGuardModel.startEngines(measurementSettings)
        sensorDataModel.registerSensors(measurementSettings.measurementId, sensors, samplingPeriodUs)
        wakeLock.acquire()
    }

    fun stopMeasurement() {
        if (!measurementRunning) {
            return
        }
        changeMeasurementState(false)
        healthGuardModel.stopEngines()
        sensorDataModel.unregisterSensors()
        wakeLock.release()
    }

    fun onDataChanged(dataItem: DataItem) {
        when ("/" + dataItem.uri.pathSegments.getOrNull(0)?.removePrefix("/")) {
            DataClientPaths.MEASUREMENT_START_DATA_PATH -> {
                DataMapItem.fromDataItem(dataItem).dataMap.apply {
                    val json = getString(DataClientPaths.MEASUREMENT_START_DATA_JSON)
                    val measurementSettings = Gson().fromJson(json, MeasurementSettings::class.java)

                    Log.d(TAG, "measurementSettings=$measurementSettings")

                    if (measurementRunning) {
                        stopMeasurement()
                    }
                    startMeasurement(measurementSettings)
                }
            }
        }
    }
}