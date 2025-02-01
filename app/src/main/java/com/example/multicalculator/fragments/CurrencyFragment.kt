package com.example.multicalculator.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.example.multicalculator.R
import com.example.multicalculator.SharedPrefsManager
import com.example.multicalculator.databinding.FragmentCurrencyBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class CurrencyFragment : Fragment() {
    private lateinit var binding : FragmentCurrencyBinding
    private val themeTitleList = arrayOf("Light","Dark","Auto")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCurrencyBinding.inflate(inflater,container,false)
        val sharedPrefsManager = SharedPrefsManager(requireContext())
        var checkedTheme = sharedPrefsManager.theme

        val themeDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Theme")
            .setPositiveButton("Ok"){_,_ ->
                AppCompatDelegate.setDefaultNightMode(sharedPrefsManager.themeFlag[checkedTheme])
            }
            .setSingleChoiceItems(themeTitleList,checkedTheme){_, which ->
                checkedTheme = which
            }
            .setCancelable(false)


        binding.themeBtn.setOnClickListener {
            themeDialog.show()
        }

        return  binding.root
    }

}