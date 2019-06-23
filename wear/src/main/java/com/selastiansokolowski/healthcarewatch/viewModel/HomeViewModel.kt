package com.selastiansokolowski.healthcarewatch.viewModel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.selastiansokolowski.healthcarewatch.model.SensorDataModel
import io.reactivex.BackpressureStrategy
import javax.inject.Inject

/**
 * Created by Sebastian Sokołowski on 09.07.18.
 */
class HomeViewModel
@Inject constructor(private val sensorDataModel: SensorDataModel) : ViewModel() {

    val measurementState: LiveData<Boolean> by lazy {
        initMeasurementStateLiveData()
    }

    val heartRate: LiveData<String> by lazy {
        initHeartRateLiveData()
    }

    private fun initHeartRateLiveData(): LiveData<String> {
        val sensorDataModelFlowable = sensorDataModel.heartRateObservable.toFlowable(BackpressureStrategy.LATEST)
        val sensorDataModelLiveData = LiveDataReactiveStreams.fromPublisher(sensorDataModelFlowable)
        return Transformations.map(sensorDataModelLiveData) {
            var result = ""
            it?.let { heartRate ->
                result = heartRate.toString()
            }
            return@map result
        }
    }

    private fun initMeasurementStateLiveData(): LiveData<Boolean> {
        val measurementStateFlowable = sensorDataModel.measurementStateObservable.toFlowable(BackpressureStrategy.LATEST)
        return LiveDataReactiveStreams.fromPublisher(measurementStateFlowable)
    }

    fun toggleMeasurementState() {
        sensorDataModel.toggleMeasurementState()
    }
}