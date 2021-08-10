package com.fp.devfantasypowerxi.app.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.response.League
import com.fp.devfantasypowerxi.app.api.response.NormalResponse
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.adapter.SelectedUnSelectedPlayerAdapter
import com.fp.devfantasypowerxi.app.view.fragment.CreateTeamPlayerFragment
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivityCreateTeamBinding
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

// made by Gaurav Minocha
class CreateTeamActivity : AppCompatActivity() {
    @Inject
    lateinit var oAuthRestService: OAuthRestService
    var matchKey: String = ""
    var teamVsName: String? = ""
    var teamFirstUrl: String = ""
    var teamSecondUrl: String = ""
    var isForFirstTeamCreate = false
    var contestFirstTime = League()

    var exeedCredit = false
    private val WK = 1
    private val BAT = 2
    private val AR = 3
    private val BOWLER = 4
    var teamId = 0
    //var allPlayerList: ArrayList<Player> = ArrayList<Player>()
    // PlayerMaxLimit playerMaxLimit;
    //PlayerMinLimit playerMinLimit;
    var isPointsSortingGlobal = false
    var isCreditsGlobal = false
    var isFromEditOrClone = false
    var headerText: String = ""
    var isShowTimer = false
    var isPointSelected = false
    var isCreditSelected = false
    var counterValue = 0
    lateinit var mainBinding: ActivityCreateTeamBinding
    var fantasyType = 1
    lateinit var mSelectedUnSelectedPlayerAdapter: SelectedUnSelectedPlayerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_create_team
        )
        MyApplication.getAppComponent()!!.inject(this@CreateTeamActivity)
        mainBinding.ivTeamPreview.setOnClickListener {
            startActivity(Intent(this@CreateTeamActivity, TeamPreviewActivity::class.java))
        }
        mainBinding.btnCreateTeam.setOnClickListener {
            startActivity(Intent(this@CreateTeamActivity, ChooseCandVCActivity::class.java))
        }
        initialize()
    }

    // toolbar click event
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // initialize toolbar
    @SuppressLint("SetTextI18n")
    fun initialize() {
        setSupportActionBar(mainBinding.myToolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = getString(R.string.create_team)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }

        if (intent != null && intent.extras != null) {
            if (intent.extras!!.getBoolean("isFromEditOrClone")) {
                isFromEditOrClone = intent.extras!!.getBoolean("isFromEditOrClone")
                //  selectedList = intent.getSerializableExtra("selectedList") as ArrayList<Player?>?
                teamId = intent.extras!!.getInt(Constants.KEY_TEAM_ID)
            }
            matchKey = intent.extras!!.getString(Constants.KEY_MATCH_KEY)!!
            teamVsName = intent.extras!!.getString(Constants.KEY_TEAM_VS)!!
            teamFirstUrl = intent.extras!!.getString(Constants.KEY_TEAM_FIRST_URL)!!
            teamSecondUrl = intent.extras!!.getString(Constants.KEY_TEAM_SECOND_URL)!!
            fantasyType = intent.extras!!.getInt(Constants.KEY_FANTASY_TYPE)
            headerText = intent.extras!!.getString(Constants.KEY_STATUS_HEADER_TEXT, "")
            isShowTimer = intent.extras!!.getBoolean(Constants.KEY_STATUS_IS_TIMER_HEADER, false)
            isForFirstTeamCreate = intent.extras!!.getBoolean(Constants.FIRST_CREATE_TEAM, false)
            contestFirstTime = intent.getParcelableExtra(Constants.KEY_CONTEST_FIRST_TIME_DATA)!!
        }
        setTeamNames()
        mainBinding.matchHeaderInfo.tvTeamVs.text = teamVsName
        mainBinding.ivTeamFirst.setImageURI(teamFirstUrl)
        mainBinding.ivTeamSecond.setImageURI(teamSecondUrl)

        if (isShowTimer) {
            showTimer()
        } else {
            if (headerText.equals("Winner Declared", ignoreCase = true)) {
                mainBinding.matchHeaderInfo.tvTimeTimer.setText("Winner Declared")
                mainBinding.matchHeaderInfo.tvTimeTimer.setTextColor(Color.parseColor("#08114d"))
            } else if (headerText.equals("In Progress", ignoreCase = true)) {
                mainBinding.matchHeaderInfo.tvTimeTimer.setText("In Progress")
                mainBinding.matchHeaderInfo.tvTimeTimer.setTextColor(Color.parseColor("#16ae28"))
            }
        }


        //   mBinding.tvPlayerCountPick.setText("Pick 1-4 Wicket-Keepers");
        when (fantasyType) {
            1 -> {
                mainBinding.tvPlayerCountPick.text = "Pick 1-5 Wicket-Keepers"
            }
            3 -> {
                mainBinding.tvPlayerCountPick.text = "Pick 1-3 Wicket-Keepers"
            }
            else -> {
                mainBinding.tvPlayerCountPick.text = "Pick 1-4 Wicket-Keepers"
            }
        }


        // mBinding.viewPager.addOnPageChangeListener(new );dd;
        mSelectedUnSelectedPlayerAdapter = SelectedUnSelectedPlayerAdapter(applicationContext)

        mainBinding.rvSelected.adapter = mSelectedUnSelectedPlayerAdapter
        setupViewPager(mainBinding.viewPager)
    }

    private fun setTeamNames() {
        if (teamVsName != null) {
            val teams = teamVsName!!.split("Vs").toTypedArray()
            mainBinding.tvTeamNameFirst.text = teams[0]
            mainBinding.tvTeamNameSecond.text = teams[1]
        }
    }

    fun logout() {
        mainBinding.refreshing = true
        val baseRequest = BaseRequest()
        baseRequest.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        val userFullDetailsResponseCustomCall: CustomCallAdapter.CustomCall<NormalResponse> =
            oAuthRestService.logout(baseRequest)
        userFullDetailsResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<NormalResponse> {
            override fun success(response: Response<NormalResponse>) {
                mainBinding.refreshing = false
                val updateProfileResponse: NormalResponse = response.body()!!
                if (updateProfileResponse.status == 1) {
                    logout()
                } else {
                    AppUtils.showError(
                        this@CreateTeamActivity,
                        updateProfileResponse.message
                    )
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
            }
        })
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

    // setup view pager dynamically with header changes
    internal class ViewPagerAdapter(manager: FragmentManager?) :
        FragmentPagerAdapter(manager!!) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: List<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFrag(fragment: Fragment) {
            mFragmentList.add(fragment)
            //mFragmentTitleList.add(title);
        }

        override fun getPageTitle(position: Int): CharSequence {
            when (position) {
                0 -> return "WK " + "()"
                1 -> return "BAT " + "()"
                2 -> return "AR " + "()"
                3 -> return "BOWL " + "()"
            }
            return ""
        }

        override fun getItemPosition(`object`: Any): Int {
            // POSITION_NONE makes it possible to reload the PagerAdapter
            return POSITION_NONE
        }
    }


