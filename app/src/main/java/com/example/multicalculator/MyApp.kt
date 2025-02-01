package com.example.multicalculator

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class MyApp:Application() {

    override fun onCreate() {
        super.onCreate()
        val sharedPrefsManager = SharedPrefsManager(this)
        AppCompatDelegate.setDefaultNightMode(sharedPrefsManager.themeFlag[sharedPrefsManager.theme])

    }
}