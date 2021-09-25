package com.fp.devfantasypowerxi.app.view.activity

import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.response.NormalResponse
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivityInviteFriendBinding
import retrofit2.Response
import javax.inject.Inject

// made by Gaurav Minocha
class InviteFriendActivity : AppCompatActivity() {
    private var userReferCode = ""
    lateinit var mainBinding: ActivityInviteFriendBinding

    @Inject
    lateinit var oAuthRestService: OAuthRestService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_invite_friend)
        MyApplication.getAppComponent()!!.inject(this@InviteFriendActivity)
        initialize()
        userReferCode =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_REFER_CODE)!!
        mainBinding.code.text = userReferCode
        mainBinding.btnInvite.setOnClickListener {
            shareApp()

        }

        mainBinding.copyImage.setOnClickListener { setClipboard(userReferCode) }

        mainBinding.tvTc.setOnClickListener {
            startActivity(
                Intent(
                    MyApplication.appContext,
                    WebActivity::class.java
                ).putExtra("title", "Terms & Conditions").putExtra("type", "terms/")
            )
        }

        mainBinding.tvFaq.setOnClickListener {
            startActivity(
                Intent(
                    MyApplication.appContext,
                    WebActivity::class.java
                ).putExtra("title", "Terms & Conditions").putExtra("type", "terms/")
            )
        }
    }

    private fun setClipboard(text: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
        AppUtils.showError(this@InviteFriendActivity, "Invite code copy.")
    }

    // initialize toolbar
    private fun initialize() {
        setSupportActionBar(mainBinding.myToolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = getString(R.string.refer_your_friends)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }

    }

    // toolbar click event
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun shareApp() {
        mainBinding.refreshing = true

        val userFullDetailsResponseCustomCall: CustomCallAdapter.CustomCall<NormalResponse> =
            oAuthRestService.shareApp()
        userFullDetailsResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<NormalResponse> {
            override fun success(response: Response<NormalResponse>) {
                mainBinding.refreshing = false
                val updateProfileResponse: NormalResponse = response.body()!!
                if (updateProfileResponse.status == 1) {
                   /* Toast.makeText(applicationContext,
                        updateProfileResponse.message,
                        Toast.LENGTH_SHORT).show()*/
                    val shareBody =
                        ("Play Free Fantasy Cricket and football on Fantasy Power 11 sign up and get 100 coins for free to start use . " +
                                "https://play.google.com/store/apps/details?id=com.fp.devfantasypowerxi want to Join Paid leagues download our paid apps https://fantasypower11.com/")
                    /* ("Here's (Fantasy Coins) FC 100 to play fantasy cricket with me On FantasyPower11 and earn FC 100 on share " +
                             userReferCode + " to download FantasyPower11 app & use My code"
                             + userReferCode + " To Register")*/
                    val sharingIntent = Intent(Intent.ACTION_SEND)
                    sharingIntent.type = "text/plain"
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
                    startActivity(Intent.createChooser(sharingIntent, "Share via"))
                } else {
                    AppUtils.showError(
                        this@InviteFriendActivity,
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