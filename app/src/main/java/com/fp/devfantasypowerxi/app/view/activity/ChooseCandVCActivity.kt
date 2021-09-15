package com.fp.devfantasypowerxi.app.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.request.CreateTeamRequest
import com.fp.devfantasypowerxi.app.api.request.JoinContestRequest
import com.fp.devfantasypowerxi.app.api.response.JoinItem
import com.fp.devfantasypowerxi.app.api.response.League
import com.fp.devfantasypowerxi.app.api.response.NormalResponse
import com.fp.devfantasypowerxi.app.api.response.PlayerListResult
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.adapter.ChooseCaptainVCPlayerItemAdapter
import com.fp.devfantasypowerxi.app.view.listners.PlayerItemClickListener
import com.fp.devfantasypowerxi.app.view.listners.TeamCreatedListener
import com.fp.devfantasypowerxi.app.view.viewmodel.CreateTeamViewModel
import com.fp.devfantasypowerxi.app.view.viewmodel.TeamViewModel
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.api.Resource
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivityChooseCandVCBinding
import com.github.amlcurran.showcaseview.OnShowcaseEventListener
import com.github.amlcurran.showcaseview.ShowcaseView
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

// made by Gaurav Minocha
class ChooseCandVCActivity : AppCompatActivity(), PlayerItemClickListener, OnShowcaseEventListener {

    lateinit var mainBinding: ActivityChooseCandVCBinding
    var mAdapter: ChooseCaptainVCPlayerItemAdapter? = null
    lateinit var createTeamViewModel: CreateTeamViewModel
    var matchKey: String = ""
    var teamVsName: String = ""
    var teamFirstUrl: String = ""
    var teamSecondUrl: String = ""
    var teamId = 0
    private var isFromEditClone = false
    var isShowTimer = false
    var headerText: String = ""
    var listener: TeamCreatedListener? = null
    private var isPointsSorting = false
    private var isCSorting = false
    private var isVcSorting = false
    var sportKey = ""
    var fantasyType = 0
    var counterValue = 0
    var isForFirstTeamCreate = false
    lateinit var teamViewModel: TeamViewModel
    private var contestFirstTime: League? = null
    var createdTeamId = 0
    var totalCoinsAvailable=""

