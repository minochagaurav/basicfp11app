package com.fp.devfantasypowerxi.app.view.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Base64.encode
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.common.utils.Constants.Companion.TAG_CRICKET
import com.fp.devfantasypowerxi.common.utils.Constants.Companion.TAG_FOOTBALL
import com.fp.devfantasypowerxi.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.inject.Inject


// made by Gaurav Minocha
class HomeActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityHomeBinding
    private val dialog: AlertDialog? = null
    lateinit var navigation: BottomNavigationView
     var actionMenu: FloatingActionMenu?= null
    var tag = "1"
    var fragment: Fragment? = null

    @Inject
    lateinit var oAuthRestService: OAuthRestService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        MyApplication.getAppComponent()!!.inject(this@HomeActivity)
        initialize()
      //  PrintHashKey()
        fragment = HomeFragment()
        mainBinding.fantasyTypeTab.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // Fragment fragment = null;
                when (tab.position) {
                    0 -> {fragment = HomeFragment()
                        MyApplication.preferenceDB!!.putString(Constants.SF_SPORT_KEY,TAG_CRICKET)}

                    1 -> {fragment = HomeFragment()
                        MyApplication.preferenceDB!!.putString(Constants.SF_SPORT_KEY, TAG_FOOTBALL)}
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
                        MyApplication.preferenceDB!!.putString(Constants.SF_SPORT_KEY,TAG_CRICKET)
                        mainBinding.ivLogo.visibility = View.VISIBLE
                        mainBinding.fantasyTypeTab.visibility = View.VISIBLE
                    }
                }
                R.id.navigation_my_matches -> {
                    if (actionMenu != null)
                        actionMenu!!.close(true)
                    if (supportFragmentManager.findFragmentById(R.id.fragment_container) !is MyMatchesFragment) {
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

    @SuppressLint("PackageManagerGetSignatures")
    private fun PrintHashKey() {
        try {
            val info = packageManager.getPackageInfo(
                "com.fp.devfantasypowerxi",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                /*        Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                System.out.print("Key hash is : "+Base64.encodeToString(md.digest(), Base64.DEFAULT));*/
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
    }

    // setup for layout on toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
        // set toolbar title data on change
    private fun setToolBarTitle(title: String?) {
        if (supportActionBar != null) {
            //     getSupportActionBar().setDisplayShowTitleEnabled(true);
            supportActionBar!!.title = title
            // getSupportActionBar().getTitle().
        }
    }

    // animation for cricket ,football,basketball circle
    private fun fabButtonClick() {
        val itemBuilder: SubActionButton.Builder = SubActionButton.Builder(this@HomeActivity)
        val button1: SubActionButton
        val button2: SubActionButton
        val button3: SubActionButton

        val itemIcon = ImageView(this@HomeActivity)
        button1 = itemBuilder.setContentView(itemIcon).build()
        button1.background = ContextCompat.getDrawable(applicationContext, R.drawable.cricket)
        val itemIcon1 = ImageView(this@HomeActivity)
        button2 = itemBuilder.setContentView(itemIcon1).build()
        button2.background = ContextCompat.getDrawable(applicationContext, R.drawable.football)
      //  val itemIcon2 = ImageView(this@HomeActivity)
      //  button3 = itemBuilder.setContentView(itemIcon2).build()
     //   button3.background = ContextCompat.getDrawable(applicationContext, R.drawable.basket_ball)
        val radius: Int
        val startingAngle: Int
        val endAngle: Int
        val smallBtnWidth: Int
        val smallBtnHeight: Int
        if (AppUtils.getWidth(this) > 800) {
            radius = 160
            startingAngle = 220
            endAngle = 320
            smallBtnWidth = 130
            smallBtnHeight = 130
        } else {
            radius = 117
            startingAngle = 220
            endAngle = 320
            smallBtnWidth = 120
            smallBtnHeight = 120
        }
        actionMenu = FloatingActionMenu.Builder(this@HomeActivity)
            .addSubActionView(button1, smallBtnWidth, smallBtnHeight)
            .addSubActionView(button2, smallBtnWidth, smallBtnHeight)
          //  .addSubActionView(button3, smallBtnWidth, smallBtnHeight)
            .setRadius(radius)
            .setStartAngle(startingAngle)
            .setEndAngle(endAngle)
            .attachTo(mainBinding.fab)
            .build()

//        actionMenu.open(true);
        actionMenu!!.setStateChangeListener(object : FloatingActionMenu.MenuStateChangeListener {
            override fun onMenuOpened(floatingActionMenu: FloatingActionMenu?) {

            }

            override fun onMenuClosed(floatingActionMenu: FloatingActionMenu?) {
                // Toast.makeText(HomeActivity.this, "Close", Toast.LENGTH_SHORT).show();
            }
        })
        button1.setOnClickListener {
            //   AppUtils.saveSportsKey(Constants.TAG_CRICKET)
            actionMenu!!.close(true)
            mainBinding.fab.setImageResource(R.drawable.new_home_cricket)
            if (supportFragmentManager.findFragmentById(R.id.fragment_container) is HomeFragment) {
                val homeFragment: HomeFragment? =
                    supportFragmentManager.findFragmentById(R.id.fragment_container) as HomeFragment?
            } else if (supportFragmentManager.findFragmentById(R.id.fragment_container) is MyMatchesFragment) {
                val myMatchesFragment: MyMatchesFragment? =
                    supportFragmentManager.findFragmentById(R.id.fragment_container) as MyMatchesFragment?
            }
            // button1.setBackground(getResources().getDrawable(R.drawable.cricket_active));
        }
        button2.setOnClickListener {
            // AppUtils.saveSportsKey(Constants.TAG_FOOTBALL)
            actionMenu!!.close(true)
            mainBinding.fab.setImageResource(R.drawable.new_home_football)
            if (supportFragmentManager.findFragmentById(R.id.fragment_container) is HomeFragment) {
                val homeFragment: HomeFragment? =
                    supportFragmentManager.findFragmentById(R.id.fragment_container) as HomeFragment?

            } else if (supportFragmentManager.findFragmentById(R.id.fragment_container) is MyMatchesFragment) {
                val myMatchesFragment: MyMatchesFragment? =
                    supportFragmentManager.findFragmentById(R.id.fragment_container) as MyMatchesFragment?

            }


            //   button2.setBackground(getResources().getDrawable(R.drawable.football_acitve));
        }
   /*     button3.setOnClickListener(View.OnClickListener {
            // AppUtils.saveSportsKey(Constants.TAG_BASKETBALL)
            actionMenu!!.close(true)
            mainBinding.fab.setImageResource(R.drawable.new_home_baskbatball)
            if (supportFragmentManager.findFragmentById(R.id.fragment_container) is HomeFragment) {
                val homeFragment: HomeFragment? =
                    supportFragmentManager.findFragmentById(R.id.fragment_container) as HomeFragment?
            } else if (supportFragmentManager.findFragmentById(R.id.fragment_container) is MyMatchesFragment) {
                val myMatchesFragment: MyMatchesFragment? =
                    supportFragmentManager.findFragmentById(R.id.fragment_container) as MyMatchesFragment?
            }


            // button3.setBackground(getResources().getDrawable(R.drawable.basket_ball_acitve));
        })*/
    }
}