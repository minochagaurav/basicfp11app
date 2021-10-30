package com.fp.devfantasypowerxi.app.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.request.LoginRequest
import com.fp.devfantasypowerxi.app.api.request.OtpVerfiyRequest
import com.fp.devfantasypowerxi.app.api.response.LoginResponse
import com.fp.devfantasypowerxi.app.api.response.NormalResponse
import com.fp.devfantasypowerxi.app.api.response.RegisterResponse
import com.fp.devfantasypowerxi.app.api.response.SendOtpResponse
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.common.utils.NetworkUtils
import com.fp.devfantasypowerxi.databinding.ActivityOtpVerifyBinding
import kotlinx.android.synthetic.main.activity_otp_verify.view.*
import retrofit2.Response
import javax.inject.Inject

// made by Gaurav Minocha
class OtpVerifyActivity : AppCompatActivity() {
    var mobileNo = ""
    var otp = ""
    var userId = ""
    var deviceId = ""
    private var isForOTPLogin = false

    @Inject
    lateinit var oAuthRestService: OAuthRestService
    lateinit var mainBinding: ActivityOtpVerifyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verify)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_otp_verify)
        MyApplication.getAppComponent()!!.inject(this@OtpVerifyActivity)
        initialize()
        /* mainBinding.btnSubmit.setOnClickListener {
             startActivity(Intent(this@OtpVerifyActivity, HomeActivity::class.java))
         }*/
    }

    // initialize toolbar
    @SuppressLint("SetTextI18n")
    private fun initialize() {
        setSupportActionBar(mainBinding.myToolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = ""
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
        if (intent != null && intent.extras != null) {
            if (intent.extras!!.containsKey("MOBILE")) {
                mobileNo = intent.extras!!.getString("MOBILE")!!
                userId = intent.extras!!.getString("userId")!!
                isForOTPLogin = intent.extras!!.getBoolean("isForOTPLogin")
            }
        }
        mainBinding.tvMobile.text = "+91 $mobileNo"
        mainBinding.resendTxt.visibility = View.GONE

        val diffInMs = (30 * 1000).toLong()

        val cT: CountDownTimer = object : CountDownTimer(diffInMs, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                mainBinding.resendTxt.visibility = View.VISIBLE
            }
        }
        cT.start()

        mainBinding.etPinFirst.addTextChangedListener(
            GenericTextWatcher(
                mainBinding.etPinFirst
            )
        )
        mainBinding.etPinSecond.addTextChangedListener(
            GenericTextWatcher(
                mainBinding.etPinSecond
            )
        )
        mainBinding.etPinThird.addTextChangedListener(
            GenericTextWatcher(
                mainBinding.etPinThird
            )
        )
        mainBinding.etPinFourth.addTextChangedListener(
            GenericTextWatcher(
                mainBinding.etPinFourth
            )
        )

        mainBinding.resendTxt.setOnClickListener {

            resendOTP()
        }
        mainBinding.btnSubmit.setOnClickListener {
            otp = (mainBinding.etPinFirst.text.toString().trim()
                    + mainBinding.etPinSecond.text.toString().trim()
                    + mainBinding.etPinThird.text.toString().trim()
                    + mainBinding.etPinFourth.text.toString().trim())
            if (otp != "" && otp.length == 4) {
                if (NetworkUtils.isNetworkAvailable(applicationContext)) {
                    if (isForOTPLogin) {
                        loginUser()
                    } else {
                        otpVerify()
                    }
                } else {
                    Toast.makeText(
                        this@OtpVerifyActivity,
                        getString(R.string.internet_off),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@OtpVerifyActivity,
                    "Please enter your four digit otp",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    inner class GenericTextWatcher(private val view: View) : TextWatcher {
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (view.id) {
                R.id.et_pin_first -> if (text.length == 1) mainBinding.etPinSecond.requestFocus()
                R.id.et_pin_second -> if (text.length == 1) mainBinding.etPinThird.requestFocus() else if (text.isEmpty()) mainBinding.etPinFirst.requestFocus()
                R.id.et_pin_third -> if (text.length == 1) mainBinding.etPinFourth.requestFocus() else if (text.isEmpty()) mainBinding.etPinSecond.requestFocus()
                R.id.et_pin_fourth -> if (text.isEmpty()) mainBinding.etPinThird.requestFocus()
            }
        }

        override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {

        }

        override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {

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
                val updateProfileResponse: NormalResponse = response.body()!!
                if (updateProfileResponse.status == 1) {
                    MyApplication.logout(this@OtpVerifyActivity)
                } else {
                    AppUtils.showError(this@OtpVerifyActivity, updateProfileResponse.message)
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
            }
        })
    }

    private fun otpVerify() {
        mainBinding.refreshing = true
        val otpVerfiyRequest = OtpVerfiyRequest()
        otpVerfiyRequest.mobile = mobileNo
        otpVerfiyRequest.otp = otp
        if (MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID) != "")
            otpVerfiyRequest.user_id =
                MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        val userLogin: CustomCallAdapter.CustomCall<RegisterResponse> =
            oAuthRestService.otpVerify(otpVerfiyRequest)
        userLogin.enqueue(object : CustomCallAdapter.CustomCallback<RegisterResponse> {
            override fun success(response: Response<RegisterResponse>) {
                mainBinding.refreshing = false
                mainBinding.resendTxt.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    val registerResponse: RegisterResponse = response.body()!!
                    if (registerResponse.status == 1) {
                        MyApplication.preferenceDB!!.putBoolean(
                            Constants.SHARED_PREFERENCES_IS_LOGGED_IN,
                            true
                        )
                        MyApplication.preferenceDB!!.putString(
                            Constants.SHARED_PREFERENCE_USER_ID,
                            registerResponse.result.user_id.toString() + ""
                        )
                        MyApplication.preferenceDB!!.putString(
                            Constants.SHARED_PREFERENCE_USER_NAME,
                            registerResponse.result.username
                        )
                        MyApplication.preferenceDB!!.putString(
                            Constants.SHARED_PREFERENCE_USER_MOBILE,
                            registerResponse.result.mobile
                        )
                        MyApplication.preferenceDB!!.putString(
                            Constants.SHARED_PREFERENCE_USER_EMAIL,
                            registerResponse.result.email
                        )
                        MyApplication.preferenceDB!!.putString(
                            Constants.SHARED_PREFERENCE_USER_TOKEN,
                            registerResponse.result.custom_user_token
                        )
                        MyApplication.preferenceDB!!.putString(
                            Constants.SHARED_PREFERENCE_USER_REFER_CODE,
                            registerResponse.result.refercode
                        )
                        MyApplication.preferenceDB!!.putInt(
                            Constants.SHARED_PREFERENCE_USER_BANK_VERIFY_STATUS,
                            registerResponse.result.bank_verify
                        )
                        MyApplication.preferenceDB!!.putInt(
                            Constants.SHARED_PREFERENCE_USER_PAN_VERIFY_STATUS,
                            registerResponse.result.pan_verify
                        )
                        MyApplication.preferenceDB!!.putInt(
                            Constants.SHARED_PREFERENCE_USER_MOBILE_VERIFY_STATUS,
                            registerResponse.result.mobile_verify
                        )
                        MyApplication.preferenceDB!!.putInt(
                            Constants.SHARED_PREFERENCE_USER_EMAIL_VERIFY_STATUS,
                            registerResponse.result.email_verify
                        )
                        MyApplication.preferenceDB!!.putString(
                            Constants.SHARED_PREFERENCE_JWT_TOKEN,
                            registerResponse.result.jwt_token
                        )
                        MyApplication.preferenceDB!!.putInt(
                            Constants.SHARED_PREFERENCE_USER_CONTACT_AVAILABLE,
                            registerResponse.result.is_contact_data
                        )
                        //  startActivity(Intent(this@OtpVerifyActivity, HomeActivity::class.java))
                        val intent = Intent(this@OtpVerifyActivity, HomeActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@OtpVerifyActivity,
                            registerResponse.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@OtpVerifyActivity,
                        "Oops! Something went Wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
                e!!.printStackTrace()
                logout()

            }
        })
    }

    private fun loginUser() {
        mainBinding.refreshing = true
        val loginRequest = LoginRequest()
        loginRequest.email = mobileNo
        loginRequest.fcmToken = ""
        loginRequest.deviceId = deviceId
        loginRequest.login_type = "otp"
        loginRequest.password = otp
        val userLogin: CustomCallAdapter.CustomCall<LoginResponse> =
            oAuthRestService.userLogin(loginRequest)
        userLogin.enqueue(object : CustomCallAdapter.CustomCallback<LoginResponse> {
            override fun success(response: Response<LoginResponse>) {
                mainBinding.refreshing = false
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse: LoginResponse = response.body()!!
                    if (loginResponse.status == 1) {
                        MyApplication.preferenceDB!!.putBoolean(
                            Constants.SHARED_PREFERENCES_IS_LOGGED_IN,
                            true
                        )
                        MyApplication.preferenceDB!!.putString(
                            Constants.SHARED_PREFERENCE_USER_ID,
                            loginResponse.result.user_id.toString() + ""
                        )
                        MyApplication.preferenceDB!!.putString(
                            Constants.SHARED_PREFERENCE_JWT_TOKEN,
                            loginResponse.result.jwt_token
                        )
                        MyApplication.preferenceDB!!.putString(
                            Constants.SHARED_PREFERENCE_USER_NAME,
                            loginResponse.result.username
                        )
                        MyApplication.preferenceDB!!.putString(
                            Constants.SHARED_PREFERENCE_USER_MOBILE,
                            loginResponse.result.mobile
                        )
                        MyApplication.preferenceDB!!.putString(
                            Constants.SHARED_PREFERENCE_USER_EMAIL,
                            loginResponse.result.email
                        )
                        MyApplication.preferenceDB!!.putString(
                            Constants.SHARED_PREFERENCE_USER_TOKEN,
                            loginResponse.result.custom_user_token
                        )
                        MyApplication.preferenceDB!!.putString(
                            Constants.SHARED_PREFERENCE_USER_REFER_CODE,
                            loginResponse.result.refercode
                        )
                        MyApplication.preferenceDB!!.putString(
                            Constants.SHARED_PREFERENCE_USER_TEAM_NAME,
                            loginResponse.result.team
                        )
                        MyApplication.preferenceDB!!.putString(
                            Constants.SHARED_PREFERENCE_USER_PIC,
                            loginResponse.result.user_profile_image
                        )
                        MyApplication.preferenceDB!!.putInt(
                            Constants.SHARED_PREFERENCE_USER_BANK_VERIFY_STATUS,
                            loginResponse.result.bank_verify
                        )
                        MyApplication.preferenceDB!!.putInt(
                            Constants.SHARED_PREFERENCE_USER_PAN_VERIFY_STATUS,
                            loginResponse.result.pan_verify
                        )
                        MyApplication.preferenceDB!!.putInt(
                            Constants.SHARED_PREFERENCE_USER_MOBILE_VERIFY_STATUS,
                            loginResponse.result.mobile_verify
                        )
                        MyApplication.preferenceDB!!.putInt(
                            Constants.SHARED_PREFERENCE_USER_EMAIL_VERIFY_STATUS,
                            loginResponse.result.email_verify
                        )
                        val intent = Intent(this@OtpVerifyActivity, HomeActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@OtpVerifyActivity,
                            loginResponse.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@OtpVerifyActivity,
                        "Oops! Something went Wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
                e!!.printStackTrace()
            }
        })
    }

    private fun resendOTP() {
        mainBinding.refreshing = true
        val baseRequest = BaseRequest()
        baseRequest.mobile = mobileNo
        baseRequest.user_id = userId
        val userLogin: CustomCallAdapter.CustomCall<SendOtpResponse> =
            oAuthRestService.sendOTP(baseRequest)
        userLogin.enqueue(object : CustomCallAdapter.CustomCallback<SendOtpResponse> {
            override fun success(response: Response<SendOtpResponse>) {
                mainBinding.refreshing = false
                mainBinding.resendTxt.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    val sendOtpResponse: SendOtpResponse = response.body()!!
                    Toast.makeText(
                        this@OtpVerifyActivity,
                        sendOtpResponse.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    val diffInMs = (30 * 1000).toLong()
                    val cT: CountDownTimer = object : CountDownTimer(diffInMs, 1000) {
                        override fun onTick(millisUntilFinished: Long) {}
                        override fun onFinish() {
                            mainBinding.resendTxt.visibility = View.VISIBLE
                        }
                    }
                    cT.start()
                } else {
                    Toast.makeText(
                        this@OtpVerifyActivity,
                        "Oops! Something went Wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
                e!!.printStackTrace()
                logout()

            }
        })
    }

}