/*

    private fun getData() {
        val request = MyTeamRequest()
        request.setUser_id(MyApplication.tinyDB.getString(Constants.SHARED_PREFERENCE_USER_ID))
        request.setMatchKey(matchKey)
        request.setFantasyType(fantasyType)
        request.setSport_key(Constants.TAG_CRICKET)
        createTeamViewModel.loadPlayerListRequest(request)
        createTeamViewModel.getPlayerList().observe(this) { arrayListResource ->
            Log.d("Status ", "" + arrayListResource.getStatus())
            when (arrayListResource.getStatus()) {
                LOADING -> {
                    mBinding.setRefreshing(true)
                }
                ERROR -> {
                    mBinding.setRefreshing(false)
                    if (arrayListResource.getException().getResponse()
                            .code() >= 400 && arrayListResource.getException().getResponse()
                            .code() < 404
                    ) {
                        logout()
                    } else {
                        Toast.makeText(
                            MyApplication.appContext,
                            arrayListResource.getException().getErrorModel().errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                SUCCESS -> {
                    mBinding.setRefreshing(false)
                    if (arrayListResource.getData().getStatus() === 1 && arrayListResource.getData()
                            .getResult().size() > 0
                    ) {
                        allPlayerList = arrayListResource.getData().getResult()
                        limit = arrayListResource.getData().getLimit()

                        //   playerMaxLimit = limit.getPlayerMaxLimit();
                        // playerMinLimit = limit.getPlayerMinLimit();
                        totalCredit = limit.getTotalCredits()
                        totalPlayerCount = limit.getTotalPlayers()
                        maxTeamPlayerCount = limit.getTeamMaxPlayer()
                        setData()
                        for (player in allPlayerList) {
                            if (player.getRole()
                                    .equalsIgnoreCase(Constants.KEY_PLAYER_ROLE_KEEP)
                            ) wkList.add(player) else if (player.getRole()
                                    .equalsIgnoreCase(Constants.KEY_PLAYER_ROLE_BAT)
                            ) batList.add(player) else if (player.getRole()
                                    .equalsIgnoreCase(Constants.KEY_PLAYER_ROLE_BOL)
                            ) bolList.add(player) else if (player.getRole()
                                    .equalsIgnoreCase(Constants.KEY_PLAYER_ROLE_ALL_R)
                            ) arList.add(player)
                        }
                        if (selectedList.size > 0) {
                            var i = 0
                            while (i < allPlayerList.size) {
                                for (player in selectedList) {
                                    if (player.getId() === allPlayerList.get(i).getId()) {
                                        allPlayerList.get(i).setSelected(true)
                                        if (player.getCaptain() === 1) allPlayerList.get(i)
                                            .setCaptain(true)
                                        if (player.getVicecaptain() === 1) allPlayerList.get(i)
                                            .setVcCaptain(true)
                                    }
                                }
                                i++
                            }
                            setSelectedCountForEditOrClone()
                        }
                        setupViewPager(mBinding.viewPager)
                        if (!MyApplication.tinyDB2.getBoolean(
                                Constants.SKIP_CREATETEAM_INSTRUCTION,
                                false
                            )
                        ) {
                            callIntroductionScreen(
                                R.id.tabLayout,
                                "Player Category",
                                "Select a balanced team to help you win",
                                ShowcaseView.BELOW_SHOWCASE
                            )
                            MyApplication.tinyDB2.putBoolean(
                                Constants.SKIP_CREATETEAM_INSTRUCTION,
                                true
                            )
                        }
                    } else {
                        Toast.makeText(
                            MyApplication.appContext,
                            arrayListResource.getData().getMessage(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
*/

    // initialize viewpager
    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFrag(
            CreateTeamPlayerFragment.newInstance()
        )

        adapter.addFrag(
            CreateTeamPlayerFragment.newInstance()
        )
        adapter.addFrag(
            CreateTeamPlayerFragment.newInstance()
        )

        //    if(fantasyType==0||fantasyType ==2 || fantasyType ==3)
        adapter.addFrag(
            CreateTeamPlayerFragment.newInstance()
        )
        viewPager.adapter = adapter
        mainBinding.tabLayout.setupWithViewPager(viewPager)
        if (fantasyType != 0) {
            Handler().postDelayed(
                {
                    if (fantasyType == 1) {
                        mainBinding.tabLayout.getTabAt(1)?.select()
                        // mBinding.tvPlayerCountPick.setText("Pick "+totalPlayerCount+ " Batsmen");
                    } else if (fantasyType == 2) {
                        mainBinding.tabLayout.getTabAt(3)?.select()
                        /*  mBinding.tvPlayerCountPick.setText("Pick "+totalPlayerCount+ " Bowler");*/
                    } else if (fantasyType == 3) {
                        mainBinding.tabLayout.getTabAt(0)?.select()
                        //mBinding.tvPlayerCountPick.setText("Pick "+totalPlayerCount+ " All Rounder");
                    }

                }, 100
            )
        }
    }
}