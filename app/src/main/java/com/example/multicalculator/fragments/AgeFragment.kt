package com.example.multicalculator.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.multicalculator.R
import com.example.multicalculator.databinding.FragmentAgeBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Locale


class AgeFragment : Fragment() {


    private lateinit var binding: FragmentAgeBinding

    private var dob: LocalDate? = null
    private var currentDate: LocalDate? = null

    private lateinit var selectedTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAgeBinding.inflate(inflater, container, false)

        binding.bottom.fragmentHeading.text = "Age"

        selectedTextView = binding.dobText

        highlightSelectedTextView()

        binding.dobText.setOnClickListener {
            selectedTextView = binding.dobText
            highlightSelectedTextView()
            showDatePicker { date ->
                dob = date
                binding.dobText.text = formatDate(date)
                calculateAge()
                calculateNextBirthday()
            }
        }

        binding.currentDate.setOnClickListener {
            selectedTextView = binding.currentDate
            highlightSelectedTextView()
            showDatePicker { date ->
                currentDate = date
                binding.currentDate.text = formatDate(date)
                calculateAge()
                calculateNextBirthday()
            }
        }


        binding.buttonAge.setOnClickListener {
            calculateAge()
        }

        return binding.root
    }

    private fun showDatePicker(onDateSelected: (LocalDate) -> Unit) {
        val calendar = Calendar.getInstance()

        // Check if a date is already selected, and if so, set it as the initial date
        val initialDate = dob ?: currentDate
        val initialCalendar = if (initialDate != null) {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            try {
                val date = sdf.parse(initialDate.toString())
                val calendarInstance = Calendar.getInstance()
                date?.let { calendarInstance.time = it }
                calendarInstance
            } catch (e: ParseException) {
                e.printStackTrace()
                calendar // default to today's date if the parsing fails
            }
        } else {
            calendar // default to today's date
        }


        DatePickerDialog(
            requireContext(),
            R.style.CustomDatePickerDialog,
            { _, year, month, dayOfMonth ->
                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                onDateSelected(selectedDate)
            },
            initialCalendar.get(Calendar.YEAR),
            initialCalendar.get(Calendar.MONTH),
            initialCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun calculateAge() {
        if (dob != null && currentDate != null) {
            if (dob!!.isAfter(currentDate)) {
                Toast.makeText(
                    requireContext(),
                    "DOB cannot be after Current Date",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            val period = Period.between(dob, currentDate)
            val ageYearText = "${period.years} years"
            val ageMonthText = "${period.months} months"
            val ageDaysText = "${period.days} days"
            binding.ageYear.text = ageYearText
            binding.ageMonths.text = ageMonthText + " " + ageDaysText

        }
    }

    private fun calculateNextBirthday() {
        if (dob != null) {
            val todayText = binding.currentDate.text.toString()
            val today = try {
                LocalDate.parse(todayText, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            } catch (e: Exception) {
                LocalDate.now() // Fallback to the current date if parsing fails
            }
            var nextBirthday = dob!!.withYear(today.year)

            if (nextBirthday.isBefore(today) || nextBirthday.isEqual(today)) {
                nextBirthday = nextBirthday.plusYears(1)
            }

            //val daysUntilNextBday = ChronoUnit.DAYS.between(today,nextBirthday)

            val period = Period.between(today, nextBirthday)
            val monthsUntilNextBirthday = period.months
            val daysUntilNextBirthday = period.days

            binding.bdayMmonths.text =
                "$monthsUntilNextBirthday months $daysUntilNextBirthday days"

            val nextBirthdayDay = nextBirthday.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
            binding.bdayDay.text = nextBirthdayDay
        }

    }

    private fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return date.format(formatter)
    }

    private fun highlightSelectedTextView() {
        binding.dobText.setBackgroundColor(
            if (selectedTextView == binding.dobText) Color.YELLOW else Color.WHITE
        )
        binding.currentDate.setBackgroundColor(
            if (selectedTextView == binding.currentDate) Color.YELLOW else Color.WHITE
        )
    }
}