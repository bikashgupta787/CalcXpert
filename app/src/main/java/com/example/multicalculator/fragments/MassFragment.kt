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
import com.example.multicalculator.databinding.FragmentMassBinding
import com.example.multicalculator.databinding.FragmentTempBinding

class MassFragment : Fragment() {

    private var _binding: FragmentMassBinding? = null
    private val binding get() = _binding!!

    private lateinit var selectedTextView: TextView

    private lateinit var massType1: TextView
    private lateinit var massType2: TextView
    private lateinit var spinnerFrom: Spinner
    private lateinit var spinnerTo: Spinner
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMassBinding.inflate(inflater,container,false)
        binding.bottom.fragmentHeading.text = "Mass converter"

        massType1 = binding.massTv1
        massType2 = binding.massTv2

        massType1.text = "1"

        spinnerFrom = binding.degUnit1
        spinnerTo = binding.degUnit2

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.mass_types,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerFrom.adapter = adapter
        spinnerTo.adapter = adapter

        spinnerFrom.setSelection(0)
        spinnerTo.setSelection(1)

        spinnerFrom.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                convertMass()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}

        }

        spinnerTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                convertMass()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        selectedTextView = massType1
        highlightSelectedTextView()

        convertMass()

        massType1.setOnClickListener {
            selectedTextView = massType1
            highlightSelectedTextView()
        }

        massType2.setOnClickListener {
            selectedTextView = massType2
            highlightSelectedTextView()
        }

        binding.buttonDot.setOnClickListener {
            selectedTextView.text = addToInputText(".")
        }

        binding.buttonCroxx.setOnClickListener {
            val currentText = selectedTextView.text.toString()
            if (currentText.isEmpty() || currentText == "0") {
                selectedTextView.text = ""
                val otherTv = if (selectedTextView == massType1) massType2 else massType1
                otherTv.text = ""
            } else {
                val removedLast = currentText.dropLast(1)
                selectedTextView.text = removedLast
            }
            convertMass()
        }

        binding.buttonClear.setOnClickListener {
            selectedTextView.text = ""
            massType1.text = ""
            massType2.text = ""
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
                convertMass()
            }
        }
    }

    private fun highlightSelectedTextView() {
        val selectedColor = ContextCompat.getColor(requireContext(), R.color.purple_200)
        val otherText = ContextCompat.getColor(requireContext(), R.color.black)
        massType1.setTextColor(
            if (selectedTextView == massType1) selectedColor else otherText
        )
        massType2.setTextColor(
            if (selectedTextView == massType2) selectedColor else otherText
        )
    }

    private fun convertMass() {
        val input = selectedTextView.text.toString()

        if (input.isNotEmpty()) {
            val inputValue = input.toDoubleOrNull()

            if (inputValue != null) {
                val fromUnit: String
                val toUnit: String
                val targetTv: TextView

                if (selectedTextView == massType1) {
                    fromUnit = spinnerFrom.selectedItem.toString()
                    toUnit = spinnerTo.selectedItem.toString()
                    targetTv = massType2
                } else {
                    fromUnit = spinnerTo.selectedItem.toString()
                    toUnit = spinnerFrom.selectedItem.toString()
                    targetTv = massType1
                }

                val convertedVal = convertMassUnits(inputValue, fromUnit, toUnit)
                val formattedValue = if (convertedVal % 1 == 0.0) {
                    convertedVal.toInt().toString()
                } else {
                    String.format("%.2f", convertedVal)
                }
                targetTv.text = formattedValue
            }
        }
    }

    private fun convertMassUnits(inputValue: Double, fromUnit: String, toUnit: String): Double {
        val gramsInUnit = mapOf(
            "Tonne" to 1_000_000.0,  // 1 tonne = 1,000,000 grams
            "Quintal" to 100_000.0,  // 1 quintal = 100,000 grams
            "Kilogram" to 1_000.0,   // 1 kg = 1,000 grams
            "Gram" to 1.0,           // 1 g = 1 gram
            "Milligram" to 0.001,    // 1 mg = 0.001 grams
            "Microgram" to 0.000001  // 1 µg = 0.000001 grams
        )

        val fromGrams = gramsInUnit[fromUnit] ?: return inputValue
        val toGrams = gramsInUnit[toUnit] ?: return inputValue

        return (inputValue * fromGrams) / toGrams
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}