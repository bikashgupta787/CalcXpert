package com.example.multicalculator

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.viewpager2.widget.ViewPager2
import com.example.multicalculator.databinding.ActivityMainBinding
import com.example.multicalculator.fragments.CalculatorFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val bottomNav = binding.bottomNavigation
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(bottomNav, navController)

        val fragmentsWithBottomNav = setOf(
            R.id.calculator_screen,
            R.id.home_screen,
            R.id.currency_screen
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in fragmentsWithBottomNav) {
                bottomNav.visibility = View.VISIBLE
            } else {
                bottomNav.visibility = View.GONE

            }
        }


//        val viewPager = binding.viewPager
//
//        val adapter = ViewPagerAdapter(this)
//        viewPager.adapter = adapter
//
//        bottomNav.setOnItemSelectedListener { menuItem ->
//            when(menuItem.itemId){
//                R.id.calculator_screen -> viewPager.currentItem = 0
//                R.id.home_screen -> viewPager.currentItem = 1
//                R.id.currency_screen -> viewPager.currentItem = 2
//            }
//            true
//        }
//
//        viewPager.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                when(position){
//                    0 -> bottomNav.selectedItemId = R.id.calculator_screen
//                    1 -> bottomNav.selectedItemId = R.id.home_screen
//                    2 -> bottomNav.selectedItemId = R.id.currency_screen
//                }
//            }
//        })


    }
}
