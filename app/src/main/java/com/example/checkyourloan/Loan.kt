package com.example.checkyourloan

class Loan (
    var loanAmount: Double?,
    var downPayment: Double?,
    var interestRate: Double?,
    var loanTerms: Double?,
    var monthlyPayment: Double?
) {

    fun calcInterestRate(): Double? {
        interestRate =
            if (loanAmount != null && downPayment != null && loanTerms != null && monthlyPayment != null) {
                calculateInterestRate(
                    loanAmount!!,
                    downPayment!!,
                    loanTerms!!,
                    monthlyPayment!!
                )
            } else {
                null
            }
        return interestRate
    }

    fun calcLoanTerms(): Double? {
        loanTerms =
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
        return loanTerms
    }

    fun calcLoanAmount(): Double? {
        loanAmount =
            if (downPayment != null && interestRate != null && loanTerms != null && monthlyPayment != null) {
                calculateLoanAmount(
                    downPayment!!,
                    interestRate!!,
                    loanTerms!!,
                    monthlyPayment!!
                )
            } else {
                null
            }
        return loanAmount
    }

    fun calcDownPayment(): Double? {
        downPayment =
            if (loanAmount != null && interestRate != null && loanTerms != null && monthlyPayment != null) {
                calculateDownPayment(
                    loanAmount!!,
                    interestRate!!,
                    loanTerms!!,
                    monthlyPayment!!
                )
            } else {
                null
            }
        return downPayment
    }

    fun calcMonthlyPayment(): Unit {
        monthlyPayment =
            if (loanAmount != null && downPayment != null && interestRate != null && loanTerms != null) {
                calculateMonthlyPayment(
                    loanAmount!!,
                    downPayment!!,
                    interestRate!!,
                    loanTerms!!
                )
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
                    loanTerms

                }
                MainActivity.LoanParameter.INTEREST_RATE -> {
                    calcInterestRate()
                    interestRate

                }
            }
        return value
    }

}