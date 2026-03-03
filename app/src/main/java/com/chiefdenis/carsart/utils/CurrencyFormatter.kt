package com.chiefdenis.carsart.utils

import com.chiefdenis.carsart.data.repository.AppCurrency
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object CurrencyFormatter {

    fun formatCurrency(cost: BigDecimal, currency: AppCurrency): String {
        return try {
            // Use specific locale for each currency to ensure symbol display
            val locale = when (currency) {
                AppCurrency.NGN -> Locale("en", "NG") // Nigeria
                AppCurrency.USD -> Locale.US // United States
                AppCurrency.EUR -> Locale.GERMANY // Germany
                AppCurrency.GBP -> Locale.UK // United Kingdom
                else -> Locale.getDefault()
            }
            
            val format = NumberFormat.getCurrencyInstance(locale)
            format.maximumFractionDigits = 2
            format.currency = Currency.getInstance(currency.code)
            format.isGroupingUsed = true
            format.format(cost)
        } catch (e: Exception) {
            // Fallback: manually construct with symbol
            val symbol = getCurrencySymbol(currency)
            "${symbol}${String.format("%.2f", cost.toDouble())}"
        }
    }

    fun formatCurrencyWithLocale(cost: BigDecimal, locale: Locale): String {
        val format = NumberFormat.getCurrencyInstance(locale)
        format.maximumFractionDigits = 2
        format.isGroupingUsed = true
        return format.format(cost)
    }
    
    // Helper method to get currency symbol directly
    fun getCurrencySymbol(currency: AppCurrency): String {
        return try {
            when (currency) {
                AppCurrency.NGN -> "₦"
                AppCurrency.USD -> "$"
                AppCurrency.EUR -> "€"
                AppCurrency.GBP -> "£"
                else -> Currency.getInstance(currency.code).symbol
            }
        } catch (e: Exception) {
            currency.code // Fallback to currency code
        }
    }
}
