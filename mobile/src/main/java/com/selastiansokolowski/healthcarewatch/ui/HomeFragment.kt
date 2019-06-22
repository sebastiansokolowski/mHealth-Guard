package com.selastiansokolowski.healthcarewatch.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.text.method.LinkMovementMethod
import android.view.*
import android.widget.TextView
import com.selastiansokolowski.healthcarewatch.R
import com.selastiansokolowski.healthcarewatch.viewModel.HomeViewModel
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.home_fragment.*
import javax.inject.Inject


/**
 * Created by Sebastian Sokołowski on 10.03.19.
 */
class HomeFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        homeViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(HomeViewModel::class.java)

        homeViewModel.heartRate.observe(this, Observer {
            heart_rate_tv.text = it
            heart_rate_iv.startAnimation()
        })

        homeViewModel.measurementState.observe(this, Observer {
            heart_rate_tv.text = "---"
            it?.let {
                if (it) {
                    measurement_btn.text = "Stop measurement"
                } else {
                    measurement_btn.text = "Start measurement"
                }
            }
        })

        measurement_btn.setOnClickListener {
            homeViewModel.toggleMeasurementState()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.about -> {
                showLicencesDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLicencesDialog() {
        context?.let {
            val message = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                Html.fromHtml(getString(R.string.licences), FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(getString(R.string.licences))
            }

            val dialog: AlertDialog = AlertDialog.Builder(it)
                    .setTitle("About")
                    .setMessage(message)
                    .setPositiveButton("Ok", null)
                    .show()

            dialog.findViewById<TextView>(android.R.id.message)?.movementMethod = LinkMovementMethod.getInstance()
        }
    }
}