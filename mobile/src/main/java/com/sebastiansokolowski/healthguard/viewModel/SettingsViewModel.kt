package com.sebastiansokolowski.healthguard.viewModel

import android.arch.lifecycle.ViewModel
import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import com.sebastiansokolowski.healthguard.model.SensorDataModel
import com.sebastiansokolowski.healthguard.model.SettingsModel
import com.sebastiansokolowski.healthguard.model.SetupModel
import com.sebastiansokolowski.healthguard.util.HealthEventHelper
import com.sebastiansokolowski.healthguard.view.preference.CustomMultiSelectListPreference
import com.sebastiansokolowski.shared.SettingsSharedPreferences
import javax.inject.Inject

/**
 * Created by Sebastian Sokołowski on 10.03.19.
 */
class SettingsViewModel
@Inject constructor(context: Context, private val settingsModel: SettingsModel, private val sensorDataModel: SensorDataModel, private val contentResolver: ContentResolver, val setupModel: SetupModel) : ViewModel() {

    private val healthEventHelper = HealthEventHelper(context)

    fun onSharedPreferenceChanged(key: String) {
        when (key) {
            SettingsSharedPreferences.HEALTH_EVENTS -> {
                if (sensorDataModel.measurementRunning) {
                    sensorDataModel.stopMeasurement()
                    sensorDataModel.requestStartMeasurement()
                }
            }
        }
    }

    fun setupPreference(preference: CustomMultiSelectListPreference) {
        when (preference.key) {
            SettingsSharedPreferences.CONTACTS -> {
                setContacts(preference)
            }
            SettingsSharedPreferences.HEALTH_EVENTS -> {
                setSupportedHealthEvents(preference)
            }
        }
    }

    private fun setContacts(preference: CustomMultiSelectListPreference) {
        val names = mutableListOf<String>()
        val values = mutableListOf<String>()

        val cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER),
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC")

        if (cursor != null && cursor.moveToFirst() && cursor.count > 0) {
            do {
                val name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                var number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                number = number.replace(" ", "")
                number = number.replace("-", "")

                if (!names.contains(name)) {
                    names.add("$name\n\t$number")
                    values.add(number)
                }
            } while (cursor.moveToNext())

            cursor.close()
        }

        preference.setValues(names.toTypedArray(), values.toTypedArray())
    }

    private fun setSupportedHealthEvents(preference: CustomMultiSelectListPreference) {
        val names = mutableListOf<String>()
        val values = mutableListOf<String>()

        val healthEventTypesName = settingsModel.getSupportedHealthEventTypes()

        healthEventTypesName.forEach {
            names.add(healthEventHelper.getTitle(it))
            values.add(it.name)
        }

        preference.setValues(names.toTypedArray(), values.toTypedArray())
    }

}