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
import com.example.multicalculator.databinding.FragmentLengthBinding
import com.example.multicalculator.databinding.FragmentMassBinding


class LengthFragment : Fragment() {

    private var _binding: FragmentLengthBinding? = null
    private val binding get() = _binding!!

    private lateinit var selectedTextView: TextView

    private lateinit var lengthType1: TextView
    private lateinit var lengthType2: TextView
    private lateinit var spinnerFrom: Spinner
    private lateinit var spinnerTo: Spinner
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLengthBinding.inflate(inflater, container, false)
        binding.bottom.fragmentHeading.text = "Length"

        lengthType1 = binding.lengthTv1
        lengthType2 = binding.lengthTv2

        spinnerFrom = binding.degUnit1
        spinnerTo = binding.degUnit2

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.length_types,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerFrom.adapter = adapter
        spinnerTo.adapter = adapter

        spinnerFrom.setSelection(0)
        spinnerTo.setSelection(1)

        spinnerFrom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                convertLength()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        spinnerTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                convertLength()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        selectedTextView = lengthType1

        highlightSelectedTextView()

        lengthType1.setOnClickListener {
            selectedTextView = lengthType1
            highlightSelectedTextView()
        }

        lengthType2.setOnClickListener {
            selectedTextView = lengthType2
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
            convertLength()
        }

        binding.buttonClear.setOnClickListener {
            selectedTextView.text = ""
            lengthType1.text = ""
            lengthType2.text = ""
            convertLength()
        }

        setNumberButonListeners()

        binding.bottom.backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_lengthFragment_to_home_screen)
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
                convertLength()
            }
        }
    }

    private fun highlightSelectedTextView() {
        val selectedColor = ContextCompat.getColor(requireContext(), R.color.purple_200)
        val otherText = ContextCompat.getColor(requireContext(), R.color.black)
        lengthType1.setTextColor(
            if (selectedTextView == lengthType1) selectedColor else otherText
        )
        lengthType2.setTextColor(
            if (selectedTextView == lengthType2) selectedColor else otherText
        )
    }

    private fun convertLength() {
        val input = selectedTextView.text.toString()

        if (input.isNotEmpty()) {
            val inputValue = input.toDoubleOrNull()

            if (inputValue != null) {
                val fromUnit: String
                val toUnit: String
                val targetTv: TextView

                if (selectedTextView == lengthType1) {
                    fromUnit = spinnerFrom.selectedItem.toString()
                    toUnit = spinnerTo.selectedItem.toString()
                    targetTv = lengthType2
                } else {
                    fromUnit = spinnerTo.selectedItem.toString()
                    toUnit = spinnerFrom.selectedItem.toString()
                    targetTv = lengthType1
                }

                val convertedVal = convertLengthUnits(inputValue, fromUnit, toUnit)
                targetTv.text = String.format("%.2f", convertedVal)
            }
        }
    }

    private fun convertLengthUnits(inputValue: Double, fromUnit: String, toUnit: String): Double {
        val metersInUnit = mapOf(
            "Kilometer" to 1000.0,
            "Meter" to 1.0,
            "Centimeter" to 0.01,
            "Decimeter" to 0.1,
            "Millimeter" to 0.001,
            "Micrometer" to 0.000001
        )

        val fromMeters = metersInUnit[fromUnit] ?: return inputValue
        val toMeters = metersInUnit[toUnit] ?: return inputValue
        return (inputValue * fromMeters) / toMeters
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}