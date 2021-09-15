package com.fp.devfantasypowerxi.app.view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.request.OtpVerfiyRequest
import com.fp.devfantasypowerxi.app.api.response.LoginSendOtpResponse
import com.fp.devfantasypowerxi.app.api.response.RegisterResponse
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.LayoutBottomsheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Response
import javax.inject.Inject

class VerifyOtpBtmSheet : BottomSheetDialogFragment() {
    @Inject
  lateinit  var oAuthRestService: OAuthRestService
    lateinit var mBinding: LayoutBottomsheetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        MyApplication.getAppComponent()!!.inject(this@VerifyOtpBtmSheet)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_bottomsheet, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.tvMobile.text = userMObile
        mBinding.btnVerifyOtp.setOnClickListener {
            if (mBinding.etOtp.text.toString().trim().length != 4) AppUtils.showError(
                activity as AppCompatActivity,
                "Please enter valid 4 digit OTP"
            ) else {
                if (changedMobile) {
                    updateMobile(mBinding.etOtp.text.toString().trim())
                } else {
                    otpVerify(userMObile, mBinding.etOtp.text.toString().trim())
                }
            }
        }
    }

    private fun otpVerify(mobileNo: String, otp: String) {
        mBinding.refreshing = true
        val otpVerifyRequest = OtpVerfiyRequest()
        otpVerifyRequest.mobile=mobileNo
        otpVerifyRequest.otp=otp
        if (MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!="")
            otpVerifyRequest.user_id=MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        val userLogin: CustomCallAdapter.CustomCall<RegisterResponse> = oAuthRestService.otpVerify(otpVerifyRequest)
        userLogin.enqueue(object : CustomCallAdapter.CustomCallback<RegisterResponse> {
            override fun success(response: Response<RegisterResponse>) {
                mBinding.refreshing = false
                dismiss()
                if (response.isSuccessful && response.body() != null) {
                    val registerResponse: RegisterResponse = response.body()!!
                    if (registerResponse.status == 1) {
                        MyApplication.preferenceDB!!.putString(
                            Constants.SHARED_PREFERENCE_USER_MOBILE,
                            registerResponse.result.mobile
                        )
                        MyApplication.preferenceDB!!.putInt(
                            Constants.SHARED_PREFERENCE_USER_MOBILE_VERIFY_STATUS,
                            registerResponse.result.mobile_verify
                        )
                        dismiss()
                        getTargetFragment()!!.onActivityResult(
                            getTargetRequestCode(),
                            Activity.RESULT_OK,
                            activity!!.getIntent()
                        )
                    } else {
                        Toast.makeText(
                            getContext(),
                            registerResponse.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(getContext(), "Oops! Something went Worng", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun failure(e: ApiException?) {
                mBinding.refreshing = false
                e!!.printStackTrace()
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    private fun updateMobile(otp: String) {
        mBinding.refreshing = true
        val userId: String = MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        val baseRequest = BaseRequest()
        baseRequest.user_id=userId
        baseRequest.mobile=userMObile
        baseRequest.otp=otp
        val normalResponseCustomCall: CustomCallAdapter.CustomCall<LoginSendOtpResponse> =
            oAuthRestService.mobileUpdate(baseRequest)
        normalResponseCustomCall.enqueue(object : CustomCallAdapter.CustomCallback<LoginSendOtpResponse> {
            override fun success(response: Response<LoginSendOtpResponse>) {
                mBinding.refreshing = false
                val normalResponse: LoginSendOtpResponse = response.body()!!
                if (normalResponse.status=="1") {
                    MyApplication.preferenceDB!!.putString(
                        Constants.SHARED_PREFERENCE_USER_MOBILE,
                        userMObile
                    )
                    Toast.makeText(context, normalResponse.message, Toast.LENGTH_SHORT)
                        .show()
                    dismiss()
                    getTargetFragment()!!.onActivityResult(
                        getTargetRequestCode(),
                        Activity.RESULT_OK,
                        activity!!.intent
                    )
                } else {
                    Toast.makeText(context, normalResponse.message, Toast.LENGTH_SHORT)
                        .show()
                    //AppUtils.showErrorr((AppCompatActivity) context, normalResponse.getMessage());
                }
            }

            override fun failure(e: ApiException?) {
                mBinding.refreshing = false
                e!!.printStackTrace()
            }
        })
    }

    companion object {
        const val TAG = "ActionBottomDialog"
        var userMObile: String =""
        var changedMobile = false
        fun newInstance(mobile: String, isChangedMobile: Boolean): VerifyOtpBtmSheet {
            userMObile = mobile
            changedMobile = isChangedMobile
            return VerifyOtpBtmSheet()
        }
    }
}