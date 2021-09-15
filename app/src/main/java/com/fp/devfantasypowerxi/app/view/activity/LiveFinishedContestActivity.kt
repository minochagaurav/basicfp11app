package com.fp.devfantasypowerxi.app.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.ContestRequest
import com.fp.devfantasypowerxi.app.api.response.League
import com.fp.devfantasypowerxi.app.api.response.LiveFinishedContestData
import com.fp.devfantasypowerxi.app.api.response.RefreshScoreResponse
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.view.adapter.LiveFinishedContestItemAdapter
import com.fp.devfantasypowerxi.app.view.listners.OnContestItemClickListener
import com.fp.devfantasypowerxi.app.view.viewmodel.ContestViewModel
import com.fp.devfantasypowerxi.common.api.Resource
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivityLiveFinishedContestBinding
import java.util.*
import javax.inject.Inject

// made by Gaurav Minocha
class LiveFinishedContestActivity : AppCompatActivity(), OnContestItemClickListener {
    lateinit var mainBinding: ActivityLiveFinishedContestBinding
    private lateinit var contestViewModel: ContestViewModel
    var matchKey: String = ""
    var teamVsName: String = ""
    var teamFirstUrl: String = ""
    var teamSecondUrl: String = ""

    //  var list: ArrayList<LiveFinishedContestData> = ArrayList<LiveFinishedContestData>()
    var headerText: String = ""
    var sportKey = ""
    var fantasyType = 0
    var list = ArrayList<LiveFinishedContestData>()

    @Inject
    lateinit var oAuthRestService: OAuthRestService
    lateinit var mAdapter: LiveFinishedContestItemAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_live_finished_contest)
        contestViewModel = ContestViewModel().create(this@LiveFinishedContestActivity)
        MyApplication.getAppComponent()!!.inject(contestViewModel)
        MyApplication.getAppComponent()!!.inject(this@LiveFinishedContestActivity)
        initialize()
    }

    private fun openWalletActivity() {
        startActivity(Intent(this@LiveFinishedContestActivity, MyWalletActivity::class.java))
    }

    // initialize toolbar
    @SuppressLint("SetTextI18n")
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
            headerText = intent.extras!!.getString(Constants.KEY_STATUS_HEADER_TEXT)!!
            sportKey = intent.extras!!.getString(Constants.SPORT_KEY)!!
            fantasyType = intent.extras!!.getInt(Constants.KEY_FANTASY_TYPE)
        }

        if (headerText.equals("Winner Declared", ignoreCase = true)) {
            mainBinding.matchHeaderInfo.tvTimeTimer.text = "Winner Declared"
            mainBinding.matchHeaderInfo.tvTimeTimer.setTextColor(Color.parseColor("#08114d"))
        } else if (headerText.equals("In Progress", ignoreCase = true)) {
            mainBinding.matchHeaderInfo.tvTimeTimer.text = "In Progress"
            mainBinding.matchHeaderInfo.tvTimeTimer.setTextColor(Color.parseColor("#16ae28"))
        }

        mainBinding.matchHeaderInfo.tvTeamVs.text = teamVsName
        mainBinding.matchHeaderInfo.ivTeamFirst.setImageURI(teamFirstUrl)
        mainBinding.matchHeaderInfo.ivTeamSecond.setImageURI(teamSecondUrl)
        setupRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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
