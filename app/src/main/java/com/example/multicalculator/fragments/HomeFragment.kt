package com.example.multicalculator.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
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

        binding.timeLayout.setOnClickListener {
            findNavController().navigate(R.id.action_home_screen_to_timeFragment)
        }

        binding.bmiLayout.setOnClickListener {
            findNavController().navigate(R.id.action_home_screen_to_bmiFragment)
        }

        binding.discountLayout.setOnClickListener {
            findNavController().navigate(R.id.action_home_screen_to_discountFragment)
        }

        binding.lengthLayout.setOnClickListener {
            findNavController().navigate(R.id.action_home_screen_to_lengthFragment)
        }

        binding.massLayout.setOnClickListener {
            findNavController().navigate(R.id.action_home_screen_to_massFragment)
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Navigate to the main fragment and clear back stack
                    findNavController().popBackStack(R.id.calculator_screen, false)
                }
            })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}