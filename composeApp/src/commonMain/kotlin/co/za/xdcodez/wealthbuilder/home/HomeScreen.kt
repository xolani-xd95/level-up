package co.za.xdcodez.wealthbuilder.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.za.xdcodez.wealthbuilder.finance.presentation.dashboard.BudgetDashboardRoute
import co.za.xdcodez.wealthbuilder.habits.presentation.today.TodayCheckInScreenRoute
import co.za.xdcodez.wealthbuilder.journal.presentation.home.JournalHomeScreenRoute
import co.za.xdcodez.wealthbuilder.navigation.WealthBuilderBaseScreen
import co.za.xdcodez.wealthbuilder.theme.Action
import co.za.xdcodez.wealthbuilder.theme.WealthBuilderTheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreenRoute(
    onAction: (HomeNavigationEvent) -> Unit
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 3 })
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }

    WealthBuilderBaseScreen(
        title = "Wealth Builder",
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex.value,
                containerColor = Color.Transparent,
                contentColor = Action.Primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex.value]),
                        color = Action.Primary
                    )
                }
            ) {
                Tab(
                    selected = selectedTabIndex.value == 0,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    text = { Text("Budget") },
                    selectedContentColor = Action.Primary,
                    unselectedContentColor = Color.White
                )
                Tab(
                    selected = selectedTabIndex.value == 1,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    text = { Text("Habits") },
                    selectedContentColor = Action.Primary,
                    unselectedContentColor = Color.White
                )
                Tab(
                    selected = selectedTabIndex.value == 2,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(2)
                        }
                    },
                    text = { Text("Trading") },
                    selectedContentColor = Action.Primary,
                    unselectedContentColor = Color.White
                )
            }
            HorizontalPager(
                state = pagerState,
            ) { page ->
                when (page) {
                    0 -> BudgetDashboardRoute(onNavigate = onAction)
                    1 -> TodayCheckInScreenRoute()
                    2 -> JournalHomeScreenRoute(onNavigate = onAction)
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    WealthBuilderTheme {
        HomeScreenRoute { }
    }
}
