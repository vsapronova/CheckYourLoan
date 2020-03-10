package com.piggy.calculator

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class LoanErrorsTest {

    @Test
    fun throwsIfTotalAmountEqualDownPayment() {
        val loan = Loan(1000.0, 1000.0, null, 24.0, 900.0, TermsUnit.MONTHS, LoanParameter.TERMS)
        val ex = assertFailsWith<CalcException>{ loan.calcInterestRate() }
        assertEquals(1, ex.errors.size)
        assertEquals(LoanParameter.TOTAL_AMOUNT, ex.errors[0].field)

        loan.amount = 19000.0
        loan.calcInterestRate()
        assertNotNull(loan.interestRate)
        assertEquals(18.157, loan.interestRate!!, 0.01)
    }

    @Test
    fun throwsIfTermsNaN() {
        val loan = Loan(200000.0, 2000.0, 10.0, null, 1600.0, TermsUnit.MONTHS, LoanParameter.TERMS)
        val ex = assertFailsWith<CalcException>{ loan.calcLoanTerms() }
        assertEquals(1, ex.errors.size)
        assertEquals(LoanParameter.MONTHLY_PAYMENT, ex.errors[0].field )

        loan.monthlyPayment = 1700.0
        loan.calcLoanTerms()
        assertNotNull(loan.terms)
        assertEquals(425.0, loan.terms!!, 1.0)
    }

    @Test
    fun throwsIfTermsInfinity() {
        val loan = Loan(200000.0, 2000.0, 10.0, null, 1650.0, TermsUnit.MONTHS, LoanParameter.TERMS)
        val ex = assertFailsWith<CalcException>{ loan.calcLoanTerms() }
        assertEquals(1, ex.errors.size)
        assertEquals(LoanParameter.MONTHLY_PAYMENT, ex.errors[0].field )

        loan.monthlyPayment = 1700.0
        loan.calcLoanTerms()
        assertNotNull(loan.terms)
        assertEquals(425.0, loan.terms!!, 1.0)
    }

}