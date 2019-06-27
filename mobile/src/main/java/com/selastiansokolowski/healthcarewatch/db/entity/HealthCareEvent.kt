package com.selastiansokolowski.healthcarewatch.db.entity

import com.selastiansokolowski.healthcarewatch.db.converter.HealthCareEventConverter
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

/**
 * Created by Sebastian Sokołowski on 24.06.19.
 */
@Entity
class HealthCareEvent {
    @Id
    var id: Long = 0
    lateinit var sensorEventData: ToOne<SensorEventData>
    @Convert(converter = HealthCareEventConverter::class, dbType = String::class)
    lateinit var careEvent: HealthCareEventType
}