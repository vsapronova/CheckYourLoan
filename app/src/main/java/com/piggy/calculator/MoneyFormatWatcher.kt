package com.piggy.calculator

import android.text.Editable
import android.text.TextWatcher
import java.lang.RuntimeException

class MoneyFormatWatcher(val changeCallback: () -> Unit): TextWatcher {
    var killedComma: Int? = null
    var isProcessing = false

    val thousandsSeparator = ','
    val dot = '.'

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        if (isProcessing) return
        if (after == 0 && count == 1 && s[start] == thousandsSeparator) {
            killedComma = start
        }
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable) {
        if (isProcessing) return
        isProcessing = true

        try {
            val killedCommaValue = killedComma
            if (killedCommaValue != null) {
                s.delete(killedCommaValue-1, killedCommaValue)
                killedComma = null
            }

            var index = s.length - 1
            var digitCounter = 0
            while (index >= 0) {
                val ch = s.get(index)
                when(ch) {
                    in '0'..'9' ->
                        if (digitCounter == 3) {
                            s.insert(index+1, thousandsSeparator.toString())
                            digitCounter = 0
                        } else {
                            digitCounter++
                            index--
                        }
                    dot -> {
                        digitCounter = 0
                        index--
                    }
                    thousandsSeparator ->
                        if (digitCounter == 3) {
                            digitCounter = 0
                            index--
                        } else {
                            s.delete(index, index + 1)
                            index--
                        }
                }
            }
            changeCallback()
        } catch (ex: RuntimeException) {
            println(ex.message)
        }

        isProcessing = false
    }
}