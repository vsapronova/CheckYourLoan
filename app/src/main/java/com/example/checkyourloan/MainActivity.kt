package com.example.checkyourloan

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ToggleButton
import kotlin.math.pow
import kotlin.math.round


class MainActivity : AppCompatActivity() {
    lateinit var editText: EditText
    lateinit var editText2: EditText
    lateinit var editText3: EditText
    lateinit var editText4: EditText
    lateinit var editText5: EditText
    lateinit var toggleButton: ToggleButton
    lateinit var toggleButton1: ToggleButton
    lateinit var toggleButton2: ToggleButton
    lateinit var toggleButton3: ToggleButton
//    lateinit var toggleButton4: ToggleButton
    lateinit var edits: ArrayList<EditText>
    lateinit var buttons: ArrayList<ToggleButton>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.editText)
        editText2 = findViewById(R.id.editText2)
        editText3 = findViewById(R.id.editText3)
        editText4 = findViewById(R.id.editText4)
        editText5 = findViewById(R.id.editText5)
        toggleButton = findViewById(R.id.toggleButton1)
        toggleButton1 = findViewById(R.id.toggleButton2)
        toggleButton2 = findViewById(R.id.toggleButton3)
        toggleButton3 = findViewById(R.id.toggleButton4)

        buttons = arrayListOf(toggleButton, toggleButton1, toggleButton2, toggleButton3)
        edits = arrayListOf(editText, editText2, editText3, editText4)

        for (button in buttons) {
            button.setOnCheckedChangeListener(checkedChangeListener)
        }

        for (edit in edits) edit.addTextChangedListener(editListener)
    }

    val checkedChangeListener = { button: CompoundButton, isChecked: Boolean ->
        button.text = if (isChecked) "ON" else "OFF"
    }

    val editListener = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            calculateListener()
        }
    }

    fun calculateMonthlyPayment(loanAmount: Double, downPayment: Double, interestRate: Double, loanTerms: Double): Double {
        val finalAmount = loanAmount - downPayment
        val monthlyRate = interestRate / 12 / 100
        var result = (finalAmount * monthlyRate) / (1 - (1 + monthlyRate).pow(-loanTerms))
        return round(result)
    }

    fun getDouble(edit: EditText): Double? {
        if (edit.text.length > 0) {
            return edit.text.toString().toDouble()
        } else {
//            edit.setError("Everything is bad!")
            return null
        }
    }

    fun calculateListener() {
        val loanAmount = getDouble(editText)
        val downPayment = getDouble(editText2)
        val interestRate = getDouble(editText3)
        val loanTerms = getDouble(editText4)

        if (loanAmount != null && downPayment != null && interestRate != null && loanTerms != null) {
            val payment = calculateMonthlyPayment(loanAmount, downPayment, interestRate, loanTerms)
            editText5.setText(payment.toString())
        } else {
            editText5.setText("")
        }
    }
}