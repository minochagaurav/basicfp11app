package com.fp.devfantasypowerxi.app.view.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.response.League
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.fragment.LeaderBoardFragment
import com.fp.devfantasypowerxi.app.view.fragment.WinningBreakUpFragment
import com.fp.devfantasypowerxi.app.view.interfaces.JoinedUserCallBack
import com.fp.devfantasypowerxi.app.view.listners.TeamCreatedListener
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivityUpComingContestDetailBinding
import java.util.*
import java.util.concurrent.TimeUnit

// made by Gaurav Minocha
class UpComingContestDetailActivity : AppCompatActivity(), JoinedUserCallBack {
    lateinit var mainBinding: ActivityUpComingContestDetailBinding
    lateinit var mTabAdapter: TabAdapter
    var matchKey: String = ""
    var teamVsName: String = ""
    var teamFirstUrl: String = ""
    var teamSecondUrl: String = ""
    var teamCount = 0
    var headerText: String = ""
    var isShowTimer = false
    var contestReferCode = ""
    var isForFirstTeamCreate = false
    var isForContestDetails = false
    var teamCreated = false
    var sportKey = ""
    var isCreateTeam = true


    var fantasyType = 0
    lateinit var contestForFirstTime: League
    lateinit var contest: League
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_up_coming_contest_detail
        )
        initialize()
    }

    // initialize toolbar and set tabs
    @SuppressLint("SetTextI18n")
    fun initialize() {
        setSupportActionBar(mainBinding.myToolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = getString(R.string.contest_detail)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }

        if (intent != null && intent.extras != null) {
            matchKey = intent.extras!!.getString(Constants.KEY_MATCH_KEY)!!
            teamVsName = intent.extras!!.getString(Constants.KEY_TEAM_VS)!!
            teamFirstUrl = intent.extras!!.getString(Constants.KEY_TEAM_FIRST_URL)!!
            teamSecondUrl = intent.extras!!.getString(Constants.KEY_TEAM_SECOND_URL)!!
            teamCount = intent.extras!!.getInt(Constants.KEY_TEAM_COUNT)
            contest = intent.getParcelableExtra(Constants.KEY_CONTEST_DATA)!!
            headerText = intent.extras!!.getString(Constants.KEY_STATUS_HEADER_TEXT, "")
            isShowTimer = intent.extras!!.getBoolean(Constants.KEY_STATUS_IS_TIMER_HEADER, false)
            isForContestDetails = intent.extras!!
                .getBoolean(Constants.KEY_STATUS_IS_FOR_CONTEST_DETAILS, false)
            sportKey = intent.extras!!.getString(Constants.SPORT_KEY)!!
            fantasyType = intent.extras!!.getInt(Constants.KEY_FANTASY_TYPE)
        }
        mainBinding.contestData = contest
        mainBinding.matchHeaderInfo.tvTeamVs.text = teamVsName
        mainBinding.matchHeaderInfo.ivTeamFirst.setImageURI(teamFirstUrl)
        mainBinding.matchHeaderInfo.ivTeamSecond.setImageURI(teamSecondUrl)

        mTabAdapter =
            TabAdapter(
                supportFragmentManager
            )
        mTabAdapter.addFragment(
            WinningBreakUpFragment.newInstance(
                matchKey,
                contest.id.toString() + "",
                contest.win_amount.toString() + "",
                contest.winning_percentage.toString(),
                sportKey,
                fantasyType
            ), "Winning Breakup"
        )
        mTabAdapter.addFragment(
            LeaderBoardFragment.newInstance(
                matchKey,
                contest.id.toString() + "",
                isShowTimer,
                isForContestDetails,
                contest.pdf,
                sportKey,
                fantasyType
            ), "Leaderboard"
        )

        mainBinding.viewPager.adapter = mTabAdapter
        mainBinding.tabLayout.setupWithViewPager(mainBinding.viewPager)

        mainBinding.btnJoinContest.setOnClickListener {
            startActivity(
                Intent(
                    this@UpComingContestDetailActivity,
                    CreateTeamActivity::class.java
                )
            )
        }
        if (isShowTimer) {
            showTimer()
        } else {
            if (headerText.equals("Winner Declared", ignoreCase = true)) {
                mainBinding.matchHeaderInfo.tvTimeTimer.text = "Winner Declared"
                mainBinding.matchHeaderInfo.tvTimeTimer.setTextColor(Color.parseColor("#08114d"))
            } else if (headerText.equals("In Progress", ignoreCase = true)) {
                mainBinding.matchHeaderInfo.tvTimeTimer.text = "In Progress"
                mainBinding.matchHeaderInfo.tvTimeTimer.setTextColor(Color.parseColor("#16ae28"))
            }
        }

        if (contest.image != "") {
            mainBinding.ivGadgetLeague.visibility = View.VISIBLE
            mainBinding.txtTotalWinnings.visibility = View.GONE
            AppUtils.loadPopupImage(mainBinding.ivGadgetLeague, contest.image)

            //   mainBinding.ivGadgetLeague.setImageURI(contest.getImage());
        } else {
            mainBinding.txtTotalWinnings.visibility = View.VISIBLE
            mainBinding.ivGadgetLeague.visibility = View.GONE
            mainBinding.txtTotalWinnings.text = "FC " + "${contest.win_amount}"
        }
        if (contest.winning_percentage != null && contest.winning_percentage is String) {
            if (contest.winning_percentage != ""
                && contest.winning_percentage!! != "0"
            ) {
                mainBinding.ivPercentageLeague.visibility = View.VISIBLE
            } else {
                mainBinding.ivPercentageLeague.visibility = View.GONE
            }
        }

        mainBinding.ivGadgetLeague.setOnClickListener(View.OnClickListener { /*   Intent intent = new Intent(UpComingContestDetailActivity.this, FullImageActivity.class);
                    intent.putExtra(Constants.KEY_IMAGE_URI,contest.getImage());
                    startActivity(intent);*/
            showPopUpImage(contest.image)
        })



        if (contest.isjoined) {
            if (contest.multi_entry == 1) mainBinding.btnJoinContest.text =
                "JOIN+" else mainBinding.btnJoinContest.text = "INVITE CONTEST"
        } else {
            mainBinding.btnJoinContest.text = "Join Contest Now"
        }

        isCreateTeam = if (headerText.equals(
                "In Progress",
                ignoreCase = true
            ) || headerText.equals("Winner Declared", ignoreCase = true)
        ) {
            mainBinding.txtStartValue.text =
                "Challenge Closed"
            mainBinding.btnJoinContest.visibility = View.GONE
            false
        } else {
            mainBinding.btnJoinContest.visibility = View.VISIBLE
            true
        }
        mainBinding.btnJoinContest.setOnClickListener { view ->
            if (mainBinding.btnJoinContest.text.toString().trim()
                == "Join Contest Now" || mainBinding.btnJoinContest.text
                    .toString().trim() == "JOIN+"
            ) {
                if (teamCount > 0 || teamCreated) {
                    val intent =
                        Intent(this@UpComingContestDetailActivity, MyTeamsActivity::class.java)
                    intent.putExtra(Constants.KEY_MATCH_KEY, matchKey)
                    intent.putExtra(Constants.KEY_TEAM_VS, teamVsName)
                    intent.putExtra(Constants.KEY_TEAM_FIRST_URL, teamFirstUrl)
                    intent.putExtra(Constants.KEY_TEAM_SECOND_URL, teamSecondUrl)
                    intent.putExtra(Constants.KEY_IS_FOR_JOIN_CONTEST, true)
                    intent.putExtra(Constants.KEY_CONTEST_DATA, contest)
                    intent.putExtra(Constants.KEY_TEAM_COUNT, teamCount)
                    intent.putExtra("isBonous", contest.isShowBTag())
                    intent.putExtra(Constants.KEY_STATUS_HEADER_TEXT, headerText)
                    intent.putExtra(Constants.KEY_STATUS_IS_TIMER_HEADER, isShowTimer)
                    intent.putExtra(Constants.SPORT_KEY, sportKey)
                    intent.putExtra(Constants.KEY_FANTASY_TYPE, fantasyType)
                    startActivity(intent)
                } else {
                    isForFirstTeamCreate = true
                    contestForFirstTime = contest
                    creteTeam()
                }
            } else {
                //openShareIntent()
            }
        }
        mainBinding.btnJoin.setOnClickListener(View.OnClickListener {
            if (isCreateTeam) {
                if (mainBinding.btnJoinContest.text.toString().trim()
                    == "Join Contest Now" || mainBinding.btnJoinContest.text
                        .toString().trim() == "JOIN+"
                ) {
                    if (teamCount > 0 || teamCreated) {
                        val intent = Intent(
                            this@UpComingContestDetailActivity,
                            MyTeamsActivity::class.java
                        )
                        intent.putExtra(Constants.KEY_MATCH_KEY, matchKey)
                        intent.putExtra(Constants.KEY_TEAM_VS, teamVsName)
                        intent.putExtra(Constants.KEY_TEAM_FIRST_URL, teamFirstUrl)
                        intent.putExtra(Constants.KEY_TEAM_SECOND_URL, teamSecondUrl)
                        intent.putExtra(Constants.KEY_IS_FOR_JOIN_CONTEST, true)
                        intent.putExtra(Constants.KEY_CONTEST_DATA, contest)
                        intent.putExtra(Constants.KEY_TEAM_COUNT, teamCount)
                        intent.putExtra("isBonous", contest.isShowBTag())
                        intent.putExtra(Constants.KEY_STATUS_HEADER_TEXT, headerText)
                        intent.putExtra(Constants.KEY_STATUS_IS_TIMER_HEADER, isShowTimer)
                        intent.putExtra(Constants.SPORT_KEY, sportKey)
                        intent.putExtra(Constants.KEY_FANTASY_TYPE, fantasyType)
                        startActivity(intent)
                    } else {
                        isForFirstTeamCreate = true
                        contestForFirstTime = contest
                        creteTeam()
                    }
                } else {
                    //openShareIntent()
                }
            }
        })
        mainBinding.tagC.setOnClickListener(View.OnClickListener {
            AppUtils.showToolTip(
                applicationContext,
                Constants.TAG_C_TEXT,
                mainBinding.tagC,
                resources.getColor(R.color.green_color)
            )
        })

        mainBinding.tagB.setOnClickListener(View.OnClickListener {
            val tagB: String = contest.bonus_percent + " bonus usable"
            AppUtils.showToolTip(
                applicationContext,
                tagB,
                mainBinding.tagB,
                resources.getColor(R.color.tooltipColorBonous)
            )
        })

        mainBinding.tagM.setOnClickListener(View.OnClickListener {
            val tagM = "You can join with " + contest.max_multi_entry_user.toString() + " teams"
            AppUtils.showToolTip(
                applicationContext,
                tagM,
                mainBinding.tagM,
                resources.getColor(R.color.colorPrimary)
            )
        })
    }


    fun switchTeam(joinedSwitchTeamId: String?) {
        val intent = Intent(this@UpComingContestDetailActivity, MyTeamsActivity::class.java)
        intent.putExtra(Constants.KEY_MATCH_KEY, matchKey)
        intent.putExtra(Constants.KEY_TEAM_VS, teamVsName)
        intent.putExtra(Constants.KEY_TEAM_FIRST_URL, teamFirstUrl)
        intent.putExtra(Constants.KEY_TEAM_SECOND_URL, teamSecondUrl)
        intent.putExtra(Constants.KEY_IS_FOR_JOIN_CONTEST, true)
        intent.putExtra(Constants.KEY_CONTEST_DATA, contest)
        intent.putExtra(Constants.KEY_STATUS_HEADER_TEXT, headerText)
        intent.putExtra(Constants.KEY_STATUS_IS_TIMER_HEADER, isShowTimer)
        intent.putExtra("isForJoinedId", joinedSwitchTeamId)
        intent.putExtra(Constants.KEY_IS_FOR_SWITCH_TEAM, true)
        intent.putExtra(Constants.SPORT_KEY, sportKey)
        intent.putExtra(Constants.KEY_FANTASY_TYPE, fantasyType)
        startActivity(intent)
    }

    private fun creteTeam() {
        val intent: Intent
        /* intent = if (sportKey.equals(Constants.TAG_FOOTBALL, ignoreCase = true)) {
             Intent(this@UpComingContestDetailActivity, FootballCreateTeamActivity::class.java)
         } else if (sportKey.equals(Constants.TAG_BASKETBALL, ignoreCase = true)) {
             Intent(this@UpComingContestDetailActivity, BasketBallCreateTeamActivity::class.java)
         } else {
             Intent(this@UpComingContestDetailActivity, CreateTeamActivity::class.java)
         }*/
        intent = Intent(this@UpComingContestDetailActivity, CreateTeamActivity::class.java)
        intent.putExtra(Constants.KEY_MATCH_KEY, matchKey)
        intent.putExtra(Constants.KEY_TEAM_VS, teamVsName)
        intent.putExtra(Constants.KEY_TEAM_FIRST_URL, teamFirstUrl)
        intent.putExtra(Constants.KEY_TEAM_SECOND_URL, teamSecondUrl)
        intent.putExtra(Constants.KEY_STATUS_HEADER_TEXT, headerText)
        intent.putExtra(Constants.KEY_STATUS_IS_TIMER_HEADER, true)
        intent.putExtra(Constants.SPORT_KEY, sportKey)
        intent.putExtra(Constants.KEY_FANTASY_TYPE, fantasyType)
        intent.putExtra(Constants.FIRST_CREATE_TEAM, isForFirstTeamCreate)
        intent.putExtra(Constants.KEY_CONTEST_FIRST_TIME_DATA, contestForFirstTime)

        //  startActivity(intent);
        startActivityForResult(intent, 102)
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
                        mainBinding.matchHeaderInfo.tvTimeTimer.text = twoDigitString(
                            TimeUnit.MILLISECONDS.toHours(
                                millisUntilFinished
                            )
                        ) + "h : " + twoDigitString(minutes) + "m : " + twoDigitString(seconds) + "s "
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
                        mainBinding.matchHeaderInfo.tvTimeTimer.setText("00h : 00m : 00s")
                    }
                }
            countDownTimer.start()
        } catch (e: Exception) {
        }

    }


    // setup tab data
    class TabAdapter internal constructor(fm: FragmentManager?) :
        FragmentStatePagerAdapter(fm!!) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }
    }

    // setup layout for toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_wallet, menu)
        return true
    }

    private fun openNotificationActivity() {
        startActivity(Intent(this@UpComingContestDetailActivity, NotificationActivity::class.java))
    }

    private fun openWalletActivity() {
        startActivity(Intent(this@UpComingContestDetailActivity, MyWalletActivity::class.java))
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        if (contest.isjoined) {
            if (contest.multi_entry == 1) mainBinding.btnJoinContest.text =
                "JOIN+" else mainBinding.btnJoinContest.setText(
                "INVITE CONTEST"
            )
        } else {
            mainBinding.btnJoinContest.text = "Join Contest Now"
        }
        if (supportFragmentManager.fragments.size > 1) {
            var leaderBoardFragment: LeaderBoardFragment? = null
            if (supportFragmentManager.fragments[1] is LeaderBoardFragment) leaderBoardFragment =
                supportFragmentManager.fragments[1] as LeaderBoardFragment
            leaderBoardFragment!!.refreshLeaderBoard()
        }
    }

    // click listener for toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.navigation_notification -> {

                true
            }
            R.id.navigation_wallet -> {
                startActivity(
                    Intent(
                        this@UpComingContestDetailActivity,
                        MyWalletActivity::class.java
                    )
                )
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun openPlayerPointActivityForLeatherBoard(
        isForLeaderBoard: Boolean,
        teamId: Int,
        challengeId: Int,
        teamName: String,
        points: String,
        sporttkey: String,
        pFantasyType: Int,
    ) {
        val intent: Intent
        /*if (sporttkey.equals(Constants.TAG_FOOTBALL, ignoreCase = true)) intent = Intent(
            context,
            FootballTeamPreviewPointActivity::class.java
        ) else if (sporttkey.equals(Constants.TAG_BASKETBALL, ignoreCase = true)) {
            intent = Intent(context, BasketBallTeamPreviewPointActivity::class.java)
        } else {

        }*/
        intent = Intent(applicationContext, TeamPreviewPointActivity::class.java)
        intent.putExtra("teamId", teamId)
        intent.putExtra("challengeId", challengeId)
        intent.putExtra("isForLeaderBoard", !isForLeaderBoard)
        intent.putExtra(Constants.KEY_TEAM_NAME, teamName)
        intent.putExtra("tPoints", points)
        intent.putExtra(Constants.SPORT_KEY, sporttkey)
        intent.putExtra(Constants.KEY_FANTASY_TYPE, pFantasyType)
        startActivity(intent)
    }

    private fun showPopUpImage(imageUrl: String) {
        val dialogue = Dialog(this)
        dialogue.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogue.setContentView(R.layout.popup_gadget_image_dialog)
        dialogue.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogue.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogue.setCancelable(false)
        dialogue.setCanceledOnTouchOutside(false)
        dialogue.setTitle(null)
        val imageView = dialogue.findViewById<ImageView>(R.id.iv_gadget_league)
        AppUtils.loadPopupImage(imageView, imageUrl)
        val img_Close: CardView = dialogue.findViewById(R.id.img_Close)
        img_Close.setOnClickListener { dialogue.dismiss() }
        if (dialogue.isShowing) dialogue.dismiss()
        dialogue.show()
    }

    companion object {
        var listener: TeamCreatedListener? = null
    }

    override fun getJoinedUser(joinedUser: Int) {
        contest.joinedusers = joinedUser
        if (contest.challenge_type == "percentage") {
            mainBinding.txtStartValue.text =
                contest.joinedusers.toString() + " teams already entered"
            mainBinding.txtEndValue.text = ""
            mainBinding.progressBar.max = 16
            mainBinding.progressBar.progress = 8
        } else {
            mainBinding.progressBar.max = contest.maximum_user
            mainBinding.progressBar.progress = contest.joinedusers
            val left: Int = contest.maximum_user - contest.joinedusers
            if (left != 0) {
                mainBinding.txtStartValue.text =
                    "   $left Spots  left"
            } else {
                mainBinding.txtStartValue.text =
                    "Challenge Closed"
                mainBinding.btnJoinContest.visibility = View.GONE
            }
            mainBinding.txtEndValue.text = contest.maximum_user.toString() + " Spots"
        }
    }

}