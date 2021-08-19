package com.fp.devfantasypowerxi.app.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.response.*
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.activity.HomeActivity
import com.fp.devfantasypowerxi.app.view.adapter.SliderBannerAdapterNew
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.FragmentPlayingHistoryBinding
import retrofit2.Response
import java.util.*
import javax.inject.Inject

// Created by Gaurav Minocha
class PlayingHistoryFragment : Fragment() {
    var currentPage = 0
    lateinit var timer: Timer
    private lateinit var playingHistoryItem: PlayingHistoryResult
    val DELAY_MS: Long = 600
    val PERIOD_MS: Long = 5000
    var bannerListItems: ArrayList<Banner> = ArrayList<Banner>()

    @Inject
    lateinit var oAuthRestService: OAuthRestService
    lateinit var mainBinding: FragmentPlayingHistoryBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_playing_history, container, false)

        return mainBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyApplication.getAppComponent()!!.inject(this@PlayingHistoryFragment)
    }

    override fun onResume() {
        super.onResume()
        getPlayingHistory()
    }
    private fun getPlayingHistory() {
        mainBinding.refreshing = true
        val myBalanceResponseCustomCall: CustomCallAdapter.CustomCall<PlayingHistoryResponse> =
            oAuthRestService.getMyPlayingHistory()
        myBalanceResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<PlayingHistoryResponse> {
            override fun success(response: Response<PlayingHistoryResponse>) {
                mainBinding.refreshing = false
                if (response.isSuccessful && response.body() != null) {
                    val playingHistoryResponse: PlayingHistoryResponse = response.body()!!
                    if (playingHistoryResponse.status == 1 && playingHistoryResponse.result != null) {
                        playingHistoryItem = playingHistoryResponse.result
                        setPlayingHistory()
                        bannerListItems = playingHistoryItem.banner
                        if (activity != null) mainBinding.viewPagerBanner.adapter =
                            SliderBannerAdapterNew(
                                requireContext(), bannerListItems, true
                            )
                        autoScroll()
                    } else {
                        AppUtils.showError(
                            context as AppCompatActivity,
                            playingHistoryResponse.message
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

    @SuppressLint("SetTextI18n")
    private fun setPlayingHistory() {
        mainBinding.totalMatchTxt.text = playingHistoryItem.total_match_play.toString()
        mainBinding.totalContastTxt.text = playingHistoryItem.total_league_play.toString()
        mainBinding.totalContestWinTxt.text = playingHistoryItem.total_contest_win.toString()
        mainBinding.totalWinningsTxt.text = playingHistoryItem.total_winning
    }

    fun autoScroll() {
        val handler = Handler()
        val Update = Runnable {
            if (currentPage == bannerListItems.size) {
                currentPage = 0
            }
            mainBinding.viewPagerBanner.setCurrentItem(currentPage, true)
            currentPage += 1
        }
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(Update)
            }
        }, DELAY_MS, PERIOD_MS)
    }

    private fun logout() {
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
                val updateProfileResponse = response.body()!!
                if (updateProfileResponse.status == 1) {
                    MyApplication.logout(requireActivity())
                } else {
                    AppUtils.showError(
                        activity as HomeActivity,
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