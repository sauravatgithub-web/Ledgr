package com.moneytracker.app.domain.model

import java.text.NumberFormat
import java.util.Locale

/**
 * INR amount stored as paise (1 rupee = 100 paise).
 */
data class Money(val paise: Long) {
    operator fun plus(other: Money) = Money(paise + other.paise)
    operator fun minus(other: Money) = Money(paise - other.paise)

    fun formatInr(): String {
        val rupees = paise / 100.0
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        return format.format(rupees)
    }

    companion object {
        val ZERO = Money(0)

        fun fromRupeeString(input: String): Money? {
            val cleaned = input.trim().replace(",", "")
            if (cleaned.isEmpty()) return null
            val value = cleaned.toDoubleOrNull() ?: return null
            if (value < 0) return null
            return Money(Math.round(value * 100))
        }
    }
}
