package com.example.checkyourloan

import android.graphics.Color
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.*
import android.text.style.ImageSpan
import android.view.Menu
import android.view.View
import android.widget.*
import java.util.regex.Pattern
import kotlin.math.roundToInt


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
    lateinit var spinner: Spinner

    lateinit var selectedParameter: LoanParameter

    lateinit var loan: Loan

    var isRunning: Boolean = false

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
        spinner = findViewById(R.id.spinnerTerms)


        buttons =
            arrayListOf(toggleLoanAmount, toggleDownPayment, toggleInterestRate, toggleLoanTerms, toggleMonthlyPayment)
        edits = arrayListOf(editLoanAmount, editDownPayment, editInterestRate, editLoanTerms, editMonthlyPayment)

        val images =
            arrayListOf(
                R.drawable.ic_iconfinder_money_bag_309025,
                R.drawable.ic_iconfinder_money_box_2639868,
                R.drawable.ic_iconfinder_percent_1608788,
                R.drawable.ic_iconfinder_gym_2_753128,
                R.drawable.ic_iconfinder_money_322468
            )

        for (i in 0..buttons.size-1) {
            val button = buttons[i]
            val image = images[i]
            button.textOn = createImage(image)
            button.textOff = createImage(image)
            button.setOnCheckedChangeListener(checkedChangeListener)
        }

        editLoanAmount.addTextChangedListener(MoneyFormatWatcher({ editTextChanged(editLoanAmount) }))
        editDownPayment.addTextChangedListener(MoneyFormatWatcher({ editTextChanged(editDownPayment) }))
        editMonthlyPayment.addTextChangedListener(MoneyFormatWatcher({ editTextChanged(editMonthlyPayment) }))

        editInterestRate.setFilters(arrayOf<InputFilter>(DecimalDigitsInputFilter(5, 2)))
        editInterestRate.addTextChangedListener(EditWatcher(editInterestRate))

        editLoanTerms.addTextChangedListener(EditWatcher(editLoanTerms))

        val options = arrayOf("months", "years")
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.setAdapter(adapter)

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // set selected terms unit to loan
                calculateListener()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        loan = Loan(amount = 300000.0, downPayment = 60000.0, interestRate = 3.50, terms = 360.0, monthlyPayment = null, termsUnit = TermsUnit.MONTHS)

        // set spinner value from loan.termsUnit and it also should be insice initLoanView
        selectParameter(LoanParameter.MONTHLY_PAYMENT) // this should be from loan and inside initLoanView
        initLoanView()

        isRunning = true
        calculateListener()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.mymenu, menu)
        return true
    }

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
        edits[selectedParameter.value].setTypeface(null, Typeface.BOLD)
        edits[selectedParameter.value].setTextColor(Color.BLACK)

        calculateListener()
    }

    val checkedChangeListener = { checkedButton: CompoundButton, isChecked: Boolean ->
        if (isChecked) {
            val index = buttons.indexOf(checkedButton)
            val parameter = LoanParameter.values().find { it.value == index }!!
            edits[selectedParameter.value].setTypeface(null, Typeface.NORMAL)
            edits[selectedParameter.value].setTextColor(Color.BLACK)
            selectParameter(parameter)
        }
    }

    fun editTextChanged(edit: EditText) {
        if (edit != edits[selectedParameter.value]) {
            // set value from edit into loan
            calculateListener()
        }
    }

    inner class EditWatcher(val edit: EditText) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            editTextChanged(edit)
        }
    }



    fun calculateListener() {
        if (!isRunning) return

        val amount = getDouble(editLoanAmount)
        val downPayment = getDouble(editDownPayment)
        val interestRate = getDouble(editInterestRate)
        val terms = getDouble(editLoanTerms)
        val monthlyPayment = getDouble(editMonthlyPayment)
        val selectedTermsUnit = TermsUnit.values().find { it.value == spinner.selectedItemPosition }!!

        loan.amount = amount
        loan.downPayment = downPayment
        loan.interestRate = interestRate
        loan.terms = terms
        loan.monthlyPayment = monthlyPayment
        loan.termsUnit = selectedTermsUnit

        for (edit in edits) {
            edit.setError(null)
        }

        try {
            loan.calcParameter(selectedParameter)
            initEdit(selectedParameter)
        }
        catch (ex: CalcException) {
            val edit = edits[selectedParameter.value]
            val errors = ex.errors
            for (error in errors) {
                edits[error.field.value].setError(error.message)
            }
            edit.setText("")
            // show error
        }
    }

    fun initLoanView() {
        initEdit(LoanParameter.TOTAL_AMOUNT)
        initEdit(LoanParameter.DOWN_PAYMENT)
        initEdit(LoanParameter.INTEREST_RATE)
        initEdit(LoanParameter.TERMS)
        initEdit(LoanParameter.MONTHLY_PAYMENT)
    }

    fun initEdit(parameter: LoanParameter) {
        val edit = edits[parameter.value]
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




