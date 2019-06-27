package com.selastiansokolowski.healthcarewatch.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.BaseSwipeAdapter
import com.selastiansokolowski.healthcarewatch.R
import com.selastiansokolowski.healthcarewatch.db.entity.HealthCareEvent
import com.selastiansokolowski.healthcarewatch.view.HealthCareEventHelper
import kotlinx.android.synthetic.main.health_care_event_item.view.*

/**
 * Created by Sebastian Sokołowski on 24.06.19.
 */
class HealthCareEventAdapter(val context: Context, private val healthCareEvents: List<HealthCareEvent>, private val healthCareEventAdapterItemListener: HealthCareEventAdapterItemListener) : BaseSwipeAdapter(), SwipeLayout.SwipeListener {
    private val TAG = javaClass.canonicalName

    private val healthCareEventHelper = HealthCareEventHelper(context)

    override fun getItem(position: Int): Any {
        return healthCareEvents[position]
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipe
    }

    override fun getItemId(position: Int): Long {
        return healthCareEvents[position].id
    }

    override fun getCount(): Int {
        return healthCareEvents.size
    }

    override fun generateView(position: Int, parent: ViewGroup?): View {
        val swipeLayout = LayoutInflater.from(context).inflate(R.layout.health_care_event_item, null) as SwipeLayout

        swipeLayout.showMode = SwipeLayout.ShowMode.LayDown

        return swipeLayout
    }

    override fun fillValues(position: Int, convertView: View?) {
        val item = healthCareEvents[position]
        convertView?.apply {
            tag = item
            health_care_event_item_title.text = healthCareEventHelper.getTitle(item)
            health_care_event_item_date.text = healthCareEventHelper.getDate(item)
            health_care_event_item_event_info.text = healthCareEventHelper.getEventInfo(item)
            health_care_event_item_message.text = healthCareEventHelper.getMessage(item)

            swipe.addSwipeListener(this@HealthCareEventAdapter)
        }
    }

    interface HealthCareEventAdapterItemListener {
        fun onDeleteItem(healthCareEvent: HealthCareEvent)
    }

    //SwipeListener

    override fun onOpen(layout: SwipeLayout?) {
        layout?.let {
            val healthCareEvent = it.tag as HealthCareEvent
            healthCareEventAdapterItemListener.onDeleteItem(healthCareEvent)
        }
    }

    override fun onUpdate(layout: SwipeLayout?, leftOffset: Int, topOffset: Int) {
    }

    override fun onStartOpen(layout: SwipeLayout?) {
    }

    override fun onStartClose(layout: SwipeLayout?) {
    }

    override fun onHandRelease(layout: SwipeLayout?, xvel: Float, yvel: Float) {
    }

    override fun onClose(layout: SwipeLayout?) {
    }
}