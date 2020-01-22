package com.sebastiansokolowski.healthcarewatch.model

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.util.Log
import com.google.android.gms.wearable.*
import com.google.gson.Gson
import com.sebastiansokolowski.healthcarewatch.client.WearableDataClient
import com.sebastiansokolowski.healthcarewatch.db.entity.HealthCareEvent
import com.sebastiansokolowski.healthcarewatch.db.entity.SensorEventData
import com.sebastiansokolowski.healthcarewatch.service.MeasurementService
import com.sebastiansokolowski.shared.DataClientPaths
import com.sebastiansokolowski.shared.dataModel.HealthCareEventType
import com.sebastiansokolowski.shared.dataModel.HealthSensorEvent
import com.sebastiansokolowski.shared.dataModel.SupportedHealthCareEventTypes
import io.objectbox.BoxStore
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit


/**
 * Created by Sebastian Sokołowski on 03.02.19.
 */
class SensorDataModel(val context: Context, private val wearableDataClient: WearableDataClient, private val notificationModel: NotificationModel, private val boxStore: BoxStore, private val settingsModel: SettingsModel) : DataClient.OnDataChangedListener {
    private val TAG = javaClass.canonicalName

    var measurementRunning: Boolean = false
    private var saveDataDisposable: Disposable? = null

    val sensorsObservable: PublishSubject<SensorEventData> = PublishSubject.create()
    val heartRateObservable: BehaviorSubject<SensorEventData> = BehaviorSubject.create()
    val measurementStateObservable: BehaviorSubject<Boolean> = BehaviorSubject.create()
    val supportedHealthCareEventsObservable: PublishSubject<Set<HealthCareEventType>> = PublishSubject.create()

    init {
        Wearable.getDataClient(context).addListener(this)
    }

    private fun notifyHeartRateObservable(sensorEventData: SensorEventData) {
        heartRateObservable.onNext(sensorEventData)
    }

    private fun notifySensorsObservable(sensorEventData: SensorEventData) {
        sensorsObservable.onNext(sensorEventData)
    }

    private fun notifyMeasurementStateObservable(measurementState: Boolean) {
        measurementStateObservable.onNext(measurementState)
    }

    private fun notifySupportedHealthCareEventsObservable(healthCareEvents: Set<HealthCareEventType>) {
        supportedHealthCareEventsObservable.onNext(healthCareEvents)
        supportedHealthCareEventsObservable.onComplete()
    }

    private fun changeMeasurementState(state: Boolean) {
        if (measurementRunning == state) {
            return
        }
        measurementRunning = state
        measurementStateObservable.onNext(measurementRunning)
        val serviceIntent = Intent(context, MeasurementService::class.java)
        if (state) {
            context.startService(serviceIntent)
        } else {
            context.stopService(serviceIntent)
        }
    }

    fun toggleMeasurementState() {
        if (measurementRunning) {
            wearableDataClient.sendStopMeasurementEvent()
            stopMeasurement()
        } else {
            requestStartMeasurement()
        }
    }

    fun requestStartMeasurement() {
        val measurementSettings = settingsModel.getMeasurementSettings()
        wearableDataClient.sendStartMeasurementEvent(measurementSettings)
    }

    fun startMeasurement() {
        notifyMeasurementStateObservable(true)
        if (measurementRunning) {
            return
        }
        changeMeasurementState(true)
        saveDataToDatabase()
    }

    fun stopMeasurement() {
        notifyMeasurementStateObservable(false)
        if (!measurementRunning) {
            return
        }
        changeMeasurementState(false)
    }

    private fun saveDataToDatabase() {
        saveDataDisposable = sensorsObservable
                .subscribeOn(Schedulers.io())
                .buffer(10, TimeUnit.SECONDS)
                .subscribe {
                    Log.d(TAG, "save data to database")
                    val eventBox = boxStore.boxFor(SensorEventData::class.java)
                    eventBox.put(it)

                    synchronized(this) {
                        if (!measurementRunning) {
                            saveDataDisposable?.dispose()
                        }
                    }
                }
    }

    override fun onDataChanged(dataEvent: DataEventBuffer) {
        dataEvent.forEach { event ->
            Log.d(TAG, "onDataChanged path:${event.dataItem.uri.path}")
            if (event.type != DataEvent.TYPE_CHANGED) {
                Log.d(TAG, "type not changed")
                return
            }
            when (event.dataItem.uri.path) {
                DataClientPaths.HEALTH_SENSOR_MAP_PATH -> {
                    DataMapItem.fromDataItem(event.dataItem).dataMap.apply {
                        val json = getString(DataClientPaths.HEALTH_SENSOR_MAP_JSON)
                        val sensorEvent = Gson().fromJson(json, HealthSensorEvent::class.java)

                        val sensorEventData = createSensorEventDataEntity(sensorEvent)

                        if (sensorEventData.type == Sensor.TYPE_HEART_RATE) {
                            notifyHeartRateObservable(sensorEventData)
                        }
                        notifySensorsObservable(sensorEventData)

                        Log.d(TAG, "sensorEvent=$sensorEventData")
                    }
                }
                DataClientPaths.HEALTH_CARE_MAP_PATH -> {
                    DataMapItem.fromDataItem(event.dataItem).dataMap.apply {
                        val json = getString(DataClientPaths.HEALTH_CARE_MAP_JSON)
                        val healthCareEvent = Gson().fromJson(json, com.sebastiansokolowski.shared.dataModel.HealthCareEvent::class.java)

                        val healthCareEventEntity = createHealthCareEvent(healthCareEvent)

                        val eventBox = boxStore.boxFor(HealthCareEvent::class.java)
                        eventBox.put(healthCareEventEntity)

                        notificationModel.notifyHealthCareEvent(healthCareEventEntity)

                        Log.d(TAG, "healthCareEvent=$healthCareEventEntity")
                    }
                }
                DataClientPaths.SUPPORTED_HEALTH_CARE_EVENTS_MAP_PATH -> {
                    DataMapItem.fromDataItem(event.dataItem).dataMap.apply {
                        val json = getString(DataClientPaths.HEALTH_CARE_MAP_JSON)
                        val healthCareEventTypesSupported = Gson().fromJson(json, SupportedHealthCareEventTypes::class.java)

                        notifySupportedHealthCareEventsObservable(healthCareEventTypesSupported.supportedTypes)

                        Log.d(TAG, "supported health care events :$healthCareEventTypesSupported")
                    }
                }
            }
        }
    }

    private fun createSensorEventDataEntity(healthSensorEvent: HealthSensorEvent): SensorEventData {
        return SensorEventData().apply {
            this.type = healthSensorEvent.type
            this.accuracy = healthSensorEvent.accuracy
            this.timestamp = healthSensorEvent.timestamp
            this.values = healthSensorEvent.values
        }
    }

    private fun createHealthCareEvent(healthCareEvent: com.sebastiansokolowski.shared.dataModel.HealthCareEvent): HealthCareEvent {
        return HealthCareEvent().apply {
            this.careEvent = healthCareEvent.healthCareEventType
            this.sensorEventData.target = createSensorEventDataEntity(healthCareEvent.healthSensorEvent)
        }
    }
}