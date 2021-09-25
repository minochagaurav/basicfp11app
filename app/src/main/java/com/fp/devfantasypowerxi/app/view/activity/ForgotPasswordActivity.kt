package com.fp.devfantasypowerxi.app.view.activity

import android.os.Bundle
import android.util.Patterns
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.response.NormalResponse
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivityForgotPasswordBinding
import retrofit2.Response
import javax.inject.Inject

// made by Gaurav Minocha
class ForgotPasswordActivity : AppCompatActivity() {
    @Inject
    lateinit var oAuthRestService: OAuthRestService
    lateinit var mainBinding: ActivityForgotPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
        MyApplication.getAppComponent()!!.inject(this@ForgotPasswordActivity)
        // initialize toolbar
        setSupportActionBar(mainBinding.myToolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = "Forgot Password"
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }


        mainBinding.submit.setOnClickListener {
            if (mainBinding.etEmail.text.toString().trim().isEmpty()
                || !Patterns.EMAIL_ADDRESS.matcher(mainBinding.etEmail.text.toString().trim())
                    .matches()
            ) {
                AppUtils.showError(this@ForgotPasswordActivity, "Please Enter your email id")
            } else {
                forgotPassword(mainBinding.etEmail.text.toString().trim())
            }
        }

    }

    private fun forgotPassword(email: String?) {
        mainBinding.refreshing = true
        val baseRequest = BaseRequest()
        baseRequest.email = email!!
        val bankDetailResponseCustomCall: CustomCallAdapter.CustomCall<NormalResponse> =
            oAuthRestService.forgotPassword(baseRequest)
        bankDetailResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<NormalResponse> {
            override fun success(response: Response<NormalResponse>) {
                mainBinding.refreshing = false
                if (response.isSuccessful && response.body() != null) {
                    val normalResponse: NormalResponse = response.body()!!
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        normalResponse.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    if (normalResponse.status == 1) {
                        finish()
                    }
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
                e!!.printStackTrace()
                if (e.response != null) {
                    if (e.response.code() in 400..403) {
                        logout()
                    }
                }
            }
        })
    }

    // toolbar click event
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
                val updateProfileResponse = response.body()!!
                if (updateProfileResponse.status == 1) {
                    MyApplication.logout(this@ForgotPasswordActivity)
                } else {
                    AppUtils.showError(
                        this@ForgotPasswordActivity,
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