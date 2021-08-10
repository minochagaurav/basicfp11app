package com.fp.devfantasypowerxi.app.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.databinding.FragmentPanValidationBinding
// Created by Gaurav Minocha
class PanValidationFragment : Fragment() {
    lateinit var mainBinding: FragmentPanValidationBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mainBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_pan_validation, container, false)
        return mainBinding.root
    }

}