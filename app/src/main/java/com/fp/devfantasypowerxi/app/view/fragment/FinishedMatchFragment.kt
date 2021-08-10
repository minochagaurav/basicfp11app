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
import com.fp.devfantasypowerxi.app.view.adapter.MyMatchItemAdapter
import com.fp.devfantasypowerxi.databinding.FragmentCommonMatchesBinding
// Made By Gaurav Minocha
class FinishedMatchFragment : Fragment() {
    lateinit var mainBinding: FragmentCommonMatchesBinding
    private lateinit var mAdapter: MyMatchItemAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_common_matches, container, false)
        setupRecyclerView()
        return mainBinding.root
    }
        // setup recycle data
    private fun setupRecyclerView() {
        mainBinding.ivNoImage.visibility = View.GONE
        mAdapter = MyMatchItemAdapter(requireContext())
        mainBinding.recyclerView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        mainBinding.recyclerView.layoutManager = mLayoutManager
        mainBinding.recyclerView.adapter = mAdapter
    }

}