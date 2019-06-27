package com.selastiansokolowski.healthcarewatch.db.converter

import com.selastiansokolowski.healthcarewatch.db.entity.HealthCareEventType
import io.objectbox.converter.PropertyConverter

/**
 * Created by Sebastian Sokołowski on 24.06.19.
 */
class HealthCareEventConverter : PropertyConverter<HealthCareEventType, String> {
    override fun convertToDatabaseValue(entityProperty: HealthCareEventType): String {
        return entityProperty.name
    }

    override fun convertToEntityProperty(databaseValue: String): HealthCareEventType {
        return HealthCareEventType.valueOf(databaseValue)
    }

}