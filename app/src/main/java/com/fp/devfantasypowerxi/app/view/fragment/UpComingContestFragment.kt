package com.fp.devfantasypowerxi.app.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.request.ContestRequest
import com.fp.devfantasypowerxi.app.api.response.CategoryByContestCategory
import com.fp.devfantasypowerxi.app.api.response.CategoryByContestResponse
import com.fp.devfantasypowerxi.app.api.response.NormalResponse
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.activity.HomeActivity
import com.fp.devfantasypowerxi.app.view.activity.UpComingContestActivity
import com.fp.devfantasypowerxi.app.view.adapter.CategoryContestItemAdapter
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.FragmentUpComingContestBinding
import retrofit2.Response
import java.util.*
import javax.inject.Inject

// Created by Gaurav Minocha
class UpComingContestFragment : Fragment() {
    lateinit var mainBinding: FragmentUpComingContestBinding
    lateinit var mAdapter: CategoryContestItemAdapter
    var matchKey: String = ""
    var list: ArrayList<CategoryByContestCategory> = ArrayList<CategoryByContestCategory>()

    @Inject
    lateinit var oAuthRestService: OAuthRestService
    var teamCount = 0
    var joinedContestCount = 0
    var totalContest = 0
    var sportKey: String = ""
    var fantasyType = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mainBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_up_coming_contest,
            container,
            false
        )
        mainBinding.joinContestByCode.setOnClickListener { (activity as UpComingContestActivity).joinByContestCode() }
        mainBinding.btnCreateTeam.setOnClickListener {
            (activity as UpComingContestActivity?)?.creteTeam(
                false
            )
        }
        mainBinding.btbCreatePrivateContest.setOnClickListener { (activity as UpComingContestActivity).openPrivateCreateContest() }
        mainBinding.tvAllContest.setOnClickListener {
            (activity as UpComingContestActivity?)?.openAllContestActivity(
                111
            )
        }

        mainBinding.swipeRefreshLayout.setOnRefreshListener {
            getContestByCategory()
            mainBinding.swipeRefreshLayout.isRefreshing = false
        }
        return mainBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyApplication.getAppComponent()!!.inject(this@UpComingContestFragment)
        if (arguments != null) {
            matchKey = requireArguments().getString(Constants.KEY_MATCH_KEY)!!
            sportKey = requireArguments().getString(Constants.SPORT_KEY)!!
            fantasyType = requireArguments().getInt(Constants.KEY_FANTASY_TYPE)
        }
        if (activity is UpComingContestActivity) {
            fantasyType = AppUtils.getFantasyType()
        }

    }

    override fun onResume() {
        super.onResume()
        getContestByCategory()
    }

   /* override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (isVisibleToUser) {
            Handler(Looper.getMainLooper()).postDelayed({

            }, 200)
        }
    }
*/
    private fun getContestByCategory() {
        mainBinding.refreshing = true
        val request = ContestRequest()
        request.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        request.matchkey = matchKey
        request.sport_key = AppUtils.getSaveSportKey()
        request.fantasy_type = AppUtils.getFantasyType()
        val myBalanceResponseCustomCall: CustomCallAdapter.CustomCall<CategoryByContestResponse> =
            oAuthRestService.getContestByCategory(request.matchkey,request.sport_key)
        myBalanceResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<CategoryByContestResponse> {
            @SuppressLint("SetTextI18n")
            override fun success(response: Response<CategoryByContestResponse>) {
                mainBinding.refreshing = false
                if (response.isSuccessful && response.body() != null) {
                    val categoryByContestResponse: CategoryByContestResponse = response.body()!!
                    if (categoryByContestResponse.status == 1 && categoryByContestResponse.result != null) {
                        list = categoryByContestResponse.result.categories
                        totalContest = categoryByContestResponse.result.total_contest
                        teamCount = categoryByContestResponse.result.user_teams
                        joinedContestCount =
                            categoryByContestResponse.result.joined_leagues
                        val matchAnnouncement: String =
                            categoryByContestResponse.result.match_announcement
                        mainBinding.tvAllContest.text = "View all $totalContest Contests"
                        if (activity != null && activity is UpComingContestActivity) (activity as UpComingContestActivity?)?.setTabTitle(
                            teamCount,
                            joinedContestCount
                        )
                        if (!matchAnnouncement.equals("", ignoreCase = true)) {
                            mainBinding.rlAnnouncement.visibility = View.VISIBLE
                            mainBinding.tvAnn.text = matchAnnouncement
                        } else {
                            mainBinding.rlAnnouncement.visibility = View.GONE
                        }
                        val animationToLeft: Animation = TranslateAnimation(700F, -1000F, 0F, 0F)
                        animationToLeft.duration = 17000
                        animationToLeft.repeatMode = Animation.RESTART
                        animationToLeft.repeatCount = Animation.INFINITE
                        mainBinding.tvAnn.animation = animationToLeft
                        setupRecyclerView()
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

    private fun setupRecyclerView() {
        mAdapter = CategoryContestItemAdapter(
            requireContext(),
            list,
            requireActivity() as UpComingContestActivity
        )
        mainBinding.recyclerView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        mainBinding.recyclerView.layoutManager = mLayoutManager
        mainBinding.recyclerView.adapter = mAdapter
    }

}