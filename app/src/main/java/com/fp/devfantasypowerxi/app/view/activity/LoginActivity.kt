package com.fp.devfantasypowerxi.app.view.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputType
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.request.LoginRequest
import com.fp.devfantasypowerxi.app.api.response.LoginResponse
import com.fp.devfantasypowerxi.app.api.response.LoginSendOtpResponse
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.common.utils.NetworkUtils
import com.fp.devfantasypowerxi.databinding.ActivityLoginBinding
import retrofit2.Response
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.regex.Pattern
import javax.inject.Inject

// made by Gaurav Minocha
class LoginActivity : AppCompatActivity() {
    var passwordNotVisible = 0

    var deviceId = ""

    @Inject
    lateinit var oAuthRestService: OAuthRestService
    var isLoginWithOTP = false
    lateinit var mainBinding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        MyApplication.getAppComponent()!!.inject(this@LoginActivity)
        // click event on buttons
        mainBinding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }
        mainBinding.tvLoginWithOtp.setOnClickListener {
            val mobile = mainBinding.etEmail.text.toString().trim()
            val mobilePattern = "[0-9]{10}"
            if (mobile.isEmpty()) {
                Toast.makeText(
                    MyApplication.appContext,
                    "Enter a valid mobile number.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!Pattern.matches(mobilePattern, mobile)) {
                Toast.makeText(
                    MyApplication.appContext,
                    "Please enter valid 10 digit phone number",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (NetworkUtils.isNetworkAvailable(applicationContext))
                    sendOtpMobile(mobile) else AppUtils.showError(
                    this@LoginActivity,
                    getString(R.string.internet_off)
                )
            }
        }

        mainBinding.btnLogin.setOnClickListener {
            //startActivity(Intent(this@LoginActivity, HomeActivity::class.java))

            if (validate()) {
                if (NetworkUtils.isNetworkAvailable(applicationContext))
                    loginUser()
                else AppUtils.showError(
                    this,
                    getString(R.string.internet_off)
                )
            }
        }
        mainBinding.tvRegisterNow.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))

        }

        mainBinding.showPass.setOnClickListener {
            if (mainBinding.etPassword.text.toString().trim() != "") {
                if (passwordNotVisible == 0) {
                    mainBinding.etPassword.inputType =
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    mainBinding.showPass.setImageResource(R.drawable.ic_password_view)
                    passwordNotVisible = 1
                } else {
                    mainBinding.showPass.setImageResource(R.drawable.view)
                    mainBinding.etPassword.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    passwordNotVisible = 0
                }
                mainBinding.etPassword.setSelection(mainBinding.etPassword.length())
            }
        }
    }

    private fun sendOtpMobile(mobileNo: String) {
        mainBinding.refreshing = true
        val baseRequest = BaseRequest()
        baseRequest.mobile = mobileNo
        val userLogin: CustomCallAdapter.CustomCall<LoginSendOtpResponse> =
            oAuthRestService.sendLoginOTP(baseRequest)
        userLogin.enqueue(object : CustomCallAdapter.CustomCallback<LoginSendOtpResponse> {
            override fun success(response: Response<LoginSendOtpResponse>) {
                mainBinding.refreshing = false
                if (response.isSuccessful() && response.body() != null) {
                    val loginSendOtpResponse: LoginSendOtpResponse = response.body()!!
                    if (loginSendOtpResponse.status == "1") {
                        // otp = loginSendOtpResponse.getSendOtpResponse().getOtp();
                        isLoginWithOTP = true
                        /*        mainBinding.llPassword.setVisibility(View.GONE);
                            mainBinding.tilOtp.setVisibility(View.VISIBLE);*/
                        val intent = Intent(
                            this@LoginActivity,
                            OtpVerifyActivity::class.java
                        )
                        intent.putExtra("MOBILE", mobileNo)
                        intent.putExtra("userId", loginSendOtpResponse.userId)
                        intent.putExtra("isForOTPLogin", true)
                        startActivity(intent)
                        Toast.makeText(
                            this@LoginActivity,
                            loginSendOtpResponse.sendOtpResponse.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            loginSendOtpResponse.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
            }
        })
    }

    fun validate(): Boolean {
        return if (isLoginWithOTP) {
            val mobile = mainBinding.etEmail.text.toString().trim()
            val otp = mainBinding.etOtp.text.toString().trim()
            val mobilePattern = "[0-9]{10}"
            if (mobile.isEmpty()) {
                Toast.makeText(
                    MyApplication.appContext,
                    "Enter a valid mobile number.",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            } else if (!(Pattern.matches(mobilePattern, mobile))) {
                Toast.makeText(
                    MyApplication.appContext,
                    "Please enter valid 10 digit phone number",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            } else if (otp.isEmpty()) {
                //mainBinding.etPassword.setError("Please Fill The Password.");
                Toast.makeText(MyApplication.appContext, "Please Fill Otp.", Toast.LENGTH_SHORT)
                    .show()
                return false
            }
            if (otp.length != 4) {
                Toast.makeText(
                    this@LoginActivity,
                    "Please Enter your valid otp",
                    Toast.LENGTH_SHORT
                ).show()
                false
            } else {
                true
            }
        } else {
            val email = mainBinding.etEmail.text.toString().trim()
            val password = mainBinding.etPassword.text.toString().trim()
            if (email.isEmpty() /*|| !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()*/) {
                /*        mainBinding.etPassword.setText("");
                  mainBinding.etEmail.setError("Enter a valid email address.");*/
                Toast.makeText(
                    MyApplication.appContext,
                    "Enter a valid user id.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            } else if (password.isEmpty()) {
                //mainBinding.etPassword.setError("Please Fill The Password.");
                Toast.makeText(
                    MyApplication.appContext,
                    "Please Fill The Password.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            } else {
                true
            }
        }
    }

    private fun PrintHashKey() {
        try {
            val info = packageManager.getPackageInfo(
                "com.img.fantasypowerxi",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
                print("Key hash is : " + Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }

    private fun loginUser() {
        mainBinding.refreshing = true
        val loginRequest = LoginRequest()
        loginRequest.email = mainBinding.etEmail.text.toString().trim()
        loginRequest.fcmToken = ""
        loginRequest.deviceId = deviceId
        if (isLoginWithOTP) {
            loginRequest.login_type = "otp"
            loginRequest.password = mainBinding.etOtp.text.toString().trim()
        } else {
            loginRequest.password = mainBinding.etPassword.text.toString().trim()
        }
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
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            loginResponse.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Oops! Something went Worng",
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

}