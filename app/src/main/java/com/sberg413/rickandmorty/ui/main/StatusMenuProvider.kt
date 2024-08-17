package com.sberg413.rickandmorty.ui.main

import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.view.MenuProvider
import com.sberg413.rickandmorty.R

class StatusMenuProvider(private val mainViewModel: MainViewModel): MenuProvider {

    companion object {
        private const val TAG = "StatusMenuProvider"
    }

    private val spinnerListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            val filterVal = parent.adapter.getItem(position) as String
            Log.d(TAG," Selected: $filterVal")
            mainViewModel.setStatusFilter(filterVal)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main_menu, menu)
        (menu.findItem(R.id.spinner)?.actionView as Spinner).apply {
            onItemSelectedListener = spinnerListener
            adapter = ArrayAdapter.createFromResource(
                context,
                R.array.filter_options,
                android.R.layout.simple_spinner_item
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            setSelection(
                mainViewModel.getSelectedStatusIndex(
                    resources.getStringArray(R.array.filter_options).asList()
                )
            )
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false
    }
}