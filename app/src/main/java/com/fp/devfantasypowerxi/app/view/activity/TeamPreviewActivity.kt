package com.fp.devfantasypowerxi.app.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.databinding.ActivityInviteFriendBinding
import com.fp.devfantasypowerxi.databinding.ActivityTeamPreviewBinding
// made by Gaurav Minocha
class TeamPreviewActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityTeamPreviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_team_preview)

        // close activity
        mainBinding.icClose.setOnClickListener {
            finish()
        }
    }
}