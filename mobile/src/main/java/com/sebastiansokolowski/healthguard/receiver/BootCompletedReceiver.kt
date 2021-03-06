package com.sebastiansokolowski.healthguard.receiver

import android.content.Context
import android.content.Intent
import android.util.Log
import com.sebastiansokolowski.healthguard.service.WearableService
import dagger.android.DaggerBroadcastReceiver


/**
 * Created by Sebastian Sokołowski on 04.06.19.
 */
class BootCompletedReceiver : DaggerBroadcastReceiver() {
    private val TAG = javaClass.canonicalName

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive action=${intent?.action}")
        super.onReceive(context, intent)

        context?.apply {
            startService(Intent(this, WearableService::class.java))
        }
    }
}