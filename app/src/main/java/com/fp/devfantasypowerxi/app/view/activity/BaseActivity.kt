package com.fp.devfantasypowerxi.app.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.facebook.drawee.view.SimpleDraweeView
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.response.BalanceResponse
import com.fp.devfantasypowerxi.app.api.response.BalanceResult
import com.fp.devfantasypowerxi.app.api.response.NormalResponse
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.utils.DisplayProgress
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.utils.Constants
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import retrofit2.Response

abstract class BaseActivity : AppCompatActivity(), DisplayProgress {
    lateinit var mDrawerToggle: ActionBarDrawerToggle
    private var layoutDrawer: DrawerLayout? = null
    private var tvUsername: TextView? = null
    private var tvTeamName: TextView? = null
    private var ivUserImage: SimpleDraweeView? = null
    private var llVerify: LinearLayout? = null
    private var llMyTransactions: LinearLayout? = null
    private var btnInvite: Button? = null
    private var llLeaderboard: LinearLayout? = null
    private var llMore: LinearLayout? = null
   // private var btnAddCash: Button? = null
    private var llProfile: LinearLayout? = null
    private var llLogOut: LinearLayout? = null
   // private var tvUnutilizedBalanceValue: TextView? = null
    //private var tvWinningsValue: TextView? = null
  //  private var tvBonusValue: TextView? = null
    private var tvTotalBalanceValue: TextView? = null
    private var tvMatchPlayedValue: TextView? = null
    private var tvContestPlayedValue: TextView? = null
    private var tvContestWinValue: TextView? = null
    private var tvTotalWinValue: TextView? = null
    open lateinit var oAuthRestService: OAuthRestService
    protected abstract fun setDrawer()
    protected fun setUpNavigationDrawer() {
        mDrawerToggle = object : ActionBarDrawerToggle(currentActivity(),
            layoutDrawer,
            R.string.drawer_open,
            R.string.drawer_closed) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                userBalance
                tvUsername!!.text =
                    MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_NAME)
                tvTeamName!!.text =
                    MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_TEAM_NAME)
                ivUserImage!!.setImageURI(MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_PIC))
            }
        }
        layoutDrawer!!.addDrawerListener(mDrawerToggle)
        mDrawerToggle.syncState()
        mDrawerToggle.setDrawerIndicatorEnabled(false)
        llVerify!!.setOnClickListener { v: View? ->
            closeDrawer()
            Handler(Looper.myLooper()!!)
                .postDelayed({
                    startActivity(Intent(currentActivity(),
                        VerifyAccountActivity::class.java))
                }, 300)
        }
       /* llWithdraw!!.setOnClickListener { v: View? ->
            closeDrawer()
            Handler(Looper.myLooper()!!)
                .postDelayed({
                    startActivity(Intent(currentActivity(),
                        WithdrawCashActivity::class.java))
                }, 300)
        }*/
        llMyTransactions!!.setOnClickListener { v: View? ->
            closeDrawer()
            Handler(Looper.myLooper()!!)
                .postDelayed({
                    startActivity(Intent(currentActivity(),
                        TransactionsActivity::class.java))
                },
                    300)
        }
        btnInvite!!.setOnClickListener { v: View? ->
            val shareBody =
                ("Play Free Fantasy Cricket and football on Fantasy Power 11 sign up and get 100 coins for free to start use . " +
                        "https://fp11games.in/fp11_practice_app/referral/"+MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_REFER_CODE))
             ("Here's (Fantasy Coins) FC 100 to play fantasy cricket with me On FantasyPower11 and earn FC 100 on share " +
                     MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_REFER_CODE) + " to download FantasyPower11 app & use My code"
                     + MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_REFER_CODE) + " To Register")
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(sharingIntent, "Share via"))
            closeDrawer()
        }
        llLeaderboard!!.setOnClickListener { v: View? ->
            closeDrawer()
            Handler(Looper.myLooper()!!)
                .postDelayed({ openLeaderboardActivity() },
                    300)
        }
        llMore!!.setOnClickListener { v: View? ->
            closeDrawer()
            Handler(Looper.myLooper()!!)
                .postDelayed({ startActivity(Intent(currentActivity(), MoreActivity::class.java)) },
                    300)
        }
       /* btnAddCash!!.setOnClickListener { v: View? ->
            closeDrawer()
            Handler(Looper.myLooper()!!)
                .postDelayed({
                    startActivity(Intent(currentActivity(),
                        AddBalanceActivity::class.java))
                },
                    300)
        }*/
        llProfile!!.setOnClickListener { v: View? ->
            closeDrawer()
            Handler(Looper.myLooper()!!)
                .postDelayed({
                    startActivity(Intent(currentActivity(),
                        PersonalDetailsActivity::class.java))
                },
                    300)
        }
        llLogOut!!.setOnClickListener { v: View? ->
            logout()
            closeDrawer()
        }
        /*  ivUserImage!!.setOnClickListener { v: View? ->
              closeDrawer()
              Handler(Looper.myLooper()!!)
                  .postDelayed({
                      if (imageSelectionUtils == null) {
                          imageSelectionUtils = ImageSelectionUtils(this,
                              oAuthRestService,
                              ivUserImage,
                              currentActivity)
                      }
                      imageSelectionUtils.startImageSelectionProcess()
                  },
                      300)
          }*/
    }
  /*  private fun generateContentLink(): Uri {


        val baseUrl = Uri.parse("https://fp11games.in/fp11_practice_app/referral/"+MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_REFER_CODE))
        val domain = "https://devfantasypowerxi.page.link"
        val sharelinktext = "https://devfantasypowerxi.page.link/?" +
                "&apn=" + packageName +
                "&st=" + "My Refer Link" +
                "&sd=" + "Reward Coins 20" +
                "&si=" + "https://fp11games.in/fp11_practice_app/referral/"+MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_REFER_CODE)

        val link = FirebaseDynamicLinks.getInstance()
            .createDynamicLink()
            .setLink(baseUrl)
            *//*.setLink(Uri.parse(sharelinktext))*//*
            .setDomainUriPrefix(domain)
            .setAndroidParameters(DynamicLink.AndroidParameters.Builder("com.fp.devfantasypowerxi").build())
            .buildDynamicLink()


            Log.e("create link", link.uri.toString())
        return link.uri
    }
    private fun onShareClicked() {
        var deepLink: Uri? = null
        deepLink =generateContentLink()
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, deepLink.toString())
        startActivity(Intent.createChooser(intent, "Share Link"))

    }*/
    protected fun findViews(
        layoutDrawer: DrawerLayout?,
        tvUsername: TextView?,
        tvTeamName: TextView?,
        ivUserImage: SimpleDraweeView?,
        llVerify: LinearLayout?,
        llMyTransactions: LinearLayout?,
        btnInvite: Button?,
        llLeaderboard: LinearLayout?,
        llMore: LinearLayout?,
     //   btnAddCash: Button?,
        llProfile: LinearLayout?,
        llLogOut: LinearLayout?,
      //  tvUnutilizedBalanceValue: TextView?,
       // tvWinningsValue: TextView?,
       // tvBonusValue: TextView?,
        tvTotalBalanceValue: TextView?,
        tvMatchPlayedValue: TextView?,
        tvContestPlayedValue: TextView?,
        tvContestWinValue: TextView?,
        tvTotalWinValue: TextView?,
        oAuthRestService: OAuthRestService,
    ) {
        this.layoutDrawer = layoutDrawer
        this.tvUsername = tvUsername
        this.tvTeamName = tvTeamName
        this.ivUserImage = ivUserImage
        this.llVerify = llVerify
        this.llMyTransactions = llMyTransactions
        this.btnInvite = btnInvite
        this.llLeaderboard = llLeaderboard
        this.llMore = llMore
      //  this.btnAddCash = btnAddCash
        this.llProfile = llProfile
        this.llLogOut = llLogOut
       // this.tvUnutilizedBalanceValue = tvUnutilizedBalanceValue
       // this.tvWinningsValue = tvWinningsValue
       // this.tvBonusValue = tvBonusValue
        this.tvTotalBalanceValue = tvTotalBalanceValue
        this.tvMatchPlayedValue = tvMatchPlayedValue
        this.tvContestPlayedValue = tvContestPlayedValue
        this.tvContestWinValue = tvContestWinValue
        this.tvTotalWinValue = tvTotalWinValue
        this.oAuthRestService = oAuthRestService
    }

    private fun openLeaderboardActivity() {
        //    startActivity(Intent(currentActivity, LeaderboardActivity::class.java))
    }

    private val userBalance: Unit
        private get() {
            showLoadingCircle()
            val baseRequest = BaseRequest()
            baseRequest.user_id =
                MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)
                    .toString()
            val myBalanceResponseCustomCall: CustomCallAdapter.CustomCall<BalanceResponse> =
                oAuthRestService.getUsableBalance()
            myBalanceResponseCustomCall.enqueue(object :
                CustomCallAdapter.CustomCallback<BalanceResponse> {
                override fun success(response: Response<BalanceResponse>) {
                    hideLoadingCircle()
                    if (response.isSuccessful && response.body() != null) {
                        val myBalanceResponse: BalanceResponse = response.body()!!
                        if (myBalanceResponse.status == 1
                        ) {
                            val myBalanceResultItem: BalanceResult =
                                myBalanceResponse.result
                            MyApplication.preferenceDB!!.putString(Constants.KEY_USER_BALANCE,
                                myBalanceResultItem.usertotalbalance + "")
                            MyApplication.preferenceDB!!.putString(Constants.KEY_USER_WINING_AMOUNT,
                                myBalanceResultItem.winning + "")
                            MyApplication.preferenceDB!!.putString(Constants.KEY_USER_BONUS_AMOUNT,
                                myBalanceResultItem.bonus + "")
                            MyApplication.preferenceDB!!.putString(Constants.KEY_USER_BONUS_AMOUNT,
                                myBalanceResultItem.usertotalbalance + "")
                            setUserBalance(myBalanceResultItem)

                            when {
                                areAllVerified() -> {
                                    llVerify!!.visibility = View.GONE
                                }
                                isMobileVerified -> {
                                    llVerify!!.visibility = View.VISIBLE
                        //                                llWithdraw!!.visibility = View.VISIBLE
                                }
                                else -> {
                                    llVerify!!.visibility = View.VISIBLE
                                }
                            }
                        } else {
                            AppUtils.showError(currentActivity(), myBalanceResponse.message)
                        }
                    }
                }

                override fun failure(e: ApiException?) {
                    hideLoadingCircle()
                    e!!.printStackTrace()
                    if (e.response != null && e.response.code() >= 400 && e.response
                            .code() < 404
                    ) {
                        logout()
                    }
                }
            })
        }

    @SuppressLint("SetTextI18n")
    private fun setUserBalance(myBalanceResultItem: BalanceResult) {
     //   tvUnutilizedBalanceValue!!.text = "FC " + myBalanceResultItem.balance
      //  tvWinningsValue!!.text = "FC " + myBalanceResultItem.winning
      //  tvBonusValue!!.text = "FC" + myBalanceResultItem.bonus
        tvTotalBalanceValue!!.text = "FC " + myBalanceResultItem.usertotalbalance
        tvMatchPlayedValue!!.text = myBalanceResultItem.total_match_play
        tvContestPlayedValue!!.text = myBalanceResultItem.total_league_play
        tvContestWinValue!!.text = myBalanceResultItem.total_contest_win
        tvTotalWinValue!!.text = "FC " + myBalanceResultItem.total_winning
    }

    private fun areAllVerified(): Boolean {
        return false
        /*  return MyApplication.tinyDB.getInt(Constants.SHARED_PREFERENCE_USER_EMAIL_VERIFY_STATUS,
              0) === 1 &&
                  MyApplication.tinyDB.getInt(Constants.SHARED_PREFERENCE_USER_MOBILE_VERIFY_STATUS,
                      0) === 1 && MyApplication.tinyDB.getInt(
              Constants.SHARED_PREFERENCE_USER_PAN_VERIFY_STATUS,
              0) === 1 && MyApplication.tinyDB.getInt(
              Constants.SHARED_PREFERENCE_USER_BANK_VERIFY_STATUS, 0) === 1*/
    }

    private val isMobileVerified: Boolean
        private get() = MyApplication.preferenceDB!!.getInt(Constants.SHARED_PREFERENCE_USER_MOBILE_VERIFY_STATUS,
            0) == 1

    private fun closeDrawer() {
        layoutDrawer!!.closeDrawer(GravityCompat.START, true)
    }

    fun logout() {
        showLoadingCircle()
        val baseRequest = BaseRequest()
        baseRequest.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        val userFullDetailsResponseCustomCall: CustomCallAdapter.CustomCall<NormalResponse> =
            oAuthRestService.logout(baseRequest)
        userFullDetailsResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<NormalResponse> {
            override fun success(response: Response<NormalResponse>) {
                hideLoadingCircle()
                val updateProfileResponse: NormalResponse = response.body()!!
                if (updateProfileResponse.status == 1) {
                    logout(currentActivity())
                } else {
                    AppUtils.showError(
                         currentActivity(),
                        updateProfileResponse.message
                    )
                }
            }

            override fun failure(e: ApiException?) {
                hideLoadingCircle()
            }
        })
    }

    protected abstract fun currentActivity(): AppCompatActivity
    private fun logout(context: Activity) {
        Toast.makeText(context, "You have logout successfully.", Toast.LENGTH_LONG).show()
        MyApplication.preferenceDB!!.clear()
        val i = Intent(context, LoginActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.finish()
        context.startActivity(i)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mDrawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mDrawerToggle.onConfigurationChanged(newConfig)
    }
}