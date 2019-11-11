package com.example.piggycalculator

import org.junit.Test
import kotlin.test.assertFailsWith



class NullTest {
    @Test
    fun throwsIfTotalAmountNull() {
        val loan = Loan(null, 500.0, 3.0, 12.0, 900.0, TermsUnit.MONTHS)
        assertFailsWith<CalcException>{ loan.calcDownPayment() }
        assertFailsWith<CalcException>{ loan.calcInterestRate() }
        assertFailsWith<CalcException>{ loan.calcLoanTerms() }
        assertFailsWith<CalcException>{ loan.calcMonthlyPayment() }
    }

    @Test
    fun throwsIfDownPaymentNull() {
        val loan = Loan(20000.0, null, 3.0, 24.0, 900.0, TermsUnit.MONTHS)
        assertFailsWith<CalcException>{ loan.calcLoanAmount() }
        assertFailsWith<CalcException>{ loan.calcInterestRate() }
        assertFailsWith<CalcException>{ loan.calcLoanTerms() }
        assertFailsWith<CalcException>{ loan.calcMonthlyPayment() }
    }

    @Test
    fun throwsIfInterestRateNull() {
        val loan = Loan(10000.0, 500.0, null, 12.0, 900.0, TermsUnit.MONTHS)
        assertFailsWith<CalcException>{ loan.calcLoanAmount() }
        assertFailsWith<CalcException>{ loan.calcDownPayment() }
        assertFailsWith<CalcException>{ loan.calcLoanTerms() }
        assertFailsWith<CalcException>{ loan.calcMonthlyPayment() }
    }

    @Test
    fun throwsIfTermsNull() {
        val loan = Loan(10000.0, 500.0, 3.0, null, 900.0, TermsUnit.MONTHS)
        assertFailsWith<CalcException>{ loan.calcLoanAmount() }
        assertFailsWith<CalcException>{ loan.calcDownPayment() }
        assertFailsWith<CalcException>{ loan.calcInterestRate() }
        assertFailsWith<CalcException>{ loan.calcMonthlyPayment() }
    }

    @Test
    fun throwsIfMonthlyPaymentNull() {
        val loan = Loan(10000.0, 500.0, 3.0, 12.0, null, TermsUnit.MONTHS)
        assertFailsWith<CalcException>{ loan.calcLoanAmount() }
        assertFailsWith<CalcException>{ loan.calcDownPayment() }
        assertFailsWith<CalcException>{ loan.calcInterestRate() }
        assertFailsWith<CalcException>{ loan.calcLoanTerms() }
    }
}
