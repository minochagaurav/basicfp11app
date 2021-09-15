package com.fp.devfantasypowerxi.app.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.JoinContestRequest
import com.fp.devfantasypowerxi.app.view.fragment.PlayingHistoryFragment
import com.fp.devfantasypowerxi.app.view.fragment.TransactionsFragment
import com.fp.devfantasypowerxi.app.view.viewmodel.TeamViewModel
import com.fp.devfantasypowerxi.common.api.Resource
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivityMyWalletBinding
import java.util.*

// made by Gaurav Minocha
class MyWalletActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityMyWalletBinding
    lateinit var mAdapter: TabAdapter
    lateinit var teamViewModel: TeamViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_wallet)

        mainBinding.tvUserName.text =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_NAME)
        teamViewModel = TeamViewModel().create(this)
        MyApplication.getAppComponent()!!.inject(teamViewModel)
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
       // mAdapter.addFragment(BalanceFragment(), "Balance")
        mAdapter.addFragment(PlayingHistoryFragment(), "Playing History")
        mAdapter.addFragment(TransactionsFragment(), "Transactions")
        mainBinding.viewPager.adapter = mAdapter
        mainBinding.tabLayout.setupWithViewPager(mainBinding.viewPager)
        checkBalance()
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

    private fun checkBalance() {
        val request = JoinContestRequest()
        request.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        teamViewModel.loadBalanceRequest(request)
        teamViewModel.getBalanceData().observe(this) { arrayListResource ->
            Log.d("Status ", "" + arrayListResource.status)
            when (arrayListResource.status) {
                Resource.Status.LOADING -> {
                    // mainBinding.refreshing = true
                }
                Resource.Status.ERROR -> {
                    //   mainBinding.refreshing = false
                    Toast.makeText(
                        MyApplication.appContext,
                        arrayListResource.exception!!.getErrorModel().errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Resource.Status.SUCCESS -> {
                    // mainBinding.refreshing = false
                    if (arrayListResource.data!!.status == 1
                    ) {
                        mainBinding.tvCoinsAvailable.text =
                            arrayListResource.data.result.usertotalbalance


                    } else {
                        Toast.makeText(
                            MyApplication.appContext,
                            arrayListResource.data.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
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