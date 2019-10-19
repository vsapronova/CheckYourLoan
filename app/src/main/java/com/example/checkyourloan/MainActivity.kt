package com.example.checkyourloan

import android.graphics.Color
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.text.*
import android.text.style.ImageSpan
import android.view.Menu
import android.view.View
import android.widget.*
import java.util.regex.Pattern
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    lateinit var edits: Map<LoanParameter, EditText>
    lateinit var buttons: Map<LoanParameter, ToggleButton>

    lateinit var selectedParameter: LoanParameter

    lateinit var loan: Loan

    var isRunning: Boolean = false

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editTotalAmount = findViewById<EditText>(R.id.editLoanAmount)
        val editDownPayment = findViewById<EditText>(R.id.editDownPayment)
        val editInterestRate = findViewById<EditText>(R.id.editInterestRate)
        val editTerms = findViewById<EditText>(R.id.editLoanTerms)
        val editMonthlyPayment = findViewById<EditText>(R.id.editMonthlyPayment)
        val toggleTotalAmount = findViewById<ToggleButton>(R.id.toggleLoanAmount)
        val toggleDownPayment = findViewById<ToggleButton>(R.id.toggleDownPayment)
        val toggleInterestRate = findViewById<ToggleButton>(R.id.toggleInterestRate)
        val toggleTerms = findViewById<ToggleButton>(R.id.toggleLoanTerms)
        val toggleMonthlyPayment = findViewById<ToggleButton>(R.id.toggleMonthlyPayment)
        val spinner = findViewById<Spinner>(R.id.spinnerTerms)


        buttons =
            mapOf(
                LoanParameter.TOTAL_AMOUNT to toggleTotalAmount,
                LoanParameter.DOWN_PAYMENT to toggleDownPayment,
                LoanParameter.INTEREST_RATE to toggleInterestRate,
                LoanParameter.TERMS to toggleTerms,
                LoanParameter.MONTHLY_PAYMENT to toggleMonthlyPayment
            )

        edits =
            mapOf(
                LoanParameter.TOTAL_AMOUNT to editTotalAmount,
                LoanParameter.DOWN_PAYMENT to editDownPayment,
                LoanParameter.INTEREST_RATE to editInterestRate,
                LoanParameter.TERMS to editTerms,
                LoanParameter.MONTHLY_PAYMENT to editMonthlyPayment
            )

        val images =
            mapOf(
                LoanParameter.TOTAL_AMOUNT to R.drawable.ic_iconfinder_money_bag_309025,
                LoanParameter.DOWN_PAYMENT to R.drawable.ic_iconfinder_money_box_2639868,
                LoanParameter.INTEREST_RATE to R.drawable.ic_iconfinder_percent_1608788,
                LoanParameter.TERMS to R.drawable.ic_iconfinder_gym_2_753128,
                LoanParameter.MONTHLY_PAYMENT to R.drawable.ic_iconfinder_money_322468

            )

        for (parameter in LoanParameter.values()) {
            val button = buttons.getValue(parameter)
            val image = images.getValue(parameter)
            button.textOn = createImage(image)
            button.textOff = createImage(image)
            button.setOnCheckedChangeListener { checkedButton: CompoundButton, isChecked: Boolean ->
                if (isChecked) {
                    selectParameter(parameter)
                }
            }
        }

        editTotalAmount.addTextChangedListener(MoneyFormatWatcher({ editTextChanged(LoanParameter.TOTAL_AMOUNT) }))
        editDownPayment.addTextChangedListener(MoneyFormatWatcher({ editTextChanged(LoanParameter.DOWN_PAYMENT) }))
        editMonthlyPayment.addTextChangedListener(MoneyFormatWatcher({ editTextChanged(LoanParameter.MONTHLY_PAYMENT) }))

        editInterestRate.setFilters(arrayOf<InputFilter>(DecimalDigitsInputFilter(5, 2)))
        editInterestRate.addTextChangedListener(EditWatcher(LoanParameter.INTEREST_RATE))

        editTerms.addTextChangedListener(EditWatcher(LoanParameter.TERMS))

        val adapter = ArrayAdapter.createFromResource(this, R.array.termsUnit, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.setAdapter(adapter)

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedTermsUnit = TermsUnit.values().find { it.value == spinner.selectedItemPosition }!!
                loan.termsUnit = selectedTermsUnit
                calculateListener()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        initLoan(defaultLoan)
    }

    val defaultLoan = Loan(
        amount = 300000.0,
        downPayment = 60000.0,
        interestRate = 3.50,
        terms = 360.0,
        monthlyPayment = null,
        termsUnit = TermsUnit.MONTHS,
        calculatedParam = LoanParameter.MONTHLY_PAYMENT
    )

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.mymenu, menu)
        return true
    }

    fun setParameterState(param: LoanParameter, calc: Boolean) {
        val button = buttons[param]
        button!!.isEnabled = !calc
        button.isChecked = calc

        val edit = edits[param]!!
        edit.isEnabled = !calc
        edit.setTypeface(null, if (calc) Typeface.BOLD else Typeface.NORMAL)
        edit.setTextColor(Color.BLACK)
    }

    fun createImage(imageId: Int): SpannableString {
        val imageSpan = ImageSpan(this, imageId)
        val content = SpannableString("X")
        content.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return content
    }

    fun selectParameter(parameter: LoanParameter) {
        LoanParameter.values().forEach {
            setParameterState(it, calc = parameter == it)
        }

        loan.calculatedParam = parameter
        selectedParameter = parameter

        calculateListener()
    }

    fun editTextChanged(parameter: LoanParameter) {
        if (parameter != selectedParameter) {
            loan.setValue(parameter, getDouble(edits[parameter]!!))
            calculateListener()
        }
    }

    inner class EditWatcher(val parameter: LoanParameter) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            editTextChanged(parameter)
        }
    }

    fun calculateListener() {
        if (!isRunning) return

        for (edit  in edits.values) {
            edit.setError(null)
        }

        try {
            loan.calcParameter(selectedParameter)
            initEdit(selectedParameter)
        }
        catch (ex: CalcException) {
            val edit = edits[selectedParameter]
            val errors = ex.errors
            for (error in errors) {
                edits[error.field]!!.setError(error.message)
            }
            edit!!.setText("")
        }
    }

    fun initLoan(loan: Loan) {
        this.loan = loan
        initLoanView()
        calculateListener()
    }

    fun initLoanView() {
        isRunning = false

        selectedParameter = loan.calculatedParam
        selectParameter(loan.calculatedParam)

        initEdit(LoanParameter.TOTAL_AMOUNT)
        initEdit(LoanParameter.DOWN_PAYMENT)
        initEdit(LoanParameter.INTEREST_RATE)
        initEdit(LoanParameter.TERMS)
        initEdit(LoanParameter.MONTHLY_PAYMENT)

        // TODO: set spinner value from loan.termsUnit and it also should be inside initLoanView

        isRunning = true
    }

    fun initEdit(parameter: LoanParameter) {
        val edit = edits.getValue(parameter)
        val value = loan.getValue(parameter)
        val valueStr = formatValue(value, parameter)
        edit.setText(valueStr)
    }

    fun formatValue(valueOnly: Double?, selectedParameter: LoanParameter): String {
        val rounded =
            if (valueOnly != null) {
                when (selectedParameter) {
                    LoanParameter.TOTAL_AMOUNT -> {
                        valueOnly.roundToInt().toString()
                    }
                    LoanParameter.DOWN_PAYMENT -> {
                        valueOnly.roundToInt().toString()
                    }
                    LoanParameter.INTEREST_RATE -> {
                        ("%.2f".format(valueOnly))
                    }
                    LoanParameter.TERMS -> {
                        valueOnly.roundToInt().toString()
                    }
                    LoanParameter.MONTHLY_PAYMENT -> {
                        valueOnly.roundToInt().toString()
                    }
                }
            } else ""
        return rounded
    }

    class DecimalDigitsInputFilter(digitsBeforeZero: Int, digitsAfterZero: Int) : InputFilter {

        internal var mPattern: Pattern

        init {
            mPattern =
                Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?")
        }

        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {

            val matcher = mPattern.matcher(dest)
            return if (!matcher.matches()) "" else null
        }

    }

}




