package com.fp.devfantasypowerxi.app.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.view.adapter.MatchItemAdapter
import com.fp.devfantasypowerxi.app.view.adapter.TeamItemAdapter
import com.fp.devfantasypowerxi.databinding.FragmentHomeBinding
import com.fp.devfantasypowerxi.databinding.FragmentMyTeamBinding
// Created by Gaurav Minocha
class MyTeamFragment : Fragment() {
    lateinit var mainBinding: FragmentMyTeamBinding
    lateinit var mAdapter: TeamItemAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_team, container, false)

        return mainBinding.root
    }

    // setup Recycler data
    private fun setupRecyclerView() {
        mAdapter = TeamItemAdapter(requireContext())
        mainBinding.recyclerView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        mainBinding.recyclerView.layoutManager = mLayoutManager
        mainBinding.recyclerView.adapter = mAdapter

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (isVisibleToUser) {
            Handler(Looper.getMainLooper()).postDelayed({
                setupRecyclerView()
            }, 200)
        }
    }
}