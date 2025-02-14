package com.example.multicalculator.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.multicalculator.databinding.FragmentDataBinding

class DataFragment : Fragment() {

    private lateinit var binding: FragmentDataBinding
    private lateinit var dataType1Tv: TextView
    private lateinit var dataType2Tv: TextView
    private lateinit var dataFrom: Spinner
    private lateinit var dataTo: Spinner
    private lateinit var selectedTv: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDataBinding.inflate(inflater, container, false)
        binding.bottom.fragmentHeading.text = "Data converter"

        dataType1Tv = binding.dataTypeTv1
        dataType2Tv = binding.dataTypeTv2

        dataType1Tv.text = "1"

        dataFrom = binding.dataTypeUnit1
        dataTo = binding.dataTypeUnit2

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.data_types,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        dataFrom.adapter = adapter
        dataTo.adapter = adapter

        dataFrom.setSelection(2) //b
        dataTo.setSelection(1)  //mb


        dataFrom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                convertDataType()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        dataTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                convertDataType()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}

        }

        selectedTv = dataType1Tv

        highlightSelectedTv()

        convertDataType()

        //setupTextWatchers()

        dataType1Tv.setOnClickListener {
            selectedTv = dataType1Tv
            highlightSelectedTv()
        }

        dataType2Tv.setOnClickListener {
            selectedTv = dataType2Tv
            highlightSelectedTv()
        }

        binding.buttonDot.setOnClickListener {
            selectedTv.text = addToInputText(".")
        }

        binding.buttonCroxx.setOnClickListener {
            val currentText = selectedTv.text.toString()

            if (currentText.isEmpty() || currentText == "0") {
                selectedTv.text = ""
                val otherTv = if (selectedTv == dataType1Tv) dataType2Tv else dataType1Tv
                otherTv.text = ""
            } else {
                val removeLast = currentText.dropLast(1)
                selectedTv.text = removeLast
                convertDataType()
            }

        }

        binding.buttonClear.setOnClickListener {
            selectedTv.text = ""
            dataType1Tv.text = ""
            dataType2Tv.text = ""
            convertDataType()
        }

        setNumberButtonListeners()

        binding.bottom.backBtn.setOnClickListener{
            findNavController().navigate(R.id.action_dataFragment_to_home_screen)
        }

        return binding.root
    }

    private fun addToInputText(buttonValue: String): String {
        return selectedTv.text.toString() + "" + buttonValue
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
                selectedTv.text = selectedTv.text.toString() + number
                convertDataType()
            }
        }
    }

    private fun highlightSelectedTv() {
        val selectedColor = ContextCompat.getColor(requireContext(), R.color.purple_200)
        val otherText = ContextCompat.getColor(requireContext(), R.color.black)

        dataType1Tv.setTextColor(
            if (selectedTv == dataType1Tv) selectedColor else otherText
        )
        dataType2Tv.setTextColor(
            if (selectedTv == dataType2Tv) selectedColor else otherText
        )
    }


    private fun convertDataType() {
        val input = selectedTv.text.toString()

        if (input.isNotEmpty()) {
            val inputValue = input.toDoubleOrNull()

            if (inputValue != null) {

                val fromUnit: String
                val toUnit: String
                val targetTv: TextView

                if (selectedTv == dataType1Tv) {
                    fromUnit = dataFrom.selectedItem.toString()
                    toUnit = dataTo.selectedItem.toString()
                    targetTv = dataType2Tv
                } else {
                    fromUnit = dataTo.selectedItem.toString()
                    toUnit = dataFrom.selectedItem.toString()
                    targetTv = dataType1Tv
                }

                val convertedVal = convertDataUnits(inputValue, fromUnit, toUnit)
                val formattedValue = if (convertedVal % 1 == 0.0){
                    convertedVal.toInt().toString()
                } else {
                    String.format("%.2f", convertedVal)
                }

                targetTv.text = formattedValue
            }
        }
    }


    private fun convertDataUnits(value: Double, fromUnit: String, toUnit: String): Double {
        val conversionMap = mapOf(
            "Byte" to 1.0,
            "Kilobyte" to 1024.0,
            "Megabyte" to 1024.0 * 1024.0,
            "Gigabyte" to 1024.0 * 1024.0 * 1024.0,
            "Terabyte" to 1024.0 * 1024.0 * 1024.0 * 1024.0
        )

        val fromValue = conversionMap[fromUnit] ?: return value
        val toValue = conversionMap[toUnit] ?: return value

        return value * (fromValue / toValue)
    }



}