package co.za.xdcodes.level_up.di

import co.za.xdcodes.level_up.dashboard.data.FirebaseDashboardRepository
import co.za.xdcodes.level_up.dashboard.domain.DashboardRepository
import co.za.xdcodes.level_up.dashboard.presentation.DashboardViewModel
import co.za.xdcodes.level_up.finance.data.FirebaseBudgetRepositoryImpl
import co.za.xdcodes.level_up.finance.domain.BudgetRepository
import co.za.xdcodes.level_up.finance.presentation.budget.BudgetScreenViewModel
import co.za.xdcodes.level_up.workout.data.FirebaseWorkoutRepository
import co.za.xdcodes.level_up.workout.domain.WorkoutRepository
import co.za.xdcodes.level_up.workout.presentation.WorkoutListViewModel
import co.za.xdcodes.level_up.workout.presentation.create.CreateWorkoutViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

// container for related dependencies
val sharedModule = module {
    singleOf(::FirebaseDashboardRepository).bind<DashboardRepository>()
    singleOf(::FirebaseBudgetRepositoryImpl).bind<BudgetRepository>()
    singleOf(::FirebaseWorkoutRepository).bind<WorkoutRepository>()
    viewModelOf(::DashboardViewModel)
    viewModelOf(::BudgetScreenViewModel)
    viewModelOf(::WorkoutListViewModel)
    viewModelOf(::CreateWorkoutViewModel)
}