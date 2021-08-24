package com.fp.devfantasypowerxi.app.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.MyTeamRequest
import com.fp.devfantasypowerxi.app.api.response.Team
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.activity.UpComingContestActivity
import com.fp.devfantasypowerxi.app.view.adapter.TeamItemAdapter
import com.fp.devfantasypowerxi.app.view.viewmodel.TeamViewModel
import com.fp.devfantasypowerxi.common.api.Resource
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.FragmentMyTeamBinding
import java.util.*
import javax.inject.Inject

// Created by Gaurav Minocha
class MyTeamFragment : Fragment() {
    lateinit var mainBinding: FragmentMyTeamBinding

    lateinit var teamViewModel: TeamViewModel
    var matchKey: String? = null
    var sportKey: String? = null
    var list: ArrayList<Team> = ArrayList<Team>()
    var teamCount = 0
    var isForJoinContest = false
    var joinedContestCount = 0
    var fantasyType = 0

    @Inject
    lateinit var oAuthRestService: OAuthRestService

    lateinit var mAdapter: TeamItemAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_team, container, false)
        mainBinding.swipeRefreshLayout.setOnRefreshListener {
            getData()
            mainBinding.swipeRefreshLayout.isRefreshing = false
        }
        return mainBinding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        teamViewModel = TeamViewModel().create(this)
        MyApplication.getAppComponent()!!.inject(teamViewModel)
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
        mAdapter = TeamItemAdapter(requireContext(), isForJoinContest, false, 0, 0, false, list)
        mainBinding.recyclerView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        mainBinding.recyclerView.layoutManager = mLayoutManager
        mainBinding.recyclerView.adapter = mAdapter
        mainBinding.btnCreateTeam.setOnClickListener { view ->
            (activity as UpComingContestActivity?)!!.creteTeam(
                true
            )
        }
        mainBinding.noTeamCreateButton.setOnClickListener { view ->
            (activity as UpComingContestActivity?)!!.creteTeam(
                true
            )
        }

    }

    override fun onResume() {
        super.onResume()
        mAdapter = TeamItemAdapter(requireContext(), isForJoinContest, false, 0, 0, false, list)
        getData()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (isVisibleToUser) {
            Handler(Looper.getMainLooper()).postDelayed({
                setupRecyclerView()
            }, 200)
        }
    }


    private fun getData() {
        val request = MyTeamRequest()
        request.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        request.matchkey = matchKey!!
        request.sport_key =/* AppUtils.getSaveSportKey()*/"cricket"
        request.challenge_id = "0"
        request.fantasy_type = AppUtils.getFantasyType()
        teamViewModel.loadMyTeamRequest(request)
        teamViewModel.getContestData().observe(viewLifecycleOwner, { arrayListResource ->
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
                            mainBinding.rlNoTeam.visibility = View.GONE
                            mainBinding.rlMainLayout.visibility = View.VISIBLE
                            list = arrayListResource.data.result.teams
                            teamCount = arrayListResource.data.result.user_teams
                            joinedContestCount =
                                arrayListResource.data.result.joined_leagues
                            mAdapter.updateData(list, 1)
                            if (activity != null && activity is UpComingContestActivity) (activity as UpComingContestActivity).setTabTitle(
                                teamCount,
                                joinedContestCount
                            )
                        } else {
                            mainBinding.rlNoTeam.setVisibility(View.VISIBLE)
                            mainBinding.rlMainLayout.setVisibility(View.GONE)
                        }
                        setTeamCreateButtonName()
                    } else {
                        Toast.makeText(
                            MyApplication.appContext,
                            arrayListResource.data.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setTeamCreateButtonName() {
        mainBinding.btnCreateTeam.text = "Create Team " + (teamCount + 1)
        if (teamCount >= Constants.TOTAL_CREATE_TEAM_COUNT) mainBinding.btnCreateTeam.visibility =
            View.GONE else mainBinding.btnCreateTeam.visibility = View.VISIBLE
    }

}