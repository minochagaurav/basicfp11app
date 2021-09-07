package com.fp.devfantasypowerxi.app.view.fragment

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.fragment.app.FragmentTransaction
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
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList

// Created by Gaurav Minocha
class MyMatchesFragment(val check: Int) : Fragment() {
    lateinit var mainBinding: FragmentMyMatchesBinding
    private lateinit var adapter: ViewPagerAdapter
    var fantasyType = 0
    lateinit var upComingMatchListViewModel: MyMatchesUpComingMatchListViewModel
    var sportTypes = ArrayList<SportType>()
    var fantasyTypeList: ArrayList<FantasyType> = ArrayList<FantasyType>()
    var sprotList = ArrayList<SportType>()
    var matches: ArrayList<MatchListResult> = ArrayList()
    lateinit var response: MatchListResponse
    var fragment: Fragment? = null
    var tabPosition = 0
    lateinit var fm: FragmentManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fm = childFragmentManager
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
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
        upComingMatchListViewModel = MyMatchesUpComingMatchListViewModel().create(this@MyMatchesFragment)
        MyApplication.getAppComponent()!!.inject(upComingMatchListViewModel)
        if (check == 0) {
            getData(upComingMatchListViewModel.searchData)
        }

        //    fragment = UpcomingMatchFragment(0, matches)

        if (AppUtils.getSaveSportKey() == Constants.TAG_FOOTBALL) {
            mainBinding.fantasySportTab.getTabAt(1)!!.select()
        } else {
            mainBinding.fantasySportTab.getTabAt(0)!!.select()
        }
        mainBinding.fantasySportTab.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // Fragment fragment = null;
                when (tab.position) {
                    0 -> {
                        MyApplication.preferenceDB!!.putString(Constants.SF_SPORT_KEY,
                            Constants.TAG_CRICKET)
                        if (upComingMatchListViewModel.searchData.hasActiveObservers()) {
                            upComingMatchListViewModel.searchData.removeObservers(this@MyMatchesFragment)
                        }
                        getData(upComingMatchListViewModel.searchData)
                        fragment = MyMatchesFragment(1)

                    }

                    1 -> {
                        MyApplication.preferenceDB!!.putString(Constants.SF_SPORT_KEY,
                            Constants.TAG_FOOTBALL)
                        if (upComingMatchListViewModel.searchData.hasActiveObservers()) {
                            upComingMatchListViewModel.searchData.removeObservers(this@MyMatchesFragment)
                        }
                        getData(upComingMatchListViewModel.searchData)
                        fragment = MyMatchesFragment(1)
                    }
                }


                val ft: FragmentTransaction = fm.beginTransaction()
                if (fragment is MyMatchesFragment) {
                    ft.replace(R.id.view_pager, fragment as MyMatchesFragment)
                    //   ft.detach(this@MyMatchesFragment)
                    // ft.attach(this@MyMatchesFragment)
                }

                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                ft.commit()
                fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
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
          mainBinding.lifecycleOwner = this@MyMatchesFragment
        val baseRequest = BaseRequest()
        baseRequest.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        baseRequest.sport_key = AppUtils.getSaveSportKey()
        baseRequest.fantasy_type = AppUtils.getFantasyType().toString()
        upComingMatchListViewModel.load(baseRequest)
        liveData.observe(
            mainBinding.lifecycleOwner!!,
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

                        Log.e("print ", response.toString())
                        matches = response.result
                        setupRecyclerView()
                        fantasyType = fantasyTypeList[0].type
                        val currentItem: Int = mainBinding.viewPager.currentItem
                        tabPosition = currentItem
                        mainBinding.fantasyTypeBottomNavigation.visibility = View.GONE

                    }
                }
            })
    }

    // setup view pager
    private fun setupViewPager(viewPager: ViewPager) {
        adapter = ViewPagerAdapter(
            fm, matches
        )
        adapter.addFrag(getString(R.string.upcoming))
        adapter.addFrag(getString(R.string.live))
        adapter.addFrag(getString(R.string.finished))
        viewPager.offscreenPageLimit = 3
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