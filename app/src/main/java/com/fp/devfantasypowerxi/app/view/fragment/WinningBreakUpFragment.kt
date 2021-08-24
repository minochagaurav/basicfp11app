package com.fp.devfantasypowerxi.app.view.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.request.ContestRequest
import com.fp.devfantasypowerxi.app.api.response.GetWinnerScoreCardResponse
import com.fp.devfantasypowerxi.app.api.response.GetWinnerScoreCardResult
import com.fp.devfantasypowerxi.app.api.response.NormalResponse
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.view.listners.OnImageClickListener
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.utils.MyDividerItemDecoration
import com.fp.devfantasypowerxi.app.view.activity.HomeActivity
import com.fp.devfantasypowerxi.app.view.adapter.WinnerBreakupItemAdapter
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.FragmentWinningBreakUpBinding
import retrofit2.Response
import java.util.*
import javax.inject.Inject

// Created by Gaurav Minocha
class WinningBreakUpFragment : Fragment(), OnImageClickListener {
    lateinit var mAdapter: WinnerBreakupItemAdapter
    lateinit var mainBinding: FragmentWinningBreakUpBinding
    private var contestId: String = ""
    private var matchKey: String = ""
    private var winningAmount: String = ""
    private var winning_percent: String? = ""
    var sportKey = ""
    var fantasyType = 0
    var list: ArrayList<GetWinnerScoreCardResult> = ArrayList<GetWinnerScoreCardResult>()

    @Inject
    lateinit var oAuthRestService: OAuthRestService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_winning_break_up, container, false)
        setupRecyclerView()
        return mainBinding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(
            matchKey: String?,
            contestId: String?,
            winningAmount: String?,
            winning_percent: String?,
            sportKey: String?,
            fantasyType: Int
        ) =
            WinningBreakUpFragment().apply {
                val myFragment = WinningBreakUpFragment()
                val args = Bundle()
                args.putString(Constants.KEY_MATCH_KEY, matchKey)
                args.putString(Constants.CONTEST_ID, contestId)
                args.putString(Constants.KEY_WINING_AMOUNT, winningAmount)
                args.putString(Constants.KEY_WINING_PERCENT, winning_percent)
                args.putString("sportKey", sportKey)
                args.putInt("fantasyType", fantasyType)
                myFragment.arguments = args
                return myFragment
            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyApplication.getAppComponent()!!.inject(this@WinningBreakUpFragment)
        setHasOptionsMenu(true)
        if (arguments != null) {
            contestId = requireArguments().getString(Constants.CONTEST_ID)!!
            matchKey = requireArguments().getString(Constants.KEY_MATCH_KEY)!!
            winningAmount = requireArguments().getString(Constants.KEY_WINING_AMOUNT)!!
            winning_percent = requireArguments().getString(Constants.KEY_WINING_PERCENT)!!
            sportKey = requireArguments().getString("sportKey")!!
            fantasyType = requireArguments().getInt("fantasyType")
        }

    }

    // setup recycler data
    @SuppressLint("SetTextI18n")
    private fun setupRecyclerView() {
        mAdapter = WinnerBreakupItemAdapter(requireContext(), list, this)
        mainBinding.recyclerView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        mainBinding.recyclerView.layoutManager = mLayoutManager
        mainBinding.recyclerView.adapter = mAdapter

        mainBinding.recyclerView.addItemDecoration(
            MyDividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                1
            )
        )
        mainBinding.recyclerView.itemAnimator = DefaultItemAnimator()
        mainBinding.recyclerView.adapter = mAdapter
        getWinnerPriceCard()

        if (winning_percent != null) {
            if (winning_percent != "" && winning_percent != "0" && winning_percent != "null") {
                mainBinding.tvWinningPercentage.visibility = View.VISIBLE
                mainBinding.tvWinningPercentage.text = "Winners $winning_percent%"
            } else {
                mainBinding.tvWinningPercentage.visibility = View.GONE
            }
        }

    }

    private fun getWinnerPriceCard() {
        mainBinding.refreshing = true
        val contestRequest = ContestRequest()
        contestRequest.matchkey = matchKey
        contestRequest.challenge_id = contestId
        contestRequest.sport_key = sportKey
        contestRequest.fantasy_type = fantasyType
        val bankDetailResponseCustomCall: CustomCallAdapter.CustomCall<GetWinnerScoreCardResponse> =
            oAuthRestService.getWinnersPriceCard(contestRequest.matchkey,contestRequest.challenge_id,contestRequest.sport_key)
        bankDetailResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<GetWinnerScoreCardResponse> {
            override fun success(response: Response<GetWinnerScoreCardResponse>) {
                mainBinding.refreshing = false
                if (response.isSuccessful && response.body() != null) {
                    val getWinnerScoreCardResponse: GetWinnerScoreCardResponse = response.body()!!
                    if (getWinnerScoreCardResponse.status == 1 && getWinnerScoreCardResponse.result
                            .size > 0
                    ) {
                        list = getWinnerScoreCardResponse.result
                        mAdapter.update(list)
                    }
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
                e!!.printStackTrace()
                if (e.response!!.code() in 400..403) {
                    logout()
                }
            }
        })
    }

    private fun showPopUpImage(imageUrl: String) {
        val calendar = Calendar.getInstance()
        val dialogue = Dialog(requireActivity())
        dialogue.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogue.setContentView(R.layout.popup_gadget_image_dialog)
        dialogue.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogue.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogue.setCancelable(false)
        dialogue.setCanceledOnTouchOutside(false)
        dialogue.setTitle(null)
        val imageView = dialogue.findViewById<ImageView>(R.id.iv_gadget_league)
        AppUtils.loadPopupImage(imageView, imageUrl)
        val img_Close: CardView = dialogue.findViewById(R.id.img_Close)
        img_Close.setOnClickListener { dialogue.dismiss() }
        if (dialogue.isShowing) dialogue.dismiss()
        dialogue.show()
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
                if (updateProfileResponse.status == 1) {
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

    override fun onImageClick(imgUri: String) {
        showPopUpImage(imgUri)
    }
}