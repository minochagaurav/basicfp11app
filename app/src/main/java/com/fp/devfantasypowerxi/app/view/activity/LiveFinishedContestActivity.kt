package com.fp.devfantasypowerxi.app.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.backends.pipeline.Fresco
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.view.adapter.LiveFinishedContestItemAdapter
import com.fp.devfantasypowerxi.databinding.ActivityLiveFinishedContestBinding
// made by Gaurav Minocha
class LiveFinishedContestActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityLiveFinishedContestBinding
    lateinit var mAdapter: LiveFinishedContestItemAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_live_finished_contest)
        initialize()
    }

    // initialize toolbar
    fun initialize() {
        setSupportActionBar(mainBinding.myToolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = getString(R.string.contest)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
        setupRecyclerView()
    }

    // toolbar click event
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // setup recycle data
    private fun setupRecyclerView() {
        mAdapter = LiveFinishedContestItemAdapter(applicationContext)
        mainBinding.recyclerView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        mainBinding.recyclerView.layoutManager = mLayoutManager
        mainBinding.recyclerView.adapter = mAdapter

        mainBinding.topLayout.setOnClickListener {
            startActivity(Intent(this@LiveFinishedContestActivity, PlayerPointsActivity::class.java))
        }

    }
}