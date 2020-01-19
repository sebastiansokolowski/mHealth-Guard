package com.sebastiansokolowski.healthcarewatch.di

import com.sebastiansokolowski.healthcarewatch.ui.*
import com.sebastiansokolowski.healthcarewatch.ui.sensorData.HistorySensorDataFragment
import com.sebastiansokolowski.healthcarewatch.ui.sensorData.LiveSensorDataFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeWatchDataFragment(): LiveDataFragment

    @ContributesAndroidInjector
    abstract fun contributeHistoryDataFragment(): HistoryDataFragment

    @ContributesAndroidInjector
    abstract fun contributeLiveSensorDataFragment(): LiveSensorDataFragment

    @ContributesAndroidInjector
    abstract fun contributeSensorDataFragment(): HistorySensorDataFragment

    @ContributesAndroidInjector
    abstract fun contributeSettingsFragment(): SettingsFragment

    @ContributesAndroidInjector
    abstract fun contributeAdvancedSettingsFragment(): AdvancedSettingsFragment
}
