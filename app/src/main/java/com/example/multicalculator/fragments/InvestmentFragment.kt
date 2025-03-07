package com.example.multicalculator.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.multicalculator.R
import com.example.multicalculator.databinding.FragmentInvestmentBinding
import com.example.multicalculator.databinding.FragmentLoanBinding

class InvestmentFragment : Fragment() {
    private lateinit var binding: FragmentInvestmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInvestmentBinding.inflate(inflater, container, false)
        binding.bottom.fragmentHeading.text = "Investment Calculator"
        binding.buttonCal.setOnClickListener {
            calculateInvestment()
        }

        binding.totalInvestmentAmt.setOnClickListener {
            binding.bmiCardView.visibility = View.GONE
            binding.buttonCal.visibility = View.VISIBLE
        }

        binding.interestRate.setOnClickListener {
            binding.bmiCardView.visibility = View.GONE
            binding.buttonCal.visibility = View.VISIBLE
        }

        binding.duration.setOnClickListener {
            binding.bmiCardView.visibility = View.GONE
            binding.buttonCal.visibility = View.VISIBLE
        }

        return binding.root
    }

    private fun calculateInvestment() {
        val principal = binding.totalInvestmentAmt.text.toString().toDoubleOrNull()
        val annualRate = binding.interestRate.text.toString().toDoubleOrNull()
        val durationYears = binding.duration.text.toString().toDoubleOrNull()

        if (principal == null || annualRate == null || durationYears == null || durationYears == 0.0) {
            Toast.makeText(requireContext(), "Please enter valid value", Toast.LENGTH_SHORT).show()
            return
        }

        val rate = annualRate / 100
        val selectType = binding.radioGroup.checkedRadioButtonId

        var maturityAmt = 0.0
        var totalInterest = 0.0

        if (selectType == R.id.oneTime) {
            maturityAmt = principal * Math.pow(1 + rate, durationYears)
            totalInterest = maturityAmt - principal
        } else if (selectType == R.id.recurring) {
            val months = durationYears * 12
            val monthlyRate = rate / 12
            maturityAmt = principal * ((Math.pow(
                1 + monthlyRate,
                months
            ) - 1) / monthlyRate) * (1 + monthlyRate)
            totalInterest = maturityAmt - (principal * months)
        }

        binding.bmiCardView.visibility = View.VISIBLE
        binding.buttonCal.visibility = View.GONE
        binding.totalVal.text = "Rs. ${String.format("%.2f", maturityAmt)}"
        binding.totalInterestVal.text = "Rs. ${String.format("%.2f", totalInterest)}"
        binding.totalInvestVal.text = "Rs. " + principal.toString()
        binding.durationView.text = durationYears.toString() + " years"
    }


}