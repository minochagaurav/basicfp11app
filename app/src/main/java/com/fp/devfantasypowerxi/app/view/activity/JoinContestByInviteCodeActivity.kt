package com.fp.devfantasypowerxi.app.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.request.JoinContestRequest
import com.fp.devfantasypowerxi.app.api.response.*
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.adapter.SpinnerAdapter
import com.fp.devfantasypowerxi.app.view.viewmodel.TeamViewModel
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.api.Resource
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivityJoinContestByInviteCodeBinding
import retrofit2.Response
import java.util.*
import javax.inject.Inject

// made by Gaurav Minocha
class JoinContestByInviteCodeActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityJoinContestByInviteCodeBinding

    @Inject
    lateinit var oAuthRestService: OAuthRestService
    lateinit var teamViewModel: TeamViewModel
    var challenge_id = 0
    var joinnigB: String = ""

    var matchKey: String? = null
    var sportKey = ""
    var fantasyType = 0
    var teamVsName: String = ""
    var teamFirstUrl: String = ""
    var teamSecondUrl: String = ""
    var teamList = ArrayList<TeamFindItem>()
    var headerText: String = ""
    var date: String = ""
    var totalCoinsAvailable: String = ""
    var isBonous = 0
    var contest = League()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_join_contest_by_invite_code
        )
        teamViewModel = TeamViewModel().create(this)
        MyApplication.getAppComponent()!!.inject(teamViewModel)
        MyApplication.getAppComponent()!!.inject(this@JoinContestByInviteCodeActivity)
        initialize()

    }

    // initialize toolbar
    fun initialize() {
        setSupportActionBar(mainBinding.myToolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = getString(R.string.invite_contest_code)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }


        if (intent != null && intent.extras != null) {
            matchKey = intent.extras!!.getString(Constants.KEY_MATCH_KEY)
            teamVsName = intent.extras!!.getString(Constants.KEY_TEAM_VS)!!
            teamFirstUrl = intent.extras!!.getString(Constants.KEY_TEAM_FIRST_URL)!!
            teamSecondUrl = intent.extras!!.getString(Constants.KEY_TEAM_SECOND_URL)!!
            headerText = intent.extras!!.getString(Constants.KEY_STATUS_HEADER_TEXT)!!
            date = intent.extras!!.getString(Constants.KEY_STATUS_HEADER_TEXT)!!
            sportKey = intent.extras!!.getString(Constants.SPORT_KEY)!!
            fantasyType = intent.extras!!.getInt(Constants.KEY_FANTASY_TYPE)
        }

        mainBinding.btnJoinContest.setOnClickListener(View.OnClickListener {
            if (mainBinding.promoCode.getText().toString().equals("")) AppUtils.showError(
                this@JoinContestByInviteCodeActivity,
                "Please enter promo code"
            ) else {
                joinContestByCode()
            }
        })
    }

    // toolbar click event
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    private fun joinContestByCode() {
        mainBinding.refreshing = true
        val userFullDetailsResponseCustomCall: CustomCallAdapter.CustomCall<JoinByContestCodeResponse> =
            oAuthRestService.joinByContestCode(mainBinding.promoCode.text.toString().trim())
        userFullDetailsResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<JoinByContestCodeResponse> {
            override fun success(response: Response<JoinByContestCodeResponse>) {
                mainBinding.refreshing = false
                val normalResponse: JoinByContestCodeResponse? = response.body()
                if (normalResponse != null) {
                    if (normalResponse.status == 1) {
                        if (normalResponse.result[0].message
                                .equals("Challenge opened")
                        ) {
                            challenge_id = normalResponse.result[0].challengeid
                            contest.id = challenge_id
                            joinnigB = java.lang.String.valueOf(
                                normalResponse.result.get(0).entryfee
                            )
                            findJoinTeam()
                        } else if (normalResponse.result[0].message
                                .equals("already used")
                        ) {
                            AppUtils.showError(
                                this@JoinContestByInviteCodeActivity,
                                "Invite code already used"
                            )
                        } else if (normalResponse.result[0].message
                                .equals("Challenge closed")
                        ) {
                            AppUtils.showError(
                                this@JoinContestByInviteCodeActivity,
                                "Sorry, this League is full! Please join another League."
                            )
                        } else if (normalResponse.result.get(0).message
                                .equals("invalid code")
                        ) {
                            AppUtils.showError(
                                this@JoinContestByInviteCodeActivity,
                                "Invalid code"
                            )
                        } else {
                            AppUtils.showError(
                                this@JoinContestByInviteCodeActivity,
                                normalResponse.result[0].message
                            )
                        }
                    } else {
                        Toast.makeText(
                            this@JoinContestByInviteCodeActivity,
                            normalResponse.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@JoinContestByInviteCodeActivity,
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

    private fun findJoinTeam() {
        mainBinding.refreshing = true
        val baseRequest = BaseRequest()
        baseRequest.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        baseRequest.matchkey = matchKey!!
        baseRequest.sport_key = sportKey
        baseRequest.fantasy_type = fantasyType.toString()
        baseRequest.challenge_id = challenge_id.toString()
        val userFullDetailsResponseCustomCall: CustomCallAdapter.CustomCall<FindJoinTeamResponse> =
            oAuthRestService.findJoinTeam(baseRequest)
        userFullDetailsResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<FindJoinTeamResponse> {
            override fun success(response: Response<FindJoinTeamResponse>) {
                mainBinding.setRefreshing(false)
                val findJoinTeamResponse: FindJoinTeamResponse? = response.body()
                if (findJoinTeamResponse != null) {
                    if (findJoinTeamResponse.status == 1) {
                        if (findJoinTeamResponse.result.size > 0) {
                            teamList = ArrayList<TeamFindItem>(findJoinTeamResponse.result)
                            checkBalance()
                        } else {
                            val intent = Intent(
                                this@JoinContestByInviteCodeActivity,
                                MyTeamsActivity::class.java
                            )
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
                    } else {
                        Toast.makeText(
                            this@JoinContestByInviteCodeActivity,
                            findJoinTeamResponse.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@JoinContestByInviteCodeActivity,
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

    private fun checkBalance() {
        val request = JoinContestRequest()
        request.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
       request.challengeid = challenge_id.toString() + ""
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

                        /*   val balanceItem: UsableBalanceItem =
                               arrayListResource.data.result[0]
                           availableB = balanceItem.getUsertotalbalance()
                           usableB = balanceItem.getUsablebalance()*/
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
        val view = LayoutInflater.from(this@JoinContestByInviteCodeActivity)
            .inflate(R.layout.joined_team_confirm_dialog, null)
        val builder = AlertDialog.Builder(this@JoinContestByInviteCodeActivity)
        builder.setView(view)
        val okBtn = view.findViewById<View>(R.id.ok_btn) as LinearLayout
        val currentBalTxt = view.findViewById<View>(R.id.currentBalTxt) as TextView
        val joinedBaltxt = view.findViewById<View>(R.id.joinedBaltxt) as TextView
        val tPay = view.findViewById<View>(R.id.toPay) as TextView
        val remaingBaltxt = view.findViewById<View>(R.id.remaingBaltxt) as TextView
        val cancelButton = view.findViewById<View>(R.id.cancel_button) as RelativeLayout
        val switchTeamBtn = view.findViewById<View>(R.id.switch_team_Btn) as RelativeLayout
        val llTeamLayout = view.findViewById<View>(R.id.ll_team_layout) as LinearLayout
        currentBalTxt.text = "FC $totalCoinsAvailable"
        joinedBaltxt.text = "FC $joinnigB"

        tPay.text = "FC " +joinnigB.toInt()

        val teamSpinner: Spinner = view.findViewById<View>(R.id.teamList) as Spinner
        val ar = arrayOfNulls<String>(teamList.size + 1)
        val selectedTeam = arrayOf("")



        ar[0] = "Select Team"
        for (i in teamList.indices) {
            ar[i + 1] = "Team " + teamList[i].teamnumber
        }

        teamSpinner.adapter = SpinnerAdapter(applicationContext, ar)
        teamSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                if (i != 0) selectedTeam[0] =
                    teamList[i - 1].teamid.toString() + "" else selectedTeam[0] = "0"
                (teamSpinner.selectedView as TextView).setTextColor(Color.BLACK)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        llTeamLayout.visibility = View.VISIBLE

        val remainBal = totalCoinsAvailable.toInt()-joinnigB.toInt() //- Double.parseDouble(joinnigB);


        if (remainBal > 0) {
            //  DecimalFormat decimalFormat = new DecimalFormat("#.##");
            //   remaingBaltxt.setText("₹ " + decimalFormat.format(remainBal));
            remaingBaltxt.text = "FC $remainBal"
        } else {
            remaingBaltxt.text = "FC 0.0"
        }

        val alertDialog = builder.create()
        alertDialog.show()

        okBtn.setOnClickListener { alertDialog.dismiss() }

        cancelButton.setOnClickListener { alertDialog.dismiss() }

        switchTeamBtn.setOnClickListener {
            alertDialog.dismiss()

            if (totalCoinsAvailable!="0")
            {
                joinContest(selectedTeam[0])
            }else
            {
                startActivity(Intent(this@JoinContestByInviteCodeActivity, AddBalanceActivity::class.java))
            }
            /*if (isBonous == 1) {
                if (usableB + availableB < joinnigB.toDouble()) {
                    startActivity(
                        Intent(
                            this@JoinContestByInviteCodeActivity,
                            AddBalanceActivity::class.java
                        )
                    )
                } else {
                    if (!selectedTeam[0].equals(
                            "0",
                            ignoreCase = true
                        )
                    ) joinContest(selectedTeam[0])
                }
            } else {
                if (availableB < joinnigB.toDouble()) {
                    startActivity(
                        Intent(
                            this@JoinContestByInviteCodeActivity,
                            AddBalanceActivity::class.java
                        )
                    )
                } else {
                    if (!selectedTeam[0].equals(
                            "0",
                            ignoreCase = true
                        )
                    ) joinContest(selectedTeam[0])
                }
            }*/
        }

    }

    private fun joinContest(teamId: String) {
        val request = JoinContestRequest()
        request.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        request.challengeid =challenge_id.toString()
        request.matchkey = matchKey!!
        request.teamid = teamId
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
                                this@JoinContestByInviteCodeActivity,
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
                                this@JoinContestByInviteCodeActivity,
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
/*

    private fun joinContest(teamId: String) {
        mainBinding.refreshing = true
        val request = JoinContestRequest()
        request.user_id=MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)
        request.setLeagueId(challenge_id.toString() + "")
        request.setSport_key(sportKey)
        request.setFantasyType(fantasyType)
        request.setMatchKey(matchKey)
        request.setTeamId(teamId)
        val userFullDetailsResponseCustomCall: CustomCallAdapter.CustomCall<JoinContestResponse> =
            oAuthRestService.joinContest(request)
        userFullDetailsResponseCustomCall.enqueue(object : CustomCallAdapter.CustomCallback<JoinContestResponse?>() {
            fun success(response: Response<JoinContestResponse?>) {
                mBinding.setRefreshing(false)
                val findJoinTeamResponse = response.body()
                if (findJoinTeamResponse != null) {
                    if (findJoinTeamResponse.getStatus() === 1) {
                        val joinItems: ArrayList<JoinItem> = findJoinTeamResponse.getResult()
                        if (joinItems[0].isStatus()) {
                            Toast.makeText(
                                this@JoinContestByInviteCodeActivity,
                                "You have Successfully join this contest",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            this@JoinContestByInviteCodeActivity,
                            findJoinTeamResponse.getMessage(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@JoinContestByInviteCodeActivity,
                        "Oops something went wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            fun failure(e: ApiException) {
                mainBinding.setRefreshing(false)
                if (e.getResponse().code() >= 400 && e.getResponse().code() < 404) {
                    logout()
                }
            }
        })
    }
*/


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
                        applicationContext as AppCompatActivity,
                        updateProfileResponse.message
                    )
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
            }
        })
    }
}