    @Inject
    lateinit var oAuthRestService: OAuthRestService
    var list: ArrayList<PlayerListResult> = ArrayList<PlayerListResult>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_choose_cand_v_c
        )
        createTeamViewModel = CreateTeamViewModel().create(this@ChooseCandVCActivity)
        teamViewModel = TeamViewModel().create(this@ChooseCandVCActivity)
        MyApplication.getAppComponent()!!.inject(teamViewModel)
        MyApplication.getAppComponent()!!.inject(createTeamViewModel)
        MyApplication.getAppComponent()!!.inject(this@ChooseCandVCActivity)
        mainBinding.ivTeamPreview.setOnClickListener {
            startActivity(Intent(this@ChooseCandVCActivity, TeamPreviewActivity::class.java))
        }
        mainBinding.btnCreateTeam.setOnClickListener {
            finish()
        }
        initialize()
    }

    // initialize toolbar
    @SuppressLint("SetTextI18n")
    fun initialize() {
        setSupportActionBar(mainBinding.myToolbar)
        if (supportActionBar != null) {
            //    supportActionBar!!.title = getString(R.string.choose_c_vc_title)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
        if (intent != null && intent.extras != null) {
            matchKey = intent.extras!!.getString(Constants.KEY_MATCH_KEY)!!
            teamVsName = intent.extras!!.getString(Constants.KEY_TEAM_VS)!!
            teamFirstUrl = intent.extras!!.getString(Constants.KEY_TEAM_FIRST_URL)!!
            teamSecondUrl = intent.extras!!.getString(Constants.KEY_TEAM_SECOND_URL)!!
            list = intent.getParcelableArrayListExtra("playerList")!!
            teamId = intent.extras!!.getInt(Constants.KEY_TEAM_ID)
            isFromEditClone = intent.extras!!.getBoolean("isFromEditOrClone", false)
            isShowTimer = intent.extras!!.getBoolean(Constants.KEY_STATUS_IS_TIMER_HEADER, false)
            headerText = intent.extras!!.getString(Constants.KEY_STATUS_HEADER_TEXT, "")
            sportKey = intent.extras!!.getString(Constants.SPORT_KEY)!!
            fantasyType = intent.extras!!.getInt(Constants.KEY_FANTASY_TYPE)
            isForFirstTeamCreate = intent.extras!!.getBoolean(Constants.FIRST_CREATE_TEAM, false)
            contestFirstTime =
                intent.getParcelableExtra(Constants.KEY_CONTEST_FIRST_TIME_DATA)!!
        }
        if (sportKey.equals(Constants.TAG_BASKETBALL, ignoreCase = true)) {
            supportActionBar!!.title = getString(R.string.choose_star_title)
            mainBinding.llSortBySp.visibility = View.VISIBLE
            mainBinding.llSortByC.visibility = View.GONE
            mainBinding.llSortByVc.visibility = View.GONE

            //This is for top Layout
            mainBinding.llCaptain.visibility = View.VISIBLE
            mainBinding.llViceCaptain.visibility = View.GONE
            mainBinding.tvSp.text = "SP"
        } else {
            supportActionBar!!.title = getString(R.string.choose_c_vc_title)
            mainBinding.llSortBySp.visibility = View.GONE
            mainBinding.llSortByC.visibility = View.VISIBLE
            mainBinding.llSortByVc.visibility = View.VISIBLE
            mainBinding.tvSp.text = "C"
            mainBinding.llCaptain.visibility = View.VISIBLE
            mainBinding.llViceCaptain.visibility = View.VISIBLE
        }

        mainBinding.matchHeaderInfo.tvTeamVs.text = teamVsName
        mainBinding.matchHeaderInfo.ivTeamFirst.setImageURI(teamFirstUrl)
        mainBinding.matchHeaderInfo.ivTeamSecond.setImageURI(teamSecondUrl)
        if (isFromEditClone) {
            for (player in list) {
                if (player.isCaptain) selectedCaptain = player.name
                if (player.isVcCaptain) selectedVCaptain = player.name
            }

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
        setupRecyclerView()


        mainBinding.btnCreateTeam.setOnClickListener {
            if (sportKey.equals(Constants.TAG_BASKETBALL, ignoreCase = true)) {
                if (selectedCaptain == "") {
                    AppUtils.showError(this@ChooseCandVCActivity, "Please select star player.")
                } else {
                    createTeam()
                }
            } else {
                if (selectedCaptain == "" || selectedVCaptain == "") {
                    AppUtils.showError(
                        this@ChooseCandVCActivity,
                        "Please select captain & vice-captain"
                    )
                } else {
                    createTeam()
                }
            }
        }
        mainBinding.llSortByPoint.setOnClickListener {
            if (mAdapter != null) {
                if (isPointsSorting) {
                    isPointsSorting = false
                    mAdapter!!.sortWithPoints(true)
                    mainBinding.icSortByPoint.visibility = View.VISIBLE
                    mainBinding.icSortByPoint.setImageResource(R.drawable.ic_down_sort)
                    mainBinding.ivSortByC.visibility = View.INVISIBLE
                    mainBinding.ivSortByVc.visibility = View.INVISIBLE
                } else {
                    isPointsSorting = true
                    mAdapter!!.sortWithPoints(false)
                    mainBinding.icSortByPoint.visibility = View.VISIBLE
                    mainBinding.icSortByPoint.setImageResource(R.drawable.ic_up_sort)
                    mainBinding.ivSortByC.visibility = View.INVISIBLE
                    mainBinding.ivSortByVc.visibility = View.INVISIBLE
                }
            }
        }


        mainBinding.llSortByC.setOnClickListener {
            if (mAdapter != null) {
                if (isCSorting) {
                    isCSorting = false
                    mAdapter!!.sortWithC(true)
                    mainBinding.ivSortByC.setImageResource(R.drawable.ic_down_sort)
                    mainBinding.ivSortByC.visibility = View.VISIBLE
                    mainBinding.icSortByPoint.visibility = View.INVISIBLE
                    mainBinding.ivSortByVc.visibility = View.INVISIBLE
                } else {
                    isCSorting = true
                    mAdapter!!.sortWithC(false)
                    mainBinding.ivSortByC.setImageResource(R.drawable.ic_up_sort)
                    mainBinding.ivSortByC.visibility = View.VISIBLE
                    mainBinding.icSortByPoint.visibility = View.INVISIBLE
                    mainBinding.ivSortByVc.visibility = View.INVISIBLE
                }
            }
        }
        mainBinding.llSortByVc.setOnClickListener {
            if (mAdapter != null) {
                if (isVcSorting) {
                    isVcSorting = false
                    mAdapter!!.sortWithVc(true)
                    mainBinding.ivSortByVc.visibility = View.VISIBLE
                    mainBinding.ivSortByVc.setImageResource(R.drawable.ic_down_sort)
                    mainBinding.ivSortByC.visibility = View.INVISIBLE
                    mainBinding.icSortByPoint.visibility = View.INVISIBLE
                } else {
                    isVcSorting = true
                    mAdapter!!.sortWithVc(false)
                    mainBinding.ivSortByVc.setImageResource(R.drawable.ic_up_sort)
                    mainBinding.ivSortByVc.visibility = View.VISIBLE
                    mainBinding.ivSortByC.visibility = View.INVISIBLE
                    mainBinding.icSortByPoint.visibility = View.INVISIBLE
                }
            }
        }
        mainBinding.llSortBySp.setOnClickListener {
            if (mAdapter != null) {
                if (isCSorting) {
                    isCSorting = false
                    mAdapter!!.sortWithC(true)
                    mainBinding.ivSortBySp.setImageResource(R.drawable.ic_down_sort)
                } else {
                    isCSorting = true
                    mAdapter!!.sortWithC(false)
                    mainBinding.ivSortBySp.setImageResource(R.drawable.ic_up_sort)
                }
            }
        }
        mainBinding.ivTeamPreview.setOnClickListener {
            navigateTeamPreviewCricket()
            /*  when {
                  sportKey.equals(Constants.TAG_FOOTBALL, ignoreCase = true) -> {
                      navigateTeamPreviewFootball()
                  }

                  else -> {
                      navigateTeamPreviewCricket()
                  }
              }*/
        }


    }


    fun openPlayerInfoActivity(playerId: String?, playerName: String?, team: String?) {
        val intent = Intent(this, PlayerInfoActivity::class.java)
        intent.putExtra("matchKey", matchKey)
        intent.putExtra("playerId", playerId)
        intent.putExtra("playerName", playerName)
        intent.putExtra("team", team)
        startActivity(intent)
    }


    private fun navigateTeamPreviewCricket() {
        val intent = Intent(this@ChooseCandVCActivity, TeamPreviewActivity::class.java)
        intent.putExtra(Constants.KEY_MATCH_KEY, matchKey)
        intent.putExtra(Constants.KEY_TEAM_VS, teamVsName)
        intent.putExtra(Constants.KEY_TEAM_FIRST_URL, teamFirstUrl)
        intent.putExtra(Constants.KEY_TEAM_SECOND_URL, teamSecondUrl)
        intent.putExtra(Constants.KEY_FANTASY_TYPE, fantasyType)
        val selectedWkList = ArrayList<PlayerListResult>()
        val selectedBatLiSt = ArrayList<PlayerListResult>()
        val selectedArList = ArrayList<PlayerListResult>()
        val selectedBowlList = ArrayList<PlayerListResult>()
        for (player in list) {
            if (player.isSelected) {
                if (player.role == Constants.KEY_PLAYER_ROLE_KEEP) {
                    selectedWkList.add(player)
                }
                if (player.role == Constants.KEY_PLAYER_ROLE_BAT) {
                    selectedBatLiSt.add(player)
                }
                if (player.role == Constants.KEY_PLAYER_ROLE_ALL_R) {
                    selectedArList.add(player)
                }
                if (player.role == Constants.KEY_PLAYER_ROLE_BOL) {
                    selectedBowlList.add(player)
                }
            }
        }
        intent.putExtra(Constants.KEY_TEAM_LIST_WK, selectedWkList)
        intent.putExtra(Constants.KEY_TEAM_LIST_BAT, selectedBatLiSt)
        intent.putExtra(Constants.KEY_TEAM_LIST_AR, selectedArList)
        intent.putExtra(Constants.KEY_TEAM_LIST_BOWL, selectedBowlList)
        startActivity(intent)
    }

    // toolbar Click Event
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showTimer() {
        try {
            val countDownTimer: CountDownTimer =
                object : CountDownTimer(AppUtils.eventDateMileSecond(headerText), 1000) {
                    @SuppressLint("SetTextI18n")
                    override fun onTick(millisUntilFinished: Long) {
                        val seconds = millisUntilFinished / 1000 % 60
                        val minutes = millisUntilFinished / (1000 * 60) % 60
                     //   val diffHours = millisUntilFinished / (60 * 60 * 1000)
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
                        mainBinding.matchHeaderInfo.tvTimeTimer.text = "00h : 00m : 00s"
                    }
                }
            countDownTimer.start()
        } catch (e: Exception) {
        }

    }

    private fun createTeam() {
        val request = CreateTeamRequest()
        request.userid =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        request.matchkey = matchKey
        request.sport_key = sportKey
        request.fantasy_type = fantasyType
        val sb = StringBuilder()
        var captain = ""
        var vcCaptain = ""
        for (player in list) {
            if (!player.isHeader) sb.append(player.id.toString() + "").append(",")
            if (player.isCaptain) captain = player.id.toString() + ""
            if (player.isVcCaptain) vcCaptain = player.id.toString() + ""
        }

        Log.e("players sequence",sb.toString())
        request.players = sb.toString()
        request.captain = captain
        if (teamId != 0) request.teamid = teamId
        request.vicecaptain = vcCaptain
        createTeamViewModel.loadCreateTeamRequest(request)
        if (UpComingContestDetailActivity.listener != null) {
            listener = UpComingContestDetailActivity.listener!!
        }
        if (AllContestActivity.listener != null) {
            listener = AllContestActivity.listener!!
        }
        createTeamViewModel.createTeam().observe(this, { arrayListResource ->
            when (arrayListResource.status) {
                Resource.Status.LOADING -> {
                    mainBinding.refreshing = true
                }
                Resource.Status.ERROR -> {
                    mainBinding.refreshing = false
                    if (listener != null) listener!!.getTeamCreated(false)
                    if (arrayListResource.exception!!.response!!
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
                }
                Resource.Status.SUCCESS -> {
                    mainBinding.refreshing = false
                    Log.e("createTeam", "called")
                    if (arrayListResource.data!!.status == 1
                    ) {
                        if (UpComingContestDetailActivity.listener != null) {
                            listener = UpComingContestDetailActivity.listener!!
                            listener!!.getTeamCreated(true)
                        }
                        if (AllContestActivity.listener != null) {
                            listener = AllContestActivity.listener!!
                            listener!!.getTeamCreated(true)
                        }

                        Toast.makeText(
                            MyApplication.appContext,
                            arrayListResource.data.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        createdTeamId = arrayListResource.data.result.teamid
                        if (isForFirstTeamCreate) {
                            checkBalance()
                        } else {
                            val returnIntent = Intent()
                            returnIntent.putExtra("isTeamCreated", true)
                            setResult(RESULT_OK, returnIntent)
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            MyApplication.appContext,
                            arrayListResource.data.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        if (listener != null) listener!!.getTeamCreated(false)
                    }
                }
            }
        })
    }

    private fun checkBalance() {
        val request = JoinContestRequest()
        request.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        if (contestFirstTime != null) request.challengeid = contestFirstTime!!.id.toString() + ""
        teamViewModel.loadBalanceRequest(request)
        teamViewModel.getBalanceData().observe(this) { arrayListResource ->
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
                    if (arrayListResource.data!!.status == 1
                    ) {
                        totalCoinsAvailable = arrayListResource.data.result.usertotalbalance


                        createDialog()
                    } else {
                        Toast.makeText(
                            MyApplication.appContext,
                            arrayListResource.data.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun createDialog() {
        val view: View = LayoutInflater.from(this@ChooseCandVCActivity)
            .inflate(R.layout.joined_team_confirm_dialog, null)
        val builder = AlertDialog.Builder(this@ChooseCandVCActivity)
        builder.setView(view)
        val okBtn = view.findViewById<View>(R.id.ok_btn) as LinearLayout
        val currentBalTxt = view.findViewById<View>(R.id.currentBalTxt) as TextView
        val joinedBalTxt = view.findViewById<View>(R.id.joinedBaltxt) as TextView
        val remainBalTxt = view.findViewById<View>(R.id.remaingBaltxt) as TextView
        val cancelButton = view.findViewById<View>(R.id.cancel_button) as RelativeLayout
        val tPay = view.findViewById<View>(R.id.toPay) as TextView
        val switchTeamBtn = view.findViewById<View>(R.id.switch_team_Btn) as RelativeLayout
        currentBalTxt.text = "FC $totalCoinsAvailable"
        joinedBalTxt.text = "FC " + contestFirstTime!!.entryfee
        tPay.text= "FC " + contestFirstTime!!.entryfee
       /* val remainBal = usableB
        tPay.text = "FC " + (contestFirstTime!!.entryfee.toDouble() - remainBal)
        if (remainBal > 0) {
            val decimalFormat = DecimalFormat("#.##")
            joinedBalTxt.text = "FC " + decimalFormat.format(remainBal)
        } else {
            remainBalTxt.text = "FC 0.0"
        }*/
        val alertDialog = builder.create()
        alertDialog.show()
        okBtn.setOnClickListener { alertDialog.dismiss() }
        cancelButton.setOnClickListener { alertDialog.dismiss() }
        switchTeamBtn.setOnClickListener {
            alertDialog.dismiss()
            if (totalCoinsAvailable!="0")
            {
                joinChallenge()
            }else
            {
                startActivity(Intent(this@ChooseCandVCActivity, AddBalanceActivity::class.java))
            }
          /*  if (contestFirstTime!!.is_bonus == 1) {
                if (usableB + availableB < contestFirstTime!!.entryFee.toDouble()) {
                    startActivity(Intent(this@ChooseCandVCActivity, AddBalanceActivity::class.java))
                } else {
                    joinChallenge()
                }
            } else {
                if (availableB < contestFirstTime!!.entryFee.toDouble()) {
                    startActivity(Intent(this@ChooseCandVCActivity, AddBalanceActivity::class.java))
                } else {
                    joinChallenge()
                }
            }*/
        }
    }


    private fun joinChallenge() {
        val request = JoinContestRequest()
        request.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        request.challengeid = contestFirstTime!!.id.toString() + ""
        request.matchkey = matchKey
        request.teamid = createdTeamId.toString()
        request.sport_key = sportKey
        request.fantasy_type = fantasyType
        teamViewModel.loadJoinContestRequest(request)
        teamViewModel.joinContest().observe(this) { arrayListResource ->
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
                            .result.size > 0
                    ) {
                        val joinItems: ArrayList<JoinItem> =
                            arrayListResource.data.result
                        if (joinItems[0].status) {
                            Toast.makeText(
                                this@ChooseCandVCActivity,
                                "You have Successfully join this contest",
                                Toast.LENGTH_SHORT
                            ).show()
                            //finish();
                            val returnIntent = Intent()
                            returnIntent.putExtra("isTeamCreated", true)
                            setResult(RESULT_OK, returnIntent)
                            finish()
                        } else {
                            AppUtils.showError(
                                this@ChooseCandVCActivity,
                                joinItems[0].join_message
                            )
                        }
                    } else {
                        Toast.makeText(
                            MyApplication.appContext,
                            arrayListResource.data.message,
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
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
            CustomCallAdapter.CustomCallback<NormalResponse> {
            override fun success(response: Response<NormalResponse>) {
                mainBinding.refreshing = false
                val updateProfileResponse: NormalResponse = response.body()!!
                if (updateProfileResponse.status == 1) {
                    logout()
                } else {
                    AppUtils.showError(
                        this@ChooseCandVCActivity,
                        updateProfileResponse.message
                    )
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
            }
        })
    }

    // setup Recycler Data
    private fun setupRecyclerView() {
        mAdapter = ChooseCaptainVCPlayerItemAdapter(
            applicationContext,
            selectedCaptain,
            selectedVCaptain,
            list,
            this,
            sportKey
        )
        mainBinding.recyclerView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        mainBinding.recyclerView.layoutManager = mLayoutManager
        mainBinding.recyclerView.adapter = mAdapter

    }

    companion object {
        private var selectedCaptain = ""
        private var selectedVCaptain = ""
        fun setCaptainVcCaptionName(caption: String, VCaption: String) {
            selectedCaptain = caption
            selectedVCaptain = VCaption

        }

    }

    override fun onPlayerClick(isSelect: Boolean, position: Int, type: Int) {

    }

    override fun onShowcaseViewHide(showcaseView: ShowcaseView?) {

    }

    override fun onShowcaseViewDidHide(showcaseView: ShowcaseView?) {

    }

    override fun onShowcaseViewShow(showcaseView: ShowcaseView?) {

    }

    override fun onShowcaseViewTouchBlocked(motionEvent: MotionEvent?) {

    }
}