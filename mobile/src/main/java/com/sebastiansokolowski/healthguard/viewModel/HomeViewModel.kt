package com.sebastiansokolowski.healthguard.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import android.net.Uri
import com.sebastiansokolowski.healthguard.db.entity.HealthEventEntity
import com.sebastiansokolowski.healthguard.db.entity.HealthEventEntity_
import com.sebastiansokolowski.healthguard.db.entity.SensorEventEntity
import com.sebastiansokolowski.healthguard.model.MeasurementModel
import com.sebastiansokolowski.healthguard.model.SensorDataModel
import com.sebastiansokolowski.healthguard.model.SetupModel
import com.sebastiansokolowski.healthguard.model.ShareDataModel
import com.sebastiansokolowski.healthguard.util.SingleEvent
import com.sebastiansokolowski.healthguard.viewModel.sensorData.SensorEventViewModel
import com.sebastiansokolowski.shared.dataModel.DataExport
import io.objectbox.BoxStore
import io.objectbox.rx.RxQuery
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Sebastian Sokołowski on 10.03.19.
 */
class HomeViewModel
@Inject constructor(private val setupModel: SetupModel, private val measurementModel: MeasurementModel, private val sensorDataModel: SensorDataModel, private val shareDataModel: ShareDataModel, boxStore: BoxStore) : SensorEventViewModel(boxStore) {

    private val disposables = CompositeDisposable()
    private var heartRateDisposable: Disposable? = null

    val setupState: LiveData<SetupModel.SetupStatus> by lazy {
        initSetupState()
    }

    val measurementState: LiveData<Boolean> by lazy {
        initMeasurementStateLiveData()
    }

    val heartRate: MutableLiveData<String> = MutableLiveData()

    val filesToShare: LiveData<SingleEvent<ArrayList<Uri>>> by lazy {
        initFilesToShareLiveData()
    }

    init {
        initEventsView()
        initHeartRate()
    }

    override fun getHealthEventsObservable(): Observable<MutableList<HealthEventEntity>> {
        val query = healthEventEntityBox.query()
                .orderDesc(HealthEventEntity_.__ID_PROPERTY)
                .build()

        return RxQuery.observable(query)
    }

    override fun getSensorEventsObservable(): Observable<MutableList<SensorEventEntity>>? {
        return null
    }

    private fun initHeartRate() {
        measurementModel.measurementStateObservable
                .subscribeOn(Schedulers.io())
                .filter { it }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    heartRateDisposable?.dispose()
                    sensorDataModel.heartRateObservable
                            .doOnComplete {
                                heartRate.postValue("")
                            }
                            .subscribe { sensorEventData ->
                                val result = sensorEventData.value.toInt().toString()
                                heartRate.postValue(result)
                            }.let {
                                heartRateDisposable = it
                            }
                }.let {
                    disposables.add(it)
                }
    }

    private fun initSetupState(): LiveData<SetupModel.SetupStatus> {
        val setupStateFlowable = setupModel.setupStatus.toFlowable(BackpressureStrategy.LATEST)
        return LiveDataReactiveStreams.fromPublisher(setupStateFlowable)
    }

    private fun initMeasurementStateLiveData(): LiveData<Boolean> {
        val measurementStateFlowable = measurementModel.measurementStateObservable.toFlowable(BackpressureStrategy.LATEST)
        return LiveDataReactiveStreams.fromPublisher(measurementStateFlowable)
    }

    private fun initFilesToShareLiveData(): LiveData<SingleEvent<ArrayList<Uri>>> {
        val filesToShareFlowable = shareDataModel.filesToShareObservable.toFlowable(BackpressureStrategy.LATEST)
        return LiveDataReactiveStreams.fromPublisher(filesToShareFlowable)
    }

    fun shareDataForTesting(comment: String, testMode: DataExport.TestMode, counter: Int?) {
        shareDataModel.shareDataForTesting(comment, testMode, counter)
    }

    fun toggleMeasurementState() {
        measurementModel.toggleMeasurementState()
    }

    override fun onCleared() {
        shareDataModel.clear()
        super.onCleared()
        heartRateDisposable?.dispose()
        disposables.clear()
    }
}