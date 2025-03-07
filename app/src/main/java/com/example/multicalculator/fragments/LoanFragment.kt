package com.example.multicalculator.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.multicalculator.R
import com.example.multicalculator.databinding.FragmentLoanBinding
import kotlin.time.times

class LoanFragment : Fragment() {
    private lateinit var binding: FragmentLoanBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoanBinding.inflate(inflater,container,false)
        binding.bottom.fragmentHeading.text = "Loan Calculator"
        binding.buttonCalculate.setOnClickListener {
            calculateEMI()
        }

        binding.principalAmt.setOnClickListener {
            binding.bmiCardView.visibility = View.GONE
            binding.buttonCalculate.visibility = View.VISIBLE
        }

        binding.interestRate.setOnClickListener{
            binding.bmiCardView.visibility = View.GONE
            binding.buttonCalculate.visibility = View.VISIBLE
        }

        binding.loanTenure.setOnClickListener{
            binding.bmiCardView.visibility = View.GONE
            binding.buttonCalculate.visibility = View.VISIBLE
        }
        return binding.root
    }

    private fun calculateEMI() {
        val principal = binding.principalAmt.text.toString().toDoubleOrNull()
        val annualRate = binding.interestRate.text.toString().toDoubleOrNull()
        val tenureYears = binding.loanTenure.text.toString().toDoubleOrNull()

        if (principal == null || annualRate == null || tenureYears == 0.0) {
            Toast.makeText(requireContext(),"Enter valid values",Toast.LENGTH_SHORT).show()
            return
        }

        val monthlyRate = (annualRate/12) / 100
        val tenureMonths = tenureYears!! * 12

        val emi = if (monthlyRate > 0) {
            (principal * monthlyRate * Math.pow(1 + monthlyRate, tenureMonths)) /
                    (Math.pow(1 + monthlyRate, tenureMonths) - 1)
        } else {
            principal / tenureMonths
        }

        val totalPayment = emi * tenureMonths
        val totalInterest = totalPayment - principal

        binding.bmiCardView.visibility = View.VISIBLE
        binding.buttonCalculate.visibility = View.GONE
        binding.emiTxt.text = "Rs ${String.format("%.2f",emi)}"
        binding.totalInterestTxt.text = "Rs ${String.format("%.2f", totalInterest)}"
        binding.totalPaymentTxt.text = "Rs ${String.format("%.2f", totalPayment)}"
        binding.principalAmtView.text = "Rs " + principal.toString()
        binding.emiYear.text = tenureYears.toString() + " years"

    }

}