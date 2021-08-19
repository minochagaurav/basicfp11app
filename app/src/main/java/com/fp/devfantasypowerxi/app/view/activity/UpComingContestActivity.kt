package com.fp.devfantasypowerxi.app.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.facebook.drawee.backends.pipeline.Fresco
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.ContestRequest
import com.fp.devfantasypowerxi.app.api.response.*
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.fragment.MyJoinedContestFragment
import com.fp.devfantasypowerxi.app.view.fragment.MyTeamFragment
import com.fp.devfantasypowerxi.app.view.fragment.UpComingContestFragment
import com.fp.devfantasypowerxi.app.view.listners.OnContestItemClickListener
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivityUpComingContestBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

// made by Gaurav Minocha
class UpComingContestActivity : AppCompatActivity(), OnContestItemClickListener {
    var matchKey: String? = null
    var teamVsName: String? = null
    var teamFirstUrl: String? = null
    var teamSecondUrl: String? = null
    var date: String? = null
    var userReferCode = ""
    private var teamCount = 0
    private var joinedContestCount = 0
    var isForContestDetails: String = ""
    var sportKey: String = Constants.TAG_CRICKET
    var fantasyType = 0
    var isForFirstTeamCreate = false
    lateinit var contestForFirstTime: League
    var contestId = 0
    var fantasyTypeList = ArrayList<FantasyType>()

