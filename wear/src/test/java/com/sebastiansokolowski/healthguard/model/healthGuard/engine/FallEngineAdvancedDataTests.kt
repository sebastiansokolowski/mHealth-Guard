package com.sebastiansokolowski.healthguard.model.healthGuard.engine

import com.sebastiansokolowski.shared.dataModel.HealthEventType
import com.sebastiansokolowski.shared.dataModel.settings.FallSettings
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
class FallEngineAdvancedDataTests : DataTestsBase() {

    private val fallPath = "C:\\Users\\admin\\Dysk Google (mateuszkozik222@gmail.com)\\mHealthGuard_test-data\\Fall"

    override fun getEventPath() = fallPath

    override fun createEngine() = FallEngineAdvanced()

    override fun getHealthEventType() = HealthEventType.FALL_ADVANCED

    @Test
    fun findTheBestFallSettings() {
        val thresholdValues = IntRange(20, 25).step(1).toList()
        val samplingTimeSValues = IntRange(6, 10).step(1).toList()
        val inactivityDetectorTimeoutSValues = IntRange(1, 1).step(1).toList()
        val inactivityDetectorThresholdValues = IntRange(1, 1).step(1).toList()
        val minNumberOfThresholdValues = IntRange(1, 4).step(1).toList()

        val executorService = Executors.newScheduledThreadPool(5)
        val numberOfOptions = thresholdValues.size * samplingTimeSValues.size *
                inactivityDetectorTimeoutSValues.size * inactivityDetectorThresholdValues.size *
                minNumberOfThresholdValues.size
        val countDownLatch = CountDownLatch(numberOfOptions)

        var theBestSummaryTestResult: TestResultSummary? = null
        var theBestFallSettings: FallSettings? = null
        thresholdValues.forEach { threshold ->
            samplingTimeSValues.forEach { samplingTimeS ->
                inactivityDetectorTimeoutSValues.forEach { inactivityDetectorTimeoutS ->
                    inactivityDetectorThresholdValues.forEach { inactivityDetectorThreshold ->
                        minNumberOfThresholdValues.forEach { minNumberOfThreshold ->
                            executorService.submit {
                                val fallSettings = FallSettings(threshold, samplingTimeS, minNumberOfThreshold,
                                        false, inactivityDetectorTimeoutS, inactivityDetectorThreshold)
                                println("testing $fallSettings")

                                val measurementSettings = MeasurementSettings(fallSettings = fallSettings)
                                val summary = testFiles(measurementSettings)

                                synchronized(this) {
                                    if (theBestSummaryTestResult == null || theBestSummaryTestResult!!.getWeightDetectionAccuracy() <= summary.getWeightDetectionAccuracy()) {
                                        theBestSummaryTestResult = summary
                                        theBestFallSettings = fallSettings

                                        println("found $theBestFallSettings$theBestSummaryTestResult")
                                    }
                                }
                                countDownLatch.countDown()
                            }
                        }
                    }
                }
            }
        }
        countDownLatch.await()

        println("\nThe best tests result $theBestSummaryTestResult" +
                "\nFall settings\n$theBestFallSettings")
    }

    @Test
    fun testFiles() {
        val fallSettings = FallSettings()
        val measurementSettings = MeasurementSettings(fallSettings = fallSettings)
        val summary = testFiles(measurementSettings, true)

        println("\nFall test results summary$summary\n")
    }

}