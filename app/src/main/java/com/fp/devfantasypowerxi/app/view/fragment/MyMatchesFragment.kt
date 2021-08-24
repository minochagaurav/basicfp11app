package com.fp.devfantasypowerxi.app.view.fragment

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.LiveData
import androidx.viewpager.widget.ViewPager
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.response.FantasyType
import com.fp.devfantasypowerxi.app.api.response.MatchListResponse
import com.fp.devfantasypowerxi.app.api.response.MatchListResult
import com.fp.devfantasypowerxi.app.api.response.SportType
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.viewmodel.MyMatchesUpComingMatchListViewModel
import com.fp.devfantasypowerxi.common.api.Resource
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.FragmentMyMatchesBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList

// Created by Gaurav Minocha
class MyMatchesFragment : Fragment() {
    lateinit var mainBinding: FragmentMyMatchesBinding
    private lateinit var adapter: ViewPagerAdapter
    var fantasyType = 0
    lateinit var upComingMatchListViewModel: MyMatchesUpComingMatchListViewModel
    var sportTypes = ArrayList<SportType>()
    var fantasyTypeList: ArrayList<FantasyType> = ArrayList<FantasyType>()
    var sprotList = ArrayList<SportType>()
    var matches: ArrayList<MatchListResult> = ArrayList()
    lateinit var response: MatchListResponse
    var tabPosition = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_my_matches, container, false)

        val root: View = mainBinding.tabLayout.getChildAt(0)
        if (root is LinearLayout) {
            root.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
            val drawable = GradientDrawable()
            drawable.setColor(resources.getColor(R.color.selectedColor))
            drawable.setSize(2, root.getHeight())
            root.dividerDrawable = drawable
        }
        upComingMatchListViewModel = MyMatchesUpComingMatchListViewModel().create(this)
        MyApplication.getAppComponent()!!.inject(upComingMatchListViewModel)
        getData(upComingMatchListViewModel.searchData)
        return mainBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sportJson: String =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_SPORTS_LIST)!!
        sportTypes = Gson().fromJson(
            sportJson,
            object : TypeToken<ArrayList<SportType>>() {}.type
        )
        fantasyTypeList = sportTypes[0].fantasy_type
    }

    private fun getData(liveData: LiveData<Resource<MatchListResponse>>) {
        val baseRequest = BaseRequest()
        baseRequest.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        baseRequest.sport_key = AppUtils.getSaveSportKey()
        baseRequest.fantasy_type = AppUtils.getFantasyType().toString()
        upComingMatchListViewModel.load(baseRequest)
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
                        response = arrayListResource.data ?: MatchListResponse()
                        matches = response.result
                        setupRecyclerView()
                        if (AppUtils.getSaveSportKey() == "" || AppUtils.getSaveSportKey() ==
                            Constants.TAG_CRICKET
                        ) {
                            mainBinding.topLayout.visibility = View.VISIBLE
                            fantasyType = fantasyTypeList[0].type
                            val currentItem: Int = mainBinding.viewPager.currentItem
                            tabPosition = currentItem
                        } else {
                            mainBinding.fantasyTypeBottomNavigation.visibility = View.GONE
                        }

                    }
                }
            })
    }

    // setup view pager
    private fun setupViewPager(viewPager: ViewPager) {
        adapter = ViewPagerAdapter(
            childFragmentManager, matches
        )
        adapter.addFrag(getString(R.string.upcoming))
        adapter.addFrag(getString(R.string.live))
        adapter.addFrag(getString(R.string.finished))
        viewPager.adapter = adapter
        mainBinding.tabLayout.setupWithViewPager(viewPager)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sprotList = sportTypes
    }

    private fun setupRecyclerView() {
        setupViewPager(mainBinding.viewPager)
    }

    class ViewPagerAdapter(manager: FragmentManager?, val matches: ArrayList<MatchListResult>) :
        FragmentPagerAdapter(manager!!) {
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return UpcomingMatchFragment(position, matches)
        }

        override fun getCount(): Int {
            return mFragmentTitleList.size
        }

        fun addFrag(title: String) {
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }
    }
}