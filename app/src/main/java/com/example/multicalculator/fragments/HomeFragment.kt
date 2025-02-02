package com.example.multicalculator.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.replace
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.multicalculator.R
import com.example.multicalculator.databinding.FragmentCalculatorBinding
import com.example.multicalculator.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.ageLayout.setOnClickListener {
            findNavController().navigate(R.id.action_home_screen_to_ageFragment)
        }

        binding.tempLayout.setOnClickListener {
            findNavController().navigate(R.id.action_home_screen_to_tempFragment)
        }

        binding.dataLayout.setOnClickListener {
            findNavController().navigate(R.id.action_home_screen_to_dataFragment)
        }

        return binding.root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}