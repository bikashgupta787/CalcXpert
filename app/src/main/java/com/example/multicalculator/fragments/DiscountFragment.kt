package com.example.multicalculator.fragments

import android.os.Bundle
import android.text.InputFilter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.multicalculator.R
import com.example.multicalculator.databinding.FragmentDiscountBinding
import com.example.multicalculator.databinding.FragmentMassBinding

class DiscountFragment : Fragment() {

    private var _binding: FragmentDiscountBinding? = null
    private val binding get() = _binding!!
    private lateinit var selectedTextView: TextView
    private lateinit var originalPrice: TextView
    private lateinit var discount: TextView
    private lateinit var finalPrice: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiscountBinding.inflate(inflater, container, false)
        binding.bottom.fragmentHeading.text = "Discount"
        originalPrice = binding.originalPriceTv
        discount = binding.discountPriceTv
        finalPrice = binding.finalPriceTv

        originalPrice.text = "100"
        discount.text = "10"

        selectedTextView = originalPrice
        highlightSelectedTextView()
        calculateDiscountPrice()

//        discount.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
//            val input = (dest.toString() + source.toString()).toIntOrNull() ?: return@InputFilter ""
//            if (input in 0..99) source else ""
//        })


        originalPrice.setOnClickListener {
            selectedTextView = originalPrice
            highlightSelectedTextView()
        }



        discount.setOnClickListener {
            selectedTextView = discount
            highlightSelectedTextView()
        }

        binding.buttonDot.setOnClickListener {
            selectedTextView.text = addToInputText(".")
        }

        binding.buttonCroxx.setOnClickListener {
            val currentText = selectedTextView.text.toString()
            if (currentText.isEmpty()) {
                selectedTextView.text = ""
            } else {
                val removedLast = currentText.dropLast(1)
                selectedTextView.text = removedLast
            }
            calculateDiscountPrice()
        }

        binding.buttonClear.setOnClickListener {
            selectedTextView.text = ""
            originalPrice.text = ""
            discount.text = ""
            finalPrice.text = ""
            binding.savedTv.text = ""
        }

        setNumberButonListeners()

        binding.bottom.backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_discountFragment_to_home_screen)
        }

        return binding.root
    }

    private fun addToInputText(buttonValue: String): String {
        return selectedTextView.text.toString() + "" + buttonValue
    }

    private fun setNumberButonListeners() {
        val numberButtons = listOf(
            binding.button0, binding.button1, binding.button2, binding.button3,
            binding.button4, binding.button5, binding.button6,
            binding.button7, binding.button8, binding.button9
        )

        numberButtons.forEach { button ->
            button.setOnClickListener {
                val number = button.text.toString()
                selectedTextView.text = selectedTextView.text.toString() + number
                calculateDiscountPrice()
            }
        }
    }

    private fun highlightSelectedTextView() {
        val selectedColor = ContextCompat.getColor(requireContext(), R.color.purple_200)
        val otherText = ContextCompat.getColor(requireContext(), R.color.black)
        originalPrice.setTextColor(
            if (selectedTextView == originalPrice) selectedColor else otherText
        )
        discount.setTextColor(
            if (selectedTextView == discount) selectedColor else otherText
        )
    }

    private fun calculateDiscountPrice() {
        val originalPriceText = binding.originalPriceTv.text.toString().toDoubleOrNull()
        val discountPrice = binding.discountPriceTv.text.toString().toDoubleOrNull()

        if (originalPriceText == null || discountPrice == null) {
            binding.finalPriceTv.text = "NA"
            return
        }

        val disountAmount = (originalPriceText * discountPrice) / 100
        val finalPrice = originalPriceText - disountAmount
        val savedPrice = originalPriceText - finalPrice

        val formattedValue = if (finalPrice % 1 == 0.0) {
            finalPrice.toInt().toString()
        } else {
            String.format("%.2f", finalPrice)
        }

        binding.finalPriceTv.text = formattedValue

        val formattedValue2 = if (savedPrice % 1 == 0.0) {
            savedPrice.toInt().toString()
        } else {
            String.format("%.2f", savedPrice)
        }

        binding.savedTv.text = "You Saved " + formattedValue2
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}