package com.fp.devfantasypowerxi.app.view.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.andrognito.flashbar.Flashbar
import com.andrognito.flashbar.anim.FlashAnim
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.request.MyTeamRequest
import com.fp.devfantasypowerxi.app.api.response.League
import com.fp.devfantasypowerxi.app.api.response.Limit
import com.fp.devfantasypowerxi.app.api.response.NormalResponse
import com.fp.devfantasypowerxi.app.api.response.PlayerListResult
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.utils.FootballSelectedPlayer
import com.fp.devfantasypowerxi.app.view.adapter.SelectedUnSelectedPlayerAdapter
import com.fp.devfantasypowerxi.app.view.adapter.TeamFilterAdapter
import com.fp.devfantasypowerxi.app.view.fragment.CreateTeamPlayerFragment
import com.fp.devfantasypowerxi.app.view.listners.TeamFilterClickListener
import com.fp.devfantasypowerxi.app.view.viewmodel.GetPlayerDataViewModel
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.api.Resource
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivityCreateTeamBinding
import com.github.amlcurran.showcaseview.OnShowcaseEventListener
import com.github.amlcurran.showcaseview.ShowcaseView
import com.github.amlcurran.showcaseview.targets.ViewTarget
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Response
import javax.inject.Inject

class FootballCreateTeamActivity : AppCompatActivity(), OnShowcaseEventListener,
    TeamFilterClickListener {
    lateinit var mainBinding: ActivityCreateTeamBinding
    private lateinit var mSelectedUnSelectedPlayerAdapter: SelectedUnSelectedPlayerAdapter
    lateinit var createTeamViewModel: GetPlayerDataViewModel
    var matchKey: String = ""
    var teamVsName: String? = ""
    var teamFirstUrl: String = ""
    var teamSecondUrl: String = ""
    var isForFirstTeamCreate = false
    private var contestFirstTime: League = League()
    var exeedCredit = false

    var gkList = ArrayList<PlayerListResult>()
    var defList = ArrayList<PlayerListResult>()
    var midList = ArrayList<PlayerListResult>()
    var stList = ArrayList<PlayerListResult>()

    var gkListALL = ArrayList<PlayerListResult>()
    var defListALL = ArrayList<PlayerListResult>()
    var midListALL = ArrayList<PlayerListResult>()
    var stListALL = ArrayList<PlayerListResult>()
    private var allPlayerList = ArrayList<PlayerListResult>()
    var counterValue = 0
    var teamCode = "All"
    var teamNamesList: ArrayList<String> = ArrayList()
    lateinit var bottomSheetDialog: BottomSheetDialog
    lateinit var teamfilterAdapter: TeamFilterAdapter
    var selectTeamPosition = 2

    @Inject
    lateinit var oAuthRestService: OAuthRestService
    lateinit var selectedPlayer: FootballSelectedPlayer
    var selectedList: ArrayList<PlayerListResult> = ArrayList()
    var teamId = 0
    lateinit var context: Context
    private var isFromEditOrClone = false
    var headerText: String = ""
    var isShowTimer = false
    var selectedType = GK
    var fantasyType = 0
    private var totalPlayerCount = 0
    var maxTeamPlayerCount = 0
    var totalCredit = 0.0
    private lateinit var limit: Limit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createTeamViewModel = GetPlayerDataViewModel().create(this@FootballCreateTeamActivity)
        MyApplication.getAppComponent()!!.inject(createTeamViewModel)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_team)
        context = this@FootballCreateTeamActivity
        MyApplication.getAppComponent()!!.inject(this@FootballCreateTeamActivity)
        initialize()
    }

    @SuppressLint("SetTextI18n")
    fun initialize() {
        if (intent != null && intent.extras != null) {
            if (intent.extras!!.getBoolean("isFromEditOrClone")) {
                isFromEditOrClone = intent.extras!!.getBoolean("isFromEditOrClone")
                selectedList = intent.getParcelableArrayListExtra("selectedList")!!
                teamId = intent.extras!!.getInt(Constants.KEY_TEAM_ID)
            }
            matchKey = intent.extras!!.getString(Constants.KEY_MATCH_KEY)!!
            teamVsName = intent.extras!!.getString(Constants.KEY_TEAM_VS)
            teamFirstUrl = intent.extras!!.getString(Constants.KEY_TEAM_FIRST_URL)!!
            teamSecondUrl = intent.extras!!.getString(Constants.KEY_TEAM_SECOND_URL)!!
            fantasyType = intent.extras!!.getInt(Constants.KEY_FANTASY_TYPE)
            headerText = intent.extras!!.getString(Constants.KEY_STATUS_HEADER_TEXT, "")
            isShowTimer = intent.extras!!
                .getBoolean(Constants.KEY_STATUS_IS_TIMER_HEADER, false)
            isForFirstTeamCreate = intent.extras!!
                .getBoolean(Constants.FIRST_CREATE_TEAM, false)
            contestFirstTime =
                intent.extras!!.getParcelable(Constants.KEY_CONTEST_FIRST_TIME_DATA) ?: League()

        }
        setTeamNames()
        mainBinding.tvBack.setOnClickListener { finish() }
        mainBinding.headerClearTeam.setOnClickListener {
            if (selectedPlayer.selectedPlayer > 0) {
                showPopUpClearTeam()
            } else {
                AppUtils.showError(
                    this@FootballCreateTeamActivity,
                    "No Player to selected to clear"
                )
            }


        }
        mainBinding.ivInfo.setOnClickListener {
            showPopUpInfo()
        }
        mainBinding.tvFilterByText.setOnClickListener {
            showBottomSheetDialog()
        }

        mainBinding.ivTeamFirst.setImageURI(teamFirstUrl)
        mainBinding.ivTeamSecond.setImageURI(teamSecondUrl)

        mainBinding.tvPlayerCountPick.text = "Pick 1 Goal-Keeper"
        mainBinding.viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
            }

            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                val fm = supportFragmentManager
                when (position) {
                    0 -> {
                        selectedType = GK
                        mainBinding.tabLayout.getTabAt(0)?.text =
                            Constants.GK + if (selectedPlayer.wk_selected == 0) "" else "(" + selectedPlayer.wk_selected
                                .toString() + ")"
                        mainBinding.tvPlayerCountPick.text = "Pick 1 Goal-Keeper"

                    }
                    1 -> {
                        selectedType = DEF
                        mainBinding.tabLayout.getTabAt(1)?.text =
                            Constants.DEF + if (selectedPlayer.bat_selected == 0) "" else "(" + selectedPlayer.bat_selected
                                .toString() + ")"
                        mainBinding.tvPlayerCountPick.text = "Pick 3-5 Defender"

                    }
                    2 -> {
                        selectedType = MID
                        mainBinding.tabLayout.getTabAt(2)?.text =
                            Constants.MID + if (selectedPlayer.ar_selected == 0) "" else "(" + selectedPlayer.ar_selected
                                .toString() + ")"
                        mainBinding.tvPlayerCountPick.text = "Pick 3-5 Midfielder"

                    }
                    3 -> {
                        selectedType = ST
                        mainBinding.tabLayout.getTabAt(3)?.text =
                            Constants.ST + if (selectedPlayer.bowl_selected == 0) "" else "(" + selectedPlayer.bowl_selected
                                .toString() + ")"
                        if (fm.fragments[0] is CreateTeamPlayerFragment) mainBinding.tvPlayerCountPick.text =
                            "Pick 1-3 Forward"

                    }
                }
                callFragmentRefresh()
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        mSelectedUnSelectedPlayerAdapter =
            SelectedUnSelectedPlayerAdapter(applicationContext, 0, totalPlayerCount)
        createTeamData()
        mainBinding.rvSelected.adapter = mSelectedUnSelectedPlayerAdapter
        setupRecyclerView()
        mainBinding.btnCreateTeam.setOnClickListener { v ->
            if (selectedPlayer.selectedPlayer == totalPlayerCount) {
                val selectedList = ArrayList<PlayerListResult>()



                gkList.sortWith { contest: PlayerListResult, t1: PlayerListResult ->
                    contest.id.compareTo(t1.id)
                }
                midList.sortWith { contest: PlayerListResult, t1: PlayerListResult ->
                    contest.id.compareTo(t1.id)
                }
                stList.sortWith { contest: PlayerListResult, t1: PlayerListResult ->
                    contest.id.compareTo(t1.id)
                }
                defList.sortWith { contest: PlayerListResult, t1: PlayerListResult ->
                    contest.id.compareTo(t1.id)
                }
                for (player in gkList) {
                    if (player.isSelected) selectedList.add(player)
                }
                for (player in midList) {
                    if (player.isSelected) selectedList.add(player)
                }
                for (player in stList) {
                    if (player.isSelected) selectedList.add(player)
                }
                for (player in defList) {
                    if (player.isSelected) selectedList.add(player)
                }
                val intent =
                    Intent(this@FootballCreateTeamActivity, ChooseCandVCActivity::class.java)
                intent.putExtra(Constants.KEY_MATCH_KEY, matchKey)
                intent.putExtra(Constants.KEY_TEAM_VS, teamVsName)
                intent.putExtra(Constants.KEY_TEAM_FIRST_URL, teamFirstUrl)
                intent.putExtra("playerList", selectedList)
                intent.putExtra(Constants.KEY_TEAM_ID, teamId)
                intent.putExtra(Constants.KEY_STATUS_HEADER_TEXT, headerText)
                intent.putExtra(Constants.KEY_STATUS_IS_TIMER_HEADER, isShowTimer)
                intent.putExtra(Constants.SPORT_KEY, Constants.TAG_FOOTBALL)
                intent.putExtra(Constants.FIRST_CREATE_TEAM, isForFirstTeamCreate)
                intent.putExtra(Constants.KEY_CONTEST_FIRST_TIME_DATA, contestFirstTime)
                if (isFromEditOrClone) intent.putExtra("isFromEditOrClone",
                    true) else intent.putExtra("isFromEditOrClone", false)
                intent.putExtra(Constants.KEY_TEAM_SECOND_URL, teamSecondUrl)
                startActivityForResult(intent, 101)

                //startActivity(intent);
            } else {
                showToast("Please select $totalPlayerCount players")
            }
        }
        mainBinding.ivTeamPreview.setOnClickListener { view ->
            val intent = Intent(this@FootballCreateTeamActivity,
                FootballTeamPreviewActivity::class.java)
            intent.putExtra(Constants.KEY_MATCH_KEY, matchKey)
            intent.putExtra(Constants.KEY_TEAM_VS, teamVsName)
            intent.putExtra(Constants.KEY_TEAM_FIRST_URL, teamFirstUrl)
            intent.putExtra(Constants.KEY_TEAM_SECOND_URL, teamSecondUrl)
            val selectedWkList = ArrayList<PlayerListResult>()
            val selectedBatLiSt = ArrayList<PlayerListResult>()
            val selectedArList = ArrayList<PlayerListResult>()
            val selectedBowlList = ArrayList<PlayerListResult>()
            for (player in gkList) {
                if (player.isSelected) selectedWkList.add(player)
            }
            for (player in defList) {
                if (player.isSelected) selectedBatLiSt.add(player)
            }
            for (player in midList) {
                if (player.isSelected) selectedArList.add(player)
            }
            for (player in stList) {
                if (player.isSelected) selectedBowlList.add(player)
            }
            intent.putExtra(Constants.KEY_TEAM_LIST_WK, selectedWkList)
            intent.putExtra(Constants.KEY_TEAM_LIST_BAT, selectedBatLiSt)
            intent.putExtra(Constants.KEY_TEAM_LIST_AR, selectedArList)
            intent.putExtra(Constants.KEY_TEAM_LIST_BOWL, selectedBowlList)
            startActivity(intent)
        }
    }

    private fun setTeamNames() {
        if (teamVsName != null) {
            val teams = teamVsName!!.split("Vs").toTypedArray()
            mainBinding.tvTeamNameFirst.text = teams[0]
            mainBinding.tvTeamNameSecond.text = teams[1]
            teamNamesList.add(teams[0])
            teamNamesList.add(teams[1])
            teamNamesList.add("All")
        }
    }

    fun showToast(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupRecyclerView() {
        getData()
    }

    private fun getData() {
        val request = MyTeamRequest()
        request.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        request.matchkey = matchKey
        request.fantasy_type = fantasyType
        request.sport_key = Constants.TAG_FOOTBALL
        createTeamViewModel.loadPlayerListRequest(request)
        createTeamViewModel.getPlayerList().observe(this, { arrayListResource ->
            Log.d("Status ", "" + arrayListResource.status)
            when (arrayListResource.status) {
                Resource.Status.LOADING -> {
                    mainBinding.refreshing = true
                }
                Resource.Status.ERROR -> {
                    mainBinding.refreshing = false
                    if (arrayListResource.exception!!.response!!
                            .code() in 400..403
                    ) {
                        logout()
                    } else {
                        Toast.makeText(MyApplication.appContext,
                            arrayListResource.exception.getErrorModel().errorMessage,
                            Toast.LENGTH_SHORT).show()
                    }
                }
                Resource.Status.SUCCESS -> {
                    mainBinding.refreshing = false
                    if (arrayListResource.data!!.status == 1 && arrayListResource.data
                            .result.size > 0
                    ) {
                        allPlayerList = arrayListResource.data.result
                        limit = arrayListResource.data.limit
                        totalCredit = limit.total_credits
                        totalPlayerCount = limit.maxplayers
                        maxTeamPlayerCount = limit.team_max_player
                        setData()
                        for (player in allPlayerList) {
                            when (player.role) {
                                Constants.KEY_PLAYER_ROLE_GK -> gkList.add(player)
                                Constants.KEY_PLAYER_ROLE_MID -> midList.add(player)
                                Constants.KEY_PLAYER_ROLE_DEF -> defList.add(player)
                                Constants.KEY_PLAYER_ROLE_ST -> stList.add(player)
                            }
                        }
                        gkListALL.addAll(gkList)
                        midListALL.addAll(midList)
                        defListALL.addAll(defList)
                        stListALL.addAll(stList)
                        if (selectedList.size > 0) {
                            var i = 0
                            while (i < allPlayerList.size) {
                                for (player in selectedList) {
                                    if (player.id == allPlayerList[i].id) {
                                        allPlayerList[i].isSelected = true
                                        if (player.captain == 1) allPlayerList[i].isCaptain = true
                                        if (player.vicecaptain == 1) allPlayerList[i].isVcCaptain =
                                            true
                                    }
                                }
                                i++
                            }
                            setSelectedCountForEditOrClone()
                        }
                        setupViewPager(mainBinding.viewPager)
                        if (!MyApplication.preferenceDBTwo!!.getBoolean(Constants.SKIP_CREATETEAM_INSTRUCTION,
                                false)
                        ) {
                            callIntroductionScreen(
                                R.id.tabLayout,
                                "Player Category",
                                "Select a balanced team to help you win",
                                ShowcaseView.BELOW_SHOWCASE
                            )
                            MyApplication.preferenceDBTwo!!.putBoolean(Constants.SKIP_CREATETEAM_INSTRUCTION,
                                true)
                        }
                    } else {
                        Toast.makeText(MyApplication.appContext,
                            arrayListResource.data.message,
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setData() {
        mainBinding.tvTotalCredit.text = " /$totalCredit"
        mainBinding.tvTotalPlayer.text = " /$totalPlayerCount"

        mSelectedUnSelectedPlayerAdapter.updateTotalPlayerCount(totalPlayerCount)
    }

    internal inner class ViewPagerAdapter(manager: FragmentManager?) :
        FragmentPagerAdapter(manager!!) {
        private val mFragmentList: MutableList<Fragment> = java.util.ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFrag(fragment: Fragment) {
            mFragmentList.add(fragment)
        }

        override fun getPageTitle(position: Int): CharSequence {
            when (position) {
                0 -> return Constants.GK + " " + if (selectedPlayer.wk_selected == 0) "" else "(" + selectedPlayer.wk_selected
                    .toString() + ")"
                1 -> return Constants.DEF + " " + if (selectedPlayer.bat_selected == 0) "" else "(" + selectedPlayer.bat_selected
                    .toString() + ")"
                2 -> return Constants.MID + " " + if (selectedPlayer.ar_selected == 0) "" else "(" + selectedPlayer.ar_selected
                    .toString() + ")"
                3 -> return Constants.ST + " " + if (selectedPlayer.bowl_selected == 0) "" else "(" + selectedPlayer.bowl_selected
                    .toString() + ")"
            }
            return ""
        }

        override fun getItemPosition(`object`: Any): Int {
            // POSITION_NONE makes it possible to reload the PagerAdapter
            return POSITION_NONE
        }
    }

    private fun callIntroductionScreen(
        target: Int,
        title: String?,
        description: String?,
        abovE_SHOWCASE: Int,
    ) {
        val showcaseView = ShowcaseView.Builder(this).withNewStyleShowcase()
            .setTarget(ViewTarget(target, this))
            .setContentTitle(title)
            .setContentText(description)
            .setStyle(if (counterValue == 0) R.style.CustomShowcaseTheme else R.style.CustomShowcaseTheme)
             .hideOnTouchOutside().setShowcaseEventListener(this)
            .build()
        showcaseView.forceTextPosition(abovE_SHOWCASE)
        counterValue += 1
        showcaseView.hideButton()

        // new Handler().postDelayed(() -> showcaseView.hideButton(), 2500);
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter: ViewPagerAdapter =
            ViewPagerAdapter(
                supportFragmentManager)
        adapter.addFrag(CreateTeamPlayerFragment.newInstance(allPlayerList, gkList, GK, 0))
        adapter.addFrag(CreateTeamPlayerFragment.newInstance(allPlayerList, defList, DEF, 0))
        adapter.addFrag(CreateTeamPlayerFragment.newInstance(allPlayerList, midList, MID, 0))
        adapter.addFrag(CreateTeamPlayerFragment.newInstance(allPlayerList, stList, ST, 0))
        viewPager.adapter = adapter
        mainBinding.tabLayout.setupWithViewPager(viewPager)
        if (fantasyType > 0) {
            Handler().postDelayed(
                {
                    mainBinding.tabLayout.getTabAt(1)!!.select()
                    mainBinding.tabLayout.clearOnTabSelectedListeners()
                    val tabStrip = mainBinding.tabLayout.getChildAt(0) as LinearLayout
                    for (i in 0 until tabStrip.childCount) {
                        tabStrip.getChildAt(i).setOnTouchListener { v, event -> true }
                    }
                    mainBinding.viewPager.setPagingEnabled(false)
                }, 100)
        }
    }

    private fun setSelectedCountForEditOrClone() {
        var countWK = 0
        var countBAT = 0
        var countBALL = 0
        var countALL = 0
        var totalCount = 0
        var team1Count = 0
        var team2Count = 0
        var usedCredit = 0.0
        for (player in allPlayerList) {
            if (player.isSelected) {
                if (player.role == Constants.KEY_PLAYER_ROLE_GK) {
                    countWK++
                }
                if (player.role == Constants.KEY_PLAYER_ROLE_DEF) {
                    countBAT++
                }
                if (player.role == Constants.KEY_PLAYER_ROLE_MID) {
                    countALL++
                }
                if (player.role == Constants.KEY_PLAYER_ROLE_ST) {
                    countBALL++
                }
                if (player.team == "team1") {
                    team1Count++
                }
                if (player.team == "team2") {
                    team2Count++
                }
                totalCount++
                usedCredit += player.credit
            }
        }
        selectedPlayer.wk_selected = countWK
        selectedPlayer.bat_selected = countBAT
        selectedPlayer.ar_selected = countALL
        selectedPlayer.bowl_selected = countBALL
        selectedPlayer.selectedPlayer = totalPlayerCount
        selectedPlayer.localTeamplayerCount = team1Count
        selectedPlayer.visitorTeamPlayerCount = team2Count
        selectedPlayer.total_credit = usedCredit
        updateTeamData(
            0,
            selectedPlayer.wk_selected,
            selectedPlayer.bat_selected,
            selectedPlayer.ar_selected,
            selectedPlayer.bowl_selected,
            selectedPlayer.selectedPlayer,
            selectedPlayer.localTeamplayerCount,
            selectedPlayer.visitorTeamPlayerCount,
            selectedPlayer.total_credit)
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
                        this@FootballCreateTeamActivity,
                        updateProfileResponse.message
                    )
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
            }
        })
    }

    fun createTeamData() {
        selectedPlayer = FootballSelectedPlayer()
        selectedPlayer.extra_player = 3
        selectedPlayer.wk_min_count = 1
        selectedPlayer.wk_max_count = 1
        selectedPlayer.wk_selected = 0
        selectedPlayer.bat_mincount = 3
        selectedPlayer.bat_maxcount = 5
        selectedPlayer.bat_selected = 0
        selectedPlayer.bowl_mincount = 1
        selectedPlayer.bowl_maxcount = 3
        selectedPlayer.bowl_selected = 0
        selectedPlayer.ar_mincount = 3
        selectedPlayer.ar_maxcount = 5
        selectedPlayer.ar_selected = 0
        selectedPlayer.selectedPlayer = 0
        selectedPlayer.localTeamplayerCount = 0
        selectedPlayer.visitorTeamPlayerCount = 0
        selectedPlayer.total_credit = 0.0
        updateUi()
    }

    fun updateTeamData(
        extra_player: Int,
        wk_selected: Int,
        bat_selected: Int,
        ar_selected: Int,
        bowl_selected: Int,
        selectPlayer: Int,
        localTeamplayerCount: Int,
        visitorTeamPlayerCount: Int,
        total_credit: Double,
    ) {
        exeedCredit = false
        selectedPlayer.extra_player = extra_player
        selectedPlayer.wk_selected = wk_selected
        selectedPlayer.bat_selected = bat_selected
        selectedPlayer.ar_selected = ar_selected
        selectedPlayer.bowl_selected = bowl_selected
        selectedPlayer.selectedPlayer = selectPlayer
        selectedPlayer.localTeamplayerCount = localTeamplayerCount
        selectedPlayer.visitorTeamPlayerCount = visitorTeamPlayerCount
        selectedPlayer.total_credit = total_credit
        updateUi()
    }

    companion object {
        private const val GK = 1
        private const val DEF = 2
        private const val MID = 3
        private const val ST = 4

    }

    fun showTeamValidation(mesg: String) {
        val flashbar: Flashbar = Flashbar.Builder(this)
            .gravity(Flashbar.Gravity.TOP)
            .message(mesg)
            .backgroundDrawable(R.drawable.bg_gradient_create_team_warning)
            .showIcon()
            .icon(R.drawable.close)
            .iconAnimation(FlashAnim.with(this)
                .animateIcon()
                .pulse()
                .alpha()
                .duration(1000)
                .accelerate())
            .build()
        flashbar.show()
        Handler().postDelayed({ flashbar.dismiss() }, 2000)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                val returnIntent = Intent()
                returnIntent.putExtra("isTeamCreated",
                    data!!.getBooleanExtra("isTeamCreated", false))
                setResult(RESULT_OK, returnIntent)
                finish()
            }
        }
    }

    fun onPlayerClick(isSelect: Boolean, position: Int, type: Int) {
        if (type == GK) {
            var player_credit = 0.0
            if (isSelect) {
                if (selectedPlayer.selectedPlayer >= totalPlayerCount) {
                    showTeamValidation("You can choose maximum $totalPlayerCount players.")
                    return
                }
                if (selectedPlayer.wk_selected >= 1) {
                    showTeamValidation("You can select only 1 Goal-Keeper.")
                    return
                }
                if (gkList[position].team == "team1") {
                    if (selectedPlayer.localTeamplayerCount >= maxTeamPlayerCount) {
                        showTeamValidation("You can select only $maxTeamPlayerCount from each team.")
                        return
                    }
                } else {
                    if (selectedPlayer.visitorTeamPlayerCount >= maxTeamPlayerCount) {
                        showTeamValidation("You can select only $maxTeamPlayerCount from each team.")
                        return
                    }
                }
                if (selectedPlayer.wk_selected < selectedPlayer.wk_max_count) {
                    if (selectedPlayer.selectedPlayer < totalPlayerCount) {
                        if (selectedPlayer.wk_selected < selectedPlayer.wk_min_count || selectedPlayer.extra_player > 0) {
                            var extra: Int = selectedPlayer.extra_player
                            if (selectedPlayer.wk_selected >= selectedPlayer.wk_min_count) {
                                extra = selectedPlayer.extra_player - 1
                            }
                            player_credit = gkList[position].credit
                            val total_credit: Double =
                                selectedPlayer.total_credit + player_credit
                            if (total_credit > totalCredit) {
                                exeedCredit = true
                                showTeamValidation("Not enough credits to select this player.")
                                return
                            }
                            var localTeamplayerCount: Int = selectedPlayer.localTeamplayerCount
                            var visitorTeamPlayerCount: Int =
                                selectedPlayer.visitorTeamPlayerCount
                            if (gkList[position].team == "team1") localTeamplayerCount =
                                selectedPlayer.localTeamplayerCount + 1 else visitorTeamPlayerCount =
                                selectedPlayer.visitorTeamPlayerCount + 1
                            gkList[position].isSelected = isSelect
                            updateTeamData(
                                extra,
                                selectedPlayer.wk_selected + 1,
                                selectedPlayer.bat_selected,
                                selectedPlayer.ar_selected,
                                selectedPlayer.bowl_selected,
                                selectedPlayer.selectedPlayer + 1,
                                localTeamplayerCount,
                                visitorTeamPlayerCount,
                                total_credit
                            )
                        } else {
                            minimumPlayerWarning()
                        }
                    }
                }
            } else {
                if (selectedPlayer.wk_selected > 0) {
                    //showTeamValidation("Pick 1 Goal-Keeper");
                    player_credit = gkList[position].credit
                    val total_credit: Double = selectedPlayer.total_credit - player_credit
                    var extra: Int = selectedPlayer.extra_player
                    if (selectedPlayer.wk_selected > selectedPlayer.wk_min_count) {
                        extra = selectedPlayer.extra_player + 1
                    }
                    var localTeamplayerCount: Int = selectedPlayer.localTeamplayerCount
                    var visitorTeamPlayerCount: Int = selectedPlayer.visitorTeamPlayerCount
                    if (gkList[position].team.equals("team1")) localTeamplayerCount =
                        selectedPlayer.localTeamplayerCount - 1 else visitorTeamPlayerCount =
                        selectedPlayer.visitorTeamPlayerCount - 1
                    gkList[position].isSelected = isSelect
                    updateTeamData(
                        extra,
                        selectedPlayer.wk_selected - 1,
                        selectedPlayer.bat_selected,
                        selectedPlayer.ar_selected,
                        selectedPlayer.bowl_selected,
                        selectedPlayer.selectedPlayer - 1,
                        localTeamplayerCount,
                        visitorTeamPlayerCount,
                        total_credit
                    )
                }
            }
        } else if (type == DEF) {
            var player_credit = 0.0
            if (isSelect) {
                if (selectedPlayer.selectedPlayer >= totalPlayerCount) {
                    showTeamValidation("You can choose maximum $totalPlayerCount players")
                    return
                }
                if (selectedPlayer.bat_selected >= 5) {
                    showTeamValidation("You can select only 5 Defender")
                    return
                }
                if (defList[position].team.equals("team1")) {
                    if (selectedPlayer.localTeamplayerCount >= maxTeamPlayerCount) {
                        showTeamValidation("You can select only $maxTeamPlayerCount from each team.")
                        return
                    }
                } else {
                    if (selectedPlayer.visitorTeamPlayerCount >= maxTeamPlayerCount) {
                        showTeamValidation("You can select only $maxTeamPlayerCount from each team.")
                        return
                    }
                }
                if (selectedPlayer.bat_selected < selectedPlayer.bat_maxcount) {
                    if (selectedPlayer.selectedPlayer < totalPlayerCount) {
                        if (selectedPlayer.bat_selected < selectedPlayer.bat_mincount || selectedPlayer.extra_player > 0) {
                            var extra: Int = selectedPlayer.extra_player
                            if (selectedPlayer.bat_selected >= selectedPlayer.bat_mincount) {
                                extra = selectedPlayer.extra_player - 1
                            }
                            player_credit = defList[position].credit
                            val total_credit: Double =
                                selectedPlayer.total_credit + player_credit
                            if (total_credit > totalCredit) {
                                exeedCredit = true
                                showTeamValidation("Not enough credits to select this player.")
                                return
                            }
                            var localTeamplayerCount: Int = selectedPlayer.localTeamplayerCount
                            var visitorTeamPlayerCount: Int =
                                selectedPlayer.visitorTeamPlayerCount
                            if (defList[position].team.equals("team1")) localTeamplayerCount =
                                selectedPlayer.localTeamplayerCount + 1 else visitorTeamPlayerCount =
                                selectedPlayer.visitorTeamPlayerCount + 1
                            defList[position].isSelected = isSelect
                            updateTeamData(
                                extra,
                                selectedPlayer.wk_selected,
                                selectedPlayer.bat_selected + 1,
                                selectedPlayer.ar_selected,
                                selectedPlayer.bowl_selected,
                                selectedPlayer.selectedPlayer + 1,
                                localTeamplayerCount,
                                visitorTeamPlayerCount,
                                total_credit
                            )
                        } else {
                            minimumPlayerWarning()
                        }
                    }
                }
            } else {
                if (selectedPlayer.bat_selected > 0) {
                    //  showTeamValidation("Pick 3-5 Defender");
                    player_credit = defList[position].credit
                    val total_credit: Double = selectedPlayer.total_credit - player_credit
                    var extra: Int = selectedPlayer.extra_player
                    if (selectedPlayer.bat_selected > selectedPlayer.bat_mincount) {
                        extra = selectedPlayer.extra_player + 1
                    }
                    var localTeamplayerCount: Int = selectedPlayer.localTeamplayerCount
                    var visitorTeamPlayerCount: Int = selectedPlayer.visitorTeamPlayerCount
                    if (defList[position].team.equals("team1")) localTeamplayerCount =
                        selectedPlayer.localTeamplayerCount - 1 else visitorTeamPlayerCount =
                        selectedPlayer.visitorTeamPlayerCount - 1
                    defList[position].isSelected = isSelect
                    updateTeamData(
                        extra,
                        selectedPlayer.wk_selected,
                        selectedPlayer.bat_selected - 1,
                        selectedPlayer.ar_selected,
                        selectedPlayer.bowl_selected,
                        selectedPlayer.selectedPlayer - 1,
                        localTeamplayerCount,
                        visitorTeamPlayerCount,
                        total_credit
                    )
                }
            }
        } else if (type == MID) {
            var player_credit = 0.0
            if (isSelect) {
                if (selectedPlayer.selectedPlayer >= totalPlayerCount) {
                    showTeamValidation("You can choose maximum $totalPlayerCount players.")
                    return
                }
                if (selectedPlayer.ar_selected >= 5) {
                    showTeamValidation("You can select only 5 Midfielder")
                    return
                }
                if (midList[position].team.equals("team1")) {
                    if (selectedPlayer.localTeamplayerCount >= maxTeamPlayerCount) {
                        showTeamValidation("You can select only $maxTeamPlayerCount from each team.")
                        //mBinding.tvPlayerCountPick.setText("You can select only 7 from each team.");
                        return
                    }
                } else {
                    if (selectedPlayer.visitorTeamPlayerCount >= maxTeamPlayerCount) {
                        showTeamValidation("You can select only $maxTeamPlayerCount from each team.")
                        //mBinding.tvPlayerCountPick.setText("You can select only 7 from each team.");
                        return
                    }
                }
                if (selectedPlayer.ar_selected < selectedPlayer.ar_maxcount) {
                    if (selectedPlayer.selectedPlayer < totalPlayerCount) {
                        if (selectedPlayer.ar_selected < selectedPlayer.ar_mincount || selectedPlayer.extra_player > 0) {
                            var extra: Int = selectedPlayer.extra_player
                            if (selectedPlayer.ar_selected >= selectedPlayer.ar_mincount) {
                                extra = selectedPlayer.extra_player - 1
                            }
                            player_credit = midList[position].credit
                            val total_credit: Double =
                                selectedPlayer.total_credit + player_credit
                            if (total_credit > totalCredit) {
                                exeedCredit = true
                                showTeamValidation("Not enough credits to select this player.")
                                //mBinding.tvPlayerCountPick.setText("Not enough credits to select this player.");
                                return
                            }
                            var localTeamplayerCount: Int = selectedPlayer.localTeamplayerCount
                            var visitorTeamPlayerCount: Int =
                                selectedPlayer.visitorTeamPlayerCount
                            if (midList[position].team == "team1") localTeamplayerCount =
                                selectedPlayer.localTeamplayerCount + 1 else visitorTeamPlayerCount =
                                selectedPlayer.visitorTeamPlayerCount + 1
                            midList[position].isSelected = isSelect
                            updateTeamData(
                                extra,
                                selectedPlayer.wk_selected,
                                selectedPlayer.bat_selected,
                                selectedPlayer.ar_selected + 1,
                                selectedPlayer.bowl_selected,
                                selectedPlayer.selectedPlayer + 1,
                                localTeamplayerCount,
                                visitorTeamPlayerCount,
                                total_credit
                            )
                        } else {
                            minimumPlayerWarning()
                        }
                    }
                }
            } else {
                if (selectedPlayer.ar_selected > 0) {
                    //  showTeamValidation("Pick 3-5 Midfielder");
                    player_credit = midList[position].credit
                    val total_credit: Double = selectedPlayer.total_credit - player_credit
                    var extra: Int = selectedPlayer.extra_player
                    if (selectedPlayer.ar_selected > selectedPlayer.ar_mincount) {
                        extra = selectedPlayer.extra_player + 1
                    }
                    var localTeamplayerCount: Int = selectedPlayer.localTeamplayerCount
                    var visitorTeamPlayerCount: Int = selectedPlayer.visitorTeamPlayerCount
                    if (midList[position].team.equals("team1")) localTeamplayerCount =
                        selectedPlayer.localTeamplayerCount - 1 else visitorTeamPlayerCount =
                        selectedPlayer.visitorTeamPlayerCount - 1
                    midList[position].isSelected = isSelect
                    updateTeamData(
                        extra,
                        selectedPlayer.wk_selected,
                        selectedPlayer.bat_selected,
                        selectedPlayer.ar_selected - 1,
                        selectedPlayer.bowl_selected,
                        selectedPlayer.selectedPlayer - 1,
                        localTeamplayerCount,
                        visitorTeamPlayerCount,
                        total_credit
                    )
                }
            }
        } else if (type == ST) {
            var player_credit = 0.0
            if (isSelect) {
                if (selectedPlayer.selectedPlayer >= totalPlayerCount) {
                    showTeamValidation("You can choose maximum $totalPlayerCount players.")
                    return
                }
                if (selectedPlayer.bowl_selected >= 3) {
                    showTeamValidation("You can select only 3 Forward.")
                    return
                }
                if (stList[position].team.equals("team1")) {
                    if (selectedPlayer.localTeamplayerCount >= maxTeamPlayerCount) {
                        showTeamValidation("You can select only $maxTeamPlayerCount from each team.")
                        return
                    }
                } else {
                    if (selectedPlayer.visitorTeamPlayerCount >= maxTeamPlayerCount) {
                        showTeamValidation("You can select only $maxTeamPlayerCount from each team.")
                        return
                    }
                }
                if (selectedPlayer.bowl_selected < selectedPlayer.bowl_maxcount) {
                    if (selectedPlayer.selectedPlayer < totalPlayerCount) {
                        if (selectedPlayer.bowl_selected < selectedPlayer.bat_mincount || selectedPlayer.extra_player > 0) {
                            var extra: Int = selectedPlayer.extra_player
                            if (selectedPlayer.bowl_selected >= selectedPlayer.bowl_mincount) {
                                extra = selectedPlayer.extra_player - 1
                            }
                            player_credit = stList[position].credit
                            val total_credit: Double =
                                selectedPlayer.total_credit + player_credit
                            if (total_credit > totalCredit) {
                                exeedCredit = true
                                showTeamValidation("Not enough credits to select this player.")
                                //mBinding.tvPlayerCountPick.setText("Not enough credits to select this player.");
                                return
                            }
                            var localTeamplayerCount: Int = selectedPlayer.localTeamplayerCount
                            var visitorTeamPlayerCount: Int =
                                selectedPlayer.visitorTeamPlayerCount
                            if (stList[position].team == "team1") localTeamplayerCount =
                                selectedPlayer.localTeamplayerCount + 1 else visitorTeamPlayerCount =
                                selectedPlayer.visitorTeamPlayerCount + 1
                            stList[position].isSelected = isSelect
                            updateTeamData(
                                extra,
                                selectedPlayer.wk_selected,
                                selectedPlayer.bat_selected,
                                selectedPlayer.ar_selected,
                                selectedPlayer.bowl_selected + 1,
                                selectedPlayer.selectedPlayer + 1,
                                localTeamplayerCount,
                                visitorTeamPlayerCount,
                                total_credit
                            )
                        } else {
                            minimumPlayerWarning()
                        }
                    }
                }
            } else {
                if (selectedPlayer.bowl_selected > 0) {
                    //  showTeamValidation("Pick 1-3 Forward");
                    player_credit = stList[position].credit
                    val total_credit: Double = selectedPlayer.total_credit - player_credit
                    var extra: Int = selectedPlayer.extra_player
                    if (selectedPlayer.bowl_selected > selectedPlayer.bowl_mincount) {
                        extra = selectedPlayer.extra_player + 1
                    }
                    var localTeamplayerCount: Int = selectedPlayer.localTeamplayerCount
                    var visitorTeamPlayerCount: Int = selectedPlayer.visitorTeamPlayerCount
                    if (stList[position].team.equals("team1")) localTeamplayerCount =
                        selectedPlayer.localTeamplayerCount - 1 else visitorTeamPlayerCount =
                        selectedPlayer.visitorTeamPlayerCount - 1
                    stList[position].isSelected = isSelect
                    updateTeamData(
                        extra,
                        selectedPlayer.wk_selected,
                        selectedPlayer.bat_selected,
                        selectedPlayer.ar_selected,
                        selectedPlayer.bowl_selected - 1,
                        selectedPlayer.selectedPlayer - 1,
                        localTeamplayerCount,
                        visitorTeamPlayerCount,
                        total_credit
                    )
                }
            }
        }
    }


    private fun minimumPlayerWarning() {
        when {
            selectedPlayer.bowl_selected < 1 -> {
                showTeamValidation("You must select at least 1 State-Forward.")
            }
            selectedPlayer.bat_selected < 3 -> {
                showTeamValidation("You must select at least 3 Defender.")
            }
            selectedPlayer.ar_selected < 3 -> {
                showTeamValidation("You must select at least 1 Midfielder")
            }
            selectedPlayer.wk_selected < 1 -> {
                showTeamValidation("You must select at least 1 Goal-Keeper.")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUi() {
        mainBinding.tvSelectedPlayer.text = selectedPlayer.selectedPlayer.toString() + ""
        if (selectedPlayer.total_credit < 0) selectedPlayer.total_credit = 0.0
        val creditLeft: String =
            java.lang.String.valueOf(totalCredit - selectedPlayer.total_credit)
        mainBinding.tvUsedCredit.text = creditLeft + ""
        mainBinding.tvTeamCountFirst.text = "(" + selectedPlayer.localTeamplayerCount
            .toString() + ")"
        mainBinding.tvTeamCountSecond.text = "(" + selectedPlayer.visitorTeamPlayerCount
            .toString() + ")"
        mSelectedUnSelectedPlayerAdapter.update(selectedPlayer.selectedPlayer)
        if (mainBinding.tabLayout.tabCount > 0) {
            Log.e("childCount", mainBinding.tabLayout.childCount.toString() + "")
            Log.e("tabCount", mainBinding.tabLayout.tabCount.toString() + "")
            callFragmentRefresh()
        }
    }

    private fun callFragmentRefresh() {
        if (mainBinding.viewPager.adapter != null) mainBinding.viewPager.adapter!!
            .notifyDataSetChanged()
        val fm = supportFragmentManager
        when (selectedType) {
            1 -> {
                gkList = if (teamCode != "All") {
                    gkListALL.filter { it.teamcode == teamCode } as ArrayList<PlayerListResult>
                } else {
                    gkListALL
                }
                mainBinding.tabLayout.getTabAt(0)?.text =
                    Constants.GK + " " + if (selectedPlayer.wk_selected == 0) "" else "(" + selectedPlayer.wk_selected
                        .toString() + ")"
                if (fm.fragments[0] is CreateTeamPlayerFragment) (fm.fragments[0] as CreateTeamPlayerFragment).refresh(
                    gkList,
                    selectedType, teamCode)
            }
            2 -> {
                defList = if (teamCode != "All") {
                    defListALL.filter { it.teamcode == teamCode } as ArrayList<PlayerListResult>
                } else {
                    defListALL
                }
                mainBinding.tabLayout.getTabAt(1)?.text =
                    Constants.DEF + " " + if (selectedPlayer.bat_selected == 0) "" else "(" + selectedPlayer.bat_selected
                        .toString() + ")"
                if (fm.fragments[0] is CreateTeamPlayerFragment) (fm.fragments[0] as CreateTeamPlayerFragment).refresh(
                    defList,
                    selectedType, teamCode)
            }
            3 -> {
                midList = if (teamCode != "All") {
                    midListALL.filter { it.teamcode == teamCode } as ArrayList<PlayerListResult>
                } else {
                    midListALL
                }
                mainBinding.tabLayout.getTabAt(2)?.text =
                    Constants.MID + " " + if (selectedPlayer.ar_selected == 0) "" else "(" + selectedPlayer.ar_selected
                        .toString() + ")"
                if (fm.fragments[0] is CreateTeamPlayerFragment) (fm.fragments[0] as CreateTeamPlayerFragment).refresh(
                    midList,
                    selectedType, teamCode)
            }
            4 -> {
                stList = if (teamCode != "All") {
                    stListALL.filter { it.teamcode == teamCode } as ArrayList<PlayerListResult>
                } else {
                    stListALL
                }
                mainBinding.tabLayout.getTabAt(3)?.text =
                    Constants.ST + " " + if (selectedPlayer.bowl_selected == 0) "" else "(" + selectedPlayer.bowl_selected
                        .toString() + ")"
                if (fm.fragments[0] is CreateTeamPlayerFragment) (fm.fragments[0] as CreateTeamPlayerFragment).refresh(
                    stList,
                    selectedType, teamCode)
            }
        }
    }

    override fun onShowcaseViewHide(showcaseView: ShowcaseView?) {
        when (counterValue) {
            1 -> {
                callIntroductionScreen(
                    R.id.tv_used_credit,
                    "Credit Counter",
                    "Use $totalCredit credits to pick your players", ShowcaseView.BELOW_SHOWCASE
                )
            }
            2 -> {
                callIntroductionScreen(
                    R.id.tv_total_player,
                    "Player Counter",
                    "Pick $totalPlayerCount players to create your team",
                    ShowcaseView.BELOW_SHOWCASE
                )
            }
        }
    }

    override fun onShowcaseViewDidHide(showcaseView: ShowcaseView?) {

    }

    override fun onShowcaseViewShow(showcaseView: ShowcaseView?) {

    }

    override fun onShowcaseViewTouchBlocked(motionEvent: MotionEvent?) {

    }

    private fun showPopUpInfo() {
        val dialogue = Dialog(this)
        dialogue.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogue.setContentView(R.layout.rules_layout_dialog_football)
        dialogue.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogue.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogue.setCancelable(true)
        dialogue.setCanceledOnTouchOutside(true)
        dialogue.setTitle(null)
        val close: AppCompatButton = dialogue.findViewById(R.id.close)
        close.setOnClickListener { dialogue.dismiss() }
        if (dialogue.isShowing) dialogue.dismiss()
        dialogue.show()

    }

    private fun showBottomSheetDialog() {
        bottomSheetDialog = BottomSheetDialog(this@FootballCreateTeamActivity)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        val rvView = bottomSheetDialog.findViewById<RecyclerView>(R.id.rvView)
        val txtCross = bottomSheetDialog.findViewById<TextView>(R.id.tvCross)
        txtCross!!.setOnClickListener { bottomSheetDialog.cancel() }
        teamfilterAdapter =
            TeamFilterAdapter(this@FootballCreateTeamActivity,
                teamNamesList,
                this@FootballCreateTeamActivity,
                selectTeamPosition)
        if (rvView != null) {
            rvView.setHasFixedSize(false)
            rvView.layoutManager = mLayoutManager
            rvView.adapter = teamfilterAdapter
        }
        bottomSheetDialog.show()
    }

    override fun getTeamDataClick(teamCode: String, position: Int) {
        selectTeamPosition = position
        bottomSheetDialog.dismiss()
        this.teamCode = teamCode.replace(" ", "")
        callFragmentRefresh()
    }


    private fun showPopUpClearTeam() {
        val dialogue = Dialog(this)
        dialogue.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogue.setContentView(R.layout.clear_team_layout_dialog)
        dialogue.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogue.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogue.setCancelable(true)
        dialogue.setCanceledOnTouchOutside(true)
        dialogue.setTitle(null)
        val close: TextView = dialogue.findViewById(R.id.cancel)
        val clearTeam: TextView = dialogue.findViewById(R.id.clear_team)
        close.setOnClickListener { dialogue.dismiss() }
        clearTeam.setOnClickListener {
            dialogue.dismiss()
            selectedPlayer = FootballSelectedPlayer()
            updateTeamData(
                selectedPlayer.extra_player,
                selectedPlayer.wk_selected,
                selectedPlayer.bat_selected,
                selectedPlayer.ar_selected,
                selectedPlayer.bowl_selected,
                selectedPlayer.selectedPlayer,
                selectedPlayer.localTeamplayerCount,
                selectedPlayer.visitorTeamPlayerCount,
                selectedPlayer.total_credit
            )
            gkList.forEach { it.isSelected = false }
            defList.forEach { it.isSelected = false }
            midList.forEach { it.isSelected = false }
            stList.forEach { it.isSelected = false }

        }
        if (dialogue.isShowing) dialogue.dismiss()
        dialogue.show()
    }

}