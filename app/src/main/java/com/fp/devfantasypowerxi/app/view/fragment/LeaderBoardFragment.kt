package com.fp.devfantasypowerxi.app.view.fragment

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
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.view.adapter.ContestJoinTeamItemAdapter
import com.fp.devfantasypowerxi.app.view.viewmodel.ContestDetailsViewModel
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.FragmentLeaderBoardBinding
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

    private var contestId: String = ""
    private var matchKey: String = ""
    private var pdfUrl: String = ""

    @Inject
    lateinit var oAuthRestService: OAuthRestService
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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

    /*
        companion object {
            @JvmStatic
            fun newInstance() =
                LeaderBoardFragment().apply {
                }
        }*/
    // setup recycler data
    private fun setupRecyclerView() {
        mAdapter =
            ContestJoinTeamItemAdapter(requireContext())
        mainBinding.recyclerView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        mainBinding.recyclerView.layoutManager = mLayoutManager
        mainBinding.recyclerView.adapter = mAdapter
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
            fantasyType: Int
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

   /* private fun getData(showLeaderBoard: Boolean) {
        val request = ContestRequest()
        request.user_id =MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        request.matchkey =matchKey
        request.showLeaderBoard = showLeaderBoard
        request.challenge_id =contestId
        request.sport_key=sportKey
        request.fantasy_type=fantasyType
        request.page="1"
        contestDetailsViewModel.loadContestRequest(request)
        contestDetailsViewModel.getContestData().observe(this) { arrayListResource ->
            Log.d("Status ", "" + arrayListResource.getStatus())
            when (arrayListResource.getStatus()) {
                LOADING -> {
                    fragmentLeaderBoardBinding.setRefreshing(true)
                }
                ERROR -> {
                    fragmentLeaderBoardBinding.setRefreshing(false)
                    Toast.makeText(
                        MyApplication.appContext,
                        arrayListResource.getException().getErrorModel().errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                SUCCESS -> {
                    fragmentLeaderBoardBinding.setRefreshing(false)
                    if (arrayListResource.getData().getStatus() === 1 && arrayListResource.getData()
                            .getResult().getValue().size() > 0
                    ) {
                        list = arrayListResource.getData().getResult().getValue()
                        mAdapter.updateData(list)
                        //setTeamContestCount();
                    } else {
                        // Toast.makeText(MyApplication.appContext,arrayListResource.getData().getMessage(),Toast.LENGTH_SHORT).show();
                        // setTeamContestCount();
                    }
                }
            }
        }
    }*/
}