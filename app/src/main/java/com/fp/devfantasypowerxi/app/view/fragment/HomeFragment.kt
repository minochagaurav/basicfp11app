package com.fp.devfantasypowerxi.app.view.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.response.*
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.activity.HomeActivity
import com.fp.devfantasypowerxi.app.view.activity.UpComingContestActivity
import com.fp.devfantasypowerxi.app.view.adapter.MatchItemAdapter
import com.fp.devfantasypowerxi.app.view.adapter.SliderBannerAdapter
import com.fp.devfantasypowerxi.app.view.listners.OnMatchItemClickListener
import com.fp.devfantasypowerxi.app.view.viewmodel.UpComingMatchListViewModel
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.api.Resource
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.gson.Gson
import retrofit2.Response
import java.util.*
import javax.inject.Inject

// created by Gaurav Minocha
class HomeFragment : Fragment(), OnMatchItemClickListener {
    lateinit var mainBinding: FragmentHomeBinding
    lateinit var mAdapter: MatchItemAdapter
    var serverDate = ""
    var bannerListItems = ArrayList<Banner>()
    var app_download_url = ""
    var app_download_referral_url = ""
    var sportKey = ""
    var currentPage = 0
    private lateinit var timer: Timer
    val DELAY_MS: Long = 600
    var sportList = ArrayList<SportType>()
    private var fantasyTypeList = ArrayList<FantasyType>()
    private val PERIOD_MS: Long = 5000
    var currentVersion = 0
    var isSwipeTrue = false
    private var onlineVersion = 0
    private var list = ArrayList<MatchListResult>()
    private lateinit var upComingMatchListViewModel: UpComingMatchListViewModel

