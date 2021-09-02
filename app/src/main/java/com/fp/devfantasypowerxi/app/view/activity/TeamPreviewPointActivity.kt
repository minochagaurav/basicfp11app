package com.fp.devfantasypowerxi.app.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.request.TeamPreviewPointRequest
import com.fp.devfantasypowerxi.app.api.response.NormalResponse
import com.fp.devfantasypowerxi.app.api.response.PlayerListResult
import com.fp.devfantasypowerxi.app.api.response.TeamPointPreviewResponse
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.adapter.PreviewPlayerItemAdapter
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivityTeamPreviewPointBinding
import retrofit2.Response
import java.util.*
import javax.inject.Inject

class TeamPreviewPointActivity : AppCompatActivity() {

    lateinit var mainBinding: ActivityTeamPreviewPointBinding

    var listBat = ArrayList<PlayerListResult>()
    var listBowl = ArrayList<PlayerListResult>()
    var listAr = ArrayList<PlayerListResult>()
    var listWK = ArrayList<PlayerListResult>()

    var teamId: String = ""
    var challengeId: String = ""
    var isForLeaderBoard = false
    var teamName = ""
    var tPoints = ""
    var fantasyType = 0

    @Inject
    lateinit var oAuthRestService: OAuthRestService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //   setContentView(R.layout.activity_team_preview_point)

        MyApplication.getAppComponent()!!.inject(this@TeamPreviewPointActivity)
        mainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_team_preview_point)
        initialize()

        val horizontalLayoutManagaerr = LinearLayoutManager(
            this@TeamPreviewPointActivity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        mainBinding.wickRecyclerView.layoutManager = horizontalLayoutManagaerr

        val horizontalLayoutManagaer = LinearLayoutManager(
            this@TeamPreviewPointActivity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        mainBinding.bolRecyclerView.layoutManager = horizontalLayoutManagaer

        val horizontalLayoutManagaer1 = LinearLayoutManager(
            this@TeamPreviewPointActivity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        mainBinding.allRecyclerView.layoutManager = horizontalLayoutManagaer1

        val horizontalLayoutManagaer2 = LinearLayoutManager(
            this@TeamPreviewPointActivity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        mainBinding.batRecyclerView.layoutManager = horizontalLayoutManagaer2


      /*  if (fantasyType == 1) {
            mainBinding.ivFanTypePreview.setImageResource(R.drawable.ic_batting_prev)
        } else if (fantasyType == 2) {
            mainBinding.ivFanTypePreview.setImageResource(R.drawable.ic_bowling_prev)
        } else if (fantasyType == 3) {
            mainBinding.ivFanTypePreview.setImageResource(R.drawable.ic_premium_prev)
        } else {
            mainBinding.ivFanTypePreview.setImageResource(R.drawable.ic_classic_prev)
        }*/

        mainBinding.icClose.setOnClickListener { finish() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun initialize() {
        if (intent != null && intent.extras != null) {
            teamId = intent.extras!!.getInt("teamId").toString() + ""
            challengeId = intent.extras!!.getInt("challengeId").toString() + ""
            isForLeaderBoard = intent.extras!!.getBoolean("isForLeaderBoard")
            teamName = intent.extras!!.getString(Constants.KEY_TEAM_NAME)!!
            tPoints = intent.extras!!.getString("tPoints")!!
            fantasyType = intent.extras!!.getInt(Constants.KEY_FANTASY_TYPE)
        }
        mainBinding.icClose.setOnClickListener { view -> finish() }
        mainBinding.teamName.text = teamName
        // activityTeamPointPreviewBinding.totalPoints.setText("Total Points "+tPoints);
        getPlayerInfo()
    }

    private fun getPlayerInfo() {
        mainBinding.refreshing = true
        val teamPreviewPointRequest = TeamPreviewPointRequest()
        teamPreviewPointRequest.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        teamPreviewPointRequest.challenge = challengeId
        teamPreviewPointRequest.teamid = teamId
        teamPreviewPointRequest.sport_key = Constants.TAG_CRICKET
        val bankDetailResponseCustomCall: CustomCallAdapter.CustomCall<TeamPointPreviewResponse> =
            oAuthRestService.getPreviewPoints(
                teamPreviewPointRequest.challenge,
                teamPreviewPointRequest.sport_key,
                teamPreviewPointRequest.joinid,
                teamPreviewPointRequest.teamid
            )
        bankDetailResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<TeamPointPreviewResponse> {
            @SuppressLint("ClickableViewAccessibility")
            override fun success(response: Response<TeamPointPreviewResponse>) {
                mainBinding.refreshing = false
                if (response.isSuccessful && response.body() != null) {
                    val teamPointPreviewResponse: TeamPointPreviewResponse =
                        response.body() ?: TeamPointPreviewResponse()
                    if (teamPointPreviewResponse.status == 1) {
                        listWK = teamPointPreviewResponse.result.keeper
                        listBat = teamPointPreviewResponse.result.batsman
                        listBowl = teamPointPreviewResponse.result.bowler
                        listAr = teamPointPreviewResponse.result.allrounder
                        mainBinding.wickRecyclerView.setOnTouchListener { view, motionEvent -> true }
                        mainBinding.wickRecyclerView.adapter =
                            PreviewPlayerItemAdapter(isForLeaderBoard, listWK)
                        mainBinding.batRecyclerView.setOnTouchListener { view, motionEvent -> true }
                        mainBinding.batRecyclerView.adapter =
                            PreviewPlayerItemAdapter(isForLeaderBoard, listBat)
                        mainBinding.bolRecyclerView.setOnTouchListener { view, motionEvent -> true }
                        mainBinding.bolRecyclerView.adapter =
                            PreviewPlayerItemAdapter(isForLeaderBoard, listBowl)
                        mainBinding.allRecyclerView.setOnTouchListener { view, motionEvent -> true }
                        mainBinding.team1Name.text= teamPointPreviewResponse.result.team1name
                        mainBinding.team2Name.text= teamPointPreviewResponse.result.team2name
                        mainBinding.team1Players.text= teamPointPreviewResponse.result.team1players.size.toString()
                        mainBinding.team2Players.text= teamPointPreviewResponse.result.team2players.size.toString()
                        mainBinding.totalCreditPoints.text= teamPointPreviewResponse.result.points.toString()
                        mainBinding.allRecyclerView.adapter =
                            PreviewPlayerItemAdapter(isForLeaderBoard, listAr)
                    } else {
                        AppUtils.showError(
                            this@TeamPreviewPointActivity,
                            teamPointPreviewResponse.message
                        )
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
                        this@TeamPreviewPointActivity,
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