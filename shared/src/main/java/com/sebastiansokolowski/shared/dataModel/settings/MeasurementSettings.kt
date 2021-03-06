package com.sebastiansokolowski.shared.dataModel.settings

import com.sebastiansokolowski.shared.dataModel.HealthEventType

/**
 * Created by Sebastian Sokołowski on 20.01.20.
 */
data class MeasurementSettings(val samplingMs: Int = 20, val healthEvents: Set<HealthEventType> = emptySet(), val batterySaver: Boolean = true, val testMode: Boolean = false,
                               val heartRateAnomalySettings: HeartRateAnomalySettings = HeartRateAnomalySettings(),
                               val fallSettings: FallSettings = FallSettings(),
                               val convulsionsSettings: ConvulsionsSettings = ConvulsionsSettings(),
                               var measurementId: Long = -1)