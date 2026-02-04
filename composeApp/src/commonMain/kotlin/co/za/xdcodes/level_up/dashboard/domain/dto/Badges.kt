package co.za.xdcodes.level_up.dashboard.domain.dto

import level_up.composeapp.generated.resources.D1_consistency
import level_up.composeapp.generated.resources.M1_consistency
import level_up.composeapp.generated.resources.Res
import level_up.composeapp.generated.resources.W1_consistency
import level_up.composeapp.generated.resources.W2_consistency
import org.jetbrains.compose.resources.DrawableResource

enum class ConsistencyTier(
    val daysRequired: Int,
    val title: String,
    val description: String,
    val drawable: DrawableResource
) {
    DAY_1(
        1,
        "Day-One Kickoff",
        "You showed up 💪🏾",
        Res.drawable.D1_consistency
    ),
    WEEK_1(
        5,
        "1 Week Consistency",
        "Momentum unlocked 🔥",
        Res.drawable.W1_consistency
    ),
    WEEK_2(
        10,
        "2 Weeks Consistency",
        "Discipline forming 🧠",
        Res.drawable.W2_consistency
    ),
    MONTH_1(
        30,
        "1 Month Consistency",
        "Identity level habit 🏆",
        Res.drawable.M1_consistency
    )
}
