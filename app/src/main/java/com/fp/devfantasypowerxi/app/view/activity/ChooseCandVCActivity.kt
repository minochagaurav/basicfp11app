package com.fp.devfantasypowerxi.app.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.view.adapter.ChooseCaptainVCPlayerItemAdapter
import com.fp.devfantasypowerxi.app.view.adapter.SelectedUnSelectedPlayerAdapter
import com.fp.devfantasypowerxi.databinding.ActivityChooseCandVCBinding
// made by Gaurav Minocha
class ChooseCandVCActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityChooseCandVCBinding
    lateinit var mAdapter: ChooseCaptainVCPlayerItemAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_choose_cand_v_c
        )

        mainBinding.ivTeamPreview.setOnClickListener {
            startActivity(Intent(this@ChooseCandVCActivity, TeamPreviewActivity::class.java))
        }
        mainBinding.btnCreateTeam.setOnClickListener {
            finish()
        }
        initialize()
    }

    // initialize toolbar
    fun initialize() {
        setSupportActionBar(mainBinding.myToolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = getString(R.string.choose_c_vc_title)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
        setupRecyclerView()
    }
    // toolbar Click Event
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // setup Recycler Data
    private fun setupRecyclerView() {
        mAdapter = ChooseCaptainVCPlayerItemAdapter(applicationContext)
        mainBinding.recyclerView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        mainBinding.recyclerView.layoutManager = mLayoutManager
        mainBinding.recyclerView.adapter = mAdapter

    }
}