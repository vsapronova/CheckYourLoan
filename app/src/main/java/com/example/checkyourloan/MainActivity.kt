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
    lateinit var editLoanAmount: EditText
    lateinit var editDownPayment: EditText
    lateinit var editInterestRate: EditText
    lateinit var editLoanTerms: EditText
    lateinit var editMonthlyPayment: EditText
    lateinit var toggleLoanAmount: ToggleButton
    lateinit var toggleDownPayment: ToggleButton
    lateinit var toggleInterestRate: ToggleButton
    lateinit var toggleLoanTerms: ToggleButton
    lateinit var toggleMonthlyPayment: ToggleButton
    lateinit var edits: ArrayList<EditText>
    lateinit var buttons: ArrayList<ToggleButton>

    var selectedParameter = LoanParameter.MONTHLY_PAYMENT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editLoanAmount = findViewById(R.id.editLoanAmount)
        editDownPayment = findViewById(R.id.editDownPayment)
        editInterestRate = findViewById(R.id.editInterestRate)
        editLoanTerms = findViewById(R.id.editLoanTerms)
        editMonthlyPayment = findViewById(R.id.editMonthlyPayment)
        toggleLoanAmount = findViewById(R.id.toggleLoanAmount)
        toggleDownPayment = findViewById(R.id.toggleDownPayment)
        toggleInterestRate = findViewById(R.id.toggleInterestRate)
        toggleLoanTerms = findViewById(R.id.toggleLoanTerms)
        toggleMonthlyPayment = findViewById(R.id.toggleMonthlyPayment)

        buttons = arrayListOf(toggleLoanAmount, toggleDownPayment, toggleInterestRate, toggleLoanTerms, toggleMonthlyPayment)
        edits = arrayListOf(editLoanAmount, editDownPayment, editInterestRate, editLoanTerms, editMonthlyPayment)

        for (button in buttons) {
            button.setOnCheckedChangeListener(checkedChangeListener)
        }

        for (edit in edits) edit.addTextChangedListener(editListener)
    }

    val checkedChangeListener = { checkedButton: CompoundButton, isChecked: Boolean ->
        if (isChecked) {
            checkedButton.text = if (isChecked) "ON" else "OFF"

            for (button in buttons) {
                if (button != checkedButton) {
                    button.isChecked = false
                }
            }

            TODO("Disable selected edit and toggle")

            TODO("Set selectedParameter here")

            TODO("Trigger calculate?")
        }
    }

    val editListener = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            calculateListener()
        }
    }

    enum class LoanParameter(val value: Int) {
        LOAN_AMOUNT(0),
        DOWN_PAYMENT(1),
        INTEREST_RATE(2),
        LOAN_TERMS(3),
        MONTHLY_PAYMENT(4),
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
        val loanAmount = getDouble(editLoanAmount)
        val downPayment = getDouble(editDownPayment)
        val interestRate = getDouble(editInterestRate)
        val loanTerms = getDouble(editLoanTerms)
        val monthlyPayment = getDouble(editMonthlyPayment)

        val value: Double? =
            when(selectedParameter) {
                LoanParameter.MONTHLY_PAYMENT -> {
                        if (loanAmount != null && downPayment != null && interestRate != null && loanTerms != null) {
                            calculateMonthlyPayment(loanAmount, downPayment, interestRate, loanTerms)
                        } else {
                            null
                        }
                }
                else -> null
            }
        val edit = edits[selectedParameter.value]
        edit.setText(value?.toString() ?: "")

    }
}