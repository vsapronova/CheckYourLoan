package com.example.checkyourloan


import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import kotlin.math.pow
import kotlin.math.round


class MainActivity : AppCompatActivity() {
    lateinit var editText: EditText
    lateinit var editText2: EditText
    lateinit var editText3: EditText
    lateinit var editText4: EditText
    lateinit var editText5: EditText
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        editText = findViewById(R.id.editText)
        editText2 = findViewById(R.id.editText2)
        editText3 = findViewById(R.id.editText3)
        editText4 = findViewById(R.id.editText4)
        editText5 = findViewById(R.id.editText5)


        val watcher = object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                calculateListener()
            }
        }

        editText.addTextChangedListener(watcher)
        editText2.addTextChangedListener(watcher)
        editText3.addTextChangedListener(watcher)
        editText4.addTextChangedListener(watcher)

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