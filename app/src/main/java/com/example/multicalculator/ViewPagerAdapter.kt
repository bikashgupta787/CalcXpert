package com.example.multicalculator

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.multicalculator.fragments.CalculatorFragment
import com.example.multicalculator.fragments.CurrencyFragment
import com.example.multicalculator.fragments.HomeFragment
import java.lang.IllegalStateException

class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
       return when (position){
           0 -> CalculatorFragment()
           1 -> HomeFragment()
           2 -> CurrencyFragment()
           else -> throw IllegalStateException("Unexpected position $position")
       }
    }
}