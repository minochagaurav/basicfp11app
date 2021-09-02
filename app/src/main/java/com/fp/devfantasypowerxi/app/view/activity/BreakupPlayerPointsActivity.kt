package com.fp.devfantasypowerxi.app.view.activity

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.backends.pipeline.Fresco
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.response.PlayerPointItem
import com.fp.devfantasypowerxi.app.view.adapter.PlayerBreakupItemAdapter
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivityBreakupPlayerPointsBinding

class BreakupPlayerPointsActivity : AppCompatActivity() {

    private var playerPointItem: PlayerPointItem = PlayerPointItem()
    lateinit var mainBinding: ActivityBreakupPlayerPointsBinding
    lateinit var mAdapter: PlayerBreakupItemAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_breakup_player_points)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_breakup_player_points)

        if (intent != null) {
            playerPointItem =
                intent.getParcelableExtra("playerPointItem")?:PlayerPointItem()
        }
        initialize()
        setData()
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
        mAdapter = PlayerBreakupItemAdapter(applicationContext, playerPointItem.field_label)
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


    @SuppressLint("SetTextI18n")
    private fun setData() {
        if (playerPointItem.isSelected == 1) {
            mainBinding.isselectedTxt.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.player_in_your_team,
                0
            )
            mainBinding.isselectedTxt.text = "In your team"
        } else {
            mainBinding.isselectedTxt.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.player_not_your_team,
                0
            )
            mainBinding.isselectedTxt.text = "Not in your team"
        }
        if (playerPointItem.image != "") {
            mainBinding.ivPlayer.setImageURI(Uri.parse(playerPointItem.image))
        }
        /*else {
            if (playerPointItem.getTeam().equalsIgnoreCase("team2")) {
                mainBinding.ivPlayer.setImageURI(UriUtil.getUriForResourceId(R.drawable.player_team_two));
            } else {
                mainBinding.ivPlayer.setImageURI(UriUtil.getUriForResourceId(R.drawable.player_team_one));
            }
        }*/mainBinding.selectedBy.text = playerPointItem.selected_by.toString()
        mainBinding.credits.setText(playerPointItem.credit.toString())
        mainBinding.points.text = playerPointItem.total.toString()
        mAdapter = PlayerBreakupItemAdapter(
            this@BreakupPlayerPointsActivity,
            playerPointItem.field_label
        )
        mainBinding.recyclerView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        mainBinding.recyclerView.layoutManager = mLayoutManager
        mainBinding.recyclerView.adapter = mAdapter
        mainBinding.name.text = playerPointItem.player_name
        mainBinding.totalPoint.text = playerPointItem.total.toString()
        mainBinding.startingPoint.text = playerPointItem.startingpoints.toString()
        mainBinding.runPoints.text = playerPointItem.runs.toString()
        mainBinding.fourPoints.text = playerPointItem.fours.toString()
        mainBinding.sixPoints.text = playerPointItem.sixs.toString()
        mainBinding.centuryPoints.text = playerPointItem.century.toString()
        mainBinding.Points150.text = playerPointItem.point150.toString()
        mainBinding.Points200.text = playerPointItem.point200.toString()
        mainBinding.strikeRatePoints.text = playerPointItem.strike_rate.toString()
        mainBinding.wicketspoints.text = playerPointItem.wickets.toString()
        mainBinding.economypoints.text = playerPointItem.economy_rate.toString()
        mainBinding.runoutPoints.text = playerPointItem.runouts
        mainBinding.maidenPoints.text = playerPointItem.maidens.toString()
        mainBinding.catchPoints.text = playerPointItem.catch_points.toString()
        mainBinding.stumpingpoints.text = playerPointItem.stumping.toString()
        mainBinding.notOutPoints.text = playerPointItem.not_out.toString()
        mainBinding.winningBonus.text = playerPointItem.winner_point.toString()
        mainBinding.duck.text = playerPointItem.duck
        mainBinding.notOutPoints.text = playerPointItem.not_out.toString()
        mainBinding.actualnOutTxt.text = playerPointItem.actual_notout?:""
        mainBinding.actuabonusTotal.text = playerPointItem.actual_winning
        mainBinding.actualcatTxt.text = playerPointItem.actual_catch
        mainBinding.actualStartTxt.text = playerPointItem.actual_startingpoints
        mainBinding.actualrunTxt.text = playerPointItem.actual_runs
        mainBinding.actualfourTxt.text = playerPointItem.actual_fours
        mainBinding.actualduckTxt.text = playerPointItem.actual_duck
        mainBinding.actualerTxt.text = playerPointItem.actual_economy_rate.toString()
        mainBinding.actualmdoTxt.text = playerPointItem.actual_maidens
        mainBinding.actualsixTxt.text = playerPointItem.actual_sixs
        mainBinding.actualsrTxt.text = playerPointItem.actual_strike_rate.toString()
        mainBinding.actualhndrTxt.text = playerPointItem.century.toString()
        mainBinding.actualwktsTxt.text = playerPointItem.actual_wicket
        mainBinding.actualroutTxt.text = playerPointItem.actual_runouts
        mainBinding.actualTotal.text = playerPointItem.total.toString()
        mainBinding.actuastumplTotal.text = playerPointItem.actual_stumping
        if (playerPointItem.is_topplayer == 1) {
            mainBinding.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.top_player, 0)
        } else {
            mainBinding.name.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.top_player_disable,
                0
            )
        }
        when {
            playerPointItem.actual_halcentury == 1 -> {
                mainBinding.actualfifthTxt.setText(playerPointItem.actual_halcentury)
                mainBinding.halfcenturyPoints.setText(playerPointItem.halcentury)
                mainBinding.cancutryTxt.text = "50's"
            }
            playerPointItem.actual_century == 1 -> {
                mainBinding.actualfifthTxt.setText(playerPointItem.actual_century)
                mainBinding.halfcenturyPoints.setText(playerPointItem.century)
                mainBinding.cancutryTxt.text = "100's"
            }
            playerPointItem.actual_point150 == 1 -> {
                mainBinding.actualfifthTxt.setText(playerPointItem.actual_point150)
                mainBinding.halfcenturyPoints.setText(playerPointItem.point150)
                mainBinding.cancutryTxt.text = "150's"
            }
            playerPointItem.actual_point200 == 1 -> {
                mainBinding.actualfifthTxt.setText(playerPointItem.actual_point200)
                mainBinding.halfcenturyPoints.setText(playerPointItem.point200)
                mainBinding.cancutryTxt.text = "200's"
            }
        }
        if (playerPointItem.actual_halcentury == 0 && playerPointItem.actual_century == 0 && playerPointItem.actual_point150 == 0 && playerPointItem.actual_point200 == 0) {
            mainBinding.haflLayout.visibility = View.GONE
        }
    }


}