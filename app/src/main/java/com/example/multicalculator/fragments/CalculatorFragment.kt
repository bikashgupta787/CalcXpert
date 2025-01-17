package com.example.multicalculator.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.multicalculator.R
import com.example.multicalculator.databinding.FragmentCalculatorBinding
import org.mariuszgromada.math.mxparser.Expression
import java.text.DecimalFormat


class CalculatorFragment : Fragment() {

    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!
    private lateinit var inputTV: TextView
    private lateinit var outputTV: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)

        inputTV = binding.input
        outputTV = binding.output

        binding.buttonClear.setOnClickListener {
            inputTV.text = ""
            outputTV.text = ""
        }

        binding.buttonBracket.setOnClickListener {
            inputTV.text = addToInputText("(")
        }

        binding.buttonBracketR.setOnClickListener {
            inputTV.text = addToInputText(")")
        }

        binding.button0.setOnClickListener {
            inputTV.text = addToInputText("0")
        }

        binding.button1.setOnClickListener {
            inputTV.text = addToInputText("1")
        }

        binding.button2.setOnClickListener {
            inputTV.text = addToInputText("2")
        }

        binding.button3.setOnClickListener {
            inputTV.text = addToInputText("3")
        }

        binding.button4.setOnClickListener {
            inputTV.text = addToInputText("4")
        }

        binding.button5.setOnClickListener {
            inputTV.text = addToInputText("5")
        }

        binding.button6.setOnClickListener {
            inputTV.text = addToInputText("6")
        }

        binding.button7.setOnClickListener {
            inputTV.text = addToInputText("7")
        }

        binding.button8.setOnClickListener {
            inputTV.text = addToInputText("8")
        }

        binding.button9.setOnClickListener {
            inputTV.text = addToInputText("9")
        }

        binding.buttonDot.setOnClickListener {
            inputTV.text = addToInputText(".")
        }

        binding.buttonDivision.setOnClickListener {
            inputTV.text = addToInputText("÷")
        }

        binding.buttonMultiply.setOnClickListener {
            inputTV.text = addToInputText("×")
        }

        binding.buttonSubtraction.setOnClickListener {
            inputTV.text = addToInputText("-")
        }

        binding.buttonAddition.setOnClickListener {
            inputTV.text = addToInputText("+")
        }
        binding.buttonEquals.setOnClickListener {
            showResult()
        }

        binding.buttonCroxx.setOnClickListener {
            val removedLast = inputTV.text.toString().dropLast(1)
            inputTV.text = removedLast
        }

        return binding.root
    }

    private fun addToInputText(buttonValue: String): String {
        return inputTV.text.toString() + "" + buttonValue
    }

    private fun getInputExpression(): String {
        var expression = inputTV.text.replace(Regex("÷"), "/")
        expression = expression.replace(Regex("×"), "*")
        return expression
    }

    private fun showResult() {
        try {
            val expression = getInputExpression()
            val result = Expression(expression).calculate()
            if (result.isNaN()) {
                // Show Error Message
                outputTV.text = ""
                outputTV.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            } else {
                // Show Result
                outputTV.text = DecimalFormat("0.######").format(result).toString()
                outputTV.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
            }
        } catch (e: Exception) {
            // Show Error Message
            outputTV.text = ""
            outputTV.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}
