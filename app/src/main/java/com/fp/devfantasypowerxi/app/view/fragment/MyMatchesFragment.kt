package com.fp.devfantasypowerxi.app.view.fragment

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.response.FantasyType
import com.fp.devfantasypowerxi.app.api.response.SportType
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.FragmentMyMatchesBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
// Created by Gaurav Minocha
class MyMatchesFragment : Fragment() {
    lateinit var mainBinding: FragmentMyMatchesBinding
    private lateinit var adapter: ViewPagerAdapter
    var fantasyType = 0
    var sportTypes = ArrayList<SportType>()
    var fantasyTypeList: ArrayList<FantasyType> = ArrayList<FantasyType>()
    var sprotList = ArrayList<SportType>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_my_matches, container, false)
        /* if(container!=null)
        container.removeAllViews();*/
        val root: View = mainBinding.tabLayout.getChildAt(0)
        if (root is LinearLayout) {
            (root as LinearLayout).showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
            val drawable = GradientDrawable()
            drawable.setColor(resources.getColor(R.color.selectedColor))
            drawable.setSize(2, root.getHeight())
            //  ((LinearLayout) root).setDividerPadding(10);
            (root as LinearLayout).dividerDrawable = drawable
        }
     //   setupViewPager(mainBinding.viewPager)
        return mainBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sportJson: String = MyApplication.preferenceDB!!.getString(Constants.SHARED_SPORTS_LIST)!!
        sportTypes = Gson().fromJson(
            sportJson,
            object : TypeToken<ArrayList<SportType?>?>() {}.type
        )
        fantasyTypeList = sportTypes[0].fantasy_type
    }
    // setup view pager
    private fun setupViewPager(viewPager: ViewPager) {
        adapter = ViewPagerAdapter(
            childFragmentManager
        )
        val bundle = Bundle()
        bundle.putString(Constants.SPORT_KEY, AppUtils.getSaveSportKey())
        val upcomingMatchFragment = UpcomingMatchFragment()
        upcomingMatchFragment.arguments = bundle
        val liveMatchFragment = LiveMatchFragment()
        liveMatchFragment.arguments = bundle
        val finishedMatchFragment = FinishedMatchFragment()
        finishedMatchFragment.arguments = bundle
        adapter.addFrag(upcomingMatchFragment, getString(R.string.upcoming))
        adapter.addFrag(liveMatchFragment, getString(R.string.live))
        adapter.addFrag(finishedMatchFragment, getString(R.string.finished))
        viewPager.adapter = adapter
        mainBinding.tabLayout.setupWithViewPager(viewPager)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupRecyclerView()
        sprotList = sportTypes
        if (AppUtils.getSaveSportKey()=="" || AppUtils.getSaveSportKey()==
                Constants.TAG_CRICKET
        ) {
         //   mainBinding.fantasyTypeBottomNavigation.visibility = View.VISIBLE
          //  setFantasyType(fantasyTypeList)
            //  if (!(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof HomeFragment)) {
            fantasyType = fantasyTypeList[0].type
            dataRefresh(fantasyType)
        } else {
            mainBinding.fantasyTypeBottomNavigation.visibility = View.GONE
        }


    }
    private fun setupRecyclerView() {
        setupViewPager(mainBinding.viewPager)
    }

    private fun dataRefresh(fantasyType: Int) {
        AppUtils.setFantasyType(fantasyType)
        val currentItem: Int = mainBinding.viewPager.currentItem
        when {
            adapter.getItem(currentItem) is UpcomingMatchFragment -> {
                (adapter.getItem(currentItem) as UpcomingMatchFragment)
            }
            adapter.getItem(currentItem) is LiveMatchFragment -> {
                (adapter.getItem(currentItem) as LiveMatchFragment)
            }
            adapter.getItem(currentItem) is FinishedMatchFragment -> {
                (adapter.getItem(currentItem) as FinishedMatchFragment)
            }
        }
    }
    class ViewPagerAdapter(manager: FragmentManager?) :
        FragmentPagerAdapter(manager!!) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFrag(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }
    }
}