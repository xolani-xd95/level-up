package co.za.xdcodes.level_up.di

import co.za.xdcodes.level_up.dashboard.data.FirebaseDashboardRepository
import co.za.xdcodes.level_up.dashboard.domain.DashboardRepository
import co.za.xdcodes.level_up.dashboard.presentation.DashboardViewModel
import co.za.xdcodes.level_up.finance.data.FirebaseBudgetRepositoryImpl
import co.za.xdcodes.level_up.finance.domain.BudgetRepository
import co.za.xdcodes.level_up.finance.presentation.budgetOverview.BudgetScreenViewModel
import co.za.xdcodes.level_up.finance.presentation.budgetTransactions.TransactionsViewModel
import co.za.xdcodes.level_up.finance.presentation.createbudget.CreateBudgetViewModel
import co.za.xdcodes.level_up.journal.data.FirebaseTradingRepositoryImpl
import co.za.xdcodes.level_up.journal.domain.JournalRepository
import co.za.xdcodes.level_up.journal.presentation.details.DayDetailViewModel
import co.za.xdcodes.level_up.journal.presentation.home.JournalHomeViewModel
import co.za.xdcodes.level_up.journal.presentation.setup.JournalSetupViewModel
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
    singleOf(::FirebaseTradingRepositoryImpl).bind<JournalRepository>()
    viewModelOf(::DashboardViewModel)
    viewModelOf(::BudgetScreenViewModel)
    viewModelOf(::CreateBudgetViewModel)
    viewModelOf(::TransactionsViewModel)
    viewModelOf(::JournalHomeViewModel)
    viewModelOf(::DayDetailViewModel)
    viewModelOf(::JournalSetupViewModel)
}