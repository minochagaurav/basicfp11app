package com.fp.devfantasypowerxi.app.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.view.adapter.PlayerItemAdapter
import com.fp.devfantasypowerxi.databinding.FragmentCreateTeamPlayerBinding
import com.fp.devfantasypowerxi.databinding.FragmentHomeBinding
// Created by Gaurav Minocha
class CreateTeamPlayerFragment : Fragment() {
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
        return mainBinding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CreateTeamPlayerFragment().apply {

            }
    }

    // recycle the data
    private fun setupRecyclerView() {
        playerItemAdapter = PlayerItemAdapter(requireContext())
        mainBinding.recyclerView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        mainBinding.recyclerView.layoutManager = mLayoutManager
        mainBinding.recyclerView.adapter = playerItemAdapter

    }
}