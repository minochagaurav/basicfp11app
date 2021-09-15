package com.fp.devfantasypowerxi.app.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
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
import com.fp.devfantasypowerxi.app.api.request.JoinContestRequest
import com.fp.devfantasypowerxi.app.api.response.JoinedContestContest
import com.fp.devfantasypowerxi.app.api.response.League
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.adapter.JoinedContestItemAdapter
import com.fp.devfantasypowerxi.app.view.listners.OnContestItemClickListener
import com.fp.devfantasypowerxi.app.view.viewmodel.ContestDetailsViewModel
import com.fp.devfantasypowerxi.common.api.Resource
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivityJoinedContestBinding
import java.util.*

class JoinedContestActivity : AppCompatActivity(), OnContestItemClickListener {

    lateinit var mainBinding: ActivityJoinedContestBinding
    lateinit var mAdapter: JoinedContestItemAdapter
    var matchKey: String = ""
    var teamVsName: String = ""
    var teamFirstUrl: String = ""
    var teamSecondUrl: String = ""
    var headerText: String = ""
    var date: String = ""
    var teamCount = 0
    var sportKey = ""
    var fantasyType = 0
    var list: ArrayList<JoinedContestContest> = ArrayList<JoinedContestContest>()
    lateinit var contestDetailsViewModel: ContestDetailsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_joined_contest)
        contestDetailsViewModel = ContestDetailsViewModel().create(this)
        MyApplication.getAppComponent()!!.inject(contestDetailsViewModel)
        initialize()
    }

    @SuppressLint("SetTextI18n")
    fun initialize() {
        setSupportActionBar(mainBinding.mytoolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = getString(R.string.joined_contest)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
        if (intent != null && intent.extras != null) {
            matchKey = intent.extras!!.getString(Constants.KEY_MATCH_KEY)!!
            teamVsName = intent.extras!!.getString(Constants.KEY_TEAM_VS)!!
            teamFirstUrl = intent.extras!!.getString(Constants.KEY_TEAM_FIRST_URL)!!
            teamSecondUrl = intent.extras!!.getString(Constants.KEY_TEAM_SECOND_URL)!!
            headerText = intent.extras!!.getString(Constants.KEY_STATUS_HEADER_TEXT)!!
            date = intent.extras!!.getString(Constants.KEY_STATUS_HEADER_TEXT)!!
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
        showTimer()
        setupRecyclerView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() {
        mAdapter = JoinedContestItemAdapter(applicationContext, list, this)
        mainBinding.recyclerView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        mainBinding.recyclerView.layoutManager = mLayoutManager
        mainBinding.recyclerView.adapter = mAdapter
        getData()
    }

    private fun getData() {
        val request = JoinContestRequest()
        request.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        request.matchkey = matchKey
        request.sport_key = sportKey
        request.fantasy_type = fantasyType
        contestDetailsViewModel.loadJoinedContestRequest(request)
        contestDetailsViewModel.getJoinedContestData().observe(this, { arrayListResource ->
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
                    if (arrayListResource.data!!.status == 1 && arrayListResource.data
                            .result.contest.size > 0
                    ) {
                        teamCount =
                            arrayListResource.data.result.user_teams
                        list = arrayListResource.data.result.contest
                        mAdapter.updateData(list)
                        if (arrayListResource.data.result
                                .match_announcement != ""
                        ) {
                            mainBinding.llTopLayout.visibility = View.VISIBLE
                            mainBinding.tvAnn.text = arrayListResource.data.result
                                .match_announcement
                        } else {
                            mainBinding.llTopLayout.visibility = View.GONE
                        }
                        val animationToLeft: Animation = TranslateAnimation(700F, -400F, 0F, 0F)
                        animationToLeft.duration = 17000
                        animationToLeft.repeatMode = Animation.RESTART
                        animationToLeft.repeatCount = Animation.INFINITE
                        mainBinding.tvAnn.animation = animationToLeft
                    } else {
                        Toast.makeText(
                            MyApplication.appContext,
                            arrayListResource.data.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    override fun onContestClick(contest: League?, isForDetail: Boolean) {
        if (isForDetail) {
            val intent =
                Intent(this@JoinedContestActivity, UpComingContestDetailActivity::class.java)
            intent.putExtra(Constants.KEY_MATCH_KEY, matchKey)
            intent.putExtra(Constants.KEY_TEAM_VS, teamVsName)
            intent.putExtra(Constants.KEY_TEAM_FIRST_URL, teamFirstUrl)
            intent.putExtra(Constants.KEY_TEAM_SECOND_URL, teamSecondUrl)
            intent.putExtra(Constants.KEY_CONTEST_DATA, contest)
            intent.putExtra(Constants.KEY_STATUS_HEADER_TEXT, headerText)
            intent.putExtra(Constants.KEY_STATUS_IS_TIMER_HEADER, true)
            intent.putExtra(Constants.KEY_STATUS_IS_FOR_CONTEST_DETAILS, true)
            intent.putExtra(Constants.KEY_TEAM_COUNT, teamCount)
            intent.putExtra(Constants.SPORT_KEY, sportKey)
            intent.putExtra(Constants.KEY_FANTASY_TYPE, fantasyType)
            startActivity(intent)
        } else {
            val intent = Intent(this@JoinedContestActivity, MyTeamsActivity::class.java)
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


    private fun showTimer() {
        try {
            val countDownTimer: CountDownTimer =
                object : CountDownTimer(AppUtils.eventDateMileSecond(headerText), 1000) {
                    @SuppressLint("SetTextI18n")
                    override fun onTick(millisUntilFinished: Long) {
                        val seconds = millisUntilFinished / 1000 % 60
                        val minutes = millisUntilFinished / (1000 * 60) % 60
                        val diffHours = millisUntilFinished / (60 * 60 * 1000)
                        mainBinding.matchHeaderInfo.tvTimeTimer.text =
                            twoDigitString(diffHours) + "h : " + twoDigitString(
                                minutes
                            ) + "m : " + twoDigitString(seconds) + "s "

                        /*   if (TimeUnit.MILLISECONDS.toDays(millisUntilFinished) > 0) {
                        mBinding.matchHeaderInfo.tvTimeTimer.setText(TimeUnit.MILLISECONDS.toDays(millisUntilFinished) + "d " + twoDigitString(diffHours) + "h " + twoDigitString(minutes) + "m " + twoDigitString(seconds) + "s ");
                    } else {
                        mBinding.matchHeaderInfo.tvTimeTimer.setText(twoDigitString(diffHours) + "h " + twoDigitString(minutes) + "m " + twoDigitString(seconds) + "s ");
                    }*/
                    }

                    private fun twoDigitString(number: Long): String {
                        if (number == 0L) {
                            return "00"
                        } else if (number / 10 == 0L) {
                            return "0$number"
                        }
                        return number.toString()
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onFinish() {
                        mainBinding.matchHeaderInfo.tvTimeTimer.text = "00h 00m 00s"
                    }
                }
            countDownTimer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}