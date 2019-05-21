package com.example.checkyourloan

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ToggleButton
import kotlin.math.abs
import kotlin.math.log
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

    lateinit var selectedParameter: LoanParameter

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

        for (button in buttons) button.setOnCheckedChangeListener(checkedChangeListener)
        for (edit in edits) edit.addTextChangedListener(EditWatcher(edit))

        selectParameter(LoanParameter.MONTHLY_PAYMENT)
    }

    fun setParameterState(param: LoanParameter, calc: Boolean) {
        val button = buttons[param.value]
        button.isEnabled = !calc
        button.isChecked = calc

        edits[param.value].isEnabled = !calc
    }

    fun selectParameter(param: LoanParameter) {
        selectedParameter = param

        LoanParameter.values().forEach {
            setParameterState(it, calc = selectedParameter == it)
        }

        calculateListener()
    }

    val checkedChangeListener = { checkedButton: CompoundButton, isChecked: Boolean ->
        if (isChecked) {
            val index = buttons.indexOf(checkedButton)
            val parameter = LoanParameter.values().find { it.value == index } !!
            selectParameter(parameter)
        }
    }

    inner class EditWatcher(val edit: EditText): TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            if (edit != edits[selectedParameter.value]) {
                calculateListener()
            }
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
        var resultMonthlyPayment = (finalAmount * monthlyRate) / (1 - (1 + monthlyRate).pow(-loanTerms))
        return resultMonthlyPayment
    }

    fun calculateLoanAmount(downPayment: Double, interestRate: Double, loanTerms: Double, monthlyPayment: Double): Double {
        val monthlyRate = interestRate / 12 / 100
        var resultLoanAmount = (monthlyPayment*(1-(1+monthlyRate).pow(-loanTerms)))/monthlyRate + downPayment
        return resultLoanAmount
    }

    fun calculateDownPayment(loanAmount: Double, interestRate: Double, loanTerms: Double, monthlyPayment: Double): Double {
        val monthlyRate = interestRate / 12 / 100
        var resultDownPayment = loanAmount - (monthlyPayment*(1-(1+monthlyRate).pow(-loanTerms)))/monthlyRate
        return resultDownPayment
    }

    fun calculateLoanTerm(loanAmount: Double, downPayment: Double, interestRate: Double, monthlyPayment: Double): Double {
        val finalAmount = loanAmount - downPayment
        val monthlyRate = interestRate / 12 / 100
        var resultLoanTerm = -log((1 - (finalAmount*monthlyRate)/monthlyPayment), 1+monthlyRate)
        return resultLoanTerm
    }

    fun calculateInterestRate(loanAmount: Double, downPayment: Double, loanTerms: Double, monthlyPayment: Double): Double {
        fun monthly(rate: Double) = calculateMonthlyPayment(loanAmount, downPayment, rate, loanTerms)

        var step = 5.0
        var r0 = 0.1
        var r1 = 0.1

        var monthlyr0 = monthly(r0)
        var monthlyr1 = monthly(r1)

        while ( !(monthlyr0 < monthlyPayment && monthlyr1 > monthlyPayment) ) {
            if (monthlyr0 > monthlyPayment) {
                r1 = r0
                r0 -= step
            }
            else if (monthlyr1 < monthlyPayment) {
                r0 = r1
                r1 += step
            }
            monthlyr0 = monthly(r0)
            monthlyr1 = monthly(r1)
        }

        while ( abs(monthlyPayment-monthlyr0) > 0.0001) {
            val middle = r0 + (r1 - r0) / 2
            val monthlymid = monthly(middle)
            if (monthlyPayment < monthlymid) {
                r1 = middle
            } else {
                r0 = middle
            }
            monthlyr0 = monthly(r0)
        }

        return r0
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
                LoanParameter.DOWN_PAYMENT -> {
                    if (loanAmount != null && interestRate != null && loanTerms != null && monthlyPayment != null) {
                        calculateDownPayment(loanAmount, interestRate, loanTerms, monthlyPayment)
                    } else {
                        null
                    }
                }
                LoanParameter.LOAN_AMOUNT -> {
                    if (downPayment != null && interestRate != null && loanTerms != null && monthlyPayment != null) {
                        calculateLoanAmount(downPayment, interestRate, loanTerms, monthlyPayment)
                    } else {
                        null
                    }
                }
                LoanParameter.LOAN_TERMS -> {
                    if (downPayment != null && interestRate != null && loanAmount != null && monthlyPayment != null) {
                        calculateLoanTerm(loanAmount, downPayment, interestRate, monthlyPayment)
                    } else {
                        null
                    }
                }
                LoanParameter.INTEREST_RATE -> {
                    if (loanAmount != null && downPayment != null && loanTerms != null && monthlyPayment != null) {
                        calculateInterestRate(loanAmount, downPayment, loanTerms, monthlyPayment)
                    } else {
                        null
                    }
                }
            }

        val rounded = if (value != null) round(value) else null
        val edit = edits[selectedParameter.value]
        edit.setText(rounded?.toString() ?: "")
    }
}