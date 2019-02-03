package com.selastiansokolowski.healthcarewatch

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent.TYPE_CHANGED
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.selastiansokolowski.healthcarewatch.data_model.SensorEventData
import com.selastiansokolowski.shared.DataClientPaths.Companion.ACCURACY_MAP_PATH
import com.selastiansokolowski.shared.DataClientPaths.Companion.DATA_MAP_PATH
import com.selastiansokolowski.shared.DataClientPaths.Companion.DATA_MAP_SENSOR_EVENT_ACCURACY_KEY
import com.selastiansokolowski.shared.DataClientPaths.Companion.DATA_MAP_SENSOR_EVENT_SENSOR_KEY
import com.selastiansokolowski.shared.DataClientPaths.Companion.DATA_MAP_SENSOR_EVENT_TIMESTAMP_KEY
import com.selastiansokolowski.shared.DataClientPaths.Companion.DATA_MAP_SENSOR_EVENT_VALUES_KEY
import com.selastiansokolowski.shared.DataClientPaths.Companion.SUPPORTED_MAP_PATH
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, DataClient.OnDataChangedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onResume() {
        super.onResume()
        Wearable.getDataClient(this).addListener(this)
    }

    override fun onPause() {
        super.onPause()
        Wearable.getDataClient(this).removeListener(this)
    }

    override fun onDataChanged(dataEvent: DataEventBuffer) {
        dataEvent.forEach { event ->
            if (event.type != TYPE_CHANGED) {
                return
            }

            when (event.dataItem.uri.path) {
                SUPPORTED_MAP_PATH -> {

                }
                DATA_MAP_PATH -> {
                    DataMapItem.fromDataItem(event.dataItem).dataMap.apply {
                        var values = getFloatArray(DATA_MAP_SENSOR_EVENT_VALUES_KEY)
                        var sensorName = getString(DATA_MAP_SENSOR_EVENT_SENSOR_KEY)
                        var accuracy = getInt(DATA_MAP_SENSOR_EVENT_ACCURACY_KEY)
                        var timestamp = getLong(DATA_MAP_SENSOR_EVENT_TIMESTAMP_KEY)

                        val sensorEvent = SensorEventData(sensorName, accuracy, timestamp, values)

                        test_tv.text = test_tv.text.toString() + sensorEvent.toString() + "\n"
                    }
                }
                ACCURACY_MAP_PATH -> {

                }
            }
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
