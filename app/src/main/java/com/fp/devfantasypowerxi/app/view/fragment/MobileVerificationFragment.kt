package com.fp.devfantasypowerxi.app.view.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.response.LoginSendOtpResponse
import com.fp.devfantasypowerxi.app.api.response.NormalResponse
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.VerifyOtpBtmSheet
import com.fp.devfantasypowerxi.app.view.activity.HomeActivity
import com.fp.devfantasypowerxi.app.view.interfaces.BottomSheetCallback
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.FragmentMobileVarificationBinding
import retrofit2.Response
import javax.inject.Inject

// Create by Gaurav Minocha
class MobileVerificationFragment : Fragment() {
    val DIALOG_FRAGMENT = 1

    private var isChangedMobile = false
    private var isChangedEmail = false

    var mStackLevel = 0

    @Inject
    lateinit var oAuthRestService: OAuthRestService

    lateinit var callback: BottomSheetCallback
    lateinit var mainBinding: FragmentMobileVarificationBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_mobile_varification,
                container,
                false
            )
        showVerifiedNotVerifiedLayout()

        mainBinding.btnSubmit.setOnClickListener {
            if (isChangedEmail) {
                val email: String = mainBinding.etChangeEmail.getText().toString().trim()
                if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email)
                        .matches()
                ) Toast.makeText(context, "Please enter valid email address", Toast.LENGTH_SHORT)
                    .show() else {
                    emailUpdate(email)
                }
            } else {
                verifyEmailByOtp(MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_EMAIL))
            }
        }

        mainBinding.ivEmailChange.setOnClickListener {
            mainBinding.llEmailChange.visibility = View.VISIBLE
            isChangedEmail = true
        }
        mainBinding.btnSendOtp.setOnClickListener {
            if (mainBinding.etMobile.text.toString().trim().length != 10) AppUtils.showError(
                context as AppCompatActivity,
                "Please enter valid mobile number"
            ) else {
                if (isChangedMobile) {
                    updateMobile(mainBinding.etMobile.text.toString().trim())
                } else {
                    verifyByMobile(mainBinding.etMobile.text.toString().trim())
                }
            }
        }

        mainBinding.ivMobileChange.setOnClickListener {
            isChangedMobile = true
            mainBinding.mobileVerifiedLayout.visibility = View.GONE
            mainBinding.mobileNotVerifiedLayout.visibility = View.VISIBLE
        }
        return mainBinding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("level", mStackLevel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        MyApplication.getAppComponent()!!.inject(this@MobileVerificationFragment)

        if (savedInstanceState != null) {
            mStackLevel = savedInstanceState.getInt("level")
        }
        super.onCreate(savedInstanceState)
    }


    private fun showVerifiedNotVerifiedLayout() {
        if (MyApplication.preferenceDB!!.getInt(
                Constants.SHARED_PREFERENCE_USER_EMAIL_VERIFY_STATUS,
                0
            ) == 1
        ) {
            mainBinding.emailVerifiedLayout.visibility = View.VISIBLE
            mainBinding.emailNotVerifiedLayout.visibility = View.GONE
        } else {
            mainBinding.emailNotVerifiedLayout.visibility = View.VISIBLE
            mainBinding.emailVerifiedLayout.visibility = View.GONE
        }
        mainBinding.tvEmailSend.text =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_EMAIL)
        mainBinding.tvVerifiedEmail.text =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_EMAIL)
        if (MyApplication.preferenceDB!!.getInt(
                Constants.SHARED_PREFERENCE_USER_MOBILE_VERIFY_STATUS,
                0
            ) == 1
        ) {
            mainBinding.mobileVerifiedLayout.visibility = View.VISIBLE
            mainBinding.mobileNotVerifiedLayout.visibility = View.GONE
        } else {
            mainBinding.mobileNotVerifiedLayout.visibility = View.VISIBLE
            mainBinding.mobileVerifiedLayout.visibility = View.GONE
        }
        mainBinding.tvVerifiedMobileNo.text =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_MOBILE)
    }


    private fun verifyEmailByOtp(email: String?) {
        mainBinding.refreshing = true
        val userId: String =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        val baseRequest = BaseRequest()
        baseRequest.user_id = userId
        baseRequest.email = email!!
        val normalResponseCustomCall: CustomCallAdapter.CustomCall<NormalResponse> =
            oAuthRestService.verifyEmailByOtp(baseRequest)
        normalResponseCustomCall.enqueue(object : CustomCallAdapter.CustomCallback<NormalResponse> {
            override fun success(response: Response<NormalResponse>) {
                mainBinding.refreshing = false
                val normalResponse: NormalResponse = response.body()!!
                if (normalResponse.status == 1) {
                    AppUtils.showError(context as AppCompatActivity, normalResponse.message)
                } else {
                    AppUtils.showError(context as AppCompatActivity, normalResponse.message)
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
                e!!.printStackTrace()
                if (e.response!!.code() >= 400 && e.response.code() < 404) {
                    logout()
                }
            }
        })
    }

    private fun verifyByMobile(mobile: String) {
        mainBinding.refreshing = true
        val userId: String =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        val baseRequest = BaseRequest()
        baseRequest.user_id = userId
        baseRequest.mobile = mobile
        val normalResponseCustomCall: CustomCallAdapter.CustomCall<NormalResponse> =
            oAuthRestService.verifyByMobile(baseRequest)
        normalResponseCustomCall.enqueue(object : CustomCallAdapter.CustomCallback<NormalResponse> {
            override fun success(response: Response<NormalResponse>) {
                mainBinding.refreshing = false
                val normalResponse = response.body()!!
                if (normalResponse.status == 1) {
                    showBottomSheet(mobile)
                    AppUtils.showError(context as AppCompatActivity, normalResponse.message)
                } else {
                    AppUtils.showError(context as AppCompatActivity, normalResponse.message)
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

    private fun updateMobile(mobile: String) {
        mainBinding.refreshing = true
        val userId: String =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        val baseRequest = BaseRequest()
        baseRequest.user_id = userId
        baseRequest.mobile = mobile
        val normalResponseCustomCall: CustomCallAdapter.CustomCall<LoginSendOtpResponse> =
            oAuthRestService.mobileUpdate(baseRequest)
        normalResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<LoginSendOtpResponse> {
            override fun success(response: Response<LoginSendOtpResponse>) {
                mainBinding.setRefreshing(false)
                val normalResponse: LoginSendOtpResponse = response.body()!!
                if (normalResponse.status == "1") {
                    Toast.makeText(context, normalResponse.message, Toast.LENGTH_SHORT).show()
                    //AppUtils.showErrorr((AppCompatActivity) context, normalResponse.getMessage());
                    showBottomSheet(mobile)
                } else {
                    Toast.makeText(context, normalResponse.message, Toast.LENGTH_SHORT).show()
                    //AppUtils.showErrorr((AppCompatActivity) context, normalResponse.getMessage());
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
                e!!.printStackTrace()
                if (e.response!!.code() in 400..403) {
                    logout()
                }
            }
        })
    }

    fun showBottomSheet(mobile: String) {
        mStackLevel++
        val addPhotoBottomDialogFragment: VerifyOtpBtmSheet =
            VerifyOtpBtmSheet.newInstance(mobile, isChangedMobile)
        addPhotoBottomDialogFragment.setTargetFragment(
            this,
            DIALOG_FRAGMENT
        )
        addPhotoBottomDialogFragment.show(
            childFragmentManager,
            VerifyOtpBtmSheet.TAG
        )
    }

    private fun emailUpdate(email: String) {
        mainBinding.refreshing = true
        val userId: String =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        val baseRequest = BaseRequest()
        baseRequest.user_id = userId
        baseRequest.email = email
        val normalResponseCustomCall: CustomCallAdapter.CustomCall<LoginSendOtpResponse> =
            oAuthRestService.emailUpdate(baseRequest)
        normalResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<LoginSendOtpResponse> {
            override fun success(response: Response<LoginSendOtpResponse>) {
                mainBinding.refreshing = false
                val normalResponse = response.body()!!
                if (normalResponse.status == "1") {
                    mainBinding.llEmailChange.visibility = View.GONE
                    mainBinding.tvEmailSend.text = email
                    MyApplication.preferenceDB!!.putString(
                        Constants.SHARED_PREFERENCE_USER_EMAIL,
                        email
                    )
                }
                Toast.makeText(context, normalResponse.message, Toast.LENGTH_SHORT).show()
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
                e!!.printStackTrace()
                if (e.response!!.code() in 400..403) {
                    logout()
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                showVerifiedNotVerifiedLayout()
            }
        }
    }

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
                if (updateProfileResponse.status == 1) {
                    MyApplication.logout(activity!!)
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