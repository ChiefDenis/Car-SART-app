package com.chiefdenis.carsart.utils

import com.chiefdenis.carsart.data.repository.AppCurrency
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object CurrencyFormatter {

    fun formatCurrency(cost: BigDecimal, currency: AppCurrency): String {
        val format = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2
        format.currency = Currency.getInstance(currency.code)
        return format.format(cost)
    }

    fun formatCurrencyWithLocale(cost: BigDecimal, locale: Locale): String {
        val format = NumberFormat.getCurrencyInstance(locale)
        format.maximumFractionDigits = 2
        return format.format(cost)
    }
}
