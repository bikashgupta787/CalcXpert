package com.example.multicalculator.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.example.multicalculator.R
import com.example.multicalculator.databinding.FragmentTempBinding

class TempFragment : Fragment() {

    private var _binding: FragmentTempBinding? = null
    private val binding get() = _binding!!

    private lateinit var selectedTextView: TextView

    private lateinit var celsiusTv: TextView
    private lateinit var farhnTv: TextView
    private lateinit var spinnerFrom: Spinner
    private lateinit var spinnerTo: Spinner
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTempBinding.inflate(inflater, container, false)
        binding.bottom.fragmentHeading.text = "Temperature"

        celsiusTv = binding.celciusTv
        farhnTv = binding.farhTv

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
                TODO("Not yet implemented")
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
        selectedTextView = celsiusTv

        // Highlight the selected TextView
        highlightSelectedTextView()

        // Set OnClickListener for TextViews
        celsiusTv.setOnClickListener {
            selectedTextView = binding.celciusTv
            highlightSelectedTextView()
        }
        farhnTv.setOnClickListener {
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
                selectedTextView.text = "0"
            } else {
                // Otherwise, remove the last character
                val removedLast = currentText.dropLast(1)
                selectedTextView.text = removedLast
            }

            // Call the conversion function to update the other TextView
            convertTemperature()
        }

        binding.buttonClear.setOnClickListener {
            selectedTextView.text = ""
            convertTemperature()
        }

        setNumberButtonListeners()

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
        celsiusTv.setBackgroundColor(
            if (selectedTextView == celsiusTv) Color.YELLOW else Color.WHITE
        )
        farhnTv.setBackgroundColor(
            if (selectedTextView == farhnTv) Color.YELLOW else Color.WHITE
        )
    }


//    private fun convertTemperature() {
//        val input = selectedTextView.text.toString()
//
//        if (input.isNotEmpty()) {
//            val inputValue = input.toDoubleOrNull()
//
//            if (inputValue != null) {
//                if (selectedTextView == celsiusTv) {
//                    // Convert Celsius to Fahrenheit
//                    val fahrenheit = (inputValue * 9 / 5) + 32
//                    farhnTv.text = fahrenheit.toString()
//                } else if (selectedTextView == farhnTv) {
//                    // Convert Fahrenheit to Celsius
//                    val celsius = (inputValue - 32) * 5 / 9
//                    celsiusTv.text = celsius.toString()
//                }
//            }
//        }
//    }

    private fun convertTemperature() {
        val input = selectedTextView.text.toString()

        if (input.isNotEmpty()) {
            val inputValue = input.toDoubleOrNull()

            if (inputValue != null) {
                val fromUnit = spinnerFrom.selectedItem.toString()
                val toUnit = spinnerTo.selectedItem.toString()

                val convertedValue = convertTemperatureUnits(inputValue, fromUnit, toUnit)
                val targetTextView = if (selectedTextView == celsiusTv) farhnTv else celsiusTv
                targetTextView.text = String.format("%.2f", convertedValue)
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