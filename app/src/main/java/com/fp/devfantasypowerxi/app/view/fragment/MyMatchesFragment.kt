package com.fp.devfantasypowerxi.app.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.databinding.FragmentMyMatchesBinding
import java.util.*
// Created by Gaurav Minocha
class MyMatchesFragment : Fragment() {
    lateinit var mainBinding: FragmentMyMatchesBinding
    private lateinit var adapter: ViewPagerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_my_matches, container, false)
        setupViewPager(mainBinding.viewPager)
        return mainBinding.root
    }
    // setup view pager
    private fun setupViewPager(viewPager: ViewPager) {
        adapter = ViewPagerAdapter(
            childFragmentManager
        )

        val upcomingMatchFragment = UpcomingMatchFragment()

        val liveMatchFragment = LiveMatchFragment()

        val finishedMatchFragment = FinishedMatchFragment()

        adapter.addFrag(upcomingMatchFragment, getString(R.string.upcoming))
        adapter.addFrag(liveMatchFragment, getString(R.string.live))
        adapter.addFrag(finishedMatchFragment, getString(R.string.finished))
        viewPager.adapter = adapter
        mainBinding.tabLayout.setupWithViewPager(viewPager)
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