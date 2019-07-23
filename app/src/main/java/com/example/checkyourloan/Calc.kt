package com.example.checkyourloan

import kotlin.math.abs
import kotlin.math.log
import kotlin.math.pow

fun calculateMonthlyPayment(loanAmount: Double, downPayment: Double, interestRate: Double, loanTerms: Double): Double {
    val finalAmount = loanAmount - downPayment
    return if (interestRate == 0.0) {
        finalAmount/loanTerms
    }
    else {
        val monthlyRate = interestRate / 12 / 100
        (finalAmount * monthlyRate) / (1 - (1 + monthlyRate).pow(-loanTerms))
    }
}

fun calculateLoanAmount(downPayment: Double, interestRate: Double, loanTerms: Double, monthlyPayment: Double): Double {
    return if (interestRate == 0.0) {
        monthlyPayment * loanTerms + downPayment
    } else {
        val monthlyRate = interestRate / 12 / 100
        (monthlyPayment * (1 - (1 + monthlyRate).pow(-loanTerms))) / monthlyRate + downPayment
    }
}

fun calculateDownPayment(loanAmount: Double, interestRate: Double, loanTerms: Double, monthlyPayment: Double): Double {
    return if (interestRate == 0.0) {
        loanAmount - monthlyPayment * loanTerms
    }
    else {
        val monthlyRate = interestRate / 12 / 100
        loanAmount - (monthlyPayment*(1-(1+monthlyRate).pow(-loanTerms)))/monthlyRate
    }
}

fun calculateLoanTerm(loanAmount: Double, downPayment: Double, interestRate: Double, monthlyPayment: Double): Double {
    val finalAmount = loanAmount - downPayment
    return if (interestRate ==  0.0) {
        finalAmount/monthlyPayment
    }
    else {
        val monthlyRate = interestRate / 12 / 100
        -log((1 - (finalAmount*monthlyRate)/monthlyPayment), 1+monthlyRate)
    }
}

fun calculateInterestRate(loanAmount: Double, downPayment: Double, loanTerms: Double, monthlyPayment: Double): Double {
    fun monthly(rate: Double) = calculateMonthlyPayment(loanAmount, downPayment, rate, loanTerms)

    var step = 5.0
    var r0 = 0.0
    var r1 = 0.0

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

fun calculateTermsInMonths(termsUnitUnit: TermsUnit, value: Double?): Double? {
    if (value == null) return null
    val value =
        when(termsUnitUnit) {
            TermsUnit.MONTHS -> {
                value
            }
            TermsUnit.YEARS -> {
                value * 12
            }
        }
    return value
}

fun convertLoanTermsMonthsYears (selectedTermsUnit: TermsUnit, value: Double): Double {
    val value =
        when (selectedTermsUnit) {
            TermsUnit.MONTHS -> {
                value
            }
            TermsUnit.YEARS -> {
                value / 12
            }
        }
    return value
}