package com.fp.devfantasypowerxi.app.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.JoinContestRequest
import com.fp.devfantasypowerxi.app.api.response.JoinedContestContest
import com.fp.devfantasypowerxi.app.api.response.League
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.activity.UpComingContestActivity
import com.fp.devfantasypowerxi.app.view.adapter.JoinedContestItemAdapter
import com.fp.devfantasypowerxi.app.view.listners.OnContestItemClickListener
import com.fp.devfantasypowerxi.app.view.viewmodel.ContestDetailsViewModel
import com.fp.devfantasypowerxi.common.api.Resource
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.FragmentMyJoinedContestBinding
import java.util.*
import javax.inject.Inject

// Created by Gaurav Minocha
class MyJoinedContestFragment : Fragment(), OnContestItemClickListener {
    lateinit var mainBinding: FragmentMyJoinedContestBinding
    lateinit var contestDetailsViewModel: ContestDetailsViewModel
    var matchKey: String? = null
    var list: ArrayList<JoinedContestContest> = ArrayList<JoinedContestContest>()

    // var list: ArrayList<JoinedContesttItem> = ArrayList<JoinedContesttItem>()
    var joinedContestCount = 0
    var fantasyType = 0
    var teamCount = 0
    var sportKey: String? = null

    @Inject
    lateinit var oAuthRestService: OAuthRestService
    lateinit var mAdapter: JoinedContestItemAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mainBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_my_joined_contest,
            container,
            false
        )
        //setupRecyclerView()


        mainBinding.swipeRefreshLayout.setOnRefreshListener {
            //    getData()
            mainBinding.swipeRefreshLayout.isRefreshing = false
        }
        return mainBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        contestDetailsViewModel = ContestDetailsViewModel().create(this@MyJoinedContestFragment)
        MyApplication.getAppComponent()!!.inject(contestDetailsViewModel)
        if (arguments != null) {
            matchKey = requireArguments().getString(Constants.KEY_MATCH_KEY)
            sportKey = requireArguments().getString(Constants.SPORT_KEY)
            fantasyType = requireArguments().getInt(Constants.KEY_FANTASY_TYPE)
        }
        if (activity is UpComingContestActivity) {
            fantasyType = AppUtils.getFantasyType()
        }

    }

    // setup Recycler data
    private fun setupRecyclerView() {
        mAdapter = JoinedContestItemAdapter(requireContext(), list, this)
        mainBinding.recyclerView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        mainBinding.recyclerView.layoutManager = mLayoutManager
        mainBinding.recyclerView.adapter = mAdapter
        //getData();
        //getData();
        mainBinding.noContestToJoin.setOnClickListener { (activity as UpComingContestActivity).movetoContest() }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (isVisibleToUser) {
            Handler(Looper.getMainLooper()).postDelayed({
                getData()
                setupRecyclerView()
            }, 200)
        }
    }

    private fun getData() {
        val request = JoinContestRequest()
        request.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        request.matchkey = matchKey!!
        request.sport_key = AppUtils.getSaveSportKey()
        request.fantasy_type = AppUtils.getFantasyType()
        contestDetailsViewModel.loadJoinedContestRequest(request)
        contestDetailsViewModel.getJoinedContestData().observe(this) { arrayListResource ->
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
                    if (arrayListResource.data!!.status == 1 && arrayListResource.data.result.contest.size > 0

                    ) {
                        mainBinding.rlNoTeam.visibility = View.GONE
                        mainBinding.rlMainLayout.visibility = View.VISIBLE
                        list = arrayListResource.data.result.contest
                        teamCount =
                            arrayListResource.data.result.user_teams
                        joinedContestCount =
                            arrayListResource.data.result.joined_leagues
                        if (arrayListResource.data.result.match_announcement != ""

                        ) {
                            mainBinding.llTopLayout.visibility = View.VISIBLE
                            mainBinding.tvAnn.text = arrayListResource.data.result.match_announcement
                        } else {
                            mainBinding.llTopLayout.visibility = View.GONE
                        }
                        val animationToLeft: Animation =
                            TranslateAnimation(700F, -400F, 0F, 0F)
                        animationToLeft.duration = 17000
                        animationToLeft.repeatMode = Animation.RESTART
                        animationToLeft.repeatCount = Animation.INFINITE
                        mainBinding.tvAnn.animation = animationToLeft
                        mAdapter.updateData(list)
                        if (activity != null && activity is UpComingContestActivity) (activity as UpComingContestActivity?)!!.setTabTitle(
                            teamCount,
                            joinedContestCount
                        )
                    } else {
                        mainBinding.rlNoTeam.visibility = View.VISIBLE
                        mainBinding.rlMainLayout.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onContestClick(contest: League?, isForDetail: Boolean) {
        (activity as UpComingContestActivity).openJoinedContestActivity(isForDetail, contest!!)
    }

}