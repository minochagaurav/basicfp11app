package com.fp.devfantasypowerxi.app.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.view.fragment.BalanceFragment
import com.fp.devfantasypowerxi.app.view.fragment.PlayingHistoryFragment
import com.fp.devfantasypowerxi.app.view.fragment.TransactionsFragment
import com.fp.devfantasypowerxi.databinding.ActivityMyWalletBinding
import java.util.*
// made by Gaurav Minocha
class MyWalletActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityMyWalletBinding
    lateinit var mAdapter: TabAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_wallet)
        initialize()
        // click event on buttons
        mainBinding.btnEditProfile.setOnClickListener {
            startActivity(Intent(this@MyWalletActivity, PersonalDetailsActivity::class.java))
        }
        mainBinding.btnVerifyAccount.setOnClickListener {
            startActivity(Intent(this@MyWalletActivity, VerifyAccountActivity::class.java))
        }
    }
    // initialize toolbar with tab data
    private fun initialize() {
        setSupportActionBar(mainBinding.myToolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = getString(R.string.wallets)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }

        mAdapter = TabAdapter(
            supportFragmentManager
        )
        mAdapter.addFragment(BalanceFragment(), "Balance")
        mAdapter.addFragment(PlayingHistoryFragment(), "Playing History")
        mAdapter.addFragment(TransactionsFragment(), "Transactions")
        mainBinding.viewPager.adapter = mAdapter
        mainBinding.tabLayout.setupWithViewPager(mainBinding.viewPager)
    }
        // setup tabular
    class TabAdapter(fm: FragmentManager?) :
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

    // toolbar click listener
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}