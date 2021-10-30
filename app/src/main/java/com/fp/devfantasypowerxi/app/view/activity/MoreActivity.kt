package com.fp.devfantasypowerxi.app.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.response.NormalResponse
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.dataModel.MoreInfoData
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.utils.MyDividerItemDecoration
import com.fp.devfantasypowerxi.app.view.adapter.MoreItemAdapter
import com.fp.devfantasypowerxi.app.view.interfaces.OnMoreItemClickListener
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivityMoreBinding
import com.fp.devfantasypowerxi.databinding.FragmentMoreBinding
import retrofit2.Response
import java.util.ArrayList
import javax.inject.Inject

class MoreActivity : AppCompatActivity(), OnMoreItemClickListener {
    lateinit var mainBinding: ActivityMoreBinding
    lateinit var mAdapter: MoreItemAdapter

    @Inject
    lateinit var oAuthRestService: OAuthRestService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  setContentView(R.layout.activity_more)

        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_more)
        MyApplication.getAppComponent()!!.inject(this@MoreActivity)
        initialize()
        setupRecyclerView()
    }
    private fun initialize() {
        setSupportActionBar(mainBinding.mytoolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = "More"
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    // setup Recycler data
    private fun setupRecyclerView() {
        val stringTitleArray = resources.getStringArray(R.array.more_menu_title_array)
        //  int[] imgs =    getResources().obtainTypedArray(R.array.more_menu_icon_array);
        val imgs = intArrayOf( /*R.drawable.ic_user_avt,*/
            R.drawable.ic_user_avt,
            R.drawable.ic_more_refer_earn,
            R.drawable.verified_protection,
            /*  R.drawable.ic_winners_detail,*/
            R.drawable.ic_more_fantasy_point,  /*
                R.drawable.ic_more_refer_list,
*/
            R.drawable.ic_privacy_policy,
            R.drawable.docs,
            R.drawable.information,
            R.drawable.find,
            R.drawable.call,
            R.drawable.logout
        )
        val moreInfoDataList: MutableList<MoreInfoData> = ArrayList<MoreInfoData>()
        for (i in stringTitleArray.indices) {
            val moreInfoData = MoreInfoData()
            moreInfoData.name = stringTitleArray[i]
            moreInfoData.resourceId = imgs[i]
            moreInfoDataList.add(moreInfoData)
        }

        mAdapter = MoreItemAdapter(moreInfoDataList, this, false)
        mainBinding.recyclerView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        mainBinding.recyclerView.layoutManager = mLayoutManager

        // adding custom divider line with padding 3dp
        mainBinding.recyclerView.addItemDecoration(
            MyDividerItemDecoration(
                applicationContext,
                LinearLayoutManager.VERTICAL,
                1
            )
        )
        mainBinding.recyclerView.itemAnimator = DefaultItemAnimator()
        mainBinding.recyclerView.adapter = mAdapter
    }

    // item click events
    override fun onMoreItemClick(position: Int, title: String?) {
        when (position) {
            0 -> {
                startActivity(Intent(applicationContext, PersonalDetailsActivity::class.java))
            }
            1 -> {
                startActivity(Intent(applicationContext, InviteFriendActivity::class.java))
            }
            2 -> {
                startActivity(Intent(applicationContext, VerifyAccountActivity::class.java))
            }
            /*  3 -> {
                  startActivity(Intent(activity, ScratchCardHistoryActivity::class.java))
              }*/
            3 -> {
                openWebViewActivity(title, "https://fantasypower11.com/fantasy-point-system")
            }
            4 -> {
                openWebViewActivity(title, "https://fp11games.in/privacypolicy.html")
            }
            5 -> {
                openWebViewActivity(title, "https://fp11games.in/terms.html")
            }
            6 -> {
                openWebViewActivity(title, "https://fp11games.in/aboutus.html")
            }
            7 -> {
                openWebViewActivity(title, "https://fantasypower11.com/how-to-play/")
            }
            8 -> {
                startActivity(Intent(applicationContext, ContactUsActivity::class.java))
            }
            9 -> {
                logout()
            }
        }
    }

    private fun openWebViewActivity(titile: String?, type: String) {
        val intent = Intent(MyApplication.appContext, WebActivity::class.java)
        intent.putExtra("title", titile)
        intent.putExtra("type", type)
        startActivity(intent)
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
           //   val activity = activity
                val updateProfileResponse: NormalResponse = response.body()!!
                if (updateProfileResponse.status == 1 || updateProfileResponse.status == 0) {
                    //if (activity != null) {
                        MyApplication.logout(this@MoreActivity)

                } else {

                        AppUtils.showError(
                            this@MoreActivity ,
                            updateProfileResponse.message
                        )
                  //  }
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
            }
        })
    }
}