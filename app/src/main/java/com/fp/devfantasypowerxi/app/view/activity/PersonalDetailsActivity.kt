package com.fp.devfantasypowerxi.app.view.activity

import android.Manifest
import android.app.DatePickerDialog
import android.app.LoaderManager
import android.content.CursorLoader
import android.content.Intent
import android.content.Loader
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.request.UpdateProfileRequest
import com.fp.devfantasypowerxi.app.api.request.UserData
import com.fp.devfantasypowerxi.app.api.response.GetUserFullDetailsResponse
import com.fp.devfantasypowerxi.app.api.response.GetUserFullDetailsValue
import com.fp.devfantasypowerxi.app.api.response.NormalResponse
import com.fp.devfantasypowerxi.app.api.response.UpdateProfileResponse
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.adapter.CursorAdapter
import com.fp.devfantasypowerxi.app.view.adapter.SpinnerAdapter
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivityPersonalDetailsBinding
import retrofit2.Response
import java.util.*
import javax.inject.Inject

// made by Gaurav Minocha
class PersonalDetailsActivity : AppCompatActivity(),
    LoaderManager.LoaderCallbacks<Cursor> {
    lateinit var mainBinding: ActivityPersonalDetailsBinding
    lateinit var mAdapter: CursorAdapter
    private val CONTACT_ID_INDEX = 0
    private val CONTACTS_LOADER_ID = 1

    @Inject
    lateinit var oAuthRestService: OAuthRestService
    lateinit var stateAr: Array<String?>
    private var name1: String = ""
    private var email1: String = ""
    private var gender1: String = ""
    private var mobile1: String = ""
    private var state1: String = ""
    private var address1: String = ""
    private var city1: String = ""
    private var pinCode1: String = ""
    private var country1: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_personal_details)
        MyApplication.getAppComponent()!!.inject(this@PersonalDetailsActivity)
        initialize()
        stateAr = resources.getStringArray(R.array.india_states)
        mainBinding.stateSpinner.adapter = SpinnerAdapter(applicationContext, stateAr)
        mainBinding.etDob.setOnClickListener { pickDate(mainBinding.etDob) }
        if (MyApplication.preferenceDB!!.getBoolean(Constants.SOCIAL_LOGIN, false)) {
            mainBinding.rlChangePassword.visibility = View.GONE
            mainBinding.changePasswordText.visibility = View.GONE


        } else {
            mainBinding.rlChangePassword.visibility = View.VISIBLE
            mainBinding.changePasswordText.visibility = View.VISIBLE
        }
        // Create the simple cursor adapter to use for our list
        // specifying the template to inflate (item_contact),

        if (MyApplication.preferenceDB!!.getInt(Constants.SHARED_PREFERENCE_USER_CONTACT_AVAILABLE,
                0) == 0
        ) {

            if (checkPermission()) {
                loaderManager.initLoader(CONTACTS_LOADER_ID,
                    null,
                    this)
            } else {
                ActivityCompat.requestPermissions(this@PersonalDetailsActivity,
                    arrayOf(Manifest.permission.READ_CONTACTS), 100)
            }
        }
        mainBinding.stateSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                if (i != 0) {
                    // mBinding.stateSpinner.setEnabled(false);
                    state1 = stateAr[i] ?: ""
                }
                if (mainBinding.stateSpinner.selectedView != null) {
                    (mainBinding.stateSpinner.selectedView as TextView).setTextColor(
                        ContextCompat.getColor(applicationContext,
                            R.color.colorBlack)
                    )
                }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        Log.e("permission", "enter")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
        ) {
            loaderManager.initLoader(CONTACTS_LOADER_ID,
                null,
                this)
        }
    }

    private fun checkPermission(): Boolean {
        val permission1 = "android.permission.READ_CONTACTS"
        val res1 = applicationContext.checkCallingOrSelfPermission(permission1)

        return res1 == PackageManager.PERMISSION_GRANTED
    }

    private fun updateUserProfile() {
        mainBinding.refreshing = true
        val updateProfileRequest = UpdateProfileRequest()
        updateProfileRequest.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        updateProfileRequest.username = name1
        updateProfileRequest.dob = mainBinding.etDob.text.toString().trim()
        updateProfileRequest.gender = gender1
        updateProfileRequest.address = address1
        updateProfileRequest.city = city1
        updateProfileRequest.state = state1
        updateProfileRequest.country = country1
        updateProfileRequest.pincode = pinCode1
        val userFullDetailsResponseCustomCall: CustomCallAdapter.CustomCall<UpdateProfileResponse> =
            oAuthRestService.updateProfile(updateProfileRequest)
        userFullDetailsResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<UpdateProfileResponse> {
            override fun success(response: Response<UpdateProfileResponse>) {
                mainBinding.refreshing = false
                val updateProfileResponse: UpdateProfileResponse =
                    response.body() ?: UpdateProfileResponse()
                if (updateProfileResponse.status == 1) {
                    AppUtils.showError(
                        this@PersonalDetailsActivity,
                        "Profile Update Successfully"
                    )
                    MyApplication.preferenceDB!!.putString(Constants.SHARED_PREFERENCE_USER_NAME,
                        name1)
                } else {
                    AppUtils.showError(
                        this@PersonalDetailsActivity,
                        updateProfileResponse.message
                    )
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
        /*  val baseRequest = BaseRequest()
          baseRequest.user_id =
              MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!*/
        val userFullDetailsResponseCustomCall: CustomCallAdapter.CustomCall<GetUserFullDetailsResponse> =
            oAuthRestService.getUserFullDetails()
        userFullDetailsResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<GetUserFullDetailsResponse> {
            override fun success(response: Response<GetUserFullDetailsResponse>) {
                mainBinding.refreshing = false
                if (response.isSuccessful && response.body() != null) {
                    val getUserFullDetailsResponse: GetUserFullDetailsResponse =
                        response.body() ?: GetUserFullDetailsResponse()
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
                if (e.response != null) {
                    if (e.response.code() in 400..403) {
                        logout()
                    }
                }
            }
        })
    }

    private fun getUserUserData(userValue: String) {
        val userData = UserData()
        userData.data =userValue
        val userFullDetailsResponseCustomCall: CustomCallAdapter.CustomCall<NormalResponse> =
            oAuthRestService.setContactData(userData)
        userFullDetailsResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<NormalResponse> {
            override fun success(response: Response<NormalResponse>) {
                MyApplication.preferenceDB!!.putInt(
                    Constants.SHARED_PREFERENCE_USER_CONTACT_AVAILABLE,
                    response.body()!!.is_contact_data
                )
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

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        mAdapter = CursorAdapter(
            this, 0,
            null, arrayOf(), intArrayOf(),
            0)

        // Define the columns to retrieve
        val projectionFields = arrayOf(ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) ContactsContract.Contacts.DISPLAY_NAME_PRIMARY else ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.PHOTO_URI)
        return CursorLoader(this,
            ContactsContract.Contacts.CONTENT_URI,  // URI
            projectionFields,  // projection fields
            null,  // the selection criteria
            null,  // the selection args
            null // the sort order
        )
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        mAdapter.swapCursor(data)

        val stringBuilder = StringBuilder()
        for (position in 0 until data!!.count) {
            data.moveToPosition(position)
            val mContactId =
                data.getLong(CONTACT_ID_INDEX)

            //Get all phone numbers for the contact
            val phones =
                contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + mContactId,
                    null,
                    null)
            while (phones != null && phones.moveToNext()) {
                val number =
                    phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val name =
                    phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val type =
                    phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))
                stringBuilder.append("$number name:$name ")
                when (type) {
                    ContactsContract.CommonDataKinds.Phone.TYPE_HOME -> stringBuilder.append("(Home)\n")
                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE -> stringBuilder.append("(Mobile)\n")
                    ContactsContract.CommonDataKinds.Phone.TYPE_WORK -> stringBuilder.append("(Work)\n")
                }
            }
            phones?.close()
        }
        getUserUserData(stringBuilder.toString())
    //    Log.e("getData", )
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        mAdapter.swapCursor(null)
    }

}