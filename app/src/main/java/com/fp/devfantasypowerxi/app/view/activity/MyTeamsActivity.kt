package com.fp.devfantasypowerxi.app.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.request.JoinContestRequest
import com.fp.devfantasypowerxi.app.api.request.MyTeamRequest
import com.fp.devfantasypowerxi.app.api.request.SwitchTeamRequest
import com.fp.devfantasypowerxi.app.api.response.*
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.adapter.TeamItemAdapter
import com.fp.devfantasypowerxi.app.view.viewmodel.TeamViewModel
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.api.Resource
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivityMyTeamsBinding
import retrofit2.Response
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject

class MyTeamsActivity : AppCompatActivity() {

    var matchKey: String? = ""
    var teamVsName: String? = ""
    var teamFirstUrl: String? = ""
    var teamSecondUrl: String? = ""
    var mAdapter: TeamItemAdapter? = null
    var list = ArrayList<Team>()
    var teamCount = 0
    private var isForJoinContest = false
    var availableB = 0.0
    private var usableB = 0.0
    var contest: League? = null
    var headerText: String? = ""
    var isShowTimer = false
    var joinnigB: String? = ""
    private var verify_action: MenuItem? = null
    private var isSwitchTeam = false
    lateinit var mainBinding: ActivityMyTeamsBinding
    var isJoinID = ""
    var sportKey = ""
    var fantasyType = 0
    private var multiEntry = 0
    var maxEntry = 0
    lateinit var teamViewModel: TeamViewModel
    @Inject
    lateinit var oAuthRestService: OAuthRestService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_my_teams
        )
        teamViewModel = TeamViewModel().create(this@MyTeamsActivity)
        MyApplication.getAppComponent()!!.inject(teamViewModel)
        MyApplication.getAppComponent()!!.inject(this@MyTeamsActivity)
        initialize()
    }

    fun initialize() {
        setSupportActionBar(mainBinding.mytoolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = getString(R.string.my_teams)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
        if (intent != null && intent.extras != null) {
            matchKey = intent.extras!!.getString(Constants.KEY_MATCH_KEY)
            teamVsName = intent.extras!!.getString(Constants.KEY_TEAM_VS)
            teamFirstUrl = intent.extras!!.getString(Constants.KEY_TEAM_FIRST_URL)
            teamSecondUrl = intent.extras!!.getString(Constants.KEY_TEAM_SECOND_URL)
            isForJoinContest = intent.extras!!
                .getBoolean(Constants.KEY_IS_FOR_JOIN_CONTEST, false)
            headerText = intent.extras!!.getString(Constants.KEY_STATUS_HEADER_TEXT, "")
            isShowTimer = intent.extras!!
                .getBoolean(Constants.KEY_STATUS_IS_TIMER_HEADER, false)
            contest = intent.extras!!.getParcelable(Constants.KEY_CONTEST_DATA)!!
            isSwitchTeam = intent.extras!!.getBoolean(Constants.KEY_IS_FOR_SWITCH_TEAM, false)
            isJoinID = intent.extras!!.getString("isForJoinedId").toString()
            sportKey = intent.extras!!.getString(Constants.SPORT_KEY).toString()
            fantasyType = intent.extras!!.getInt(Constants.KEY_FANTASY_TYPE)
        }
        if (contest != null) {
            multiEntry = contest!!.multi_entry
            maxEntry = contest!!.max_multi_entry_user
        }
        setupRecyclerView()

        mainBinding.btnCreateTeamS.setOnClickListener {
            // val intent: Intent
            /*  intent = if (sportKey.equals(Constants.TAG_FOOTBALL, ignoreCase = true)) {
                  Intent(this@MyTeamsActivity, FootballCreateTeamActivity::class.java)
              } else if (sportKey.equals(Constants.TAG_BASKETBALL, ignoreCase = true)) {
                  Intent(this@MyTeamsActivity, BasketBallCreateTeamActivity::class.java)
              } else {
                  Intent(this@MyTeamsActivity, CreateTeamActivity::class.java)
              }*/
            val intent = Intent(this@MyTeamsActivity, CreateTeamActivity::class.java)
            intent.putExtra(Constants.KEY_MATCH_KEY, matchKey)
            intent.putExtra(Constants.KEY_TEAM_VS, teamVsName)
            intent.putExtra(Constants.KEY_TEAM_FIRST_URL, teamFirstUrl)
            intent.putExtra(Constants.KEY_TEAM_SECOND_URL, teamSecondUrl)
            intent.putExtra(Constants.SPORT_KEY, sportKey)
            intent.putExtra(Constants.KEY_FANTASY_TYPE, fantasyType)
            startActivity(intent)
        }
        mainBinding.btnJoinContest.setOnClickListener {
            if (mAdapter!!.selectedTeamCount() != 0) {
                if (isSwitchTeam) {
                    switchTeam()
                } else {
                    checkBalance()
                }
            } else {
                AppUtils.showError(
                    this@MyTeamsActivity,
                    "Please select at least one team to join contest"
                )
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        } /*else if (item.itemId == R.id.verify_action) {
        }*/
        return true
    }

    private fun switchTeam() {
        mainBinding.refreshing = true
        val teamRequest = SwitchTeamRequest()
        teamRequest.matchkey = matchKey!!
        teamRequest.challenge_id = contest!!.id.toString()
        teamRequest.userid =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        teamRequest.teamid = mAdapter!!.getMultipleTeamId()
        teamRequest.joinid = isJoinID
        teamRequest.sport_key = sportKey
        teamRequest.fantasy_type = fantasyType
        val userFullDetailsResponseCustomCall: CustomCallAdapter.CustomCall<SwitchTeamResponse> =
            oAuthRestService.switchTeam(teamRequest)
        userFullDetailsResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<SwitchTeamResponse> {
            override fun success(response: Response<SwitchTeamResponse>) {
                mainBinding.refreshing = false
                val findJoinTeamResponse: SwitchTeamResponse? = response.body()
                if (findJoinTeamResponse != null) {
                    if (findJoinTeamResponse.status == 1) {
                        Toast.makeText(
                            this@MyTeamsActivity,
                            findJoinTeamResponse.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@MyTeamsActivity,
                            findJoinTeamResponse.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@MyTeamsActivity,
                        "Oops something went wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
                if (e!!.response!!.code() in 400..403) {
                    logout()
                }
            }
        })
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
                        this@MyTeamsActivity,
                        updateProfileResponse.message
                    )
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
            }
        })
    }

    private fun checkBalance() {
        mainBinding.refreshing = true

        val userFullDetailsResponseCustomCall: CustomCallAdapter.CustomCall<BalanceResponse> =
            oAuthRestService.getUsableBalance()
        userFullDetailsResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<BalanceResponse> {
            override fun success(response: Response<BalanceResponse>) {
                mainBinding.refreshing = false
                val balanceResponse = response.body()
                if (balanceResponse != null) {
                    if (balanceResponse.status == 1
                    ) {
                        val balanceItem: BalanceResult = balanceResponse.result
                           availableB = balanceItem.usertotalbalance.toDouble()
                        //  usableB = balanceItem.getUsablebalance()
                        createDialog()
                        setTeamContestCount()
                    } else {
                        Toast.makeText(
                            MyApplication.appContext,
                            balanceResponse.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
            }
        })
    }

    /*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
         */
    /*   case R.id.cb_my:
                if(item.isChecked()){
                    // If item already checked then unchecked it
                    item.setChecked(false);
                    mAdapter.deSelectAll();
                }else{
                    // If item is unchecked then checked it
                    item.setChecked(true);
                    mAdapter.selectAll();

                }
                // Update the text view text style
                return true;*/
    /*
         */
    /*       if(item.isChecked()) {
                 item.setChecked(false);
                    mAdapter.deSelectAll();
                }
                else {
                    item.setChecked(true);
                    mAdapter.selectAll();
                }
                mAdapter.selectAll();
             //   item.getActionView()
                return true;*/
    /*
            default:
                return super.onOptionsItemSelected(item);
        }

    }*/
    private fun setupRecyclerView() {

        mAdapter = TeamItemAdapter(
            this@MyTeamsActivity, isForJoinContest, isSwitchTeam,
            multiEntry,
            maxEntry,
            true, list
        )
        mainBinding.recyclerView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        mainBinding.recyclerView.layoutManager = mLayoutManager
        mainBinding.recyclerView.adapter = mAdapter

    }


    @SuppressLint("SetTextI18n")
    fun createDialog() {
        val view = LayoutInflater.from(this@MyTeamsActivity)
            .inflate(R.layout.joined_team_confirm_dialog, null)
        val builder = AlertDialog.Builder(this@MyTeamsActivity)
        builder.setView(view)
        val okBtn = view.findViewById<View>(R.id.ok_btn) as LinearLayout
        val currentBalTxt = view.findViewById<View>(R.id.currentBalTxt) as TextView
        val joinedBaltxt = view.findViewById<View>(R.id.joinedBaltxt) as TextView
        val remaingBaltxt = view.findViewById<View>(R.id.remaingBaltxt) as TextView
        val cancelButton = view.findViewById<View>(R.id.cancel_button) as RelativeLayout
        val tPay = view.findViewById<View>(R.id.toPay) as TextView
        val switchTeamBtn = view.findViewById<View>(R.id.switch_team_Btn) as RelativeLayout
        currentBalTxt.text = "C $availableB"
        joinedBaltxt.text = "C " + contest!!.entryfee
        val remainBal = usableB
        val totalAmountShow: Double =
            mAdapter!!.selectedTeamCount() * contest!!.entryfee.toDouble()
        joinedBaltxt.text = "₹ $totalAmountShow"
        if (remainBal > 0) {
            val decimalFormat = DecimalFormat("#.##")
            remaingBaltxt.text = "-₹ " + decimalFormat.format(remainBal)
        } else {
            remaingBaltxt.text = "-₹ 0.0"
        }
        tPay.text = "₹ " + (totalAmountShow - remainBal)


        val alertDialog = builder.create()
        alertDialog.show()
        okBtn.setOnClickListener { alertDialog.dismiss() }
        cancelButton.setOnClickListener { alertDialog.dismiss() }
        switchTeamBtn.setOnClickListener {
            alertDialog.dismiss()

            if (availableB < contest!!.entryfee.toDouble())
            {
                startActivity(Intent(this@MyTeamsActivity, AddBalanceActivity::class.java))
            }else
            {
                if (isSwitchTeam) {
                    switchTeam()
                } else {
                    joinChallenge()
                }

            }
           /* if (contest!!.is_bonus == 1) {
                if (usableB + availableB < contest!!.entryfee.toDouble()) {

                } else {

                }
            } else {
                if (availableB < contest!!.entryfee.toDouble()) {
                    startActivity(Intent(this@MyTeamsActivity, AddBalanceActivity::class.java))
                } else {
                    if (isSwitchTeam) {
                        switchTeam()
                    } else {
                        joinChallenge()
                    }
                }
            }*/
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_sellect_all, menu)
        verify_action = menu.findItem(R.id.verify_action)
        val rootView = verify_action!!.actionView as LinearLayout
        checkBox = rootView.findViewById<View>(R.id.cb_select_all) as CheckBox
        checkBox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (!buttonView.isPressed) {
                return@OnCheckedChangeListener
            }
            if (isChecked) {
                mAdapter!!.selectAll()
            } else {
                mAdapter!!.deSelectAll()
            }
        })
        if (verify_action != null) {
            verify_action!!.isVisible = multiEntry == 1
        }
        return true
    }

    private fun joinChallenge() {
        val request = JoinContestRequest()
        request.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        request.challengeid = contest!!.id.toString()
        request.matchkey = contest!!.matchkey
        //request.setTeamId(mAdapter.getSelectedTeamId()+"");
        request.teamid = mAdapter!!.getMultipleTeamId()
        request.sport_key = sportKey
        request.fantasy_type = fantasyType
        teamViewModel.loadJoinContestRequest(request)
        teamViewModel.joinContest().observe(this, { arrayListResource ->
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
                        val joinItems: ArrayList<JoinItem> = arrayListResource.data.result
                        if (joinItems[0].status) {
                            Toast.makeText(
                                this@MyTeamsActivity,
                                "You have Successfully join this contest",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        } else {
                            AppUtils.showError(this@MyTeamsActivity, joinItems[0].join_message)
                        }
                    } else {
                        Toast.makeText(
                            MyApplication.appContext,
                            arrayListResource.data.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        getData()
        if (mAdapter != null) mAdapter!!.setTotalSelected(0)
    }

    @SuppressLint("SetTextI18n")
    private fun setTeamContestCount() {
        mainBinding.btnCreateTeamS.text = "Create Team " + (list.size + 1)
        if (teamCount >= Constants.TOTAL_CREATE_TEAM_COUNT) mainBinding.btnCreateTeamS.visibility =
            View.GONE else mainBinding.btnCreateTeamS.visibility = View.VISIBLE
        teamCount += 1
    }

    private fun getData() {
        val request = MyTeamRequest()
        request.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        request.matchkey = matchKey!!
        request.sport_key = "cricket"
        request.fantasy_type = fantasyType
        if (contest != null)
            request.challenge_id =
                contest!!.id.toString()
        else
            request.challenge_id = 0.toString()
        teamViewModel.loadMyTeamRequest(request)
        teamViewModel.getContestData().observe(this) { arrayListResource ->
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
                    if (arrayListResource.data!!.status == 1) {
                        if (arrayListResource.data.result.teams.size > 0) {
                            list = arrayListResource.data.result.teams
                            teamCount = arrayListResource.data.result.teams.size
                            maxEntry =
                                arrayListResource.data.result.max_multi_entry_user
                            mAdapter!!.updateData(list, maxEntry)
                        } else {
                            Toast.makeText(
                                MyApplication.appContext,
                                "You have not created any team yet",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        setTeamContestCount()
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


    fun editOrClone(list: ArrayList<PlayerListResult>, teamId: Int) {
        val intent = Intent(this@MyTeamsActivity, CreateTeamActivity::class.java)
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
        intent.putExtra(Constants.KEY_STATUS_HEADER_TEXT, headerText)
        intent.putExtra(Constants.KEY_STATUS_IS_TIMER_HEADER, isShowTimer)
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
        val intent = Intent(this@MyTeamsActivity, TeamPreviewActivity::class.java)
        intent.putExtra(Constants.KEY_MATCH_KEY, matchKey)
        intent.putExtra(Constants.KEY_TEAM_VS, teamVsName)
        intent.putExtra(Constants.KEY_TEAM_FIRST_URL, teamFirstUrl)
        intent.putExtra(Constants.KEY_TEAM_SECOND_URL, teamSecondUrl)
        intent.putExtra(Constants.KEY_TEAM_NAME, teamName)
        intent.putExtra(Constants.KEY_STATUS_HEADER_TEXT, headerText)
        intent.putExtra(Constants.KEY_STATUS_IS_TIMER_HEADER, isShowTimer)
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

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var checkBox: CheckBox
        fun setChecked(check: Boolean) {
            checkBox.isChecked = check
        }
    }
}