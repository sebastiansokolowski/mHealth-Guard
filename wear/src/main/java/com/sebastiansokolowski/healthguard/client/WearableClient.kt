package com.sebastiansokolowski.healthguard.client

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.*
import com.google.gson.Gson
import com.sebastiansokolowski.healthguard.BuildConfig
import com.sebastiansokolowski.shared.DataClientPaths
import com.sebastiansokolowski.shared.DataClientPaths.Companion.HEALTH_EVENT_MAP_JSON
import com.sebastiansokolowski.shared.DataClientPaths.Companion.HEALTH_EVENT_MAP_PATH
import com.sebastiansokolowski.shared.DataClientPaths.Companion.SENSOR_EVENTS_MAP_ARRAY_LIST
import com.sebastiansokolowski.shared.DataClientPaths.Companion.SENSOR_EVENTS_MAP_PATH
import com.sebastiansokolowski.shared.DataClientPaths.Companion.SUPPORTED_HEALTH_EVENTS_MAP_JSON
import com.sebastiansokolowski.shared.DataClientPaths.Companion.SUPPORTED_HEALTH_EVENTS_MAP_PATH
import com.sebastiansokolowski.shared.dataModel.HealthEvent
import com.sebastiansokolowski.shared.dataModel.SensorEvent
import com.sebastiansokolowski.shared.dataModel.SupportedHealthEventTypes


/**
 * Created by Sebastian Sokołowski on 16.07.18.
 */
class WearableClient(context: Context) {
    private val TAG = javaClass.canonicalName

    private val maxSizeOfDataToSend = 500

    private val dataClient: DataClient = Wearable.getDataClient(context)
    private val messageClient: MessageClient = Wearable.getMessageClient(context)
    private val capabilityClient: CapabilityClient = Wearable.getCapabilityClient(context)

    fun sendMeasurementEvent(state: Boolean) {
        Log.d(TAG, "sendMeasurementEvent state: $state")

        if (state) {
            sendMessage(DataClientPaths.START_MEASUREMENT_PATH)
        } else {
            sendMessage(DataClientPaths.STOP_MEASUREMENT_PATH)
        }
    }

    fun sendSensorEvents(events: List<SensorEvent>, urgent: Boolean) {
        Log.d(TAG, "sendSensorEvents size=${events.size}")

        val gson = GsonBuilder()
                .registerTypeAdapter(FloatArray::class.java, SensorEventValuesSerializer())
                .create()

        events.chunked(maxSizeOfDataToSend).iterator().forEach {
            Log.v(TAG, "sendSensorEvents chunked size=${it.size}")

            val data = ArrayList<String>()
            it.forEach {
                data.add(gson.toJson(it))
            }

            val putDataMapReq = PutDataMapRequest.createWithAutoAppendedId(SENSOR_EVENTS_MAP_PATH)
            putDataMapReq.dataMap.apply {
                putStringArrayList(SENSOR_EVENTS_MAP_ARRAY_LIST, data)
            }

            send(putDataMapReq, urgent)
        }
    }

    fun requestStartMeasurement() {
        Log.d(TAG, "requestStartMeasurement")

        sendMessage(DataClientPaths.REQUEST_START_MEASUREMENT_PATH)
    }

    fun sendSupportedHealthEvents(supportedHealthEventTypes: SupportedHealthEventTypes) {
        Log.d(TAG, "sendSupportedHealthEvents healthEventTypesSupported: $supportedHealthEventTypes")

        val putDataMapReq = PutDataMapRequest.createWithAutoAppendedId(SUPPORTED_HEALTH_EVENTS_MAP_PATH)
        putDataMapReq.dataMap.apply {
            putString(SUPPORTED_HEALTH_EVENTS_MAP_JSON, Gson().toJson(supportedHealthEventTypes))
        }

        send(putDataMapReq, true)
    }

    fun sendHealthEvent(healthEvent: HealthEvent) {
        Log.d(TAG, "sendHealthEvent sensorEvent=${healthEvent.sensorEvent}")

        val putDataMapReq = PutDataMapRequest.createWithAutoAppendedId(HEALTH_EVENT_MAP_PATH)
        putDataMapReq.dataMap.apply {
            putString(HEALTH_EVENT_MAP_JSON, Gson().toJson(healthEvent))
        }

        send(putDataMapReq, true)
    }

    private fun sendMessage(message: String) {
        Thread(Runnable {
            val task = capabilityClient.getCapability(
                    DataClientPaths.NODE_CAPABILITY,
                    CapabilityClient.FILTER_REACHABLE)
            val capabilityInfo: CapabilityInfo = Tasks.await(task)

            if (capabilityInfo.nodes.isNotEmpty()) {
                val nodeId = capabilityInfo.nodes.iterator().next()
                messageClient.sendMessage(nodeId.id, message, null).apply {
                    if (BuildConfig.DEBUG) {
                        addOnSuccessListener {
                            Log.d(TAG, "Success sent message")
                        }
                        addOnFailureListener { ex ->
                            Log.d(TAG, "Error sending message $ex")
                        }
                    }
                }
            } else {
                Log.d(TAG, "missing node!")
            }
        }).start()
    }

    private fun send(request: PutDataMapRequest, urgent: Boolean) {
        var putDataReq = request
                .asPutDataRequest()
        if (urgent) {
            putDataReq = putDataReq.setUrgent()
        }

        val dataItemTask = dataClient.putDataItem(putDataReq)

        if (BuildConfig.DEBUG) {
            dataItemTask.addOnSuccessListener {
                Log.v(TAG, "Success sent data path:${request.uri} urgent:$urgent")
            }
            dataItemTask.addOnFailureListener { ex ->
                Log.v(TAG, "Error sending data $ex")
            }
        }
    }
}
