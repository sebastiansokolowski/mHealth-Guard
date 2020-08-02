package com.sebastiansokolowski.healthguard.model.healthGuard.engine

import com.sebastiansokolowski.shared.dataModel.HealthEventType
import com.sebastiansokolowski.shared.dataModel.settings.EpilepsySettings
import com.sebastiansokolowski.shared.dataModel.settings.MeasurementSettings
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

/**
 * Created by Sebastian Sokołowski on 04.11.19.
 */
@RunWith(MockitoJUnitRunner.Silent::class)
class EpilepsyEngineDataTests : DataTestsBase() {

    private val eventPath = ""

    override fun getEventPath() = eventPath

    override fun createEngine() = EpilepsyEngine()

    override fun getHealthEventType() = HealthEventType.EPILEPSY

    @Test
    fun findTheBestEpilepsySettings() {

        val thresholdValues = IntRange(3, 15).step(1).toList()
        val samplingTimeSValues = IntRange(5, 30).step(1).toList()
        val motionsValues = IntRange(30, 150).step(5).toList()

        val executorService = Executors.newScheduledThreadPool(10)
        val numberOfOptions = thresholdValues.size * samplingTimeSValues.size *
                motionsValues.size
        val countDownLatch = CountDownLatch(numberOfOptions)

        var theBestSummaryTestResult: TestResultSummary? = null
        var theBestSeizureSettings: EpilepsySettings? = null
        thresholdValues.forEach { threshold ->
            samplingTimeSValues.forEach { samplingTimeS ->
                motionsValues.forEach { motions ->
                    executorService.submit {
                        val epilepsySettings = EpilepsySettings(threshold, samplingTimeS, motions)
                        println("testing $epilepsySettings")

                        val measurementSettings = MeasurementSettings(epilepsySettings = epilepsySettings)
                        val summary = testFiles(measurementSettings)

                        synchronized(this) {
                            if (theBestSummaryTestResult == null || theBestSummaryTestResult!!.getDetectionAccuracy() <= summary.getDetectionAccuracy()) {
                                theBestSummaryTestResult = summary
                                theBestSeizureSettings = epilepsySettings

                                println("found $theBestSeizureSettings$theBestSummaryTestResult")
                            }
                        }
                        countDownLatch.countDown()
                    }
                }
            }
        }
        countDownLatch.await()

        println("\nThe best tests result $theBestSummaryTestResult" +
                "\nEpilepsy settings\n$theBestSeizureSettings")
    }

    @Test
    fun testFiles() {
        val epilepsySettings = EpilepsySettings()
        val measurementSettings = MeasurementSettings(epilepsySettings = epilepsySettings)
        val summary = testFiles(measurementSettings)

        println("\nEpilepsy test results summary$summary\n")
    }

}