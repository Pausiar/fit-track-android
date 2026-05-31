package com.pausiar.fit.ui.navigation

/** Type-safe-ish route definitions for the nav graph. */
object Routes {
    const val DASHBOARD = "dashboard"
    const val DAY = "day/{dayId}/{date}"
    const val HISTORY = "history"
    const val EDIT = "edit"
    const val SETTINGS = "settings"

    fun day(dayId: Int, dateIso: String) = "day/$dayId/$dateIso"
}
