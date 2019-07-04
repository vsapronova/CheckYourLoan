package com.example.checkyourloan

class Loan (
    var loanAmount: Double?,
    var downPayment: Double?,
    var interestRate: Double?,
    var loanTermsMonths: Double?,
    var monthlyPayment: Double?
) {

    fun calcInterestRate(): Double? {
        interestRate =
            if (loanAmount != null && downPayment != null && loanTermsMonths != null && monthlyPayment != null) {
                calculateInterestRate(
                    loanAmount!!,
                    downPayment!!,
                    loanTermsMonths!!,
                    monthlyPayment!!
                )
            } else {
                null
            }
        return interestRate
    }

    fun calcLoanTerms(): Double? {
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
        return loanTermsMonths
    }

    fun calcLoanAmount(): Double? {
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
        return loanAmount
    }

    fun calcDownPayment(): Double? {
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
        return downPayment
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

    fun calcParameter(parameter: MainActivity.LoanParameter): Double? {
        val value: Double? =
            when (parameter) {
                MainActivity.LoanParameter.MONTHLY_PAYMENT -> {
                    calcMonthlyPayment()
                    monthlyPayment
                }
                MainActivity.LoanParameter.DOWN_PAYMENT -> {
                    calcDownPayment()
                    downPayment

                }
                MainActivity.LoanParameter.LOAN_AMOUNT -> {
                    calcLoanAmount()
                    loanAmount

                }
                MainActivity.LoanParameter.LOAN_TERMS -> {
                    calcLoanTerms()
                    loanTermsMonths

                }
                MainActivity.LoanParameter.INTEREST_RATE -> {
                    calcInterestRate()
                    interestRate

                }
            }
        return value
    }

}

fun checkValue (value: Double): Double? {
    return if (value != null && value > 0 && value.isInfinite() == false) value else null
}