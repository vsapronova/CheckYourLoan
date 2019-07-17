package com.example.checkyourloan


class Loan (
    var loanAmount: Double?,
    var downPayment: Double?,
    var interestRate: Double?,
    var loanTerms: Double?,
    var monthlyPayment: Double?
) {

    inner class Checker {
        var errors = listOf<FieldError>()

        private fun emptyField(param: LoanParameter): FieldError {
            return FieldError(param, message = "Field can not be empty")
        }

        fun loanAmountNotNull() {
            if (loanAmount == null) {
                errors += emptyField(LoanParameter.LOAN_AMOUNT)
            }
        }


        fun downPaymentNotNull() {
            if (downPayment == null) {
                errors += emptyField(LoanParameter.DOWN_PAYMENT)
            }
        }

        fun interestRateNotNull() {
            if (interestRate == null) {
                errors += emptyField(LoanParameter.INTEREST_RATE)
            }
        }

        fun loanTermsNotNull() {
            if (loanTerms == null) {
                errors += emptyField(LoanParameter.LOAN_TERMS)
            }
        }

        fun monthlyPaymentNotNull() {
            if (monthlyPayment == null) {
                errors += emptyField(LoanParameter.MONTHLY_PAYMENT)
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
            loanAmount = null
            checker.throwCalcException()
        }

        val value =
            calculateLoanAmount(
                downPayment!!,
                interestRate!!,
                loanTerms!!,
                monthlyPayment!!
            )
        checkValue(value)
        loanAmount = value
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
                loanAmount!!,
                interestRate!!,
                loanTerms!!,
                monthlyPayment!!
            )
        checkValue(value)
        downPayment = value
    }

    fun calcInterestRate() {
        val checker = Checker()

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
                loanAmount!!,
                downPayment!!,
                loanTerms!!,
                monthlyPayment!!
            )
        checkValue(value)
        interestRate = value
    }

    fun calcLoanTerms() {
        val checker = Checker()

        checker.loanAmountNotNull()
        checker.downPaymentNotNull()
        checker.interestRateNotNull()
        checker.monthlyPaymentNotNull()

        if (checker.hasErrors) {
            loanTerms = null
            checker.throwCalcException()
        }

        val value =
            calculateLoanTerm(
                loanAmount!!,
                downPayment!!,
                interestRate!!,
                monthlyPayment!!
            )
        checkValue(value)
        loanTerms = value
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
                loanAmount!!,
                downPayment!!,
                interestRate!!,
                loanTerms!!

            )
        checkValue(value)
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
                LoanParameter.LOAN_AMOUNT -> {
                    calcLoanAmount()
                    loanAmount

                }
                LoanParameter.LOAN_TERMS -> {
                    calcLoanTerms()
                    loanTerms

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
    return if (value > 0 && value.isInfinite() == false) value else null
}

class FieldError(var field: LoanParameter, var message: String)

class CalcException(val errors: List<FieldError>): Exception()