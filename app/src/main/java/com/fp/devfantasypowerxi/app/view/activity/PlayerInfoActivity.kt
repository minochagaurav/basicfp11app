package com.fp.devfantasypowerxi.app.view.activity

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.request.PlayerInfoRequest
import com.fp.devfantasypowerxi.app.api.response.NormalResponse
import com.fp.devfantasypowerxi.app.api.response.PlayerInfoMatchesItem
import com.fp.devfantasypowerxi.app.api.response.PlayerInfoResponse
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.adapter.PlayerInfoItemAdapter
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivityPlayerInfoBinding
import retrofit2.Response
import java.util.*
import javax.inject.Inject

class PlayerInfoActivity : AppCompatActivity() {
    lateinit var mAdapter: PlayerInfoItemAdapter
    var matchKey: String = ""
    var playerId: String = ""
    var playerName: String = ""
    var team: String = ""
    var image: String = ""
    var sportKey = ""
    lateinit var mainBinding: ActivityPlayerInfoBinding
    var list = ArrayList<PlayerInfoMatchesItem>()

    @Inject
    lateinit var oAuthRestService: OAuthRestService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //   setContentView(R.layout.activity_player_info)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_player_info)
        MyApplication.getAppComponent()!!.inject(this@PlayerInfoActivity)
        initialize()
    }

    fun initialize() {
        setSupportActionBar(mainBinding.topToolbar)
        if (intent != null && intent.extras != null) {
            matchKey = intent.extras!!.getString("matchKey")!!
            playerId = intent.extras!!.getString("playerId")!!
            playerName = intent.extras!!.getString("playerName")!!
            team = intent.extras!!.getString("team")!!
            image = intent.extras!!.getString("image")!!
            sportKey = intent.extras!!.getString(Constants.SPORT_KEY)!!
        }
        if (supportActionBar != null) {
            supportActionBar!!.title = ""
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
        if (sportKey.equals(Constants.TAG_CRICKET, ignoreCase = true)) {
            mainBinding.llCricketLayout.visibility = View.VISIBLE
        } else {
            mainBinding.llCricketLayout.visibility = View.GONE
        }
        mainBinding.ivPlayer.setImageURI(Uri.parse(image))

        setupRecyclerView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() {
        mAdapter = PlayerInfoItemAdapter(list)
        mainBinding.recyclerView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        mainBinding.recyclerView.layoutManager = mLayoutManager
        mainBinding.recyclerView.adapter = mAdapter
        getPlayerInfo()
    }


    private fun getPlayerInfo() {
        mainBinding.refreshing = true
        val playerInfoRequest = PlayerInfoRequest()
        playerInfoRequest.matchkey = matchKey
        playerInfoRequest.playerid = playerId
        playerInfoRequest.sport_key = sportKey
        val bankDetailResponseCustomCall: CustomCallAdapter.CustomCall<PlayerInfoResponse> =
            oAuthRestService.getPlayerInfo(playerInfoRequest.matchkey,
                playerInfoRequest.playerid,
                playerInfoRequest.sport_key)
        bankDetailResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<PlayerInfoResponse> {
            override fun success(response: Response<PlayerInfoResponse>) {
                mainBinding.refreshing = false
                if (response.isSuccessful && response.body() != null) {
                    val playerInfoResponse: PlayerInfoResponse =
                        response.body() ?: PlayerInfoResponse()
                    if (playerInfoResponse.status == 1) {
                        mAdapter.updateData(playerInfoResponse.result.matches)
                        mainBinding.playerInfoResult = playerInfoResponse.result
                    } else {
                        AppUtils.showError(this@PlayerInfoActivity,
                            playerInfoResponse.message)
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
                        this@PlayerInfoActivity,
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