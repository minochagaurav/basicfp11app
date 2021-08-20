package com.fp.devfantasypowerxi.app.view.fragment

import android.content.Intent
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
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.response.FantasyType
import com.fp.devfantasypowerxi.app.api.response.MatchListResponse
import com.fp.devfantasypowerxi.app.api.response.MatchListResult
import com.fp.devfantasypowerxi.app.api.response.SportType
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.activity.LiveFinishedContestActivity
import com.fp.devfantasypowerxi.app.view.adapter.MyMatchItemAdapter
import com.fp.devfantasypowerxi.app.view.listners.OnMatchItemClickListener
import com.fp.devfantasypowerxi.app.view.viewmodel.MyMatchesFinishedMatchListViewModel
import com.fp.devfantasypowerxi.app.view.viewmodel.MyMatchesLiveMatchListViewModel
import com.fp.devfantasypowerxi.app.view.viewmodel.MyMatchesUpComingMatchListViewModel
import com.fp.devfantasypowerxi.common.api.Resource
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.FragmentCommonMatchesBinding
import java.util.ArrayList

// Made By Gaurav Minocha
class FinishedMatchFragment : Fragment(), OnMatchItemClickListener {
    lateinit var mainBinding: FragmentCommonMatchesBinding

    // String sportKey;
    lateinit var finishedMatchListViewModel: MyMatchesFinishedMatchListViewModel
    private lateinit var mAdapter: MyMatchItemAdapter
    var fantasyType = 0
    var sportTypes = ArrayList<SportType>()
    var fantasyTypeList: ArrayList<FantasyType> = ArrayList<FantasyType>()
    var sprotList = ArrayList<SportType>()
    var sportKey: String = ""
    private var list: ArrayList<MatchListResult> = ArrayList<MatchListResult>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_common_matches, container, false)
        setupRecyclerView()
        return mainBinding.root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        finishedMatchListViewModel = MyMatchesFinishedMatchListViewModel().create(this)
        MyApplication.getAppComponent()!!.inject(finishedMatchListViewModel)
        setupRecyclerView()

    }
    override fun setUserVisibleHint(isVisibleToUser: Boolean)
    {
        if (isVisibleToUser)
        {
            Log.e("finish","finish")
            Handler(Looper.getMainLooper()).postDelayed({
                getData(finishedMatchListViewModel.searchData)
            }, 200)
        }
        //   super.setUserVisibleHint(isVisibleToUser)
    }

    // setup recycle data
    private fun setupRecyclerView() {
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

    private fun getData(liveData: LiveData<Resource<MatchListResponse>>) {
        val baseRequest = BaseRequest()
        baseRequest.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        baseRequest.sport_key = AppUtils.getSaveSportKey()
        baseRequest.fantasy_type = AppUtils.getFantasyType().toString()
        finishedMatchListViewModel.load(baseRequest)
        liveData.observe(
            viewLifecycleOwner,
            { arrayListResource: Resource<MatchListResponse> ->
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
                            list = arrayListResource.data.result

                            if (list.size > 0) {
                                mAdapter.updateData(list)
                                mainBinding.rlNoMatch.visibility = View.GONE
                            } else {
                                mainBinding.rlNoMatch.visibility = View.VISIBLE
                            }
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

    override fun onMatchItemClick(
        matchKey: String,
        teamVsName: String,
        teamFirstUrl: String,
        teamSecondUrl: String,
        date: String?
    ) {
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


    fun refreshFragment() {

        getData(finishedMatchListViewModel.searchData)
    }

}