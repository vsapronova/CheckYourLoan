package com.example.checkyourloan

import android.widget.EditText

class Loan(
    var amount: Double?,
    var downPayment: Double?,
    var interestRate: Double?,
    var terms: Double?,
    var monthlyPayment: Double?,
    var termsUnit: TermsUnit
) {
    val termsInMonths: Double?
        get() = calculateTermsInMonths(termsUnit, terms)

    inner class Checker {
        var errors = listOf<FieldError>()

        private fun emptyField(param: LoanParameter): FieldError {
            return FieldError(param, message = "Field can not be empty")
        }

        fun loanAmountNotNull() {
            if (amount == null) {
                errors += emptyField(LoanParameter.TOTAL_AMOUNT)
            }
        }

        fun calculatedLoanAmountPositive(value: Double) {
            if (value < 0) {
                errors += FieldError(LoanParameter.MONTHLY_PAYMENT, "Increase Monthly Payment")
            }
        }

        fun calculatedLoanAmountNaN(value: Double) {
            if (value.isNaN()) {
                errors += FieldError(LoanParameter.DOWN_PAYMENT, "Increase Down Payment")
            }
        }

        fun calculatedLoanAmountInfinite(value: Double) {
            if (value.isInfinite()) {
                errors += FieldError(LoanParameter.MONTHLY_PAYMENT, "Increase Monthly Payment")
            }
        }

        fun downPaymentNotNull() {
            if (downPayment == null) {
                errors += emptyField(LoanParameter.DOWN_PAYMENT)
            }
        }

        fun calculatedDownPaymentPositive(value: Double) {
            if (value < 0) {
                errors += FieldError(LoanParameter.TOTAL_AMOUNT, "Increase Total Amount")
            }
        }

        fun calculatedDownPaymentNaN (value: Double) {
            if (value.isNaN()) {
                errors += FieldError(LoanParameter.MONTHLY_PAYMENT, "Increase Monthly Payment")
            }
        }

        fun calculatedDownPaymentInfinite (value: Double) {
            if (value.isInfinite()) {
                errors += FieldError(LoanParameter.MONTHLY_PAYMENT, "Increase Monthly Payment")
            }
        }

        fun interestRateNotNull() {
            if (interestRate == null) {
                errors += emptyField(LoanParameter.INTEREST_RATE)
            }
        }

        fun loanAmountBiggerDownPayment() {
            errors += FieldError(LoanParameter.TOTAL_AMOUNT, "Increase Total Amount")
        }

        fun loanAmountEqualDownPayment() {
            errors += FieldError(LoanParameter.TOTAL_AMOUNT, "Increase Total Amount")
        }

        fun calculatedInterestRatePositive(value: Double) {
            if (value < 0) {
                errors += FieldError(LoanParameter.MONTHLY_PAYMENT, message = "Increase Monthly Payment")
            }
        }

        fun calculatedInterestRateNaN(value: Double) {
            if (value.isNaN()) {
                errors += FieldError(LoanParameter.TOTAL_AMOUNT, message = "Increase Total Amount")
            }
        }

        fun calculatedInterestRateInfinite(value: Double) {
            if (value.isInfinite()) {
                errors += FieldError(LoanParameter.TOTAL_AMOUNT, message = "Increase Total Amount")
            }
        }

        fun loanTermsNotNull() {
            if (terms == null) {
                errors += emptyField(LoanParameter.TERMS)
            }
        }

        fun calculatedLoanTermsPositive(value: Double) {
            if (value < 0) {
                errors += FieldError(LoanParameter.TOTAL_AMOUNT, "Increase Total Amount")
            }
        }

        fun calculatedLoanTermsNaN(value: Double) {
            if (value.isNaN()) {
                errors += FieldError(LoanParameter.MONTHLY_PAYMENT, "Increase Monthly Payment")
            }
        }

        fun calculatedLoanTermsInfinite(value: Double) {
            if (value.isInfinite()) {
                errors += FieldError(LoanParameter.MONTHLY_PAYMENT, "Increase Monthly Payment")
            }
        }

        fun monthlyPaymentNotNull() {
            if (monthlyPayment == null) {
                errors += emptyField(LoanParameter.MONTHLY_PAYMENT)
            }
        }

        fun calculatedMonthlyPaymentPositive(value: Double) {
            if (value < 0) {
               errors += FieldError(LoanParameter.TOTAL_AMOUNT, "Increase Total Amount")
            }
        }

        fun calculatedMonthlyPaymentNaN(value: Double) {
            if (value.isNaN()) {
                errors += FieldError(LoanParameter.TOTAL_AMOUNT, "Change Total Amount")
            }
        }

        fun calculatedMonthlyPaymentInfinite(value: Double) {
            if (value.isInfinite()) {
                errors += FieldError(LoanParameter.TERMS, "Decrease Loan Terms")
            }
        }

        val hasErrors: Boolean
            get() = !errors.isEmpty()

        fun throwCalcException() {
            throw CalcException(errors)
        }
    }

    fun calcLoanAmount() {
        val checker = Checker()

        checker.interestRateNotNull()
        checker.downPaymentNotNull()
        checker.loanTermsNotNull()
        checker.monthlyPaymentNotNull()

        if (checker.hasErrors) {
            amount = null
            checker.throwCalcException()
        }

        val value =
            calculateLoanAmount(
                downPayment!!,
                interestRate!!,
                termsInMonths!!,
                monthlyPayment!!
            )

        checker.calculatedLoanAmountPositive(value)
        checker.calculatedLoanAmountNaN(value)
        checker.calculatedLoanAmountInfinite(value)

        if (checker.hasErrors) {
            checker.throwCalcException()
        }

        amount = value
    }

    fun calcDownPayment() {
        val checker = Checker()

        checker.loanAmountNotNull()
        checker.interestRateNotNull()
        checker.loanTermsNotNull()
        checker.monthlyPaymentNotNull()

        if (checker.hasErrors) {
            downPayment = null
            checker.throwCalcException()
        }

        val value =
            calculateDownPayment(
                amount!!,
                interestRate!!,
                termsInMonths!!,
                monthlyPayment!!
            )

        checker.calculatedDownPaymentPositive(value)
        checker.calculatedDownPaymentNaN(value)
        checker.calculatedDownPaymentInfinite(value)

        if (checker.hasErrors) {
            checker.throwCalcException()
        }

        downPayment = value
    }

    fun calcInterestRate() {
        val checker = Checker()

        if (amount != null && downPayment != null && amount!! < downPayment!!) {
            checker.loanAmountBiggerDownPayment()
        }

        if (amount != null && downPayment != null && amount!! == downPayment!!) {
            checker.loanAmountEqualDownPayment()
        }
        checker.loanAmountNotNull()
        checker.downPaymentNotNull()
        checker.loanTermsNotNull()
        checker.monthlyPaymentNotNull()

        if (checker.hasErrors) {
            interestRate = null
            checker.throwCalcException()
        }

        val value =
            calculateInterestRate(
                amount!!,
                downPayment!!,
                termsInMonths!!,
                monthlyPayment!!
            )

        checker.calculatedInterestRatePositive(value)
        checker.calculatedInterestRateNaN(value)
        checker.calculatedInterestRateInfinite(value)

        if (checker.hasErrors) {
            checker.throwCalcException()
        }

        interestRate = value
    }

    fun calcLoanTerms() {
        val checker = Checker()

        checker.loanAmountNotNull()
        checker.downPaymentNotNull()
        checker.interestRateNotNull()
        checker.monthlyPaymentNotNull()

        if (checker.hasErrors) {
            terms = null
            checker.throwCalcException()
        }

        val value =
            calculateLoanTerm(
                amount!!,
                downPayment!!,
                interestRate!!,
                monthlyPayment!!
            )

        checker.calculatedLoanTermsPositive(value)
        checker.calculatedLoanTermsNaN(value)
        checker.calculatedLoanTermsInfinite(value)

        if (checker.hasErrors) {
            checker.throwCalcException()
        }

        terms = convertLoanTermsMonthsYears(termsUnit, value)

    }

    fun calcMonthlyPayment() {
        val checker = Checker()

        checker.loanAmountNotNull()
        checker.downPaymentNotNull()
        checker.interestRateNotNull()
        checker.loanTermsNotNull()

        if (checker.hasErrors) {
            monthlyPayment = null
            checker.throwCalcException()
        }

        val value =
            calculateMonthlyPayment(
                amount!!,
                downPayment!!,
                interestRate!!,
                termsInMonths!!
            )

        checker.calculatedMonthlyPaymentPositive(value)
        checker.calculatedMonthlyPaymentNaN(value)
        checker.calculatedMonthlyPaymentInfinite(value)

        if (checker.hasErrors) {
            checker.throwCalcException()
        }

        monthlyPayment = value
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
                LoanParameter.TOTAL_AMOUNT -> {
                    calcLoanAmount()
                    amount

                }
                LoanParameter.TERMS -> {
                    calcLoanTerms()
                    terms

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
    TOTAL_AMOUNT(0),
    DOWN_PAYMENT(1),
    INTEREST_RATE(2),
    TERMS(3),
    MONTHLY_PAYMENT(4),
}

enum class TermsUnit (val value: Int) {
    MONTHS (0),
    YEARS (1)
}

fun getDouble(edit: EditText): Double? {
    if (edit.text.length > 0) {
        val strValue = edit.text.toString()
        val cleanStrValue = strValue.replace(",", "")
        return cleanStrValue.toDouble()
    } else {
        return null
    }
}





class FieldError(var field: LoanParameter, var message: String)

class CalcException(val errors: List<FieldError>): Exception()