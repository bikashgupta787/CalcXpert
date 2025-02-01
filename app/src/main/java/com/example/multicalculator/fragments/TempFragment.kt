package com.example.multicalculator.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.multicalculator.R
import com.example.multicalculator.databinding.FragmentTempBinding

class TempFragment : Fragment() {

    private var _binding: FragmentTempBinding? = null
    private val binding get() = _binding!!

    private lateinit var selectedTextView: TextView

    private lateinit var tempType1: TextView
    private lateinit var tempType2: TextView
    private lateinit var spinnerFrom: Spinner
    private lateinit var spinnerTo: Spinner
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTempBinding.inflate(inflater, container, false)
        binding.bottom.fragmentHeading.text = "Temperature"

        tempType1 = binding.celciusTv
        tempType2 = binding.farhTv

        spinnerFrom = binding.degUnit1
        spinnerTo = binding.degUnit2


        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.degree_types,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerFrom.adapter = adapter
        spinnerTo.adapter = adapter

        // Default selections
        spinnerFrom.setSelection(0) // Celsius
        spinnerTo.setSelection(1) // Fahrenheit

        spinnerFrom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //val selectedItem = p0?.getItemAtPosition(p2).toString()
                convertTemperature()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }

        spinnerTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //val selectedItem = p0?.getItemAtPosition(p2).toString()
                convertTemperature()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        // Set default selected TextView
        selectedTextView = tempType1

        // Highlight the selected TextView
        highlightSelectedTextView()

        // Set OnClickListener for TextViews
        tempType1.setOnClickListener {
            selectedTextView = binding.celciusTv
            highlightSelectedTextView()
        }
        tempType2.setOnClickListener {
            selectedTextView = binding.farhTv
            highlightSelectedTextView()
        }

        binding.buttonDot.setOnClickListener {
            selectedTextView.text = addToInputText(".")
        }

        binding.buttonCroxx.setOnClickListener {
            val currentText = selectedTextView.text.toString()

            // Check if text is empty or null and set it to "0"
            if (currentText.isEmpty()) {
                selectedTextView.text = ""
            } else {
                val removedLast = currentText.dropLast(1)
                selectedTextView.text = removedLast
            }

            // Call the conversion function to update the other TextView
            convertTemperature()
        }

        binding.buttonClear.setOnClickListener {
            selectedTextView.text = ""
            tempType1.text = ""
            tempType2.text = ""
            convertTemperature()
        }

        setNumberButtonListeners()

        binding.bottom.backBtn.setOnClickListener{
            findNavController().navigate(R.id.action_tempFragment_to_home_screen)
        }

        return binding.root
    }

    private fun addToInputText(buttonValue: String): String {
        return selectedTextView.text.toString() + "" + buttonValue
    }

    private fun setNumberButtonListeners() {
        val numberButtons = listOf(
            binding.button0, binding.button1, binding.button2, binding.button3,
            binding.button4, binding.button5, binding.button6,
            binding.button7, binding.button8, binding.button9
        )

        numberButtons.forEach { button ->
            button.setOnClickListener {
                val number = button.text.toString()
                selectedTextView.text = selectedTextView.text.toString() + number
                convertTemperature()
            }
        }
    }

    private fun highlightSelectedTextView() {
        val selectedColor = ContextCompat.getColor(requireContext(), R.color.purple_200)
        val otherText = ContextCompat.getColor(requireContext(), R.color.black)
        tempType1.setTextColor(
            if (selectedTextView == tempType1) selectedColor else otherText
        )
        tempType2.setTextColor(
            if (selectedTextView == tempType2) selectedColor else otherText
        )
    }


    private fun convertTemperature() {
        val input = selectedTextView.text.toString()

        if (input.isNotEmpty()) {
            val inputValue = input.toDoubleOrNull()

            if (inputValue != null) {
//                val fromUnit = spinnerFrom.selectedItem.toString()
//                val toUnit = spinnerTo.selectedItem.toString()
//
//                val convertedValue = convertTemperatureUnits(inputValue, fromUnit, toUnit)
//                val targetTextView = if (selectedTextView == celsiusTv) farhnTv else celsiusTv
//                targetTextView.text = String.format("%.2f", convertedValue)

                val fromUnit: String
                val toUnit: String
                val targetTv: TextView

                if (selectedTextView == tempType1) {
                    fromUnit = spinnerFrom.selectedItem.toString()
                    toUnit = spinnerTo.selectedItem.toString()
                    targetTv = tempType2
                } else {
                    fromUnit = spinnerTo.selectedItem.toString()
                    toUnit = spinnerFrom.selectedItem.toString()
                    targetTv = tempType1
                }

                val convertedVal = convertTemperatureUnits(inputValue, fromUnit, toUnit)
                targetTv.text = String.format("%.2f", convertedVal)
            }
        }
    }

    private fun convertTemperatureUnits(value: Double, fromUnit: String, toUnit: String): Double {
        return when (fromUnit to toUnit) {
            "Celsius" to "Fahrenheit" -> (value * 9 / 5) + 32
            "Celsius" to "Kelvin" -> value + 273.15
            "Fahrenheit" to "Celsius" -> (value - 32) * 5 / 9
            "Fahrenheit" to "Kelvin" -> (value - 32) * 5 / 9 + 273.15
            "Kelvin" to "Celsius" -> value - 273.15
            "Kelvin" to "Fahrenheit" -> (value - 273.15) * 9 / 5 + 32
            else -> value
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}