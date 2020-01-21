package com.sebastiansokolowski.healthcarewatch.model.healthCare.detector

import android.hardware.Sensor
import com.sebastiansokolowski.healthcarewatch.model.healthCare.DetectorBase
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Created by Sebastian Sokołowski on 20.01.20.
 */
class ActivityDetector(private var activityThreshold: Int, private var bufferTime: Long) : DetectorBase() {
    val TAG = this::class.java.simpleName

    private var disposable: Disposable? = null

    val activityStateObservable: PublishSubject<Boolean> = PublishSubject.create()

    override fun startDetector() {
        disposable = sensorsObservable
                .subscribeOn(Schedulers.io())
                .filter { it.type == Sensor.TYPE_LINEAR_ACCELERATION }
                .buffer(bufferTime, TimeUnit.SECONDS)
                .take(1)
                .map {
                    var sum = 0.0
                    var count = 0
                    it.forEach {
                        sum +=
                                sqrt(
                                        it.values[0].toDouble().pow(2.0) +
                                                it.values[1].toDouble().pow(2.0) +
                                                it.values[2].toDouble().pow(2.0)
                                )
                        count++
                    }
                    sum / count
                }.subscribe {
                    notifyActivityState(it >= activityThreshold)
                }
    }


    fun notifyActivityState(activity: Boolean) {
        activityStateObservable.onNext(activity)
    }

    override fun stopDetector() {
        disposable?.dispose()
    }

}