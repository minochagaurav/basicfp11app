package com.fp.devfantasypowerxi.app.view.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.request.UpdateProfileRequest
import com.fp.devfantasypowerxi.app.api.response.GetUserFullDetailsResponse
import com.fp.devfantasypowerxi.app.api.response.GetUserFullDetailsValue
import com.fp.devfantasypowerxi.app.api.response.NormalResponse
import com.fp.devfantasypowerxi.app.api.response.UpdateProfileResponse
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.adapter.SpinnerAdapter
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivityPersonalDetailsBinding
import retrofit2.Response
import java.util.*
import javax.inject.Inject

// made by Gaurav Minocha
class PersonalDetailsActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityPersonalDetailsBinding
    @Inject
    lateinit var oAuthRestService: OAuthRestService
    lateinit var stateAr: Array<String>
    private var name1: String = ""
    private  var email1: String = ""
    private  var gender1: String = ""
    private var mobile1: String = ""
    private  var state1: String = ""
    private var address1: String = ""
    private var city1: String = ""
    private  var pinCode1: String = ""
    private  var country1: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_personal_details)
        MyApplication.getAppComponent()!!.inject(this@PersonalDetailsActivity)
        initialize()
        stateAr = resources.getStringArray(R.array.india_states)
        mainBinding.stateSpinner.adapter = SpinnerAdapter(applicationContext, stateAr)
        mainBinding.etDob.setOnClickListener { pickDate(mainBinding.etDob) }
        mainBinding.stateSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                if (i != 0) {
                    // mBinding.stateSpinner.setEnabled(false);
                    state1 = stateAr[i]
                }
                (mainBinding.stateSpinner.selectedView as TextView).setTextColor(
                    ContextCompat.getColor(applicationContext,
                        R.color.colorBlack)
                )
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        mainBinding.tvChangePassword.setOnClickListener {
            startActivity(
                Intent(
                    this@PersonalDetailsActivity,
                    ChangePasswordActivity::class.java
                )
            )
        }

        mainBinding.btnSave.setOnClickListener {
            name1 = mainBinding.name.text.toString()
            email1 = mainBinding.email.text.toString()
            mobile1 = mainBinding.mobile.text.toString()
            address1 = mainBinding.address.text.toString()
            city1 = mainBinding.city.text.toString()
            pinCode1 = mainBinding.pinCode.text.toString()
            gender1 = if (mainBinding.maleRb.isChecked) "male" else "female"
            if (name1.length < 4) AppUtils.showError(
                this@PersonalDetailsActivity,
                "Please enter valid name"
            ) else if (email1.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email1)
                    .matches()
            ) AppUtils.showError(
                this@PersonalDetailsActivity,
                "Please enter valid email address"
            ) else if (mobile1.length != 10) AppUtils.showError(
                this@PersonalDetailsActivity,
                "Please enter valid mobile number"
            ) else if (state1 == "Select State") {
                AppUtils.showError(this@PersonalDetailsActivity, "Please select your state")
            } else {
                mainBinding.refreshing = true
                updateUserProfile()
            }
        }

        mainBinding.refreshing = true
        getFullUserDetails()
    }
    private fun updateUserProfile() {
        mainBinding.refreshing = true
        val updateProfileRequest = UpdateProfileRequest()
        updateProfileRequest.user_id =MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        updateProfileRequest.username =name1
        updateProfileRequest.dob=mainBinding.etDob.text.toString().trim()
        updateProfileRequest.gender=gender1
        updateProfileRequest.address=address1
        updateProfileRequest.city =city1
        updateProfileRequest.state =state1
        updateProfileRequest.country=country1
        updateProfileRequest.pincode=pinCode1
        val userFullDetailsResponseCustomCall: CustomCallAdapter.CustomCall<UpdateProfileResponse> =
            oAuthRestService.updateProfile(updateProfileRequest)
        userFullDetailsResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<UpdateProfileResponse> {
            override fun success(response: Response<UpdateProfileResponse>) {
                mainBinding.refreshing = false
                val updateProfileResponse: UpdateProfileResponse = response.body()!!
                if (updateProfileResponse.status == 1) {
                    AppUtils.showError(
                        this@PersonalDetailsActivity,
                        "Profile Update Successfully"
                    )
                    MyApplication.preferenceDB!!.putString(Constants.SHARED_PREFERENCE_USER_NAME, name1)
                } else {
                    AppUtils.showError(
                        this@PersonalDetailsActivity,
                        updateProfileResponse.message
                    )
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
                if (e!!.response!!.code() >= 400 && e.response!!.code() < 404) {
                    logout()
                }
            }
        })
    }
    // initialize toolbar
    private fun initialize() {
        setSupportActionBar(mainBinding.myToolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = getString(R.string.update_profile)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }

        mainBinding.tvChangePassword.setOnClickListener {
            startActivity(Intent(this@PersonalDetailsActivity, ChangePasswordActivity::class.java))
        }
    }

    private fun getFullUserDetails() {
        mainBinding.refreshing = true
        val baseRequest = BaseRequest()
        baseRequest.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        val userFullDetailsResponseCustomCall: CustomCallAdapter.CustomCall<GetUserFullDetailsResponse> =
            oAuthRestService.getUserFullDetails(baseRequest)
        userFullDetailsResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<GetUserFullDetailsResponse> {
            override fun success(response: Response<GetUserFullDetailsResponse>) {
                mainBinding.refreshing = false
                if (response.isSuccessful && response.body() != null) {
                    val getUserFullDetailsResponse: GetUserFullDetailsResponse = response.body()!!
                    if (getUserFullDetailsResponse.status == 1) {
                        val userDetailValue: GetUserFullDetailsValue =
                            getUserFullDetailsResponse.result.value
                        mainBinding.name.setText(userDetailValue.username)
                        mainBinding.email.setText(userDetailValue.email)
                        mainBinding.mobile.setText(userDetailValue.mobile.toString())
                        if (userDetailValue.gender == "female"
                        ) mainBinding.femaleRb.isChecked = true else mainBinding.maleRb.isChecked =
                            true
                        mainBinding.etDob.setText(userDetailValue.dob)
                        mainBinding.address.setText(userDetailValue.address)
                        mainBinding.city.setText(userDetailValue.city)
                        mainBinding.pinCode.setText(userDetailValue.pincode)

                        for (i in stateAr.indices) {
                            if (stateAr[i].equals(
                                    userDetailValue.state,
                                    ignoreCase = true
                                )
                            ) mainBinding.stateSpinner.setSelection(i)
                        }
                        if (userDetailValue.dobfreeze == 1) {
                            mainBinding.etDob.isEnabled = false
                        }
                        if (userDetailValue.statefreeze == 1) mainBinding.stateSpinner.isEnabled =
                            false
                       mainBinding.email.isEnabled = false
                    } else {
                        AppUtils.showError(
                            this@PersonalDetailsActivity,
                            getUserFullDetailsResponse.message
                        )
                    }
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
                    logout()
                } else {
                    AppUtils.showError(
                        this@PersonalDetailsActivity,
                        updateProfileResponse.message
                    )
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
            }
        })
    }

    fun pickDate(dialog: TextView) {
        val mCurrentDate = Calendar.getInstance()
        val mYear = mCurrentDate[Calendar.YEAR]
        val mMonth = mCurrentDate[Calendar.MONTH]
        val mDay = mCurrentDate[Calendar.DAY_OF_MONTH]
        val mDatePicker = DatePickerDialog(
            this@PersonalDetailsActivity,
            { _, selectedYear, selectedMonth, selectedDay ->
                val d = (selectedMonth + 1).toString() + "/" + selectedDay + "/" + selectedYear
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

    // toolbar click event
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}