package com.example.multicalculator.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.multicalculator.R
import com.example.multicalculator.databinding.FragmentCurrencyConverterBinding
import com.example.multicalculator.service.RetrofitClient
import com.example.multicalculator.service.RetrofitInstance
import com.example.multicalculator.service.Service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CurrencyConverter : Fragment() {

    private lateinit var binding: FragmentCurrencyConverterBinding
    private lateinit var currency1: Spinner
    private lateinit var currency2: Spinner
    private lateinit var amountTv: TextView
    private lateinit var quantityTv: TextView
    private lateinit var selectedTextView: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentCurrencyConverterBinding.inflate(inflater,container,false)
        binding.bottom.fragmentHeading.text = "Currency converter"

        currency1 = binding.spinner1
        currency2 = binding.spinner2
        amountTv = binding.amtTv
        quantityTv = binding.qtyTv

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.currency_types,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        currency1.adapter = adapter
        currency2.adapter = adapter

        currency1.setSelection(0)
        currency2.setSelection(1)

        quantityTv.setOnClickListener {
            selectedTextView = quantityTv
        }

        binding.buttonDot.setOnClickListener {
            selectedTextView.text = addToInputText(".")
        }

        binding.buttonCroxx.setOnClickListener {
            val currentText = selectedTextView.text.toString()

            if (currentText.isEmpty() || currentText == "0") {
                selectedTextView.text = ""
                val otherTv = if (selectedTextView == quantityTv) amountTv else quantityTv
                otherTv.text = ""
            } else {
                val removedLast = currentText.dropLast(1)
                selectedTextView.text = removedLast
            }
        }

        binding.buttonGo.setOnClickListener {
            val fromCurrency = currency1.selectedItem.toString()
            val toCurrency = currency2.selectedItem.toString()
            val quantity = quantityTv.text.toString().toDoubleOrNull()

            if (quantity != null){
                convertCurrency(fromCurrency,toCurrency,quantity) { convertedAmount ->
                    amountTv.text = String.format("%.2f %s",convertedAmount,toCurrency)
                }
            }
        }

        binding.buttonClear.setOnClickListener {
            selectedTextView.text = ""
            amountTv.text = ""
            quantityTv.text = ""
        }

        binding.bottom.backBtn.setOnClickListener{
            findNavController().navigate(R.id.action_currencyConverter_to_currency_screen)
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
            }
        }
    }

    private fun convertCurrency(from: String, to: String, quantity: Double, onResult: (Double) -> Unit) {
        RetrofitClient.instance.getExchangeRate(from, to).enqueue(object : Callback<RetrofitInstance> {
            override fun onResponse(
                call: Call<RetrofitInstance>,
                response: Response<RetrofitInstance>
            ) {
                if (response.isSuccessful){
                    val rate = response.body()?.conversion_rate ?: 1.0
                    val convertedAmount = quantity * rate
                    onResult(convertedAmount)
                }
            }

            override fun onFailure(call: Call<RetrofitInstance>, t: Throwable) {
                Toast.makeText(requireContext(), "Error fetching exchange rate", Toast.LENGTH_SHORT).show()
            }

        })
    }

}