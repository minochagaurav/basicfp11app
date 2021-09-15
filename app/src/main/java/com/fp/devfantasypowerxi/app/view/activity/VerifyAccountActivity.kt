package com.fp.devfantasypowerxi.app.view.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.response.NormalResponse
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.fragment.BankVerificationFragment
import com.fp.devfantasypowerxi.app.view.fragment.MobileVerificationFragment
import com.fp.devfantasypowerxi.app.view.fragment.PanValidationFragment
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivityVerifyAccountBinding
import retrofit2.Response
import javax.inject.Inject

// mad by Gaurav Minocha
class VerifyAccountActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityVerifyAccountBinding

    @Inject
    lateinit var oAuthRestService: OAuthRestService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_verify_account)
        MyApplication.getAppComponent()!!.inject(this@VerifyAccountActivity)
        initialize()
        //  allVerify()
    }

    // initialize toolbar
    private fun initialize() {
        setSupportActionBar(mainBinding.myToolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = getString(R.string.verify)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }

        mainBinding.tabLayout.setupWithViewPager(mainBinding.vp)
        mainBinding.vp.adapter = SectionPagerAdapter(supportFragmentManager)
    }

    // toolbar click listeners
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /*   private fun allVerify() {
           mainBinding.refreshing = true
           val baseRequest = BaseRequest()
           baseRequest.user_id =
               MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
           val allVerifyResponseCustomCall: CustomCallAdapter.CustomCall<AllVerifyResponse> =
               oAuthRestService.allVerify(baseRequest)
           allVerifyResponseCustomCall.enqueue(object :
               CustomCallAdapter.CustomCallback<AllVerifyResponse> {
               override fun success(response: Response<AllVerifyResponse>) {
                   mainBinding.refreshing = false
                   if (response.isSuccessful && response.body() != null) {
                       val allVerifyResponse: AllVerifyResponse = response.body()!!
                       if (allVerifyResponse.status == 1) {
                           val allVerifyItem: AllVerifyResult = allVerifyResponse.result
                           if (allVerifyItem.bank_verify == 1) MyApplication.preferenceDB!!.putInt(
                               Constants.SHARED_PREFERENCE_USER_BANK_VERIFY_STATUS,
                               1
                           ) else if (allVerifyItem.bank_verify == 0) MyApplication.preferenceDB!!.putInt(
                               Constants.SHARED_PREFERENCE_USER_BANK_VERIFY_STATUS,
                               0
                           ) else if (allVerifyItem.bank_verify == -1) MyApplication.preferenceDB!!.putInt(
                               Constants.SHARED_PREFERENCE_USER_BANK_VERIFY_STATUS,
                               -1
                           ) else if (allVerifyItem.bank_verify == 2) {
                               MyApplication.preferenceDB!!.putInt(
                                   Constants.SHARED_PREFERENCE_USER_BANK_VERIFY_STATUS,
                                   2
                               )
                           }
                           if (allVerifyItem.pan_verify == 1) MyApplication.preferenceDB!!.putInt(
                               Constants.SHARED_PREFERENCE_USER_PAN_VERIFY_STATUS,
                               1
                           ) else if (allVerifyItem.pan_verify == 0) MyApplication.preferenceDB!!.putInt(
                               Constants.SHARED_PREFERENCE_USER_PAN_VERIFY_STATUS,
                               0
                           ) else if (allVerifyItem.pan_verify == -1) MyApplication.preferenceDB!!.putInt(
                               Constants.SHARED_PREFERENCE_USER_PAN_VERIFY_STATUS,
                               -1
                           ) else if (allVerifyItem.pan_verify == 2) {
                               MyApplication.preferenceDB!!.putInt(
                                   Constants.SHARED_PREFERENCE_USER_PAN_VERIFY_STATUS,
                                   2
                               )
                           }
                           if (allVerifyItem.email_verify == 1) MyApplication.preferenceDB!!.putInt(
                               Constants.SHARED_PREFERENCE_USER_EMAIL_VERIFY_STATUS,
                               1
                           ) else MyApplication.preferenceDB!!.putInt(
                               Constants.SHARED_PREFERENCE_USER_EMAIL_VERIFY_STATUS,
                               0
                           )
                           if (allVerifyItem.email_verify == 1) MyApplication.preferenceDB!!.putInt(
                               Constants.SHARED_PREFERENCE_USER_MOBILE_VERIFY_STATUS,
                               1
                           ) else MyApplication.preferenceDB!!.putInt(
                               Constants.SHARED_PREFERENCE_USER_MOBILE_VERIFY_STATUS,
                               0
                           )
                           mainBinding.tabLayout.setupWithViewPager(mainBinding.vp)
                           mainBinding.vp.setAdapter(SectionPagerAdapter(supportFragmentManager))
                       } else {
                           AppUtils.showError(
                               this@VerifyAccountActivity,
                               allVerifyResponse.message
                           )
                       }
                   }
               }

               override fun failure(e: ApiException?) {
                   mainBinding.setRefreshing(false)
                   e!!.printStackTrace()
                   if (e.response!!.code() in 400..403) {
                       logout()
                   }
               }
           })
       }
   */

    fun logout() {
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
                val updateProfileResponse: NormalResponse = response.body()!!
                if (updateProfileResponse.status == 1 && updateProfileResponse.status == 0) {
                    logout()
                } else {
                    AppUtils.showError(
                        this@VerifyAccountActivity,
                        updateProfileResponse.message
                    )
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
            }
        })
    }

    // setup for viewpager
    class SectionPagerAdapter(fm: FragmentManager?) :
        FragmentStatePagerAdapter(fm!!) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> MobileVerificationFragment()
                1 -> MobileVerificationFragment()
                else -> MobileVerificationFragment()
            }
        }

        override fun getCount(): Int {
            return 1/*if (MyApplication.preferenceDB!!.getInt(
                    Constants.SHARED_PREFERENCE_USER_MOBILE_VERIFY_STATUS,
                    0
                ) == 1 &&
                MyApplication.preferenceDB!!.getInt(
                    Constants.SHARED_PREFERENCE_USER_EMAIL_VERIFY_STATUS,
                    0
                ) == 1
                && (MyApplication.preferenceDB!!.getInt(
                    Constants.SHARED_PREFERENCE_USER_PAN_VERIFY_STATUS,
                    0
                ) == 1 || MyApplication.preferenceDB!!.getInt(
                    Constants.SHARED_PREFERENCE_USER_PAN_VERIFY_STATUS,
                    0
                ) == 0)
            ) {
                3
            } else if (MyApplication.preferenceDB!!.getInt(
                    Constants.SHARED_PREFERENCE_USER_MOBILE_VERIFY_STATUS,
                    0
                ) == 1 &&
                MyApplication.preferenceDB!!.getInt(
                    Constants.SHARED_PREFERENCE_USER_EMAIL_VERIFY_STATUS,
                    0
                ) == 1
            ) {
                2
            } else {
                1
            }*/
        }

        override fun getPageTitle(position: Int): CharSequence {
            return when (position) {
                0 -> "Email & Mobile"
                1 -> "PAN Card"
                else -> "Bank"
            }
        }
    }
}