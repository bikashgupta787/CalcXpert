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
    private lateinit var quantityTv: EditText
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentCurrencyConverterBinding.inflate(inflater,container,false)

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

        binding.convertBtn.setOnClickListener {
            val fromCurrency = currency1.selectedItem.toString()
            val toCurrency = currency2.selectedItem.toString()
            val quantity = quantityTv.text.toString().toDoubleOrNull()

            if (quantity != null){
                convertCurrency(fromCurrency,toCurrency,quantity) { convertedAmount ->
                    amountTv.text = String.format("%.2f %s",convertedAmount,toCurrency)
                }
            }
        }

        return binding.root
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