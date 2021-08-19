package com.fp.devfantasypowerxi.app.view.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.backends.pipeline.Fresco
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.view.adapter.PlayerBreakupItemAdapter
import com.fp.devfantasypowerxi.databinding.ActivityBreakupPlayerPointsBinding

class BreakupPlayerPointsActivity : AppCompatActivity() {

    lateinit var mainBinding: ActivityBreakupPlayerPointsBinding
    lateinit  var mAdapter: PlayerBreakupItemAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_breakup_player_points)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_breakup_player_points)
        initialize()
    }


    // initialize toolbar
    fun initialize() {
        Fresco.initialize(this)
        setSupportActionBar(mainBinding.myToolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = getString(R.string.points_breakup)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
        setupRecyclerView()
    }

    // setup recycler data
    private fun setupRecyclerView() {
        mAdapter = PlayerBreakupItemAdapter(applicationContext )
        mainBinding.recyclerView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        mainBinding.recyclerView.layoutManager = mLayoutManager
        mainBinding.recyclerView.adapter = mAdapter
    }

    // toolbar click listener
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}