package co.za.xdcodez.wealthbuilder.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun WealthBuilderBaseScreen(
    modifier: Modifier = Modifier,
    title: String? = null,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            if (!title.isNullOrEmpty()) {
                WealthBuilderTopBar(
                    title = title,
                    onBackClick = onBackClick,
                    actions = actions
                )
            }
        },
        floatingActionButton = floatingActionButton,
        content = { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                content = { content() }
            )
        }
    )
}