    @Inject
    lateinit var oAuthRestService: OAuthRestService
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mainBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        setAdapter()
        return mainBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity is HomeActivity) {
            sportKey = AppUtils.saveSportKey
        }
        upComingMatchListViewModel = UpComingMatchListViewModel().create(this)
        MyApplication.getAppComponent()!!.inject(this@HomeFragment)
        MyApplication.getAppComponent()!!.inject(upComingMatchListViewModel)
        getBannerList()
        setupRecyclerView()

        mainBinding.viewPagerBanner.clipToPadding = false
        mainBinding.viewPagerBanner.setPadding(40, 0, 40, 0)
        mainBinding.viewPagerBanner.pageMargin = 20

        getData(upComingMatchListViewModel.getMatchListData())
    }

    private fun setupRecyclerView() {
        mainBinding.swipeRefreshLayout.setOnRefreshListener {
            isSwipeTrue = true
            upComingMatchListViewModel.getMatchListData().removeObservers(this)
            getData(upComingMatchListViewModel.getMatchListData())
        }
    }

    private fun getBannerList() {
        val bankDetailResponseCustomCall: CustomCallAdapter.CustomCall<BannerListResponse> =
            oAuthRestService.getBannerList()
        bankDetailResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<BannerListResponse> {
            override fun success(response: Response<BannerListResponse>) {
                serverDate = response.headers()["Date"].toString()
                if (response.isSuccessful && response.body() != null) {
                    val bannerListResponse: BannerListResponse = response.body()!!
                    if (bannerListResponse.status == 1 && bannerListResponse.result.size > 0
                    ) {
                        bannerListItems = bannerListResponse.result
                        if (bannerListResponse.in_app_image != "")
                        //showPopUpImage(bannerListResponse.in_app_image)
                            if (bannerListResponse.all_announcement != "") {
                                mainBinding.llTopLayout.visibility = View.VISIBLE
                                mainBinding.tvAnn.text = bannerListResponse.all_announcement
                            } else {
                                mainBinding.llTopLayout.visibility = View.GONE
                            }
                        val animationToLeft: Animation = TranslateAnimation(700F, -1000F, 0F, 0F)
                        animationToLeft.duration = 17000
                        animationToLeft.repeatMode = Animation.RESTART
                        animationToLeft.repeatCount = Animation.INFINITE
                        mainBinding.tvAnn.animation = animationToLeft
                        if (activity != null) mainBinding.viewPagerBanner.adapter =
                            SliderBannerAdapter(
                                activity!!, bannerListItems, false
                            )
                        autoScroll()
                        //Set sport type dynamic with api
                        setSportsCategory(bannerListResponse.sport_types)
                        /*   app_download_url = bannerListResponse.app_download_url
                           app_download_referral_url =
                               bannerListResponse.app_referral_url
                           onlineVersion = bannerListResponse.version
                           currentVersion = BuildConfig.VERSION_CODE
                           if (currentVersion < onlineVersion) {
                               val builder = AlertDialog.Builder(
                                   activity
                               )
                               builder.setMessage("New Version for Fantasy Power 11 is available for download. Kindly update for latest features.")
                                   .setCancelable(false)
                                   .setPositiveButton(
                                       "Update"
                                   ) { _, _ ->

                                   }
                               val alert = builder.create()
                               alert.setCanceledOnTouchOutside(false)
                               alert.setOnCancelListener { activity!!.finish() }
                               alert.show()
                           }*/
                    }
                }
            }

            override fun failure(e: ApiException?) {
                e!!.printStackTrace()
            }
        })
    }

    private fun setSportsCategory(list: ArrayList<SportType>) {
        val jsonList = Gson().toJson(list)
        MyApplication.preferenceDB!!.putString(Constants.SHARED_SPORTS_LIST, jsonList)
        sportList = list
        if (list.size > 1) {
            mainBinding.sportTab.visibility = View.VISIBLE
        } else {
            mainBinding.sportTab.visibility = View.GONE
        }
        fantasyTypeList = list[0].fantasy_type
        for (sportType in list) {
            mainBinding.sportTab.addTab(
                mainBinding.sportTab.newTab().setText(sportType.sport_name)
            )
        }
/*

        mainBinding.sportTab.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val view = tab.customView
                val textView = view!!.findViewById<TextView>(R.id.tab)
                textView.setTextColor(resources.getColor(R.color.colorPrimary))
                AppUtils.saveSportsKey(sportKey)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val view = tab.customView
                val textView = view!!.findViewById<TextView>(R.id.tab)
                textView.setTextColor(resources.getColor(R.color.unselected_tab))
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
*/

     /*   for (i in list.indices) {
            val tabOne =
                LayoutInflater.from(activity).inflate(R.layout.custom_tab, null) as TextView
            tabOne.text = list[i].sport_name
            if (list[i].sport_name.equals(Constants.TAG_CRICKET, ignoreCase = true)) {
                tabOne.setCompoundDrawablesWithIntrinsicBounds(0,
                    R.drawable.cricket_tab_selector,
                    0,
                    0)
            } else if (list[i].sport_name.equals(Constants.TAG_FOOTBALL, ignoreCase = true)) {
                tabOne.setCompoundDrawablesWithIntrinsicBounds(0,
                    R.drawable.football_tab_selector,
                    0,
                    0)
            }
            mainBinding.sportTab.addTab(mainBinding.sportTab.newTab()
                .setText(list[i].sport_name).setCustomView(tabOne))
//            fragmentHomeBinding.sportTab.getTabAt(i).setCustomView(tabOne);
        }*/
    }

    fun autoScroll() {
        val handler = Handler()
        val update = Runnable {
            if (currentPage == bannerListItems.size) {
                currentPage = 0
            }
            mainBinding.viewPagerBanner.setCurrentItem(currentPage, true)
            currentPage += 1
        }
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, DELAY_MS, PERIOD_MS)
    }

    // recycle the data
    private fun setAdapter() {
        val activity = activity
        if (activity != null) {
            mAdapter = MatchItemAdapter(
                this@HomeFragment,
                requireActivity() as AppCompatActivity, list
            )
            mainBinding.recyclerView.setHasFixedSize(true)
            val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(
                activity
            )
            mainBinding.recyclerView.layoutManager = mLayoutManager
            mainBinding.recyclerView.adapter = mAdapter
        }
    }

    private fun getData(liveData: LiveData<Resource<MatchListResponse>>) {
        mainBinding.lifecycleOwner = this@HomeFragment
        val baseRequest = BaseRequest()
        baseRequest.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        baseRequest.sport_key = AppUtils.saveSportKey
        upComingMatchListViewModel.loadMatchListRequest(baseRequest)
        liveData.observe(
            mainBinding.lifecycleOwner!!,
            { arrayListResource: Resource<MatchListResponse> ->
                Log.d("Status ", "" + arrayListResource.status)
                when (arrayListResource.status) {
                    Resource.Status.LOADING -> {
                        mainBinding.refreshing = true
                        if (isSwipeTrue) mainBinding.swipeRefreshLayout.isRefreshing = true
                    }
                    Resource.Status.ERROR -> {
                        mainBinding.refreshing = false
                        if (isSwipeTrue) mainBinding.swipeRefreshLayout.isRefreshing = false
                        if (arrayListResource.exception!!.response != null) {
                            if (arrayListResource.exception.response!!
                                    .code() in 400..403
                            ) {
                                logout()
                            } else {
                                Toast.makeText(
                                    MyApplication.appContext,
                                    arrayListResource.exception.getErrorModel().errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                MyApplication.appContext,
                                arrayListResource.exception.getErrorModel().errorMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    Resource.Status.SUCCESS -> {

                        if (isSwipeTrue) mainBinding.swipeRefreshLayout.isRefreshing = false
                        isSwipeTrue = false
                        mainBinding.refreshing = false
                        if (arrayListResource.data!!.status == 1) {
                            val activity = activity
                            if (activity!= null) {
                                list = arrayListResource.data.result
                                mAdapter = MatchItemAdapter(
                                    this@HomeFragment,
                                    requireActivity() as AppCompatActivity, list
                                )
                                mainBinding.recyclerView.setHasFixedSize(true)
                                val mLayoutManager: RecyclerView.LayoutManager =
                                    LinearLayoutManager(activity)
                                mainBinding.recyclerView.layoutManager = mLayoutManager
                                mainBinding.recyclerView.adapter = mAdapter
                            }
                            if (list.size > 0) {
                                mainBinding.rlNoMatch.visibility = View.GONE
                            } else {
                                mainBinding.rlNoMatch.visibility = View.VISIBLE
                            }
                        } else {
                            logout()
                            Toast.makeText(
                                MyApplication.appContext,
                                arrayListResource.data.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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
                val updateProfileResponse = response.body()!!
                if (updateProfileResponse.status == 1 || updateProfileResponse.status == 0) {
                    val activity = activity
                    if (activity != null) {
                        MyApplication.logout(activity)
                    }
                } else {
                    AppUtils.showError(
                        activity as HomeActivity,
                        updateProfileResponse.message
                    )
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
            }
        })
    }


    override fun onMatchItemClick(
        matchKey: String,
        teamVsName: String,
        teamFirstUrl: String,
        teamSecondUrl: String,
        date: String?,
    ) {
        Log.e("Upcoming", matchKey)
        val intent = Intent(activity, UpComingContestActivity::class.java)
        intent.putExtra(Constants.KEY_MATCH_KEY, matchKey)
        intent.putExtra(Constants.KEY_TEAM_VS, teamVsName)
        intent.putExtra(Constants.KEY_TEAM_FIRST_URL, teamFirstUrl)
        intent.putExtra(Constants.KEY_TEAM_SECOND_URL, teamSecondUrl)
        intent.putExtra(Constants.KEY_STATUS_HEADER_TEXT, date)
        intent.putExtra(Constants.SPORT_KEY, sportKey)
        intent.putParcelableArrayListExtra(Constants.KEY_FANTASY_TYPE_LIST, fantasyTypeList)
        startActivity(intent)
    }
}