package com.fp.devfantasypowerxi.app.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
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
import com.fp.devfantasypowerxi.app.api.response.*
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.utils.SelectedPlayer
import com.fp.devfantasypowerxi.app.view.adapter.SelectedUnSelectedPlayerAdapter
import com.fp.devfantasypowerxi.app.view.adapter.TeamFilterAdapter
import com.fp.devfantasypowerxi.app.view.fragment.CreateTeamPlayerFragment
import com.fp.devfantasypowerxi.app.view.fragment.CreateTeamPlayerFragment.Companion.newInstance
import com.fp.devfantasypowerxi.app.view.listners.TeamFilterClickListener
import com.fp.devfantasypowerxi.app.view.viewmodel.GetPlayerDataViewModel
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter.CustomCallback
import com.fp.devfantasypowerxi.common.api.Resource
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivityCreateTeamBinding
import com.github.amlcurran.showcaseview.OnShowcaseEventListener
import com.github.amlcurran.showcaseview.ShowcaseView
import com.github.amlcurran.showcaseview.targets.ViewTarget
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.recycler_preview_player_item.*
import retrofit2.Response
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class CreateTeamActivity : AppCompatActivity(), TeamFilterClickListener, OnShowcaseEventListener {
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
    var wkList = ArrayList<PlayerListResult>()
    var bolList = ArrayList<PlayerListResult>()
    var batList = ArrayList<PlayerListResult>()
    var arList = ArrayList<PlayerListResult>()

    var wkListALL = ArrayList<PlayerListResult>()
    var bolListALL = ArrayList<PlayerListResult>()
    var batListALL = ArrayList<PlayerListResult>()
    var arListALL = ArrayList<PlayerListResult>()
    private var allPlayerList = ArrayList<PlayerListResult>()
    var counterValue = 0
    var teamCode = "All"
    var teamNamesList: ArrayList<String> = ArrayList()
    lateinit var bottomSheetDialog: BottomSheetDialog
    lateinit var teamfilterAdapter: TeamFilterAdapter

    @Inject
    lateinit var oAuthRestService: OAuthRestService
    lateinit var selectedPlayer: SelectedPlayer
    var selectedList: ArrayList<PlayerListResult> = ArrayList()
    var teamId = 0
    var selectTeamPosition = 2
    lateinit var context: Context
    private var isFromEditOrClone = false
    var headerText: String = ""
    var isShowTimer = false
    var selectedType = WK
    var fantasyType = 0
    private var totalPlayerCount = 0
    var maxTeamPlayerCount = 0
    var totalCredit = 0.0
    private lateinit var limit: Limit
    var isPointsSortingGlobal = false
    var isCreditsGlobal = false
    var isPointSelected = false
    var isCreditSelected = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createTeamViewModel = GetPlayerDataViewModel().create(this@CreateTeamActivity)
        MyApplication.getAppComponent()!!.inject(createTeamViewModel)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_team)
        context = this@CreateTeamActivity
        MyApplication.getAppComponent()!!.inject(this@CreateTeamActivity)
        initialize()
        createTeamAc = this
    }

    @SuppressLint("SetTextI18n")
    fun initialize() {
        if (intent != null && intent.extras != null) {
            if (intent.extras!!.getBoolean("isFromEditOrClone")) {
                isFromEditOrClone = intent.extras!!.getBoolean("isFromEditOrClone")
                selectedList =
                    intent.getParcelableArrayListExtra("selectedList")!!
                teamId = intent.extras!!.getInt(Constants.KEY_TEAM_ID)
            }
            matchKey = intent.extras!!.getString(Constants.KEY_MATCH_KEY)!!
            teamVsName = intent.extras!!.getString(Constants.KEY_TEAM_VS)!!
            teamFirstUrl = intent.extras!!.getString(Constants.KEY_TEAM_FIRST_URL)!!
            teamSecondUrl = intent.extras!!.getString(Constants.KEY_TEAM_SECOND_URL)!!
            fantasyType = intent.extras!!.getInt(Constants.KEY_FANTASY_TYPE)
            headerText = intent.extras!!
                .getString(Constants.KEY_STATUS_HEADER_TEXT, "")
            isShowTimer = intent.extras!!.getBoolean(Constants.KEY_STATUS_IS_TIMER_HEADER, false)
            isForFirstTeamCreate = intent.extras!!
                .getBoolean(Constants.FIRST_CREATE_TEAM, false)
            contestFirstTime =
                intent.extras!!.getParcelable(Constants.KEY_CONTEST_FIRST_TIME_DATA) ?: League()
        }
        setTeamNames()
        // mainBinding.matchHeaderInfo.tvTeamVs.text = teamVsName
        mainBinding.ivInfo.setOnClickListener {
            showPopUpInfo()
        }
        mainBinding.tvFilterByText.setOnClickListener {
            showBottomSheetDialog()
        }
        mainBinding.tvBack.setOnClickListener { finish() }
        mainBinding.headerClearTeam.setOnClickListener {
            if (selectedPlayer.selectedPlayer > 0) {
                showPopUpClearTeam()
            } else {
                AppUtils.showError(
                    this@CreateTeamActivity,
                    "No Player to selected to clear"
                )
            }
        }
        mainBinding.ivTeamFirst.setImageURI(teamFirstUrl)
        mainBinding.ivTeamSecond.setImageURI(teamSecondUrl)
        /*   if (isShowTimer) {
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
   */
        //   mainBinding.tvPlayerCountPick.setText("Pick 1-4 Wicket-Keepers");
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
        mainBinding.viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        selectedType = WK
                        mainBinding.tabLayout.getTabAt(0)?.text =
                            "WK " + (if (selectedPlayer.wk_selected == 0) "" else "(" + selectedPlayer.wk_selected + ")")
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

                        /*   if (fm.fragments[0] is CreateTeamPlayerFragment) (fm.fragments[0] as CreateTeamPlayerFragment).refresh(
                               wkList,
                               WK, teamCode
                           )*/
                    }
                    1 -> {
                        selectedType = BAT
                        mainBinding.tabLayout.getTabAt(1)?.text =
                            "BAT " + (if (selectedPlayer.bat_selected == 0) "" else "(" + selectedPlayer.bat_selected + ")")
                        when (fantasyType) {
                            1 -> {
                                mainBinding.tvPlayerCountPick.text = "Pick 1-5 Batsmen"
                            }
                            3 -> {
                                mainBinding.tvPlayerCountPick.text = "Pick 2-4 Batsmen"
                            }
                            else -> {
                                mainBinding.tvPlayerCountPick.text = "Pick 3-6 Batsmen"
                            }
                        }
                        /* if (fm.fragments[0] is CreateTeamPlayerFragment) (fm.fragments[0] as CreateTeamPlayerFragment).refresh(
                             batList,
                             BAT, teamCode
                         )*/
                    }
                    2 -> {
                        selectedType = AR
                        mainBinding.tabLayout.getTabAt(2)?.text =
                            "AR " + (if (selectedPlayer.ar_selected == 0) "" else "(" + selectedPlayer.ar_selected + ")")
                        if (fantasyType == 1 || fantasyType == 2) {
                            mainBinding.tvPlayerCountPick.text = "Pick 1-5 All-Rounders"
                        } else if (fantasyType == 3) {
                            mainBinding.tvPlayerCountPick.text = "Pick 1-2 All-Rounders"
                        } else {
                            mainBinding.tvPlayerCountPick.text = "Pick 1-4 All-Rounders"
                        }
                        /* if (fm.fragments[0] is CreateTeamPlayerFragment) (fm.fragments[0] as CreateTeamPlayerFragment).refresh(
                             arList,
                             AR, teamCode
                         )*/
                    }
                    3 -> {
                        selectedType = BOWLER
                        mainBinding.tabLayout.getTabAt(3)?.text =
                            "BOWL " + (if (selectedPlayer.bowl_selected == 0) "" else "(" + selectedPlayer.bowl_selected + ")")
                        if (fantasyType == 1 || fantasyType == 2) {
                            mainBinding.tvPlayerCountPick.text = "Pick 1-5 Bowlers"
                        } else if (fantasyType == 3) {
                            mainBinding.tvPlayerCountPick.text = "Pick 2-4 Bowlers"
                        } else {
                            mainBinding.tvPlayerCountPick.text = "Pick 3-6 Bowlers"
                        }
                        /*  if (fm.fragments[0] is CreateTeamPlayerFragment) (fm.fragments[0] as CreateTeamPlayerFragment).refresh(
                              bolList,
                              BOWLER, teamCode
                          )*/
                    }
                }
                callFragmentRefresh()
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        // mainBinding.viewPager.addOnPageChangeListener(new );dd;
        mSelectedUnSelectedPlayerAdapter =
            SelectedUnSelectedPlayerAdapter(applicationContext, 0, totalPlayerCount)
        createTeamData()
        mainBinding.rvSelected.adapter = mSelectedUnSelectedPlayerAdapter
        setupRecyclerView()
        mainBinding.btnCreateTeam.setOnClickListener {

            if (selectedPlayer.selectedPlayer == totalPlayerCount) {
                val selectedList = ArrayList<PlayerListResult>()
                //  Log.e("check list ", wkListALL.toString())
                wkListALL.sortWith { contest: PlayerListResult, t1: PlayerListResult ->
                    contest.id.compareTo(t1.id)
                }
                batListALL.sortWith { contest: PlayerListResult, t1: PlayerListResult ->
                    contest.id.compareTo(t1.id)
                }
                arListALL.sortWith { contest: PlayerListResult, t1: PlayerListResult ->
                    contest.id.compareTo(t1.id)
                }
                bolListALL.sortWith { contest: PlayerListResult, t1: PlayerListResult ->
                    contest.id.compareTo(t1.id)
                }
                for (player: PlayerListResult in wkListALL) {
                    if (player.isSelected) selectedList.add(player)
                }
                for (player: PlayerListResult in batListALL) {
                    if (player.isSelected) selectedList.add(player)
                }
                for (player: PlayerListResult in arListALL) {
                    if (player.isSelected) selectedList.add(player)
                }
                for (player: PlayerListResult in bolListALL) {
                    if (player.isSelected) selectedList.add(player)
                }


                val intent = Intent(this@CreateTeamActivity, ChooseCandVCActivity::class.java)
                intent.putExtra(Constants.KEY_MATCH_KEY, matchKey)
                intent.putExtra(Constants.KEY_TEAM_VS, teamVsName)
                intent.putExtra(Constants.KEY_TEAM_FIRST_URL, teamFirstUrl)
                intent.putExtra("playerList", selectedList)
                intent.putExtra(Constants.KEY_TEAM_ID, teamId)
                intent.putExtra(Constants.KEY_STATUS_HEADER_TEXT, headerText)
                intent.putExtra(Constants.KEY_STATUS_IS_TIMER_HEADER, isShowTimer)
                intent.putExtra(Constants.KEY_FANTASY_TYPE, fantasyType)
                intent.putExtra(Constants.SPORT_KEY, Constants.TAG_CRICKET)
                intent.putExtra(Constants.FIRST_CREATE_TEAM, isForFirstTeamCreate)
                intent.putExtra(Constants.KEY_CONTEST_FIRST_TIME_DATA, contestFirstTime)
                if (isFromEditOrClone) intent.putExtra(
                    "isFromEditOrClone",
                    true
                ) else intent.putExtra("isFromEditOrClone", false)
                intent.putExtra(Constants.KEY_TEAM_SECOND_URL, teamSecondUrl)
                startActivityForResult(intent, 101)

            } else {
                showToast("Please select $totalPlayerCount players")
            }
        }
        mainBinding.ivTeamPreview.setOnClickListener { view: View? ->
            val intent = Intent(this@CreateTeamActivity, TeamPreviewActivity::class.java)
            intent.putExtra(Constants.KEY_MATCH_KEY, matchKey)
            intent.putExtra(Constants.KEY_TEAM_VS, teamVsName)
            intent.putExtra(Constants.KEY_TEAM_FIRST_URL, teamFirstUrl)
            intent.putExtra(Constants.KEY_TEAM_SECOND_URL, teamSecondUrl)
            intent.putExtra(Constants.KEY_FANTASY_TYPE, fantasyType)
            val selectedWkList = ArrayList<PlayerListResult>()
            val selectedBatLiSt: ArrayList<PlayerListResult> = ArrayList<PlayerListResult>()
            val selectedArList: ArrayList<PlayerListResult> = ArrayList<PlayerListResult>()
            val selectedBowlList: ArrayList<PlayerListResult> = ArrayList<PlayerListResult>()
            for (player: PlayerListResult in wkList) {
                if (player.isSelected) selectedWkList.add(player)
            }
            for (player: PlayerListResult in batList) {
                if (player.isSelected) selectedBatLiSt.add(player)
            }
            for (player: PlayerListResult in arList) {
                if (player.isSelected) selectedArList.add(player)
            }
            for (player: PlayerListResult in bolList) {
                if (player.isSelected) selectedBowlList.add(player)
            }
            intent.putExtra(Constants.KEY_TEAM_LIST_WK, selectedWkList)
            intent.putExtra(Constants.KEY_TEAM_LIST_BAT, selectedBatLiSt)
            intent.putExtra(Constants.KEY_TEAM_LIST_AR, selectedArList)
            intent.putExtra(Constants.KEY_TEAM_LIST_BOWL, selectedBowlList)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //   getMenuInflater().inflate(R.menu.menu_main,menu);
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

    private fun openNotificationActivity() {
        startActivity(Intent(this@CreateTeamActivity, NotificationActivity::class.java))
    }

    private fun openWalletActivity() {
        startActivity(Intent(this@CreateTeamActivity, MyWalletActivity::class.java))
    }

    private fun setupRecyclerView() {
        data
    }

    //   playerMaxLimit = limit.getPlayerMaxLimit();
    // playerMinLimit = limit.getPlayerMinLimit();
    private val data: Unit
        @SuppressLint("Range")
        get() {
            mainBinding.pickLayout.visibility = View.GONE
            val request = MyTeamRequest()
            request.user_id =
                MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
            request.matchkey = matchKey
            request.fantasy_type = fantasyType
            request.sport_key = AppUtils.getSaveSportKey()
            createTeamViewModel.loadPlayerListRequest(request)
            createTeamViewModel.getPlayerList()
                .observe(this, { arrayListResource: Resource<PlayerListResponse> ->
                    Log.d("Status ", "" + arrayListResource.status)
                    when (arrayListResource.status) {
                        Resource.Status.LOADING -> {
                            mainBinding.refreshing = true
                        }
                        Resource.Status.ERROR -> {
                            mainBinding.pickLayout.visibility = View.GONE
                            mainBinding.refreshing = false
                            if (arrayListResource.exception!!.response != null) {
                                if (arrayListResource.exception.response!!
                                        .code() in 400..403
                                ) {
                                    logout()
                                } else {
                                    Toast.makeText(
                                        MyApplication.appContext,
                                        arrayListResource.exception.getErrorModel().errorMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    MyApplication.appContext,
                                    arrayListResource.exception.getErrorModel().errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        Resource.Status.SUCCESS -> {
                            mainBinding.pickLayout.visibility = View.VISIBLE
                            mainBinding.refreshing = false
                            if (arrayListResource.data!!.status == 1 && arrayListResource.data.result.size > 0) {
                                allPlayerList = arrayListResource.data.result
                                limit = arrayListResource.data.limit

                                //   playerMaxLimit = limit.getPlayerMaxLimit();
                                // playerMinLimit = limit.getPlayerMinLimit();
                                totalCredit = limit.total_credits
                                totalPlayerCount = limit.maxplayers
                                maxTeamPlayerCount = limit.team_max_player
                                if (allPlayerList.size > 1) {

                                    if (allPlayerList[1].teamcolor != "") {
                                        mainBinding.tvTeamNameFirst.background.colorFilter =
                                            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                                                Color.parseColor(allPlayerList[1].teamcolor),
                                                BlendModeCompat.SRC_ATOP
                                            )

                                    }
                                    if (allPlayerList[0].teamcolor != "") {

                                        mainBinding.tvTeamNameSecond.background.colorFilter =
                                            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                                                Color.parseColor(allPlayerList[0].teamcolor),
                                                BlendModeCompat.SRC_ATOP
                                            )
                                    }
                                }
                                setData()
                                for (player in allPlayerList) {
                                    when (player.role) {
                                        Constants.KEY_PLAYER_ROLE_KEEP -> wkList.add(player)
                                        Constants.KEY_PLAYER_ROLE_BAT -> batList.add(player)
                                        Constants.KEY_PLAYER_ROLE_BOL -> bolList.add(player)
                                        Constants.KEY_PLAYER_ROLE_ALL_R -> arList.add(player)
                                    }
                                }
                                wkListALL.addAll(wkList)
                                batListALL.addAll(batList)
                                bolListALL.addAll(bolList)
                                arListALL.addAll(arList)
                                if (selectedList.size > 0) {
                                    var i = 0
                                    while (i < allPlayerList.size) {
                                        for (player in selectedList) {
                                            if (player.id
                                                == allPlayerList[i].id
                                            ) {
                                                allPlayerList[i].isSelected = true
                                                if (player.captain == 1) allPlayerList[i]
                                                    .isCaptain = true
                                                if (player.vicecaptain == 1) allPlayerList[i]
                                                    .isVcCaptain = true
                                            }
                                        }
                                        i++
                                    }
                                    setSelectedCountForEditOrClone()
                                }
                                setupViewPager(mainBinding.viewPager)
                                if (!MyApplication.preferenceDBTwo!!.getBoolean(
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
                                    MyApplication.preferenceDBTwo!!.putBoolean(
                                        Constants.SKIP_CREATETEAM_INSTRUCTION,
                                        true
                                    )
                                }
                            } else {
                                mainBinding.pickLayout.visibility = View.GONE
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
                if (player.role == Constants.KEY_PLAYER_ROLE_KEEP) {
                    countWK++
                }
                if (player.role == Constants.KEY_PLAYER_ROLE_BAT) {
                    countBAT++
                }
                if (player.role == Constants.KEY_PLAYER_ROLE_BOL) {
                    countBALL++
                }
                if (player.role == Constants.KEY_PLAYER_ROLE_ALL_R) {
                    countALL++
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
        if (fantasyType == 1 || fantasyType == 2) selectedPlayer.extra_player =
            5 else selectedPlayer.extra_player = 0
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
    }

    private fun setTeamNames() {
        if (teamVsName != null) {
            val teams = teamVsName!!.split("Vs").toTypedArray()
            mainBinding.tvTeamNameFirst.text = teams[0]
            mainBinding.tvTeamNameSecond.text = teams[1]
            teamNamesList.add(teams[0].replace(" ", ""))
            teamNamesList.add(teams[1].replace(" ", ""))
            teamNamesList.add("All")

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        isCreditsGlobal = false
        isPointsSortingGlobal = false
        isPointSelected = false
        isCreditSelected = false
    }

    private fun showPopUpInfo() {
        val dialogue = Dialog(this)
        dialogue.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogue.setContentView(R.layout.rules_layout_dialog)
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
            selectedPlayer = SelectedPlayer()
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
            wkList.forEach { it.isSelected = false }
            bolList.forEach { it.isSelected = false }
            arList.forEach { it.isSelected = false }
            batList.forEach { it.isSelected = false }

        }
        if (dialogue.isShowing) dialogue.dismiss()
        dialogue.show()
    }
    /*  private fun showTimer() {
          try {
              val countDownTimer: CountDownTimer =
                  object : CountDownTimer(AppUtils.eventDateMileSecond(headerText), 1000) {
                      @SuppressLint("SetTextI18n")
                      override fun onTick(millisUntilFinished: Long) {
                          val seconds = millisUntilFinished / 1000 % 60
                          val minutes = (millisUntilFinished / (1000 * 60)) % 60
                          val diffHours = (millisUntilFinished / (60 * 60 * 1000))
                          mainBinding.matchHeaderInfo.tvTimeTimer.text =
                              twoDigitString(diffHours) + "h : " + twoDigitString(minutes) + "m : " + twoDigitString(
                                  seconds
                              ) + "s "
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
      }*/

    fun openPlayerInfoActivity(
        playerId: String?,
        playerName: String?,
        team: String?,
        image: String?,
    ) {
        val intent = Intent(this, PlayerInfoActivity::class.java)
        intent.putExtra("matchKey", matchKey)
        intent.putExtra("playerId", playerId)
        intent.putExtra("playerName", playerName)
        intent.putExtra("team", team)
        intent.putExtra("image", image)
        intent.putExtra(Constants.SPORT_KEY, Constants.TAG_CRICKET)
        startActivity(intent)
    }

    internal inner class ViewPagerAdapter(manager: FragmentManager?) : FragmentPagerAdapter(
        (manager)!!
    ) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
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
                0 -> return "WK " + (if (selectedPlayer.wk_selected == 0) "" else "(" + selectedPlayer.wk_selected + ")")
                1 -> return "BAT " + (if (selectedPlayer.bat_selected == 0) "" else "(" + selectedPlayer.bat_selected + ")")
                2 -> return "AR " + (if (selectedPlayer.ar_selected == 0) "" else "(" + selectedPlayer.ar_selected + ")")
                3 -> return "BOWL " + (if (selectedPlayer.bowl_selected == 0) "" else "(" + selectedPlayer.bowl_selected + ")")
            }
            return ""
        }

        override fun getItemPosition(`object`: Any): Int {
            // POSITION_NONE makes it possible to reload the PagerAdapter
            return POSITION_NONE
        }
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(
            supportFragmentManager
        )
        adapter.addFrag(newInstance(allPlayerList, wkList, WK, fantasyType))
        adapter.addFrag(newInstance(allPlayerList, batList, BAT, fantasyType))
        adapter.addFrag(newInstance(allPlayerList, arList, AR, fantasyType))
        adapter.addFrag(newInstance(allPlayerList, bolList, BOWLER, fantasyType))
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 0
        mainBinding.tabLayout.setupWithViewPager(viewPager)
        if (fantasyType != 0) {
            Handler().postDelayed(
                {
                    when (fantasyType) {
                        1 -> {
                            mainBinding.tabLayout.getTabAt(1)!!.select()
                        }
                        2 -> {
                            mainBinding.tabLayout.getTabAt(3)!!.select()
                        }
                        3 -> {
                            mainBinding.tabLayout.getTabAt(0)!!.select()

                        }
                    }

                }, 100
            )
        }
    }

    @SuppressLint("SetTextI18n")
    fun onPlayerClick(isSelect: Boolean, position: Int, type: Int) {
        if (type == WK) {
            val playerCredit: Double
            if (isSelect) {
                if (selectedPlayer.selectedPlayer >= totalPlayerCount) {
                    showTeamValidation("You can choose maximum $totalPlayerCount players.")
                    return
                }
                if (fantasyType == 0) {
                    if (selectedPlayer.wk_selected >= 4) {
                        showTeamValidation("You can select only 4 Wicket-Keepers.")
                        return
                    }
                }
                if ((wkList[position].team == "team1")) {
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
                            var extra = selectedPlayer.extra_player
                            if (selectedPlayer.wk_selected >= selectedPlayer.wk_min_count) {
                                extra = selectedPlayer.extra_player - 1
                            }
                            playerCredit = wkList[position].credit
                            val totalCredits = selectedPlayer.total_credit + playerCredit
                            if (totalCredits > totalCredit) {
                                exeedCredit = true
                                showTeamValidation("Not enough credits to select this player.")
                                return
                            }
                            var localTeamPlayerCount = selectedPlayer.localTeamplayerCount
                            var visitorTeamPlayerCount = selectedPlayer.visitorTeamPlayerCount
                            if ((wkList[position].team == "team1")) localTeamPlayerCount =
                                selectedPlayer.localTeamplayerCount + 1 else visitorTeamPlayerCount =
                                selectedPlayer.visitorTeamPlayerCount + 1
                            wkList[position].isSelected = isSelect
                            updateTeamData(
                                extra,
                                selectedPlayer.wk_selected + 1,
                                selectedPlayer.bat_selected,
                                selectedPlayer.ar_selected,
                                selectedPlayer.bowl_selected,
                                selectedPlayer.selectedPlayer + 1,
                                localTeamPlayerCount,
                                visitorTeamPlayerCount,
                                totalCredits
                            )
                        } else {
                            minimumPlayerWarning()
                        }
                    }
                }
            } else {
                if (selectedPlayer.wk_selected > 0) {
                    if (fantasyType == 1) {
                        mainBinding.tvPlayerCountPick.text = "Pick 1-5 Wicket-Keepers"
                    } else if (fantasyType == 3) {
                        mainBinding.tvPlayerCountPick.text = "Pick 1-3 Wicket-Keepers"
                    } else {
                        mainBinding.tvPlayerCountPick.text = "Pick 1-4 Wicket-Keepers"
                    }
                    playerCredit = wkList[position].credit
                    val total_credit = selectedPlayer.total_credit - playerCredit
                    var extra = selectedPlayer.extra_player
                    if (selectedPlayer.wk_selected > selectedPlayer.wk_min_count) {
                        extra = selectedPlayer.extra_player + 1
                    }
                    var localTeamPlayerCount = selectedPlayer.localTeamplayerCount
                    var visitorTeamPlayerCount = selectedPlayer.visitorTeamPlayerCount
                    if ((wkList[position].team == "team1")) localTeamPlayerCount =
                        selectedPlayer.localTeamplayerCount - 1 else visitorTeamPlayerCount =
                        selectedPlayer.visitorTeamPlayerCount - 1
                    wkList[position].isSelected = isSelect
                    updateTeamData(
                        extra,
                        selectedPlayer.wk_selected - 1,
                        selectedPlayer.bat_selected,
                        selectedPlayer.ar_selected,
                        selectedPlayer.bowl_selected,
                        selectedPlayer.selectedPlayer - 1,
                        localTeamPlayerCount,
                        visitorTeamPlayerCount,
                        total_credit
                    )
                }
            }
        } else if (type == BAT) {
            val player_credit: Double
            if (isSelect) {
                if (selectedPlayer.selectedPlayer >= totalPlayerCount) {
                    showTeamValidation("You can choose maximum $totalPlayerCount players")
                    return
                }
                if (selectedPlayer.bat_selected >= 6) {
                    showTeamValidation("You can select only 6 Batsmen")
                    return
                }
                if ((batList[position].team == "team1")) {
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
                            var extra = selectedPlayer.extra_player
                            if (selectedPlayer.bat_selected >= selectedPlayer.bat_mincount) {
                                extra = selectedPlayer.extra_player - 1
                            }
                            player_credit = batList[position].credit
                            val total_credit = selectedPlayer.total_credit + player_credit
                            if (total_credit > totalCredit) {
                                exeedCredit = true
                                showTeamValidation("Not enough credits to select this player.")
                                return
                            }
                            var localTeamPlayerCount = selectedPlayer.localTeamplayerCount
                            var visitorTeamPlayerCount = selectedPlayer.visitorTeamPlayerCount
                            if ((batList[position].team == "team1")) localTeamPlayerCount =
                                selectedPlayer.localTeamplayerCount + 1 else visitorTeamPlayerCount =
                                selectedPlayer.visitorTeamPlayerCount + 1
                            batList[position].isSelected = isSelect
                            updateTeamData(
                                extra,
                                selectedPlayer.wk_selected,
                                selectedPlayer.bat_selected + 1,
                                selectedPlayer.ar_selected,
                                selectedPlayer.bowl_selected,
                                selectedPlayer.selectedPlayer + 1,
                                localTeamPlayerCount,
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
                    if (fantasyType == 1) {
                        mainBinding.tvPlayerCountPick.text = "Pick 1-5 Batsmen"
                    } else if (fantasyType == 3) {
                        mainBinding.tvPlayerCountPick.text = "Pick 2-4 Batsmen"
                    } else {
                        mainBinding.tvPlayerCountPick.text = "Pick 3-6 Batsmen"
                    }
                    player_credit = batList[position].credit
                    val total_credit = selectedPlayer.total_credit - player_credit
                    var extra = selectedPlayer.extra_player
                    if (selectedPlayer.bat_selected > selectedPlayer.bat_mincount) {
                        extra = selectedPlayer.extra_player + 1
                    }
                    var localTeamPlayerCount = selectedPlayer.localTeamplayerCount
                    var visitorTeamPlayerCount = selectedPlayer.visitorTeamPlayerCount
                    if ((batList[position].team == "team1")) localTeamPlayerCount =
                        selectedPlayer.localTeamplayerCount - 1 else visitorTeamPlayerCount =
                        selectedPlayer.visitorTeamPlayerCount - 1
                    batList[position].isSelected = isSelect
                    updateTeamData(
                        extra,
                        selectedPlayer.wk_selected,
                        selectedPlayer.bat_selected - 1,
                        selectedPlayer.ar_selected,
                        selectedPlayer.bowl_selected,
                        selectedPlayer.selectedPlayer - 1,
                        localTeamPlayerCount,
                        visitorTeamPlayerCount,
                        total_credit
                    )
                }
            }
        } else if (type == AR) {
            val player_credit: Double
            if (isSelect) {
                if (selectedPlayer.selectedPlayer >= totalPlayerCount) {
                    showTeamValidation("You can choose maximum $totalPlayerCount players.")
                    return
                }
                if (fantasyType == 0) {
                    if (selectedPlayer.ar_selected >= 4) {
                        showTeamValidation("You can select only 4 All-Rounders.")
                        return
                    }
                } else if (fantasyType == 3) {
                    if (selectedPlayer.ar_selected >= 2) {
                        showTeamValidation("You can select only 2 All-Rounders.")
                        return
                    }
                }
                if ((arList[position].team == "team1")) {
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
                if (selectedPlayer.ar_selected < selectedPlayer.ar_maxcount) {
                    if (selectedPlayer.selectedPlayer < totalPlayerCount) {
                        if (selectedPlayer.ar_selected < selectedPlayer.ar_mincount || selectedPlayer.extra_player > 0) {
                            var extra = selectedPlayer.extra_player
                            if (selectedPlayer.ar_selected >= selectedPlayer.ar_mincount) {
                                extra = selectedPlayer.extra_player - 1
                            }
                            player_credit = arList[position].credit
                            val total_credit = selectedPlayer.total_credit + player_credit
                            if (total_credit > totalCredit) {
                                exeedCredit = true
                                showTeamValidation("Not enough credits to select this player.")
                                return
                            }
                            var localTeamPlayerCount = selectedPlayer.localTeamplayerCount
                            var visitorTeamPlayerCount = selectedPlayer.visitorTeamPlayerCount
                            if ((arList[position].team == "team1")) localTeamPlayerCount =
                                selectedPlayer.localTeamplayerCount + 1 else visitorTeamPlayerCount =
                                selectedPlayer.visitorTeamPlayerCount + 1
                            arList[position].isSelected = isSelect
                            updateTeamData(
                                extra,
                                selectedPlayer.wk_selected,
                                selectedPlayer.bat_selected,
                                selectedPlayer.ar_selected + 1,
                                selectedPlayer.bowl_selected,
                                selectedPlayer.selectedPlayer + 1,
                                localTeamPlayerCount,
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
                    if (fantasyType == 1 || fantasyType == 2) {
                        mainBinding.tvPlayerCountPick.text = "Pick 1-5 All-Rounders"
                    } else if (fantasyType == 3) {
                        mainBinding.tvPlayerCountPick.text = "Pick 1-2 All-Rounders"
                    } else {
                        mainBinding.tvPlayerCountPick.text = "Pick 1-4 All-Rounders"
                    }
                    player_credit = arList[position].credit
                    val total_credit = selectedPlayer.total_credit - player_credit
                    var extra = selectedPlayer.extra_player
                    if (selectedPlayer.ar_selected > selectedPlayer.ar_mincount) {
                        extra = selectedPlayer.extra_player + 1
                    }
                    var localTeamPlayerCount = selectedPlayer.localTeamplayerCount
                    var visitorTeamPlayerCount = selectedPlayer.visitorTeamPlayerCount
                    if ((arList[position].team == "team1")) localTeamPlayerCount =
                        selectedPlayer.localTeamplayerCount - 1 else visitorTeamPlayerCount =
                        selectedPlayer.visitorTeamPlayerCount - 1
                    arList[position].isSelected = isSelect
                    updateTeamData(
                        extra,
                        selectedPlayer.wk_selected,
                        selectedPlayer.bat_selected,
                        selectedPlayer.ar_selected - 1,
                        selectedPlayer.bowl_selected,
                        selectedPlayer.selectedPlayer - 1,
                        localTeamPlayerCount,
                        visitorTeamPlayerCount,
                        total_credit
                    )
                }
            }
        } else if (type == BOWLER) {
            val playerCredit: Double
            if (isSelect) {
                if (selectedPlayer.selectedPlayer >= totalPlayerCount) {
                    showTeamValidation("You can choose maximum $totalPlayerCount players.")
                    return
                }
                if (selectedPlayer.bowl_selected >= 6) {
                    showTeamValidation("You can select only 6 Bowlers.")
                    return
                }
                if ((bolList[position].team == "team1")) {
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
                        if (selectedPlayer.bowl_selected < selectedPlayer.bowl_mincount || selectedPlayer.extra_player > 0) {
                            var extra = selectedPlayer.extra_player
                            if (selectedPlayer.bowl_selected >= selectedPlayer.bowl_mincount) {
                                extra = selectedPlayer.extra_player - 1
                            }
                            playerCredit = bolList[position].credit
                            val total_credit = selectedPlayer.total_credit + playerCredit
                            if (total_credit > totalCredit) {
                                exeedCredit = true
                                showTeamValidation("Not enough credits to select this player.")
                                return
                            }
                            var localTeamPlayerCount = selectedPlayer.localTeamplayerCount
                            var visitorTeamPlayerCount = selectedPlayer.visitorTeamPlayerCount
                            if ((bolList[position].team == "team1")) localTeamPlayerCount =
                                selectedPlayer.localTeamplayerCount + 1 else visitorTeamPlayerCount =
                                selectedPlayer.visitorTeamPlayerCount + 1
                            bolList[position].isSelected = isSelect
                            updateTeamData(
                                extra,
                                selectedPlayer.wk_selected,
                                selectedPlayer.bat_selected,
                                selectedPlayer.ar_selected,
                                selectedPlayer.bowl_selected + 1,
                                selectedPlayer.selectedPlayer + 1,
                                localTeamPlayerCount,
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
                    if (fantasyType == 1 || fantasyType == 2) {
                        mainBinding.tvPlayerCountPick.text = "Pick 1-5 Bowlers"
                    } else if (fantasyType == 3) {
                        mainBinding.tvPlayerCountPick.text = "Pick 2-4 Bowlers"
                    } else {
                        mainBinding.tvPlayerCountPick.text = "Pick 3-6 Bowlers"
                    }
                    playerCredit = bolList[position].credit
                    val total_credit = selectedPlayer.total_credit - playerCredit
                    var extra = selectedPlayer.extra_player
                    if (selectedPlayer.bowl_selected > selectedPlayer.bowl_mincount) {
                        extra = selectedPlayer.extra_player + 1
                    }
                    var localTeamPlayerCount = selectedPlayer.localTeamplayerCount
                    var visitorTeamPlayerCount = selectedPlayer.visitorTeamPlayerCount
                    if ((bolList[position].team == "team1")) localTeamPlayerCount =
                        selectedPlayer.localTeamplayerCount - 1 else visitorTeamPlayerCount =
                        selectedPlayer.visitorTeamPlayerCount - 1
                    bolList[position].isSelected = isSelect
                    updateTeamData(
                        extra,
                        selectedPlayer.wk_selected,
                        selectedPlayer.bat_selected,
                        selectedPlayer.ar_selected,
                        selectedPlayer.bowl_selected - 1,
                        selectedPlayer.selectedPlayer - 1,
                        localTeamPlayerCount,
                        visitorTeamPlayerCount,
                        total_credit
                    )
                }
            }
        }
    }

    private fun createTeamData() {
        selectedPlayer = SelectedPlayer()
        if (fantasyType == 1 || fantasyType == 2) selectedPlayer.extra_player =
            5 else if (fantasyType == 3) selectedPlayer.extra_player =
            1 else selectedPlayer.extra_player = 3
        if (fantasyType == 3) {
            selectedPlayer.wk_min_count = 1
            selectedPlayer.wk_max_count = 3
        } else {
            selectedPlayer.wk_min_count = 1
            selectedPlayer.wk_max_count = 4
        }
        selectedPlayer.wk_selected = 0
        if (fantasyType == 3) {
            selectedPlayer.bat_mincount = 2
            selectedPlayer.bat_maxcount = 4
        } else {
            selectedPlayer.bat_mincount = 3
            selectedPlayer.bat_maxcount = 6
        }
        selectedPlayer.bat_selected = 0
        if (fantasyType == 3) {
            selectedPlayer.bowl_mincount = 2
            selectedPlayer.bowl_maxcount = 4
        } else {
            selectedPlayer.bowl_mincount = 3
            selectedPlayer.bowl_maxcount = 6
        }
        selectedPlayer.bowl_selected = 0
        selectedPlayer.ar_mincount = 1
        if (fantasyType == 1 || fantasyType == 2) selectedPlayer.ar_maxcount =
            5 else if (fantasyType == 3) selectedPlayer.ar_maxcount =
            2 else selectedPlayer.ar_maxcount = 4
        selectedPlayer.ar_selected = 0
        selectedPlayer.selectedPlayer = 0
        selectedPlayer.localTeamplayerCount = 0
        selectedPlayer.visitorTeamPlayerCount = 0
        selectedPlayer.total_credit = 0.0
        updateUi()
    }

    private fun updateTeamData(
        extra_player: Int,
        wk_selected: Int,
        bat_selected: Int,
        ar_selected: Int,
        bowl_selected: Int,
        selectPlayer: Int,
        localTeamPlayerCount: Int,
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
        selectedPlayer.localTeamplayerCount = localTeamPlayerCount
        selectedPlayer.visitorTeamPlayerCount = visitorTeamPlayerCount
        selectedPlayer.total_credit = total_credit
        updateUi()
    }

    @SuppressLint("SetTextI18n")
    private fun updateUi() {
        if (selectedPlayer.selectedPlayer >= 0) mainBinding.tvSelectedPlayer.text =
            selectedPlayer.selectedPlayer.toString() else mainBinding.tvSelectedPlayer.text =
            0.toString() + ""
        if (selectedPlayer.total_credit < 0) selectedPlayer.total_credit = 0.0
        val creditLeft = (totalCredit - selectedPlayer.total_credit).toString()
        mainBinding.tvUsedCredit.text = creditLeft + ""
        mainBinding.tvTeamCountFirst.text = selectedPlayer.localTeamplayerCount.toString()
        mainBinding.tvTeamCountSecond.text = selectedPlayer.visitorTeamPlayerCount.toString()
        mSelectedUnSelectedPlayerAdapter.update(selectedPlayer.selectedPlayer)
        if (mainBinding.tabLayout.tabCount > 0) {
            Log.e("childCount", mainBinding.tabLayout.childCount.toString() + "")
            Log.e("tabCount", mainBinding.tabLayout.tabCount.toString() + "")
            callFragmentRefresh()
        }
    }

    private fun callFragmentRefresh() {
        try {
            if (mainBinding.viewPager.adapter != null) mainBinding.viewPager.adapter!!.notifyDataSetChanged()
            val fm = supportFragmentManager
            when (selectedType) {
                1 -> {
                    wkList = if (teamCode != "All") {
                        wkListALL.filter {
                            it.teamcode.equals(teamCode,
                                ignoreCase = true)
                        } as ArrayList<PlayerListResult>
                    } else {
                        wkListALL
                    }
                    mainBinding.tabLayout.getTabAt(0)!!.text =
                        "WK " + (if (selectedPlayer.wk_selected == 0) "" else "(" + selectedPlayer.wk_selected + ")")

                    if (fm.fragments[0] is CreateTeamPlayerFragment) (fm.fragments[0] as CreateTeamPlayerFragment).refresh(
                        wkList,
                        selectedType, teamCode
                    )
                }
                2 -> {
                    batList = if (teamCode != "All") {
                        batListALL.filter {
                            it.teamcode.equals(teamCode,
                                ignoreCase = true)
                        } as ArrayList<PlayerListResult>
                    } else {
                        batListALL
                    }
                    mainBinding.tabLayout.getTabAt(1)!!.text =
                        "BAT " + (if (selectedPlayer.bat_selected == 0) "" else "(" + selectedPlayer.bat_selected + ")")
                    if (fm.fragments[0] is CreateTeamPlayerFragment) (fm.fragments[0] as CreateTeamPlayerFragment).refresh(
                        batList,
                        selectedType, teamCode
                    )
                }
                3 -> {
                    arList = if (teamCode != "All") {
                        arListALL.filter {
                            it.teamcode.equals(teamCode,
                                ignoreCase = true)
                        } as ArrayList<PlayerListResult>
                    } else {
                        arListALL
                    }
                    mainBinding.tabLayout.getTabAt(2)!!.text =
                        "AR " + (if (selectedPlayer.ar_selected == 0) "" else "(" + selectedPlayer.ar_selected + ")")
                    if (fm.fragments[0] is CreateTeamPlayerFragment) (fm.fragments[0] as CreateTeamPlayerFragment).refresh(
                        arList,
                        selectedType, teamCode
                    )
                }
                4 -> {
                    bolList = if (teamCode != "All") {
                        bolListALL.filter {
                            it.teamcode.equals(teamCode,
                                ignoreCase = true)
                        } as ArrayList<PlayerListResult>
                    } else {
                        bolListALL
                    }
                    mainBinding.tabLayout.getTabAt(3)!!.text =
                        "BOWL " + (if (selectedPlayer.bowl_selected == 0) "" else "(" + selectedPlayer.bowl_selected + ")")
                    if (fm.fragments[0] is CreateTeamPlayerFragment) (fm.fragments[0] as CreateTeamPlayerFragment).refresh(
                        bolList,
                        selectedType, teamCode
                    )
                }
            }
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "some system issue try again", Toast.LENGTH_SHORT).show()
            FirebaseCrashlytics.getInstance().setCustomKey("key", matchKey)

        }

    }

    fun showToast(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun minimumPlayerWarning() {
        if (fantasyType == 0) {
            when {
                selectedPlayer.bowl_selected < 3 -> {
                    showTeamValidation("You must select at least 3 Bowlers.")
                }
                selectedPlayer.bat_selected < 3 -> {
                    showTeamValidation("You must select at least 3 Batsmen.")
                }
                selectedPlayer.ar_selected < 1 -> {
                    showTeamValidation("You must select at least 1 All-Rounders.")
                }
                selectedPlayer.wk_selected < 1 -> {
                    showTeamValidation("You must select at least 1 Wicket-Keepers.")
                }
            }
        } else if (fantasyType == 3) {
            when {
                selectedPlayer.bowl_selected < 2 -> {
                    showTeamValidation("You must select at least 2 Bowlers.")
                }
                selectedPlayer.bat_selected < 2 -> {
                    showTeamValidation("You must select at least 2 Batsmen.")
                }
                selectedPlayer.ar_selected < 1 -> {
                    showTeamValidation("You must select at least 1 All-Rounders.")
                }
                selectedPlayer.wk_selected < 1 -> {
                    showTeamValidation("You must select at least 1 Wicket-Keepers.")
                }
            }
        }
    }

    private fun showTeamValidation(msg: String) {
        val flashBar: Flashbar = Flashbar.Builder(this)
            .gravity(Flashbar.Gravity.TOP)
            .message(msg)
            .backgroundDrawable(R.drawable.bg_gradient_create_team_warning)
            .showIcon()
            .icon(R.drawable.close)
            .iconAnimation(
                FlashAnim.with(this)
                    .animateIcon()
                    .pulse()
                    .alpha()
                    .duration(1000)
                    .accelerate()
            )
            .build()
        flashBar.show()
        Handler().postDelayed({ flashBar.dismiss() }, 2000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                val returnIntent = Intent()
                returnIntent.putExtra(
                    "isTeamCreated",
                    data!!.getBooleanExtra("isTeamCreated", false)
                )
                setResult(RESULT_OK, returnIntent)
                finish()
            }
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
            CustomCallback<NormalResponse> {
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

    @SuppressLint("SetTextI18n")
    private fun setData() {
        mainBinding.tvTotalCredit.text = " /$totalCredit"
        mainBinding.tvTotalPlayer.text = " /$totalPlayerCount"
        //   mainBinding.tvMaxPlayerWarning.setText("Max "+maxTeamPlayerCount+" Players from a one team");
        mSelectedUnSelectedPlayerAdapter.updateTotalPlayerCount(totalPlayerCount)
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

        /*     new Handler().postDelayed(() -> showcaseView.hideButton(), 2500);*/
    }


    override fun onShowcaseViewHide(showcaseView: ShowcaseView) {
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

    //    override fun onShowcaseViewDidHide(showcaseView: ShowcaseView) {}
//    override fun onShowcaseViewShow(showcaseView: ShowcaseView) {}
//    override fun onShowcaseViewTouchBlocked(motionEvent: MotionEvent) {}
    private fun showBottomSheetDialog() {
        bottomSheetDialog = BottomSheetDialog(this@CreateTeamActivity)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        val rvView = bottomSheetDialog.findViewById<RecyclerView>(R.id.rvView)
        val txtCross = bottomSheetDialog.findViewById<TextView>(R.id.tvCross)
        txtCross!!.setOnClickListener { bottomSheetDialog.cancel() }
        teamfilterAdapter =
            TeamFilterAdapter(this@CreateTeamActivity,
                teamNamesList,
                this@CreateTeamActivity,
                selectTeamPosition)
        if (rvView != null) {
            rvView.setHasFixedSize(false)
            rvView.layoutManager = mLayoutManager
            rvView.adapter = teamfilterAdapter
        }
        bottomSheetDialog.show()
    }

    companion object {
        private const val WK = 1
        private const val BAT = 2
        private const val AR = 3
        private const val BOWLER = 4

        @SuppressLint("StaticFieldLeak")
        var createTeamAc: Activity? = null
    }

    override fun getTeamDataClick(teamCode: String, position: Int) {
        selectTeamPosition = position
        bottomSheetDialog.dismiss()
        this.teamCode = teamCode.replace(" ", "")
        callFragmentRefresh()
        //  val fm = supportFragmentManager
        /*if (fm.fragments[0] is CreateTeamPlayerFragment) (fm.fragments[0] as CreateTeamPlayerFragment).filterTeamData(
            teamCode
        )*/
        // CreateTeamPlayerFragment().filterTeamData(teamCode)
    }


    override fun onShowcaseViewDidHide(showcaseView: ShowcaseView?) {

    }

    override fun onShowcaseViewShow(showcaseView: ShowcaseView?) {

    }

    override fun onShowcaseViewTouchBlocked(motionEvent: MotionEvent?) {

    }

}