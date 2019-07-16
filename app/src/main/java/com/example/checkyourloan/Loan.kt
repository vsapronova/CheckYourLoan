package com.example.checkyourloan

import kotlinx.android.synthetic.main.activity_main.view.*

class Loan (
    var loanAmount: Double?,
    var downPayment: Double?,
    var interestRate: Double?,
    var loanTermsMonths: Double?,
    var monthlyPayment: Double?
) {

    fun calcInterestRate() {
        if (loanAmount != null && downPayment != null && loanTermsMonths != null && monthlyPayment != null) {
            interestRate = calculateInterestRate(
                loanAmount!!,
                downPayment!!,
                loanTermsMonths!!,
                monthlyPayment!!
            )
        } else {
            interestRate = null

            var errors = listOf<FieldError>()
            if (loanAmount == null) {
                errors += (FieldError(LoanParameter.LOAN_AMOUNT, message = "Field can not be empty"))
            }
            if (downPayment == null) {
                errors += (FieldError(LoanParameter.DOWN_PAYMENT, message = "Field can not be empty"))
            }
            if (loanTermsMonths == null) {
                errors += (FieldError(LoanParameter.LOAN_TERMS, message = "Field can not be empty"))
            }
            if (monthlyPayment == null) {
                errors += (FieldError(LoanParameter.MONTHLY_PAYMENT, message = "Field can not be empty"))
            }
            throw CalcException(errors = errors)
        }
    }

    fun calcLoanTerms() {
        loanTermsMonths =
            if (downPayment != null && interestRate != null && loanAmount != null && monthlyPayment != null) {
                calculateLoanTerm(
                    loanAmount!!,
                    downPayment!!,
                    interestRate!!,
                    monthlyPayment!!
                )
            } else {
                null
            }
    }

    fun calcLoanAmount() {
        loanAmount =
            if (downPayment != null && interestRate != null && loanTermsMonths != null && monthlyPayment != null) {
                calculateLoanAmount(
                    downPayment!!,
                    interestRate!!,
                    loanTermsMonths!!,
                    monthlyPayment!!
                )
            } else {
                null
            }
    }

    fun calcDownPayment() {
        downPayment =
            if (loanAmount != null && interestRate != null && loanTermsMonths != null && monthlyPayment != null) {
                calculateDownPayment(
                    loanAmount!!,
                    interestRate!!,
                    loanTermsMonths!!,
                    monthlyPayment!!
                )
            } else {
                null
            }
    }

    fun calcMonthlyPayment() {
        monthlyPayment =
            if (loanAmount != null && downPayment != null && interestRate != null && loanTermsMonths != null) {
                val value =
                    calculateMonthlyPayment(
                        loanAmount!!,
                        downPayment!!,
                        interestRate!!,
                        loanTermsMonths!!
                    )
                checkValue(value)
            } else {
                null
            }
    }

    fun calcParameter(parameter: LoanParameter): Double? {
        val value: Double? =
            when (parameter) {
                LoanParameter.MONTHLY_PAYMENT -> {
                    calcMonthlyPayment()
                    monthlyPayment
                }
                LoanParameter.DOWN_PAYMENT -> {
                    calcDownPayment()
                    downPayment

                }
                LoanParameter.LOAN_AMOUNT -> {
                    calcLoanAmount()
                    loanAmount

                }
                LoanParameter.LOAN_TERMS -> {
                    calcLoanTerms()
                    loanTermsMonths

                }
                LoanParameter.INTEREST_RATE -> {
                    calcInterestRate()
                    interestRate

                }
            }
        return value
    }

}

enum class LoanParameter(val value: Int) {
    LOAN_AMOUNT(0),
    DOWN_PAYMENT(1),
    INTEREST_RATE(2),
    LOAN_TERMS(3),
    MONTHLY_PAYMENT(4),
}


fun checkValue (value: Double): Double? {
    return if (value != null && value > 0 && value.isInfinite() == false) value else null
}

class FieldError(var field: LoanParameter, var message: String)

class CalcException(val errors: List<FieldError>): Exception()