    @Inject
    lateinit var oAuthRestService: OAuthRestService
    lateinit var mTabAdapter: TabAdapter
    lateinit var mainBinding: ActivityUpComingContestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_up_coming_contest)
        MyApplication.getAppComponent()!!.inject(this@UpComingContestActivity)
        userReferCode =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_REFER_CODE)!!
        initialize()
        setFantasyType(fantasyTypeList)
    }

    // initialize toolbar and set tabs
    fun initialize() {
        setSupportActionBar(mainBinding.myToolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = getString(R.string.contest)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
        if (intent != null && intent.extras != null) {
            matchKey = intent.extras!!.getString(Constants.KEY_MATCH_KEY)
            teamVsName = intent.extras!!.getString(Constants.KEY_TEAM_VS)
            teamFirstUrl = intent.extras!!.getString(Constants.KEY_TEAM_FIRST_URL)
            teamSecondUrl = intent.extras!!.getString(Constants.KEY_TEAM_SECOND_URL)
            date = intent.extras!!.getString(Constants.KEY_STATUS_HEADER_TEXT)
            isForContestDetails = intent.extras!!.getString(Constants.KEY_STATUS_HEADER_TEXT)!!
            sportKey = intent.extras!!.getString(Constants.SPORT_KEY)!!
            fantasyTypeList = intent.getParcelableArrayListExtra(Constants.KEY_FANTASY_TYPE_LIST)!!
        }
        mainBinding.matchHeaderInfo.tvTeamVs.text = teamVsName
        mainBinding.matchHeaderInfo.ivTeamFirst.setImageURI(teamFirstUrl)
        mainBinding.matchHeaderInfo.ivTeamSecond.setImageURI(teamSecondUrl)

        mTabAdapter = TabAdapter(
            supportFragmentManager
        )
        val myTeamFragment = MyTeamFragment()
        val bundle = Bundle()
        bundle.putString(Constants.KEY_MATCH_KEY, matchKey)
        bundle.putString(Constants.SPORT_KEY, sportKey)
        bundle.putInt(Constants.KEY_FANTASY_TYPE, fantasyType)
        myTeamFragment.arguments = bundle
        val upComingContestFragment = UpComingContestFragment()
        upComingContestFragment.arguments = bundle
        val myJoinedContestFragment = MyJoinedContestFragment()
        myJoinedContestFragment.arguments = bundle
        mTabAdapter.addFragment(upComingContestFragment, "Contests")
        mTabAdapter.addFragment(myJoinedContestFragment, "My Contests (0)")
        mTabAdapter.addFragment(myTeamFragment, "My Teams (0)")
        mainBinding.viewPager.adapter = mTabAdapter
        mainBinding.tabLayout.setupWithViewPager(mainBinding.viewPager)
        mainBinding.viewPager.offscreenPageLimit = 1

        val root: View = mainBinding.tabLayout.getChildAt(0)
        if (root is LinearLayout) {
            (root).showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
            val drawable = GradientDrawable()
            drawable.setColor(resources.getColor(R.color.selectedColor))
            drawable.setSize(2, root.getHeight())
            (root).dividerDrawable = drawable
        }
        try {
            val countDownTimer: CountDownTimer =
                object : CountDownTimer(AppUtils.eventDateMileSecond(date), 1000) {
                    @SuppressLint("SetTextI18n")
                    override fun onTick(millisUntilFinished: Long) {
                        val seconds = millisUntilFinished / 1000 % 60
                        val minutes = millisUntilFinished / (1000 * 60) % 60
                        val diffHours = millisUntilFinished / (60 * 60 * 1000)
                        mainBinding.matchHeaderInfo.tvTimeTimer.setText(
                            twoDigitString(
                                TimeUnit.MILLISECONDS.toHours(
                                    millisUntilFinished
                                )
                            ) + "h : " + twoDigitString(minutes) + "m : " + twoDigitString(seconds) + "s "
                        )
                    }

                    private fun twoDigitString(number: Long): String {
                        if (number == 0L) {
                            return "00"
                        } else if (number / 10 == 0L) {
                            return "0$number"
                        }
                        return number.toString()
                    }

                    override fun onFinish() {
                        mainBinding.matchHeaderInfo.tvTimeTimer.text = "00h : 00m : 00s"
                    }
                }
            countDownTimer.start()
        } catch (e: Exception) {
        }

        mainBinding.fantasyTypeBottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_classic -> {
                    //  if (!(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof HomeFragment)) {
                    fantasyType = fantasyTypeList[0].type
                    AppUtils.setFantasyType(fantasyType)
                    mainBinding.viewPager.setAdapter(mTabAdapter)
                    mTabAdapter.notifyDataSetChanged()
                    openFantasyRuleOncePerDay(Constants.TAG_FANTASY_TYPE_CLASSIC)
                }
                R.id.navigation_batting -> {
                    // if (!(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof MyMatchesFragment)) {
                    fantasyType = fantasyTypeList[1].type
                    AppUtils.setFantasyType(fantasyType)
                    mainBinding.viewPager.setAdapter(mTabAdapter)
                    mTabAdapter.notifyDataSetChanged()
                    openFantasyRuleOncePerDay(Constants.TAG_FANTASY_TYPE_BATTING)
                }
                R.id.navigation_bowling -> {
                    ///  if (!(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof AddCashFragment)) {
                    fantasyType = fantasyTypeList[2].type
                    AppUtils.setFantasyType(fantasyType)
                    mainBinding.viewPager.setAdapter(mTabAdapter)
                    mTabAdapter.notifyDataSetChanged()
                    openFantasyRuleOncePerDay(Constants.TAG_FANTASY_TYPE_BOWLING)
                }
                R.id.navigation_premium -> {
                    //  if (!(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof MoreFragment)) {
                    fantasyType = fantasyTypeList[3].type
                    AppUtils.setFantasyType(fantasyType)
                    mainBinding.viewPager.setAdapter(mTabAdapter)
                    mTabAdapter.notifyDataSetChanged()
                    openFantasyRuleOncePerDay(Constants.TAG_FANTASY_TYPE_PREMIUM)
                }
            }
            true
        }
        if (!MyApplication.preferenceDBTwo!!.getBoolean(
                Constants.SHOW_RULE_POPUP_CLASSIC_CRICKET,
                false
            )
        ) {
            MyApplication.preferenceDBTwo!!.putBoolean(
                Constants.SHOW_RULE_POPUP_CLASSIC_CRICKET,
                true
            )
            getFantasyRule(Constants.TAG_FANTASY_TYPE_CLASSIC)
        }
    }
    fun movetoContest() {
        mainBinding.tabLayout.getTabAt(0)!!.select()
    }

    private fun openFantasyRuleOncePerDay(tag: String) {
        val calendar = Calendar.getInstance()
        val currentDay = calendar[Calendar.DAY_OF_MONTH]
        if (tag.equals(Constants.TAG_FANTASY_TYPE_BATTING, ignoreCase = true)) {
            val lastDay: Int =
                MyApplication.preferenceDB!!.getInt(Constants.SHOW_RULE_POPUP_BATTING_CRICKET, 0)
            if (lastDay != currentDay) {
                MyApplication.preferenceDB!!.putInt(
                    Constants.SHOW_RULE_POPUP_BATTING_CRICKET,
                    currentDay
                )
                getFantasyRule(Constants.TAG_FANTASY_TYPE_BATTING)
            }
        } else if (tag.equals(Constants.TAG_FANTASY_TYPE_BOWLING, ignoreCase = true)) {
            val lastDay: Int =
                MyApplication.preferenceDB!!.getInt(Constants.SHOW_RULE_POPUP_BOWLING_CRICKET, 0)
            if (lastDay != currentDay) {
                MyApplication.preferenceDB!!.putInt(
                    Constants.SHOW_RULE_POPUP_BOWLING_CRICKET,
                    currentDay
                )
                getFantasyRule(Constants.TAG_FANTASY_TYPE_BOWLING)
            }
        } else if (tag.equals(Constants.TAG_FANTASY_TYPE_PREMIUM, ignoreCase = true)) {
            val lastDay: Int =
                MyApplication.preferenceDB!!.getInt(Constants.SHOW_RULE_POPUP_PREMIUM_CRICKET, 0)
            if (lastDay != currentDay) {
                MyApplication.preferenceDB!!.putInt(
                    Constants.SHOW_RULE_POPUP_PREMIUM_CRICKET,
                    currentDay
                )
                getFantasyRule(Constants.TAG_FANTASY_TYPE_PREMIUM)
            }
        } else {
            val lastDay: Int =
                MyApplication.preferenceDB!!.getInt(Constants.SHOW_RULE_POPUP_CLASSIC_CRICKET, 0)
            if (lastDay != currentDay) {
                MyApplication.preferenceDB!!.putInt(
                    Constants.SHOW_RULE_POPUP_CLASSIC_CRICKET,
                    currentDay
                )
                getFantasyRule(Constants.TAG_FANTASY_TYPE_CLASSIC)
            }
        }
    }
    fun openJoinedContestActivity(isForDetail: Boolean, contest: League) {
        if (isForDetail) {
            val intent =
                Intent(this@UpComingContestActivity, UpComingContestDetailActivity::class.java)
            intent.putExtra(Constants.KEY_MATCH_KEY, matchKey)
            intent.putExtra(Constants.KEY_TEAM_VS, teamVsName)
            intent.putExtra(Constants.KEY_TEAM_FIRST_URL, teamFirstUrl)
            intent.putExtra(Constants.KEY_TEAM_SECOND_URL, teamSecondUrl)
            intent.putExtra(Constants.KEY_CONTEST_DATA, contest)
            intent.putExtra(Constants.KEY_STATUS_HEADER_TEXT, date)
            intent.putExtra(Constants.KEY_STATUS_IS_TIMER_HEADER, true)
            intent.putExtra(Constants.KEY_TEAM_COUNT, teamCount)
            intent.putExtra(Constants.KEY_STATUS_IS_FOR_CONTEST_DETAILS, true)
            intent.putExtra(Constants.SPORT_KEY, AppUtils.getSaveSportKey())
            intent.putExtra(Constants.KEY_FANTASY_TYPE, AppUtils.getFantasyType())
            startActivity(intent)
        } else {
            val intent = Intent(this@UpComingContestActivity, MyTeamsActivity::class.java)
            intent.putExtra(Constants.KEY_MATCH_KEY, matchKey)
            intent.putExtra(Constants.KEY_TEAM_VS, teamVsName)
            intent.putExtra(Constants.KEY_TEAM_FIRST_URL, teamFirstUrl)
            intent.putExtra(Constants.KEY_TEAM_SECOND_URL, teamSecondUrl)
            intent.putExtra(Constants.KEY_IS_FOR_JOIN_CONTEST, true)
            intent.putExtra(Constants.KEY_CONTEST_DATA, contest)
            intent.putExtra(Constants.KEY_STATUS_HEADER_TEXT, date)
            intent.putExtra(Constants.KEY_TEAM_COUNT, teamCount)
            intent.putExtra(Constants.KEY_STATUS_IS_TIMER_HEADER, true)
            intent.putExtra(Constants.SPORT_KEY, AppUtils.getSaveSportKey())
            intent.putExtra(Constants.KEY_FANTASY_TYPE, AppUtils.getFantasyType())
            startActivity(intent)
        }
    }

    private fun getFantasyRule(fantasyTag: String?) {
        val contestRequest = ContestRequest()
        contestRequest.sport_key = AppUtils.getSaveSportKey()
        contestRequest.fantasy_type = AppUtils.getFantasyType()
        val bankDetailResponseCustomCall: CustomCallAdapter.CustomCall<FantasyRuleResponse> =
            oAuthRestService.getFantasyRule(contestRequest)
        bankDetailResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<FantasyRuleResponse> {
            override fun success(response: Response<FantasyRuleResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val getWinnerScoreCardResponse: FantasyRuleResponse = response.body()!!
                    if (getWinnerScoreCardResponse.status == 1) {
                        val fantasyRuleResponse: String = getWinnerScoreCardResponse.result
                        //if (priceList.size() > 0) {
                        AppUtils.showFantasyRulePopup(
                            this@UpComingContestActivity,
                            fantasyTag!!,
                            fantasyRuleResponse,
                            getWinnerScoreCardResponse.banner
                        )
                        //  }
                    }
                }
            }

            override fun failure(e: ApiException?) {
                e!!.printStackTrace()
            }
        })
    }
    fun editOrClone(list: ArrayList<PlayerListResult>, teamId: Int) {
        val intent =Intent(this@UpComingContestActivity, CreateTeamActivity::class.java)
        /* intent = if (sportKey.equals(Constants.TAG_FOOTBALL, ignoreCase = true)) {
             Intent(this@MyTeamsActivity, FootballCreateTeamActivity::class.java)
         } else if (sportKey.equals(Constants.TAG_BASKETBALL, ignoreCase = true)) {
             Intent(this@MyTeamsActivity, BasketBallCreateTeamActivity::class.java)
         } else {
             Intent(this@MyTeamsActivity, CreateTeamActivity::class.java)
         }*/
        intent.putExtra(Constants.KEY_MATCH_KEY, matchKey)
        intent.putExtra(Constants.KEY_TEAM_VS, teamVsName)
        intent.putExtra(Constants.KEY_TEAM_FIRST_URL, teamFirstUrl)
        intent.putExtra(Constants.KEY_TEAM_SECOND_URL, teamSecondUrl)
        intent.putExtra(Constants.KEY_TEAM_ID, teamId)
        intent.putExtra(Constants.KEY_STATUS_HEADER_TEXT, isForContestDetails)
        intent.putExtra(Constants.KEY_STATUS_IS_TIMER_HEADER, isForContestDetails)
        intent.putExtra("isFromEditOrClone", true)
        intent.putExtra("selectedList", list)
        intent.putExtra(Constants.SPORT_KEY, sportKey)
        intent.putExtra(Constants.KEY_FANTASY_TYPE, fantasyType)
        startActivity(intent)
    }


    fun openPreviewActivity(list: ArrayList<PlayerListResult>, teamName: String?) {
        /*val intent: Intent
        intent = if (sportKey.equals(Constants.TAG_FOOTBALL, ignoreCase = true)) {
            Intent(this@MyTeamsActivity, FootballTeamPreviewActivity::class.java)
        } else if (sportKey.equals(Constants.TAG_BASKETBALL, ignoreCase = true)) {
            Intent(this@MyTeamsActivity, BasketBallTeamPreviewActivity::class.java)
        } else {

        }*/
        val intent = Intent(this@UpComingContestActivity, TeamPreviewActivity::class.java)
        intent.putExtra(Constants.KEY_MATCH_KEY, matchKey)
        intent.putExtra(Constants.KEY_TEAM_VS, teamVsName)
        intent.putExtra(Constants.KEY_TEAM_FIRST_URL, teamFirstUrl)
        intent.putExtra(Constants.KEY_TEAM_SECOND_URL, teamSecondUrl)
        intent.putExtra(Constants.KEY_TEAM_NAME, teamName)
        intent.putExtra(Constants.KEY_STATUS_HEADER_TEXT, isForContestDetails)
        intent.putExtra(Constants.KEY_STATUS_IS_TIMER_HEADER, isForContestDetails)
        intent.putExtra(Constants.SPORT_KEY, sportKey)
        intent.putExtra(Constants.KEY_FANTASY_TYPE, fantasyType)
        val selectedWkList = ArrayList<PlayerListResult>()
        val selectedBatLiSt = ArrayList<PlayerListResult>()
        val selectedArList = ArrayList<PlayerListResult>()
        val selectedBowlList = ArrayList<PlayerListResult>()
        val cList = ArrayList<PlayerListResult>()

        for (player in list) {
            when (player.role) {
                Constants.KEY_PLAYER_ROLE_KEEP -> selectedWkList.add(player)
                Constants.KEY_PLAYER_ROLE_BAT -> selectedBatLiSt.add(player)
                Constants.KEY_PLAYER_ROLE_ALL_R -> selectedArList.add(player)
                Constants.KEY_PLAYER_ROLE_BOL -> selectedBowlList.add(player)
            }
        }

        intent.putExtra(Constants.KEY_TEAM_LIST_WK, selectedWkList)
        intent.putExtra(Constants.KEY_TEAM_LIST_BAT, selectedBatLiSt)
        intent.putExtra(Constants.KEY_TEAM_LIST_AR, selectedArList)
        intent.putExtra(Constants.KEY_TEAM_LIST_BOWL, selectedBowlList)
        intent.putExtra(Constants.KEY_TEAM_LIST_C, cList)
        startActivity(intent)
    }
    private fun setFantasyType(list: ArrayList<FantasyType>) {
        if (list.size > 1) {
            if (AppUtils.saveSportKey == "" ||
                AppUtils.saveSportKey == Constants.TAG_CRICKET
            ) mainBinding.fantasyTypeBottomNavigation.visibility = View.VISIBLE
        } else {
            mainBinding.fantasyTypeBottomNavigation.visibility = View.GONE
        }
        for (fantasyType in list) {
            mainBinding.fantasyTypeTab.addTab(
                mainBinding.fantasyTypeTab.newTab().setText(fantasyType.name)
            )
        }
        mainBinding.fantasyTypeTab.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                fantasyType = list[tab.position].type
                mainBinding.viewPager.adapter = mTabAdapter
                mTabAdapter.notifyDataSetChanged()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    // add layout for toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_wallet, menu)
        return true
    }

    // toolbar click listeners
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.navigation_notification -> {
                true
            }
            R.id.navigation_wallet -> {
                startActivity(Intent(this@UpComingContestActivity, MyWalletActivity::class.java))
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun joinByContestCode() {
        val intent =
            Intent(this@UpComingContestActivity, JoinContestByInviteCodeActivity::class.java)
        intent.putExtra(Constants.KEY_MATCH_KEY, matchKey)
        intent.putExtra(Constants.KEY_TEAM_VS, teamVsName)
        intent.putExtra(Constants.KEY_TEAM_FIRST_URL, teamFirstUrl)
        intent.putExtra(Constants.KEY_TEAM_SECOND_URL, teamSecondUrl)
        intent.putExtra(Constants.KEY_STATUS_HEADER_TEXT, date)
        intent.putExtra(Constants.KEY_STATUS_IS_TIMER_HEADER, true)
        intent.putExtra(Constants.SPORT_KEY, AppUtils.saveSportKey)
        intent.putExtra(Constants.KEY_FANTASY_TYPE, AppUtils.getFantasyType())
        startActivity(intent)
    }

    fun openAllContestActivity(categoryId: Int) {
        val intent = Intent(this@UpComingContestActivity, AllContestActivity::class.java)
        intent.putExtra(Constants.KEY_MATCH_KEY, matchKey)
        intent.putExtra(Constants.KEY_TEAM_VS, teamVsName)
        intent.putExtra(Constants.KEY_TEAM_FIRST_URL, teamFirstUrl)
        intent.putExtra(Constants.KEY_TEAM_SECOND_URL, teamSecondUrl)
        intent.putExtra(Constants.KEY_STATUS_HEADER_TEXT, date)
        intent.putExtra(Constants.KEY_TEAM_COUNT, teamCount)
        intent.putExtra(Constants.KEY_STATUS_IS_TIMER_HEADER, true)
        intent.putExtra(Constants.KEY_STATUS_IS_FOR_CONTEST_DETAILS, true)
        intent.putExtra(Constants.KEY_ALL_CONTEST, categoryId)
        intent.putExtra(Constants.SPORT_KEY, AppUtils.getSaveSportKey())
        intent.putExtra(Constants.KEY_FANTASY_TYPE, AppUtils.getFantasyType())
        startActivity(intent)
    }

    fun openPrivateCreateContest() {
        val intent = Intent(this@UpComingContestActivity, PrivateContestActivity::class.java)
        intent.putExtra(Constants.KEY_MATCH_KEY, matchKey)
        intent.putExtra(Constants.KEY_TEAM_VS, teamVsName)
        intent.putExtra(Constants.KEY_TEAM_FIRST_URL, teamFirstUrl)
        intent.putExtra(Constants.KEY_TEAM_SECOND_URL, teamSecondUrl)
        intent.putExtra(Constants.KEY_STATUS_HEADER_TEXT, date)
        intent.putExtra(Constants.KEY_STATUS_IS_TIMER_HEADER, true)
        intent.putExtra(Constants.SPORT_KEY, AppUtils.getSaveSportKey())
        intent.putExtra(Constants.KEY_FANTASY_TYPE, AppUtils.getFantasyType())
        startActivity(intent)
    }

    fun creteTeam(isFromFragment: Boolean) {
        val intent: Intent
        /*  intent = if (sportKey.equals(Constants.TAG_FOOTBALL, ignoreCase = true)) {
              Intent(this@UpComingContestActivity, FootballCreateTeamActivity::class.java)
          } else if (sportKey.equals(Constants.TAG_BASKETBALL, ignoreCase = true)) {
              Intent(this@UpComingContestActivity, BasketBallCreateTeamActivity::class.java)
          } else {
              Intent(this@UpComingContestActivity, CreateTeamActivity::class.java)
          }*/
        intent = Intent(this@UpComingContestActivity, CreateTeamActivity::class.java)
        intent.putExtra(Constants.KEY_MATCH_KEY, matchKey)
        intent.putExtra(Constants.KEY_TEAM_VS, teamVsName)
        intent.putExtra(Constants.KEY_TEAM_FIRST_URL, teamFirstUrl)
        intent.putExtra(Constants.KEY_TEAM_SECOND_URL, teamSecondUrl)
        intent.putExtra(Constants.KEY_STATUS_HEADER_TEXT, date)
        intent.putExtra(Constants.KEY_STATUS_IS_TIMER_HEADER, true)
        intent.putExtra(Constants.SPORT_KEY, AppUtils.getSaveSportKey())
        intent.putExtra(Constants.KEY_FANTASY_TYPE, AppUtils.getFantasyType())
        if (isFromFragment) {
            intent.putExtra(Constants.FIRST_CREATE_TEAM, false)
        } else {
            intent.putExtra(Constants.FIRST_CREATE_TEAM, isForFirstTeamCreate)
            intent.putExtra(Constants.KEY_CONTEST_FIRST_TIME_DATA, contestForFirstTime)
        }
        // startActivity(intent);
        startActivityForResult(intent, 102)
    }

    fun setTabTitle(teamCount: Int, joinedContestCount: Int) {
        this.teamCount = teamCount
        this.joinedContestCount = joinedContestCount
        if (mainBinding.tabLayout.tabCount <= 3) {
            mainBinding.tabLayout.getTabAt(1)?.text = "My Contests ($joinedContestCount)"
            mainBinding.tabLayout.getTabAt(2)?.text = "My Teams ($teamCount)"
        }
    }

    fun getWinnerPriceCard(contestId: Int, amount: String) {
        val contestRequest = ContestRequest()
        contestRequest.matchkey = matchKey!!
        contestRequest.sport_key = AppUtils.getSaveSportKey()
        contestRequest.fantasy_type = AppUtils.getFantasyType()
        contestRequest.challenge_id = contestId.toString() + ""
        val bankDetailResponseCustomCall: CustomCallAdapter.CustomCall<GetWinnerScoreCardResponse> =
            oAuthRestService.getWinnersPriceCard(
                contestRequest.matchkey,
                contestRequest.challenge_id
            )
        bankDetailResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<GetWinnerScoreCardResponse> {
            override fun success(response: Response<GetWinnerScoreCardResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val getWinnerScoreCardResponse: GetWinnerScoreCardResponse = response.body()!!
                    if (getWinnerScoreCardResponse.status == 1 && getWinnerScoreCardResponse.result
                            .size > 0
                    ) {
                        val priceList: ArrayList<GetWinnerScoreCardResult> =
                            getWinnerScoreCardResponse.result
                        if (priceList.size > 0) {
                            AppUtils.showWinningPopup(
                                this@UpComingContestActivity,
                                priceList,
                                "" + amount
                            )
                        }
                    }
                }
            }

            override fun failure(e: ApiException?) {
                e!!.printStackTrace()
            }
        })
    }


    // setup tab data
    class TabAdapter(fm: FragmentManager?) :
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

    override fun onContestClick(contest: League?, isForDetail: Boolean) {
        if (isForDetail) {
            val intent =
                Intent(this@UpComingContestActivity, UpComingContestDetailActivity::class.java)
            intent.putExtra(Constants.KEY_MATCH_KEY, matchKey)
            intent.putExtra(Constants.KEY_TEAM_VS, teamVsName)
            intent.putExtra(Constants.KEY_TEAM_FIRST_URL, teamFirstUrl)
            intent.putExtra(Constants.KEY_TEAM_SECOND_URL, teamSecondUrl)
            intent.putExtra(Constants.KEY_CONTEST_DATA, contest)
            intent.putExtra(Constants.KEY_STATUS_HEADER_TEXT, date)
            intent.putExtra(Constants.KEY_TEAM_COUNT, teamCount)
            intent.putExtra(Constants.KEY_STATUS_IS_TIMER_HEADER, true)
            intent.putExtra(Constants.KEY_STATUS_IS_FOR_CONTEST_DETAILS, true)
            intent.putExtra(Constants.SPORT_KEY, AppUtils.getSaveSportKey())
            intent.putExtra(Constants.KEY_FANTASY_TYPE, AppUtils.getFantasyType())
            startActivity(intent)
        } else {
            if (teamCount > 0) {
                val intent = Intent(this@UpComingContestActivity, MyTeamsActivity::class.java)
                intent.putExtra(Constants.KEY_MATCH_KEY, matchKey)
                intent.putExtra(Constants.KEY_TEAM_VS, teamVsName)
                intent.putExtra(Constants.KEY_TEAM_FIRST_URL, teamFirstUrl)
                intent.putExtra(Constants.KEY_TEAM_SECOND_URL, teamSecondUrl)
                intent.putExtra(Constants.KEY_IS_FOR_JOIN_CONTEST, true)
                intent.putExtra(Constants.KEY_CONTEST_DATA, contest)
                intent.putExtra(Constants.KEY_STATUS_HEADER_TEXT, date)
                intent.putExtra(Constants.KEY_STATUS_IS_TIMER_HEADER, true)
                intent.putExtra(Constants.SPORT_KEY, AppUtils.getSaveSportKey())
                intent.putExtra(Constants.KEY_FANTASY_TYPE, AppUtils.getFantasyType())
                startActivity(intent)
            } else {
                isForFirstTeamCreate = true
                contestForFirstTime = contest!!
                creteTeam(false)
            }
        }
    }

}