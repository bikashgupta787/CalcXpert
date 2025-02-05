package com.example.multicalculator.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.multicalculator.R
import com.example.multicalculator.databinding.FragmentBmiBinding
import com.google.android.material.tabs.TabLayout


class BmiFragment : Fragment() {
    private var _binding: FragmentBmiBinding? = null
    private val binding get() = _binding!!
    private lateinit var cardView: ConstraintLayout
    private lateinit var tabLayout: TableLayout
    private lateinit var selectedTextView: TextView
    private lateinit var heightVal: TextView
    private lateinit var weightVal: TextView
    private lateinit var heightspinner: Spinner
    private lateinit var weightspinner: Spinner
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBmiBinding.inflate(inflater, container, false)
        binding.bottom.fragmentHeading.text = "BMI"
        cardView = binding.bmiCardView
        tabLayout = binding.tableLayout

        heightVal = binding.dataTypeTv2
        weightVal = binding.dataTypeTv1

        heightspinner = binding.heightType
        weightspinner = binding.weightType

        val adapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.bmi_weight, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        weightspinner.adapter = adapter

        val adapter2 = ArrayAdapter.createFromResource(
            requireContext(), R.array.bmi_height, android.R.layout.simple_spinner_item
        )
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        heightspinner.adapter = adapter2

        weightspinner.setSelection(0)
        heightspinner.setSelection(0)

        weightspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                cardView.visibility = View.GONE
                tabLayout.visibility = View.VISIBLE
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        heightspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                cardView.visibility = View.GONE
                tabLayout.visibility = View.VISIBLE
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        selectedTextView = weightVal
        highlightSelectedTextView()

        heightVal.setOnClickListener {
            selectedTextView = heightVal
            highlightSelectedTextView()
            cardView.visibility = View.GONE
            tabLayout.visibility = View.VISIBLE
        }

        weightVal.setOnClickListener {
            selectedTextView = weightVal
            highlightSelectedTextView()
            cardView.visibility = View.GONE
            tabLayout.visibility = View.VISIBLE
        }

        binding.buttonGo.setOnClickListener {
            cardView.visibility = View.VISIBLE
            tabLayout.visibility = View.GONE
            calculateBMI()
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
        }

        binding.buttonClear.setOnClickListener {
            selectedTextView.text = ""
            heightVal.text = ""
            weightVal.text = ""
        }

        binding.bottom.backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_bmiFragment_to_home_screen)
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
            }
        }
    }

    private fun highlightSelectedTextView() {
        val selectedColor = ContextCompat.getColor(requireContext(), R.color.purple_200)
        val otherText = ContextCompat.getColor(requireContext(), R.color.black)
        heightVal.setTextColor(
            if (selectedTextView == heightVal) selectedColor else otherText
        )
        weightVal.setTextColor(
            if (selectedTextView == weightVal) selectedColor else otherText
        )
    }

    private fun calculateBMI() {
        val weightText = weightVal.text.toString().toDoubleOrNull()
        val heightText = heightVal.text.toString().toDoubleOrNull()

        if (weightText == null || heightText == null || heightText == 0.0) {
            binding.bmiTv.text = "NA"
            return
        }

        val weightUnit = weightspinner.selectedItem.toString()
        val heightUnit = heightspinner.selectedItem.toString()

        val weightInKg = when (weightUnit) {
            "Kilograms" -> weightText
            "Pounds" -> weightText * 0.453592
            else -> {
                weightText
            }
        }

        val heightInMeters = when (heightUnit) {
            "Centimeters" -> heightText / 100
            "Meters" -> heightText
            "Feet" -> heightText * 0.3048
            "Inches" -> heightText * 0.0254
            else -> heightText
        }

        val bmi = weightInKg / (heightInMeters * heightInMeters)
        binding.bmiTv.text = String.format("%.1f", bmi)

        binding.bmiType.text = when {
            bmi < 18.5 -> "Underweight"
            bmi in 18.5..25.0 -> "Normal"
            else -> "Overweight"
        }.toString()

//        val heightInMeters = heightText / 100
//        val bmi = weightText / (heightInMeters * heightInMeters)
//        binding.bmiTv.text = String.format("%.1f", bmi)
//
//        if (bmi <= 18.5) {
//            binding.bmiType.text = "Underweight"
//        } else if (bmi in 18.5..25.0) {
//            binding.bmiType.text = "Normal"
//        } else {
//            binding.bmiType.text = "Overweight"
//        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}