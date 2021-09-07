package com.fp.devfantasypowerxi.app.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.response.PlayerListResult
import com.fp.devfantasypowerxi.app.view.adapter.PreviewPlayerItemAdapter
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivityFootballTeamPreviewBinding
import java.util.*

class FootballTeamPreviewActivity : AppCompatActivity() {

    lateinit var mainBinding: ActivityFootballTeamPreviewBinding
    var matchKey: String = ""
    var teamVsName: String = ""
    var teamFirstUrl: String = ""
    var teamSecondUrl: String = ""
    var teamName = ""
    var headerText: String = ""
  //  var isShowTimer = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_football_team_preview)
        mainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_football_team_preview)
        mainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_football_team_preview)
        initialize()

        val horizontalLayoutManagaerr = LinearLayoutManager(this@FootballTeamPreviewActivity,
            LinearLayoutManager.HORIZONTAL,
            false)
        mainBinding.wickRecyclerView.setLayoutManager(horizontalLayoutManagaerr)

        val horizontalLayoutManagaer = LinearLayoutManager(this@FootballTeamPreviewActivity,
            LinearLayoutManager.HORIZONTAL,
            false)
        mainBinding.bolRecyclerView.setLayoutManager(horizontalLayoutManagaer)

        val horizontalLayoutManagaer1 = LinearLayoutManager(this@FootballTeamPreviewActivity,
            LinearLayoutManager.HORIZONTAL,
            false)
        mainBinding.allRecyclerView.setLayoutManager(horizontalLayoutManagaer1)

        val horizontalLayoutManagaer2 = LinearLayoutManager(this@FootballTeamPreviewActivity,
            LinearLayoutManager.HORIZONTAL,
            false)
        mainBinding.batRecyclerView.setLayoutManager(horizontalLayoutManagaer2)


    }


    @SuppressLint("ClickableViewAccessibility")
    fun initialize() {

        var listBat = ArrayList<PlayerListResult>()
        var listBowl = ArrayList<PlayerListResult>()
        var listAr = ArrayList<PlayerListResult>()
        var listWK = ArrayList<PlayerListResult>()
        if (intent != null && intent.extras != null) {
            matchKey = intent.extras!!.getString(Constants.KEY_MATCH_KEY)!!
            teamVsName = intent.extras!!.getString(Constants.KEY_TEAM_VS)!!
            teamFirstUrl = intent.extras!!.getString(Constants.KEY_TEAM_FIRST_URL)!!
            teamSecondUrl = intent.extras!!.getString(Constants.KEY_TEAM_SECOND_URL)!!
            teamName = intent.extras!!.getString(Constants.KEY_TEAM_NAME)?:""
            headerText = intent.extras!!.getString(Constants.KEY_STATUS_HEADER_TEXT, "")
         /*   isShowTimer = intent.extras!!
                .getBoolean(Constants.KEY_STATUS_IS_TIMER_HEADER, false)*/
            listWK =
                intent.extras!!.getParcelableArrayList(Constants.KEY_TEAM_LIST_WK)!!
            listBowl =
                intent.extras!!.getParcelableArrayList(Constants.KEY_TEAM_LIST_BOWL)!!
            listBat =
                intent.extras!!.getParcelableArrayList(Constants.KEY_TEAM_LIST_BAT)!!
            listAr =
                intent.extras!!.getParcelableArrayList(Constants.KEY_TEAM_LIST_AR)!!
        }
        mainBinding.teamName.text = teamName
        mainBinding.wickRecyclerView.setOnTouchListener { view, motionEvent -> true }
        mainBinding.wickRecyclerView.adapter = PreviewPlayerItemAdapter(false,
            listWK)
        mainBinding.batRecyclerView.setOnTouchListener { view, motionEvent -> true }
        mainBinding.batRecyclerView.adapter = PreviewPlayerItemAdapter(false,
            listBat)
        mainBinding.allRecyclerView.setOnTouchListener { view, motionEvent -> true }
        mainBinding.allRecyclerView.adapter = PreviewPlayerItemAdapter(false,
            listAr)
        mainBinding.bolRecyclerView.setOnTouchListener { view, motionEvent -> true }
        mainBinding.bolRecyclerView.adapter = PreviewPlayerItemAdapter(false,
            listBowl)
        mainBinding.icClose.setOnClickListener { finish() }
    }

}