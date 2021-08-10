package com.fp.devfantasypowerxi.app.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.databinding.ActivityScratchCardHistoryBinding
// made by Gaurav Minocha
class ScratchCardHistoryActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityScratchCardHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_scratch_card_history)
        initialize()
    }
    // initialize toolbar
    @SuppressLint("RestrictedApi")
    private fun initialize() {
        setSupportActionBar(mainBinding.toolBar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDefaultDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeButtonEnabled(true)
        }

    }
    // toolbar Click listener
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}