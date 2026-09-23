package com.moneytracker.app.domain.model

import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId

object YearMonths {
    fun current(zoneId: ZoneId = ZoneId.systemDefault()): String =
        YearMonth.now(zoneId).toString()

    fun fromEpochMs(epochMs: Long, zoneId: ZoneId = ZoneId.systemDefault()): String =
        YearMonth.from(Instant.ofEpochMilli(epochMs).atZone(zoneId)).toString()

    fun displayLabel(yearMonth: String): String {
        val ym = YearMonth.parse(yearMonth)
        val month = ym.month.name.lowercase().replaceFirstChar { it.titlecase() }
        return "$month ${ym.year}"
    }
}
