package co.za.xdcodez.wealthbuilder.di

import co.za.xdcodez.wealthbuilder.finance.data.FirebaseBudgetRepositoryImpl
import co.za.xdcodez.wealthbuilder.finance.domain.BudgetRepository
import co.za.xdcodez.wealthbuilder.finance.presentation.budgetOverview.BudgetScreenViewModel
import co.za.xdcodez.wealthbuilder.finance.presentation.budgetTransactions.TransactionsViewModel
import co.za.xdcodez.wealthbuilder.finance.presentation.createbudget.CreateBudgetViewModel
import co.za.xdcodez.wealthbuilder.journal.data.FirebaseTradingRepositoryImpl
import co.za.xdcodez.wealthbuilder.journal.domain.JournalRepository
import co.za.xdcodez.wealthbuilder.journal.presentation.details.DayDetailViewModel
import co.za.xdcodez.wealthbuilder.journal.presentation.home.JournalHomeViewModel
import co.za.xdcodez.wealthbuilder.journal.presentation.setup.JournalSetupViewModel
import co.za.xdcodez.wealthbuilder.habits.data.FirebaseHabitsRepositoryImpl
import co.za.xdcodez.wealthbuilder.habits.domain.HabitsRepository
import co.za.xdcodez.wealthbuilder.habits.presentation.today.TodayCheckInViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

// container for related dependencies
val sharedModule = module {
    singleOf(::FirebaseBudgetRepositoryImpl).bind<BudgetRepository>()
    singleOf(::FirebaseTradingRepositoryImpl).bind<JournalRepository>()
    singleOf(::FirebaseHabitsRepositoryImpl).bind<HabitsRepository>()
    viewModelOf(::BudgetScreenViewModel)
    viewModelOf(::CreateBudgetViewModel)
    viewModelOf(::TransactionsViewModel)
    viewModelOf(::JournalHomeViewModel)
    viewModelOf(::DayDetailViewModel)
    viewModelOf(::JournalSetupViewModel)
    viewModelOf(::TodayCheckInViewModel)
}