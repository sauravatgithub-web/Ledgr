package com.moneytracker.app.domain.usecase

import com.moneytracker.app.domain.model.Category
import com.moneytracker.app.domain.model.Expense
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ExportMonthCsv {
    private val dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    private val zone = ZoneId.systemDefault()

    operator fun invoke(
        yearMonth: String,
        expenses: List<Expense>,
        categories: List<Category>,
    ): String {
        val names = categories.associateBy({ it.id }, { it.name })
        val lines = ArrayList<String>(expenses.size + 1)
        lines += "date,amount_inr,place,category,non_negotiable"
        expenses.sortedBy { it.createdAtEpochMs }.forEach { e ->
            val whenStr = Instant.ofEpochMilli(e.createdAtEpochMs).atZone(zone).format(dateFmt)
            val amount = "%.2f".format(e.amount.paise / 100.0)
            val place = csvEscape(e.place)
            val category = csvEscape(names[e.categoryId] ?: "Unknown")
            lines += "$whenStr,$amount,$place,$category,${e.isNonNegotiable}"
        }
        return lines.joinToString("\n") + "\n"
    }

    private fun csvEscape(value: String): String {
        val needsQuotes = value.contains(',') || value.contains('"') || value.contains('\n')
        val escaped = value.replace("\"", "\"\"")
        return if (needsQuotes) "\"$escaped\"" else escaped
    }
}
