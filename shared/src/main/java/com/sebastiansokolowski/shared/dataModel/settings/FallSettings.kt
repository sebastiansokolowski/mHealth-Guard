package com.sebastiansokolowski.shared.dataModel.settings

/**
 * Created by Sebastian Sokołowski on 20.01.20.
 */
data class FallSettings(val threshold: Int = 20, val sampleCount: Int = 10, val stepDetector: Boolean = true, val stepDetectorTimeoutInS: Int = 10, val timeOfInactivity: Int = 0, val activityThreshold: Int = 3)