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
import com.fp.devfantasypowerxi.app.view.adapter.JoinedContestItemAdapter
import com.fp.devfantasypowerxi.app.view.adapter.MatchItemAdapter
import com.fp.devfantasypowerxi.databinding.FragmentMyJoinedContestBinding
// Created by Gaurav Minocha
class MyJoinedContestFragment : Fragment() {
    lateinit var mainBinding: FragmentMyJoinedContestBinding
    lateinit var mAdapter: JoinedContestItemAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mainBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_my_joined_contest,
            container,
            false
        )
        //setupRecyclerView()
        return mainBinding.root
    }
    // setup Recycler data
    private fun setupRecyclerView() {
        mAdapter = JoinedContestItemAdapter(requireContext())
        mainBinding.recyclerView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        mainBinding.recyclerView.layoutManager = mLayoutManager
        mainBinding.recyclerView.adapter = mAdapter
        //getData();
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (isVisibleToUser) {
            Handler(Looper.getMainLooper()).postDelayed({
                setupRecyclerView()
            }, 200)
        }
    }
}