package com.fp.devfantasypowerxi.app.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
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
import com.fp.devfantasypowerxi.databinding.ActivityFootballTeamPreviewPointBinding
import retrofit2.Response
import java.util.*
import javax.inject.Inject

class FootballTeamPreviewPointActivity : AppCompatActivity() {

    lateinit var mainBinding: ActivityFootballTeamPreviewPointBinding

    var listGk = ArrayList<PlayerListResult>()
    var listDif = ArrayList<PlayerListResult>()
    var listMid = ArrayList<PlayerListResult>()
    var listForward = ArrayList<PlayerListResult>()

    var teamId: String = ""
    var challengeId: String = ""
    var isForLeaderBoard = false
    var teamName = ""
    var tPoints = ""

    @Inject
    lateinit var oAuthRestService: OAuthRestService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_football_team_preview_point)
        MyApplication.getAppComponent()!!.inject(this@FootballTeamPreviewPointActivity)
        mainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_football_team_preview_point)
        initialize()
        val horizontalLayoutManagerR = LinearLayoutManager(this@FootballTeamPreviewPointActivity,
            LinearLayoutManager.HORIZONTAL,
            false)
        mainBinding.wickRecyclerView.layoutManager = horizontalLayoutManagerR

        val horizontalLayoutManager = LinearLayoutManager(this@FootballTeamPreviewPointActivity,
            LinearLayoutManager.HORIZONTAL,
            false)
        mainBinding.bolRecyclerView.layoutManager = horizontalLayoutManager

        val horizontalLayoutManager1 = LinearLayoutManager(this@FootballTeamPreviewPointActivity,
            LinearLayoutManager.HORIZONTAL,
            false)
        mainBinding.allRecyclerView.layoutManager = horizontalLayoutManager1

        val horizontalLayoutManager2 = LinearLayoutManager(this@FootballTeamPreviewPointActivity,
            LinearLayoutManager.HORIZONTAL,
            false)
        mainBinding.batRecyclerView.layoutManager = horizontalLayoutManager2

        mainBinding.icClose.setOnClickListener { finish() }

    }

    fun initialize() {

        if (intent != null && intent.extras != null) {
            teamId = intent.extras!!.getInt("teamId").toString() + ""
            challengeId = intent.extras!!.getInt("challengeId").toString() + ""
            isForLeaderBoard = intent.extras!!.getBoolean("isForLeaderBoard")
            teamName = intent.extras!!.getString(Constants.KEY_TEAM_NAME)!!
            tPoints = intent.extras!!.getString("tPoints")!!
        }

        mainBinding.icClose.setOnClickListener { finish() }
        mainBinding.teamName.text = teamName

        getPlayerInfo()
    }


    private fun getPlayerInfo() {
        mainBinding.refreshing = true
        val teamPreviewPointRequest = TeamPreviewPointRequest()
        teamPreviewPointRequest.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        teamPreviewPointRequest.challenge = challengeId
        teamPreviewPointRequest.teamid = teamId
        teamPreviewPointRequest.sport_key = Constants.TAG_FOOTBALL
        val bankDetailResponseCustomCall: CustomCallAdapter.CustomCall<TeamPointPreviewResponse> =
            oAuthRestService.getPreviewPoints(
                teamPreviewPointRequest.sport_key,
                teamPreviewPointRequest.joinid,
                teamPreviewPointRequest.teamid)
        bankDetailResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<TeamPointPreviewResponse> {
            @SuppressLint("ClickableViewAccessibility")
            override fun success(response: Response<TeamPointPreviewResponse>) {
                mainBinding.refreshing = false
                if (response.isSuccessful && response.body() != null) {
                    val teamPointPreviewResponse: TeamPointPreviewResponse =
                        response.body() ?: TeamPointPreviewResponse()
                    if (teamPointPreviewResponse.status == 1) {
                        listGk = teamPointPreviewResponse.result.Goalkeeper
                        listDif = teamPointPreviewResponse.result.Defender
                        listMid = teamPointPreviewResponse.result.Midfielder
                        listForward = teamPointPreviewResponse.result.Forward
                        mainBinding.wickRecyclerView.setOnTouchListener { _, _ -> true }
                        mainBinding.wickRecyclerView.adapter =
                            PreviewPlayerItemAdapter(isForLeaderBoard, listGk)
                        mainBinding.batRecyclerView.setOnTouchListener { _, _ -> true }
                        mainBinding.batRecyclerView.adapter =
                            PreviewPlayerItemAdapter(isForLeaderBoard, listDif)
                        mainBinding.allRecyclerView.setOnTouchListener { _, _ -> true }
                        mainBinding.allRecyclerView.adapter =
                            PreviewPlayerItemAdapter(isForLeaderBoard, listMid)
                        mainBinding.bolRecyclerView.setOnTouchListener { _, _ -> true }
                        mainBinding.team1Name.text= teamPointPreviewResponse.result.team1name
                        mainBinding.team2Name.text= teamPointPreviewResponse.result.team2name
                        mainBinding.team1Players.text= teamPointPreviewResponse.result.team1players.size.toString()
                        mainBinding.team2Players.text= teamPointPreviewResponse.result.team2players.size.toString()
                        mainBinding.totalCreditPoints.text= teamPointPreviewResponse.result.points.toString()
                        mainBinding.bolRecyclerView.adapter =
                            PreviewPlayerItemAdapter(isForLeaderBoard, listForward)
                    } else {
                        AppUtils.showError(this@FootballTeamPreviewPointActivity,
                            teamPointPreviewResponse.message)
                    }
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
                e!!.printStackTrace()
                if (e.response!= null) {
                    if (e.response.code() in 400..403) {
                        logout()
                    }
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
                        this@FootballTeamPreviewPointActivity,
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