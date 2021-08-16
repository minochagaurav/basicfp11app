package com.fp.devfantasypowerxi.app.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.response.MatchListResult
import com.fp.devfantasypowerxi.app.api.response.PlayerListResult
import com.fp.devfantasypowerxi.app.view.activity.CreateTeamActivity
import com.fp.devfantasypowerxi.app.view.adapter.PlayerItemAdapter
import com.fp.devfantasypowerxi.app.view.listners.PlayerItemClickListener
import com.fp.devfantasypowerxi.databinding.FragmentCreateTeamPlayerBinding
import java.util.*

// Created by Gaurav Minocha
class CreateTeamPlayerFragment : Fragment(), PlayerItemClickListener {
    private val list: ArrayList<MatchListResult> = ArrayList<MatchListResult>()
    var mainPlayerList = ArrayList<PlayerListResult>()
    var playerTypeList = ArrayList<PlayerListResult>()
    var type = 0
    var fantasyType = 0

    private var isPointsSorting = false
    private var isCredits = false

    lateinit var mainBinding: FragmentCreateTeamPlayerBinding
    lateinit var playerItemAdapter: PlayerItemAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_create_team_player,
            container,
            false
        )
        setupRecyclerView()


        mainBinding.tvPoints.setOnClickListener {

            if (activity is CreateTeamActivity) {
                (activity as CreateTeamActivity).isPointSelected = true
                (activity as CreateTeamActivity).isCreditSelected = false
            }
            if (isPointsSorting) {
                isPointsSorting = false
                if (activity is CreateTeamActivity) (activity as CreateTeamActivity).isPointsSortingGlobal =
                    false
                playerItemAdapter.sortWithPoints(false)
                mainBinding.ivPointSortImage.setVisibility(View.VISIBLE)
                mainBinding.ivPointSortImage.setImageResource(R.drawable.ic_down_sort)
                mainBinding.ivCreditSortImage.setVisibility(View.INVISIBLE)
            } else {
                isPointsSorting = true
                playerItemAdapter.sortWithPoints(true)
                if (activity is CreateTeamActivity) (activity as CreateTeamActivity).isPointsSortingGlobal =
                    true
                mainBinding.ivPointSortImage.visibility = View.VISIBLE
                mainBinding.ivPointSortImage.setImageResource(R.drawable.ic_up_sort)
                mainBinding.ivCreditSortImage.visibility = View.INVISIBLE
            }

        }

        mainBinding.tvCredits.setOnClickListener {

            if (activity is CreateTeamActivity) {
                (activity as CreateTeamActivity?)!!.isPointSelected = false
                (activity as CreateTeamActivity?)!!.isCreditSelected = true
            }
            if (isCredits) {
                isCredits = false
                if (activity is CreateTeamActivity) (activity as CreateTeamActivity?)!!.isCreditsGlobal =
                    false
                playerItemAdapter.sortWithCredit(false)
                mainBinding.ivCreditSortImage.visibility = View.VISIBLE
                mainBinding.ivCreditSortImage.setImageResource(R.drawable.ic_down_sort)
                mainBinding.ivPointSortImage.visibility = View.INVISIBLE
            } else {
                isCredits = true
                playerItemAdapter.sortWithCredit(true)
                if (activity is CreateTeamActivity) (activity as CreateTeamActivity?)!!.isCreditsGlobal =
                    true
                mainBinding.ivCreditSortImage.visibility = View.VISIBLE
                mainBinding.ivCreditSortImage.setImageResource(R.drawable.ic_up_sort)
                mainBinding.ivPointSortImage.visibility = View.INVISIBLE
            }

        }

        return mainBinding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(
            mainPlayerList: ArrayList<PlayerListResult>,
            playerTypeList: ArrayList<PlayerListResult>,
            type: Int,
            fantasyType: Int
        ) =
            CreateTeamPlayerFragment().apply {
                val myFragment = CreateTeamPlayerFragment()
                val args = Bundle()
                args.putInt("type", type)
                args.putInt("fantType", fantasyType)
                args.putSerializable("mainList", mainPlayerList)
                args.putSerializable("typeList", playerTypeList)
                myFragment.arguments = args
                return myFragment
            }


    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mainPlayerList = requireArguments().getParcelableArrayList("mainList")!!
            playerTypeList = requireArguments().getParcelableArrayList("typeList")!!
            type = requireArguments().getInt("type")
            fantasyType = requireArguments().getInt("fantType")
        }
    }

    // recycle the data
    private fun setupRecyclerView() {
        playerItemAdapter = PlayerItemAdapter(
            requireContext(),
            mainPlayerList,
            playerTypeList,
            this,
            type,
            fantasyType
        )
        mainBinding.recyclerView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        mainBinding.recyclerView.layoutManager = mLayoutManager
        mainBinding.recyclerView.adapter = playerItemAdapter

    }


    private fun refreshSortIcon() {
        if (activity is CreateTeamActivity) {
            if ((activity as CreateTeamActivity?)!!.isPointSelected) {
                if ((activity as CreateTeamActivity?)!!.isPointsSortingGlobal) {
                    playerItemAdapter.sortWithPoints(true)
                    mainBinding.ivCreditSortImage.visibility = View.INVISIBLE
                    mainBinding.ivPointSortImage.setImageResource(R.drawable.ic_up_sort)
                    mainBinding.ivPointSortImage.visibility = View.VISIBLE
                } else {
                    playerItemAdapter.sortWithPoints(false)
                    mainBinding.ivCreditSortImage.visibility = View.INVISIBLE
                    mainBinding.ivPointSortImage.setImageResource(R.drawable.ic_down_sort)
                    mainBinding.ivPointSortImage.visibility = View.VISIBLE
                }
            } else {
                if ((activity as CreateTeamActivity?)!!.isCreditsGlobal) {
                    playerItemAdapter.sortWithCredit(true)
                    mainBinding.ivCreditSortImage.visibility = View.VISIBLE
                    mainBinding.ivCreditSortImage.setImageResource(R.drawable.ic_up_sort)
                    mainBinding.ivPointSortImage.visibility = View.INVISIBLE
                } else {
                    playerItemAdapter.sortWithCredit(false)
                    mainBinding.ivCreditSortImage.visibility = View.VISIBLE
                    mainBinding.ivCreditSortImage.setImageResource(R.drawable.ic_down_sort)
                    mainBinding.ivPointSortImage.visibility = View.INVISIBLE
                }
            }
        }
    }

    fun refresh(selectedList: ArrayList<PlayerListResult>, type: Int) {
        playerItemAdapter.updateData(selectedList, type)
        playerItemAdapter.notifyDataSetChanged()
        refreshSortIcon()
    }

    override fun onPlayerClick(isSelect: Boolean, position: Int, type: Int) {

        (activity as CreateTeamActivity).onPlayerClick(isSelect, position, type)

    }
}