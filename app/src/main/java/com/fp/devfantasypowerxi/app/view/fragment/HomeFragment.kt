package com.fp.devfantasypowerxi.app.view.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import com.fp.devfantasypowerxi.BuildConfig
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.response.*
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.activity.HomeActivity
import com.fp.devfantasypowerxi.app.view.activity.ScratchCardHistoryActivity
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
import com.google.gson.Gson
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

// created by Gaurav Minocha
class HomeFragment : Fragment(), OnMatchItemClickListener {
    lateinit var mainBinding: FragmentHomeBinding
    lateinit var mAdapter: MatchItemAdapter
    var findScratchCard = 0
    var serverDate = ""
    var bannerListItems = ArrayList<Banner>()
    var app_download_url = ""
    var app_download_referral_url = ""
    var sportKey = ""
    var currentPage = 0
    lateinit var timer: Timer
    val DELAY_MS: Long = 600
    var sprotList = ArrayList<SportType>()
    var fantasyTypeList = ArrayList<FantasyType>()
    val PERIOD_MS: Long = 5000
    var currentVersion = 0
    var isSwipeTrue = false
    private var onlineVersion = 0
    private var list = ArrayList<MatchListResult>()
    lateinit var upComingMatchListViewModel: UpComingMatchListViewModel

    @Inject
    lateinit var oAuthRestService: OAuthRestService
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        setAdapter()
        return mainBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                            showPopUpImage(bannerListResponse.in_app_image)
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
                        app_download_url = bannerListResponse.app_download_url
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
                                ) { dialog, id ->

                                }
                            val alert = builder.create()
                            alert.setCanceledOnTouchOutside(false)
                            alert.setOnCancelListener { activity!!.finish() }
                            alert.show()
                        }
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
        sprotList = list
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
    }

    fun autoScroll() {
        val handler = Handler()
        val Update = Runnable {
            if (currentPage == bannerListItems.size) {
                currentPage = 0
            }
            mainBinding.viewPagerBanner.setCurrentItem(currentPage, true)
            currentPage += 1
        }
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(Update)
            }
        }, DELAY_MS, PERIOD_MS)
    }

    // recycle the data
    private fun setAdapter() {
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

    private fun getData(liveData: LiveData<Resource<MatchListResponse>>) {
        val baseRequest = BaseRequest()
        baseRequest.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        baseRequest.sport_key = AppUtils.saveSportKey
        upComingMatchListViewModel.loadMatchListRequest(baseRequest)
        liveData.observe(
            viewLifecycleOwner,
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
                        if (arrayListResource.exception!!.response!!
                                .code() >= 400 && arrayListResource.exception.response!!
                                .code() < 404
                        ) {
                            logout()
                        } else {
                            Toast.makeText(
                                MyApplication.appContext,
                                arrayListResource.exception.getErrorModel().errorMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    Resource.Status.SUCCESS -> {
                        if (isSwipeTrue) mainBinding.swipeRefreshLayout.setRefreshing(false)
                        isSwipeTrue = false
                        mainBinding.refreshing = false
                        if (arrayListResource.data!!.status == 1) {
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
                    MyApplication.logout(requireActivity())
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


    private fun showPopUpImage(image: String) {
        val calendar = Calendar.getInstance()
        val currentDay = calendar[Calendar.DAY_OF_MONTH]
        val lastDay: Int = MyApplication.preferenceDB!!.getInt(Constants.SHOW_IN_APP_IMAGE_POPUP, 0)
        if (lastDay != currentDay) {
            MyApplication.preferenceDB!!.putInt(Constants.SHOW_IN_APP_IMAGE_POPUP, currentDay)
            val dialogue = Dialog(requireContext())
            dialogue.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogue.setContentView(R.layout.popup_image_dialog)
            dialogue.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            dialogue.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogue.setCancelable(false)
            dialogue.setCanceledOnTouchOutside(false)
            dialogue.setTitle(null)
            val imageView: SimpleDraweeView = dialogue.findViewById(R.id.image)
            imageView.setImageURI(image)
            val img_Close: CardView = dialogue.findViewById(R.id.img_Close)
            img_Close.setOnClickListener { dialogue.dismiss() }
            if (dialogue.isShowing) dialogue.dismiss()
            dialogue.show()
        }
    }

    override fun onMatchItemClick(
        matchKey: String,
        teamVsName: String,
        teamFirstUrl: String,
        teamSecondUrl: String,
        date: String?
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