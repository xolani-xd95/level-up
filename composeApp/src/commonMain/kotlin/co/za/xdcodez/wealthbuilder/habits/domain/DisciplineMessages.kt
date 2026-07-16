package co.za.xdcodez.wealthbuilder.habits.domain

private data class DisciplineTier(val minDays: Int, val label: (Int) -> String)

private val disciplineTiers = listOf(
    DisciplineTier(80) { _ -> "Eighty days. Tomorrow is another choice." },
    DisciplineTier(50) { _ -> "Fifty days. Protect the standard you've built." },
    DisciplineTier(30) { _ -> "Thirty days. Don't negotiate with yourself." },
    DisciplineTier(21) { _ -> "Three weeks. Keep showing up." },
    DisciplineTier(14) { _ -> "Two weeks. Keep stacking days." },
    DisciplineTier(7)  { _ -> "One week. Start the next one tomorrow." },
    DisciplineTier(3)  { _ -> "Momentum is building. Protect it." },
).sortedByDescending { it.minDays }

fun getDisciplineMessage(fullDisciplineStreak: Int): String? {
    if (fullDisciplineStreak <= 0) return null
    return disciplineTiers
        .firstOrNull { fullDisciplineStreak >= it.minDays }
        ?.label
        ?.invoke(fullDisciplineStreak)
}