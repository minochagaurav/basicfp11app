package com.fp.devfantasypowerxi.app.view.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.facebook.drawee.backends.pipeline.Fresco
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.fragment.AddCashFragment
import com.fp.devfantasypowerxi.app.view.fragment.HomeFragment
import com.fp.devfantasypowerxi.app.view.fragment.MoreFragment
import com.fp.devfantasypowerxi.app.view.fragment.MyMatchesFragment
import com.fp.devfantasypowerxi.common.utils.Constants.Companion.TAG_CRICKET
import com.fp.devfantasypowerxi.common.utils.Constants.Companion.TAG_FOOTBALL
import com.fp.devfantasypowerxi.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*
import javax.inject.Inject


// made by Gaurav Minocha
class HomeActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityHomeBinding
    lateinit var navigation: BottomNavigationView
    private var actionMenu: FloatingActionMenu? = null
    private var tag = "1"
    var fragment: Fragment? = null
    var isAllFabVisible: Boolean = false

    @Inject
    lateinit var oAuthRestService: OAuthRestService

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        MyApplication.getAppComponent()!!.inject(this@HomeActivity)
        initialize()
        //  PrintHashKey()
        fragment = HomeFragment()
        AppUtils.saveSportsKey(TAG_CRICKET)
        //  mainBinding.fabButton.shrink()
        /*  mainBinding.fabButton.setOnClickListener {
              showPopUpImage()
          }
  */

        mainBinding.fabLive.setOnClickListener {


            val isAppInstalled: Boolean = appInstalledOrNot("com.img.fantasypowerxi")
            if (isAppInstalled) {
                //This intent will help you to launch if the package is already installed
                val launchIntent = packageManager
                    .getLaunchIntentForPackage("com.img.fantasypowerxi")
                startActivity(launchIntent)
            } else {
                val shareLink = "https://fantasypower11.com/"
                val share = Intent(Intent.ACTION_VIEW)
                share.data = Uri.parse(shareLink)
                startActivity(share)
            }
        }
        mainBinding.fabScore.setOnClickListener {
            val isAppInstalled: Boolean = appInstalledOrNot("com.fp.cricbull")
            if (isAppInstalled) {
                //This intent will help you to launch if the package is already installed
                val launchIntent = packageManager
                    .getLaunchIntentForPackage("com.fp.cricbull")
                startActivity(launchIntent)
            } else {
                val shareLink = "https://play.google.com/store/apps/details?id=com.fp.cricbull"
                val share = Intent(Intent.ACTION_VIEW)
                share.data = Uri.parse(shareLink)
                startActivity(share)
            }

        }
        mainBinding.fabShopping.setOnClickListener {

            val isAppInstalled: Boolean = appInstalledOrNot("com.fansfab.com")
            if (isAppInstalled) {
                //This intent will help you to launch if the package is already installed
                val launchIntent = packageManager
                    .getLaunchIntentForPackage("com.fansfab.com")
                startActivity(launchIntent)
            } else {
                val shareLink = "https://play.google.com/store/apps/details?id=com.fansfab.com"
                val share = Intent(Intent.ACTION_VIEW)
                share.data = Uri.parse(shareLink)
                startActivity(share)
            }

        }
        menu_labels_right.setClosedOnTouchOutside(true)
        menu_labels_right.setMenuButtonColorNormalResId(R.color.colorPrimary)

        mainBinding.fantasyTypeTab.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        fragment = HomeFragment()
                        AppUtils.saveSportsKey(TAG_CRICKET)
                    }
                    1 -> {
                        fragment = HomeFragment()
                        AppUtils.saveSportsKey(TAG_FOOTBALL)
                    }
                }

                val fm: FragmentManager = supportFragmentManager
                val ft: FragmentTransaction = fm.beginTransaction()
                if (fragment is HomeFragment) {
                    ft.replace(R.id.fragment_container, fragment as HomeFragment)
                }

                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                ft.commit()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

    }
    private fun appInstalledOrNot(uri: String): Boolean {
        val pm = packageManager
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return false
    }
    // setup for fragment container
    private fun loadFragment(fragment: Fragment?): Boolean {
        if (fragment != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment, "1")
                .commit()
            return true
        }
        return false
    }

    // click event for toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.navigation_notification -> {
                startActivity(Intent(this@HomeActivity, NotificationActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // initialize toolbar and bottom navigation's
    private fun initialize() {
        navigation = mainBinding.bottomNavigation
        setSupportActionBar(mainBinding.myToolbar)

        navigation.selectedItemId = R.id.navigation_home
        setToolBarTitle("")
        //   fabButtonClick()
        loadFragment(HomeFragment())
        Fresco.initialize(this)
        mainBinding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            var fragment: Fragment? = null
            when (item.itemId) {
                R.id.navigation_home -> {
                    if (actionMenu != null)
                        actionMenu!!.close(true)
                    if (supportFragmentManager.findFragmentById(R.id.fragment_container) !is HomeFragment) {
                        fragment = HomeFragment()
                        setToolBarTitle("")
                        tag = "1"
                        mainBinding.ivLogo.visibility = View.VISIBLE
                        mainBinding.fantasyTypeTab.visibility = View.VISIBLE
                        if (AppUtils.getSaveSportKey() == TAG_FOOTBALL) {
                            mainBinding.fantasyTypeTab.getTabAt(1)!!.select()
                        } else {
                            mainBinding.fantasyTypeTab.getTabAt(0)!!.select()
                        }
                    }
                }
                R.id.navigation_my_matches -> {
                    if (actionMenu != null)
                        actionMenu!!.close(true)
                    if (supportFragmentManager.findFragmentById(R.id.fragment_container) !is MyMatchesFragment) {
                        MyMatchesFragment.newInstance(0)
                        fragment = MyMatchesFragment()
                        tag = "2"
                        setToolBarTitle(getString(R.string.title_menu_my_matches))
                        mainBinding.ivLogo.visibility = View.GONE
                        mainBinding.fantasyTypeTab.visibility = View.GONE
                    }
                }
                R.id.navigation_add_cash -> {
                    if (actionMenu != null)
                        actionMenu!!.close(true)
                    if (supportFragmentManager.findFragmentById(R.id.fragment_container) !is AddCashFragment) {
                        fragment = AddCashFragment()
                        tag = "3"
                        mainBinding.ivLogo.visibility = View.GONE
                        mainBinding.fantasyTypeTab.visibility = View.GONE
                        setToolBarTitle(getString(R.string.title_menu_add_cash))
                    }
                }
                R.id.navigation_more -> {
                    if (actionMenu != null)
                        actionMenu!!.close(true)
                    if (supportFragmentManager.findFragmentById(R.id.fragment_container) !is MoreFragment) {
                        fragment = MoreFragment()
                        tag = "4"
                        setToolBarTitle(getString(R.string.title_menu_more))
                        mainBinding.ivLogo.visibility = View.GONE
                        mainBinding.fantasyTypeTab.visibility = View.GONE
                    }
                }
            }
            loadFragment(fragment)
        }
    }

    /* @SuppressLint("PackageManagerGetSignatures")
     private fun PrintHashKey() {
         try {
             val info = packageManager.getPackageInfo(
                 "com.fp.devfantasypowerxi",
                 PackageManager.GET_SIGNATURES
             )
             for (signature in info.signatures) {
                 val md = MessageDigest.getInstance("SHA")
                 md.update(signature.toByteArray())
                 *//*        Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                System.out.print("Key hash is : "+Base64.encodeToString(md.digest(), Base64.DEFAULT));*//*
                val hashKey = String(
                    encode(md.digest(), Base64.DEFAULT)
                )
                Log.e("hashkey", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }*/

    // setup for layout on toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // set toolbar title data on change
    private fun setToolBarTitle(title: String?) {
        if (supportActionBar != null) {
            supportActionBar!!.title = title
        }
    }
}