package co.za.xdcodes.level_up.dashboard.domain.dto

import level_up.composeapp.generated.resources.D1_consistency
import level_up.composeapp.generated.resources.Res
import level_up.composeapp.generated.resources._1st_day_2km
import level_up.composeapp.generated.resources._2_weeks_runner
import level_up.composeapp.generated.resources._3_day_consistent
import level_up.composeapp.generated.resources._3rd_day_2km
import level_up.composeapp.generated.resources.day_1_runner
import level_up.composeapp.generated.resources.day_1_workout
import level_up.composeapp.generated.resources.feb_runner
import org.jetbrains.compose.resources.DrawableResource

data class CategoryMedalTier(
    val requiredCount: Int,
    val title: String,
    val description: String,
    val drawable: DrawableResource
)

val workoutMedals = listOf(
    CategoryMedalTier(
        requiredCount = 1,
        description = "First Burn",
        title = "Day-1-Workout",
        drawable = Res.drawable.day_1_workout
    ),
    CategoryMedalTier(
        requiredCount = 4,
        description = "1 Week Strong",
        title = "4-Day-Consistent",
        drawable = Res.drawable.D1_consistency
    ),
    CategoryMedalTier(
        requiredCount = 8,
        description = "Iron Habit",
        title = "8-Workouts-Streak",
        drawable = Res.drawable.D1_consistency
    ),
    CategoryMedalTier(
        requiredCount = 16,
        description = "Iron Habit",
        title = "8-Workouts-Streak",
        drawable = Res.drawable.D1_consistency
    )
)

val nonSmokingMedals = listOf(
    CategoryMedalTier(
        requiredCount = 1,
        description = "Day One Clean",
        title = "1st smoke-free day",
        drawable = Res.drawable.D1_consistency
    ),
    CategoryMedalTier(
        requiredCount = 7,
        description = "Clear Lungs",
        title = "7days smoke-free",
        drawable = Res.drawable.D1_consistency
    ),
    CategoryMedalTier(
        requiredCount = 30,
        description = "New Identity",
        title = "30days smoke-free",
        drawable = Res.drawable.D1_consistency
    )
)

val runningMedals = listOf(
    CategoryMedalTier(
        requiredCount = 1,
        description = "Day One Clean",
        title = "Day-1-Runner",
        drawable = Res.drawable.day_1_runner
    ),
    CategoryMedalTier(
        requiredCount = 3,
        description = "Clear Lungs",
        title = "3-Days-Consistent",
        drawable = Res.drawable._3_day_consistent
    ),
    CategoryMedalTier(
        requiredCount = 6,
        description = "New Identity",
        title = "2-Weeks-Runner",
        drawable = Res.drawable._2_weeks_runner
    ),
    CategoryMedalTier(
        requiredCount = 12,
        description = "New Identity",
        title = "Feb-Runner",
        drawable = Res.drawable.feb_runner
    )
)