package com.fp.devfantasypowerxi.app.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.response.PlayerListResult
import com.fp.devfantasypowerxi.app.view.adapter.PreviewPlayerItemAdapter
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivityTeamPreviewBinding
import java.util.*

// made by Gaurav Minocha
class TeamPreviewActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityTeamPreviewBinding
    var matchKey: String = ""
    var teamVsName: String = ""
    var teamFirstUrl: String = ""
    var teamSecondUrl: String = ""
    var teamName = ""
    var headerText: String = ""
    var isShowTimer = false
    var fantasyType = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_team_preview)
        initialize()

        val horizontalLayoutManagaerr =
            LinearLayoutManager(this@TeamPreviewActivity, LinearLayoutManager.HORIZONTAL, false)
        mainBinding.wickRecyclerView.layoutManager = horizontalLayoutManagaerr

        val horizontalLayoutManagaer =
            LinearLayoutManager(this@TeamPreviewActivity, LinearLayoutManager.HORIZONTAL, false)
        mainBinding.bolRecyclerView.layoutManager = horizontalLayoutManagaer

        val horizontalLayoutManagaer1 =
            LinearLayoutManager(this@TeamPreviewActivity, LinearLayoutManager.HORIZONTAL, false)
        mainBinding.allRecyclerView.layoutManager = horizontalLayoutManagaer1

        val horizontalLayoutManagaer2 =
            LinearLayoutManager(this@TeamPreviewActivity, LinearLayoutManager.HORIZONTAL, false)
        mainBinding.batRecyclerView.layoutManager = horizontalLayoutManagaer2
        // close activity
        mainBinding.icClose.setOnClickListener {
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.navigation_notification -> {
                openNotificationActivity()
                true
            }
            R.id.navigation_wallet -> {
                openWalletActivity()
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun initialize() {

        var listBat = ArrayList<PlayerListResult>()
        var listBowl = ArrayList<PlayerListResult>()
        var listAr= ArrayList<PlayerListResult>()
        var listWK= ArrayList<PlayerListResult>()
        if (intent != null && intent.extras != null) {
            matchKey = intent.extras!!.getString(Constants.KEY_MATCH_KEY)!!
            teamVsName = intent.extras!!.getString(Constants.KEY_TEAM_VS)!!
            teamFirstUrl = intent.extras!!.getString(Constants.KEY_TEAM_FIRST_URL)!!
            teamSecondUrl = intent.extras!!.getString(Constants.KEY_TEAM_SECOND_URL)!!
            teamName = intent.extras!!.getString(Constants.KEY_TEAM_NAME)?:""
            headerText = intent.extras!!.getString(Constants.KEY_STATUS_HEADER_TEXT, "")
            isShowTimer = intent.extras!!
                .getBoolean(Constants.KEY_STATUS_IS_TIMER_HEADER, false)
            fantasyType = intent.extras!!.getInt(Constants.KEY_FANTASY_TYPE)
            listWK =
                intent.extras!!.getParcelableArrayList(Constants.KEY_TEAM_LIST_WK)!!
            listBowl =
                intent.extras!!.getParcelableArrayList(Constants.KEY_TEAM_LIST_BOWL)!!
            listBat =
                intent.extras!!.getParcelableArrayList(Constants.KEY_TEAM_LIST_BAT)!!
            listAr =
                intent.extras!!.getParcelableArrayList(Constants.KEY_TEAM_LIST_AR) !!
        }
        mainBinding.teamName.text = teamName
        mainBinding.wickRecyclerView.setOnTouchListener { _, _ -> true }
        mainBinding.wickRecyclerView.adapter = PreviewPlayerItemAdapter(
            false,
            listWK
        )
        mainBinding.batRecyclerView.setOnTouchListener { _, _ -> true }
        mainBinding.batRecyclerView.adapter = PreviewPlayerItemAdapter(
            false,
            listBat
        )
        mainBinding.bolRecyclerView.setOnTouchListener { _, _ -> true }
        mainBinding.bolRecyclerView.adapter = PreviewPlayerItemAdapter(
            false,
            listBowl
        )
        mainBinding.allRecyclerView.setOnTouchListener { _, _ -> true }
        mainBinding.allRecyclerView.adapter = PreviewPlayerItemAdapter(
            false,
            listAr
        )
        if (fantasyType == 1) {
            mainBinding.ivFanTypePreview.setImageResource(R.drawable.ic_batting_prev)
        } else if (fantasyType == 2) {
            mainBinding.ivFanTypePreview.setImageResource(R.drawable.ic_bowling_prev)
        } else if (fantasyType == 3) {
            mainBinding.ivFanTypePreview.setImageResource(R.drawable.ic_premium_prev)
        } else {
            mainBinding.ivFanTypePreview.setImageResource(R.drawable.ic_classic_prev)
        }
        mainBinding.icClose.setOnClickListener { finish() }
    }


    private fun openNotificationActivity() {
        startActivity(Intent(this@TeamPreviewActivity, NotificationActivity::class.java))
    }

    private fun openWalletActivity() {
        startActivity(Intent(this@TeamPreviewActivity, MyWalletActivity::class.java))
    }


}