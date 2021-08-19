package com.fp.devfantasypowerxi.app.view.activity

import android.os.Bundle
import android.text.InputType
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.request.ChangePasswordRequest
import com.fp.devfantasypowerxi.app.api.response.NormalResponse
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivityChangePasswordBinding
import retrofit2.Response
import javax.inject.Inject

// made by Gaurav Minocha
class ChangePasswordActivity : AppCompatActivity() {

    @Inject
    lateinit var oAuthRestService: OAuthRestService
    lateinit var mainBinding: ActivityChangePasswordBinding

    private var oldPassword = ""
    var newPassword = ""
    var cnfPassword = ""

    var passwordNotVisibleForOld = 0
    private var passwordNotVisibleForNew = 0
    private var passwordNotVisibleForConfirm = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)
        MyApplication.getAppComponent()!!.inject(this@ChangePasswordActivity)
        initialize()
    }

    // initialize toolbar
    fun initialize() {
        setSupportActionBar(mainBinding.myToolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = ""
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }

        mainBinding.btnSave.setOnClickListener {
            newPassword = mainBinding.etNewPassword.text.toString().trim()
            cnfPassword = mainBinding.etConfirmPassword.text.toString().trim()
            oldPassword = mainBinding.etOldPassword.text.toString().trim()
            if (oldPassword.length < 4) Toast.makeText(
                this@ChangePasswordActivity,
                "Please enter valid old password",
                Toast.LENGTH_SHORT
            ).show() else if (newPassword.length < 4) Toast.makeText(
                this@ChangePasswordActivity,
                "Please enter valid new password",
                Toast.LENGTH_SHORT
            ).show() else if (cnfPassword.length < 4) Toast.makeText(
                this@ChangePasswordActivity,
                "Please enter valid confirm password",
                Toast.LENGTH_SHORT
            ).show() else if (!cnfPassword.equals(newPassword, ignoreCase = true)) Toast.makeText(
                this@ChangePasswordActivity,
                "Your new password and confirm password must be same",
                Toast.LENGTH_SHORT
            ).show() else {
                changePassword()
            }
        }
        mainBinding.ivOldShowPassword.setOnClickListener {
            if (mainBinding.etOldPassword.text.toString().trim() != "") {
                if (passwordNotVisibleForOld == 0) {
                    mainBinding.etOldPassword.inputType =
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    mainBinding.ivOldShowPassword.setImageResource(R.drawable.ic_password_view)
                    passwordNotVisibleForOld = 1
                } else {
                    mainBinding.ivOldShowPassword.setImageResource(R.drawable.view)
                    mainBinding.etOldPassword.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    passwordNotVisibleForOld = 0
                }
                mainBinding.etOldPassword.setSelection(mainBinding.etOldPassword.length())
            }
        }
        mainBinding.ivNewShowPassword.setOnClickListener {
            if (mainBinding.etNewPassword.text.toString().trim() != "") {
                passwordNotVisibleForNew = if (passwordNotVisibleForNew == 0) {
                    mainBinding.etNewPassword.inputType =
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    mainBinding.ivNewShowPassword.setImageResource(R.drawable.ic_password_view)
                    1
                } else {
                    mainBinding.ivNewShowPassword.setImageResource(R.drawable.view)
                    mainBinding.etNewPassword.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    0
                }
                mainBinding.etNewPassword.setSelection(mainBinding.etNewPassword.length())
            }
        }

        mainBinding.ivConfirmShowPassword.setOnClickListener {
            if (mainBinding.etConfirmPassword.text.toString().trim() != "") {
                passwordNotVisibleForConfirm = if (passwordNotVisibleForConfirm == 0) {
                    mainBinding.etConfirmPassword.inputType =
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    mainBinding.ivConfirmShowPassword.setImageResource(R.drawable.ic_password_view)
                    1
                } else {
                    mainBinding.ivConfirmShowPassword.setImageResource(R.drawable.view)
                    mainBinding.etConfirmPassword.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    0
                }
                mainBinding.etConfirmPassword.setSelection(mainBinding.etConfirmPassword.length())
            }
        }

    }

    private fun changePassword() {
        mainBinding.refreshing = true
        val changePasswordRequest = ChangePasswordRequest()
        changePasswordRequest.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        changePasswordRequest.oldpassword = mainBinding.etOldPassword.text.toString().trim()
        changePasswordRequest.newpassword =
            mainBinding.etConfirmPassword.text.toString().trim()
        val userFullDetailsResponseCustomCall: CustomCallAdapter.CustomCall<NormalResponse> =
            oAuthRestService.changePassword(changePasswordRequest)
        userFullDetailsResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<NormalResponse> {
            override fun success(response: Response<NormalResponse>) {
                mainBinding.refreshing = false
                val normalResponse: NormalResponse = response.body()!!
                if (normalResponse.status == 1) {
                    finish()
                }
                Toast.makeText(
                    this@ChangePasswordActivity,
                    normalResponse.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
                if (e!!.response!!.code() >= 400 && e.response!!.code() < 404) {
                    logout()
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
                val updateProfileResponse: NormalResponse = response.body()!!
                if (updateProfileResponse.status == 1) {
                    MyApplication.logout(this@ChangePasswordActivity)
                } else {
                    AppUtils.showError(
                        this@ChangePasswordActivity,
                        updateProfileResponse.message
                    )
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
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
}