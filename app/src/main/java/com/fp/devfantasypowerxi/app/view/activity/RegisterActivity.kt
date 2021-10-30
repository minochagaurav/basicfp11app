package com.fp.devfantasypowerxi.app.view.activity

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.RegisterRequest
import com.fp.devfantasypowerxi.app.api.response.NormalResponse
import com.fp.devfantasypowerxi.app.api.response.RegisterResponse
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.common.utils.NetworkUtils
import com.fp.devfantasypowerxi.databinding.ActivityRegisterBinding
import retrofit2.Response
import java.util.*
import javax.inject.Inject


// made by Gaurav Minocha

class RegisterActivity : AppCompatActivity() {

    @Inject
    lateinit var oAuthRestService: OAuthRestService
    lateinit var mainBinding: ActivityRegisterBinding
    var deviceId = ""
    var passwordNotVisible = 0
    var cnfPasswordNotVisible = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        MyApplication.getAppComponent()!!.inject(this@RegisterActivity)
        initialize()
        getReferCode()
    }

    private fun initialize() {
        mainBinding.ivShowPassword.setOnClickListener {
            if (mainBinding.etPassword.text.toString().trim() != "") {
                if (passwordNotVisible == 0) {
                    mainBinding.etPassword.inputType =
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    mainBinding.ivShowPassword.setImageResource(R.drawable.ic_password_view)
                    passwordNotVisible = 1
                } else {
                    mainBinding.ivShowPassword.setImageResource(R.drawable.view)
                    mainBinding.etPassword.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    passwordNotVisible = 0
                }
                mainBinding.etPassword.setSelection(mainBinding.etPassword.length())
            }
        }
        mainBinding.ivShowCnfPassword.setOnClickListener {
            if (mainBinding.etCnfPassword.text.toString().trim() != "") {
                if (cnfPasswordNotVisible == 0) {
                    mainBinding.etCnfPassword.inputType =
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    mainBinding.ivShowCnfPassword.setImageResource(R.drawable.ic_password_view)
                    cnfPasswordNotVisible = 1
                } else {
                    mainBinding.ivShowCnfPassword.setImageResource(R.drawable.view)
                    mainBinding.etCnfPassword.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    cnfPasswordNotVisible = 0
                }
                mainBinding.etCnfPassword.setSelection(mainBinding.etCnfPassword.length())
            }
        }

        //mainBinding.termsCC.setText(mainBinding.termsCC.getText().toString()+ Html.fromHtml(getResources().getString(R.string.term_conditions_second)));
        /*mainBinding.btnFacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(RegisterActivity.this, Arrays.asList("public_profile","email"));
            }
        });*/

        /*  mainBinding.tvTc.setOnClickListener {
              startActivity(
                  Intent(
                      MyApplication.appContext,
                      WebActivity::class.java
                  ).putExtra("title", "Terms & Conditions").putExtra("type", "terms/")
              )
          }*/
        mainBinding.tvDateOfBirth.setOnClickListener { pickDate(mainBinding.etDob) }
        mainBinding.etDob.setOnClickListener { pickDate(mainBinding.etDob) }


/*

        mainBinding.tvPp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyApplication.appContext, WebActivity.class).putExtra("title", "Privacy Policy").putExtra("type", "privacy.html"));
            }
        });
*/
        /*   LoginManager.getInstance().registerCallback(callbackManager,
               object : FacebookCallback<LoginResult?>() {
                   fun onSuccess(loginResult: LoginResult) {
                       val socialLoginRequest = SocialLoginRequest()
                       val request: GraphRequest = GraphRequest.newMeRequest(
                           loginResult.getAccessToken(),
                           object : GraphJSONObjectCallback() {
                               fun onCompleted(`object`: JSONObject?, response: GraphResponse) {
                                   Log.v("FBLoginActivity", response.toString())
                                   val json: JSONObject = response.getJSONObject()
                                   try {
                                       socialLoginRequest.setEmail(json.getString("email"))
                                       socialLoginRequest.setName(json.getString("name"))
                                       socialLoginRequest.setImageUrl(
                                           "https://graph.facebook.com/" + json.getString(
                                               "id"
                                           ) + "/picture?width=150&width=150"
                                       )
                                       //socialLoginRequest.setImageUrl(Profile.getCurrentProfile().getProfilePictureUri(100,100).toString());
                                       socialLoginRequest.setSocialLoginType("facebook")
                                       socialLoginRequest.setDeviceId(deviceId)
                                       socialLoginRequest.setFcmToken(fcmToken)
                                       loginUserWithSocial(socialLoginRequest)
                                   } catch (e: JSONException) {
                                       e.printStackTrace()
                                   }
                               }
                           })
                       val parameters = Bundle()
                       parameters.putString("fields", "id,name,email")
                       request.setParameters(parameters)
                       request.executeAsync()
                   }

                   fun onCancel() {
                       Toast.makeText(this@RegisterActivity, "Login Canceled.", Toast.LENGTH_SHORT)
                           .show()
                   }

                   fun onError(exception: FacebookException?) {
                       Toast.makeText(
                           this@RegisterActivity,
                           "Cannot connect facebook error.",
                           Toast.LENGTH_SHORT
                       ).show()
                   }
               })
   */
        /* mainBinding.btnGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });*/mainBinding.tvLoginNow.setOnClickListener { finish() }
        mainBinding.btnRegister.setOnClickListener { validation() }
    }

    fun pickDate(dialog: TextView) {
        val mCurrentDate = Calendar.getInstance()
        val mYear = mCurrentDate[Calendar.YEAR]
        val mMonth = mCurrentDate[Calendar.MONTH]
        val mDay = mCurrentDate[Calendar.DAY_OF_MONTH]
        val mDatePicker = DatePickerDialog(
            this@RegisterActivity,
            { _, selectedyear, selectedmonth, selectedday ->
                val d = (selectedmonth + 1).toString() + "/" + selectedday + "/" + selectedyear
                dialog.text = d
            }, mYear, mMonth, mDay
        )
        mDatePicker.datePicker.maxDate = (System.currentTimeMillis() - 5.681e+11).toLong()
        mDatePicker.setTitle("Select Birth Date")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDatePicker.datePicker.firstDayOfWeek = Calendar.MONDAY
        }
        mDatePicker.show()
    }

    private fun validation() {
        val referCode = mainBinding.etReferCode.text.toString().trim()
        val email = mainBinding.etEmail.text.toString().trim()
        val phone = mainBinding.etMobileNo.text.toString().trim()
        val password = mainBinding.etPassword.text.toString().trim()
        val cnfPassword = mainBinding.etCnfPassword.text.toString().trim()
        if (phone.isEmpty()) showToast("Please enter valid mobile number.") else if (phone.length < 10) showToast(
            "Please enter 10 digits mobile number."
        ) else if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email)
                .matches()
        ) showToast("Please enter valid email address") else if (password.isEmpty()) showToast("Please enter valid password.") else if (password.length < 4) showToast(
            "Password should be 4 to 16 char long"
        ) else if (cnfPassword.isEmpty()) showToast("Please enter confirm password.") else if (cnfPassword.length < 4) showToast(
            "Password should be 4 to 16 char long"
        ) else if (password != cnfPassword) showToast("Password & Confirm password not matched") else if (!mainBinding.termsCC.isChecked) {
            showToast("Please accept FantasyPower11's T&Cs")
        } else {
            if (NetworkUtils.isNetworkAvailable(applicationContext)) {
                val registerRequest = RegisterRequest()
                registerRequest.refer_code = referCode
                registerRequest.email = email
                registerRequest.mobile = phone
                registerRequest.password = password
                registerRequest.dob = mainBinding.etDob.text.toString().trim()
                registerRequest.fcmToken =
                    MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_FIREBASE_TOKEN)
                        ?: ""
                registerRequest.deviceId = deviceId
                registerUser(registerRequest)
            } else {
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun getReferCode() {
        val myBalanceResponseCustomCall: CustomCallAdapter.CustomCall<NormalResponse> =
            oAuthRestService.getReferCode()
        myBalanceResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<NormalResponse> {
            override fun success(response: Response<NormalResponse>) {
                mainBinding.refreshing = false
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.status == 1) {
                        mainBinding.etReferCode.setText(response.body()!!.code)
                    }
                }
            }

            override fun failure(e: ApiException?) {
            }
        })
    }

    private fun registerUser(registerRequest: RegisterRequest) {
        val view = this@RegisterActivity.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        mainBinding.refreshing = true
        val userLogin: CustomCallAdapter.CustomCall<RegisterResponse> =
            oAuthRestService.userRegister(registerRequest)
        userLogin.enqueue(object : CustomCallAdapter.CustomCallback<RegisterResponse> {
            override fun success(response: Response<RegisterResponse>) {
                mainBinding.refreshing = false
                if (response.isSuccessful && response.body() != null) {
                    val registerResponse: RegisterResponse = response.body()!!
                    if (registerResponse.status == 1) {


                        val intent = Intent(this@RegisterActivity, OtpVerifyActivity::class.java)
                        intent.putExtra("MOBILE", mainBinding.etMobileNo.text.toString().trim())
                        intent.putExtra(
                            "userId",
                            registerResponse.result.user_id.toString() + ""
                        )
                        startActivity(intent)
                        // finish();
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            registerResponse.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}