/*
    // toolbar click event
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }*/

    // setup recycle data
    private fun setupRecyclerView() {
        mAdapter = LiveFinishedContestItemAdapter(applicationContext,list,this)
        mainBinding.recyclerView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        mainBinding.recyclerView.layoutManager = mLayoutManager
        mainBinding.recyclerView.adapter = mAdapter
        getData()
        mainBinding.topLayout.setOnClickListener {
            val intent = Intent(applicationContext, PlayerPointsActivity::class.java)
            intent.putExtra(Constants.KEY_MATCH_KEY, matchKey)
            intent.putExtra(Constants.KEY_TEAM_VS, teamVsName)
            intent.putExtra(Constants.KEY_TEAM_FIRST_URL, teamFirstUrl)
            intent.putExtra(Constants.KEY_TEAM_SECOND_URL, teamSecondUrl)
            intent.putExtra(Constants.SPORT_KEY, sportKey)
            intent.putExtra(Constants.KEY_FANTASY_TYPE, fantasyType)

            startActivity(intent)
        }
    }

    private fun getData() {
        val request = ContestRequest()
        request.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        request.matchkey = matchKey
        request.sport_key = sportKey
        request.fantasy_type = fantasyType
        contestViewModel.loadRefreshScore(request)
        contestViewModel.getRefreshScore().observe(this) { arrayListResource ->
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
                    val scoreResponse: RefreshScoreResponse =
                        arrayListResource.data ?: RefreshScoreResponse()
                    if (scoreResponse.status == 1) {
                        if (scoreResponse.result.contest.size > 0) {
                            list = scoreResponse.result.contest
                            mAdapter.updateData(list)
                            setTotalInvestment(
                                scoreResponse.result.total_investment,
                                scoreResponse.result.total_winnings,
                                scoreResponse.result.total_profilt
                            )
                            mainBinding.noChallengeJoined.visibility = View.GONE
                        } else {
                            mainBinding.noChallengeJoined.visibility = View.VISIBLE
                        }
                        if (scoreResponse.result.match_announcement != ""

                        ) {
                            mainBinding.llTopLayout.visibility = View.VISIBLE
                            mainBinding.tvAnn.text = scoreResponse.result.match_announcement
                        } else {
                            mainBinding.llTopLayout.visibility = View.GONE
                        }
                        val animationToLeft: Animation =
                            TranslateAnimation(700F, -400F, 0F, 0F)
                        animationToLeft.duration = 17000
                        animationToLeft.repeatMode = Animation.RESTART
                        animationToLeft.repeatCount = Animation.INFINITE
                        mainBinding.tvAnn.animation = animationToLeft
                    } else {
                        Toast.makeText(
                            MyApplication.appContext,
                            arrayListResource.data!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
    @SuppressLint("SetTextI18n")
    fun setTotalInvestment(ttInvestMent: String, ttWinnings: String, ttProfit: String) {
        if (!ttInvestMent.equals("", ignoreCase = true)) {
            mainBinding.cvTotal.visibility = View.VISIBLE
            mainBinding.tvTotalInvestment.text = "FC $ttInvestMent"
            mainBinding.tvTotalWinnings.text = "FC $ttWinnings"
            mainBinding.tvTotalProfit.text = "FC $ttProfit"
        } else {
            mainBinding.cvTotal.visibility = View.GONE
        }
    }

    override fun onContestClick(contest: League?, isForDetail: Boolean) {
        if (isForDetail) {
            val intent =
                Intent(this@LiveFinishedContestActivity, UpComingContestDetailActivity::class.java)
            intent.putExtra(Constants.KEY_MATCH_KEY, matchKey)
            intent.putExtra(Constants.KEY_TEAM_VS, teamVsName)
            intent.putExtra(Constants.KEY_TEAM_FIRST_URL, teamFirstUrl)
            intent.putExtra(Constants.KEY_TEAM_SECOND_URL, teamSecondUrl)
            intent.putExtra(Constants.KEY_CONTEST_DATA, contest)
            intent.putExtra(Constants.KEY_STATUS_HEADER_TEXT, headerText)
            intent.putExtra(Constants.KEY_STATUS_IS_TIMER_HEADER, false)
            intent.putExtra(Constants.KEY_STATUS_IS_FOR_CONTEST_DETAILS, false)
            intent.putExtra(Constants.SPORT_KEY, sportKey)
            intent.putExtra(Constants.KEY_FANTASY_TYPE, fantasyType)
            startActivity(intent)
        } else {
            val intent = Intent(this@LiveFinishedContestActivity, MyTeamsActivity::class.java)
            intent.putExtra(Constants.KEY_MATCH_KEY, matchKey)
            intent.putExtra(Constants.KEY_TEAM_VS, teamVsName)
            intent.putExtra(Constants.KEY_TEAM_FIRST_URL, teamFirstUrl)
            intent.putExtra(Constants.KEY_TEAM_SECOND_URL, teamSecondUrl)
            intent.putExtra(Constants.KEY_IS_FOR_JOIN_CONTEST, true)
            intent.putExtra(Constants.KEY_CONTEST_DATA, contest)
            intent.putExtra(Constants.KEY_STATUS_HEADER_TEXT, headerText)
            intent.putExtra(Constants.KEY_STATUS_IS_TIMER_HEADER, true)
            intent.putExtra(Constants.SPORT_KEY, sportKey)
            intent.putExtra(Constants.KEY_FANTASY_TYPE, fantasyType)
            startActivity(intent)
        }
    }
}