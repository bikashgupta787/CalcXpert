package com.example.multicalculator.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
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
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.example.multicalculator.R
import com.example.multicalculator.databinding.FragmentBmiBinding
import com.google.android.material.tabs.TabLayout
import java.io.File
import java.io.FileOutputStream


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
    private lateinit var shareBtn: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBmiBinding.inflate(inflater, container, false)
        binding.bottom.fragmentHeading.text = "BMI"
        cardView = binding.bmiCardView
        tabLayout = binding.tableLayout
        shareBtn = binding.shareBtn

        heightVal = binding.dataTypeTv2
        weightVal = binding.dataTypeTv1

        heightVal.text = "160"
        weightVal.text = "70"

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
                shareBtn.visibility = View.GONE
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        heightspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                cardView.visibility = View.GONE
                tabLayout.visibility = View.VISIBLE
                shareBtn.visibility = View.GONE
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
            shareBtn.visibility = View.GONE
        }

        weightVal.setOnClickListener {
            selectedTextView = weightVal
            highlightSelectedTextView()
            cardView.visibility = View.GONE
            tabLayout.visibility = View.VISIBLE
            shareBtn.visibility = View.GONE
        }

        binding.buttonGo.setOnClickListener {
            val weightText = weightVal.text.toString().trim()
            val heightText = heightVal.text.toString().trim()

            if (weightText.isEmpty() || heightText.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter values", Toast.LENGTH_SHORT).show()
            } else {
                cardView.visibility = View.VISIBLE
                tabLayout.visibility = View.GONE
                shareBtn.visibility = View.VISIBLE
                calculateBMI()
            }
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

        shareBtn.setOnClickListener {
            val layout = cardView
            val bitmap = convertLayoutToImg(layout)
            val imageUri = saveBitmapToFile(requireContext(), bitmap)

            imageUri?.let { shareImg(it) }
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

    private fun convertLayoutToImg(layout: View): Bitmap {
        val bitmap = Bitmap.createBitmap(layout.width, layout.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        layout.draw(canvas)
        return bitmap
    }

    private fun saveBitmapToFile(context: Context, bitmap: Bitmap): Uri? {
        return try {
            val filesDir =
                File(context.cacheDir, "images") // Use cacheDir instead of externalCacheDir
            filesDir.mkdirs() // Ensure directory exists

            val imageFile = File(filesDir, "bmi_result.png")
            val outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            FileProvider.getUriForFile(context, "${context.packageName}.provider", imageFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun shareImg(uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Share via"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}