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
import com.example.multicalculator.databinding.FragmentTimeBinding


class TimeFragment : Fragment() {

    private var _binding: FragmentTimeBinding? = null
    private val binding get() = _binding!!

    private lateinit var selectedTextView: TextView
    private lateinit var timeType1: TextView
    private lateinit var timeType2: TextView
    private lateinit var spinnerType1: Spinner
    private lateinit var spinnerType2: Spinner
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTimeBinding.inflate(inflater, container, false)
        binding.bottom.fragmentHeading.text = "Time converter"

        timeType1 = binding.dataTypeTv1
        timeType2 = binding.dataTypeTv2

        timeType1.text = "1"

        spinnerType1 = binding.dataTypeUnit1
        spinnerType2 = binding.dataTypeUnit2

        val adapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.time_types, android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType1.adapter = adapter
        spinnerType2.adapter = adapter

        spinnerType1.setSelection(0)
        spinnerType2.setSelection(1)

        spinnerType1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                convertTime()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}

        }

        spinnerType2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                convertTime()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}

        }

        selectedTextView = timeType1
        highlightSelectedTextView()
        convertTime()

        timeType1.setOnClickListener {
            selectedTextView = timeType1
            highlightSelectedTextView()
        }

        timeType2.setOnClickListener {
            selectedTextView = timeType2
            highlightSelectedTextView()
        }

        binding.buttonDot.setOnClickListener {
            selectedTextView.text = addToInputText(".")
        }

        binding.buttonCroxx.setOnClickListener {
            val currentText = selectedTextView.text.toString()
            if (currentText.isEmpty() || currentText == "0") {
                selectedTextView.text = ""
                val otherTv = if (selectedTextView == timeType1) timeType2 else timeType1
                otherTv.text = ""
            } else {
                val removedLast = currentText.dropLast(1)
                selectedTextView.text = removedLast
            }
            convertTime()
        }

        binding.buttonClear.setOnClickListener {
            selectedTextView.text = ""
            timeType1.text = ""
            timeType2.text = ""
            convertTime()
        }

        binding.bottom.backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_timeFragment_to_home_screen)
        }

        setNUmberButtonListeners()

        return binding.root
    }

    private fun addToInputText(buttonValue: String): String {
        return selectedTextView.text.toString() + "" + buttonValue
    }

    private fun setNUmberButtonListeners() {
        val numberButtons = listOf(
            binding.button0,
            binding.button1,
            binding.button2,
            binding.button3,
            binding.button4,
            binding.button5,
            binding.button6,
            binding.button7,
            binding.button8,
            binding.button9
        )

        numberButtons.forEach { button ->
            button.setOnClickListener {
                val number = button.text.toString()
                selectedTextView.text = selectedTextView.text.toString() + number
                convertTime()
            }
        }
    }

    private fun highlightSelectedTextView() {
        val selectedColor = ContextCompat.getColor(requireContext(), R.color.purple_200)
        val otherText = ContextCompat.getColor(requireContext(), R.color.black)
        timeType1.setTextColor(
            if (selectedTextView == timeType1) selectedColor else otherText
        )
        timeType2.setTextColor(
            if (selectedTextView == timeType2) selectedColor else otherText
        )
    }

    private fun convertTime() {
        val input = selectedTextView.text.toString()
        if (input.isNotEmpty()) {
            val inputValue = input.toDoubleOrNull()
            if (inputValue != null) {
                val fromUnit: String
                val toUnit: String
                val targetTv: TextView

                if (selectedTextView == timeType1) {
                    fromUnit = spinnerType1.selectedItem.toString()
                    toUnit = spinnerType2.selectedItem.toString()
                    targetTv = timeType2
                } else {
                    fromUnit = spinnerType2.selectedItem.toString()
                    toUnit = spinnerType1.selectedItem.toString()
                    targetTv = timeType1
                }

                val convertedValue = convertTimeUnits(inputValue, fromUnit, toUnit)
                val formattedValue = if (convertedValue % 1 == 0.0){
                    convertedValue.toInt().toString()
                } else {
                    String.format("%.2f", convertedValue)
                }

                targetTv.text = formattedValue
            }
        }
    }

    private fun convertTimeUnits(value: Double, fromUnit: String, toUnit: String): Double {
        val secondsInUnit = mapOf(
            "Year" to 31536000.0,
            "Month" to 2628000.0,  // Approximate (30.44 days per month)
            "Week" to 604800.0,
            "Day" to 86400.0,
            "Hour" to 3600.0,
            "Minute" to 60.0,
            "Second" to 1.0
        )

        val fromSeconds = secondsInUnit[fromUnit] ?: return value
        val toSeconds = secondsInUnit[toUnit] ?: return value

        return (value * fromSeconds) / toSeconds
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}