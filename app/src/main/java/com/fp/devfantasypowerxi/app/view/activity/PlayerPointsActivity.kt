package com.fp.devfantasypowerxi.app.view.activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.ContestRequest
import com.fp.devfantasypowerxi.app.api.response.PlayerPointItem
import com.fp.devfantasypowerxi.app.view.adapter.PlayerPointsItemAdapter
import com.fp.devfantasypowerxi.app.view.viewmodel.ContestViewModel
import com.fp.devfantasypowerxi.common.api.Resource
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivityPlayerPointsBinding
import java.util.*

// made by Gaurav Minocha
class PlayerPointsActivity : AppCompatActivity() {
    var matchKey: String = ""
    var teamVsName: String = ""
    var teamFirstUrl: String = ""
    var teamSecondUrl: String = ""
    var sportKey = ""
    lateinit var contestViewModel: ContestViewModel
    lateinit var mainBinding: ActivityPlayerPointsBinding
    lateinit var mAdapter: PlayerPointsItemAdapter
    var list: ArrayList<PlayerPointItem> = ArrayList<PlayerPointItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contestViewModel = ContestViewModel().create(this@PlayerPointsActivity)
        MyApplication.getAppComponent()!!.inject(contestViewModel)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_player_points)
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
        if (intent != null && intent.extras != null) {
            matchKey = intent.extras!!.getString(Constants.KEY_MATCH_KEY)!!
            teamVsName = intent.extras!!.getString(Constants.KEY_TEAM_VS)!!
            teamFirstUrl = intent.extras!!.getString(Constants.KEY_TEAM_FIRST_URL)!!
            teamSecondUrl = intent.extras!!.getString(Constants.KEY_TEAM_SECOND_URL)!!
            sportKey = intent.extras!!.getString(Constants.SPORT_KEY)!!
        }
        mainBinding.matchHeaderInfo.tvTeamVs.text = teamVsName
        setupRecyclerView()
    }

    // toolbar click listener
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // setup recycler data
    private fun setupRecyclerView() {
        mAdapter = PlayerPointsItemAdapter(applicationContext,list)
        mainBinding.recyclerView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        mainBinding.recyclerView.layoutManager = mLayoutManager
        mainBinding.recyclerView.adapter = mAdapter
        getData()
    }

    private fun getData() {
        val request = ContestRequest()
        request.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        request.matchkey = matchKey
        request.sport_key = sportKey
        contestViewModel.loadPlayerPointRequest(request)
        contestViewModel.getPlayerPoints().observe(this@PlayerPointsActivity) { arrayListResource ->
            Log.d("Status ", "" + arrayListResource.status)
            when (arrayListResource.status) {
                Resource.Status.LOADING -> {
                    mainBinding.refreshing = true
                }
                Resource.Status.ERROR -> {
                    mainBinding.refreshing = false
                    Toast.makeText(
                        MyApplication.appContext,
                        arrayListResource.exception!!.getErrorModel().errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Resource.Status.SUCCESS -> {
                    mainBinding.refreshing = false
                    if (arrayListResource.data!!.status == 1) {
                        list = arrayListResource.data.result
                           mAdapter.updateData(list)
                    } else {
                        Toast.makeText(
                            MyApplication.appContext,
                            arrayListResource.data.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }


}