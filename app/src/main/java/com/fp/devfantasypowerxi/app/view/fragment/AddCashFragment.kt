package com.fp.devfantasypowerxi.app.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.view.activity.PersonalDetailsActivity
import com.fp.devfantasypowerxi.app.view.activity.VerifyAccountActivity
import com.fp.devfantasypowerxi.databinding.FragmentAddCashBinding
import java.util.*
    // made by Gaurav Minocha
class AddCashFragment : Fragment() {
    lateinit var mainBinding: FragmentAddCashBinding
    lateinit var mAdapter: TabAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_cash, container, false)

        mAdapter = TabAdapter(fragmentManager)
        mAdapter.addFragment(BalanceFragment(), "Balance")
        mAdapter.addFragment(PlayingHistoryFragment(), "Playing History")
        mAdapter.addFragment(TransactionsFragment(), "Transactions")
        mainBinding.viewPager.adapter = mAdapter
        mainBinding.tabLayout.setupWithViewPager(mainBinding.viewPager)
        mainBinding.btnEditProfile.setOnClickListener {
            startActivity(Intent(activity, PersonalDetailsActivity::class.java))
        }
        mainBinding.btnVerifyAccount.setOnClickListener {
            startActivity(Intent(activity, VerifyAccountActivity::class.java))
        }
        return mainBinding.root
    }
    // setup tabs
    class TabAdapter internal constructor(fm: FragmentManager?) :
        FragmentStatePagerAdapter(fm!!) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }
    }


}