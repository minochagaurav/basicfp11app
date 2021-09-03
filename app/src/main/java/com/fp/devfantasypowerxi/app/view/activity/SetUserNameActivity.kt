package com.fp.devfantasypowerxi.app.view.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.request.ReferRequest
import com.fp.devfantasypowerxi.app.api.response.LoginSendOtpResponse
import com.fp.devfantasypowerxi.app.api.response.NormalResponse
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivityLoginBinding
import com.fp.devfantasypowerxi.databinding.ActivitySetUserNameBinding
import retrofit2.Response
import javax.inject.Inject

class SetUserNameActivity : AppCompatActivity() {
    @Inject
    lateinit var oAuthRestService: OAuthRestService
    lateinit var mainBinding: ActivitySetUserNameBinding
    var userName = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_set_user_name)
        MyApplication.getAppComponent()!!.inject(this@SetUserNameActivity)
        mainBinding.btnSubmit.setOnClickListener {
            if (mainBinding.etTeamName.text.toString() != "") {
                sendTeamNReferCode()
            } else {
                Toast.makeText(
                    MyApplication.appContext,
                    "Please set your team name.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        if (intent != null && intent.extras != null) {
            userName = intent.extras!!.getString(Constants.USER_NAME)!!
            mainBinding.etTeamName.setText(userName)
        }

    }

    private fun sendTeamNReferCode() {
        mainBinding.refreshing = true
        val referRequest = ReferRequest()
        referRequest.refercode = mainBinding.etReferCode.text.toString()
        referRequest.username = mainBinding.etTeamName.text.toString()
        val userLogin: CustomCallAdapter.CustomCall<NormalResponse> =
            oAuthRestService.addReferCode(referRequest)
        userLogin.enqueue(object : CustomCallAdapter.CustomCallback<NormalResponse> {
            override fun success(response: Response<NormalResponse>) {
                mainBinding.refreshing = false
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.status == 1) {

                        MyApplication.preferenceDB!!.putString(Constants.SHARED_PREFERENCE_USER_NAME,
                            mainBinding.etTeamName.text.toString())
                        val intent = Intent(this@SetUserNameActivity, HomeActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
            }
        })
    }

}