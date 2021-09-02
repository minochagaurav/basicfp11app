package com.fp.devfantasypowerxi.app.view.activity

import android.content.ClipData
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.ClipboardManager
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivityInviteFriendBinding
// made by Gaurav Minocha
class InviteFriendActivity : AppCompatActivity() {
    var userReferCode = ""
    lateinit var mainBinding: ActivityInviteFriendBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_invite_friend)
        initialize()
        userReferCode = MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_REFER_CODE)!!
        mainBinding.code.text = userReferCode
        mainBinding.btnInvite.setOnClickListener { view ->
            val shareBody =
                ("Here's FC 100 to play fantasy cricket with me On FantasyPower11" +
                        " And Also Earn Unlimited Real Cash with referred Friend for Lifetime . " +
                        userReferCode + " to download FantasyPower11 app & use My code"
                        + userReferCode + " To Register")
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(sharingIntent, "Share via"))
        }

        mainBinding.copyImage.setOnClickListener { view -> setClipboard(userReferCode) }

        mainBinding.tvTc.setOnClickListener { view ->
            startActivity(
                Intent(
                    MyApplication.appContext,
                    WebActivity::class.java
                ).putExtra("title", "Terms & Conditions").putExtra("type", "terms/")
            )
        }

        mainBinding.tvFaq.setOnClickListener { view ->
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

}