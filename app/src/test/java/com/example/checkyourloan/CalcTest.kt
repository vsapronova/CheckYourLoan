package com.example.checkyourloan


import org.junit.Assert.*
import org.junit.Test

import kotlin.test.*



class CalcTest {

    @Test
    fun calculateLoanAmountSuccess() {
        val result = calculateLoanAmount(500.0, 5.0, 24.0, 1000.0)
        assertEquals(23294.0, result, 1.0)
    }

    @Test
    fun calculateLoanAmountZeroInterestRate() {
        val result = calculateLoanAmount(500.0, 0.0, 24.0, 1000.0)
        assertEquals(24500.0, result, 1.0)
    }

    @Test
    fun calculateDownPaymentSuccess() {
        val result = calculateDownPayment(15000.0, 3.0, 12.0, 900.0)
        assertEquals(4373.0, result, 1.0)
    }

    @Test
    fun calculateDownPaymentZeroInterestRate() {
        val result = calculateDownPayment(15000.0, 0.0, 12.0, 900.0)
        assertEquals(4200.0, result, 1.0)
    }

    @Test
    fun calculateInterestRateSuccess() {
        val result = calculateInterestRate(19000.0, 1000.0, 24.0, 900.0)
        assertEquals(18.16, result, 1.0)
    }

    @Test
    fun calculateTermsSuccess() {
        val result = calculateLoanTerm(19000.0, 1000.0, 10.0, 1200.0)
        assertEquals(16.0, result, 1.0)
    }

    @Test
    fun calculateTermsZeroInterestRate() {
        val result = calculateLoanTerm(19000.0, 1000.0, 0.0, 1200.0)
        assertEquals(15.0, result, 1.0)
    }

    @Test
    fun calculateMonthlyPaymentSuccess() {
        val actual = calculateMonthlyPayment(1000.0, 100.0, 3.0, 12.0)
        assertEquals(76.0, actual, 1.0)
    }

    @Test
    fun calculateMonthlyPaymentZeroInterestRate() {
        val actual = calculateMonthlyPayment(1000.0, 100.0, 0.0, 10.0)
        assertEquals(90.0, actual, 0.1)
    }








}

