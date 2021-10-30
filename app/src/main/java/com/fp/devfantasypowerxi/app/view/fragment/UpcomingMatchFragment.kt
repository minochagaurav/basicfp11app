package com.fp.devfantasypowerxi.app.view.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.response.MatchListResult
import com.fp.devfantasypowerxi.app.api.response.PlayerListResult
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.activity.LiveFinishedContestActivity
import com.fp.devfantasypowerxi.app.view.activity.UpComingContestActivity
import com.fp.devfantasypowerxi.app.view.adapter.MyMatchItemAdapter
import com.fp.devfantasypowerxi.app.view.listners.OnMatchItemClickListener
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.FragmentCommonMatchesBinding

// Create by Gaurav Minocha
class UpcomingMatchFragment :
    Fragment(), OnMatchItemClickListener {
    lateinit var mAdapter: MyMatchItemAdapter
    var position: Int = 0
    var matches: ArrayList<MatchListResult> = ArrayList()

    lateinit var mainBinding: FragmentCommonMatchesBinding
    var sportKey: String = ""
    private var list: ArrayList<MatchListResult> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        mainBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_common_matches, container, false)
        setupRecyclerView()

        return mainBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.e("onActivityCreated", "onActivityCreated")
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (isVisibleToUser) {
            if (arguments != null) {
                sportKey = requireArguments().getString(Constants.SPORT_KEY).toString()
                matches = requireArguments().getParcelableArrayList("matchList")!!
                position = requireArguments().getInt("position")
            }
            Log.e("upcoming", "upcoming")
            Handler(Looper.getMainLooper()).postDelayed({
                //  getData(upComingMatchListViewModel.searchData)
                dataUpdate()
            }, 200)
        }
        //   super.setUserVisibleHint(isVisibleToUser)
    }

    private fun setupRecyclerView() {
        val activity = activity
        if (activity != null) {
            mAdapter = MyMatchItemAdapter(
                requireActivity(),
                list,
                this,
                AppUtils.getSaveSportKey(),
                AppUtils.getFantasyType()
            )
            mainBinding.recyclerView.setHasFixedSize(true)
            val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
            mainBinding.recyclerView.layoutManager = mLayoutManager
            mainBinding.recyclerView.adapter = mAdapter
        }
    }

    private fun dataUpdate() {
        val matchesData: ArrayList<MatchListResult> = ArrayList()
        // list = matches
        when (position) {
            0 -> {
                for (match in matches) {
                    if (match.match_status_key == Constants.KEY_UPCOMING_MATCH) matchesData.add(
                        match)
                }
            }
            1 -> {
                for (match in matches) {
                    if (match.match_status_key == Constants.KEY_LIVE_MATCH) matchesData.add(match)
                }
            }
            else -> {
                for (match in matches) {
                    if (match.match_status_key == Constants.KEY_FINISHED_MATCH) matchesData.add(
                        match)
                }
            }
        }

        if (matchesData.size > 0) {
            mAdapter.updateData(matchesData)
            mainBinding.rlNoMatch.visibility = View.GONE
        } else {
            mainBinding.rlNoMatch.visibility = View.VISIBLE
        }

    }

    override fun onMatchItemClick(
        matchKey: String,
        teamVsName: String,
        teamFirstUrl: String,
        teamSecondUrl: String,
        date: String?,
    ) {

        if (position == 0) {
            Log.e("Upcoming", matchKey)
            val intent = Intent(activity, UpComingContestActivity::class.java)
            intent.putExtra(Constants.KEY_MATCH_KEY, matchKey)
            intent.putExtra(Constants.KEY_TEAM_VS, teamVsName)
            intent.putExtra(Constants.KEY_TEAM_FIRST_URL, teamFirstUrl)
            intent.putExtra(Constants.KEY_TEAM_SECOND_URL, teamSecondUrl)
            intent.putExtra(Constants.KEY_STATUS_HEADER_TEXT, date)
            intent.putExtra(Constants.SPORT_KEY, AppUtils.getSaveSportKey())
            intent.putExtra(Constants.KEY_FANTASY_TYPE, AppUtils.getFantasyType())
            startActivity(intent)
        } else if (position == 1) {
            val intent = Intent(activity, LiveFinishedContestActivity::class.java)
            intent.putExtra(Constants.KEY_MATCH_KEY, matchKey)
            intent.putExtra(Constants.KEY_TEAM_VS, teamVsName)
            intent.putExtra(Constants.KEY_TEAM_FIRST_URL, teamFirstUrl)
            intent.putExtra(Constants.KEY_TEAM_SECOND_URL, teamSecondUrl)
            intent.putExtra(Constants.KEY_STATUS_HEADER_TEXT, date)
            intent.putExtra(Constants.SPORT_KEY, AppUtils.getSaveSportKey())
            intent.putExtra(Constants.KEY_FANTASY_TYPE, AppUtils.getFantasyType())

            startActivity(intent)
        } else if (position == 2) {
            val intent = Intent(activity, LiveFinishedContestActivity::class.java)
            intent.putExtra(Constants.KEY_MATCH_KEY, matchKey)
            intent.putExtra(Constants.KEY_TEAM_VS, teamVsName)
            intent.putExtra(Constants.KEY_TEAM_FIRST_URL, teamFirstUrl)
            intent.putExtra(Constants.KEY_TEAM_SECOND_URL, teamSecondUrl)
            intent.putExtra(Constants.KEY_STATUS_HEADER_TEXT, date)
            intent.putExtra(Constants.SPORT_KEY, AppUtils.getSaveSportKey())
            intent.putExtra(Constants.KEY_FANTASY_TYPE, AppUtils.getFantasyType())

            startActivity(intent)
        }

    }

    /*    public String getSportKey() {
        String key= ((MyMatchesFragment) getParentFragment()).getSportKeyFromParent();
        return  key;
    }*/
    /*  public int getFantasyKey() {
        int fantKey = ((MyMatchesFragment) getParentFragment()).getFantasyKeyFormParent();
        return fantKey;
    }*/
    fun refreshFragment() {

        //  getData(upComingMatchListViewModel.searchData)
    }

    companion object {

        @JvmStatic
        fun newInstance(
            cPosition: Int, cMatches: ArrayList<MatchListResult>,
        ) =
            UpcomingMatchFragment().apply {
                // position = cPosition
                // matches = cMatches
                val args = Bundle()
                val myFragment = UpcomingMatchFragment()
                args.putInt("position", cPosition)
                args.putSerializable("matchList", cMatches)
                myFragment.arguments = args
                return myFragment
            }
    }
}