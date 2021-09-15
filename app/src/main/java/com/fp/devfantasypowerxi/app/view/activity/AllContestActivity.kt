package com.fp.devfantasypowerxi.app.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
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
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.request.ContestRequest
import com.fp.devfantasypowerxi.app.api.response.*
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.adapter.ContestItemAdapter
import com.fp.devfantasypowerxi.app.view.fragment.MyJoinedContestFragment
import com.fp.devfantasypowerxi.app.view.fragment.MyTeamFragment
import com.fp.devfantasypowerxi.app.view.fragment.UpComingContestFragment
import com.fp.devfantasypowerxi.app.view.listners.OnContestItemClickListener
import com.fp.devfantasypowerxi.app.view.listners.TeamCreatedListener
import com.fp.devfantasypowerxi.app.view.viewmodel.ContestViewModel
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.api.Resource
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivityAllContestBinding
import retrofit2.Response
import java.util.*
import javax.inject.Inject

// made by Gaurav Minocha
class AllContestActivity : AppCompatActivity(), OnContestItemClickListener, TeamCreatedListener {
    lateinit var mainBinding: ActivityAllContestBinding
    lateinit var mAdapter: ContestItemAdapter
    private lateinit var contestViewModel: ContestViewModel
    var matchKey: String = ""
    var teamVsName: String = ""
    var teamFirstUrl: String = ""
    var teamSecondUrl: String = ""
    var categoryId = 0
    var date: String = ""
    var userReferCode = ""
    var teamCount = 0
    var isEntry: Boolean = false
    var isWinner: Boolean = false
    var list: ArrayList<League> = ArrayList<League>()

    private val entryFee = ArrayList<String>()
    private val winning = ArrayList<String>()
    private val contestType = ArrayList<String>()
    private val contestSize = ArrayList<String>()
    var isTeamCreated = false
    var isForContestDetails = false
    var sportKey = ""

    var isForFirstTeamCreate = false
    var fantasyType = 0
    private lateinit var contestForFirstTime: League

