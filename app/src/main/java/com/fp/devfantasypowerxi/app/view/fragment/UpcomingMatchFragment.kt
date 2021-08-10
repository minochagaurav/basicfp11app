package com.fp.devfantasypowerxi.app.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.databinding.FragmentCommonMatchesBinding

// Create by Gaurav Minocha
class UpcomingMatchFragment : Fragment() {

    lateinit var mainBinding: FragmentCommonMatchesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mainBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_common_matches, container, false)

        return mainBinding.root
    }

}