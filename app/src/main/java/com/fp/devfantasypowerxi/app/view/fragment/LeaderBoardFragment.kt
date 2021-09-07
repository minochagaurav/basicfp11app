package com.fp.devfantasypowerxi.app.view.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.fp.devfantasypowerxi.app.api.request.ContestRequest
import com.fp.devfantasypowerxi.app.api.response.Contest
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.activity.UpComingContestDetailActivity
import com.fp.devfantasypowerxi.app.view.adapter.ContestJoinTeamItemAdapter
import com.fp.devfantasypowerxi.app.view.interfaces.JoinedUserCallBack
import com.fp.devfantasypowerxi.app.view.viewmodel.ContestDetailsViewModel
import com.fp.devfantasypowerxi.common.api.Resource
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.FragmentLeaderBoardBinding
import java.util.*
import javax.inject.Inject

// created by Gaurav Minocha
class LeaderBoardFragment : Fragment() {
    lateinit var mainBinding: FragmentLeaderBoardBinding
    lateinit var mAdapter: ContestJoinTeamItemAdapter
    var isForContestDetails = false
  lateinit var contestDetailsViewModel: ContestDetailsViewModel
    var isShowTimer = false
    var sportKey = ""
    var fantasyType = 0
    var list= ArrayList<Contest>()
    private var contestId: String = ""
    private var matchKey: String = ""
    private var pdfUrl: String = ""
    lateinit var   mCallBack: JoinedUserCallBack
    @Inject
    lateinit var oAuthRestService: OAuthRestService
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mainBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_leader_board,
            container,
            false
        )

        setupRecyclerView()
        return mainBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mCallBack = context as JoinedUserCallBack
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement FragmentToActivity")
        }
    }

    /*
        companion object {
            @JvmStatic
            fun newInstance() =
                LeaderBoardFragment().apply {
                }
        }*/
    // setup recycler data
    private fun setupRecyclerView() {
        sportKey= AppUtils.getSaveSportKey()
        mAdapter =
            ContestJoinTeamItemAdapter(requireContext(),isForContestDetails,list,sportKey,fantasyType)
        mainBinding.recyclerView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        mainBinding.recyclerView.layoutManager = mLayoutManager
        mainBinding.recyclerView.adapter = mAdapter


        if (!isShowTimer) {
            //mBinding.tvRank.setVisibility(View.VISIBLE);
            //mBinding.tvPoints.setVisibility(View.VISIBLE);
            mainBinding.downloadTeam.visibility = View.VISIBLE
        } else {
            //mBinding.tvRank.setVisibility(View.GONE);
            // mBinding.tvPoints.setVisibility(View.GONE);
            mainBinding.downloadTeam.setVisibility(View.GONE)
        }




        mainBinding.downloadTeam.setOnClickListener {
            if (pdfUrl != "") startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://fpapp.fantasypower11.com/$pdfUrl")
                )
            ) else {
                AppUtils.showError(activity as UpComingContestDetailActivity, "PDF Not Ready Yet")
            }
        }
        getData(true)
    }

    companion object {
        @JvmStatic
        fun newInstance(
            matchKey: String?,
            contestId: String?,
            isShowTimer: Boolean,
            isForContestDetails: Boolean,
            pdfUrl: String?,
            sportKey: String?,
            fantasyType: Int,
        ) =
            LeaderBoardFragment().apply {
                val myFragment = LeaderBoardFragment()
                val args = Bundle()
                args.putString(Constants.KEY_MATCH_KEY, matchKey)
                args.putString(Constants.CONTEST_ID, contestId)
                args.putBoolean("isShowTimer", isShowTimer)
                args.putBoolean("isForContestDetails", isForContestDetails)
                args.putString("pdfUrl", pdfUrl)
                args.putString("sportKey", sportKey)
                args.putInt("fantasyType", fantasyType)
                myFragment.arguments = args
                return myFragment
            }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contestDetailsViewModel = ContestDetailsViewModel().create(this)
        MyApplication.getAppComponent()!!.inject(contestDetailsViewModel)
        MyApplication.getAppComponent()!!.inject(this@LeaderBoardFragment)
        setHasOptionsMenu(true)
        if (arguments != null) {
            contestId = requireArguments().getString(Constants.CONTEST_ID)!!
            matchKey = requireArguments().getString(Constants.KEY_MATCH_KEY)!!
            isShowTimer = requireArguments().getBoolean("isShowTimer")
            isForContestDetails = requireArguments().getBoolean("isForContestDetails")
            pdfUrl = requireArguments().getString("pdfUrl")!!
            fantasyType = requireArguments().getInt("fantasyType")
            sportKey = requireArguments().getString("sportKey")!!
        }

    }

    private fun getData(showLeaderBoard: Boolean) {
        val request = ContestRequest()
        request.user_id =MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        request.matchkey =matchKey
        request.showLeaderBoard = showLeaderBoard
        request.challenge_id =contestId
        request.sport_key=sportKey
        request.fantasy_type=fantasyType
        request.page="1"
        contestDetailsViewModel.loadContestRequest(request)
        contestDetailsViewModel.getContestData().observe(viewLifecycleOwner) { arrayListResource ->
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
                            .result.contest.size > 0
                    ) {
                        list = arrayListResource.data.result.contest
                        mAdapter.updateData(list)
                        mCallBack.getJoinedUser(arrayListResource.data.result.joinedusers)
                        //setTeamContestCount();
                    }
                }
            }
        }
    }
    fun refreshLeaderBoard() {
        getData(true)
    }

}