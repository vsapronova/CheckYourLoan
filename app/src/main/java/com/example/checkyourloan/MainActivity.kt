package com.example.checkyourloan

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import kotlin.math.round
import android.text.Spanned
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.Menu
import android.widget.*
import java.lang.RuntimeException
import java.text.DecimalFormatSymbols


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

        for (button in buttons) {
            button.textOn = createImage(android.R.drawable.btn_star_big_on)
            button.textOff = createImage(android.R.drawable.btn_star_big_off)
            button.setOnCheckedChangeListener(checkedChangeListener)
        }
        for (edit in edits) {
            edit.addTextChangedListener(ExperimentalWatcher(edit))
        }

        selectParameter(LoanParameter.MONTHLY_PAYMENT)

        val spinner: Spinner = findViewById(R.id.spinnerTerms)
        val adapter = ArrayAdapter.createFromResource(this, R.array.terms, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.setAdapter(adapter)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.mymenu, menu)
        return true
    }



//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//         Handle presses on the action bar menu items
//        when (item.itemId) {
//            R.id.action_cut -> {
//                text_view.text = "Cut"
//                return true
//            }
//            R.id.action_copy -> {
//                text_view.text = "Copy"
//                return true
//            }
//            R.id.action_paste -> {
//                text_view.text = "Paste"
//                return true
//            }
//            R.id.action_new -> {
//                text_view.text = "New"
//                return true
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    fun setParameterState(param: LoanParameter, calc: Boolean) {
        val button = buttons[param.value]
        button.isEnabled = !calc
        button.isChecked = calc

        edits[param.value].isEnabled = !calc
    }

    fun createImage(imageId: Int): SpannableString {
        val imageSpan = ImageSpan(this, imageId)
        val content = SpannableString("X")
        content.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return content
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

    inner class ExperimentalWatcher(val edit: EditText): TextWatcher {
        var killedComma: Int? = null
        var isProcessing = false

        val thousandsSeparator = ','

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            if (isProcessing) return
            if (after == 0 && count == 1 && s[start] == thousandsSeparator) {
                killedComma = start
            }
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            if (isProcessing) return
            isProcessing = true

            try {
                val killedCommaValue = killedComma
                if (killedCommaValue != null) {
                    s.delete(killedCommaValue-1, killedCommaValue)
                    killedComma = null
                }

                var index = s.length - 1
                var digitCounter = 0
                while (index >= 0) {
                    val ch = s.get(index)
                    when(ch) {
                        in '0'..'9' ->
                            if (digitCounter == 3) {
                                s.insert(index+1, thousandsSeparator.toString())
                                digitCounter = 0
                            } else {
                                digitCounter++
                                index--
                            }
                        thousandsSeparator ->
                            if (digitCounter == 3) {
                                digitCounter = 0
                                index--
                            } else {
                                s.delete(index, index + 1)
                                index--
                            }
                    }
                }
            } catch (ex: RuntimeException) {
                println(ex.message)
            }


            isProcessing = false
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

        val loan = Loan(loanAmount, downPayment, interestRate, loanTerms, monthlyPayment)

        val value: Double? =
            when(selectedParameter) {
                LoanParameter.MONTHLY_PAYMENT -> {
                    getMonthlyPayment(loan)
                }
                LoanParameter.DOWN_PAYMENT -> {
                    getDownPayment(loan)
                }
                LoanParameter.LOAN_AMOUNT -> {
                    getLoanAmount(loan)
                }
                LoanParameter.LOAN_TERMS -> {
                    getLoanTerms(loan)
                }
                LoanParameter.INTEREST_RATE -> {
                    getInterestRate(loan)
                }
            }

        val rounded = if (value != null) round(value) else null
        val edit = edits[selectedParameter.value]
        edit.setText(rounded?.toString() ?: "")
    }

    class Loan (
        var loanAmount: Double?,
        var downPayment: Double?,
        var interestRate: Double?,
        var loanTerms: Double?,
        var monthlyPayment: Double?
    )

    fun getInterestRate(loan: Loan): Double? {
        return if (loan.loanAmount != null && loan.downPayment != null && loan.loanTerms != null && loan.monthlyPayment != null) {
            calculateInterestRate(loan.loanAmount!!, loan.downPayment!!, loan.loanTerms!!, loan.monthlyPayment!!)
        } else {
            null
        }
    }

    fun getLoanTerms(loan: Loan): Double? {
        return if (loan.downPayment != null && loan.interestRate != null && loan.loanAmount != null && loan.monthlyPayment != null) {
            calculateLoanTerm(loan.loanAmount!!, loan.downPayment!!, loan.interestRate!!, loan.monthlyPayment!!)
        } else {
            null
        }
    }

    fun getLoanAmount(loan: Loan): Double? {
        return if (loan.downPayment != null && loan.interestRate != null && loan.loanTerms != null && loan.monthlyPayment != null) {
            calculateLoanAmount(loan.downPayment!!, loan.interestRate!!, loan.loanTerms!!, loan.monthlyPayment!!)
        } else {
            null
        }
    }

    fun getDownPayment(loan: Loan): Double? {
        return if (loan.loanAmount != null && loan.interestRate != null && loan.loanTerms != null && loan.monthlyPayment != null) {
            calculateDownPayment(loan.loanAmount!!, loan.interestRate!!, loan.loanTerms!!, loan.monthlyPayment!!)
        } else {
            null
        }
    }

    fun getMonthlyPayment(loan: Loan): Double? {
        return if (loan.loanAmount != null && loan.downPayment != null && loan.interestRate != null && loan.loanTerms != null) {
            calculateMonthlyPayment(loan.loanAmount!!, loan.downPayment!!, loan.interestRate!!, loan.loanTerms!!)
        } else {
            null
        }
    }
}