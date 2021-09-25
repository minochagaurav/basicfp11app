package com.fp.devfantasypowerxi.app.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.response.Banner
import com.fp.devfantasypowerxi.app.api.response.MyBalanceResponse
import com.fp.devfantasypowerxi.app.api.response.MyBalanceResult
import com.fp.devfantasypowerxi.app.api.response.NormalResponse
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.activity.AddBalanceActivity
import com.fp.devfantasypowerxi.app.view.activity.HomeActivity
import com.fp.devfantasypowerxi.app.view.activity.WithdrawCashActivity
import com.fp.devfantasypowerxi.app.view.adapter.SliderBannerAdapterNew
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.FragmentBalanceBinding
import retrofit2.Response
import java.util.*
import javax.inject.Inject

// made by Gaurav Minocha
class BalanceFragment : Fragment() {
    lateinit var mainBinding: FragmentBalanceBinding
    private lateinit var myBalanceResultItem: MyBalanceResult
    var currentPage = 0
    lateinit var timer: Timer

    val DELAY_MS: Long = 600
    val PERIOD_MS: Long = 5000

    @Inject
    lateinit var oAuthRestService: OAuthRestService

    var bannerListItems: ArrayList<Banner> = ArrayList<Banner>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mainBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_balance, container, false)

        // buttons click listeners
        mainBinding.withdrawBtn.setOnClickListener {
            startActivity(Intent(activity, WithdrawCashActivity::class.java))
        }
        mainBinding.addCashBtn.setOnClickListener {
            startActivity(Intent(activity, AddBalanceActivity::class.java))
        }
        return mainBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        MyApplication.getAppComponent()!!.inject(this@BalanceFragment)
    }
    override fun onResume() {
        super.onResume()
        getUserBalance()
    }

    private fun getUserBalance() {
        mainBinding.refreshing = true
        val baseRequest = BaseRequest()
        baseRequest.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        val myBalanceResponseCustomCall: CustomCallAdapter.CustomCall<MyBalanceResponse> =
            oAuthRestService.getUserBalance(baseRequest)
        myBalanceResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<MyBalanceResponse> {
            override fun success(response: Response<MyBalanceResponse>) {
                mainBinding.refreshing = false
                if (response.isSuccessful && response.body() != null) {
                    val myBalanceResponse: MyBalanceResponse = response.body()!!
                    if (myBalanceResponse.status == 1 && myBalanceResponse.result.size > 0
                    ) {
                        myBalanceResultItem = myBalanceResponse.result[0]
                        MyApplication.preferenceDB!!.putString(
                            Constants.KEY_USER_BALANCE,
                            myBalanceResultItem.balance + ""
                        )
                        MyApplication.preferenceDB!!.putString(
                            Constants.KEY_USER_WINING_AMOUNT,
                            myBalanceResultItem.winning + ""
                        )
                        MyApplication.preferenceDB!!.putString(
                            Constants.KEY_USER_BONUS_AMOUNT,
                            myBalanceResultItem.bonus + ""
                        )
                        setUserBalance()
                        bannerListItems = myBalanceResultItem.banner
                        if (activity != null) mainBinding.viewPagerBanner.adapter =
                            SliderBannerAdapterNew(
                                requireContext(), bannerListItems, true
                            )
                        autoScroll()
                    } else {
                        AppUtils.showError(
                            activity as AppCompatActivity,
                            myBalanceResponse.message
                        )
                    }
                }
            }

            fun autoScroll() {
                val handler = Handler()
                val update = Runnable {
                    if (currentPage == bannerListItems.size) {
                        currentPage = 0
                    }
                    mainBinding.viewPagerBanner.setCurrentItem(currentPage, true)
                    currentPage += 1
                }
                timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        handler.post(update)
                    }
                }, DELAY_MS, PERIOD_MS)
            }

            @SuppressLint("SetTextI18n")
            private fun setUserBalance() {
                mainBinding.unutilizedTxt.text = "FC " + myBalanceResultItem.balance
                mainBinding.winningsTxt.text = "FC " + myBalanceResultItem.winning
                mainBinding.cashBonusTxt.text = "FC " + myBalanceResultItem.bonus
                mainBinding.totalBalanceTxt.text = "FC " + myBalanceResultItem.total
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
                e!!.printStackTrace()
                if (e.response!= null) {
                    if (e.response.code() in 400..403) {
                        logout()
                    }
                }
            }
        })
    }

    private fun logout() {
        mainBinding.refreshing = true
        val baseRequest = BaseRequest()
        baseRequest.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        val userFullDetailsResponseCustomCall: CustomCallAdapter.CustomCall<NormalResponse> =
            oAuthRestService.logout(baseRequest)
        userFullDetailsResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<NormalResponse> {
            override fun success(response: Response<NormalResponse>) {
                mainBinding.refreshing = false
                val updateProfileResponse = response.body()!!
                if (updateProfileResponse.status == 1) {
                    MyApplication.logout(requireActivity())
                } else {
                    AppUtils.showError(
                        activity as HomeActivity,
                        updateProfileResponse.message
                    )
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
            }
        })
    }
}