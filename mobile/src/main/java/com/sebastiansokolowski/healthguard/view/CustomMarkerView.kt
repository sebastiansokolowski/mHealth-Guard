package com.sebastiansokolowski.healthguard.view

import android.content.Context
import android.view.View
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.sebastiansokolowski.healthguard.util.EntryHelper
import com.sebastiansokolowski.healthguard.util.SafeCall
import com.sebastiansokolowski.shared.util.Utils
import kotlinx.android.synthetic.main.custom_marker_view.view.*

/**
 * Created by Sebastian Sokołowski on 17.03.19.
 */
class CustomMarkerView : MarkerView {
    constructor(context: Context) : super(context, 0)

    constructor(context: Context?, layoutResource: Int) : super(context, layoutResource)

    var unit: String = ""

    override fun refreshContent(e: Entry?, highlight: Highlight?) {

        SafeCall.safeLet(e?.x, e?.y) { x, y ->
            text.text = "${EntryHelper.getDate(x, true)}\n${Utils.formatValue(y)} $unit"
            text.textAlignment = View.TEXT_ALIGNMENT_CENTER
        }

        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }
}