    @Inject
    lateinit var oAuthRestService: OAuthRestService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_contest)
        mainBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_all_contest
        )
        MyApplication.getAppComponent()!!.inject(this@AllContestActivity)
        contestViewModel = ContestViewModel().create(this)
        MyApplication.getAppComponent()!!.inject(contestViewModel)
        initialize()
    }

    // initialize toolbar
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
            teamCount = intent.extras!!.getInt(Constants.KEY_TEAM_COUNT)
            date = intent.extras!!.getString(Constants.KEY_STATUS_HEADER_TEXT)!!
            isForContestDetails =
                intent.extras!!.getBoolean(Constants.KEY_STATUS_IS_FOR_CONTEST_DETAILS)
            categoryId = intent.getIntExtra(Constants.KEY_ALL_CONTEST, 0)
            sportKey = intent.extras!!.getString(Constants.SPORT_KEY)!!
            fantasyType = intent.extras!!.getInt(Constants.KEY_FANTASY_TYPE)
        }
        mainBinding.matchHeaderInfo.tvTeamVs.text = teamVsName
        mainBinding.matchHeaderInfo.ivTeamFirst.setImageURI(teamFirstUrl)
        mainBinding.matchHeaderInfo.ivTeamSecond.setImageURI(teamSecondUrl)
        val myTeamFragment = MyTeamFragment()
        val bundle = Bundle()
        bundle.putString(Constants.KEY_MATCH_KEY, matchKey)
        myTeamFragment.arguments = bundle

        val upComingContestFragment = UpComingContestFragment()

        upComingContestFragment.arguments = bundle

        val myJoinedContestFragment = MyJoinedContestFragment()
        myJoinedContestFragment.arguments = bundle

        mainBinding.swipeRefreshLayout.setOnRefreshListener {
            if (categoryId == 111) {
                getAllContest()
            } else {
                getAllContestByCategoryId(categoryId)
            }
        }
        if (categoryId == 111) {
            getAllContest()
        } else {
            getAllContestByCategoryId(categoryId)
        }
        try {
            val countDownTimer: CountDownTimer =
                object : CountDownTimer(AppUtils.eventDateMileSecond(date), 1000) {
                    @SuppressLint("SetTextI18n")
                    override fun onTick(millisUntilFinished: Long) {
                        val seconds = millisUntilFinished / 1000 % 60
                        val minutes = millisUntilFinished / (1000 * 60) % 60
                        val diffHours = millisUntilFinished / (60 * 60 * 1000)
                        mainBinding.matchHeaderInfo.tvTimeTimer.text =
                            twoDigitString(diffHours) + "h : " + twoDigitString(
                                minutes
                            ) + "m : " + twoDigitString(seconds) + "s "
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
        }
    }

    private fun getAllContest() {
        val request = ContestRequest()
        request.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        request.matchkey = matchKey
        request.sport_key = sportKey
        request.fantasy_type = fantasyType
        request.entryfee = TextUtils.join(",", entryFee)
        request.winning = TextUtils.join(",", winning)
        request.contest_type = TextUtils.join(",", contestType)
        request.contest_size = TextUtils.join(",", contestSize)
        contestViewModel.loadContestRequest(request)
        contestViewModel.getContestData().observe(this) { arrayListResource ->
            Log.d("Status ", "" + arrayListResource.status)
            when (arrayListResource.status) {
                Resource.Status.LOADING -> {
                    mainBinding.refreshing = true
                }
                Resource.Status.ERROR -> {
                    mainBinding.refreshing = false
                    mainBinding.swipeRefreshLayout.isRefreshing = false
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
                    mainBinding.swipeRefreshLayout.isRefreshing = false
                    if (arrayListResource.data!!.status == 1) {
                        list = arrayListResource.data.result!!.contest
                        setAnnouncementLayout(
                            arrayListResource.data.result.match_announcement
                        )
                        // mAdapter.updateData(list)
                        setupRecyclerView()
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

    private fun getAllContestByCategoryId(categoryId: Int) {
        mainBinding.refreshing = true
        val request = ContestRequest()
        request.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        request.matchkey = matchKey
        request.sport_key = sportKey
        request.fantasy_type = fantasyType
        request.category_id = categoryId.toString()
        request.entryfee = TextUtils.join(",", entryFee)
        request.winning = TextUtils.join(",", winning)
        request.contest_type = TextUtils.join(",", contestType)
        request.contest_size = TextUtils.join(",", contestSize)
        val myBalanceResponseCustomCall: CustomCallAdapter.CustomCall<ContestResponse> =
            oAuthRestService.getContestByCategoryCode(request)
        myBalanceResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<ContestResponse> {
            override fun success(response: Response<ContestResponse>) {
                mainBinding.refreshing = false
                mainBinding.swipeRefreshLayout.isRefreshing = false
                if (response.isSuccessful && response.body() != null) {
                    val categoryByContestResponse: ContestResponse = response.body()!!
                    if (categoryByContestResponse.status == 1 && categoryByContestResponse.result != null) {
                        if (categoryByContestResponse.result.contestArrayList
                                .size > 0
                        ) {
                            list = categoryByContestResponse.result.contestArrayList
                            setAnnouncementLayout(
                                categoryByContestResponse.result.match_announcement
                            )
                            setupRecyclerView()
                        }
                    }
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
                e!!.printStackTrace()
                if (e.response!!.code() in 400..403) {
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
                        this@AllContestActivity,
                        updateProfileResponse.message
                    )
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
            }
        })
    }

    // toolbar click Event
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onContestClick(contest: League?, isForDetail: Boolean) = if (isForDetail) {
        val intent =
            Intent(this@AllContestActivity, UpComingContestDetailActivity::class.java)
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
            val intent = Intent(this@AllContestActivity, MyTeamsActivity::class.java)
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

    private fun creteTeam(isFromFragment: Boolean) {
         val intent = if (sportKey.equals(Constants.TAG_FOOTBALL, ignoreCase = true)) {
              Intent(this@AllContestActivity, FootballCreateTeamActivity::class.java)
          }  else {
              Intent(this@AllContestActivity, CreateTeamActivity::class.java)
          }
    //    val intent = Intent(this@AllContestActivity, CreateTeamActivity::class.java)
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

    override fun getTeamCreated(team_created: Boolean) {
        isTeamCreated = team_created
    }

    fun setAnnouncementLayout(matchAmount: String) {
        if (!matchAmount.equals("", ignoreCase = true)) {
            mainBinding.rlAnnoucment.visibility = View.VISIBLE
            mainBinding.tvAnn.text = matchAmount
        } else {
            mainBinding.rlAnnoucment.visibility = View.GONE
        }
        val animationToLeft: Animation = TranslateAnimation(700F, -1000F, 0F, 0F)
        animationToLeft.duration = 17000
        animationToLeft.repeatMode = Animation.RESTART
        animationToLeft.repeatCount = Animation.INFINITE
        mainBinding.tvAnn.animation = animationToLeft
    }

    // setup Recycler Data
    private fun setupRecyclerView() {
        mAdapter = ContestItemAdapter(applicationContext, list, this, true)
        mainBinding.recyclerView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        mainBinding.recyclerView.layoutManager = mLayoutManager
        mainBinding.recyclerView.adapter = mAdapter
    }


    fun getWinnerPriceCard(contestId: Int, amount: String) {

        val contestRequest = ContestRequest()
        contestRequest.matchkey = matchKey
        contestRequest.challenge_id = contestId.toString() + ""
        val bankDetailResponseCustomCall: CustomCallAdapter.CustomCall<GetWinnerScoreCardResponse> =
            oAuthRestService.getWinnersPriceCard(contestRequest.matchkey,contestRequest.challenge_id,contestRequest.sport_key)
        bankDetailResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<GetWinnerScoreCardResponse> {
            override fun success(response: Response<GetWinnerScoreCardResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val getWinnerScoreCardResponse: GetWinnerScoreCardResponse? = response.body()
                    if (getWinnerScoreCardResponse!!.status == 1 && getWinnerScoreCardResponse.result
                            .size > 0
                    ) {
                        val priceList: ArrayList<GetWinnerScoreCardResult> =
                            getWinnerScoreCardResponse.result
                        if (priceList.size > 0) {
                            AppUtils.showWinningPopup(
                                this@AllContestActivity,
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


    companion object{
        var listener: TeamCreatedListener? = null
    }

}